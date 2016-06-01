package de.seitenbau.govdata.search.filter;

import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;

public class BooleanFilter extends BaseFilter {
  
  private final Boolean val;
  
  public BooleanFilter(String elasticSearchField, String filterFragmentName, Boolean val)
  {
    super(elasticSearchField, filterFragmentName);
    this.val = val;
  }

  @Override
  public FilterBuilder createFilter()
  {
    return FilterBuilders.termFilter(elasticSearchField, val ? "true" : "false");
  }

  @Override
  public String getLabel()
  {
    return val ? "true" : "false";
  }
}
