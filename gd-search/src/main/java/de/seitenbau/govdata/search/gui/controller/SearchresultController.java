package de.seitenbau.govdata.search.gui.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;
import javax.portlet.PortletSession;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.ResourceURL;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.util.KeyValuePair;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.theme.ThemeDisplay;

import de.seitenbau.govdata.cache.LicenceCache;
import de.seitenbau.govdata.cache.OrganizationCache;
import de.seitenbau.govdata.constants.QueryParamNames;
import de.seitenbau.govdata.dcatde.ViewUtil;
import de.seitenbau.govdata.navigation.GovDataNavigation;
import de.seitenbau.govdata.navigation.PortletUtil;
import de.seitenbau.govdata.odp.common.filter.FilterPathUtils;
import de.seitenbau.govdata.odp.common.filter.SearchConsts;
import de.seitenbau.govdata.odp.registry.model.Licence;
import de.seitenbau.govdata.odp.registry.model.Organization;
import de.seitenbau.govdata.odp.registry.model.User;
import de.seitenbau.govdata.odp.registry.model.exception.OpenDataRegistryException;
import de.seitenbau.govdata.odr.ODRTools;
import de.seitenbau.govdata.odr.RegistryClient;
import de.seitenbau.govdata.search.adapter.SearchService;
import de.seitenbau.govdata.search.common.SearchFilterBundle;
import de.seitenbau.govdata.search.common.searchresult.ParameterProcessing;
import de.seitenbau.govdata.search.common.searchresult.PreparedParameters;
import de.seitenbau.govdata.search.common.searchresult.UrlBuilder;
import de.seitenbau.govdata.search.gui.mapper.SearchResultsViewMapper;
import de.seitenbau.govdata.search.gui.model.FilterViewListModel;
import de.seitenbau.govdata.search.gui.model.FilterViewModel;
import de.seitenbau.govdata.search.gui.model.HitViewModel;
import de.seitenbau.govdata.search.gui.model.SearchResultsViewModel;
import de.seitenbau.govdata.search.gui.model.SortViewModel;
import de.seitenbau.govdata.search.gui.model.SuggestionModel;
import de.seitenbau.govdata.search.index.model.FacetDto;
import de.seitenbau.govdata.search.index.model.FilterListDto;
import de.seitenbau.govdata.search.index.model.SearchResultContainer;
import de.seitenbau.govdata.search.index.model.SuggestionOption;
import de.seitenbau.govdata.search.sort.Sort;
import de.seitenbau.govdata.search.sort.SortType;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("VIEW")
public class SearchresultController extends AbstractBaseController
{
  /**
   * Helper Class to transport various processed filter objects needed for the view
   *
   * @author tscheffler
   */
  @Data class ProcessedFilterBundle
  {
    private final List<FilterViewModel> FilterViewModelList;

    private final Boolean hasActiveFilters;
  }

  private static final String MODEL_KEY_SEARCH_RESULT = "searchResult";

  private static final String MODEL_KEY_SCROLL_RESOURCE_URL = "scrollResourceURL";

  public static final String NEXT_HITS_RESOURCE_URL_ID = "nextHits";

  @Inject
  private LicenceCache licenceCache;

  @Inject
  private OrganizationCache organizationCache;

  @Inject
  private GovDataNavigation navigationHelper;

  @Inject
  private SearchService indexService;

  @Inject
  private SearchResultsViewMapper searchResultsMapper;

  @Inject
  private RegistryClient registryClient;

