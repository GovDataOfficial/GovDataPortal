package de.seitenbau.govdata.edit.gui.controller;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletRequest;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.validation.Valid;
import javax.ws.rs.ClientErrorException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;

import de.fhg.fokus.odp.registry.ckan.impl.ContactImpl;
import de.fhg.fokus.odp.registry.ckan.impl.ResourceImpl;
import de.fhg.fokus.odp.registry.ckan.impl.TagImpl;
import de.fhg.fokus.odp.registry.ckan.json.ContactBean;
import de.fhg.fokus.odp.registry.ckan.json.ResourceBean;
import de.fhg.fokus.odp.registry.ckan.json.TagBean;
import de.fhg.fokus.odp.registry.model.Category;
import de.fhg.fokus.odp.registry.model.Metadata;
import de.fhg.fokus.odp.registry.model.MetadataEnumType;
import de.fhg.fokus.odp.registry.model.Organization;
import de.fhg.fokus.odp.registry.model.RoleEnumType;
import de.fhg.fokus.odp.registry.model.Tag;
import de.fhg.fokus.odp.registry.model.User;
import de.fhg.fokus.odp.registry.model.exception.OpenDataRegistryException;
import de.seitenbau.govdata.cache.CategoryCache;
import de.seitenbau.govdata.cache.LicenceCache;
import de.seitenbau.govdata.constants.DetailsRequestParamNames;
import de.seitenbau.govdata.edit.gui.common.Constants;
import de.seitenbau.govdata.edit.model.Contact;
import de.seitenbau.govdata.edit.model.EditForm;
import de.seitenbau.govdata.edit.model.Resource;
import de.seitenbau.govdata.messages.MessageType;
import de.seitenbau.govdata.navigation.GovDataNavigation;
import de.seitenbau.govdata.odr.ODRTools;
import de.seitenbau.govdata.odr.RegistryClient;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("VIEW")
class EditController
{
  private static final String DEFAULT_LICENCE = "dl-de-by-2.0";
  private static final String MESSAGE = "message";
  private static final String MESSAGE_TYPE = "messageType";

  @Inject
  RegistryClient registryClient;

  @Inject
  private LicenceCache licenceCache;

  @Inject
  private CategoryCache categoryCache;
  
  @Inject
  private GovDataNavigation gdNavigation;

  SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

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
    try {
      // will throw an exception, if there is no ckan-user
      User ckanuserFromRequest = getCkanuserFromRequestProxy(request);
      
      // load organizations to fill dropdown in form
      organizationsForUser =
          registryClient.getInstance().getOrganizationsForUser(ckanuserFromRequest, "create_dataset");
      
      if(!organizationsForUser.isEmpty()) { // user needs at least 1 organisation to be able to edit datasets
        userCanEditDataset = true;
        
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
          if(!new ODRTools().containsOrganization(organizationsForUser, editForm.getOrganizationId())) {
            userCanEditDataset = false;
            throw new OpenDataRegistryException("User does not belong to the datasets organisation");
          }
        }

        // create a new dataset
        else
        {
          log.debug("data: creating a new dataset");
          editForm = newDataset();
        }
        
        // form data
        model.addAttribute("actionUrl", response.createActionURL().toString());
        model.addAttribute("editForm", editForm);

        // set form structure data for displaying available options
        model.addAttribute("licenceMap", licenceCache.getLicenceMap());
        model.addAttribute("categoryList", categoryCache.getCategoriesSortedByTitle());
        model.addAttribute("organizationList", organizationsForUser);
        
        model.addAttribute(MESSAGE, request.getParameter(MESSAGE));
        model.addAttribute(MESSAGE_TYPE, request.getParameter(MESSAGE_TYPE));
        
        // back to metadata-details view
        String abortUrl = null;
        // do not show button for unsaved new dataset
        if (!editForm.isNewDataset())
        {
          PortletURL createLinkForMetadata =
              gdNavigation.createLinkForMetadata("gdsearchdetails", editForm.getName());
          if(createLinkForMetadata != null) {
            abortUrl = createLinkForMetadata.toString();
          }
        }
        else
        {
          abortUrl = gdNavigation.createLinkForSearchResults("suchen", "gdsearchresult", "").toString();
        }
        model.addAttribute("metadataUrl", abortUrl);
      }
    } catch (OpenDataRegistryException | PortalException | SystemException e) {
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
    Date fromDate = parseDate(editForm.getTemporalCoverageFrom());
    Date untilDate = parseDate(editForm.getTemporalCoverageUntil());
    if (fromDate != null && untilDate != null && fromDate.after(untilDate))
    {
      editForm.setTemporalCoverageFrom(formatDate(untilDate));
      editForm.setTemporalCoverageUntil(formatDate(fromDate));
    } else {
      editForm.setTemporalCoverageFrom(formatDate(fromDate));
      editForm.setTemporalCoverageUntil(formatDate(untilDate));
    }
    
    // check if form is valid
    if (!result.hasErrors())
    {
      try
      {
        User ckanUser = getCkanuserFromRequestProxy(request);
        saveDataset(editForm, ckanUser);
        
        // get the name in the same way he new name is created for new datasets
        if (editForm.isNewDataset())
        {
          editForm.setName(registryClient.getInstance().mungeTitleToName(editForm.getTitle()));
        }

        // reload saved dataset, so we reflect the actual saved data
        editForm = loadDataset(ckanUser, editForm.getName());
        
        response.setRenderParameter(MESSAGE_TYPE, MessageType.SUCCESS.toString());
        response.setRenderParameter(MESSAGE, "od.editform.save.success");
      }
      catch (OpenDataRegistryException | PortalException | SystemException e)
      {
        response.setRenderParameter(MESSAGE_TYPE, MessageType.ERROR.toString());
        if(StringUtils.isNotEmpty(e.getMessage())) {
          response.setRenderParameter(MESSAGE, e.getMessage());
        } else {
          response.setRenderParameter(MESSAGE, "od.editform.save.error");
          e.printStackTrace(); // just so we can look it up... we don't know yet what's going on.
        }
      }
    } else {
      response.setRenderParameter(MESSAGE_TYPE, MessageType.WARNING.toString());
      response.setRenderParameter(MESSAGE, "od.editform.save.warning");
      log.warn("Form has errors: " + result.getAllErrors());
    }
  }

  /**
   * Adds a row of Resources
   * @param editForm
   * @param bindingResult
   * @param response
   */
  @RequestMapping(params = {"addRow"})
  public void addRow(final @ModelAttribute("editForm") EditForm editForm, final BindingResult bindingResult,
      ActionResponse response)
  {
    if(editForm.getResources() == null) {
      editForm.setResources(new ArrayList<Resource>());
    }
    editForm.getResources().add(new Resource());
  }

  /**
   * Removed a row of Resources
   * @param editForm
   * @param bindingResult
   * @param rowId index of the row to delete
   * @param response
   */
  @RequestMapping(params = {"removeRow"})
  public void removeRow(final @ModelAttribute("editForm") EditForm editForm,
      final BindingResult bindingResult,
      @RequestParam(value = "removeRow") Integer rowId,
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
      List<Organization> organizationsForUser =
          registryClient.getInstance().getOrganizationsForUser(ckanuserFromRequest, "create_dataset");
      if(new ODRTools().containsOrganization(organizationsForUser, editForm.getOrganizationId())) {
        if(registryClient.getInstance().deleteMetadata(ckanuserFromRequest, editForm.getName())) {
          response.sendRedirect(
              gdNavigation.createLinkForSearchResults("suchen", "gdsearchresult", "").toString());
        } else {
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
    for (RoleEnumType role : new RoleEnumType[] {RoleEnumType.AUTHOR, RoleEnumType.MAINTAINER,
        RoleEnumType.DISTRIBUTOR})
    {
      contacts.put(role.toField(), new Contact("", "", "", ""));
    }
    form.setContacts(contacts);
    
    // add one Resource
    List<Resource> resources = new ArrayList<>();
    resources.add(new Resource());
    form.setResources(resources);
    
    // default licence
    form.setLicenseId(DEFAULT_LICENCE);
    
    return form;
  }
  
  /**
   * Loads a dataset from ckan and prepares the EditForm.
   * @param metadataName id of the dataset to load
   * @return
   * @throws OpenDataRegistryException
   */
  private EditForm loadDataset(User ckanuser, String metadataName) throws OpenDataRegistryException
  {
    log.info("loading: " + metadataName);

    Metadata metadata = registryClient.getInstance().getMetadata(ckanuser, metadataName);
    if(metadata == null) {
      throw new OpenDataRegistryException("could not find dataset");
    }
    
    if(!StringUtils.equals(metadata.getState(), Metadata.State.ACTIVE.getValue())) {
      throw new OpenDataRegistryException("dataset has been deleted");
    }

    EditForm form = new EditForm();
    form.setName(metadataName);
    form.setPrivate(metadata.isPrivate());
    form.setOrganizationId(metadata.getOwnerOrg());

    // Categories must be flattened to a List of Strings, so this can be used with checkboxes
    List<String> categories = new ArrayList<>();
    for (Category cat : metadata.getCategories())
    {
      categories.add(cat.getName());
    }
    form.setCategories(categories);

    form.setLicenseId(metadata.getLicence().getName());

    form.setName(metadata.getName());

    form.setNotes(metadata.getNotes());

    // prepare resources
    List<Resource> resources = new ArrayList<>();
    for (de.fhg.fokus.odp.registry.model.Resource resource : metadata.getResources())
    {
      resources.add(new Resource(
          resource.getUrl(),
          resource.getFormat(),
          resource.getDescription(),
          resource.getName(),
          resource.getLanguage()));
    }
    if(resources.isEmpty()) { // make sure there is at least one row!
      resources.add(new Resource());
    }
    form.setResources(resources);

    // concat all tags to comma separated list with no trailing comma
    StringBuilder serializedTags = new StringBuilder();
    List<Tag> tags = metadata.getTags();
    for (int i = 0; i < tags.size() - 1; i++)
    {
      serializedTags.append(tags.get(i).getName()).append(", ");
    }
    if (!tags.isEmpty())
    {
      serializedTags.append(tags.get(tags.size() - 1).getName());
    }
    form.setTags(serializedTags.toString());

    form.setTitle(metadata.getTitle());

    form.setTyp(metadata.getType().toField());

    form.setUrl(metadata.getUrl());

    form.setTemporalCoverageFrom(formatDate(metadata.getTemporalCoverageFrom()));
    form.setTemporalCoverageUntil(formatDate(metadata.getTemporalCoverageTo()));

    // correct wrong OGD-Schema value for proper use
    String spatial = metadata.getSpatialDataValue();
    if(spatial != null) {
      spatial = spatial.replace("polygon", "Polygon");
    }
    form.setSpatial(spatial);

    form.setDatesCreated(formatDate(metadata.getCreated()));
    form.setDatesPublished(formatDate(metadata.getPublished()));
    form.setDatesModified(formatDate(metadata.getModified()));

    Map<String, Contact> contacts = new HashMap<>();
    for (RoleEnumType role : new RoleEnumType[] {RoleEnumType.AUTHOR, RoleEnumType.MAINTAINER,
        RoleEnumType.DISTRIBUTOR})
    {
      de.fhg.fokus.odp.registry.model.Contact contact = metadata.getContact(role);
      if (contact != null)
      {
        contacts.put(role.toField(),
            new Contact(contact.getName(), contact.getUrl(), contact.getEmail(), contact.getAddress()));
      }
      else
      {
        // we need to have that contact, so the form can show the row.
        contacts.put(role.toField(), new Contact("", "", "", ""));
      }
    }
    form.setContacts(contacts);

    return form;
  }

  /**
   * Format Date as String or pass through null
   * @param date
   * @return
   */
  private String formatDate(Date date)
  {
    return (date != null ? dateFormat.format(date) : null);
  }

  private Date parseDate(String date)
  {
    try
    {
      return (StringUtils.isEmpty(date) ? null : dateFormat.parse(date));
    }
    catch (ParseException e)
    {
      // if we don't understand the date, we return null.
      return null;
    }
  }

  /**
   * Converts the EditForm to a dataset and saves it to ckan
   * @param editForm form to save
   * @throws OpenDataRegistryException
   */
  private void saveDataset(EditForm form, de.fhg.fokus.odp.registry.model.User ckanUser)
      throws OpenDataRegistryException
  {
    log.info("saving: " + form.getName());
    
    // get existing Dataset if available
    Metadata metadata = null;

    if (form.isNewDataset())
    {
      metadata = registryClient.getInstance().createMetadata(MetadataEnumType.fromField(form.getTyp()));
    }
    else
    {
      metadata = registryClient.getInstance().getMetadata(ckanUser, form.getName());
      if (metadata == null)
      {
        throw new OpenDataRegistryException("dataset is not new, but could not be found in ckan: " + form.getName());
      }
    }
    
    metadata.setOwnerOrg(form.getOrganizationId());
    
    metadata.setPrivate(form.isPrivate());

    metadata.setTitle(form.getTitle());
    
    metadata.setNotes(form.getNotes());

    // tags
    List<Tag> tags = metadata.getTags(); // get the list and add tags
    String[] formTags = form.getTags().trim().split(",");
    tags.clear(); // we don't want to keep existing tags
    if (formTags.length > 0)
    {
      for (String formTag : formTags)
      {
        formTag = formTag.trim();
        if(formTag.length() > 0) {
          TagBean tagBean = new TagBean();
          tagBean.setName(formTag);
          tagBean.setDisplay_name(formTag);
          tags.add(new TagImpl(tagBean));
        }
      }
    }

    metadata.setUrl(form.getUrl());

    metadata.setType(MetadataEnumType.fromField(form.getTyp()));

    metadata.setLicence(licenceCache.getLicenceMap().get(form.getLicenseId()));

    metadata.setSpatialDataValue(form.getSpatial());

    Date fromDate = parseDate(form.getTemporalCoverageFrom());
    Date untilDate = parseDate(form.getTemporalCoverageUntil());
    metadata.setTemporalCoverageFrom(fromDate);
    metadata.setTemporalCoverageTo(untilDate);

    metadata.setCreated(parseDate(form.getDatesCreated()));
    metadata.setPublished(parseDate(form.getDatesPublished()));
    metadata.setModified(parseDate(form.getDatesModified()));

    // categories
    List<Category> categories = metadata.getCategories();
    categories.clear(); // we don't want to keep existing categories
    if(form.getCategories() != null) {
      for (String formCat : form.getCategories())
      {
        categories.add(categoryCache.getCategoryMap().get(formCat));
      }
    }

    // resources
    List<de.fhg.fokus.odp.registry.model.Resource> resources = metadata.getResources();
    resources.clear();
    if (form.getResources() != null)
    {
      for (Resource res : form.getResources())
      {
        ResourceBean resourceBean = new ResourceBean();
        resourceBean.setName(res.getName());
        resourceBean.setDescription(res.getDescription());
        resourceBean.setFormat(res.getFormat());
        resourceBean.setUrl(res.getUrl());
        resourceBean.setLanguage(res.getLanguage());
        resources.add(new ResourceImpl(resourceBean));
      }
    }

    // contacts
    List<de.fhg.fokus.odp.registry.model.Contact> contacts = metadata.getContacts();
    contacts.clear();
    if (form.getContacts() != null)
    {
      for (Entry<String, Contact> con : form.getContacts().entrySet())
      {
        // don't save contact if name is empty
        if(StringUtils.isEmpty(con.getValue().getName())) {
          continue;
        }
        
        ContactBean contactBean = new ContactBean();
        contactBean.setRole(con.getKey());
        contactBean.setName(con.getValue().getName());
        contactBean.setEmail(con.getValue().getEmail());
        contactBean.setUrl(con.getValue().getUrl());
        contactBean.setAddress(con.getValue().getAddress());
        contacts.add(new ContactImpl(contactBean));
      }
    }

    try {
      registryClient.getInstance().persistMetadata(ckanUser, metadata);
    } catch(ClientErrorException e) {
      if(e.getResponse().getStatus() == 409) {
        throw new OpenDataRegistryException("od.editform.save.error.alreadyexists");
      } else {
        throw new OpenDataRegistryException();
      }
    }
  }
}
