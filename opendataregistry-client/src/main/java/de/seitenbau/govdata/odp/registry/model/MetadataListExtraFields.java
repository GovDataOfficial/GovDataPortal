package de.seitenbau.govdata.odp.registry.model;

import lombok.Getter;

/**
 * All Extras-Fields which store information as List of Strings.
 */
public enum MetadataListExtraFields
{

  HAS_VERSION("has_version"),
  IS_VERSION_OF("is_version_of"),
  LANGUAGE("language"),
  ALTERNATE_IDENTIFIER("alternate_identifier"),
  CONFORMS_TO("conforms_to"),
  CONTRIBUTOR_ID("contributorID"),
  GEOCODING_TEXT("geocodingText"),
  LEGALBASIS_TEXT("legalbasisText"),
  POLITICAL_GEOCODING_URI("politicalGeocodingURI"),
  USED_DATASETS("used_datasets");

  @Getter
  private String field;

  MetadataListExtraFields(String field)
  {
    this.field = field;
  }

}
