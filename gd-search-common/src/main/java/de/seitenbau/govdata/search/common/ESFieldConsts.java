package de.seitenbau.govdata.search.common;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import de.seitenbau.govdata.odp.common.filter.SearchConsts;

/**
 * This keeps all constants related to elastic search field identifiers.
 *
 * @author tscheffler
 */
public abstract class ESFieldConsts
{
  /**
   * Key for field BOUNDINGBOX in search index.
   */
  public static final String BOUNDINGBOX = "metadata.boundingbox";

  /**
   * Key for field HAS_OPEN in search index.
   */
  public static final String HAS_OPEN = "metadata.has_open";

  /**
   * Key for field HAS_CLOSED in search index.
   */
  public static final String HAS_CLOSED = "metadata.has_closed";

  /**
   * Key for field HAS_DATASERVICE in search index.
   */
  public static final String HAS_DATASERVICE = "metadata.has_data_service";

  /**
   * Key for field TEMPORAL_START in search index.
   */
  public static final String TEMPORAL_START = "metadata.temporal_start";

  /**
   * Key for field TEMPORAL_END in search index.
   */
  public static final String TEMPORAL_END = "metadata.temporal_end";

  /**
   * Key for field PRIVATE in search index.
   */
  public static final String PRIVATE = "metadata.private";

  /**
   * Key for field TYPE in search index, e.g. "dataset", "article" or "blog".
   */
  public static final String FIELD_METADATA_TYPE = "metadata.type";

  /**
   * Key for field FIELD_TAGS_SEARCH in search index.
   */
  public static final String FIELD_TAGS_SEARCH = "tags";

  /**
   * Key for facet field tags in search index.
   */
  public static final String FIELD_TAGS_FACET = "tags.facet";

  /**
   * Key for field title in search index.
   */
  public static final String FIELD_TITLE = "title";

  /**
   * Key for field title.word_delimiter in search index.
   */
  public static final String FIELD_TITLE_SEARCH_WORD = "title.word_delimiter";

  /**
   * Key for field description in search index.
   */
  public static final String FIELD_DESCRIPTION = "preamble";

  /**
   * Key for field notes in search index.
   */
  public static final String FIELD_DESCRIPTION_SEARCH_WORD = "preamble.word_delimiter";

  /**
   * Key for metadata.portletId in search index.
   */
  public static final String FIELD_METADATA_PORTLET_ID = "metadata.portletId";

  /**
   * Key for facet field format in search index.
   */
  public static final String FIELD_METADATA_RESOURCES_FORMAT = "metadata.resources.format.facet";

  /**
   * Key for field 'metadata.name' in search index.
   */
  public static final String FIELD_METADATA_NAME = "metadata.name";

  /**
   * Key for field 'metadata.maintainer' in search index.
   */
  public static final String FIELD_METADATA_MAINTAINER = "metadata.maintainer";

  /**
   * Key for field 'metadata.publisher_name' in search index.
   */
  public static final String FIELD_METADATA_PUBLISHER_NAME = "metadata.publisher_name";

  /**
   * Key for field 'metadata.groups' in search index.
   */
  public static final String FIELD_METADATA_GROUPS = "metadata.groups.keyword";

  /**
   * Key for field 'metadata.resources_licenses' in search index.
   */
  public static final String FIELD_METADATA_RESOURCES_LICENSES = "metadata.resources_licenses";

  /**
   * Key for field platforms in search index.
   */
  public static final String FIELD_SHOWCASE_PLATFORM = "metadata.platforms.facet";

  /**
   * Key for field primary-showcase-type in search index.
   */
  public static final String FIELD_PRIMARY_SHOWCASE_TYPE = "metadata.primary_showcase_type.keyword";

  /**
   * Key for field additional-showcase-type in search index.
   */
  public static final String FIELD_ADDITIONAL_SHOWCASE_TYPE = "metadata.showcase_types.facet";

  /**
   * Key for raw field contributor-id in search index.
   */
  public static final String FIELD_CONTRIBUTOR_ID_RAW = "metadata.contributorID.raw";

  /**
   * Key for raw field geocodingURI in search index.
   */
  public static final String FIELD_GEOCODING_URI_RAW = "metadata.politicalGeocodingURI.raw";

