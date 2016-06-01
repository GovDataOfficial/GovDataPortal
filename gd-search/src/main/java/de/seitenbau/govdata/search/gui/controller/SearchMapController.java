package de.seitenbau.govdata.search.gui.controller;

import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;


import de.seitenbau.govdata.constants.QueryParamNames;
import de.seitenbau.govdata.search.gui.model.SearchMapViewModel;

@RequestMapping("VIEW")
public class SearchMapController extends AbstractBaseController
{

  @Value("${elasticsearch.search.paths.typefiltered}")
  private String[] typeFilteredPaths;
  
  @Value("${mapsearch.useOsm}")
  private boolean useOsm = false;
  
  @Value("${mapsearch.tileUrl}")
  private String tileUrl;
  
  @Value("${mapsearch.geocodingUrl}")
  private String geocodingUrl;
  
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
        .build();
    
    model.addAttribute("searchMap", viewModel);
    
    return "searchmap";
  }
}
