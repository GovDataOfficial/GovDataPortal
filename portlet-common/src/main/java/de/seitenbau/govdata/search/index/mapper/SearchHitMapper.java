package de.seitenbau.govdata.search.index.mapper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.elasticsearch.common.lang3.StringUtils;
import org.elasticsearch.search.SearchHit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import de.seitenbau.govdata.date.DateUtil;
import de.seitenbau.govdata.search.index.PortalIndexConstants;
import de.seitenbau.govdata.search.index.model.HitDto;
import de.seitenbau.govdata.search.index.model.ResourceDto;

/**
 * Mappt einen Eintrag im Elasticsearch-Index zu einem Bean für die Darstellung in der
 * Suchergebnisseite.
 * 
 * @author rnoerenberg
 *
 */
@Component
public class SearchHitMapper
{
  private static final Logger logger = LoggerFactory.getLogger(SearchHitMapper.class);

  /**
   * Mappt ein Elasticsearch-Objekt {@link SearchHit} zu einem Datenobjekt {@link HitDto}.
   * 
   * @param searchHit das zu mappende Elasticsearch-Objekt.
   * @return das gemappte Datenobjekt.
   */
  @SuppressWarnings("unchecked")
  public HitDto mapToHitDto(SearchHit searchHit)
  {
    final String method = "mapToHitDto() : ";
    logger.trace(method + "Start");

    Map<String, Object> data = searchHit.getSource();
    Map<String, Object> metadata = (Map<String, Object>) data.get("metadata");

    String lastModified;
    Date lastModifiedDate;
    if (StringUtils.equals(searchHit.getType(), PortalIndexConstants.INDEX_TYPE_PORTAL))
    {
      lastModified = getFieldValueString(metadata, "modified", "");
      lastModifiedDate = DateUtil.parseDateString(lastModified);
    }
    else
    {
      lastModified = getFieldValueString(metadata, "dct_modified_fallback_ckan", "");
      lastModifiedDate = DateUtil.parseDateString(lastModified);
    }

    String temporalCoverageFrom = getFieldValueString(metadata, "temporal_start", null);
    Date temporalCoverageFromDate = null;
    if (temporalCoverageFrom != null)
    {
      temporalCoverageFromDate = DateUtil.parseDateString(temporalCoverageFrom);
    }
    String temporalCoverageTo = getFieldValueString(metadata, "temporal_end", null);
    Date temporalCoverageToDate = null;
    if (temporalCoverageTo != null)
    {
      temporalCoverageToDate = DateUtil.parseDateString(temporalCoverageTo);
    }

    // find the correct publisher to use
    String[] contactNameAndEmail = extractContact(metadata);

    HitDto mappedHit = HitDto.builder()
        .id(searchHit.getId())
        .name(getFieldValueString(metadata, "name", searchHit.getId()))
        .type(getFieldValueString(metadata, "type", ""))
        .title(getFieldValueString(data, "title", ""))
        .content(getFieldValueString(data, "preamble", ""))
        .lastModified(lastModifiedDate)
        .temporalCoverageFrom(temporalCoverageFromDate)
        .temporalCoverageTo(temporalCoverageToDate)
        .contact(contactNameAndEmail[0])
        .contactEmail(contactNameAndEmail[1])
        .groups(getFieldValueAsStringList(metadata, "groups"))
        .articleId(getFieldValueString(metadata, "articleId", ""))
        .groupId(Long.parseLong(getFieldValueString(metadata, "groupId", "0")))
        .entryClassPK(getFieldValueString(metadata, "entryClassPK", ""))
        .portletId(getFieldValueString(metadata, "portletId", ""))
        .hasOpen((Boolean) metadata.get("has_open"))
        .hasClosed((Boolean) metadata.get("has_closed"))
        .ownerOrg(getFieldValueString(metadata, "owner_org", ""))
        .resources(extractResources(metadata.get("resources")))
        .build();

    logger.trace(method + "End");
    return mappedHit;
  }

  @SuppressWarnings("unchecked")
  private String[] extractContact(Map<String, Object> metadata)
  {
    List<Map<String, String>> extras = (List<Map<String, String>>) metadata.get("extras");
    // find the best suited contact to be the contact for this metadata
    String name;

    // author from extras
    name = getValueFromExtras(extras, "author_name");
    if (name != null)
    {
      return new String[] {name, getValueFromExtras(extras, "author_email")};
    }

    // author from metadata
    name = getFieldValueString(metadata, "author", null);
    if (name != null)
    {
      return new String[] {name, getFieldValueString(metadata, "author_email", null)};
    }

    // publisher from extras
    name = getValueFromExtras(extras, "publisher_name");
    if (name != null)
    {
      return new String[] {name, getValueFromExtras(extras, "publisher_email")};
    }

    // maintainer from extras
    name = getValueFromExtras(extras, "maintainer_name");
    if (name != null)
    {
      return new String[] {name, getValueFromExtras(extras, "maintainer_email")};
    }

    // maintainer from metadata
    return new String[] {getFieldValueString(metadata, "maintainer", null),
        getFieldValueString(metadata, "maintainer_email", null)};
  }

  private String getValueFromExtras(List<Map<String, String>> extras, String key)
  {
    if (extras != null)
    {
      for (Map<String, String> extra : extras)
      {
        if (extra.get("key").equals(key))
        {
          return extra.get("value");
        }
      }
    }
    return null;
  }

  private String getFieldValueString(Map<?, ?> data, String name, String defaultValue)
  {
    String result = defaultValue;
    if (data != null)
    {
      Object field = data.get(name);
      if (field instanceof String)
      {
        result = (String) field;
      }
    }
    return result;
  }

  @SuppressWarnings("unchecked")
  private List<String> getFieldValueAsStringList(Map<String, Object> data, String name)
  {
    List<String> stringArray = (List<String>) data.get(name);
    if (null != stringArray)
    {
      return stringArray;
    }
    return new ArrayList<>();
  }

  private List<ResourceDto> extractResources(Object resourcesListAsObject)
  {
    List<ResourceDto> result = new ArrayList<>();
    if (resourcesListAsObject != null)
    {
      List<?> resources = (List<?>) resourcesListAsObject;
      for (Object resObject : resources)
      {
        if (resObject instanceof Map)
        {
          Map<?, ?> resMap = (Map<?, ?>) resObject;
          ResourceDto resource =
              ResourceDto.builder()
                  .description(getFieldValueString(resMap, "description", ""))
                  .format(getFieldValueString(resMap, "format", ""))
                  .hash(getFieldValueString(resMap, "hash", ""))
                  .id(getFieldValueString(resMap, "id", ""))
                  .language(getFieldValueString(resMap, "language", ""))
                  .type(getFieldValueString(resMap, "type", ""))
                  .url(getFieldValueString(resMap, "url", ""))
                  .license(getFieldValueString(resMap, "license", ""))
                  .build();
          result.add(resource);
        }
      }
    }
    return result;
  }
}
