package de.seitenbau.govdata.edit.gui.controller;

import static de.seitenbau.govdata.navigation.GovDataNavigation.FRIENDLY_URL_NAME_SHOWROOM_PAGE;
import static de.seitenbau.govdata.navigation.GovDataNavigation.PORTLET_NAME_SEARCHRESULT;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.MimeResponse;
import javax.portlet.PortletRequest;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.ResourceURL;
import javax.validation.Valid;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;

import de.seitenbau.govdata.cache.CategoryCache;
import de.seitenbau.govdata.common.api.EntityCreatedResponse;
import de.seitenbau.govdata.common.api.RestUserMetadata;
import de.seitenbau.govdata.common.client.impl.RestCallFailedException;
import de.seitenbau.govdata.common.client.impl.ValidationException;
import de.seitenbau.govdata.common.messaging.SearchIndexEntry;
import de.seitenbau.govdata.common.showcase.model.ShowcasePlatformEnum;
import de.seitenbau.govdata.common.showcase.model.ShowcaseTypeEnum;
import de.seitenbau.govdata.constants.DetailsRequestParamNames;
import de.seitenbau.govdata.db.api.ShowcaseResource;
import de.seitenbau.govdata.db.api.model.Showcase;
import de.seitenbau.govdata.edit.constants.EditCommonConstants;
import de.seitenbau.govdata.edit.mapper.ShowcaseMapper;
import de.seitenbau.govdata.edit.model.Image;
import de.seitenbau.govdata.edit.model.ShowcaseViewModel;
import de.seitenbau.govdata.index.queue.adapter.IndexQueueAdapterServiceRESTResource;
import de.seitenbau.govdata.messages.MessageType;
import de.seitenbau.govdata.navigation.GovDataNavigation;
import de.seitenbau.govdata.odp.common.util.ImageUtil;
import de.seitenbau.govdata.permission.PermissionUtil;
import de.seitenbau.govdata.search.index.IndexConstants;
import de.seitenbau.govdata.search.index.util.SearchIndexUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * Controller für das Bearbeiten von Showcases.
 * 
 * @author sgebhart
 *
 */
@Slf4j
@Controller
@RequestMapping("VIEW")
public class EditShowcaseController
{
  private static final String RESOURCE_ID_GET_FILE = "get-file";

  private static final String REQUEST_PARAM_FILENAME = "filename";

  private static final String MESSAGE = "message";

  private static final String MESSAGE_TYPE = "messageType";

  private static final String VIEW_NAME_EDIT = "showcase-edit";

  @Inject
  private IndexQueueAdapterServiceRESTResource indexClient;

  @Inject
  private CategoryCache categoryCache;

  @Inject
  private GovDataNavigation gdNavigation;

  @Inject
  private ShowcaseResource showcaseResource;

  @Inject
  private SearchIndexUtil searchIndexUtil;

  /**
   * Initializes required variables.
   * 
   * @throws Exception
   */
  @PostConstruct
  public void initialize() throws Exception
  {
    FileUtils.deleteQuietly(EditCommonConstants.PATH_TEMP_DIR_EDIT_PORTLET.toFile());
    Files.createDirectories(EditCommonConstants.PATH_TEMP_DIR_EDIT_PORTLET);
  }


