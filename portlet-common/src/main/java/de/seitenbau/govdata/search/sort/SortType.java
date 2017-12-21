package de.seitenbau.govdata.search.sort;

import org.apache.commons.lang3.StringUtils;

public enum SortType
{
  RELEVANCE("relevance"), LASTMODIFICATION("lastmodification"), TITLE("title");
  
  SortType(String key)
  {
    this.key = key;
  }
  
  public String getKey()
  {
    return key;
  }
  
  public static SortType fromString(String key)
  {
    for (SortType type : SortType.values())
    {
      if (StringUtils.equals(type.getKey(), key))
      {
        return type;
      }
    }
    
    // key not found.
    return null;
  }

  private String key;
}
