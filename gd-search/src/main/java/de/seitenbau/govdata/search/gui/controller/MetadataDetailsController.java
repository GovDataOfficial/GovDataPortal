package de.seitenbau.govdata.search.gui.controller;

import static de.seitenbau.govdata.navigation.GovDataNavigation.FRIENDLY_URL_NAME_DATASET_PAGE;
import static de.seitenbau.govdata.navigation.GovDataNavigation.FRIENDLY_URL_NAME_SHOWROOM_PAGE;
import static de.seitenbau.govdata.navigation.GovDataNavigation.PORTLET_NAME_SEARCHRESULT;

import java.io.IOException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;
import javax.portlet.MimeResponse;
import javax.portlet.MimeResponse.Copy;
import javax.portlet.PortletMode;
import javax.portlet.PortletModeException;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.ResourceURL;
import javax.portlet.WindowStateException;
import javax.ws.rs.NotFoundException;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import com.google.common.collect.Lists;
import com.liferay.counter.kernel.service.CounterLocalServiceUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.security.auth.GuestOrUserUtil;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portletmvc4spring.bind.annotation.RenderMapping;
import com.liferay.portletmvc4spring.bind.annotation.ResourceMapping;

import de.fhg.fokus.odp.entities.model.MetadataComment;
import de.fhg.fokus.odp.entities.service.MetadataCommentLocalServiceUtil;
import de.seitenbau.govdata.cache.CategoryCache;
import de.seitenbau.govdata.cache.OrganizationCache;
import de.seitenbau.govdata.clean.StringCleaner;
import de.seitenbau.govdata.common.api.PageableRequest;
import de.seitenbau.govdata.common.api.PagedList;
import de.seitenbau.govdata.common.api.SearchFilter;
import de.seitenbau.govdata.common.client.impl.EntityNotFoundException;
import de.seitenbau.govdata.constants.DetailsRequestParamNames;
import de.seitenbau.govdata.constants.QueryParamNames;
import de.seitenbau.govdata.dataset.details.beans.CurrentMetadataContact;
import de.seitenbau.govdata.dataset.details.beans.CurrentUser;
import de.seitenbau.govdata.dataset.details.beans.ISelectedObject;
import de.seitenbau.govdata.dataset.details.beans.MetadataCommentWrapperExt;
import de.seitenbau.govdata.dataset.details.beans.SelectedMetadata;
import de.seitenbau.govdata.dataset.details.beans.SelectedShowcase;
import de.seitenbau.govdata.db.api.ShowcaseResource;
import de.seitenbau.govdata.db.api.model.Showcase;
import de.seitenbau.govdata.edit.mapper.ShowcaseMapper;
import de.seitenbau.govdata.edit.model.Link;
import de.seitenbau.govdata.edit.model.ShowcaseViewModel;
import de.seitenbau.govdata.messages.MessageKey;
import de.seitenbau.govdata.navigation.GovDataNavigation;
import de.seitenbau.govdata.odp.common.cache.BaseCache;
import de.seitenbau.govdata.odp.common.filter.SearchConsts;
import de.seitenbau.govdata.odp.registry.model.Metadata;
import de.seitenbau.govdata.odp.registry.model.Organization;
import de.seitenbau.govdata.odp.registry.model.exception.OpenDataRegistryException;
import de.seitenbau.govdata.odr.ODRTools;
import de.seitenbau.govdata.odr.RegistryClient;
import de.seitenbau.govdata.permission.PermissionUtil;
import de.seitenbau.govdata.redis.adapter.RedisClientAdapter;
import de.seitenbau.govdata.redis.util.RedisReportUtil;
import de.seitenbau.govdata.search.common.NotificationMailSender;
import de.seitenbau.govdata.search.common.NotificationMailSender.EventType;
import de.seitenbau.govdata.search.util.RequestUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * Controller for the metadata details page.
 *
 * @author rnoerenberg
 */
@Slf4j
@RequestMapping("VIEW")
public class MetadataDetailsController
{
  private static final String MODEL_KEY_CURRENT_USER = "currentUser";

  private static final String MODEL_KEY_COMMENT = "comment";

  private static final String MODEL_KEY_SELECTED_METADATA = "selectedMetadata";

  private static final String HTTP_STATUS_INTERNAL_SERVER_ERROR = "500";

  private static final String VIEW_MODEL_NAME = "details";

  private static final String APP_VIEW_MODEL_NAME = "appdetails";

  private static final String SUBMIT_RATING_RESOURCE_URL_ID = "submitRating";

  private static final String ADD_COMMENT_RESOURCE_URL_ID = "addComment";

