package de.seitenbau.govdata.search.filter;

import java.util.Date;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import de.seitenbau.govdata.date.DateUtil;

public class TemporalCoverageFrom extends BaseFilter
{
  private Date dateFrom;

  /**
   * Data structure to hold or-date information
   * @param elasticSearchField
   * @param filterFragmentName
   * @param dateFrom
   */
  public TemporalCoverageFrom(String elasticSearchField, String filterFragmentName, Date dateFrom)
  {
    super(elasticSearchField, filterFragmentName);
    this.dateFrom = DateUtil.getCopyOfDate(dateFrom);
  }

  @Override
  public QueryBuilder createFilter()
  {
    return QueryBuilders.boolQuery()
        .must(QueryBuilders.rangeQuery(getElasticSearchField()).gte(getSimpleDateFormat().format(dateFrom)));
  }

  @Override
  public String getLabel()
  {
    return getLabelDateFormat().format(dateFrom);
  }

}