  /**
   * Key for raw field geocodingText in search index.
   */
  public static final String FIELD_GEOCODING_TEXT = "metadata.geocodingText.word_delimiter";

  /**
   * Key for field politicalGeocodingLevelURI in search index.
   */
  public static final String FIELD_GEOCODING_LEVEL_URI_RAW = "metadata.politicalGeocodingLevelURI.raw";

  /**
   * Key for facet select showcase-type in search index.
   */
  public static final String FIELD_SHOWCASE_TYPES_VALUE =
      "if (doc.containsKey('" + FIELD_PRIMARY_SHOWCASE_TYPE + "') && doc.containsKey('"
          + FIELD_ADDITIONAL_SHOWCASE_TYPE + "')) {def a = []; for (int j = 0; j < doc['"
          + FIELD_ADDITIONAL_SHOWCASE_TYPE + "'].length; j++) {a.add(doc['" + FIELD_ADDITIONAL_SHOWCASE_TYPE
          + "'][j]);} a.add(doc['" + FIELD_PRIMARY_SHOWCASE_TYPE + "'].value); return a}";

  /**
   * Key for the metrics date in search index.
   */
  public static final String FIELD_METRICS_DATE = "date";

  /**
   * Key for the sorting date field in search index.
   */
  public static final String SORT_DATE = "sort_date_dct";

  /**
   * Key for sorting title field in search index.
   */
  public static final String SORT_TITLE = "title.sort";

  /**
   * Map with available facets.
   */
  public static final Map<String, String> FACET_MAP;

  /**
   * Map with available extended search facets.
   */
  public static final Map<String, String> EXT_SEARCH_MAP;

  /**
   * Map with available boolean facets.
   */
  public static final Map<String, String> BOOL_FACET_MAP;

  static
  {
    Map<String, String> facetMapBuilder = new HashMap<>();
    facetMapBuilder.put(SearchConsts.FACET_GROUPS, FIELD_METADATA_GROUPS);
    facetMapBuilder.put(SearchConsts.FACET_TAGS, FIELD_TAGS_FACET);
    facetMapBuilder.put(SearchConsts.FACET_FORMAT, FIELD_METADATA_RESOURCES_FORMAT);
    facetMapBuilder.put(SearchConsts.FACET_PLATFORMS, FIELD_SHOWCASE_PLATFORM);
    facetMapBuilder.put(SearchConsts.FACET_SHOWCASE_TYPE, FIELD_SHOWCASE_TYPES_VALUE);
    facetMapBuilder.put(SearchConsts.FACET_LICENCE, FIELD_METADATA_RESOURCES_LICENSES);
    facetMapBuilder.put(SearchConsts.FACET_SOURCEPORTAL, SearchConsts.FIELD_OWNER_ORG);
    FACET_MAP = Collections.unmodifiableMap(facetMapBuilder);

    Map<String, String> extSearchMapBuilder = new HashMap<>();
    extSearchMapBuilder.put(SearchConsts.FILTER_EXT_TITLE, FIELD_TITLE);
    extSearchMapBuilder.put(SearchConsts.FILTER_EXT_NOTES, FIELD_DESCRIPTION);
    extSearchMapBuilder.put(SearchConsts.FILTER_EXT_PUBLISHER, FIELD_METADATA_PUBLISHER_NAME);
    extSearchMapBuilder.put(SearchConsts.FILTER_EXT_MAINTAINER, FIELD_METADATA_MAINTAINER);
    EXT_SEARCH_MAP = Collections.unmodifiableMap(extSearchMapBuilder);

    Map<String, String> boolFacetMapBuilder = new HashMap<>();
    boolFacetMapBuilder.put(SearchConsts.FACET_HAS_OPEN, HAS_OPEN);
    boolFacetMapBuilder.put(SearchConsts.FACET_HAS_CLOSED, HAS_CLOSED);
    boolFacetMapBuilder.put(SearchConsts.FACET_HAS_DATA_SERVICE, HAS_DATASERVICE);
    BOOL_FACET_MAP = Collections.unmodifiableMap(boolFacetMapBuilder);
  }
}
