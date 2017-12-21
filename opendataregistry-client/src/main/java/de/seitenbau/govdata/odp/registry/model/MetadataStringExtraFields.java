package de.seitenbau.govdata.odp.registry.model;

import lombok.Getter;

/**
 * All Extras-Fields which store information as String.
 */
public enum MetadataStringExtraFields
{

  ACCESS_RIGHTS("access_rights"),
  QUALITY_PROCESS_URI("qualityProcessURI"),
  DOCUMENTATION("documentation"),
  PROVENANCE("provenance"),
  VERSION("version"),
  VERSION_NOTES("version_notes"),
  SPATIAL("spatial"),

  // these would be enums, but may as well be strings for now.
  POLITICAL_GEOCODING_LEVEL_URI("politicalGeocodingLevelURI"),
  FREQUENCY("frequency");

  @Getter
  private String field;

  MetadataStringExtraFields(String field)
  {
    this.field = field;
  }

}
