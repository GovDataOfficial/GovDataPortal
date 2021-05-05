package de.seitenbau.govdata.search.comparator;

import java.util.Comparator;

import de.seitenbau.govdata.search.gui.model.FilterViewModel;

public class FilterViewModelDocCountDescComparator implements Comparator<FilterViewModel>
{
  /*
   * (non-Javadoc)
   * 
   * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
   */
  @Override
  public int compare(FilterViewModel model1, FilterViewModel model2)
  {
    return Long.compare(model2.getDocCount(), model1.getDocCount());
  }
}
