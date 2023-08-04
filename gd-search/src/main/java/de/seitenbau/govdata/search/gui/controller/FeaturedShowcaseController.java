package de.seitenbau.govdata.search.gui.controller;

import static de.seitenbau.govdata.navigation.GovDataNavigation.FRIENDLY_URL_NAME_SHOWROOM_PAGE;
import static de.seitenbau.govdata.navigation.GovDataNavigation.PORTLET_NAME_SEARCHRESULT;

import java.util.Locale;
import java.util.Objects;

import javax.inject.Inject;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.context.MessageSource;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portletmvc4spring.bind.annotation.RenderMapping;

import de.seitenbau.govdata.dataset.details.beans.SelectedShowcase;
import de.seitenbau.govdata.navigation.GovDataNavigation;
import de.seitenbau.govdata.odp.common.filter.SearchConsts;
import de.seitenbau.govdata.search.showcase.cache.FeaturedShowcaseCache;
import de.seitenbau.govdata.search.util.RequestUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("VIEW")
public class FeaturedShowcaseController extends AbstractBaseController
{
  /**
   * Controller for displaying selected featured showcases.
   * @author sgebhart
   */
  private static final String MODEL_SHOWCASE_KEY = "showcase";

  private static final String MODEL_LINK_SHOWCASE_KEY = "linkToShowcase";

  private static final String MODEL_LINK_SHOWROOM_KEY = "linkToShowroom";

  private static final String MODEL_SHOW_SHOWROOM_KEY = "showShowroomButton";

  private static final String MODEL_ERROR_MSG_KEY = "errormessage";

  private static final String[] BLOCKED_FRIENDLY_URLS = {SearchConsts.FRIENDLY_URL_MAPPING_DETAILS};

  @Inject
  private MessageSource messageSource;

  @Inject
  private GovDataNavigation gdNavigation;

  @Inject
  private FeaturedShowcaseCache featuredShowcaseCache;

  /**
   * Display a selected featured showcase.
   * @param request
   * @param response
   * @param model
   * @return
   * @throws SystemException
   * @throws PortalException
   */
  @RenderMapping
  public String showFeaturedShowcase(
      RenderRequest request,
      RenderResponse response,
      Model model) throws SystemException, PortalException
  {
    final String method = "showFeaturedShowcase() : ";

    // do not render the portlet on details view
    if (RequestUtil.isBlockedUrlMapping(request, BLOCKED_FRIENDLY_URLS))
    {
      log.debug(method + "End, do nothing.");
      return null;
    }

    ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
    PortletPreferences portletPreferences = request.getPreferences();

    // read values from portlet configuration
    String selectedFeaturedShowcaseId = GetterUtil.getString(portletPreferences.getValue("showcaseId", ""));
    boolean showShowroomButton =
        GetterUtil.getBoolean(portletPreferences.getValue("showShowroomButton", StringPool.TRUE));

    SelectedShowcase selectedShowcase;
    try
    {
      long appId = Long.parseLong(selectedFeaturedShowcaseId);

      // get showcase from cache
      selectedShowcase =
          featuredShowcaseCache.getShowcaseForKey(themeDisplay.getPortletDisplay().getResourcePK(), appId);
      Objects.requireNonNull(selectedShowcase);

      // check if showcase is private
      if (selectedShowcase.getShowcase().isPrivate())
      {
        model.addAttribute(MODEL_ERROR_MSG_KEY, messageSource.getMessage(
            "od.featured.showcase.error.is.private", new Object[0], Locale.getDefault()));
        return "featuredshowcaseerror";
      }
    }
    catch (Exception ex)
    {
      log.warn(method + "Problem with selected showcase-ID " + selectedFeaturedShowcaseId + ": "
          + ex.getMessage());
      model.addAttribute(MODEL_ERROR_MSG_KEY, messageSource.getMessage(
          "od.featured.showcase.error.not.found", new Object[0], Locale.getDefault()));
      return "featuredshowcaseerror";
    }

    // create link to showcase
    String linkToShowcase = gdNavigation
        .createLinkForMetadata("gdsearchdetails", selectedFeaturedShowcaseId, FRIENDLY_URL_NAME_SHOWROOM_PAGE)
        .toString();

    // create link to showroom
    PortletURL redirectUrl = gdNavigation.createLinkForSearchResults(FRIENDLY_URL_NAME_SHOWROOM_PAGE,
        PORTLET_NAME_SEARCHRESULT, "");

    model.addAttribute(MODEL_KEY_THEME_DISPLAY, themeDisplay);
    model.addAttribute(MODEL_SHOWCASE_KEY, selectedShowcase);
    model.addAttribute(MODEL_LINK_SHOWCASE_KEY, linkToShowcase);
    model.addAttribute(MODEL_LINK_SHOWROOM_KEY, redirectUrl.toString());
    model.addAttribute(MODEL_SHOW_SHOWROOM_KEY, showShowroomButton);

    return "featuredshowcase";
  }

}
