package de.seitenbau.govdata.search.filter;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

public class BooleanFilter extends BaseFilter
{
  
  private final Boolean val;
  
  /**
   * Data structure to hold boolean-filter information
   * @param elasticSearchField
   * @param filterFragmentName
   * @param val
   */
  public BooleanFilter(String elasticSearchField, String filterFragmentName, Boolean val)
  {
    super(elasticSearchField, filterFragmentName);
    this.val = val;
  }

  @Override
  public QueryBuilder createFilter()
  {
    return QueryBuilders.boolQuery()
        .must(QueryBuilders.termQuery(elasticSearchField, val ? "true" : "false"));
  }

  @Override
  public String getLabel()
  {
    return val ? "true" : "false";
  }
}
