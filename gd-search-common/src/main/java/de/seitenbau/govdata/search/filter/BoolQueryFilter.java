package de.seitenbau.govdata.search.filter;

import java.util.List;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

public class BoolQueryFilter extends BaseFilter
{

  private List<String> phrases;

  /**
   * Data structure to hold bool-query-filter information
   * @param elasticSearchField
   * @param filterFragmentName
   * @param phrase
   */
  public BoolQueryFilter(String elasticSearchField, String filterFragmentName, List<String> phrases)
  {
    super(elasticSearchField, filterFragmentName);
    this.phrases = phrases;
  }

  @Override
  public QueryBuilder createFilter()
  {
    BoolQueryBuilder qb = QueryBuilders.boolQuery();
    for (String s : phrases)
    {
      QueryBuilder stringQuery = QueryBuilders.matchPhraseQuery(elasticSearchField, s);
      qb.should(stringQuery);
    }
    return qb;
  }

  @Override
  public String getLabel()
  {
    return phrases.toString();
  }

}