  /**
   * Displaying edit showcase form.
   * 
   * @param showcaseId
   * @param editForm
   * @param result
   * @param request
   * @param response
   * @param model
   * @return
   */
  @RenderMapping
  public String showForm(
      @RequestParam(value = DetailsRequestParamNames.PARAM_METADATA, required = false) String showcaseId,
      @Valid @ModelAttribute("editForm") ShowcaseViewModel editForm,
      BindingResult result,
      RenderRequest request,
      RenderResponse response,
      Model model)
  {
    // check user permission
    boolean userCanEditShowcase = false;

    try
    {
      // make sure we have a logged in user with the required roles
      User user = getLiferayUser(request);
      if (!PermissionUtil.hasEditShowcasePermission(user))
      {
        return failView(model, "od.editform.showcase.accessdenied");
      }
      userCanEditShowcase = true;

      // if we have existing form data, use them
      if (editForm.isInEditing())
      {
        log.debug("data: using editform data");
      }
      // try to (re)load the given showcase
      else if (StringUtils.isNotEmpty(showcaseId) || !editForm.isNewShowcase())
      {
        if (StringUtils.isNotEmpty(showcaseId))
        {
          // workaround, because after updating or deleting showcase the id is concatenated and
          // stays twice
          showcaseId = StringUtils.split(showcaseId, ",")[0];
        }
        else
        {
          showcaseId = String.valueOf(editForm.getId());
        }
        log.debug("data: loading dataset: " + showcaseId);
        editForm = loadDataset(showcaseId);
      }
      // create a new showcase
      else
      {
        log.debug("data: creating a new showcase");
        editForm = newShowcase();
      }

      List<ShowcaseTypeEnum> showcaseTypes = new ArrayList<ShowcaseTypeEnum>();
      for (ShowcaseTypeEnum type : ShowcaseTypeEnum.values())
      {
        showcaseTypes.add(type);
      }

      List<ShowcasePlatformEnum> showcasePlatforms = new ArrayList<ShowcasePlatformEnum>();
      for (ShowcasePlatformEnum plat : ShowcasePlatformEnum.values())
      {
        showcasePlatforms.add(plat);
      }

      ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
      model.addAttribute("themeDisplay", themeDisplay);

      // form data
      model.addAttribute("actionUrl", response.createActionURL().toString());
      model.addAttribute("editForm", editForm);

      // set form structure data for displaying available options
      model.addAttribute("categoryList", categoryCache.getCategoriesSortedByTitle());
      model.addAttribute("platformList", showcasePlatforms);
      model.addAttribute("showcaseTypes", showcaseTypes);

      model.addAttribute(MESSAGE, request.getParameter(MESSAGE));
      model.addAttribute(MESSAGE_TYPE, request.getParameter(MESSAGE_TYPE));
      for (Image image : editForm.getImages())
      {
        if (StringUtils.isNotEmpty(image.getTmpFileName()))
        {
          String tmp = createResourceUrl(response, image.getTmpFileName(), RESOURCE_ID_GET_FILE);
          image.setTempImageResourceUrl(tmp);
        }
      }

      // back to metadata-details view
      String abortUrl = null;
      // do not show button for unsaved new dataset
      if (!editForm.isNewShowcase())
      {
        PortletURL createLinkForMetadata =
            gdNavigation.createLinkForMetadata("gdsearchdetails", Long.toString(editForm.getId()));
        if (createLinkForMetadata != null)
        {
          abortUrl = createLinkForMetadata.toString();
        }
      }
      else
      {
        abortUrl = gdNavigation
            .createLinkForSearchResults(FRIENDLY_URL_NAME_SHOWROOM_PAGE, PORTLET_NAME_SEARCHRESULT, "")
            .toString();
      }
      model.addAttribute("metadataUrl", abortUrl);
    }
    catch (PortalException | SystemException | NumberFormatException | RestCallFailedException e)
    {
      // you can't do anything!
      log.error("Could not load edit dataset form: " + e.getMessage());
      userCanEditShowcase = false;
    }

    model.addAttribute("userCanEditShowcase", userCanEditShowcase);
    return VIEW_NAME_EDIT;
  }

