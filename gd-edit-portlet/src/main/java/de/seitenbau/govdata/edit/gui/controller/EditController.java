package de.seitenbau.govdata.edit.gui.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletRequest;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.validation.Valid;
import javax.validation.ValidationException;
import javax.ws.rs.ClientErrorException;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;

import de.seitenbau.govdata.cache.CategoryCache;
import de.seitenbau.govdata.cache.LicenceCache;
import de.seitenbau.govdata.cache.OrganizationCache;
import de.seitenbau.govdata.clean.StringCleaner;
import de.seitenbau.govdata.constants.DetailsRequestParamNames;
import de.seitenbau.govdata.date.DateUtil;
import de.seitenbau.govdata.edit.gui.common.Constants;
import de.seitenbau.govdata.edit.model.Contact;
import de.seitenbau.govdata.edit.model.ContactAddress;
import de.seitenbau.govdata.edit.model.EditForm;
import de.seitenbau.govdata.edit.model.Resource;
import de.seitenbau.govdata.fuseki.FusekiClient;
import de.seitenbau.govdata.messages.MessageType;
import de.seitenbau.govdata.navigation.GovDataNavigation;
import de.seitenbau.govdata.navigation.PortletUtil;
import de.seitenbau.govdata.odp.registry.ckan.impl.MetadataImpl;
import de.seitenbau.govdata.odp.registry.ckan.impl.ResourceImpl;
import de.seitenbau.govdata.odp.registry.ckan.impl.TagImpl;
import de.seitenbau.govdata.odp.registry.ckan.json.ResourceBean;
import de.seitenbau.govdata.odp.registry.ckan.json.TagBean;
import de.seitenbau.govdata.odp.registry.model.Category;
import de.seitenbau.govdata.odp.registry.model.Licence;
import de.seitenbau.govdata.odp.registry.model.Metadata;
import de.seitenbau.govdata.odp.registry.model.MetadataListExtraFields;
import de.seitenbau.govdata.odp.registry.model.MetadataStringExtraFields;
import de.seitenbau.govdata.odp.registry.model.Organization;
import de.seitenbau.govdata.odp.registry.model.RoleEnumType;
import de.seitenbau.govdata.odp.registry.model.Tag;
import de.seitenbau.govdata.odp.registry.model.User;
import de.seitenbau.govdata.odp.registry.model.exception.OpenDataRegistryException;
import de.seitenbau.govdata.odp.registry.model.exception.UnknownRoleException;
import de.seitenbau.govdata.odr.ODRTools;
import de.seitenbau.govdata.odr.RegistryClient;
import de.seitenbau.govdata.portlet.common.model.OptionTag;
import lombok.extern.slf4j.Slf4j;

/**
 * Controller für das Bearbeiten von Metadaten.
 * 
 * @author tscheffler
 * @author rnoerenberg
 *
 */
@Slf4j
@Controller
@RequestMapping("VIEW")
public class EditController
{
  private static final String DATE_PATTERN = "dd.MM.yyyy";

  private static final String DEFAULT_LICENCE = "http://dcat-ap.de/def/licenses/dl-by-de/2.0";

  private static final String MESSAGE = "message";

  private static final String MESSAGE_TYPE = "messageType";

  private static final RoleEnumType[] EDITABLE_CONTACTS = {
      RoleEnumType.CREATOR,
      RoleEnumType.MAINTAINER,
      RoleEnumType.PUBLISHER,
      RoleEnumType.ORIGINATOR
  };

  @Inject
  private FusekiClient fusekiClient;

  @Inject
  private RegistryClient registryClient;

  @Inject
  private LicenceCache licenceCache;

  @Inject
  private CategoryCache categoryCache;

  @Inject
  private OrganizationCache organizationCache;

  @Inject
  private GovDataNavigation gdNavigation;

  @Inject
  private MessageSource messageSource;

