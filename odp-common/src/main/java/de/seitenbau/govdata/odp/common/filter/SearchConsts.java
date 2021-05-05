package de.seitenbau.govdata.odp.common.filter;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Enthält alle Konstanten für gd-search.
 *
 * @author rnoerenberg
 */
public abstract class SearchConsts
{
  public static final String PARAM_ELASTICSEARCH_LIFERAY_INDEX_NAME = "elasticsearch.liferay.index.name";

  public static final String CONFIG_ELASTICSEARCH_LIFERAY_INDEX_NAME = "${"
      + PARAM_ELASTICSEARCH_LIFERAY_INDEX_NAME + "}";

  public static final String PARAM_ELASTICSEARCH_SEARCH_INDEX_NAMES = "elasticsearch.search.index.names";

  public static final String CONFIG_ELASTICSEARCH_SEARCH_INDEX_NAMES = "${"
      + PARAM_ELASTICSEARCH_SEARCH_INDEX_NAMES + "}";

  /**
   * Source type in the search index.
   */
  public static final String SOURCE_CKAN = "ckan";

  /**
   * Source type in the search index.
   */
  public static final String SOURCE_PORTAL = "portal";

  /**
   * Filter type for all types.
   */
  public static final String TYPE_ALL = "all";

  /**
   * Filter type for standard CMS articles.
   */
  public static final String TYPE_ARTICLE = "article";

  /**
   * Filter type for blog articles.
   */
  public static final String TYPE_BLOG = "blog";

  /**
   * Filter type for datasets.
   */
  public static final String TYPE_DATASET = "dataset";

  /**
   * Types that can contain CKAN-Datasets
   */
  public static final String[] CKAN_TYPES;

  /**
   * All ckan filter types with the filter type all.
   */
  public static final String[] CKAN_TYPES_ALL;

  /**
   * Valid filter types.
   */
  public static final Set<String> VALID_FILTER_TYPES;

  static
  {
    CKAN_TYPES = new String[] {TYPE_DATASET};
    CKAN_TYPES_ALL = new String[] {TYPE_ALL, TYPE_DATASET};
    VALID_FILTER_TYPES = Stream.of(CKAN_TYPES_ALL, new String[] {TYPE_ARTICLE, TYPE_BLOG})
        .flatMap(Stream::of)
        .collect(Collectors.toSet());
  }

  public static final String FILTER_KEY_TYPE = "type";

  public static final String FACET_GROUPS = "groups";

  public static final String FACET_TAGS = "tags";

  public static final String FACET_FORMAT = "format";

  public static final String FACET_OPENNESS = "openness";

  public static final String FACET_HAS_OPEN = "has_open";

  public static final String FACET_HAS_CLOSED = "has_closed";

  public static final String FACET_LICENCE = "licence";

  public static final String FACET_SOURCEPORTAL = "sourceportal";

  // fields used in extended search
  public static final String FILTER_EXT_TITLE = "title";

  public static final String FILTER_EXT_PUBLISHER = "publisher";

  public static final String FILTER_EXT_MAINTAINER = "maintainer";

  public static final String FILTER_EXT_NOTES = "notes";

  public static final String FRIENDLY_URL_MAPPING_RESULTS = "searchresult";

  public static final String FRIENDLY_URL_MAPPING_DETAILS = "details";

  public static final String SEARCH_ASCENDING = "asc";

  public static final String SEARCH_DESCENDING = "desc";

  public static final String FIELD_OWNER_ORG = "metadata.owner_org";

  public static final String METRIC_GROUP_DATA_NAME = "group_latest_date";

  public static final String METRICS_TOP_HIT_NAME = "newest_data";

}
