package de.seitenbau.govdata.search.filter;

import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;

public class TermFilter extends BaseFilter
{
  
  private final String term;

  public TermFilter(String elasticSearchField, String filterFragmentName, String term)
  {
    super(elasticSearchField, filterFragmentName);
    this.term = term;
  }

  @Override
  public FilterBuilder createFilter()
  {
    return FilterBuilders.termFilter(elasticSearchField, term);
  }

  @Override
  public String getLabel()
  {
    return term;
  }
}
