package de.seitenbau.govdata.search.filter;

import java.text.SimpleDateFormat;

import org.elasticsearch.index.query.FilterBuilder;

public abstract class BaseFilter
{
  protected static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  protected static final SimpleDateFormat labelDateFormat = new SimpleDateFormat("dd.MM.yyyy");
  

  protected String elasticSearchField;
  protected String filterFragmentName;

  public BaseFilter(String elasticSearchField, String filterFragmentName)
  {
    this.elasticSearchField = elasticSearchField;
    this.filterFragmentName = filterFragmentName;
  }
  
  public String getFragmentName() {
    return filterFragmentName;
  }
  
  public abstract FilterBuilder createFilter();
  
  public abstract String getLabel();
  
  public String toString() {
    return "field: " + elasticSearchField + ", fragment: " + filterFragmentName;
  }
}
