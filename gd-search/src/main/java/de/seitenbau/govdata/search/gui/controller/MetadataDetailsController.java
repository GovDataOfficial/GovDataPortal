package de.seitenbau.govdata.search.gui.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import javax.inject.Inject;
import javax.portlet.MimeResponse;
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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import com.liferay.counter.kernel.service.CounterLocalServiceUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.service.BaseServiceImpl;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.WebKeys;

import de.fhg.fokus.odp.entities.model.MetadataComment;
import de.fhg.fokus.odp.entities.service.MetadataCommentLocalServiceUtil;
import de.seitenbau.govdata.clean.StringCleaner;
import de.seitenbau.govdata.constants.DetailsRequestParamNames;
import de.seitenbau.govdata.constants.QueryParamNames;
import de.seitenbau.govdata.dataset.details.beans.CurrentMetadataContact;
import de.seitenbau.govdata.dataset.details.beans.CurrentUser;
import de.seitenbau.govdata.dataset.details.beans.MetadataCommentWrapper;
import de.seitenbau.govdata.dataset.details.beans.SelectedMetadata;
import de.seitenbau.govdata.messages.MessageKey;
import de.seitenbau.govdata.navigation.GovDataNavigation;
import de.seitenbau.govdata.navigation.PortletUtil;
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
import lombok.extern.slf4j.Slf4j;

/**
 * Controller for the metadata details page.
 *
 * @author rnoerenberg
 */
@Slf4j
@RequestMapping("VIEW")
public class MetadataDetailsController extends BaseServiceImpl
{
  private static final String MODEL_KEY_THEME_DISPLAY = "themeDisplay";

  private static final String MODEL_KEY_CURRENT_USER = "currentUser";

  private static final String MODEL_KEY_COMMENT = "comment";

  private static final String MODEL_KEY_SELECTED_METADATA = "selectedMetadata";

  private static final String HTTP_STATUS_INTERNAL_SERVER_ERROR = "500";

  private static final String VIEW_MODEL_NAME = "details";

  private static final String SUBMIT_RATING_RESOURCE_URL_ID = "submitRating";

  private static final String ADD_COMMENT_RESOURCE_URL_ID = "addComment";

  private static final String EDIT_COMMENT_RESOURCE_URL_ID = "editComment";

  private static final String DELETE_COMMENT_RESOURCE_URL_ID = "deleteComment";

  private static final String HTTP_STATUS_FORBIDDEN = "403";

  private static final String HTTP_STATUS_BAD_REQUEST = "400";

  private static final String HTTP_STATUS_NOT_FOUND = "404";

  private static final String MODEL_KEY_COMMENTS_ENABLED = "commentsEnabled";

  private static final String MODEL_KEY_GUEST_COMMENTS_ENABLED = "guestCommentsEnabled";

  private static final String MODEL_KEY_RATINGS_ENABLED = "ratingsEnabled";

  private static final String MODEL_KEY_CANEDIT = "userCanEditDataset";

  private static final String MODEL_KEY_EDITDATASETURL = "editDatasetUrl";

  private static final String MODEL_KEY_SHOW_BROKEN_LINKS_HINT = "showBrokenLinksHint";

  private static final String MODEL_KEY_METADATA_JSON_LD = "metadataJsonLd";

  @Inject
  private RegistryClient registryClient;

  @Inject
  private RedisClientAdapter redisClientAdapter;

  @Inject
  private NotificationMailSender notificationMailSender;

  @Inject
  private GovDataNavigation gdNavigation;

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
    if (!isResponsible(request))
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

