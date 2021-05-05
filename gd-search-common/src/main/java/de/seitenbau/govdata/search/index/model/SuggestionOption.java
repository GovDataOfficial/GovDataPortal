package de.seitenbau.govdata.search.index.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enthält die Informationen zu einem Suchvorschlag.
 * 
 * @author rnoerenberg
 *
 */
@Getter
@AllArgsConstructor
public class SuggestionOption implements Comparable<SuggestionOption>
{
  private String name;
  private float score;

  @Override
  public int compareTo(SuggestionOption o)
  {
    return Float.compare(o.score, this.score);
  }
  
  @Override
  public boolean equals(Object other)
  {
    return (other instanceof SuggestionOption && ((SuggestionOption) other).getName().equalsIgnoreCase(name));
  }
  
  @Override
  public int hashCode()
  {
    return name.hashCode();
  }
  
}