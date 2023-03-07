package de.seitenbau.govdata.search.common.searchresult;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.common.geo.ShapeRelation;

import com.liferay.portal.kernel.util.PropsUtil;

import de.seitenbau.govdata.constants.QueryParamNames;
import de.seitenbau.govdata.odp.common.filter.FilterPathUtils;
import de.seitenbau.govdata.odp.common.filter.SearchConsts;
import de.seitenbau.govdata.odp.common.util.GovDataCollectionUtils;
import de.seitenbau.govdata.search.common.ESFieldConsts;
import de.seitenbau.govdata.search.common.QuerySanatizer;
import de.seitenbau.govdata.search.common.SearchFilterBundle;
import de.seitenbau.govdata.search.common.SearchQuery;
import de.seitenbau.govdata.search.filter.BaseFilter;
import de.seitenbau.govdata.search.filter.BoolQueryFilter;
import de.seitenbau.govdata.search.filter.BooleanFilter;
import de.seitenbau.govdata.search.filter.BoundingBox;
import de.seitenbau.govdata.search.filter.OrFilter;
import de.seitenbau.govdata.search.filter.QueryFilter;
import de.seitenbau.govdata.search.filter.TemporalCoverageFrom;
import de.seitenbau.govdata.search.filter.TemporalCoverageTo;
import de.seitenbau.govdata.search.filter.TermFilter;
import de.seitenbau.govdata.search.filter.TermsFilter;
import de.seitenbau.govdata.search.filter.WildcardFilter;
import de.seitenbau.govdata.search.sort.Sort;
import de.seitenbau.govdata.search.sort.SortType;
import de.seitenbau.govdata.search.util.GeoStateParser;
import de.seitenbau.govdata.search.util.states.StateContainer;
import lombok.extern.slf4j.Slf4j;

/**
 * Verarbeitet die Parameter für die Suche.
 *
 * @author rnoerenberg
 */
@Slf4j
public final class ParameterProcessing
{
  private static GeoStateParser geoStateParser = new GeoStateParser();

  private ParameterProcessing()
  {
    // no instance allowed
  }

  /**
   * Preprocessing of search parameters and actual search.
   *
   * @param parameterMap
   * @param currentPage  url of the currentPage, helps to determine the actual selected document
   *                     type. Can be null.
   * @return
   */
  public static PreparedParameters prepareParameters(Map<String, String[]> parameterMap, String currentPage)
  {
    PreparedParameters preparedParameters = new PreparedParameters();

    // Query
    String q = getSingleString(parameterMap, QueryParamNames.PARAM_PHRASE);
    preparedParameters.setQuery(new SearchQuery(QuerySanatizer.sanatizeQuery(q)));

    // Facet Filters
    String filterRequestParam = getSingleString(parameterMap, QueryParamNames.PARAM_FILTER);
    preparedParameters.setActiveFilters(FilterPathUtils.deserializeFilter(filterRequestParam));

    // Type
    preparedParameters.setType(resolveType(preparedParameters.getActiveFilters(), currentPage));

    // insert Type into ActiveFilters
    if (!StringUtils.equals(preparedParameters.getType(), SearchConsts.TYPE_ALL))
    {
      List<String> typeList = new ArrayList<String>();
      typeList.add(preparedParameters.getType());
      preparedParameters.getActiveFilters().put(SearchConsts.FILTER_KEY_TYPE, typeList);
    }

    // Sorting
    String sortParam = getSingleString(parameterMap, QueryParamNames.PARAM_SORT);
    preparedParameters.setSelectedSorting(mapSortParamToAvailableSort(sortParam));

    // Bounding Box
    String boundingBoxParam = getSingleString(parameterMap, QueryParamNames.PARAM_BOUNDINGBOX);
    if (StringUtils.isNotEmpty(boundingBoxParam))
    {
      try
      {
        preparedParameters.setBoundingBox(new BoundingBox(ESFieldConsts.BOUNDINGBOX,
            QueryParamNames.PARAM_BOUNDINGBOX, boundingBoxParam, ShapeRelation.INTERSECTS));
      }
      catch (Exception e)
      {
        // keep BoundingBox = null
        log.error("Error parsing bounding box!", e.getMessage());
      }
    }

    // Date Filters
    String dateFromParam = getSingleString(parameterMap, QueryParamNames.PARAM_START);
    if (StringUtils.isNotEmpty(dateFromParam))
    {
      try
      {
        preparedParameters.setDateFrom(QuerySanatizer.getDateFromString(dateFromParam));
      }
      catch (ParseException e)
      {
        log.error("Error parsing date-from filter! -> " + dateFromParam, e.getMessage());
      }
    }
    String dateUntilParam = getSingleString(parameterMap, QueryParamNames.PARAM_END);
    if (StringUtils.isNotEmpty(dateUntilParam))
    {
      try
      {
        preparedParameters.setDateUntil(QuerySanatizer.getDateFromString(dateUntilParam));
      }
      catch (ParseException e)
      {
        log.error("Error parsing date-until filter! -> " + dateUntilParam, e.getMessage());
      }
    }

    // Extra Check: If dateFrom is after dateUntil -> switch both dates
    if (preparedParameters.getDateFrom() != null && preparedParameters.getDateUntil() != null)
    {
      if (preparedParameters.getDateFrom().after(preparedParameters.getDateUntil()))
      {
        Date tmp = preparedParameters.getDateFrom();
        preparedParameters.setDateFrom(preparedParameters.getDateUntil());
        preparedParameters.setDateUntil(tmp);
      }
    }

    // Determine if there are any filters set (except query)
    preparedParameters.setFiltersActive(
        preparedParameters.getDateFrom() != null
            || preparedParameters.getDateUntil() != null
            || preparedParameters.getBoundingBox() != null
            || preparedParameters.getType() != SearchConsts.TYPE_ALL
            || !preparedParameters.getActiveFilters().isEmpty());

    return preparedParameters;
  }

