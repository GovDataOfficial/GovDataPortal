package de.seitenbau.govdata.search.util.states;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StateContainer
{

  private String id;

  private String name;

  private int displayId;

  private String displayName;

  private BoundingBoxContainer boundingBox;

  private List<String> filterTitle;

  private List<String> filterTags;

  private List<String> filterDescription;

  private boolean filterSpatial;

  private List<String> filterGeocodingUri;

  private List<String> filterGeocodingLevelUri;

  private List<String> filterGeocodingDescription;

  private List<String> filterContributorIds;

}