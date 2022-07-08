package de.seitenbau.govdata.search.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import de.seitenbau.govdata.odp.common.filter.SearchConsts;
import de.seitenbau.govdata.search.gui.model.NumberViewModel;
import de.seitenbau.govdata.search.index.model.FacetDto;
import de.seitenbau.govdata.search.index.model.SearchResultContainer;

public class NumberParser
{
  private SearchResultContainer numberData;

  private static final String[] TYPES =
      {SearchConsts.TYPE_DATASET, SearchConsts.TYPE_SHOWCASE, SearchConsts.TYPE_BLOG};

  /**
   * Parse the raw elastic-search data and return the numbers for datasets, showcases and blogs.
   * @return
   */
  public List<NumberViewModel> getValues()
  {
    List<NumberViewModel> valueList = new ArrayList<>();
    for (FacetDto filter : numberData.getFilterMap().get(SearchConsts.FILTER_KEY_TYPE))
    {
      if (ArrayUtils.contains(TYPES, filter.getName()))
      {
        valueList.add(new NumberViewModel(filter.getDocCount(), filter.getName()));
      }
    }
    // make sure no keys are missing
    for (String type : TYPES)
    {
      if (!valueList.stream().anyMatch(o -> o.getName().equals(type)))
      {
        valueList.add(new NumberViewModel(0L, type));
      }
    }
    return valueList;
  }

  /**
   * Set the data for parsing
   * @param numberData
   */
  public void setData(SearchResultContainer numberData)
  {
    this.numberData = numberData;
  }

}