  private static String getSingleString(Map<String, String[]> parameterMap, String key)
  {
    if (parameterMap.containsKey(key))
    {
      String[] values = parameterMap.get(key);
      if (values.length > 0)
      {
        return values[0];
      }
    }
    return null;
  }

  private static String resolveType(Map<String, List<String>> activeFilters, String currentPage)
  {
    final String method = "resolveType() : ";
    log.trace(method + "Start");

    // 1. Is there a type-filter in the filterMap?
    List<String> groupList = activeFilters.get(SearchConsts.FILTER_KEY_TYPE);
    // Validate type filter! Use only if it is a valid value!
    if (CollectionUtils.isNotEmpty(groupList))
    {
      String filterTypeValue = groupList.get(0);
      if (SearchConsts.VALID_FILTER_TYPES.contains(filterTypeValue))
      {
        log.trace(method + "End with selected filter '" + filterTypeValue + "'");
        return filterTypeValue;
      }
      else
      {
        // remove type filter because of invalid type filter
        activeFilters.remove(SearchConsts.FILTER_KEY_TYPE);
        log.debug(method + "Detected invalid type filter '" + filterTypeValue + "'. Fallback to default!");
      }
    }

    // 2. try to guess the type by the page we are on
    HashMap<String, String> pageMapping = new HashMap<>();
    pageMapping.put("/daten", SearchConsts.TYPE_DATASET);
    pageMapping.put("/showroom", SearchConsts.TYPE_SHOWCASE);
    if (pageMapping.containsKey(currentPage))
    {
      log.trace(method + "End");
      return pageMapping.get(currentPage);
    }

    // 3. fall back to "all documents" filter
    log.trace(method + "End with default '" + SearchConsts.TYPE_ALL + "'");
    return SearchConsts.TYPE_ALL;
  }

  private static Sort mapSortParamToAvailableSort(String sortParam)
  {
    try
    {
      String[] parts = sortParam.split("_"); // "relevance_asc" => "relevance", "asc"
      boolean ascending = StringUtils.equals(parts[1], "asc");

      SortType type = SortType.fromString(parts[0]);
      if (type != null)
      {
        return new Sort(type, ascending);
      }

      // return default...
      return new Sort(SortType.RELEVANCE, false);

    }
    catch (Exception e)
    {
      // return default...
      return new Sort(SortType.RELEVANCE, false);
    }
  }

  /**
   * Creates a filter bundle for the further processing from the search parameters.
   * 
   * @param preparm the search parameters
   * @param editorOrganizationIdList a list with organizations the current user has rights to edit
   *        datasets
   * @return the created filter bundle
   */
  public static SearchFilterBundle createFilterBundle(PreparedParameters preparm,
      List<String> editorOrganizationIdList)
  {
    return createFilterBundle(preparm, editorOrganizationIdList, Collections.emptyList());
  }

