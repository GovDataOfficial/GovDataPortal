package de.seitenbau.govdata.search.common.searchresult;

import java.util.Date;
import java.util.List;
import java.util.Map;

import de.seitenbau.govdata.search.common.SearchQuery;
import de.seitenbau.govdata.search.sort.Sort;
import lombok.Data;

/**
 * Bundle of preprocessed parameters. These are extracted from the request parameter Map.
 *
 * @author tscheffler
 */
@Data
public class PreparedParameters
{
  // the query phrase
  private SearchQuery query;

  // active facet filters
  private Map<String, List<String>> activeFilters;

  // filter by document type
  private String type;

  // Sort results
  private Sort selectedSorting;

  // filter by Raumbezug
  private String boundingBox;

  // filter by Zeitraum
  private Date dateFrom;

  private Date dateUntil;

  // meta: are there active search filters? (except query)
  private boolean filtersActive;
}