  private static final String EDIT_COMMENT_RESOURCE_URL_ID = "editComment";

  private static final String DELETE_COMMENT_RESOURCE_URL_ID = "deleteComment";

  private static final String HTTP_STATUS_FORBIDDEN = "403";

  private static final String HTTP_STATUS_BAD_REQUEST = "400";

  private static final String HTTP_STATUS_NOT_FOUND = "404";

  private static final String HTTP_STATUS_OK = "200";

  private static final String MODEL_KEY_COMMENTS_ENABLED = "commentsEnabled";

  private static final String MODEL_KEY_GUEST_COMMENTS_ENABLED = "guestCommentsEnabled";

  private static final String MODEL_KEY_RATINGS_ENABLED = "ratingsEnabled";

  private static final String MODEL_KEY_CANEDIT = "userCanEditDataset";

  private static final String MODEL_KEY_EDITDATASETURL = "editDatasetUrl";

  private static final String MODEL_KEY_SHOW_BROKEN_LINKS_HINT = "showBrokenLinksHint";

  private static final String MODEL_KEY_METADATA_JSON_LD = "metadataJsonLd";

  private static final String USED_DATASETS_URL_COLUMN = "usedDatasets.url";

  @Inject
  private RegistryClient registryClient;

  @Inject
  private RedisClientAdapter redisClientAdapter;

  @Inject
  private NotificationMailSender notificationMailSender;

  @Inject
  private GovDataNavigation gdNavigation;

  @Inject
  private ShowcaseResource showcaseResource;

  @Inject
  private CategoryCache categoryCache;

  @Inject
  private OrganizationCache organizationCache;

  private ShowcaseDatabaseAvailabileCache showcaseDbAvailableCache = new ShowcaseDatabaseAvailabileCache();

