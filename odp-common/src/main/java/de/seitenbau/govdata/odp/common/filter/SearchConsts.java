package de.seitenbau.govdata.odp.common.filter;

import java.util.Arrays;
import java.util.List;
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
  /**
   * Configuration parameter name for the Liferay index name in Elasticsearch.
   */
  public static final String PARAM_ELASTICSEARCH_LIFERAY_INDEX_NAME = "elasticsearch.liferay.index.name";

  /**
   * Liferay index name in Elasticsearch.
   */
  public static final String CONFIG_ELASTICSEARCH_LIFERAY_INDEX_NAME = "${"
      + PARAM_ELASTICSEARCH_LIFERAY_INDEX_NAME + "}";

  /**
   * Configuration parameter name for the index name with the showcases in Elasticsearch.
   */
  public static final String PARAM_ELASTICSEARCH_SHOWCASES_INDEX_NAME =
      "elasticsearch.search.index.showcases";

  /**
   * Showcases index name in Elasticsearch.
   */
  public static final String CONFIG_ELASTICSEARCH_SHOWCASES_INDEX_NAME = "${"
      + PARAM_ELASTICSEARCH_SHOWCASES_INDEX_NAME + "}";

  /**
   * Configuration parameter name for the index names in Elasticsearch to search in.
   */
  public static final String PARAM_ELASTICSEARCH_SEARCH_INDEX_NAMES = "elasticsearch.search.index.names";

  /**
   * Search index names in Elasticsearch.
   */
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
   * Source type in the search index.
   */
  public static final String SOURCE_SHOWCASE = "showcase";

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
   * Filter type for showcases.
   */
  public static final String TYPE_SHOWCASE = "showcase";

  /**
   * Types that can contain CKAN-Datasets
   */
  public static final String[] CKAN_TYPES;

  /**
   * All ckan filter types with the filter type all.
   */
  public static final String[] CKAN_TYPES_ALL;

  /**
   * Types that can contain spatial-data.
   */
  public static final String[] SPATIAL_TYPES;

  /**
   * All filter types that can contain spatial data.
   */
  public static final String[] SPATIAL_TYPES_ALL;

  /**
   * Valid filter types.
   */
  public static final Set<String> VALID_FILTER_TYPES;

  /**
   * Valid filter types ordered without type all.
   */
  public static final List<String> VALID_FILTER_TYPES_WITHOUT_ALL_ORDERED;

  static
  {
    CKAN_TYPES = new String[] {TYPE_DATASET};
    CKAN_TYPES_ALL = new String[] {TYPE_ALL, TYPE_DATASET};
    SPATIAL_TYPES = new String[] {TYPE_DATASET, TYPE_SHOWCASE};
    SPATIAL_TYPES_ALL = new String[] {TYPE_ALL, TYPE_DATASET, TYPE_SHOWCASE};
    VALID_FILTER_TYPES = Stream.of(CKAN_TYPES_ALL, new String[] {TYPE_ARTICLE, TYPE_BLOG, TYPE_SHOWCASE})
        .flatMap(Stream::of)
        .collect(Collectors.toSet());
    VALID_FILTER_TYPES_WITHOUT_ALL_ORDERED = Arrays.asList(SearchConsts.TYPE_DATASET,
        SearchConsts.TYPE_SHOWCASE, SearchConsts.TYPE_ARTICLE, SearchConsts.TYPE_BLOG);
  }

  /**
   * Filter key for the type.
   */
  public static final String FILTER_KEY_TYPE = "type";

  /**
   * Facet key for the groups.
   */
  public static final String FACET_GROUPS = "groups";

  /**
   * Facet key for the tags.
   */
  public static final String FACET_TAGS = "tags";

  /**
   * Facet key for the format.
   */
  public static final String FACET_FORMAT = "format";

  /**
   * Facet key for the openness.
   */
  public static final String FACET_OPENNESS = "openness";

  /**
   * Facet key for has open license.
   */
  public static final String FACET_HAS_OPEN = "has_open";

  /**
   * Facet key for has closed license.
   */
  public static final String FACET_HAS_CLOSED = "has_closed";

  /**
   * Facet key for the license.
   */
  public static final String FACET_LICENCE = "licence";

  /**
   * Facet key for the sourceportal.
   */
  public static final String FACET_SOURCEPORTAL = "sourceportal";

  /**
   * Facet key for the showcase type.
   */
  public static final String FACET_SHOWCASE_TYPE = "showcase_types";

  /**
   * Facet key for the showcase platform.
   */
  public static final String FACET_PLATFORMS = "platforms";

  /**
   * Facet key for the state.
   */
  public static final String FACET_STATE = "state";

  /**
   * Facet key for the dataservice.
   */
  public static final String FACET_DATASERVICE = "dataservice";

  /**
   * Facet filter value for has data service.
   */
  public static final String FACET_HAS_DATA_SERVICE = "has_data_service";

  /**
   * Facet key for the high value dataset.
   */
  public static final String FACET_HIGH_VALUE_DATASET = "hvd";

  /**
   * Facet filter value for the high value dataset.
   */
  public static final String FACET_IS_HIGH_VALUE_DATASET = "is_hvd";

  // fields used in extended search
  /**
   * Extended search field "title".
   */
  public static final String FILTER_EXT_TITLE = "title";

  /**
   * Extended search field "publisher".
   */
  public static final String FILTER_EXT_PUBLISHER = "publisher";

  /**
   * Extended search field "maintainer".
   */
  public static final String FILTER_EXT_MAINTAINER = "maintainer";

  /**
   * Extended search field "notes".
   */
  public static final String FILTER_EXT_NOTES = "notes";

  /**
   * Friendly URL mapping for the search result page.
   */
  public static final String FRIENDLY_URL_MAPPING_RESULTS = "searchresult";

  /**
   * Friendly URL mapping for the details page.
   */
  public static final String FRIENDLY_URL_MAPPING_DETAILS = "details";

  /**
   * Ascending sort.
   */
  public static final String SEARCH_ASCENDING = "asc";

  /**
   * Descending sort.
   */
  public static final String SEARCH_DESCENDING = "desc";

  /**
   * Field for the organization in the metadata.
   */
  public static final String FIELD_OWNER_ORG = "metadata.owner_org";

  /**
   * Field for the metric data latest date group.
   */
  public static final String METRIC_GROUP_DATA_NAME = "group_latest_date";

  /**
   * Field for the metric data latest date hit name.
   */
  public static final String METRICS_TOP_HIT_NAME = "newest_data";

}
