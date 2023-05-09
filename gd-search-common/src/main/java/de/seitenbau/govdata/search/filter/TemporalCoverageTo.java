package de.seitenbau.govdata.search.filter;

import java.util.Date;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import de.seitenbau.govdata.date.DateUtil;

public class TemporalCoverageTo extends BaseFilter
{
  private Date dateTo;

  /**
   * Data structure to hold date-filter information
   * @param elasticSearchField
   * @param filterFragmentName
   * @param dateTo
   */
  public TemporalCoverageTo(String elasticSearchField, String filterFragmentName, Date dateTo)
  {
    super(elasticSearchField, filterFragmentName);
    this.dateTo = DateUtil.getCopyOfDate(dateTo);
  }

  @Override
  public QueryBuilder createFilter()
  {
    return QueryBuilders.boolQuery()
        .must(QueryBuilders.rangeQuery(getElasticSearchField()).lte(getSimpleDateFormat().format(dateTo)));
  }

  @Override
  public String getLabel()
  {
    return getLabelDateFormat().format(dateTo);
  }

}
