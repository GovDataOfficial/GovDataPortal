package de.seitenbau.govdata.search.sort;

import lombok.Data;
import de.seitenbau.govdata.filter.SearchConsts;

@Data
public class Sort
{
  private final SortType type;
  private final Boolean ascending; // false = descending
  
  @Override
  public String toString()
  {
    return type.toString().toLowerCase() + "_"
        + (ascending ? SearchConsts.SEARCH_ASCENDING : SearchConsts.SEARCH_DESCENDING);
  }
}
