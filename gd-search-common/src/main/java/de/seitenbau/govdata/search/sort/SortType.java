package de.seitenbau.govdata.search.sort;

import org.apache.commons.lang3.StringUtils;

public enum SortType
{
  /**
   * Sort type relevance
   */
  RELEVANCE("relevance"),
  /**
   * Sort type lastmodification
   */
  LASTMODIFICATION("lastmodification"),
  /**
   * Sort type title
   */
  TITLE("title");
  
  SortType(String key)
  {
    this.key = key;
  }
  
  public String getKey()
  {
    return key;
  }
  
  /**
   * Get SortType by key.
   * @param key
   * @return
   */
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