  /**
   * Saves the showcase to the database.
   * 
   * @param editForm
   * @param result
   * @param response
   * @param request
   */
  @RequestMapping
  public void submitForm(@Valid @ModelAttribute("editForm") ShowcaseViewModel editForm, BindingResult result,
      ActionResponse response, ActionRequest request)
  {
    // check if form is valid
    if (!result.hasErrors())
    {
      try
      {
        // make sure we have a logged in user with the required roles
        User user = getLiferayUser(request);
        if (!PermissionUtil.hasEditShowcasePermission(user))
        {
          throw new SystemException("od.editform.showcase.accessdenied");
        }
        Long showcaseId = saveDataset(editForm, user);

        // mark showcase in form as "dirty", so reload the showcase will be triggered to reflect the
        // actual saved data
        editForm.setId(showcaseId);
        editForm.setInEditing(false);
        // Update search index
        updateShowcaseInSearchIndex(showcaseId);

        response.setRenderParameter(MESSAGE_TYPE, MessageType.SUCCESS.toString());
        response.setRenderParameter(MESSAGE, "od.editform.showcase.save.success");
      }
      catch (SystemException | RestCallFailedException e)
      {
        response.setRenderParameter(MESSAGE_TYPE, MessageType.ERROR.toString());
        response.setRenderParameter(MESSAGE, "od.editform.showcase.save.error");
        log.warn("Cannot save or/and load dataset!", e);
      }
      catch (ValidationException e)
      {
        response.setRenderParameter(MESSAGE_TYPE, MessageType.WARNING.toString());
        response.setRenderParameter(MESSAGE, "od.editform.showcase.save.warning");
        log.debug("Form has validation errors: " + result.getAllErrors());
      }
    }
    else
    {
      response.setRenderParameter(MESSAGE_TYPE, MessageType.WARNING.toString());
      response.setRenderParameter(MESSAGE, "od.editform.showcase.save.warning");
      log.debug("Form has errors: " + result.getAllErrors());
    }
  }

  /**
   * Deletes the showcase from the database.
   * 
   * @param editForm
   * @param bindingResult
   * @param response
   * @param request
   * @throws SystemException
   */
  @RequestMapping(params = {"deleteDataset"})
  public void deleteDataset(final @ModelAttribute("editForm") ShowcaseViewModel editForm,
      final BindingResult bindingResult,
      ActionResponse response, ActionRequest request) throws SystemException
  {
    // make sure we have a logged in user with the required roles
    User user = getLiferayUser(request);
    if (!PermissionUtil.hasEditShowcasePermission(user))
    {
      throw new SystemException("od.editform.showcase.accessdenied");
    }

    try
    {
      showcaseResource.delete(editForm.getId());
      // Update search index
      deleteShowcaseInSearchIndex(editForm.getId());
      response.sendRedirect(
          gdNavigation
              .createLinkForSearchResults(FRIENDLY_URL_NAME_SHOWROOM_PAGE, PORTLET_NAME_SEARCHRESULT, "")
              .toString());
    }
    catch (Exception ex)
    {
      throw new SystemException(ex);
    }
  }

  /**
   * Adds an new link to a showcase.
   * 
   * @param editForm
   * @param bindingResult
   * @param response
   */
  @RequestMapping(params = {"addLinkToShowcase"})
  public void addLinkToShowcase(final @ModelAttribute("editForm") ShowcaseViewModel editForm,
      final BindingResult bindingResult,
      ActionResponse response)
  {
    log.debug("addLinkToShowcase");
    editForm.addEmptyLinkToShowcase();
  }

  /**
   * Adds an new link to a dataset.
   * 
   * @param editForm
   * @param bindingResult
   * @param response
   */
  @RequestMapping(params = {"addLinkToDataset"})
  public void addLinkToDataset(final @ModelAttribute("editForm") ShowcaseViewModel editForm,
      final BindingResult bindingResult,
      ActionResponse response)
  {
    log.debug("addLinkToDataset");
    editForm.addEmptyLinkToUsedDataset();
  }