  @RenderMapping
  public String showSearchResults(
      RenderRequest request,
      RenderResponse response,
      Model model) throws SystemException
  {
    final String method = "showSearchResults() : ";
    log.trace(method + "Start");

    // Nur Trefferliste rendern, wenn das FriendlyUrlMapping für das Searchresult-Portlet in der URL
    // steht.
    if (!isResponsible(request))
    {
      log.debug(method + "End, do nothing.");
      return null;
    }

    // get current page for type filter
    ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
    String currentPage = themeDisplay.getLayout().getFriendlyURL();

    // preprocessing for parameters
    PreparedParameters preparm =
        ParameterProcessing.prepareParameters(request.getParameterMap(), currentPage);

    // Get Organizations for this user from CKAN - or fail / get an empty list.
    List<Organization> editorOrganizationList = new ArrayList<>();
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
    SearchFilterBundle searchFilterBundle = ParameterProcessing.createFilterBundle(
        preparm,
        new ODRTools().extractIDsFromOrganizations(editorOrganizationList));

    SearchResultContainer result = indexService.search(
        preparm.getQuery(),
        null, // default number of results
        searchFilterBundle,
        preparm.getSelectedSorting());

    // record Search Phrase
    recordSearchPhrase(preparm.getQuery(), request.getPortletSession());

    List<HitViewModel> hitViewModelList =
        searchResultsMapper.mapHitDtoListToHitsViewModelList(
            result.getHits(), themeDisplay, currentPage, false);

    // UrlBuilder is used to build parameterized URLs for the GUI
    UrlBuilder urlbuilder = new UrlBuilder(preparm);

    String clearFilterUrl = urlbuilder.createUrl(response, new String[] {
        QueryParamNames.PARAM_BOUNDINGBOX,
        QueryParamNames.PARAM_FILTER,
        QueryParamNames.PARAM_START,
        QueryParamNames.PARAM_END
    }).toString();

    // prepare facet filters for GUI
    Map<String, FilterViewListModel> filterViewModelMap = mapFilterToViewModel(result.getFilterMap());

    // if we are editor, insert "show only our own datasets" filter option
    String newDatasetUrlString = null;
    if (CollectionUtils.isNotEmpty(editorOrganizationList))
    {
      FilterViewListModel editorFacet = new FilterViewListModel();
      editorFacet.setSingletonFiltergroup(true);
      editorFacet.add(new FilterViewModel(-1L, QueryParamNames.PARAM_SHOW_ONLY_EDITOR_METADATA));
      filterViewModelMap.put(QueryParamNames.PARAM_SHOW_ONLY_EDITOR_METADATA, editorFacet);

      // at this point we know: This user can edit some datasets. So this user can create new
      // datasets.
      newDatasetUrlString =
          urlbuilder.createFulloptionUrlOrNull(navigationHelper, "bearbeiten", "gdeditportlet");
    }

    long totalHits =
        getTotalHitsForCurrentType(
            filterViewModelMap.get(SearchConsts.FILTER_KEY_TYPE), preparm.getType());

    ProcessedFilterBundle processedFilterBundle = postProcessingFilter(
        preparm.getActiveFilters(),
        filterViewModelMap,
        urlbuilder.createUrl(response, new String[] {}),
        themeDisplay.getLocale(),
        totalHits);

    // determine the currently active Type-Filter
    setActiveTypeFilter(filterViewModelMap, preparm.getType());

    // Prepare Models for Sorting
    List<SortViewModel> sortByList =
        createSortByList(preparm.getSelectedSorting(), urlbuilder.createUrl(response, new String[] {}));

    // Prepare BoundingBox-Clear-Url
    String clearBoundingBoxUrl =
        urlbuilder.createUrl(response, new String[] {QueryParamNames.PARAM_BOUNDINGBOX}).toString();

    // Prepare data for date-filter form (include every parameter except both date parameters)
    List<KeyValuePair> dateFilterPassthroughParams = urlbuilder.parametersAsList(new String[] {
        QueryParamNames.PARAM_START,
        QueryParamNames.PARAM_END
    });
    // and clear urls
    String clearDateFromUrl =
        urlbuilder.createUrl(response, new String[] {QueryParamNames.PARAM_START}).toString();
    String clearDateUntilUrl =
        urlbuilder.createUrl(response, new String[] {QueryParamNames.PARAM_END}).toString();

    // prepare flag for hiding filter options not usable on liferay-results (basically: check if
    // ckan types are there)
    boolean canContainCkanData = false;
    if (Arrays.asList(SearchConsts.CKAN_TYPES_ALL).contains(preparm.getType()))
    {
      for (FilterViewModel filterView : filterViewModelMap.get("type"))
      {
        if (Arrays.asList(SearchConsts.CKAN_TYPES).contains(filterView.getName())
            && filterView.getDocCount() > 0)
        {
          canContainCkanData = true;
        }
      }
    }

    // Prepare URL for switching to other search views
    String searchMapUrlString =
        urlbuilder.createFulloptionUrlOrNull(navigationHelper, "kartensuche", "gdsearchmap");
    String searchExtUrlString =
        urlbuilder.createFulloptionUrlOrNull(navigationHelper, "erweitertesuche", "gdsearchext");

    // preprare Suggestions
    List<SuggestionModel> preparedSuggestions = new ArrayList<>();
    if (!result.getSuggestions().isEmpty())
    {
      List<SuggestionOption> suggestions = new ArrayList<>(result.getSuggestions());
      Collections.sort(suggestions);

      for (SuggestionOption suggestion : suggestions)
      {
        // build URL with all filters/sorting cleared, only parameter is the suggestion
        PortletURL suggestionRenderUrl = response.createRenderURL();
        PortletUtil.setParameterInPortletUrl(
            suggestionRenderUrl, QueryParamNames.PARAM_PHRASE, suggestion.getName());
        String url = suggestionRenderUrl.toString();

        preparedSuggestions.add(SuggestionModel.builder().query(suggestion.getName()).url(url).build());
      }
    }

    // Generate URL for the ATOM-Feed
    urlbuilder = new UrlBuilder(preparm);
    String selfurl = null;
    try
    {
      selfurl = urlbuilder.createAtomFeedUrl(navigationHelper).toString();
    }
    catch (Exception e)
    {
      log.error("Atomfeed-Portlet not included in any page! Check your Liferay setup.");
    }

    boolean hasActiveFilters = processedFilterBundle.hasActiveFilters
        || preparm.getBoundingBox() != null
        || preparm.getDateFrom() != null
        || preparm.getDateUntil() != null;

    SearchResultsViewModel searchResultsViewModel = SearchResultsViewModel.builder()
        .totalHits(totalHits)
        .hits(hitViewModelList)
        .filterMap(filterViewModelMap)
        .activeFilterList(processedFilterBundle.getFilterViewModelList())
        .hasActiveFilters(hasActiveFilters)
        .hadActiveFiltersDuringSearch(preparm.isFiltersActive())
        .clearFilterUrl(clearFilterUrl)
        .hasActiveBoundingBoxFilter(preparm.getBoundingBox() != null)
        .clearBoundingBoxUrl(clearBoundingBoxUrl)
        .actionUrl(response.createActionURL().toString())
        .dateFrom(urlbuilder.getParam(QueryParamNames.PARAM_START))
        .dateUntil(urlbuilder.getParam(QueryParamNames.PARAM_END))
        .dateFilterPassthroughParams(dateFilterPassthroughParams)
        .clearDateFromUrl(clearDateFromUrl)
        .clearDateUntilUrl(clearDateUntilUrl)
        .sortByList(sortByList)
        .activeSortLabel(preparm.getSelectedSorting().toString())
        .moreNextHitsAvailable(result.isMoreNextHitsAvailable())
        .pageSize(result.getPageSize())
        .scrollId(result.getScrollId())
        .type(preparm.getType())
        .q(preparm.getQuery())
        .suggestions(preparedSuggestions)
        .atomFeedUrl(selfurl)
        .searchMapUrl(searchMapUrlString)
        .searchExtUrl(searchExtUrlString)
        .newDatasetUrl(newDatasetUrlString)
        .canContainCkanData(canContainCkanData)
        .build();

    model.addAttribute(MODEL_KEY_SEARCH_RESULT, searchResultsViewModel);
    model.addAttribute("themeDisplay", themeDisplay);

    // set scroll url
    ResourceURL nextHitsResourceURL = response.createResourceURL();
    nextHitsResourceURL.setResourceID(NEXT_HITS_RESOURCE_URL_ID);
    model.addAttribute(MODEL_KEY_SCROLL_RESOURCE_URL, nextHitsResourceURL.toString());

    log.debug("showSearchResults finished");
    return "searchresult";
  }

