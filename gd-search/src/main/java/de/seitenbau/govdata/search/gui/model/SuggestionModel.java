package de.seitenbau.govdata.search.gui.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SuggestionModel
{
  // suggested query string
  private String query;
  
  // url to execute a search for the suggested query
  private String url;
  
  public boolean equals(Object other) {
    return (other instanceof SuggestionModel && ((SuggestionModel)other).getQuery().equalsIgnoreCase(query));
  }
  
  public int hashCode() {
    return query.hashCode();
  }
}
