package de.seitenbau.govdata.search.filter;

import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.MatchQueryBuilder.Operator;
import org.elasticsearch.index.query.QueryBuilders;

public class QueryFilter extends BaseFilter
{

  private String phrase;

  public QueryFilter(String elasticSearchField, String filterFragmentName, String phrase)
  {
    super(elasticSearchField, filterFragmentName);
    this.phrase = phrase;
  }

  @Override
  public FilterBuilder createFilter()
  {
    return FilterBuilders.queryFilter(QueryBuilders.matchQuery(elasticSearchField, phrase).operator(Operator.AND));
  }

  @Override
  public String getLabel()
  {
    return phrase;
  }

}
