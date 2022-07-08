package de.seitenbau.govdata.search.filter;

import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

public class QueryFilter extends BaseFilter
{

  private String phrase;

  /**
   * Data structure to hold query-filter information
   * @param elasticSearchField
   * @param filterFragmentName
   * @param phrase
   */
  public QueryFilter(String elasticSearchField, String filterFragmentName, String phrase)
  {
    super(elasticSearchField, filterFragmentName);
    this.phrase = phrase;
  }

  @Override
  public QueryBuilder createFilter()
  {
    return QueryBuilders.boolQuery()
        .must(QueryBuilders.matchQuery(elasticSearchField, phrase).operator(Operator.AND));
  }

  @Override
  public String getLabel()
  {
    return phrase;
  }

}