  /**
   * Creates a filter bundle for the further processing from the search parameters.
   * 
   * @param preparm the search parameters
   * @param editorOrganizationIdList a list with organizations the current user has rights to edit
   *        datasets
   * @param disabledFilterTypes a list with disabled filter types
   * @return the created filter bundle
   */
  public static SearchFilterBundle createFilterBundle(PreparedParameters preparm,
      List<String> editorOrganizationIdList, List<String> disabledFilterTypes)
  {
    SearchFilterBundle bundle = new SearchFilterBundle();

    Map<String, List<String>> filterToRemove = new HashMap<>();

    if (!StringUtils.equals(preparm.getType(), SearchConsts.TYPE_ALL))
    { // type:all should be blank
      bundle.setTypeFilter(preparm.getType());
    }

    // iterate active filter storage
    for (Entry<String, List<String>> entry : preparm.getActiveFilters().entrySet())
    {
      String key = entry.getKey();

      if (StringUtils.equals(key, SearchConsts.FILTER_KEY_TYPE))
      {
        continue; // we already have the type set
      }

      if (StringUtils.equals(key, QueryParamNames.PARAM_SHOW_ONLY_EDITOR_METADATA))
      {
        if (!editorOrganizationIdList.isEmpty())
        {
          bundle.addFilter(new TermsFilter(SearchConsts.FIELD_OWNER_ORG,
              QueryParamNames.PARAM_SHOW_ONLY_EDITOR_METADATA, editorOrganizationIdList));
          bundle.setHidePrivateDatasets(false); // all datasets for this organisation should be
          // shown, even private ones
        }

      }
      else if (StringUtils.equals(key, QueryParamNames.PARAM_SHOW_ONLY_PRIVATE_SHOWCASES))
      {
        // show only private showcases
        bundle.setShowOnlyPrivateShowcases(true);
      }
      else
      {
        // add a new filter for each active filter
        for (String filter : entry.getValue())
        {
          if (ESFieldConsts.FACET_MAP.containsKey(key))
          {
            if (StringUtils.equals(key, SearchConsts.FACET_SHOWCASE_TYPE))
            {
              // Showcase Types need an or-filter to check both fields: primary and additional
              TermFilter primaryFilter =
                  new TermFilter(ESFieldConsts.FIELD_PRIMARY_SHOWCASE_TYPE, key, filter);
              TermFilter additionalFilter =
                  new TermFilter(ESFieldConsts.FIELD_ADDITIONAL_SHOWCASE_TYPE, key, filter);
              bundle.addFilter(new OrFilter(key, primaryFilter, additionalFilter));
            }
            else
            {
              bundle.addFilter(new TermFilter(ESFieldConsts.FACET_MAP.get(key), key, filter));
            }
          }
          else if (ESFieldConsts.EXT_SEARCH_MAP.containsKey(key))
          {
            bundle.addFilter(new QueryFilter(ESFieldConsts.EXT_SEARCH_MAP.get(key), key, filter));
          }
          else if (StringUtils.equals(key, SearchConsts.FACET_OPENNESS)
              || StringUtils.equals(key, SearchConsts.FACET_DATASERVICE))
          {
            // use the "key" as grouping mechanism, effectively the "filter" is the key for this filter.
            // this can be done since the "value" of this filter is always "true".
            // it is merely a bunch of boolean filters disguised as list.
            if (ESFieldConsts.BOOL_FACET_MAP.containsKey(filter))
            {
              bundle.addFilter(new BooleanFilter(ESFieldConsts.BOOL_FACET_MAP.get(filter), key, true));
            }
            else
            {
              marksFilterToRemove(filterToRemove, key, filter);
            }
          }
          else if (StringUtils.equals(key, SearchConsts.FACET_HIGH_VALUE_DATASET))
          {
            if (StringUtils.equals(filter, SearchConsts.FACET_IS_HIGH_VALUE_DATASET))
            {
              List<String> highValueDatasetTags = GovDataCollectionUtils.convertStringListToLowerCase(
                  GovDataCollectionUtils.arrayToStream(StringUtils.stripAll(StringUtils.splitByWholeSeparator(
                      PropsUtil.get("elasticsearch.high.value.dataset.tags"), ",")))
                      .collect(Collectors.toUnmodifiableList()));
              if (CollectionUtils.isNotEmpty(highValueDatasetTags))
              {
                bundle.addFilter(new TermsFilter(ESFieldConsts.FIELD_TAGS_SEARCH, key, highValueDatasetTags));
              }
            }
            else
            {
              marksFilterToRemove(filterToRemove, key, filter);
            }
          }
          else if (!disabledFilterTypes.contains(SearchConsts.FACET_STATE)
              && StringUtils.equals(key, SearchConsts.FACET_STATE))
          {
            StateContainer state = geoStateParser.getStateList().stream()
                .filter(s -> filter.equals(s.getId())).findFirst().orElse(null);
            if (state != null)
            {
              BaseFilter[] filters = createBasicGeoStateFilters(key, filter, state);
              if (state.getBoundingBox() != null && state.isFilterSpatial())
              {
                // Add filter for map-search
                BoundingBox mapFilter = new BoundingBox(ESFieldConsts.BOUNDINGBOX,
                    QueryParamNames.PARAM_BOUNDINGBOX, state.getBoundingBox(), ShapeRelation.WITHIN);
                bundle.setBoostSpatialRelevance(true);
                ArrayUtils.add(filters, mapFilter);
              }
              bundle.addFilter(new OrFilter(key, filters));
              bundle.setForceRelevanceSort(true);
            }
          }
        }
      }
    }

    // date Filters (to/from flip on constants is intentional)
    if (preparm.getDateFrom() != null)
    {
      bundle.addFilter(new TemporalCoverageFrom(ESFieldConsts.TEMPORAL_END, QueryParamNames.PARAM_START,
          preparm.getDateFrom()));
    }
    if (preparm.getDateUntil() != null)
    {
      bundle.addFilter(new TemporalCoverageTo(ESFieldConsts.TEMPORAL_START, QueryParamNames.PARAM_END,
          preparm.getDateUntil()));
    }

    // bounding box
    if (preparm.getBoundingBox() != null)
    {
      bundle.addFilter(preparm.getBoundingBox());
      bundle.setBoostSpatialRelevance(true);
      bundle.setSpatialCenter(preparm.getBoundingBox().getCenter());
    }

    cleanActiveFilters(preparm, filterToRemove);

    return bundle;
  }

