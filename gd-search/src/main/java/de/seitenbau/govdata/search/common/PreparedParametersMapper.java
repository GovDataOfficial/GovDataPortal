package de.seitenbau.govdata.search.common;

import org.springframework.stereotype.Component;

import de.seitenbau.govdata.odp.common.filter.FilterPathUtils;
import de.seitenbau.govdata.search.api.model.search.dto.PreparedParametersDto;
import de.seitenbau.govdata.search.api.model.search.dto.SearchQuery;
import de.seitenbau.govdata.search.api.model.search.dto.Sort;
import de.seitenbau.govdata.search.common.searchresult.PreparedParameters;
import lombok.extern.slf4j.Slf4j;

/**
 * Mappt zwischen {@link PreparedParameters} und {@link PreparedParametersDto}.
 * 
 * @author rnoerenberg
 *
 */
@Component
@Slf4j
public final class PreparedParametersMapper
{
  /**
   * Mappt ein {@link PreparedParameters} zu einem {@link PreparedParametersDto}.
   * 
   * @param youalad
   * @return
   */
  public static PreparedParametersDto mapToPreparedParametersDto(PreparedParameters preparm)
  {
    // SearchQuery
    SearchQuery searchQuery = SearchQuery.builder()
        .queryString(preparm.getQuery().getQueryString())
        .build();

    // Sort
    Sort sort = Sort.builder()
        .sortType(preparm.getSelectedSorting().getType().getKey())
        .ascending(preparm.getSelectedSorting().getAscending())
        .build();


    return PreparedParametersDto.builder()
        .activeFilters(FilterPathUtils.serializeFilterMap(preparm.getActiveFilters(), false))
        .dateFrom(preparm.getDateFrom())
        .dateUntil(preparm.getDateUntil())
        .type(preparm.getType())
        .boundingbox(preparm.getBoundingBox())
        .query(searchQuery)
        .sort(sort)
        .build();
  }
}
