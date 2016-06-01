package de.seitenbau.govdata.filter;

/**
 * Enthält alle Konstanten für gd-search.
 * 
 * @author rnoerenberg
 *
 */
public abstract class SearchConsts
{
  public static final String PARAM_ELASTICSEARCH_LIFERAY_INDEX_NAME = "elasticsearch.liferay.index.name";

  public static final String CONFIG_ELASTICSEARCH_LIFERAY_INDEX_NAME = "${"
      + PARAM_ELASTICSEARCH_LIFERAY_INDEX_NAME + "}";

  public static final String PARAM_ELASTICSEARCH_SEARCH_INDEX_NAMES = "elasticsearch.search.index.names";

  public static final String CONFIG_ELASTICSEARCH_SEARCH_INDEX_NAMES = "${"
      + PARAM_ELASTICSEARCH_SEARCH_INDEX_NAMES + "}";

  public final static String SOURCE_CKAN = "ckan";

  public final static String SOURCE_PORTAL = "portal";

  public final static String TYPE_ALL = "all";

  public final static String TYPE_ARTICLE = "article";

  public final static String TYPE_BLOG = "blog";

  public final static String TYPE_DATASET = "dataset";

  public final static String TYPE_DATENSATZ = "datensatz";

  public final static String TYPE_DOCUMENT = "dokument";

  public final static String TYPE_APP = "app";
  
  // Types that can contain CKAN-Datasets
  public final static String[] CKAN_TYPES;
  public final static String[] CKAN_TYPES_ALL;
  static {
    CKAN_TYPES = new String[] {TYPE_DATENSATZ, TYPE_DOCUMENT, TYPE_APP};
    CKAN_TYPES_ALL = new String[] {TYPE_ALL, TYPE_DATENSATZ, TYPE_DOCUMENT, TYPE_APP};
  }

  public final static String FILTER_KEY_TYPE = "type";
  
  public final static String FACET_GROUPS = "groups";
  public final static String FACET_TAGS = "tags";
  public final static String FACET_FORMAT = "format";
  public final static String FACET_ISOPEN = "isopen";
  public static final String FACET_ISRESTRICTED = "isrestricted"; // interally a facet, but only partially...
  public final static String FACET_LICENCE = "licence";
  public static final String FACET_SOURCEPORTAL = "sourceportal";
  
  // fields used in extended search
  public final static String FILTER_EXT_TITLE = "title";
  public final static String FILTER_EXT_AUTHOR = "author";
  public final static String FILTER_EXT_MAINTAINER = "maintainer";
  public final static String FILTER_EXT_NOTES = "notes";
  
  public final static String ISOPEN_OPEN = "free";
  public final static String ISOPEN_RESTRICTED = "restricted";

  public static final String FRIENDLY_URL_MAPPING_RESULTS = "searchresult";

  public static final String FRIENDLY_URL_MAPPING_DETAILS = "details";
  
  public static final String SEARCH_ASCENDING = "asc";
  public static final String SEARCH_DESCENDING = "desc";

  public final static String FIELD_OWNER_ORG = "metadata.owner_org";

  
}
