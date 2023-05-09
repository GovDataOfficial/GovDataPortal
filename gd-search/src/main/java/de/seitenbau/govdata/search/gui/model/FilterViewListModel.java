package de.seitenbau.govdata.search.gui.model;

import java.util.ArrayList;
import java.util.Objects;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FilterViewListModel extends ArrayList<FilterViewModel>
{
  private static final long serialVersionUID = -1302064971452849865L;

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
    FilterViewListModel other = (FilterViewListModel) obj;
    return singletonFiltergroup == other.singletonFiltergroup;
  }

}