  /**
   * Display details view for datasets, showcases, blogs and articles.
   * @param metadataIdOrName
   * @param request
   * @param response
   * @param model
   * @return
   * @throws PortalException
   * @throws SystemException
   * @throws PortletModeException
   * @throws WindowStateException
   */
  @RenderMapping
  public String showMetadataDetails(
      @RequestParam(value = DetailsRequestParamNames.PARAM_METADATA,
          required = false) String metadataIdOrName,
      RenderRequest request,
      RenderResponse response,
      Model model) throws PortalException, SystemException, PortletModeException, WindowStateException
  {
    final String method = "showMetadataDetails() : ";
    log.trace(method + "Start");

    // Nur Detailansicht rendern, wenn das FriendlyUrlMapping für das Details-Portlet in der URL
    // steht.
    if (!RequestUtil.isResponsible(request, SearchConsts.FRIENDLY_URL_MAPPING_DETAILS))
    {
      log.debug(method + "End, do nothing.");
      return null;
    }

    log.debug(method + "metadata param is " + metadataIdOrName);
    if (metadataIdOrName == null)
    {
      log.debug(method + "End, metadata param is null.");
      return null;
    }

    ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
    PortletPreferences portletPreferences = request.getPreferences();

    // CurrentUser
    User liferayUser = PortalUtil.getUser(request);
    CurrentUser currentUser = findOrCreateCurrentUser(liferayUser);

    // Metadata
    boolean showBrokenLinksHint =
        GetterUtil.getBoolean(portletPreferences.getValue(MODEL_KEY_SHOW_BROKEN_LINKS_HINT, StringPool.TRUE));


    SelectedMetadata selectedMetadata =
        createMetadata(metadataIdOrName, themeDisplay, currentUser.getCkanUser(), showBrokenLinksHint);

    if (selectedMetadata.getMetadata() == null)
    {
      // Did not find a dataset with the id. Check for Showcases.
      SelectedShowcase selectedShowcase = createSelectedShowcase(metadataIdOrName, currentUser);
      if (Objects.nonNull(selectedShowcase))
      {
        log.info("showcase {} found!", metadataIdOrName);

        if (selectedShowcase.isShowcaseVisibleForUser())
        {
          // Set showcase details as html website meta information
          setSiteInformationFromMetadata(themeDisplay.getLayout(), selectedShowcase);
          // create Link to edit form
          String url = gdNavigation
              .createLinkForShowcaseEdit("gdeditshowcaseportlet",
                  selectedShowcase.getShowcase().getId().toString())
              .toString();
          model.addAttribute(MODEL_KEY_EDITDATASETURL, url);
        }

        model.addAttribute(AbstractBaseController.MODEL_KEY_THEME_DISPLAY, themeDisplay);
        model.addAttribute("selectedShowcase", selectedShowcase);
        return APP_VIEW_MODEL_NAME;
      }
    }

    PortletURL actionUrl = response.createActionURL(Copy.NONE);
    selectedMetadata.setActionUrl(actionUrl.toString());
    selectedMetadata.setCurrentUser(currentUser);
    boolean userCanEditDataset = false;

    // do we have found the metadata?
    String metadataName = metadataIdOrName;
    if (selectedMetadata.getMetadata() != null)
    {
      metadataName = selectedMetadata.getMetadata().getName();

      // can edit this Metadata?
      if (currentUser.getCkanUser() != null)
      {
        // we have a ckan-user, so see if the user has the organisation this metadata belongs to.
        List<Organization> organizationsForUser =
            registryClient.getInstance().getOrganizationsForUser(currentUser.getCkanUser(), "create_dataset");
        if (new ODRTools().containsOrganization(
            organizationsForUser, selectedMetadata.getMetadata().getOwnerOrg()))
        {
          userCanEditDataset = true;

          // create Link to edit form
          String url =
              gdNavigation.createLinkForMetadataEdit(
                  "gdeditportlet", selectedMetadata.getMetadata().getName()).toString();
          model.addAttribute(MODEL_KEY_EDITDATASETURL, url);
        }
      }
      // add schema.org JSON-LD
      final String currentUrl = gdNavigation.createLinkForMetadata(
          "gdsearchdetails", metadataIdOrName, FRIENDLY_URL_NAME_DATASET_PAGE).toString();
      final String catalogUrl = gdNavigation.createLinkForSearchResults(FRIENDLY_URL_NAME_DATASET_PAGE,
          PORTLET_NAME_SEARCHRESULT, "").toString();
      String jsonLd = registryClient.getInstance().getJsonLdMetadata(currentUser.getCkanUser(),
          metadataIdOrName, currentUrl, catalogUrl);
      if (jsonLd == null)
      {
        // output empty node if something went wrong
        jsonLd = "";
      }
      // remove potential html tags, as text is rendered without escaping due to quotes in JSON data
      jsonLd = StringCleaner.trimAndFilterString(jsonLd);
      model.addAttribute(MODEL_KEY_METADATA_JSON_LD, jsonLd);
      // set organization
      selectedMetadata.setOrganizationName(getOrganizationName(selectedMetadata.getMetadata().getOwnerOrg()));

      // Get all Apps with the dataset
      List<Link> showcaseListForDataset = readShowcaseListForDataset(metadataIdOrName);
      selectedMetadata.setLinksToShowcases(showcaseListForDataset);
    }

    // set url for submitting rating via ajax
    selectedMetadata.setRatingActionUrl(
        createResourceUrl(response, metadataName, SUBMIT_RATING_RESOURCE_URL_ID));
    // Resource Urls for commenting
    selectedMetadata.setAddCommentResourceURL(
        createResourceUrl(response, metadataName, ADD_COMMENT_RESOURCE_URL_ID));
    selectedMetadata.setEditCommentResourceURL(
        createResourceUrl(response, metadataName, EDIT_COMMENT_RESOURCE_URL_ID));
    selectedMetadata.setDeleteCommentResourceURL(
        createResourceUrl(response, metadataName, DELETE_COMMENT_RESOURCE_URL_ID));

    model.addAttribute(MODEL_KEY_SELECTED_METADATA, selectedMetadata);
    model.addAttribute(MODEL_KEY_CURRENT_USER, currentUser);
    model.addAttribute(AbstractBaseController.MODEL_KEY_THEME_DISPLAY, themeDisplay);
    model.addAttribute(MODEL_KEY_CANEDIT, userCanEditDataset);

    // create LoginURL to be used with the fastlogin-popup - anonymous users can comment that way
    PortletURL loginURL =
        PortletURLFactoryUtil.create(
            request, PortletKeys.FAST_LOGIN, themeDisplay.getPlid(), PortletRequest.RENDER_PHASE);
    loginURL.setParameter("saveLastPath", Boolean.FALSE.toString());
    loginURL.setParameter("struts_action", "/login/login");
    loginURL.setPortletMode(PortletMode.VIEW);
    loginURL.setWindowState(LiferayWindowState.POP_UP);
    model.addAttribute("loginurl", loginURL);

    // get configuration for portlet
    boolean ratingsEnabled =
        GetterUtil.getBoolean(portletPreferences.getValue("ratingsEnabled", StringPool.TRUE));
    boolean commentsEnabled =
        GetterUtil.getBoolean(portletPreferences.getValue("commentsEnabled", StringPool.TRUE));
    boolean guestCommentsEnabled =
        GetterUtil.getBoolean(portletPreferences.getValue("guestCommentsEnabled", StringPool.TRUE));
    model.addAttribute(MODEL_KEY_RATINGS_ENABLED, ratingsEnabled);
    model.addAttribute(MODEL_KEY_COMMENTS_ENABLED, commentsEnabled);
    model.addAttribute(MODEL_KEY_GUEST_COMMENTS_ENABLED, guestCommentsEnabled);

    log.trace(method + "End");
    return VIEW_MODEL_NAME;
  }

