package de.seitenbau.govdata.search.gui.controller;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.View;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portletmvc4spring.bind.annotation.RenderMapping;
import com.liferay.portletmvc4spring.bind.annotation.ResourceMapping;

import de.seitenbau.govdata.navigation.GovDataNavigation;
import de.seitenbau.govdata.odp.registry.model.Organization;
import de.seitenbau.govdata.odp.registry.model.User;
import de.seitenbau.govdata.odp.registry.model.exception.OpenDataRegistryException;
import de.seitenbau.govdata.odr.ODRTools;
import de.seitenbau.govdata.odr.RegistryClient;
import de.seitenbau.govdata.search.adapter.SearchService;
import de.seitenbau.govdata.search.common.AtomFeedView;
import de.seitenbau.govdata.search.common.SearchFilterBundle;
import de.seitenbau.govdata.search.common.searchresult.ParameterProcessing;
import de.seitenbau.govdata.search.common.searchresult.PreparedParameters;
import de.seitenbau.govdata.search.common.searchresult.UrlBuilder;
import de.seitenbau.govdata.search.gui.mapper.SearchResultsViewMapper;
import de.seitenbau.govdata.search.gui.model.HitViewModel;
import de.seitenbau.govdata.search.index.model.SearchResultContainer;
import de.seitenbau.govdata.search.sort.Sort;
import de.seitenbau.govdata.search.sort.SortType;

@RequestMapping("VIEW")
public class AtomFeedController
{
  private static final String ATOM_FEED = "atomfeed";

  private static final Sort FEED_SORTING = new Sort(SortType.LASTMODIFICATION, false);

  @Inject
  private SearchService indexService;
  
  @Inject
  private SearchResultsViewMapper searchResultsMapper;
  
  @Inject
  private GovDataNavigation navigationHelper;
  
  @Inject
  private RegistryClient registryClient;

  @Inject
  private ParameterProcessing parameterProcessing;

  /**
   * Serve the search as atom-feed
   * @param request
   * @param response
   * @param model
   * @return
   * @throws SystemException 
   * @throws PortalException 
   */
  @ResourceMapping(value = ATOM_FEED)
  public View atomfeed(
      ResourceRequest request,
      ResourceResponse response,
      Model model) throws SystemException, PortalException
  {
    
    // preprocessing for parameters
    PreparedParameters preparm = parameterProcessing.prepareParameters(request.getParameterMap(), null);

    // Get Organizations for this user from CKAN - or fail / get an empty list. 
    List<Organization> editorOrganizationList = new ArrayList<Organization>();
    try
    {
      User ckanUser = new ODRTools().getCkanuserFromRequest(request, registryClient.getInstance());
      if (ckanUser != null)
      {
        editorOrganizationList =
            registryClient.getInstance().getOrganizationsForUser(ckanUser, "create_dataset");
      }
    }
    catch (PortalException | OpenDataRegistryException e1)
    {
      // we seem to have a problem, so no organizations this time.
    }
    
    // execute search
    SearchFilterBundle searchFilterBundle = parameterProcessing.createFilterBundle(
        preparm,
        new ODRTools().extractIDsFromOrganizations(editorOrganizationList));
    
    SearchResultContainer result = indexService.search(
        preparm.getQuery(),
        30,
        searchFilterBundle,
        FEED_SORTING);
    
    ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
    
    List<HitViewModel> hitViewModelList =
        searchResultsMapper.mapHitDtoListToHitsViewModelList(result.getHits(), themeDisplay, null, true);
    
    // build url to point at the feed itself
    UrlBuilder urlbuilder = new UrlBuilder(preparm);
    // Exceptions can't happen when this is not included in a page
    PortletURL selfurl = urlbuilder.createAtomFeedUrl(navigationHelper);
    
    return new AtomFeedView(hitViewModelList, selfurl);
  }
  
  /**
   * Display the search results.
   * @param request
   * @param response
   * @param model
   * @return
   */
  @RenderMapping
  public String showSearchResults(
      RenderRequest request,
      RenderResponse response,
      Model model)
  {
    // We just don't want to be rendered at all...
    return null;
  }
}
