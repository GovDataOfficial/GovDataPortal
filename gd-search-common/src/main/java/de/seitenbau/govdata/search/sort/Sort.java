package de.seitenbau.govdata.search.sort;

import de.seitenbau.govdata.odp.common.filter.SearchConsts;
import lombok.Data;

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