  /**
   * Add a comment to a dataset.
   * @param request
   * @param response
   * @param model
   * @return
   * @throws PortalException
   * @throws SystemException
   */
  @ResourceMapping(value = ADD_COMMENT_RESOURCE_URL_ID)
  public String addComment(
      ResourceRequest request,
      ResourceResponse response,
      Model model) throws PortalException, SystemException
  {
    final String method = "addComment() : ";
    log.trace(method + "Start");

    String emailAddress = request.getParameter(QueryParamNames.PARAM_EMAIL_ADDRESS);

    String newComment = request.getParameter(QueryParamNames.PARAM_COMMENT);
    String metadataName = request.getParameter(QueryParamNames.PARAM_DATASET);
    log.debug(method + "dataset: {}", metadataName);

    // Trim and filter comment.
    if (StringUtils.isNotEmpty(metadataName) && StringUtils.isNotEmpty(newComment))
    {
      try
      {
        ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
        // Check permission and existence of metadata
        User liferayUser = GuestOrUserUtil.getGuestOrUser(); // request User from BaseServiceImpl
        CurrentUser currentUser = findOrCreateCurrentUser(liferayUser);

        // get id of newly created temporary user, if this was a guest session
        User commentAuthor = liferayUser;
        if (liferayUser.isDefaultUser() && emailAddress != null)
        {
          User temporaryUser =
              UserLocalServiceUtil.getUserByEmailAddress(themeDisplay.getCompanyId(), emailAddress);
          if (temporaryUser.getStatus() == 6)
          { // 6 = user created as inactive guest user
            commentAuthor = temporaryUser;
          }
        }

        if (PermissionUtil.hasCreateCommentPermission(liferayUser)
            && (getMetadataFromCkan(currentUser.getCkanUser(), StringUtils.trim(metadataName)) != null))
        {
          MetadataComment comment = MetadataCommentLocalServiceUtil
              .createMetadataComment(CounterLocalServiceUtil.increment(MetadataComment.class.getName()));

          comment.setText(newComment);
          comment.setUserLiferayId(commentAuthor.getUserId());
          comment.setMetadataName(metadataName);
          comment.setCreated(new Date());

          MetadataCommentLocalServiceUtil.addMetadataComment(comment);
          log.debug(method + "Added comment with ID {} for metadata {}.", comment.get_id(), metadataName);

          model.addAttribute(MODEL_KEY_COMMENT, wrapComment(comment, themeDisplay));
          SelectedMetadata selectedMetadata = new SelectedMetadata();

          selectedMetadata.setCurrentUser(currentUser);
          // Resource Urls for commenting
          selectedMetadata.setAddCommentResourceURL(
              createResourceUrl(response, metadataName, ADD_COMMENT_RESOURCE_URL_ID));
          selectedMetadata.setEditCommentResourceURL(
              createResourceUrl(response, metadataName, EDIT_COMMENT_RESOURCE_URL_ID));
          selectedMetadata.setDeleteCommentResourceURL(
              createResourceUrl(response, metadataName, DELETE_COMMENT_RESOURCE_URL_ID));
          model.addAttribute(MODEL_KEY_SELECTED_METADATA, selectedMetadata);

          long companyId = PortalUtil.getCompanyId(request);
          Metadata metadata = getMetadataFromCkan(currentUser.getCkanUser(), metadataName);
          notificationMailSender.notifyCommentEvent(companyId, newComment,
              EventType.NEW, liferayUser, metadata,
              themeDisplay.getLocale());
        }
        else
        {
          log.debug(method + "Der Benutzer besitzt nicht das Recht Kommentare zu erstellen.");
          response.setProperty(ResourceResponse.HTTP_STATUS_CODE, HTTP_STATUS_FORBIDDEN);
          return null;
        }
      }
      catch (SystemException e)
      {
        log.warn(e.getMessage());
        response.setProperty(ResourceResponse.HTTP_STATUS_CODE, HTTP_STATUS_INTERNAL_SERVER_ERROR);
        return null;
      }
    }
    else
    {
      log.debug(method + "End, do nothing. No comment and/or dataset received.");
      response.setProperty(ResourceResponse.HTTP_STATUS_CODE, HTTP_STATUS_BAD_REQUEST);
      return null;
    }

    log.debug(method + "End");
    return "fragments/comments :: commentEntry";
  }

