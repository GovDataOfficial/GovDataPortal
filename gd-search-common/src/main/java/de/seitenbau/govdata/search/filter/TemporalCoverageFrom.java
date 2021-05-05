package de.seitenbau.govdata.search.filter;

import java.util.Date;

import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;

public class TemporalCoverageFrom extends BaseFilter
{
  private Date dateFrom;

  public TemporalCoverageFrom(String elasticSearchField, String filterFragmentName, Date dateFrom)
  {
    super(elasticSearchField, filterFragmentName);
    this.dateFrom = dateFrom;
  }

  @Override
  public FilterBuilder createFilter()
  {
    return FilterBuilders.rangeFilter(elasticSearchField).gte(simpleDateFormat.format(dateFrom));
  }

  @Override
  public String getLabel()
  {
    return labelDateFormat.format(dateFrom);
  }

}
