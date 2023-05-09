package de.seitenbau.govdata.search.index.model;

import java.util.ArrayList;
import java.util.Objects;

import lombok.Getter;
import lombok.Setter;

/**
 * Eine Liste von {@link FacetDto}.
 * 
 * @author rnoerenberg
 *
 */
@Getter
@Setter
public class FilterListDto extends ArrayList<FacetDto>
{
  private static final long serialVersionUID = -1713236388757569126L;
  
  // can there only be one selected option?
  private boolean singletonFiltergroup;

  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + Objects.hash(singletonFiltergroup);
    return result;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (this == obj)
    {
      return true;
    }
    if (!super.equals(obj))
    {
      return false;
    }
    if (getClass() != obj.getClass())
    {
      return false;
    }
    FilterListDto other = (FilterListDto) obj;
    return singletonFiltergroup == other.singletonFiltergroup;
  }
}
