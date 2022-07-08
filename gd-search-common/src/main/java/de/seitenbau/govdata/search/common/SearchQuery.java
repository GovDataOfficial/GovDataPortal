package de.seitenbau.govdata.search.common;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import lombok.Data;

/**
 * Represents the query string.
 */
@Data
public class SearchQuery
{
  private String queryString;

  /**
   * Constructor with query string parameter.
   * 
   * @param q the query string
   */
  public SearchQuery(String q)
  {
    this.queryString = q;
  }

  /**
   * Checks if the query string is empty or contains the match all character sign.
   * 
   * @return True if the query string contains no individual search phrase, otherwise False
   */
  public boolean isEmpty()
  {
    return StringUtils.isEmpty(this.queryString) || Objects.equals(this.queryString, "*");
  }
}