  @RenderMapping
  public String showForm(
      @RequestParam(value = DetailsRequestParamNames.PARAM_METADATA, required = false) String metadataName,
      @Valid @ModelAttribute("editForm") EditForm editForm,
      BindingResult result,
      RenderRequest request,
      RenderResponse response,
      Model model)
  {
    // check user permission
    boolean userCanEditDataset = false;

    List<Organization> organizationsForUser;
    try
    {
      // will throw an exception, if there is no ckan-user
      User ckanuserFromRequest = getCkanuserFromRequestProxy(request);

      // load organizations to fill dropdown in form
      organizationsForUser =
          registryClient.getInstance().getOrganizationsForUser(ckanuserFromRequest, "create_dataset");

      if (!organizationsForUser.isEmpty())
      { // user needs at least 1 organisation to be able to edit datasets
        userCanEditDataset = true;
        List<Licence> licenceList = licenceCache.getLicenceListSortedByTitle();

        // if we have existing form data, use them
        if (StringUtils.isNotEmpty(editForm.getName()))
        {
          log.debug("data: using editform data");
        }
        // try to load the given dataset
        else if (StringUtils.isNotEmpty(metadataName))
        {
          log.debug("data: loading dataset: " + metadataName);
          editForm = loadDataset(ckanuserFromRequest, metadataName);

          // check if the user is part of the organisation that this dataset belongs to
          if (!new ODRTools().containsOrganization(organizationsForUser, editForm.getOrganizationId()))
          {
            throw new OpenDataRegistryException("User does not belong to the datasets organisation");
          }
        }
        // create a new dataset
        else
        {
          log.debug("data: creating a new dataset");
          editForm = newDataset();
          // only provide active licenses in dropdown for new datasets
          licenceList = licenceCache.getActiveLicenceListSortedByTitle();
        }

        // set contributorIds for selected org
        List<String> contributorIdList =
            getContributorIdsFromOrganizations(editForm.getOrganizationId(), organizationsForUser, true);
        List<OptionTag> contributorIdSelectList = new ArrayList<>();
        // add empty default-option in case of multiple contributor-IDs and if value is invalid
        if (contributorIdList.size() > 1 && (StringUtils.isEmpty(editForm.getContributorId())
            || !contributorIdList.contains(editForm.getContributorId())))
        {
          contributorIdSelectList.add(
              new OptionTag("", messageSource.getMessage(
                  "od.select.required.item.text", new Object[0], null)));
        }
        contributorIdSelectList.addAll(mapToOptionTagList(contributorIdList));

        // form data
        model.addAttribute("actionUrl", response.createActionURL().toString());
        model.addAttribute("editForm", editForm);

        // set form structure data for displaying available options
        model.addAttribute("licenseList", licenceList);
        model.addAttribute("categoryList", categoryCache.getCategoriesSortedByTitle());
        model.addAttribute("organizationList", organizationsForUser);
        model.addAttribute("contributorIdSelectList", contributorIdSelectList);

        model.addAttribute(MESSAGE, request.getParameter(MESSAGE));
        model.addAttribute(MESSAGE_TYPE, request.getParameter(MESSAGE_TYPE));

        // back to metadata-details view
        String abortUrl = null;
        // do not show button for unsaved new dataset
        if (!editForm.isNewDataset())
        {
          PortletURL createLinkForMetadata =
              gdNavigation.createLinkForMetadata("gdsearchdetails", editForm.getName());
          if (createLinkForMetadata != null)
          {
            abortUrl = createLinkForMetadata.toString();
          }
        }
        else
        {
          abortUrl = gdNavigation.createLinkForSearchResults("suchen", "gdsearchresult", "").toString();
        }
        model.addAttribute("metadataUrl", abortUrl);
      }
    }
    catch (OpenDataRegistryException | PortalException | SystemException e)
    {
      // you can't do anything!
      log.error("Could not load edit dataset form: " + e.getMessage());
      userCanEditDataset = false;
    }

    model.addAttribute("userCanEditDataset", userCanEditDataset);
    return "edit";
  }

