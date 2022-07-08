package de.seitenbau.govdata.search.filter;

import java.util.List;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

public class TermsFilter extends BaseFilter
{

  private List<String> terms;

  /**
   * Data structure to hold terms-filter information
   * @param elasticSearchField
   * @param filterFragmentName
   * @param terms
   */
  public TermsFilter(String elasticSearchField, String filterFragmentName, List<String> terms)
  {
    super(elasticSearchField, filterFragmentName);
    this.terms = terms;
  }

  @Override
  public QueryBuilder createFilter()
  {
    return QueryBuilders.boolQuery().must(QueryBuilders.termsQuery(elasticSearchField, terms));
  }

  @Override
  public String getLabel()
  {
    return terms.toString();
  }

}