  /**
   * Edit a comment of a dataset.
   * @param request
   * @param response
   * @param model
   * @return
   * @throws PortalException
   * @throws SystemException
   */
  @ResourceMapping(value = EDIT_COMMENT_RESOURCE_URL_ID)
  public View editComment(
      ResourceRequest request,
      ResourceResponse response,
      Model model) throws PortalException, SystemException
  {
    final String method = "editComment() : ";
    log.trace(method + "Start");

    String commentId = request.getParameter(QueryParamNames.PARAM_COMMENT_ID);
    log.debug(method + "commentId: {}", commentId);
    String newComment = request.getParameter(QueryParamNames.PARAM_COMMENT);

    MappingJackson2JsonView view = new MappingJackson2JsonView();
    // Trim and filter comment.
    if (StringUtils.isNotBlank(commentId) && StringUtils.isNotBlank(newComment))
    {
      try
      {
        // check existence of metadata comment
        long id = Long.parseLong(commentId);
        MetadataComment metadataComment = MetadataCommentLocalServiceUtil.getMetadataComment(id);
        if (metadataComment != null)
        {
          // Check permission
          User liferayUser = PortalUtil.getUser(request);
          CurrentUser currentUser = findOrCreateCurrentUser(liferayUser);
          if (PermissionUtil.hasEditCommentPermission(liferayUser, metadataComment.getUserLiferayId()))
          {
            metadataComment.setText(newComment);
            MetadataCommentLocalServiceUtil.updateMetadataComment(metadataComment);
            log.debug(method + "Kommentar mit ID {} geändert.", commentId);
            view.addStaticAttribute(QueryParamNames.PARAM_COMMENT, newComment);

            long companyId = PortalUtil.getCompanyId(request);
            ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
            Metadata metadata =
                getMetadataFromCkan(currentUser.getCkanUser(), metadataComment.getMetadataName());
            notificationMailSender.notifyCommentEvent(companyId, newComment,
                EventType.CHANGED, liferayUser, metadata,
                themeDisplay.getLocale());
          }
          else
          {
            log.debug(method + "Der Benutzer hat kein Recht zum Editieren des Kommentars mit der ID {}",
                commentId);
            addMessageForUser(view, request, response, MessageKey.NO_PERMISSION, HTTP_STATUS_FORBIDDEN);
          }
        }
        else
        {
          log.debug(method + "Kein Kommentar zur ID {} gefunden.", commentId);
          addMessageForUser(view, request, response, MessageKey.COMMENT_NOT_EXISTENT,
              HTTP_STATUS_BAD_REQUEST);
        }
      }
      catch (Exception e)
      {
        log.warn(method + e.getMessage());
        addMessageForUser(view, request, response, MessageKey.ERROR_OCCURRED,
            HTTP_STATUS_INTERNAL_SERVER_ERROR);
      }
    }
    else
    {
      log.debug(method + "End, tue nichts. Kein Kommentar im Request gefunden.");
      addMessageForUser(view, request, response, MessageKey.ERROR_OCCURRED, HTTP_STATUS_BAD_REQUEST);
    }

    log.trace(method + "End");
    return view;
  }