  @RequestMapping
  public void submitForm(@Valid @ModelAttribute("editForm") EditForm editForm, BindingResult result,
      ActionResponse response, ActionRequest request)
  {
    // fix zeitbezug dates if they are in the wrong temporal order
    Date fromDate = DateUtil.parseDateString(editForm.getTemporalCoverageFrom());
    Date untilDate = DateUtil.parseDateString(editForm.getTemporalCoverageUntil());
    if (fromDate != null && untilDate != null && fromDate.after(untilDate))
    {
      editForm.setTemporalCoverageFrom(formatDate(untilDate));
      editForm.setTemporalCoverageUntil(formatDate(fromDate));
    }
    else
    {
      editForm.setTemporalCoverageFrom(formatDate(fromDate));
      editForm.setTemporalCoverageUntil(formatDate(untilDate));
    }

    // check if form is valid
    if (!result.hasErrors())
    {
      try
      {
        User ckanUser = getCkanuserFromRequestProxy(request);
        saveDataset(editForm, ckanUser, result);

        // get the name in the same way he new name is created for new datasets
        if (editForm.isNewDataset())
        {
          editForm.setName(StringCleaner.mungeTitleToName(editForm.getTitle()));
        }

        // reload saved dataset, so we reflect the actual saved data
        editForm = loadDataset(ckanUser, editForm.getName());

        response.setRenderParameter(MESSAGE_TYPE, MessageType.SUCCESS.toString());
        response.setRenderParameter(MESSAGE, "od.editform.save.success");
      }
      catch (OpenDataRegistryException | PortalException | SystemException e)
      {
        response.setRenderParameter(MESSAGE_TYPE, MessageType.ERROR.toString());
        if (e instanceof OpenDataRegistryException && StringUtils.isNotEmpty(e.getMessage()))
        {
          response.setRenderParameter(MESSAGE, e.getMessage());
          log.info("Cannot save or/and load dataset! Details: {}", e.getMessage());
        }
        else
        {
          response.setRenderParameter(MESSAGE, "od.editform.save.error");
          log.warn("Cannot save or/and load dataset!", e);
        }
      }
      catch (ValidationException e)
      {
        response.setRenderParameter(MESSAGE_TYPE, MessageType.WARNING.toString());
        response.setRenderParameter(MESSAGE, "od.editform.save.warning");
        log.debug("Form has validation errors: " + result.getAllErrors());
      }
    }
    else
    {
      response.setRenderParameter(MESSAGE_TYPE, MessageType.WARNING.toString());
      response.setRenderParameter(MESSAGE, "od.editform.save.warning");
      log.debug("Form has errors: " + result.getAllErrors());
    }
  }

  /**
   * Adds a row of Resources
   *
   * @param editForm
   * @param bindingResult
   * @param response
   */
  @RequestMapping(params = {"addResource"})
  public void addResource(final @ModelAttribute("editForm") EditForm editForm,
      final BindingResult bindingResult,
      ActionResponse response)
  {
    if (editForm.getResources() == null)
    {
      editForm.setResources(new ArrayList<>());
    }
    Resource resource = new Resource();
    resource.setLicenseId(DEFAULT_LICENCE);
    editForm.getResources().add(resource);
  }

  /**
   * Removed a row of Resources
   *
   * @param editForm
   * @param bindingResult
   * @param rowId         index of the row to delete
   * @param response
   */
  @RequestMapping(params = {"removeResource"})
  public void removeResource(final @ModelAttribute("editForm") EditForm editForm,
      final BindingResult bindingResult,
      @RequestParam(value = "removeResource") Integer rowId,
      ActionResponse response)
  {
    editForm.getResources().remove(rowId.intValue());
  }

  @RequestMapping(params = {"deleteDataset"})
  public void deleteDataset(final @ModelAttribute("editForm") EditForm editForm,
      final BindingResult bindingResult,
      ActionResponse response, ActionRequest request)
  {
    try
    {
      // check if the current user is allowed to edit this dataset
      User ckanuserFromRequest = getCkanuserFromRequestProxy(request);
      // Read metadata for usage after the metadata was deleted
      Metadata metadata = registryClient.getInstance().getMetadata(ckanuserFromRequest, editForm.getName());

      List<Organization> organizationsForUser =
          registryClient.getInstance().getOrganizationsForUser(ckanuserFromRequest, "create_dataset");
      if (new ODRTools().containsOrganization(organizationsForUser, editForm.getOrganizationId()))
      {
        if (registryClient.getInstance().deleteMetadata(ckanuserFromRequest, editForm.getName()))
        {
          response.sendRedirect(
              gdNavigation.createLinkForSearchResults("suchen", "gdsearchresult", "").toString());

          String identifier = metadata.getIdentifierWithFallback();
          fusekiClient.deleteDataset(PortletUtil.getCkanDatasetBaseLink(), metadata.getId(), identifier,
              metadata.getOwnerOrg());
        }
        else
        {
          // we could not delete the dataset!
          throw new OpenDataRegistryException();
        }
      }

    }
    catch (PortalException | SystemException | OpenDataRegistryException | IOException e)
    {
      // show the form again, displaying an error.
      response.setRenderParameter(MESSAGE_TYPE, MessageType.ERROR.toString());
      response.setRenderParameter(MESSAGE, "od.editform.delete.error");
    }
  }