  @SuppressWarnings("unchecked")
  private void recordSearchPhrase(String query, PortletSession portletSession)
  {
    String toSave = StringUtils.trim(query);
    if (StringUtils.isEmpty(toSave))
    {
      return;
    }
    HashSet<String> usedPhrases;

    // load list of used phrases from session, if existing
    Object attribute = portletSession.getAttribute("searchphrases");
    if (attribute != null && attribute instanceof HashSet)
    {
      usedPhrases = (HashSet<String>) attribute;
    }
    else
    {
      usedPhrases = new HashSet<>();
    }

    if (!usedPhrases.contains(query))
    {
      // Save new phrase
      indexService.recordSearchPhrase(query);

      // Store new phrase in history
      usedPhrases.add(query);
      portletSession.setAttribute("searchphrases", usedPhrases);
    }
  }

  private List<SortViewModel> createSortByList(Sort selectedSorting, PortletURL filterRenderUrl)
  {
    Boolean[] boolList = {true, false};
    List<SortViewModel> result = new ArrayList<>();

    for (SortType type : SortType.values())
    {
      for (Boolean ascending : boolList)
      {
        String name =
            type.getKey() + "_"
                + (ascending ? SearchConsts.SEARCH_ASCENDING : SearchConsts.SEARCH_DESCENDING);
        filterRenderUrl.setParameter(QueryParamNames.PARAM_SORT, name);
        boolean active = selectedSorting != null && StringUtils.equals(selectedSorting.toString(), name);

        result.add(new SortViewModel(name, filterRenderUrl.toString(), active));
      }
    }

    return result;
  }

