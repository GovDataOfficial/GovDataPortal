package de.seitenbau.govdata.search.filter;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

public class WildcardFilter extends BaseFilter
{

  private String phrase;

  /**
   * Data structure to hold wildcard-filter information
   * @param elasticSearchField
   * @param filterFragmentName
   * @param phrase
   */
  public WildcardFilter(String elasticSearchField, String filterFragmentName, String phrase)
  {
    super(elasticSearchField, filterFragmentName);
    this.phrase = phrase;
  }

  @Override
  public QueryBuilder createFilter()
  {
    return QueryBuilders.boolQuery().must(QueryBuilders.wildcardQuery(getElasticSearchField(), phrase));
  }

  @Override
  public String getLabel()
  {
    return phrase;
  }
}
