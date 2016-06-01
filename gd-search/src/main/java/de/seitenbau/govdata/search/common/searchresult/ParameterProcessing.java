package de.seitenbau.govdata.search.common.searchresult;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.seitenbau.govdata.constants.QueryParamNames;
import de.seitenbau.govdata.filter.FilterPathUtils;
import de.seitenbau.govdata.filter.SearchConsts;
import de.seitenbau.govdata.search.common.ESFieldConsts;
import de.seitenbau.govdata.search.common.QuerySanatizer;
import de.seitenbau.govdata.search.common.SearchFilterBundle;
import de.seitenbau.govdata.search.filter.BooleanFilter;
import de.seitenbau.govdata.search.filter.BoundingBox;
import de.seitenbau.govdata.search.filter.QueryFilter;
import de.seitenbau.govdata.search.filter.TemporalCoverageFrom;
import de.seitenbau.govdata.search.filter.TemporalCoverageTo;
import de.seitenbau.govdata.search.filter.TermFilter;
import de.seitenbau.govdata.search.filter.TermsFilter;
import de.seitenbau.govdata.search.sort.Sort;
import de.seitenbau.govdata.search.sort.SortType;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ParameterProcessing
{
  /**
   * Preprocessing of search parameters and actual search.
   * @param parameterMap
   * @param currentPage url of the currentPage, helps to determine the actual selected document type.
   *                    Can be null. 
   * @return
   */
  public static PreparedParameters prepareParameters(Map<String, String[]> parameterMap, String currentPage) {
    PreparedParameters preparedParameters = new PreparedParameters();
    
    // Query
    String q = getSingleString(parameterMap, QueryParamNames.PARAM_PHRASE);
    if(q != null) {
      preparedParameters.setQuery(QuerySanatizer.sanatizeQuery(q));
    }
    
    // Facet Filters
    String filterRequestParam = getSingleString(parameterMap, QueryParamNames.PARAM_FILTER);
    preparedParameters.setActiveFilters(FilterPathUtils.deserializeFilter(filterRequestParam));
    
    // Type
    preparedParameters.setType(resolveType(preparedParameters.getActiveFilters(), currentPage));
    
    // insert Type into ActiveFilters
    if(!StringUtils.equals(preparedParameters.getType(), SearchConsts.TYPE_ALL)) {
      List<String> typeList = new ArrayList<String>();
      typeList.add(preparedParameters.getType());
      preparedParameters.getActiveFilters().put(SearchConsts.FILTER_KEY_TYPE, typeList);
    }
    
    // Sorting
    String sortParam = getSingleString(parameterMap, QueryParamNames.PARAM_SORT);
    preparedParameters.setSelectedSorting(mapSortParamToAvailableSort(sortParam));
    
    // Bounding Box
    String boundingBoxParam = getSingleString(parameterMap, QueryParamNames.PARAM_BOUNDINGBOX);
    if(StringUtils.isNotEmpty(boundingBoxParam)) {
      try {
        preparedParameters.setBoundingBox(new BoundingBox(ESFieldConsts.BOUNDINGBOX, QueryParamNames.PARAM_BOUNDINGBOX, boundingBoxParam));
      } catch(Exception e) {
        // keep BoundingBox = null
        log.error("Error parsing bounding box!", e.getMessage());
      }
    }
    
    // Date Filters
    String dateFromParam = getSingleString(parameterMap, QueryParamNames.PARAM_FROM);
    if(StringUtils.isNotEmpty(dateFromParam)) {
      try {
        preparedParameters.setDateFrom(QuerySanatizer.getDateFromString(dateFromParam));
      } catch(ParseException e) {
        log.error("Error parsing date-from filter! -> " + dateFromParam, e.getMessage());
      }
    }
    String dateUntilParam = getSingleString(parameterMap, QueryParamNames.PARAM_UNTIL);
    if(StringUtils.isNotEmpty(dateUntilParam)) {
      try {
        preparedParameters.setDateUntil(QuerySanatizer.getDateFromString(dateUntilParam));
      } catch(ParseException e) {
        log.error("Error parsing date-until filter! -> " + dateUntilParam, e.getMessage());
      }
    }
    
    // Extra Check: If dateFrom is after dateUntil -> switch both dates
    if(preparedParameters.getDateFrom() != null && preparedParameters.getDateUntil() != null) {
      if(preparedParameters.getDateFrom().after(preparedParameters.getDateUntil())) {
        Date tmp = preparedParameters.getDateFrom();
        preparedParameters.setDateFrom(preparedParameters.getDateUntil());
        preparedParameters.setDateUntil(tmp);
      }
    }
    
    // Determine if there are any filters set (except query)
    preparedParameters.setFiltersActive(
        preparedParameters.getDateFrom() != null ||
        preparedParameters.getDateUntil() != null ||
        preparedParameters.getBoundingBox() != null ||
        preparedParameters.getType() != SearchConsts.TYPE_ALL ||
        !preparedParameters.getActiveFilters().isEmpty());
    
    return preparedParameters;
  }
  
  
  private static String getSingleString(Map<String, String[]> parameterMap, String key) {
    if(parameterMap.containsKey(key)) {
      String[] values = parameterMap.get(key);
      if(values.length > 0) {
        return values[0];
      }
    }
    return null;
  }
  
  private static String resolveType(Map<String, List<String>> activeFilters, String currentPage)
  {
    // Is there a type-filter in the filterMap?
    List<String> groupList = activeFilters.get(SearchConsts.FILTER_KEY_TYPE);
    if(groupList != null && StringUtils.isNotEmpty(groupList.get(0))) {
      return groupList.get(0);
    }
    
    // try to guess the type by the page we are on
    HashMap<String, String> pageMapping = new HashMap<>();
    pageMapping.put("/daten", SearchConsts.TYPE_DATENSATZ);
    pageMapping.put("/apps", SearchConsts.TYPE_APP);
    pageMapping.put("/dokumente", SearchConsts.TYPE_DOCUMENT);
    if(pageMapping.containsKey(currentPage)) {
      return pageMapping.get(currentPage);
    }
    
    // fall back to "all documents" filter
    return SearchConsts.TYPE_ALL;
  }
  
  private static Sort mapSortParamToAvailableSort(String sortParam) {
    try {
      String[] parts = sortParam.split("_"); // "relevance_asc" => "relevance", "asc"
      boolean ascending = StringUtils.equals(parts[1], "asc");
      
      SortType type = SortType.fromString(parts[0]);
      if(type != null) {
        return new Sort(type, ascending);
      }
      
      // return default...
      return new Sort(SortType.RELEVANCE, false);
      
    } catch (Exception e) {
      // return default...
      return new Sort(SortType.RELEVANCE, false);
    }
  }
  
  public static String convertToJSON(PreparedParameters preparm) throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    return mapper.writeValueAsString(preparm);
  }
  
  public static SearchFilterBundle createFilterBundle(PreparedParameters preparm, List<String> editorOrganizationIdList) {
    SearchFilterBundle bundle = new SearchFilterBundle();
    
    if(!StringUtils.equals(preparm.getType(), SearchConsts.TYPE_ALL)) { // type:all should be blank
      bundle.setTypeFilter(preparm.getType());
    }
      
    // iterate active filter storage
    for(Entry<String, List<String>> entry : preparm.getActiveFilters().entrySet()) {
      String key = entry.getKey();
      
      if(StringUtils.equals(key, SearchConsts.FILTER_KEY_TYPE)) {
        continue; // we already have the type set
      }
      
      if (StringUtils.equals(key, SearchConsts.FACET_ISOPEN)) {
        // boolean filter ... there can only one option be set, so it's safe to take the first entry
        bundle.addFilter(new BooleanFilter(
            ESFieldConsts.ISOPEN,
            SearchConsts.FACET_ISOPEN,
            (StringUtils.equals(entry.getValue().get(0), SearchConsts.ISOPEN_OPEN))));
        
      // organisation Editor Filter
      } else if(StringUtils.equals(key, QueryParamNames.PARAM_SHOW_ONLY_EDITOR_METADATA)) {
        if(!editorOrganizationIdList.isEmpty()) {
          bundle.addFilter(new TermsFilter(SearchConsts.FIELD_OWNER_ORG, QueryParamNames.PARAM_SHOW_ONLY_EDITOR_METADATA, editorOrganizationIdList));
          bundle.setHidePrivateDatasets(false); // all datasets for this organisation should be shown, even private ones
        }
        
      } else {
        // add a new filter for each active filter
        for(String filter : entry.getValue()) {
          if(ESFieldConsts.facetMap.containsKey(key)) {
            bundle.addFilter(new TermFilter(ESFieldConsts.facetMap.get(key), key, filter));
            
          } else if(ESFieldConsts.extSearchMap.containsKey(key)) {
            bundle.addFilter(new QueryFilter(ESFieldConsts.extSearchMap.get(key), key, filter));
          }
        }
      }
    }
    
    // date Filters (to/from flip on constants is intentional)
    if(preparm.getDateFrom() != null) {
      bundle.addFilter(new TemporalCoverageFrom(ESFieldConsts.TEMPORAL_COVERAGE_TO, QueryParamNames.PARAM_FROM, preparm.getDateFrom()));
    }
    if(preparm.getDateUntil() != null) {
      bundle.addFilter(new TemporalCoverageTo(ESFieldConsts.TEMPORAL_COVERAGE_FROM, QueryParamNames.PARAM_UNTIL, preparm.getDateUntil()));
    }
    
    // bounding box
    if(preparm.getBoundingBox() != null) {
      bundle.addFilter(preparm.getBoundingBox());
      bundle.setBoostSpatialRelevance(true);
      bundle.setSpatialCenter(preparm.getBoundingBox().getCenter());
    }

    // debugging used filters
//    for(BaseFilter f : bundle.getFilters()) {
//      log.info(f.toString());
//    }
    
    return bundle;
  }
}
