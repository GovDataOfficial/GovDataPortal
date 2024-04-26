package de.seitenbau.govdata.search.common.searchresult;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import de.seitenbau.govdata.constants.QueryParamNames;
import de.seitenbau.govdata.odp.common.filter.FilterPathUtils;
import de.seitenbau.govdata.odp.common.filter.SearchConsts;
import de.seitenbau.govdata.search.common.QuerySanatizer;
import de.seitenbau.govdata.search.common.SearchQuery;
import de.seitenbau.govdata.search.filter.util.FilterUtil;
import de.seitenbau.govdata.search.sort.Sort;
import de.seitenbau.govdata.search.sort.SortType;
import lombok.extern.slf4j.Slf4j;

/**
 * Verarbeitet die Parameter für die Suche.
 *
 * @author rnoerenberg
 */
@Slf4j
@Component
public class ParameterProcessing
{
  @Inject
  private FilterUtil filterUtil;

  /**
   * Preprocessing of search parameters and actual search.
   *
   * @param parameterMap
   * @param currentPage  url of the currentPage, helps to determine the actual selected document
   *                     type. Can be null.
   * @return
   */
  public PreparedParameters prepareParameters(Map<String, String[]> parameterMap, String currentPage)
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
      preparedParameters.setBoundingBox(boundingBoxParam);
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

  private String resolveType(Map<String, List<String>> activeFilters, String currentPage)
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
        if (StringUtils.equals(filterTypeValue, SearchConsts.TYPE_ALL)
            || filterUtil.getDefaultTypeFilterValues().contains(filterTypeValue))
        {
          log.trace(method + "End with selected filter '" + filterTypeValue + "'");
          return filterTypeValue;
        }
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
}
