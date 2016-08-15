package de.seitenbau.govdata.search.gui.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class SearchMapViewModel
{
  private String q;
  
  private String f;
  
  private String s;
  
  private String boundingbox;
  
  private String start; // dateFrom
  private String end; // dateUntil
  
  private String actionUrl;

  private boolean useOsm;
  private String tileUrl;
  private String geocodingUrl;
  private String credits;
  
  
}
