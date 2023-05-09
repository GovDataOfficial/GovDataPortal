package de.seitenbau.govdata.search.filter;

import java.text.SimpleDateFormat;

import org.elasticsearch.index.query.QueryBuilder;

public abstract class BaseFilter
{
  /**
   * date format for temporal data
   */
  private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  /**
   * date format for labels
   */
  private final SimpleDateFormat labelDateFormat = new SimpleDateFormat("dd.MM.yyyy");

  /**
   * name of the field in elasticsearch
   */
  private String elasticSearchField;

  /**
   * name of the filter-fragment
   */
  private String filterFragmentName;

  /**
   * Base Filter
   * @param elasticSearchField
   * @param filterFragmentName
   */
  public BaseFilter(String elasticSearchField, String filterFragmentName)
  {
    this.elasticSearchField = elasticSearchField;
    this.filterFragmentName = filterFragmentName;
  }
  
  public String getFragmentName()
  {
    return filterFragmentName;
  }

  public String getElasticSearchField()
  {
    return elasticSearchField;
  }

  public SimpleDateFormat getSimpleDateFormat()
  {
    return (SimpleDateFormat) simpleDateFormat.clone();
  }

  public SimpleDateFormat getLabelDateFormat()
  {
    return (SimpleDateFormat) labelDateFormat.clone();
  }

  /**
   * Generate a filter builder
   * @return
   */
  public abstract QueryBuilder createFilter();

  /**
   * Get the label for the filter
   * @return
   */
  public abstract String getLabel();
  
  @Override
  public String toString()
  {
    return "field: " + elasticSearchField + ", fragment: " + filterFragmentName;
  }
}
