package de.seitenbau.govdata.search.filter;

import java.util.List;

import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;

public class TermsFilter extends BaseFilter
{

  private List<String> terms;

  public TermsFilter(String elasticSearchField, String filterFragmentName, List<String> terms)
  {
    super(elasticSearchField, filterFragmentName);
    this.terms = terms;
  }

  @Override
  public FilterBuilder createFilter()
  {
    return FilterBuilders.termsFilter(elasticSearchField, terms);
  }

  @Override
  public String getLabel()
  {
    return terms.toString();
  }

}
