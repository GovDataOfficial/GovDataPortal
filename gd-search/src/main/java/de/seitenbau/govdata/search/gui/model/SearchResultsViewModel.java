package de.seitenbau.govdata.search.gui.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.liferay.portal.kernel.util.KeyValuePair;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchResultsViewModel
{
  private String displayName;
  
  private String actionUrl;
  
  private String searchMapUrl;
  
  private String searchExtUrl;
  
  private String newDatasetUrl;
  
  private String organizationFilterUrl;
  
  private boolean organizationFilterActive;

  private String q;

  private String type;

  private long totalHits;
  
  private String scrollId;
  
  private String clearFilterUrl;
  
  private String clearBoundingBoxUrl;
  
  private String atomFeedUrl;
  
  private String activeSortLabel;
  
  private boolean canContainSpatialData;
  
  private boolean canContainCkanData;

  private List<SuggestionModel> suggestions;
  
  private String dateFrom;
  private String dateUntil;
  private List<KeyValuePair> dateFilterPassthroughParams;
  private String clearDateFromUrl;
  private String clearDateUntilUrl;

  private boolean moreNextHitsAvailable;
  
  private int pageSize;

  private boolean hasActiveFilters;
  
  private boolean hadActiveFiltersDuringSearch;
  
  private boolean hasActiveBoundingBoxFilter;

  @Builder.Default
  private List<HitViewModel> hits = new ArrayList<HitViewModel>();

  @Builder.Default
  private Map<String, FilterViewListModel> filterMap = new HashMap<>();

  @Builder.Default
  private List<FilterViewModel> activeFilterList = new ArrayList<>();

  @Builder.Default
  private List<SortViewModel> sortByList = new ArrayList<>();
}