  /**
   * Delete a comment of a dataset.
   * @param request
   * @param response
   * @param model
   * @return
   * @throws PortalException
   * @throws SystemException
   */
  @ResourceMapping(value = DELETE_COMMENT_RESOURCE_URL_ID)
  public View deleteComment(
      ResourceRequest request,
      ResourceResponse response,
      Model model) throws PortalException, SystemException
  {
    final String method = "deleteComment() : ";
    log.trace(method + "Start");

    String commentId = request.getParameter(QueryParamNames.PARAM_COMMENT_ID);
    log.debug(method + "commentId: {}", commentId);

    MappingJackson2JsonView view = new MappingJackson2JsonView();
    if (StringUtils.isNotBlank(commentId))
    {
      try
      {
        // check existence of metadata comment
        long id = Long.parseLong(commentId);
        MetadataComment metadataComment = MetadataCommentLocalServiceUtil.getMetadataComment(id);
        if (metadataComment != null)
        {
          // Check permission
          User liferayUser = PortalUtil.getUser(request);
          if (PermissionUtil.hasDeleteCommentPermission(liferayUser, metadataComment.getUserLiferayId()))
          {
            MetadataCommentLocalServiceUtil.deleteMetadataComment(metadataComment.get_id());
            log.debug(method + "Kommentar mit ID {} gelöscht.", commentId);
            addMessageForUser(view, request, response, MessageKey.COMMENT_DELETED_SUCCESS, HTTP_STATUS_OK);
          }
          else
          {
            log.debug(method + "Der Benutzer hat kein Recht zum Löschen des Kommentars mit der ID {}",
                commentId);
            addMessageForUser(view, request, response, MessageKey.NO_PERMISSION, HTTP_STATUS_FORBIDDEN);
          }
        }
        else
        {
          log.debug(method + "Kein Kommentar zur ID {} gefunden.", commentId);
          addMessageForUser(view, request, response, MessageKey.COMMENT_NOT_EXISTENT, HTTP_STATUS_NOT_FOUND);
        }
      }
      catch (Exception e)
      {
        log.warn(method + e.getMessage());
        addMessageForUser(view, request, response, MessageKey.ERROR_OCCURRED,
            HTTP_STATUS_INTERNAL_SERVER_ERROR);
      }
    }
    else
    {
      log.debug(method + "End, tue nichts. Keine Kommentar-ID im Request gefunden.");
      addMessageForUser(view, request, response, MessageKey.ERROR_OCCURRED, HTTP_STATUS_BAD_REQUEST);
      return null;
    }

    log.trace(method + "End");
    return view;
  }

  /**
   * Save a rating for a dataset.
   * @param request
   * @param response
   * @param model
   * @return
   * @throws IOException
   */
  @ResourceMapping(value = SUBMIT_RATING_RESOURCE_URL_ID)
  public View submitRating(
      ResourceRequest request,
      ResourceResponse response,
      Model model) throws IOException
  {
    final String method = "submitRating(): ";
    log.debug(method + "Start");

    String rating = request.getParameter(QueryParamNames.PARAM_RATING);
    log.debug(method + "rating: {}", rating);

    String dataset = request.getParameter(QueryParamNames.PARAM_DATASET);
    log.debug(method + "dataset: {}", dataset);

    MappingJackson2JsonView view = new MappingJackson2JsonView();

    try
    {
      // get the current user
      User liferayUser = PortalUtil.getUser(request);
      CurrentUser currentUser = findOrCreateCurrentUser(liferayUser);

      // check rating score
      int ratingInt = Integer.parseInt(rating);

      // submit rating to ckan
      log.debug(method
          + "rateMetadata({}, {}, {})", currentUser.getCkanUser().getDisplayName(), dataset, ratingInt);
      registryClient.getInstance().rateMetadata(currentUser.getCkanUser(), dataset, ratingInt);

      // get updated metadata
      // next three lines commented out, because the information about the rating is no more
      // accessible through the only available action api
      // Metadata metadata = getMetadataFromCkan(currentUser.getCkanUser(), dataset);
      // view.addStaticAttribute("avgRating", Math.round(metadata.getAverageRating()));
      // view.addStaticAttribute("ratingCount", metadata.getRatingCount());
      view.addStaticAttribute("avgRating", 0);
      view.addStaticAttribute("ratingCount", 0);
    }
    catch (Exception e)
    {
      log.warn(method + e.getMessage());
      response.setProperty(ResourceResponse.HTTP_STATUS_CODE, HTTP_STATUS_INTERNAL_SERVER_ERROR);
      return null;
    }

    view.addStaticAttribute("rating", rating);
    log.debug(method + "End");
    return view;
  }

  private SelectedShowcase createSelectedShowcase(String metadataIdOrName, CurrentUser currentUser)
  {
    final String method = "createSelectedShowcase() : ";
    log.trace(method + "Start");

    SelectedShowcase selectedShowcase = null;
    if (showcaseDbAvailableCache.isAvailable())
    {
      try
      {
        Long appId = Long.parseLong(metadataIdOrName);
        Showcase showcase = showcaseResource.read(appId);
        Objects.requireNonNull(showcase);
        ShowcaseViewModel showcaseModel = ShowcaseMapper.mapShowcaseEntityToViewModel(showcase);
        selectedShowcase = new SelectedShowcase(showcaseModel, currentUser, categoryCache.getCategoryMap());
      }
      catch (NumberFormatException ex)
      {
        // Invalid showcase id. Continue.
        log.debug("Gets an invalid showcase identifier: {}", metadataIdOrName);
      }
      catch (EntityNotFoundException ex)
      {
        // Cannot find a showcase for the id. Continue.
        log.info("Did not find a showcase for the identifier: {}", metadataIdOrName);
      }
      catch (Exception ex)
      {
        // Workaround for showcase was not found, because of bad exception mapping in showcase
        // client
        Throwable cause = ex.getCause();
        if (cause instanceof NotFoundException)
        {
          // Cannot find a showcase for the id. Continue.
          log.info("Did not find a showcase for the identifier: {}", metadataIdOrName);
        }
        else
        {
          log.warn("{}Unexpected error while reading showcase with ID {}. Details: {}. "
              + "Disable reading showcases for {} {}.", method, metadataIdOrName, ex.getMessage(),
              showcaseDbAvailableCache.getMaxCacheTimeAmount(),
              showcaseDbAvailableCache.getCacheTemporalUnit());
          showcaseDbAvailableCache.markAsNotAvailable();
        }
      }
    }

    log.trace(method + "End");
    return selectedShowcase;
  }

