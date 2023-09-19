package de.seitenbau.govdata.search.gui.controller;

import static de.seitenbau.govdata.navigation.GovDataNavigation.FRIENDLY_URL_NAME_SEARCHRESULT_PAGE;
import static de.seitenbau.govdata.navigation.GovDataNavigation.PORTLET_NAME_SEARCHRESULT;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.inject.Inject;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portletmvc4spring.bind.annotation.RenderMapping;

import de.seitenbau.govdata.navigation.GovDataNavigation;
import de.seitenbau.govdata.odp.common.filter.FilterPathUtils;
import de.seitenbau.govdata.odp.common.filter.SearchConsts;
import de.seitenbau.govdata.search.common.cache.MastodonCache;
import de.seitenbau.govdata.search.common.cache.NumberCache;
import de.seitenbau.govdata.search.common.cache.TweetCache;
import de.seitenbau.govdata.search.common.cache.util.PostContainer;
import de.seitenbau.govdata.search.common.cache.util.SocialMediaPlatformsConsts;
import de.seitenbau.govdata.search.gui.model.NumberViewModel;
import de.seitenbau.govdata.search.util.NumberParser;

@RequestMapping("VIEW")
public class SearchNumbersController
{

  @Inject
  private NumberCache numberCache;

  @Inject
  private GovDataNavigation govDataNavigation;

  @Inject
  private TweetCache tweetCache;

  @Inject
  private MastodonCache mastodonCache;

  private NumberParser numberParser = new NumberParser();

  private static Map<String, String> typeModelAttributeMap = new HashMap<>();

  static
  {
    typeModelAttributeMap.put(SearchConsts.TYPE_DATASET, "numberDataset");
    typeModelAttributeMap.put(SearchConsts.TYPE_SHOWCASE, "numberShowcase");
    typeModelAttributeMap.put(SearchConsts.TYPE_BLOG, "numberBlog");
  }

  /**
   * Displaying counts of various types of data in GovData.
   * 
   * @param request
   * @param response
   * @param model
   * @return
   * @throws SystemException
   * @throws PortalException
   */
  @RenderMapping
  public String showSearchNumberField(
      RenderRequest request,
      RenderResponse response,
      Model model) throws SystemException, PortalException
  {
    ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);

    // get portlet config
    PortletPreferences portletPreferences = request.getPreferences();

    // manage social media posts
    boolean showPlatform = false;
    if (GetterUtil.getBoolean(portletPreferences.getValue("showTwitter", StringPool.FALSE)))
    {
      PostContainer twitterPost = tweetCache.getTweetData();
      if (twitterPost != null)
      {
        model.addAttribute(SocialMediaPlatformsConsts.TWITTER, twitterPost);
        showPlatform = true;
      }
    }
    if (GetterUtil.getBoolean(portletPreferences.getValue("showMastodon", StringPool.FALSE)))
    {
      PostContainer mastodonPost = mastodonCache.getPostData();
      if (mastodonPost != null)
      {
        model.addAttribute(SocialMediaPlatformsConsts.MASTODON, mastodonPost);
        showPlatform = true;
      }
    }

    numberParser.setData(numberCache.getRawNumberData());
    List<NumberViewModel> numbers = numberParser.getValues();

    for (NumberViewModel nvm : numbers)
    {
      String filterParam =
          FilterPathUtils.serializeFilter(SearchConsts.FILTER_KEY_TYPE, nvm.getName());
      PortletURL redirectUrl = govDataNavigation
          .createLinkForSearchResults(FRIENDLY_URL_NAME_SEARCHRESULT_PAGE, PORTLET_NAME_SEARCHRESULT, "",
              filterParam, "", "");

      nvm.setActionURL(redirectUrl.toString());
      String modelAttributeName = typeModelAttributeMap.get(nvm.getName());
      if (Objects.nonNull(modelAttributeName))
      {
        model.addAttribute(modelAttributeName, nvm);
      }
    }
    model.addAttribute(AbstractBaseController.MODEL_KEY_THEME_DISPLAY, themeDisplay);
    model.addAttribute("showPlatform", showPlatform);
    return "searchnumbers";
  }

}