  private User getCkanuserFromRequestProxy(PortletRequest request)
      throws OpenDataRegistryException, PortalException, SystemException
  {
    return new ODRTools().getCkanuserFromRequest(request, registryClient.getInstance());
  }

  private EditForm newDataset()
  {
    EditForm form = new EditForm();
    form.setName(Constants.NAME_NEW_DATASET); // invalid ckan name, but will be replaced

    // fill in mandatory contact rows
    Map<String, Contact> contacts = new HashMap<>();
    for (RoleEnumType role : new RoleEnumType[] {RoleEnumType.CREATOR, RoleEnumType.MAINTAINER,
        RoleEnumType.PUBLISHER})
    {
      contacts.put(role.getField(), new Contact("", "", "", new ContactAddress()));
    }
    form.setContacts(contacts);

    // add one Resource
    List<Resource> resources = new ArrayList<>();
    Resource resource = new Resource();
    resource.setLicenseId(DEFAULT_LICENCE);
    resources.add(resource);
    form.setResources(resources);

    return form;
  }

  /**
   * Loads a dataset from ckan and prepares the EditForm.
   *
   * @param metadataName id of the dataset to load
   * @return
   * @throws OpenDataRegistryException
   */
  private EditForm loadDataset(User ckanuser, String metadataName) throws OpenDataRegistryException
  {
    log.info("loading: " + metadataName);

    Metadata metadata = registryClient.getInstance().getMetadata(ckanuser, metadataName);
    if (metadata == null)
    {
      throw new OpenDataRegistryException("could not find dataset");
    }

    if (!StringUtils.equals(metadata.getState(), Metadata.State.ACTIVE.getValue()))
    {
      throw new OpenDataRegistryException("dataset has been deleted");
    }

    EditForm form = new EditForm();
    form.setName(metadataName);
    form.setPrivate(metadata.isPrivate());
    form.setOrganizationId(metadata.getOwnerOrg());

    // set contributor IDs
    List<String> datasetContributorIdList = metadata.getExtraList(MetadataListExtraFields.CONTRIBUTOR_ID);
    form.setContributorId(
        getGovdataContributorId(
            getContributorIdsFromOrganizations(
                metadata.getOwnerOrg(), organizationCache.getOrganizationsSorted(), false),
            datasetContributorIdList));

    // Categories must be flattened to a List of Strings, so this can be used with checkboxes
    List<String> categories = new ArrayList<>();
    for (Category cat : metadata.getCategories())
    {
      categories.add(cat.getName());
    }
    form.setCategories(categories);

    form.setName(metadata.getName());

    form.setNotes(metadata.getNotes());

    // prepare resources
    List<Resource> resources = new ArrayList<>();
    for (de.seitenbau.govdata.odp.registry.model.Resource resource : metadata.getResources())
    {
      String languageString = resource.getLanguage().stream().collect(Collectors.joining(", "));
      resources.add(new Resource(
          resource.getUrl(),
          resource.getFormat(),
          resource.getDescription(),
          resource.getName(),
          languageString,
          resource.getLicense().getName(),
          resource.getLicenseAttributionByText(),
          formatDate(resource.getLast_modified()),
          resource.getPlannedAvailability()));
    }
    if (resources.isEmpty()) // make sure there is at least one row!
    {
      resources.add(new Resource());
    }
    form.setResources(resources);

    // flatten list using commas as delimiters
    String serializedTags = metadata.getTags().stream().map(Tag::getName).collect(Collectors.joining(", "));
    form.setTags(serializedTags);

    // list extra fields
    form.setPoliciticalGeocodingURI(listToString(metadata, MetadataListExtraFields.POLITICAL_GEOCODING_URI));
    form.setGeocodingText(listToString(metadata, MetadataListExtraFields.GEOCODING_TEXT));
    form.setLegalbasisText(listToString(metadata, MetadataListExtraFields.LEGALBASIS_TEXT));

    form.setTitle(metadata.getTitle());

    form.setUrl(metadata.getUrl());

    form.setQualityProcessURI(metadata.getExtraString(MetadataStringExtraFields.QUALITY_PROCESS_URI));

    form.setPoliciticalGeocodingLevelURI(
        metadata.getExtraString(MetadataStringExtraFields.POLITICAL_GEOCODING_LEVEL_URI));

    form.setTemporalCoverageFrom(formatDate(metadata.getTemporalCoverageFrom()));
    form.setTemporalCoverageUntil(formatDate(metadata.getTemporalCoverageTo()));

    // correct wrong OGD-Schema value for proper use
    String spatial = metadata.getExtraString(MetadataStringExtraFields.SPATIAL);
    if (spatial != null)
    {
      spatial = spatial.replace("polygon", "Polygon");
    }
    form.setSpatial(spatial);

    form.setDatesPublished(formatDate(metadata.getPublished()));
    form.setDatesModified(formatDate(metadata.getModified()));

    Map<String, Contact> contacts = new HashMap<>();
    for (RoleEnumType role : EDITABLE_CONTACTS)
    {
      de.seitenbau.govdata.odp.registry.model.Contact contact = metadata.getContact(role);
      if (contact != null)
      {
        contacts.put(role.getField(),
            new Contact(contact.getName(), contact.getUrl(), contact.getEmail(),
                mapContactAddress(contact.getAddress())));
      }
      else
      {
        // we need to have that contact, so the form can show the row.
        contacts.put(role.getField(), new Contact("", "", "", new ContactAddress()));
      }
    }
    form.setContacts(contacts);

    return form;
  }

