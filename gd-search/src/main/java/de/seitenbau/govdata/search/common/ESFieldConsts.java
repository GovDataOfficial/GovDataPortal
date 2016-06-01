package de.seitenbau.govdata.search.common;

import java.util.HashMap;

import de.seitenbau.govdata.filter.SearchConsts;

/**
 * This keeps all constants related to elastic search field identifiers.
 * @author tscheffler
 */
public class ESFieldConsts
{
  public static final String BOUNDINGBOX = "metadata.boundingbox";
  
  public static final String ISOPEN = "metadata.isopen";
  
  public static final String TEMPORAL_COVERAGE_FROM = "metadata.temporal_coverage_from";
  
  public static final String TEMPORAL_COVERAGE_TO = "metadata.temporal_coverage_to";
  
  public static final String FIELD_TAGS_SEARCH = "tags";
  
  public static final String FIELD_TAGS_FACET = "tags.facet";

  public static final String FIELD_METADATA_RESOURCES_FORMAT = "metadata.resources.format.facet";
  
  public static final String METADATA_ISOPEN = "metadata.isopen";
  
  public static final HashMap<String, String> facetMap = new HashMap<>();
  public static final HashMap<String, String> extSearchMap = new HashMap<>();
  static {
    facetMap.put(SearchConsts.FACET_GROUPS, "metadata.groups");
    facetMap.put(SearchConsts.FACET_TAGS, FIELD_TAGS_FACET);
    facetMap.put(SearchConsts.FACET_FORMAT, FIELD_METADATA_RESOURCES_FORMAT);
    facetMap.put(SearchConsts.FACET_ISOPEN, METADATA_ISOPEN);
    facetMap.put(SearchConsts.FACET_LICENCE, "license_id");
    facetMap.put(SearchConsts.FACET_SOURCEPORTAL, SearchConsts.FIELD_OWNER_ORG);
    
    extSearchMap.put(SearchConsts.FILTER_EXT_TITLE, "title");
    extSearchMap.put(SearchConsts.FILTER_EXT_NOTES, "preamble");
    extSearchMap.put(SearchConsts.FILTER_EXT_AUTHOR, "metadata.author");
    extSearchMap.put(SearchConsts.FILTER_EXT_MAINTAINER, "metadata.maintainer");
  }
}