  /**
   * Uploads an image and saves it temporarily to the file system.
   * 
   * @param editForm
   * @param bindingResult
   * @param encodedImage
   * @param imageIndex
   * @param response
   */
  @RequestMapping(params = {"uploadImage"})
  public void uploadImage(final @ModelAttribute("editForm") ShowcaseViewModel editForm,
      final BindingResult bindingResult,
      @RequestParam(value = "uploadImage") String encodedImage,
      @RequestParam(value = "imageIndex") int imageIndex,
      ActionResponse response)
  {
    log.debug("uploadImage, imageIndex: {}", imageIndex);
    List<Image> images = editForm.getImages();
    Image image = images.get(imageIndex);
    // Create image file in temp dir and use the path as img src
    try
    {
      Path tmpImageFile = Files.createTempFile(EditCommonConstants.PATH_TEMP_DIR_EDIT_PORTLET, "", ".png");
      byte[] imageByteArray = ImageUtil.convertBase64StringToByteArray(encodedImage);
      if (ArrayUtils.isNotEmpty(imageByteArray))
      {
        Files.write(tmpImageFile, imageByteArray);
        image.setTmpFileName(tmpImageFile.getFileName().toString());
      }
      else
      {
        response.setRenderParameter(MESSAGE_TYPE, MessageType.ERROR.toString());
        response.setRenderParameter(MESSAGE, "od.editform.showcase.image.upload.error");
        log.warn("Received base64 encoded string could not converted to a byte array!");
      }
    }
    catch (IOException e)
    {
      response.setRenderParameter(MESSAGE_TYPE, MessageType.ERROR.toString());
      response.setRenderParameter(MESSAGE, "od.editform.showcase.image.upload.error");
      log.warn("Error while updating image preview! Details: {}", e.getMessage());
    }
  }

  /**
   * Serves a temporarily saved image as OutputStream to the response.
   * 
   * @param request
   * @param response
   */
  @ResourceMapping(RESOURCE_ID_GET_FILE)
  public void serveImage(ResourceRequest request, ResourceResponse response)
  {
    String fileName = request.getParameter(REQUEST_PARAM_FILENAME);
    log.debug("serveImage, fileName: {}", fileName);
    // Allow only simple file names without path information to prevent attacks to get any file from
    // the file system
    if (ImageUtil.isValidFilename(fileName))
    {
      Path path = EditCommonConstants.PATH_TEMP_DIR_EDIT_PORTLET.resolve(fileName);
      try (ByteArrayOutputStream tmp = new ByteArrayOutputStream();
          OutputStream out = response.getPortletOutputStream())
      {
        Files.copy(path, tmp);
        byte[] thumbnail = ImageUtil.getThumbnail(tmp.toByteArray());
        if (ArrayUtils.isNotEmpty(thumbnail))
        {
          out.write(thumbnail);
          String mimeType = URLConnection.guessContentTypeFromName(fileName);
          int fileSize = thumbnail.length;

          response.setContentType(mimeType);
          response.setContentLength(fileSize);
        }
      }
      catch (IOException e)
      {
        log.warn("Error while serving image preview! Details: {}", e.getMessage());
      }
    }
  }

  /**
   * Removes an image from the edit form.
   * 
   * @param editForm
   * @param bindingResult
   * @param imageIndex
   * @param response
   */
  @RequestMapping(params = {"removeImage"})
  public void removeImage(final @ModelAttribute("editForm") ShowcaseViewModel editForm,
      final BindingResult bindingResult,
      @RequestParam(value = "removeImage") int imageIndex,
      ActionResponse response)
  {
    log.debug("removeImage, imageIndex: {}", imageIndex);
    try
    {
      editForm.removeImage(imageIndex);
    }
    catch (IOException e)
    {
      log.warn("Error while removing image from showcase! Details: {}", e.getMessage());
    }
  }

  private ShowcaseViewModel newShowcase()
  {
    ShowcaseViewModel form = new ShowcaseViewModel();

    form.initNewModel();

    return form;
  }

  /**
   * Loads a showcase from the GovData database and prepares the EditForm.
   *
   * @param showcaseId id of the shocase to load
   * @return
   * @throws SystemException
   */
  private ShowcaseViewModel loadDataset(String showcaseId)
  {
    Long id = Long.parseLong(showcaseId);
    return loadDataset(id);
  }

  /**
   * Loads a showcase from the GovData database and prepares the EditForm.
   *
   * @param showcaseId id of the showcase to load
   * @return
   * @throws SystemException
   */
  private ShowcaseViewModel loadDataset(Long showcaseId)
  {
    log.info("loading showcase: " + showcaseId);

    Showcase showcase = loadEntity(showcaseId);
    ShowcaseViewModel form = ShowcaseMapper.mapShowcaseEntityToViewModel(showcase);
    form.initModel();

    return form;
  }

