package de.seitenbau.govdata.search.gui.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NumberViewModel
{
  private Long docCount;

  private String name;

  private String actionURL;

  /**
   * Minimal Constructor.
   * @param docCount
   * @param name
   */
  public NumberViewModel(Long docCount, String name)
  {
    this.docCount = docCount;
    this.name = name;
  }
}