  @ResourceMapping(value = NEXT_HITS_RESOURCE_URL_ID)
  public String scrollNextHits(
      ResourceRequest request,
      ResourceResponse response,
      Model model)
  {
    final String method = "scrollNextHits(): ";
    log.debug(method + "Start");

    String scrollId = request.getParameter(QueryParamNames.PARAM_SCROLL_ID);
    log.debug(method + "scrollId: {}", scrollId);

    SearchResultContainer result = new SearchResultContainer();
    try
    {
      result = indexService.scroll(scrollId);
    }
    catch (Exception e)
    {
      log.warn(method + e.getMessage());
      response.setProperty(ResourceResponse.HTTP_STATUS_CODE, "500");
      return null;
    }

    ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);

    String currentPage = themeDisplay.getLayout().getFriendlyURL();
    List<HitViewModel> hitViewModelList;
    try
    {
      hitViewModelList = searchResultsMapper.mapHitDtoListToHitsViewModelList(
          result.getHits(), themeDisplay, currentPage, false);
    }
    catch (SystemException e)
    {
      log.warn(method + e.getMessage());
      response.setProperty(ResourceResponse.HTTP_STATUS_CODE, "500");
      return null;
    }

    SearchResultsViewModel nextHits = SearchResultsViewModel.builder()
        .hits(hitViewModelList)
        .scrollId(result.getScrollId())
        .build();
    model.addAttribute(MODEL_KEY_SEARCH_RESULT, nextHits);
    model.addAttribute("themeDisplay", themeDisplay);

