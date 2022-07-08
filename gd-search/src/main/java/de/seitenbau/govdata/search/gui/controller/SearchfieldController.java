package de.seitenbau.govdata.search.gui.controller;

import java.util.List;

import javax.inject.Inject;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.ResourceURL;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.KeyValuePair;
import com.liferay.portal.kernel.util.WebKeys;

import de.seitenbau.govdata.constants.QueryParamNames;
import de.seitenbau.govdata.navigation.GovDataNavigation;
import de.seitenbau.govdata.search.adapter.SearchService;
import de.seitenbau.govdata.search.common.searchresult.ParameterProcessing;
import de.seitenbau.govdata.search.common.searchresult.PreparedParameters;
import de.seitenbau.govdata.search.common.searchresult.UrlBuilder;
import de.seitenbau.govdata.search.gui.model.SearchFieldViewModel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("VIEW")
public class SearchfieldController extends AbstractBaseController
{
  private static final String SAYT_COMPLETION = "saytCompletion"; // search-as-you-type

  @Inject
  private GovDataNavigation navigationHelper;

  @Inject
  private SearchService indexService;

  @Value("${elasticsearch.search.paths.typefiltered}")
  private String[] typeFilteredPaths;

  @RenderMapping
  public String showSearchInputField(
      RenderRequest request,
      RenderResponse response,
      Model model) throws SystemException, PortalException
  {
    // whether to show the big header or a reduced variant. User setting.
    PortletPreferences portletPreferences = request.getPreferences();
    boolean showBigHeader =
        GetterUtil.getBoolean(portletPreferences.getValue("showBigHeader", StringPool.FALSE));
    boolean showSearch =
        GetterUtil.getBoolean(portletPreferences.getValue("showSearch", StringPool.TRUE));
    String backgroundImage = GetterUtil.getString(portletPreferences.getValue("backgroundImage", "general"));

    ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);

    // construct the full background-image url
    String backgroundFile = (backgroundImage.equals("general") ? "" : "_mini_" + backgroundImage);
    String backgroundImageFull =
        themeDisplay.getPathThemeImages() + "/datavisuals/connectionmap" + backgroundFile + ".jpg";
    // get currentpage for type filter
    String currentPage = themeDisplay.getLayout().getFriendlyURL();

    PreparedParameters preparm =
        ParameterProcessing.prepareParameters(request.getParameterMap(), currentPage);
    UrlBuilder urlbuilder = new UrlBuilder(preparm);

    // List of hidden Fields to generate to pass through other fields
    List<KeyValuePair> passthroughParams = urlbuilder.parametersAsList(new String[] {
        QueryParamNames.PARAM_PHRASE
    });

    String searchExtUrlString = null;
    try
    {
      searchExtUrlString =
          urlbuilder.createFulloptionUrl(navigationHelper, "erweitertesuche", "gdsearchext").toString();
    }
    catch (IllegalArgumentException e)
    {
      log.warn("Error on creating other search view urls! Reason: {}", e.getMessage());
    }

    // prepare url for saytCompletion
    ResourceURL saytCompletionUrl = response.createResourceURL();
    saytCompletionUrl.setResourceID(SAYT_COMPLETION);

    PortletURL actionUrl = response.createActionURL();
    SearchFieldViewModel viewModel = SearchFieldViewModel.builder()
        .actionUrl(actionUrl.toString())
        .showBigHeader(showBigHeader)
        .showSearch(showSearch)
        .backgroundImage(backgroundImageFull)
        .q(preparm.getQuery().getQueryString())
        .passthroughParams(passthroughParams)
        .extendedSearchUrl(searchExtUrlString)
        .saytCompletionUrl(saytCompletionUrl.toString())
        .build();

    model.addAttribute("searchField", viewModel);

    log.debug("showSearchInputField finished");
    return "searchfield";
  }

  /**
   * AJAX-Endpoint for fetching search completions from elastic search.
   * @param request
   * @param response
   * @param model
   * @return JSON-Formatted List of completed search terms.
   * @throws PortalException
   * @throws SystemException
   */
  @ResourceMapping(value = SAYT_COMPLETION)
  public View saytCompletion(
      ResourceRequest request,
      ResourceResponse response,
      Model model) throws PortalException, SystemException
  {
    String phrase = request.getParameter(QueryParamNames.PARAM_PHRASE);
    String prefix = "";
    String suffix = phrase;

    // separate last word (do the completion on that)
    if (phrase.contains(" "))
    {
      int pos = phrase.lastIndexOf(' ');
      prefix = phrase.substring(0, pos);
      suffix = phrase.substring(pos + 1);
    }

    // fetch completion
    List<String> findSearchAsYouTypeSuggestions = indexService.findSearchAsYouTypeSuggestions(suffix);

    // add prefix to all results
    for (int i = 0; i < findSearchAsYouTypeSuggestions.size(); i++)
    {
      findSearchAsYouTypeSuggestions.set(i, prefix + " " + findSearchAsYouTypeSuggestions.get(i));
    }

    // render them into json object
    MappingJackson2JsonView view = new MappingJackson2JsonView();
    view.addStaticAttribute(QueryParamNames.PARAM_COMPLETION_SUGGESTIONS, findSearchAsYouTypeSuggestions);

    return view;
  }
}
