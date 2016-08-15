package de.seitenbau.govdata.search.gui.controller;

import java.util.Calendar;

import javax.annotation.PostConstruct;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import de.seitenbau.govdata.constants.QueryParamNames;
import de.seitenbau.govdata.search.gui.model.SearchMapViewModel;

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

  @PostConstruct
  public void init() throws Exception
  {
    if (!useOsm && StringUtils.isBlank(tileUrl))
    {
      log.warn("Configuration parameter 'tileUrl' is required when not using OpenStreetMap"
          + ", but 'tileUrl' is not set!");
    }
  }
  
  @RenderMapping
  public String showMap(
      RenderRequest request,
      RenderResponse response,
      Model model)
  {
    PortletURL actionUrl = response.createActionURL();
    
    String filterRequestParam = request.getParameter(QueryParamNames.PARAM_FILTER);
    String sort = request.getParameter(QueryParamNames.PARAM_SORT);
    String q = request.getParameter(QueryParamNames.PARAM_PHRASE);
    String boundingbox = request.getParameter(QueryParamNames.PARAM_BOUNDINGBOX);
    String dateFrom = request.getParameter(QueryParamNames.PARAM_FROM);
    String dateUntil = request.getParameter(QueryParamNames.PARAM_UNTIL);
    
    // credits: Resolve placeholder "$year".
    String creditsReplaced =
        StringUtils.replace(credits, "$year", Integer.toString(Calendar.getInstance().get(Calendar.YEAR)));
    
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
        .geocodingUrl(geocodingUrl)
        .credits(creditsReplaced)
        .build();
    
    model.addAttribute("searchMap", viewModel);
    
    return "searchmap";
  }
}
