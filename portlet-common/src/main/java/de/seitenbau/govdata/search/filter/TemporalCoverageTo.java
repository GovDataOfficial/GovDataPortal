package de.seitenbau.govdata.search.filter;

import java.util.Date;

import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;

public class TemporalCoverageTo extends BaseFilter
{
  private Date dateTo;

  public TemporalCoverageTo(String elasticSearchField, String filterFragmentName, Date dateTo)
  {
    super(elasticSearchField, filterFragmentName);
    this.dateTo = dateTo;
  }

  @Override
  public FilterBuilder createFilter()
  {
    return FilterBuilders.rangeFilter(elasticSearchField).lte(simpleDateFormat.format(dateTo));
  }

  @Override
  public String getLabel()
  {
    return labelDateFormat.format(dateTo);
  }

}