  private List<Link> readShowcaseListForDataset(String metadataIdOrName) throws PortalException
  {
    final String method = "readShowcaseListForDataset() : ";
    log.trace(method + "Start");

    List<Link> showcaseListForDataset = new ArrayList<>();
    if (showcaseDbAvailableCache.isAvailable())
    {
      try
      {
        PageableRequest pageRequest = new PageableRequest(0, 50, "title", "asc");
        SearchFilter searchFilter = SearchFilter.builder()
            .searchColumns(Lists.newArrayList(USED_DATASETS_URL_COLUMN))
            .searchKey(metadataIdOrName)
            .build();
        PagedList<Showcase> showcaseList = showcaseResource.list(pageRequest, searchFilter);
        for (Showcase showcase : showcaseList.getItems())
        {
          if (showcase.isHidden())
          {
            // Do not show private showcases
            continue;
          }
          // Create a Link for each App
          Link link = new Link();
          link.setName(showcase.getTitle());
          link.setUrl(gdNavigation.createLinkForMetadata(
              "gdsearchdetails", showcase.getId().toString(), FRIENDLY_URL_NAME_SHOWROOM_PAGE).toString());
          showcaseListForDataset.add(link);
        }
      }
      catch (Exception ex)
      {
        log.warn("{}Unexpected error while reading linked showcases for the dataset. Details: {}. "
            + "Disable reading showcases for datasets for {} {}.", method, ex.getMessage(),
            showcaseDbAvailableCache.getMaxCacheTimeAmount(),
            showcaseDbAvailableCache.getCacheTemporalUnit());
        showcaseDbAvailableCache.markAsNotAvailable();
      }
    }

    log.trace(method + "End");
    return showcaseListForDataset;
  }

  private String createResourceUrl(MimeResponse response, String metadataName, String resourceId)
  {
    ResourceURL resourceURL = response.createResourceURL();
    resourceURL.setResourceID(resourceId);
    resourceURL.setParameter(QueryParamNames.PARAM_DATASET, metadataName);
    return resourceURL.toString();
  }

  private SelectedMetadata createMetadata(
      String metadataIdOrName, ThemeDisplay themeDisplay,
      de.seitenbau.govdata.odp.registry.model.User ckanuser,
      boolean showBrokenLinkHint)
  {
    final String method = "createMetadata() : ";
    log.trace(method + "Start");

    SelectedMetadata result = new SelectedMetadata();
    // get metadata
    Metadata metadata = getMetadataFromCkan(ckanuser, metadataIdOrName);
    result.setMetadata(metadata);
    // set contact information
    result.setContact(new CurrentMetadataContact(metadata));
    // add current users rating
    // Does NOT EXIST in CKAN API... [insert raging rant here]

    // read existent comments
    List<MetadataComment> comments = null;
    if (metadata != null)
    {
      setSiteInformationFromMetadata(themeDisplay.getLayout(), result);
      try
      {
        // The method gets the comments in order by created ascending.
        comments = MetadataCommentLocalServiceUtil.findBymetadataName(metadata.getName());
        // Reverse the list of comments to have the comments ordered by created descending.
        // bmi-govdata-portal\entities\entities-portlet\src\main\webapp\WEB-INF\service.xml
        if (CollectionUtils.isNotEmpty(comments))
        {
          // Avoid UnsupportedOperationException on unmodifiable list (GOVDATA-2624)
          comments = new ArrayList<>(comments);
          Collections.reverse(comments);
        }
      }
      catch (Exception e)
      {
        log.error(method + "Loading comments of metadata " + metadata.getName(), e);
      }
      result.setComments(wrapComments(comments, themeDisplay));

      // read unavailable resource links
      if (showBrokenLinkHint)
      {
        result.setNotAvailableResourceLinks(
            RedisReportUtil.readUnavailableResourceLinks(metadata.getId(), redisClientAdapter));
      }
      else
      {
        result.setNotAvailableResourceLinks(new HashSet<String>());
      }

      log.debug(method + "SelectedMetadata created!");
    }
    else
    {
      log.info("metadata {} not found!", metadataIdOrName);
    }
    log.trace(method + "End");
    return result;
  }