  private String listToString(Metadata metadata, MetadataListExtraFields field)
  {
    // flatten list using commas as delimiters
    List<String> list = metadata.getExtraList(field);
    if (list != null)
    {
      return list.stream().collect(Collectors.joining(", "));
    }

    return null;
  }

  private ContactAddress mapContactAddress(de.seitenbau.govdata.odp.registry.ckan.impl.ContactAddress src)
  {
    return new ContactAddress(src.getAddressee(), src.getDetails(), src.getStreet(), src.getCity(),
        src.getZIP(), src.getCountry());
  }

  /**
   * Format Date as String or pass through null
   *
   * @param date date to format
   */
  private String formatDate(Date date)
  {
    return DateUtil.formatDate(date, DATE_PATTERN);
  }

  /**
   * Converts the EditForm to a dataset and saves it to ckan
   *
   * @param form form to save
   * @throws OpenDataRegistryException if saving metadata with the odr client went wrong
   */
  private void saveDataset(EditForm form, de.seitenbau.govdata.odp.registry.model.User ckanUser,
      BindingResult result) throws OpenDataRegistryException
  {
    log.info("saving: " + form.getName());

    // get existing Dataset if available
    Metadata metadata;

    if (form.isNewDataset())
    {
      metadata = registryClient.getInstance().createMetadata();
    }
    else
    {
      metadata = registryClient.getInstance().getMetadata(ckanUser, form.getName());
      if (metadata == null)
      {
        throw new OpenDataRegistryException(
            "dataset is not new, but could not be found in ckan: " + form.getName());
      }
    }

    metadata.setOwnerOrg(form.getOrganizationId());

    metadata.setPrivate(form.isPrivate());

    metadata.setTitle(form.getTitle());

    metadata.setNotes(form.getNotes());

    // Check that it is a valid value, e.g. must contain in the Contributor-IDs of the organization.
    List<String> organizationContributorIds = getContributorIdsFromOrganizations(
        form.getOrganizationId(),
        organizationCache.getOrganizationsSorted(),
        false);
    if (!organizationContributorIds.contains(form.getContributorId()))
    {
      // Add error message
      result.rejectValue(Constants.CONTRIBUTOR_ID, "od.validation_invalid_value", "Der Wert ist ungültig");
    }
    else
    {
      // add the Govdata-Contributor-ID while remove all other Contributor-IDs of the Organization
      List<String> newDatasetContributorIds = new ArrayList<String>();
      newDatasetContributorIds.add(form.getContributorId());
      newDatasetContributorIds.addAll(
          extractNonGovDataContributorIds(
              metadata.getExtraList(MetadataListExtraFields.CONTRIBUTOR_ID),
              organizationContributorIds));
      metadata.setExtraList(MetadataListExtraFields.CONTRIBUTOR_ID, newDatasetContributorIds);
    }

    // tags
    List<Tag> tags = metadata.getTags(); // get the list and replace all tags with the ones from the form
    tags.clear();
    for (String formTag : listFromString(form.getTags()))
    {
      tags.add(new TagImpl(new TagBean(formTag)));
    }

    metadata.setUrl(form.getUrl());

    metadata.setExtraString(MetadataStringExtraFields.QUALITY_PROCESS_URI, form.getQualityProcessURI());

    metadata.setExtraString(MetadataStringExtraFields.SPATIAL, form.getSpatial());

    metadata.setExtraString(MetadataStringExtraFields.POLITICAL_GEOCODING_LEVEL_URI,
        convertBlankStringToNull(form.getPoliciticalGeocodingLevelURI()));

    metadata.setExtraList(MetadataListExtraFields.POLITICAL_GEOCODING_URI,
        listFromString(form.getPoliciticalGeocodingURI()));

    metadata.setExtraList(MetadataListExtraFields.GEOCODING_TEXT,
        listFromString(form.getGeocodingText()));

    metadata.setExtraList(MetadataListExtraFields.LEGALBASIS_TEXT,
        listFromString(form.getLegalbasisText()));

    Date fromDate = DateUtil.parseDateString(form.getTemporalCoverageFrom());
    Date untilDate = DateUtil.parseDateString(form.getTemporalCoverageUntil());
    metadata.setTemporalCoverageFrom(fromDate);
    metadata.setTemporalCoverageTo(untilDate);

    metadata.setPublished(DateUtil.parseDateString(form.getDatesPublished()));
    metadata.setModified(DateUtil.parseDateString(form.getDatesModified()));

    // categories
    List<Category> categories = metadata.getCategories();
    categories.clear(); // we don't want to keep existing categories
    if (form.getCategories() != null)
    {
      for (String formCat : form.getCategories())
      {
        categories.add(categoryCache.getCategoryMap().get(formCat));
      }
    }

    // resources
    List<de.seitenbau.govdata.odp.registry.model.Resource> resources = metadata.getResources();
    resources.clear();
    if (form.getResources() != null)
    {
      for (Resource res : form.getResources())
      {
        ResourceImpl resourceImpl =
            new ResourceImpl(((MetadataImpl) metadata).getOdrClient(), new ResourceBean());
        resourceImpl.setName(res.getName());
        resourceImpl.setDescription(res.getDescription());
        resourceImpl.setUrl(res.getUrl());
        resourceImpl.setFormat(res.getFormat());
        resourceImpl.setLanguage(listFromString(res.getLanguage()));
        resourceImpl.setLast_modified(DateUtil.parseDateString(res.getModified()));
        resourceImpl.setLicense(res.getLicenseId());
        resourceImpl.setLicenseAttributionByText(res.getLicenseAttributionByText());
        resourceImpl.setPlannedAvailability(convertBlankStringToNull(res.getPlannedAvailability()));
        resources.add(resourceImpl);
      }
    }

    // contacts
    if (form.getContacts() != null)
    {
      for (Entry<String, Contact> con : form.getContacts().entrySet())
      {
        try
        {
          // Contact will be automatically stored in the metadata object
          // just by setting it on the Contact Proxy
          de.seitenbau.govdata.odp.registry.model.Contact contact =
              metadata.getContact(RoleEnumType.fromField(con.getKey()));
          contact.setName(con.getValue().getName());
          contact.setEmail(con.getValue().getEmail());
          contact.setUrl(con.getValue().getUrl());

          ContactAddress formAddress = con.getValue().getAddress();
          contact.getAddress().setAddressee(formAddress.getAddressee());
          contact.getAddress().setDetails(formAddress.getDetails());
          contact.getAddress().setStreet(formAddress.getStreet());
          contact.getAddress().setCity(formAddress.getCity());
          contact.getAddress().setZIP(formAddress.getZip());
          contact.getAddress().setCountry(formAddress.getCountry());
        }
        catch (UnknownRoleException e)
        {
          log.error(e.getMessage() + " Not saving this contact.");
        }
      }
    }
    
    if (result.hasErrors())
    {
      // If validation of valid values in lists fails, stop and throw exception.
      throw new ValidationException();
    }

    boolean requestSuccess = false;
    
    try
    {
      requestSuccess = registryClient.getInstance().persistMetadata(ckanUser, metadata);
    }
    catch (ClientErrorException e)
    {
      if (e.getResponse().getStatus() == 409)
      {
        throw new OpenDataRegistryException("od.editform.save.error.alreadyexists");
      }
      else
      {
        throw new OpenDataRegistryException();
      }
    }
    
    if (requestSuccess)
    {
      try {
        Metadata createdMetadata = registryClient.getInstance().getMetadata(ckanUser, metadata.getName());
        String ckanDatasetBaseUrl = PortletUtil.getCkanDatasetBaseLink();
        String identifier = createdMetadata.getIdentifierWithFallback();
        fusekiClient.updateOrCreateDataset(ckanDatasetBaseUrl, createdMetadata.getId(), identifier,
            createdMetadata.getOwnerOrg());
      }
      catch (Exception e) {
        log.warn(e.getMessage());
      }
    }
  }

