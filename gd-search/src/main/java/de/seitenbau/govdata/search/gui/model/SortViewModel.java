package de.seitenbau.govdata.search.gui.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SortViewModel
{

  private String name;
  
  private String actionURL;
  
  private boolean active;
}