    log.debug(method + "End");
    return "fragments/hits :: partialList";
  }

  /**
   * Sets "active=true" on the currently active Type-Filter
   *
   * @param filterViewModelMap Map containing all filters. Will be modified.
   * @param type               identifier for the active Type-Filter
   */
  private void setActiveTypeFilter(Map<String, FilterViewListModel> filterViewModelMap, String type)
  {
    FilterViewListModel filterViewListModel = filterViewModelMap.get(SearchConsts.FILTER_KEY_TYPE);

    for (FilterViewModel typeFilter : filterViewListModel)
    {
      if (StringUtils.equals(typeFilter.getName(), type))
      {
        typeFilter.setActive(true);
      }
    }
  }

  private long getTotalHitsForCurrentType(List<FilterViewModel> typeFilterList, String type)
  {
    long result = 0;
    long totalDefault = 0;
    boolean typeFound = false;
    if (CollectionUtils.isNotEmpty(typeFilterList))
    {
      for (FilterViewModel filter : typeFilterList)
      {
        // Defaultwert für alle Treffer speichern
        if (SearchConsts.TYPE_ALL.equals(filter.getName()))
        {
          totalDefault = filter.getDocCount();
        }
        if (StringUtils.equals(filter.getName(), type))
        {
          result = filter.getDocCount();
          typeFound = true;
        }
      }
      if (!typeFound)
      {
        result = totalDefault;
      }
    }
    return result;
  }

  /**
   * Iterate all available filters and generate actionURLs to toggle each filter option. The active
   * / inactive flag and the actionURL are directly set for each filter in filterMap.
   *
   * @param activeFilters      activated Filters
   * @param filterViewModelMap available Filters
   * @param filterActionUrl    Base-URL for constructing actionURLs
   */
  private ProcessedFilterBundle postProcessingFilter(
      Map<String, List<String>> activeFilters,
      Map<String, FilterViewListModel> filterViewModelMap,
      PortletURL filterActionUrl, Locale locale, long totalHits)
  {
    boolean hasActiveFilters = false;
    List<FilterViewModel> filterModelList = new ArrayList<>();

    // construct parameterString to be appended to baseURL - represents currently selected filters.
    // this will be modified by removing or adding an option to create the actionURLs for each
    // filter-toggle.
    String activeFiltersSerialized = FilterPathUtils.serializeFilterMap(activeFilters);
    log.debug("activeFiltersSerialized: {}", activeFiltersSerialized);

    for (Entry<String, FilterViewListModel> filterGroup : filterViewModelMap.entrySet())
    {
      String key = filterGroup.getKey();
      List<String> groupList = new ArrayList<>();
      if (activeFilters.get(key) != null)
      {
        groupList = new ArrayList<>(activeFilters.get(key));
      }

      // prepare filterstring in case of singletonFilter
      boolean isSingletonFilter = filterGroup.getValue().isSingletonFiltergroup();
      String strippedFilter = (isSingletonFilter)
          ? activeFiltersSerialized.replaceAll(FilterPathUtils.serializeFilterMatchAllRegex(key), "")
          : activeFiltersSerialized;

      for (FilterViewModel filter : filterGroup.getValue())
      {
        // check if filter is active (is part of request filter map)
        String val = filter.getName();
        boolean isActive = groupList.contains(val);
        groupList.remove(val); // remove it, so we end up with a list of remaining items

        // prepare FilterList for the list of removable filters above the searchresults
        if (isActive && !StringUtils.equals(key, SearchConsts.FILTER_KEY_TYPE))
        {
          // don't include types, we do not need them in this list
          hasActiveFilters = true;
          filter.setGroupName(key); // only needed for active filters
          filterModelList.add(filter);
        }

        // i18n filter name
        filter.setDisplayName(ViewUtil.getShortenedFormatRef(translateFilterName(filter.getName(), key, locale)));

        // if this is a singleton-filter, remove all active filter instances
        String modifiedFilter;
        if (isActive && isSingletonFilter)
        {
          // remove all instances
          modifiedFilter = strippedFilter;
        }
        else if (isActive)
        {
          // remove this filter instance
          modifiedFilter = strippedFilter.replace(FilterPathUtils.serializeFilter(key, val), "");
        }
        else
        {
          // add filter
          modifiedFilter = strippedFilter + FilterPathUtils.serializeFilter(key, val);
        }

        filterActionUrl.setParameter(QueryParamNames.PARAM_FILTER, modifiedFilter);
        filter.setActionURL(filterActionUrl.toString());
        filter.setActive(isActive);
      }

      // if there is a filter, but it is not part of the aggregation from server - include it
      // anyways
      for (String remainingItem : groupList)
      {
        if (StringUtils.isBlank(remainingItem))
        { // no empty / whitespace-items
          continue;
        }

        // if this is a singleton-filter, remove all active filter instances
        String modifiedFilter;
        if (isSingletonFilter)
        {
          // remove all instances
          modifiedFilter = strippedFilter;
        }
        else
        {
          // remove this filter instance
          modifiedFilter = strippedFilter.replace(FilterPathUtils.serializeFilter(key, remainingItem), "");
        }

        filterActionUrl.setParameter(QueryParamNames.PARAM_FILTER, modifiedFilter);

        FilterViewModel filter = new FilterViewModel(
            totalHits,
            remainingItem,
            translateFilterName(remainingItem, key, locale),
            filterGroup.getKey(),
            filterActionUrl.toString(),
            true);
        hasActiveFilters = true;
        filterModelList.add(filter);

        // add it to the map, so it will be shown in facet-list
        filterGroup.getValue().add(filter);
      }

      // sorting of groups filter by displayname and not by name anymore
      // (name in english, displayname in german -> wrong sorting)
      if (filterGroup.getKey().equals(SearchConsts.FACET_GROUPS))
      {
        filterGroup.getValue().sort(Comparator.comparing(FilterViewModel::getDisplayName));
      }
    }

    return new ProcessedFilterBundle(filterModelList, hasActiveFilters);
  }

  private String translateFilterName(String filterName, String filterType, Locale locale)
  {
    if (StringUtils.equals(filterType, SearchConsts.FACET_GROUPS))
    {
      // default-value: Fallback to name
      return LanguageUtil.get(locale, "od.category.label." + filterName, filterName);
    }

    if (StringUtils.equals(filterType, SearchConsts.FACET_LICENCE))
    {
      Map<String, Licence> licenceMap = licenceCache.getLicenceMap();
      Licence licence = licenceMap.get(filterName);
      if (licence != null)
      {
        return licenceMap.get(filterName).getTitle();
      }
    }

    if (StringUtils.equals(filterType, SearchConsts.FACET_SOURCEPORTAL))
    {
      Map<String, Organization> organizationMap = organizationCache.getOrganizationMap();
      Organization organization = organizationMap.get(filterName);
      if (organization != null)
      {
        return organizationMap.get(filterName).getTitle();
      }
    }

    if (StringUtils.equals(filterType, SearchConsts.FACET_OPENNESS))
    {
      return LanguageUtil.get(locale, "od.usage_agreement." + filterName); // "free" or "restricted"
    }

    if (StringUtils.equals(filterType, QueryParamNames.PARAM_SHOW_ONLY_EDITOR_METADATA))
    {
      return LanguageUtil.get(locale, "od.gdsearch.searchresult.facet.onlyEditorMetadata.option");
    }

    // fallback to raw name
    return filterName;
  }

  private Map<String, FilterViewListModel> mapFilterToViewModel(Map<String, FilterListDto> filterMap)
  {
    Map<String, FilterViewListModel> result = new HashMap<>();
    for (Entry<String, FilterListDto> entry : filterMap.entrySet())
    {
      FilterViewListModel filterViewList = new FilterViewListModel();
      filterViewList.setSingletonFiltergroup(entry.getValue().isSingletonFiltergroup());
      for (FacetDto filterDto : entry.getValue())
      {
        filterViewList.add(new FilterViewModel(filterDto.getDocCount(), filterDto.getName()));
      }
      result.put(entry.getKey(), filterViewList);
    }
    return result;
  }

  private boolean isResponsible(RenderRequest request)
  {
    final String method = "isResponsible() : ";
    log.trace(method + "Start");

    String friendlyUrlMapping = PortletUtil.extractFriendlyUrlMappingFromRequestUrl(request);
    log.debug(method + "friendlyUrlMapping: {}", friendlyUrlMapping);
    if (StringUtils.isNotEmpty(friendlyUrlMapping)
        && !SearchConsts.FRIENDLY_URL_MAPPING_RESULTS.equals(friendlyUrlMapping))
    {
      return false;
    }

    log.trace(method + "End");
    return true;
  }
}