  private List<String> extractNonGovDataContributorIds(List<String> datasetContributorIds,
      List<String> organizationContributorIds)
  {
    List<String> result = new ArrayList<>();
    if (datasetContributorIds != null)
    {
      // Remove all GovData-ContributorIDs
      for (String contributorId : datasetContributorIds)
      {
        if (!organizationContributorIds.contains(contributorId))
        {
          result.add(contributorId);
        }
      }
    }
    return result;
  }

  private List<String> getContributorIdsFromOrganizations(String orgId, List<Organization> organizations,
      boolean fallback)
  {
    List<String> contributorIdList = new ArrayList<String>();
    if (CollectionUtils.isNotEmpty(organizations))
    {
      if (StringUtils.isEmpty(orgId))
      {
        if (fallback)
        {
          // use organization selected by default
          contributorIdList.addAll(organizations.get(0).getContributorIds());
        }
      }
      else
      {
        for (Organization orga : organizations)
        {
          if (orga.getId().equals(orgId))
          {
            contributorIdList.addAll(orga.getContributorIds());
            break;
          }
        }
      }
    }
    return contributorIdList;
  }

  private String getGovdataContributorId(List<String> contributorsOrg, List<String> datasetContributors)
  {
    if (Objects.nonNull(datasetContributors) && Objects.nonNull(datasetContributors))
    {
      for (String contributorId : datasetContributors)
      {
        if (contributorsOrg.contains(contributorId))
        {
          // should never have more than one match, so return
          return contributorId;
        }
      }
    }
    return null;
  }

  private List<OptionTag> mapToOptionTagList(List<String> values)
  {
    if (Objects.nonNull(values))
    {
      return values.stream().map(value -> new OptionTag(value, value)).collect(Collectors.toList());
    }
    return Collections.emptyList();
  }

  private String convertBlankStringToNull(String value)
  {
    String result = null;
    if (StringUtils.isNotBlank(value))
    {
      result = value;
    }
    return result;
  }

  private List<String> listFromString(String s)
  {
    return Arrays.stream(s.split(",")).map(String::trim).filter(StringUtils::isNotBlank)
        .collect(Collectors.toList());
  }
}
