package de.seitenbau.govdata.search.filter;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

public class OrFilter extends BaseFilter
{
  private BaseFilter[] filters;

  /**
   * Data structure to hold or-filter information.
   * @param filterFragmentName
   * @param filters
   */
  public OrFilter(String filterFragmentName, BaseFilter... filters)
  {
    super("", filterFragmentName);
    this.filters = filters;
  }

  @Override
  public QueryBuilder createFilter()
  {
    BoolQueryBuilder orQuery = QueryBuilders.boolQuery();
    for (int i = 0; i < filters.length; i++)
    {
      orQuery.should(filters[i].createFilter());
    }
    return orQuery;
  }

  @Override
  public String getLabel()
  {
    return "OrFilterPhrase";
  }
}