  private Metadata getMetadataFromCkan(de.seitenbau.govdata.odp.registry.model.User ckanuser,
      String metadataId)
  {
    final String method = "getMetadataFromCkan() : ";
    log.trace(method + "Start");

    Metadata metadata = null;
    try
    {
      metadata = registryClient.getInstance().getMetadata(ckanuser, metadataId);
      if (metadata != null)
      {
        log.debug(method + "metadata found: " + metadata);
        if (!StringUtils.equalsIgnoreCase(metadata.getState(), Metadata.State.ACTIVE.getValue()))
        {
          log.debug(method + "metadata ignored, because metadata is not active!");
          metadata = null;
        }
      }
    }
    catch (OpenDataRegistryException e)
    {
      log.warn(method, e.getMessage());
    }

    log.trace(method + "End");
    return metadata;
  }

  private List<MetadataCommentWrapperExt> wrapComments(List<MetadataComment> comments,
      ThemeDisplay themeDisplay)
  {
    List<MetadataCommentWrapperExt> result = new ArrayList<>();
    if (CollectionUtils.isNotEmpty(comments))
    {
      for (MetadataComment metadataComment : comments)
      {
        MetadataCommentWrapperExt comment = wrapComment(metadataComment, themeDisplay);
        result.add(comment);
      }
    }
    return result;
  }

  private MetadataCommentWrapperExt wrapComment(MetadataComment metadataComment, ThemeDisplay themeDisplay)
  {
    MetadataCommentWrapperExt comment = new MetadataCommentWrapperExt(metadataComment);
    comment.setThemeDisplay(themeDisplay);
    return comment;
  }

  protected void setSiteInformationFromMetadata(Layout layout, ISelectedObject metadata)
  {
    layout.setTitle(metadata.getTitleOnlyText());
    layout.setDescription(StringUtils.abbreviate(metadata.getNotesOnlyText(), 160));
    layout.setKeywords(StringUtils.join(metadata.getTagNameList(), ","));
  }

  /**
   * Tries to find the matching ckanUser for the currently logged in Liferay user (if somebody is
   * logged in...). If no user exists, a new one will be created.
   *
   * @param liferayUser
   * @return
   */
  private CurrentUser findOrCreateCurrentUser(User liferayUser)
  {
    final String method = "findOrCreateCurrentUser() : ";
    log.trace(method + "Start");

    CurrentUser currentUser = new CurrentUser();
    if (liferayUser != null)
    {
      log.debug(method + "Liferay-User found: " + liferayUser.getFullName());
      currentUser.setLiferayUser(liferayUser);
      String screenName = liferayUser.getScreenName();

      if (screenName != null)
      {
        currentUser.setCkanUser(
            new ODRTools().findOrCreateCkanUser(screenName, registryClient.getInstance()));
      }
    }
    else
    {
      log.debug(method + "Liferay-User NOT found! -> guest");
    }
    log.trace(method + "End");
    return currentUser;
  }

  private String getOrganizationName(String id)
  {
    String orgName = "";
    Organization organization = organizationCache.getOrganizationMap().get(id);
    if (Objects.nonNull(organization))
    {
      orgName = organization.getDisplayName();
    }
    return orgName;
  }

  private void addMessageForUser(MappingJackson2JsonView view, ResourceRequest request,
      ResourceResponse response, MessageKey messageKey, String httpStatusCode)
  {
    view.addStaticAttribute(
        QueryParamNames.PARAM_MESSAGE,
        LanguageUtil.get(PortalUtil.getLocale(request), messageKey.toString()));
    response.setProperty(ResourceResponse.HTTP_STATUS_CODE, httpStatusCode);
  }

  private static final class ShowcaseDatabaseAvailabileCache extends BaseCache
  {
    private ShowcaseDatabaseAvailabileCache()
    {
      setCacheTemporalUnit(ChronoUnit.MINUTES);
      setMaxCacheTimeAmount(10);
    }

    private boolean available = true;

    private void markAsNotAvailable()
    {
      this.available = false;
      cacheUpdated();
    }

    private boolean isAvailable()
    {
      if (!this.available && isCacheExpired())
      {
        this.available = true;
      }
      return this.available;
    }
  }
}