    PortletURL actionUrl = response.createActionURL();
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
          "gdsearchdetails", metadataIdOrName, "daten").toString();
      final String catalogUrl = gdNavigation.createLinkForSearchResults("daten",
          "gdsearchresult", "").toString();
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
    model.addAttribute(MODEL_KEY_THEME_DISPLAY, themeDisplay);
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
        User liferayUser = getGuestOrUser(); // request User from BaseServiceImpl
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
            // add message for user
            view.addStaticAttribute(
                QueryParamNames.PARAM_MESSAGE,
                LanguageUtil.get(PortalUtil.getLocale(request), MessageKey.NO_PERMISSION));
            response.setProperty(ResourceResponse.HTTP_STATUS_CODE, HTTP_STATUS_FORBIDDEN);
          }
        }
        else
        {
          log.debug(method + "Kein Kommentar zur ID {} gefunden.", commentId);
          // add message for user
          view.addStaticAttribute(
              QueryParamNames.PARAM_MESSAGE,
              LanguageUtil.get(PortalUtil.getLocale(request), MessageKey.COMMENT_NOT_EXISTENT));
          response.setProperty(ResourceResponse.HTTP_STATUS_CODE, HTTP_STATUS_BAD_REQUEST);
        }
      }
      catch (Exception e)
      {
        log.warn(method + e.getMessage());
        view.addStaticAttribute(
            QueryParamNames.PARAM_MESSAGE,
            LanguageUtil.get(PortalUtil.getLocale(request), MessageKey.ERROR_OCCURRED));
        response.setProperty(ResourceResponse.HTTP_STATUS_CODE, HTTP_STATUS_INTERNAL_SERVER_ERROR);
      }
    }
    else
    {
      log.debug(method + "End, tue nichts. Kein Kommentar im Request gefunden.");
      // add message for user
      view.addStaticAttribute(
          QueryParamNames.PARAM_MESSAGE,
          LanguageUtil.get(PortalUtil.getLocale(request), MessageKey.ERROR_OCCURRED));
      response.setProperty(ResourceResponse.HTTP_STATUS_CODE, HTTP_STATUS_BAD_REQUEST);
    }

    log.trace(method + "End");
    return view;
  }

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
            // add message for user
            view.addStaticAttribute(
                QueryParamNames.PARAM_MESSAGE,
                LanguageUtil.get(PortalUtil.getLocale(request), MessageKey.COMMENT_DELETED_SUCCESS));
          }
          else
          {
            log.debug(method + "Der Benutzer hat kein Recht zum Löschen des Kommentars mit der ID {}",
                commentId);
            // add message for user
            view.addStaticAttribute(
                QueryParamNames.PARAM_MESSAGE,
                LanguageUtil.get(PortalUtil.getLocale(request), MessageKey.NO_PERMISSION));
            response.setProperty(ResourceResponse.HTTP_STATUS_CODE, HTTP_STATUS_FORBIDDEN);
          }
        }
        else
        {
          log.debug(method + "Kein Kommentar zur ID {} gefunden.", commentId);
          // add message for user
          view.addStaticAttribute(
              QueryParamNames.PARAM_MESSAGE,
              LanguageUtil.get(PortalUtil.getLocale(request), MessageKey.COMMENT_NOT_EXISTENT));
          response.setProperty(ResourceResponse.HTTP_STATUS_CODE, HTTP_STATUS_NOT_FOUND);
        }
      }
      catch (Exception e)
      {
        log.warn(method + e.getMessage());
        // add message for user
        view.addStaticAttribute(
            QueryParamNames.PARAM_MESSAGE,
            LanguageUtil.get(PortalUtil.getLocale(request), MessageKey.ERROR_OCCURRED));
        response.setProperty(ResourceResponse.HTTP_STATUS_CODE, HTTP_STATUS_INTERNAL_SERVER_ERROR);
      }
    }
    else
    {
      log.debug(method + "End, tue nichts. Keine Kommentar-ID im Request gefunden.");
      // add message for user
      view.addStaticAttribute(
          QueryParamNames.PARAM_MESSAGE,
          LanguageUtil.get(PortalUtil.getLocale(request), MessageKey.ERROR_OCCURRED));
      response.setProperty(ResourceResponse.HTTP_STATUS_CODE, HTTP_STATUS_BAD_REQUEST);
      return null;
    }

    log.trace(method + "End");
    return view;
  }

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
      // accessible through the only available acion api
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

  private List<MetadataCommentWrapper> wrapComments(List<MetadataComment> comments, ThemeDisplay themeDisplay)
  {
    List<MetadataCommentWrapper> result = new ArrayList<>();
    if (CollectionUtils.isNotEmpty(comments))
    {
      for (MetadataComment metadataComment : comments)
      {
        MetadataCommentWrapper comment = wrapComment(metadataComment, themeDisplay);
        result.add(comment);
      }
    }
    return result;
  }

  private MetadataCommentWrapper wrapComment(MetadataComment metadataComment, ThemeDisplay themeDisplay)
  {
    MetadataCommentWrapper comment = new MetadataCommentWrapper(metadataComment);
    comment.setThemeDisplay(themeDisplay);
    return comment;
  }

  private void setSiteInformationFromMetadata(Layout layout, SelectedMetadata metadata)
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

  private boolean isResponsible(RenderRequest request)
  {
    final String method = "isResponsible() : ";
    log.trace(method + "Start");

    String friendlyUrlMapping = PortletUtil.extractFriendlyUrlMappingFromRequestUrl(request);
    log.debug(method + "friendlyUrlMapping: {}", friendlyUrlMapping);
    if (!SearchConsts.FRIENDLY_URL_MAPPING_DETAILS.equals(friendlyUrlMapping))
    {
      return false;
    }

    log.trace(method + "End");
    return true;
  }
}
