package de.seitenbau.govdata.search.gui.controller;

import java.util.Calendar;
import java.util.Objects;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.portlet.MimeResponse.Copy;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portletmvc4spring.bind.annotation.RenderMapping;

import de.seitenbau.govdata.constants.QueryParamNames;
import de.seitenbau.govdata.search.gui.model.SearchMapViewModel;
import de.seitenbau.govdata.search.searchmap.cache.SearchMapCache;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("VIEW")
public class SearchMapController extends AbstractBaseController
{

  @Value("${elasticsearch.search.paths.typefiltered}")
  private String[] typeFilteredPaths;
  
  @Value("${mapsearch.useOsm}")
  private boolean useOsm = false;
  
  @Value("${mapsearch.tileUrl:}")
  private String tileUrl;
  
  @Value("${mapsearch.geocodingUrl}")
  private String geocodingUrl;
  
  @Value("${mapsearch.credits:}")
  private String credits;

  @Value("${mapsearch.layers:}")
  private String layers;

  @Inject
  private SearchMapCache searchMapCache;

  /**
   * Initializes required components and sets configuration parameters.
   */
  @PostConstruct
  public void init()
  {
    if (!useOsm && StringUtils.isBlank(tileUrl))
    {
      log.warn("Configuration parameter 'tileUrl' is required when not using OpenStreetMap"
          + ", but 'tileUrl' is not set!");
    }
  }
  
  /**
   * Shows the map search.
   * 
   * @param request the rquest
   * @param response the response
   * @param model the model with all required information used in the template
   * @return the template name
   */
  @RenderMapping
  public String showMap(
      RenderRequest request,
      RenderResponse response,
      Model model)
  {
    PortletURL actionUrl = response.createActionURL(Copy.NONE);
    ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);

    String filterRequestParam = request.getParameter(QueryParamNames.PARAM_FILTER);
    String sort = request.getParameter(QueryParamNames.PARAM_SORT);
    String q = request.getParameter(QueryParamNames.PARAM_PHRASE);
    String boundingbox = request.getParameter(QueryParamNames.PARAM_BOUNDINGBOX);
    String dateFrom = request.getParameter(QueryParamNames.PARAM_START);
    String dateUntil = request.getParameter(QueryParamNames.PARAM_END);
    
    // credits: Resolve placeholder "$year".
    String creditsReplaced =
        StringUtils.replace(credits, "$year", Integer.toString(Calendar.getInstance().get(Calendar.YEAR)));

    // geocodingUrl: Resolve placeholder $sessionId.
    String placeholder = "$sessionId";
    String geocodingUrlReplaced = geocodingUrl;
    if (geocodingUrl.contains(placeholder))
    {
      String sessionId = searchMapCache.getSessionId();
      geocodingUrlReplaced = null;
      if (Objects.nonNull(sessionId))
      {
        geocodingUrlReplaced = StringUtils.replace(geocodingUrl, placeholder, sessionId);
      }
    }

    SearchMapViewModel viewModel = SearchMapViewModel.builder()
        .f(filterRequestParam)
        .s(sort)
        .q(q)
        .boundingbox(boundingbox)
        .start(dateFrom)
        .end(dateUntil)
        .actionUrl(actionUrl.toString())
        .useOsm(useOsm)
        .tileUrl(tileUrl)
        .geocodingUrl(geocodingUrlReplaced)
        .credits(creditsReplaced)
        .layers(layers)
        .build();
    
    model.addAttribute("searchMap", viewModel);
    model.addAttribute(MODEL_KEY_THEME_DISPLAY, themeDisplay);
    return "searchmap";
  }
}
