package de.seitenbau.govdata.search.index.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SuggestionOption implements Comparable<SuggestionOption> {
  private String name;
  private float score;

  @Override
  public int compareTo(SuggestionOption o)
  {
    return Float.compare(o.score, this.score);
  }
  
  public boolean equals(Object other) {
    return (other instanceof SuggestionOption && ((SuggestionOption)other).getName().equalsIgnoreCase(name));
  }
  
  public int hashCode() {
    return name.hashCode();
  }
  
}