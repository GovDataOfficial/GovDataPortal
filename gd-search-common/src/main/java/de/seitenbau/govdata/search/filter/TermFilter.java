package de.seitenbau.govdata.search.filter;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

public class TermFilter extends BaseFilter
{
  
  private final String term;

  /**
   * Data structure to hold term-filter information
   * @param elasticSearchField
   * @param filterFragmentName
   * @param term
   */
  public TermFilter(String elasticSearchField, String filterFragmentName, String term)
  {
    super(elasticSearchField, filterFragmentName);
    this.term = term;
  }

  @Override
  public QueryBuilder createFilter()
  {
    return QueryBuilders.boolQuery().must(QueryBuilders.termQuery(getElasticSearchField(), term));
  }

  @Override
  public String getLabel()
  {
    return term;
  }
}
