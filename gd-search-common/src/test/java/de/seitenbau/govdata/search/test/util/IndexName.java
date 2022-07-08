package de.seitenbau.govdata.search.test.util;

import lombok.Getter;

public enum IndexName
{
  CKAN("govdata-ckan-de"),
  LIFERAY("govdata-liferay-de"),
  SHOWCASES("govdata-showcases-de"),
  SEARCHHISTORY("govdata-searchhistory-de"),
  METRICS("govdata-metrics-de");

  @Getter
  private String index;

  private IndexName(String indexName)
  {
    this.index = indexName;
  }
}