  /**
   * Loads a showcase from the GovData database.
   *
   * @param showcaseId id of the showcase to load
   * @return
   * @throws SystemException
   */
  private Showcase loadEntity(Long showcaseId)
  {
    log.debug("loadEntity: " + showcaseId);

    try
    {
      Showcase showcase = showcaseResource.read(showcaseId);
      return showcase;
    }
    catch (Exception ex)
    {
      throw new SystemException(ex);
    }
  }

  /**
   * Converts the EditForm to a showcase and saves it to the GovData database.
   *
   * @param form form to save
   * @throws SystemException if saving showcase with the REST client went wrong
   */
  private Long saveDataset(ShowcaseViewModel form, User user) throws SystemException
  {
    log.info("saving: " + form.getTitle());

    Showcase showcaseEntity;
    if (form.isNewShowcase())
    {
      // create
      try
      {
        form.updateModel();
        showcaseEntity = ShowcaseMapper.mapShowcaseViewModelToEntity(form);
        showcaseEntity.setCreatorUserId(String.valueOf(user.getUserId()));
        EntityCreatedResponse entityCreated = showcaseResource.create(showcaseEntity);
        return entityCreated.getId();
      }
      catch (Exception ex)
      {
        throw new SystemException(ex);
      }
    }
    else
    {
      // update
      try
      {
        ShowcaseViewModel inDb = loadDataset(form.getId());
        form.updateModel(inDb);
        showcaseEntity = ShowcaseMapper.mapShowcaseViewModelToEntity(form);
        showcaseResource.update(form.getId(), showcaseEntity);
        return form.getId();
      }
      catch (Exception ex)
      {
        throw new SystemException(ex);
      }
    }
  }

  private void updateShowcaseInSearchIndex(Long showcaseId)
  {
    final String method = "updateShowcaseInSearchIndex() : ";
    log.trace(method + "Start");

    Showcase inDb = loadEntity(showcaseId);
    SearchIndexEntry entry = searchIndexUtil.buildSearchIndexEntryFromShowcase(inDb);

    RestUserMetadata ruMetadata = new RestUserMetadata(IndexConstants.INDEX_MANDANT);
    deleteShowcaseInSearchIndex(showcaseId);
    log.debug("{}Adding showcase to search index...", method);
    indexClient.save(ruMetadata, entry);
    log.debug("{}Added showcase to search index.", method);

    log.trace(method + "End");
  }

  private void deleteShowcaseInSearchIndex(Long showcaseId)
  {
    final String method = "deleteShowcaseInSearchIndex() : ";
    log.trace(method + "Start");

    if (Objects.nonNull(showcaseId))
    {
      SearchIndexEntry entry = searchIndexUtil.createSearchIndexEntryWithBasicInformationShowcase(showcaseId);

      RestUserMetadata ruMetadata = new RestUserMetadata(IndexConstants.INDEX_MANDANT);
      log.debug("{}Deleting showcase from search index...", method);
      indexClient.deleteAndSendDeleteMessage(ruMetadata, Long.toString(showcaseId), entry);
      log.debug("{}Deleted showcase from search index.", method);
    }

    log.trace(method + "End");
  }

  private User getLiferayUser(PortletRequest request)
  {
    User user = null;
    try
    {
      user = PortalUtil.getUser(request);
    }
    catch (PortalException | SystemException e)
    {
      // user is null, everything else will fail. Nothing to do.
    }
    return user;
  }

  private String failView(Model model, String reason)
  {
    model.addAttribute(MESSAGE, reason);
    model.addAttribute(MESSAGE_TYPE, MessageType.ERROR.toString());
    return VIEW_NAME_EDIT;
  }

  private String createResourceUrl(MimeResponse response, String fileName, String resourceId)
  {
    ResourceURL resourceURL = response.createResourceURL();
    resourceURL.setResourceID(resourceId);
    resourceURL.setParameter(REQUEST_PARAM_FILENAME, fileName);
    return resourceURL.toString();
  }
}
