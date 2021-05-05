package de.seitenbau.govdata.search.common;

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
   * Key for facet field format in search index.
   */
  public static final String FIELD_METADATA_RESOURCES_FORMAT = "metadata.resources.format.facet";

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
  public static final Map<String, String> FACET_MAP = new HashMap<>();

  /**
   * Map with available extended search facets.
   */
  public static final Map<String, String> EXT_SEARCH_MAP = new HashMap<>();

  /**
   * Map with available boolean facets.
   */
  public static final Map<String, String> BOOL_FACET_MAP = new HashMap<>();

  static
  {
    FACET_MAP.put(SearchConsts.FACET_GROUPS, "metadata.groups");
    FACET_MAP.put(SearchConsts.FACET_TAGS, FIELD_TAGS_FACET);
    FACET_MAP.put(SearchConsts.FACET_FORMAT, FIELD_METADATA_RESOURCES_FORMAT);
    FACET_MAP.put(SearchConsts.FACET_LICENCE, "resources_licenses");
    FACET_MAP.put(SearchConsts.FACET_SOURCEPORTAL, SearchConsts.FIELD_OWNER_ORG);

    EXT_SEARCH_MAP.put(SearchConsts.FILTER_EXT_TITLE, "title");
    EXT_SEARCH_MAP.put(SearchConsts.FILTER_EXT_NOTES, "preamble");
    EXT_SEARCH_MAP.put(SearchConsts.FILTER_EXT_PUBLISHER, "metadata.publisher_name");
    EXT_SEARCH_MAP.put(SearchConsts.FILTER_EXT_MAINTAINER, "metadata.maintainer");

    BOOL_FACET_MAP.put(SearchConsts.FACET_HAS_OPEN, HAS_OPEN);
    BOOL_FACET_MAP.put(SearchConsts.FACET_HAS_CLOSED, HAS_CLOSED);
  }
}
