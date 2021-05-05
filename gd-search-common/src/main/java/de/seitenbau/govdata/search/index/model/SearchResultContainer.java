package de.seitenbau.govdata.search.index.model;

import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Enthält die Informationen zur Suchergebnisseite.
 * 
 * @author rnoerenberg
 *
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchResultContainer
{
  private String scrollId;

  private boolean moreNextHitsAvailable;

  private int pageSize;

  private List<HitDto> hits;
  
  private Map<String, FilterListDto> filterMap;
  
  private Set<SuggestionOption> suggestions;
}
