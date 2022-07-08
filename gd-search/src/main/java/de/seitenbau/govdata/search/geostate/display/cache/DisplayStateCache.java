package de.seitenbau.govdata.search.geostate.display.cache;

import static de.seitenbau.govdata.navigation.GovDataNavigation.FRIENDLY_URL_NAME_SEARCHRESULT_PAGE;
import static de.seitenbau.govdata.navigation.GovDataNavigation.PORTLET_NAME_SEARCHRESULT;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.portlet.PortletURL;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import com.liferay.portal.kernel.exception.PortalException;

import de.seitenbau.govdata.navigation.GovDataNavigation;
import de.seitenbau.govdata.odp.common.cache.BaseCache;
import de.seitenbau.govdata.odp.common.filter.FilterPathUtils;
import de.seitenbau.govdata.odp.common.filter.SearchConsts;
import de.seitenbau.govdata.odp.common.util.GovDataCollectionUtils;
import de.seitenbau.govdata.search.adapter.SearchService;
import de.seitenbau.govdata.search.common.SearchFilterBundle;
import de.seitenbau.govdata.search.common.SearchQuery;
import de.seitenbau.govdata.search.common.searchresult.ParameterProcessing;
import de.seitenbau.govdata.search.common.searchresult.PreparedParameters;
import de.seitenbau.govdata.search.index.model.SearchResultContainer;
import de.seitenbau.govdata.search.sort.Sort;
import de.seitenbau.govdata.search.sort.SortType;
import de.seitenbau.govdata.search.util.GeoStateParser;
import de.seitenbau.govdata.search.util.states.StateContainer;
import de.seitenbau.govdata.search.util.states.StateViewModel;
import lombok.extern.slf4j.Slf4j;

/**
 * Liefert die gecachten Werte für die Darstellung der State-Map.
 * @author sgebhart
 *
 */
@Slf4j
@Repository
public class DisplayStateCache extends BaseCache
{
  @Inject
  private GovDataNavigation govDataNavigation;

  @Inject
  private SearchService indexService;

  private GeoStateParser geoStateParser = new GeoStateParser();

  private List<StateViewModel> stateList;

  private List<String> blockedStates = new ArrayList<>();

  private int NUMBER_DARKEST_STATES = 5;

  private int NUMBER_SECOND_DARKEST_STATES = 5;

  /**
   * Initializes required components and sets configuration parameters.
   */
  @PostConstruct
  public void init()
  {
    setMaxCacheTimeAmount(1);
  }

  /**
   * Gibt die Daten aus dem Cache zurück. Falls keine gültigen vorliegen werden diese erst geladen.
   * @param blockedStatesString Kommaseparierter String mit den Bundesländern die gesperrt sein
   *        sollen.
   * @return
   */
  public List<StateViewModel> getStateList(String blockedStatesString)
  {
    final String method = "getStateList() : ";
    log.trace(method + "Start");

    // check if blocked states have been updated
    List<String> blockedStatesList =
        Stream
            .of(Optional.ofNullable(StringUtils.splitByWholeSeparator(blockedStatesString, ","))
                .orElse(new String[0]))
        .map(String::trim)
        .map(String::toLowerCase)
        .collect(Collectors.toList());

    if (stateList == null || isCacheExpired()
        || !CollectionUtils.isEqualCollection(blockedStates, blockedStatesList))
    {
      blockedStates = blockedStatesList;
      log.info("{}Empty or expired state-cache, creating new List.", method);
      stateList = createStateList();
      cacheUpdated();
    }

    // Do not cache link URLs, because the links could be corrupted while startup.
    GovDataCollectionUtils.collectionToStream(stateList)
        .forEach(s -> s.setUrl(createLinkUrl(s.getStateKey())));

    log.trace(method + "End");
    return stateList;
  }

  private String createLinkUrl(String stateKey)
  {
    final String method = "createLinkUrl() : ";
    log.trace(method + "Start");

    try
    {
      String filterParam =
          FilterPathUtils.serializeFilter(SearchConsts.FACET_STATE, stateKey);
      PortletURL redirectUrl = govDataNavigation
          .createLinkForSearchResults(FRIENDLY_URL_NAME_SEARCHRESULT_PAGE, PORTLET_NAME_SEARCHRESULT, "",
              filterParam, "");
      return redirectUrl.toString();
    }
    catch (PortalException | IllegalArgumentException e)
    {
      log.warn("{}Error while creating URL for state key {}: {}", method, stateKey,
          e.getMessage());
    }

    log.trace(method + "End");
    return null;
  }

  /**
   * Erstellt eine neue List mit Daten.
   * @return
   */
  private List<StateViewModel> createStateList()
  {
    final String method = "createStateList() : ";
    log.trace(method + "Start");

    List<StateContainer> geoStateList = geoStateParser.getStateList();
    if (geoStateList == null || geoStateList.isEmpty())
    {
      log.info(method + "Trying to use geo-state data. But it's empty.");
      log.trace(method + "End");
      return null;
    }

    List<StateViewModel> result = new ArrayList<>();
    for (StateContainer geoState : geoStateList)
    {
      StateViewModel state = new StateViewModel();
      state.setId(geoState.getDisplayId());
      state.setDisplayName(geoState.getDisplayName());
      state.setName(geoState.getName());
      state.setStateKey(geoState.getId());
      state.setBlocked(blockedStates.contains(geoState.getName().toLowerCase()));

      // get number of results
      long countResult = getNumberFromRequest(geoState.getId());
      state.setResultCount(countResult);

      result.add(state);
    }

    // calculate css class
    calculateAndSetCssClass(result);

    // sort by index
    Collections.sort(result, Comparator.comparing(StateViewModel::getId));

    log.trace(method + "End");

    return result;
  }

  void calculateAndSetCssClass(final List<StateViewModel> stateList)
  {
    List<StateViewModel> stateListNotBlocked = GovDataCollectionUtils.collectionToStream(stateList)
        .filter(svm -> !svm.isBlocked()).collect(Collectors.toList());
    // sort by result count descending order
    Collections.sort(stateListNotBlocked, Comparator.comparing(StateViewModel::getResultCount).reversed());

    int i = 0;
    for (StateViewModel state : stateListNotBlocked)
    {
      if (i < NUMBER_DARKEST_STATES)
      {
        state.setCssClass("100"); // Top 5
      }
      else if (i < NUMBER_DARKEST_STATES + NUMBER_SECOND_DARKEST_STATES)
      {
        state.setCssClass("80"); // Top 6-10
      }
      else
      {
        state.setCssClass("60");
      }
      i++;
    }
  }

  private long getNumberFromRequest(String id)
  {
    String filterParam =
        FilterPathUtils.serializeFilter(SearchConsts.FACET_STATE, id);
    PreparedParameters preparedParameters = new PreparedParameters();
    preparedParameters.setActiveFilters(FilterPathUtils.deserializeFilter(filterParam));
    SearchFilterBundle searchFilterBundle = ParameterProcessing.createFilterBundle(
        preparedParameters, new ArrayList<>());

    SearchResultContainer result = indexService.search(
        new SearchQuery(null),
        null,
        searchFilterBundle,
        new Sort(SortType.RELEVANCE, false));

    return result.getHitsTotal();
  }
}
