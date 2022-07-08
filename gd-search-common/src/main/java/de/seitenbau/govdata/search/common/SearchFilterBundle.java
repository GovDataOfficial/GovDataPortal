package de.seitenbau.govdata.search.common;

import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.common.geo.GeoPoint;

import de.seitenbau.govdata.search.filter.BaseFilter;
import lombok.Data;

@Data
public class SearchFilterBundle
{
  private List<BaseFilter> filters = new ArrayList<BaseFilter>();

  // TypeFilter must be extra, so the SearchService can get numbers for type-unfiltered results and
  // create the correct result including this filter
  private String typeFilter;

  private Boolean hidePrivateDatasets = true;

  private Boolean showOnlyPrivateShowcases = false;

  private Boolean boostSpatialRelevance = false;

  private GeoPoint spatialCenter;
  
  private boolean forceRelevanceSort;

  /**
   * Add given filter to the list of filters.
   * 
   * @param f the filter to add.
   */
  public void addFilter(BaseFilter f)
  {
    filters.add(f);
  }

  public void setSpatialCenter(GeoPoint spatialCenter)
  {
    this.spatialCenter = spatialCenter;
  }
}