  private static void marksFilterToRemove(Map<String, List<String>> filterToRemove, String key, String filter)
  {
    if (!filterToRemove.containsKey(key))
    {
      filterToRemove.put(key, new ArrayList<>(List.of(filter)));
    }
    else
    {
      filterToRemove.get(key).add(filter);
    }
  }

  private static void cleanActiveFilters(PreparedParameters preparm, Map<String, List<String>> filterToRemove)
  {
    for (Entry<String, List<String>> toRemoveEntry : filterToRemove.entrySet())
    {
      String key = toRemoveEntry.getKey();
      if (preparm.getActiveFilters().containsKey(key))
      {
        for (String toRemove : toRemoveEntry.getValue())
        {
          preparm.getActiveFilters().get(key).remove(toRemove);
        }
        if (preparm.getActiveFilters().get(key).isEmpty())
        {
          preparm.getActiveFilters().remove(key);
        }
      }
    }
  }

  private static BaseFilter[] createBasicGeoStateFilters(String key, String stateId, StateContainer state)
  {
    List<BaseFilter> filters = new ArrayList<>();
    // Filter for title (needs lower case to match case-insensitive)
    if (CollectionUtils.isNotEmpty(state.getFilterTitle()))
    {
      BoolQueryFilter titleFilter = new BoolQueryFilter(ESFieldConsts.FIELD_TITLE_SEARCH_WORD,
          key, GovDataCollectionUtils.convertStringListToLowerCase(state.getFilterTitle()));
      filters.add(titleFilter);
    }

    // Filter for description (needs lower case to match case-insensitive)
    if (CollectionUtils.isNotEmpty(state.getFilterDescription()))
    {
      BoolQueryFilter descriptionFilter =
          new BoolQueryFilter(ESFieldConsts.FIELD_DESCRIPTION_SEARCH_WORD, key,
              GovDataCollectionUtils.convertStringListToLowerCase(state.getFilterDescription()));
      filters.add(descriptionFilter);
    }

    // Filter for tags (needs lower case to match case-insensitive)
    if (CollectionUtils.isNotEmpty(state.getFilterTags()))
    {
      TermsFilter tagsFilter =
          new TermsFilter(ESFieldConsts.FIELD_TAGS_SEARCH, key,
              GovDataCollectionUtils.convertStringListToLowerCase(state.getFilterTags()));
      filters.add(tagsFilter);
    }

    // Filter for contributor IDs
    if (CollectionUtils.isNotEmpty(state.getFilterContributorIds()))
    {
      TermsFilter contributorFilter = new TermsFilter(ESFieldConsts.FIELD_CONTRIBUTOR_ID_RAW, key,
          state.getFilterContributorIds());
      filters.add(contributorFilter);
    }

    // Filter for geopolitical URI (use wildcard filter)
    WildcardFilter geoUriFilter =
        new WildcardFilter(ESFieldConsts.FIELD_GEOCODING_URI_RAW, key, "*/" + stateId + "*");
    filters.add(geoUriFilter);

    // Filter for geopolitical description (needs lower case to match case-insensitive)
    if (CollectionUtils.isNotEmpty(state.getFilterGeocodingDescription()))
    {
      BoolQueryFilter geoTextFilter =
          new BoolQueryFilter(ESFieldConsts.FIELD_GEOCODING_TEXT, key,
              GovDataCollectionUtils.convertStringListToLowerCase(state.getFilterGeocodingDescription()));
      filters.add(geoTextFilter);
    }

    // Filter for geopolitical level URI
    if (CollectionUtils.isNotEmpty(state.getFilterGeocodingLevelUri()))
    {
      TermsFilter geoLevelUriFilter =
          new TermsFilter(ESFieldConsts.FIELD_GEOCODING_LEVEL_URI_RAW, key,
              state.getFilterGeocodingLevelUri());
      filters.add(geoLevelUriFilter);
    }

    return GovDataCollectionUtils.collectionToStream(filters).toArray(BaseFilter[]::new);
  }
}
