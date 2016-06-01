package de.seitenbau.govdata.search.gui.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FilterViewModel
{
  private Long docCount;
  
  private String name;
  
  private String displayName;
  
  private String groupName;
  
  private String actionURL;
  
  private boolean active;
  
  /**
   * Minimal Constructor for initializing the first half of a FilterViewModel.
   * @param docCount
   * @param name
   */
  public FilterViewModel(Long docCount, String name)
  {
    this.docCount = docCount;
    this.name = name;
  }
}
