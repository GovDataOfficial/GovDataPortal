package de.seitenbau.govdata.odp.registry.ckan.json;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import de.seitenbau.govdata.odp.registry.date.DateDeserializer;
import lombok.Data;

/**
 * Class for a CKAN dataset.
 * 
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class MetadataBean implements Serializable
{
  // CHECKSTYLE:OFF
  private static final long serialVersionUID = -2490565832054585931L;

  @JsonProperty
  private String id;

  @JsonProperty
  private String name;

  @JsonProperty
  private String title;

  @JsonProperty
  private String maintainer;

  @JsonProperty
  private String maintainer_email;

  /**
   * CKAN-intern managing property
   */
  @JsonProperty
  @JsonDeserialize(using = DateDeserializer.class)
  private Date metadata_created;

  /**
   * CKAN-intern managing property
   */
  @JsonProperty
  @JsonDeserialize(using = DateDeserializer.class)
  private Date metadata_modified;

  /**
   * CKAN-intern managing property
   */
  @JsonProperty
  private String creator_user_id;

  /**
   * CKAN-added property
   */
  @JsonProperty
  private boolean isopen;

  @JsonProperty
  private String author;

  @JsonProperty
  private String author_email;

  @JsonProperty
  private String notes;

  /**
   * CKAN-intern managing property
   */
  @JsonProperty
  private String state;

  @JsonProperty
  private String license_id;

  /**
   * CKAN-added property
   */
  @JsonProperty
  private String license_title;

  /**
   * CKAN-added property
   */
  @JsonProperty
  private String license_url;

  @JsonProperty
  private String type;

  @JsonProperty
  private String url;

  /**
   * Contains the following fields:
   * - contributorID (json-encoded list)
   * - contact details for author*, maintainer*, publisher, contributor, originator (*native name and email)
   * - qualityProcessURI (String)
   * - access_rights (String)
   * - conforms_to (json-encoded list)
   * - documentation (String)
   * - frequency (enum)
   * - has_version (list)
   * - is_version_of (list)
   * - language (list)
   * - alternate_identifier (list)
   * - provenance (string)
   * - used_datasets (list)
   * - issued (date)
   * - modified (date)
   * - spatial (string / geojson - will not be decoded here)
   * - temporal_start (date)
   * - temporal_end (date)
   * - version (string)
   * - version_notes (string)
   * - politicalGeocodingLevelURI (enum)
   * - politicalGeocodingURI (string)
   * - geocodingText (string)
   * - legalbasisText (string)
   */
  @JsonProperty
  private List<ExtraBean> extras;

  @JsonProperty
  private List<TagBean> tags;

  @JsonProperty
  private List<ResourceBean> resources;

  @JsonProperty
  private List<GroupBean> groups;

  @JsonProperty
  private String owner_org;

  /**
   * CKAN-intern managing property
   */
  @JsonProperty(value = "private")
  private boolean isPrivate;

  public Date getMetadata_modified()
  {
    return metadata_modified == null ? null : (Date) metadata_modified.clone();
  }

  @JsonIgnore
  public List<ExtraBean> getExtras()
  {
    if (extras == null)
    {
      extras = new ArrayList<>();
    }
    return extras;
  }

  @JsonIgnore
  public List<TagBean> getTags()
  {
    if (tags == null)
    {
      tags = new ArrayList<>();
    }
    return tags;
  }

  public void setTags(List<TagBean> tags)
  {
    this.tags = tags;
  }

  public List<ResourceBean> getResources()
  {
    if (resources == null)
    {
      resources = new ArrayList<>();
    }
    return resources;
  }

  public void setResources(List<ResourceBean> resources)
  {
    this.resources = resources;
  }

  @JsonIgnore
  public List<GroupBean> getGroups()
  {
    if (groups == null)
    {
      groups = new ArrayList<>();
    }
    return groups;
  }
}
