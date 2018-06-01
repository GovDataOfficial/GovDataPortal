/**
 * Copyright (c) 2012, 2013 Fraunhofer Institute FOKUS | 2017 SEITENBAU GmbH
 * <p>
 * This file is part of Open Data Platform.
 * <p>
 * Open Data Platform is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * Open Data Plaform is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Affero General Public License
 * along with Open Data Platform.  If not, see <http://www.gnu.org/licenses/agpl-3.0>.
 */

package de.seitenbau.govdata.odp.registry.ckan.impl;

import static de.seitenbau.govdata.odp.registry.ckan.Constants.JSON_DATETIME_PATTERN;
import static de.seitenbau.govdata.odp.registry.ckan.Constants.JSON_FIELD_CATEGORIES;
import static de.seitenbau.govdata.odp.registry.ckan.Constants.JSON_FIELD_COVERAGEFROM;
import static de.seitenbau.govdata.odp.registry.ckan.Constants.JSON_FIELD_COVERAGETO;
import static de.seitenbau.govdata.odp.registry.ckan.Constants.JSON_FIELD_EXTRAS;
import static de.seitenbau.govdata.odp.registry.ckan.Constants.JSON_FIELD_MODIFIED;
import static de.seitenbau.govdata.odp.registry.ckan.Constants.JSON_FIELD_PUBLISHED;
import static de.seitenbau.govdata.odp.registry.ckan.Constants.JSON_FIELD_RESOURCES;
import static de.seitenbau.govdata.odp.registry.ckan.Constants.JSON_FIELD_TAGS;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

import de.seitenbau.govdata.date.DateUtil;
import de.seitenbau.govdata.odp.registry.ODRClient;
import de.seitenbau.govdata.odp.registry.ckan.Constants;
import de.seitenbau.govdata.odp.registry.ckan.ODRClientImpl;
import de.seitenbau.govdata.odp.registry.ckan.Util;
import de.seitenbau.govdata.odp.registry.ckan.json.ExtraBean;
import de.seitenbau.govdata.odp.registry.ckan.json.GroupBean;
import de.seitenbau.govdata.odp.registry.ckan.json.MetadataBean;
import de.seitenbau.govdata.odp.registry.ckan.json.ResourceBean;
import de.seitenbau.govdata.odp.registry.ckan.json.TagBean;
import de.seitenbau.govdata.odp.registry.model.Category;
import de.seitenbau.govdata.odp.registry.model.Contact;
import de.seitenbau.govdata.odp.registry.model.Licence;
import de.seitenbau.govdata.odp.registry.model.Metadata;
import de.seitenbau.govdata.odp.registry.model.MetadataEnumType;
import de.seitenbau.govdata.odp.registry.model.MetadataListExtraFields;
import de.seitenbau.govdata.odp.registry.model.MetadataStringExtraFields;
import de.seitenbau.govdata.odp.registry.model.Resource;
import de.seitenbau.govdata.odp.registry.model.RoleEnumType;
import de.seitenbau.govdata.odp.registry.model.Tag;

/**
 * Provides logic to access and manipulate metadata.
 */
@Slf4j
public class MetadataImpl implements Metadata, Serializable
{

  private static final long serialVersionUID = -374425005119368545L;

  private static final ObjectMapper OM = new ObjectMapper();

  private MetadataBean metadata;

  private List<Resource> resources;

  private List<Tag> tags;

  private List<Category> categories;

  private Map<String, ExtraBean> extras = new HashMap<>();

  private double averageRating;

  private int ratingCount;

  private boolean ratingfetched = false;

  /** Stores the odrClient client for retrieving additional information like licenses. */
  private transient ODRClient odrClient;

  private boolean resoucesModified;

  private DateFormat formatter;

  /**
   * Concept for Extra-Beans: Use this Impl as facade and use the beans to back the data.
   */
  public MetadataImpl(MetadataBean metadata, ODRClient odrClient)
  {
    this.odrClient = odrClient;
    this.metadata = metadata;
    this.metadata.setType(MetadataEnumType.DATASET.toField());

    formatter = new SimpleDateFormat(JSON_DATETIME_PATTERN);

    // fill map for faster access
    for (ExtraBean bean : metadata.getExtras())
    {
      extras.put(bean.getKey(), bean);
    }

    // overwrite publisher with creator values, if not already present
    if (metadata.getAuthor() != null && !metadata.getAuthor().isEmpty())
    {
      addContactIfNotPresent(RoleEnumType.PUBLISHER, metadata.getAuthor(), metadata.getAuthor_email());
    }
  }

  private void addContactIfNotPresent(RoleEnumType type, String name, String email)
  {
    Contact contact = getContact(type);
    if (!contact.exists())
    {
      contact.setName(name);
      contact.setEmail(email);
    }
  }

  private Date toDate(String value)
  {
    if (value != null)
    {
      return DateUtil.parseDateString(value);
    }
    return null;
  }

  @Override
  public String getId()
  {
    return metadata.getId();
  }

  @Override
  public String getName()
  {
    return metadata.getName();
  }

  @Override
  public void setName(String name)
  {
    metadata.setName(name);
  }

  @Override
  public String getTitle()
  {
    return metadata.getTitle();
  }

  @Override
  public void setTitle(String title)
  {
    metadata.setTitle(title);
  }

  @Override
  public String getUrl()
  {
    return metadata.getUrl();
  }

  @Override
  public void setUrl(String url)
  {
    metadata.setUrl(url);
  }

  @Override
  public String getNotes()
  {
    return metadata.getNotes();
  }

  @Override
  public void setNotes(String notes)
  {
    metadata.setNotes(notes);
  }

  @Override
  public Date getMetadataModified()
  {
    return metadata.getMetadata_modified();
  }

  @Override
  public Date getModified()
  {
    return toDate(getExtra(Constants.JSON_FIELD_MODIFIED));
  }

  @Override
  public void setModified(Date modified)
  {
    setExtraDate(JSON_FIELD_MODIFIED, modified);
  }

  @Override
  public Date getPublished()
  {
    return toDate(getExtra(Constants.JSON_FIELD_PUBLISHED));
  }

  @Override
  public void setPublished(Date published)
  {
    setExtraDate(JSON_FIELD_PUBLISHED, published);
  }

  @Override
  public boolean isOpen()
  {
    // aggregate openness from resources - open open resource is sufficient
    return getResources().stream()
        .filter(resource -> Objects.nonNull(resource.getLicense()))
        .anyMatch(resource -> resource.getLicense().isOpen());
  }

  @Override
  public Set<Licence> getResourcesLicenses()
  {
    return getResources().stream()
        .filter(resource -> resource.getLicense() != null)
        .map(Resource::getLicense)
        .collect(Collectors.toSet());
  }

  @Override
  public List<Contact> getContacts()
  {
    ArrayList<Contact> contacts = new ArrayList<>();

    for (RoleEnumType role : RoleEnumType.values())
    {
      Contact contact = getContact(role);
      if (contact.exists())
      {
        contacts.add(contact);
      }
    }

    return contacts;
  }

  @Override
  public Contact getContact(RoleEnumType role)
  {
    return new ContactImpl(this, role);
  }

  @Override
  public List<Tag> getTags()
  {
    if (tags == null)
    {
      tags = new ArrayList<>();
      for (TagBean bean : metadata.getTags())
      {
        tags.add(new TagImpl(bean));
      }
    }
    return tags;
  }

  @Override
  public Tag newTag(String name)
  {
    TagBean bean = new TagBean();
    bean.setName(name);
    bean.setDisplay_name(name);
    Tag tag = new TagImpl(bean);
    getTags().add(tag);
    return tag;
  }

  @Override
  public List<Resource> getResources()
  {
    if (resources == null)
    {
      resources = new ArrayList<>();
      for (ResourceBean bean : metadata.getResources())
      {
        resources.add(new ResourceImpl(this.odrClient, bean));
      }
    }
    return resources;
  }

  @Override
  public void cleanResources()
  {
    getResources().clear();
  }

  @Override
  public void removeResource(Resource r)
  {
    setResourcesModified(true);
    getResources().remove(r);
    metadata.getResources().remove(((ResourceImpl) r).getBean());
  }

  @Override
  public Resource newResource()
  {
    Resource resource = new ResourceImpl(this.odrClient, new ResourceBean());
    getResources().add(resource);
    return resource;
  }

  @Override
  public List<Category> getCategories()
  {
    if (categories == null)
    {
      categories = new ArrayList<>();
      // there are groups available, initialize from these groups
      if (metadata.getGroups() != null)
      {
        for (GroupBean bean : metadata.getGroups())
        {
          categories.add(new CategoryImpl(bean));
        }
      }
    }
    return categories;
  }

  @Override
  public Category newCategory()
  {
    Category category = new CategoryImpl(new GroupBean());
    getCategories().add(category);
    return category;
  }

  @Override
  public MetadataEnumType getType()
  {
    return MetadataEnumType.fromField(metadata.getType());
  }

  @Override
  public Date getTemporalCoverageFrom()
  {
    return toDate(getExtra(JSON_FIELD_COVERAGEFROM));
  }

  @Override
  public void setTemporalCoverageFrom(Date temporalCoverageFrom)
  {
    setExtraDate(JSON_FIELD_COVERAGEFROM, temporalCoverageFrom);
  }

  /**
   * Sets a date field. If the date is null, the field will be removed.
   * @param name name of the field
   * @param date date to set
   */
  private void setExtraDate(String name, Date date)
  {
    if (date != null)
    {
      setExtra(name, formatter.format(date));
    }
    else
    {
      setExtra(name, null);
    }
  }

  @Override
  public Date getTemporalCoverageTo()
  {
    return toDate(getExtra(JSON_FIELD_COVERAGETO));
  }

  @Override
  public void setTemporalCoverageTo(Date temporalCoverageTo)
  {
    setExtraDate(JSON_FIELD_COVERAGETO, temporalCoverageTo);
  }

  public boolean isNew()
  {
    return metadata.getId() == null || metadata.getId().isEmpty();
  }

  /**
   * Writes this metadata to a JsonNode which can be persisted.
   * @param clearResources if true, all resources are removed
   * @return JsonNode containing the metadata
   */
  public JsonNode write(boolean clearResources)
  {

    // don't write original resources back to the metadata
    metadata.getResources().clear();

    // serialize metadata as base for further enhancements down below.
    JsonNode node = ODRClientImpl.convert(metadata);

    // Add resources from our Implementation
    JsonNode resources = node.get("resources");
    for (Resource resource : getResources())
    {
      ((ArrayNode) resources).add(((ResourceImpl) resource).write());
    }

    ArrayNode groups = OM.createArrayNode();
    getCategories().forEach(category -> {
      ObjectNode group = OM.createObjectNode();
      group.put("name", category.getName());
      groups.add(group);
    });
    if (groups.size() > 0)
    {
      ((ObjectNode) node).set(JSON_FIELD_CATEGORIES, groups);
    }
    /* msg 10.04.2014 */
    if (clearResources)
    {
      ArrayNode resourcesNode = OM.createArrayNode();
      ((ObjectNode) node).set(JSON_FIELD_RESOURCES, resourcesNode);
    }

    ArrayNode tagsNode = OM.createArrayNode();
    for (Tag tag : getTags())
    {
      ObjectNode tagNode = OM.createObjectNode();
      tagNode.put("name", tag.getName());
      tagsNode.add(tagNode);
    }
    if (tagsNode.size() > 0)
    {
      ((ObjectNode) node).set(JSON_FIELD_TAGS, tagsNode);
    }

    ArrayNode extrasNode = OM.createArrayNode();

    // other extras
    if (extras != null && !extras.isEmpty())
    {
      for (ExtraBean bean : extras.values())
      {
        addExtras(extrasNode, bean.getKey(), bean.getValue());
      }
    }

    if (extrasNode.size() > 0)
    {
      ((ObjectNode) node).set(JSON_FIELD_EXTRAS, extrasNode);
    }

    return node;
  }

  private void addExtras(ArrayNode extras, String key, JsonNode value)
  {
    if (key != null && value != null)
    {
      ObjectNode extra = OM.createObjectNode();
      extra.put("key", key);

      // we do not want extra quotes for textnodes
      if (value instanceof TextNode)
      {
        extra.put("value", value.textValue());
      }
      else
      {
        extra.put("value", value.toString());
      }

      extras.add(extra);
    }
  }

  @Override
  public String getState()
  {
    return metadata.getState();
  }

  @Override
  public void setState(String state)
  {
    metadata.setState(state);
  }

  @Override
  public String getExtra(String name)
  {
    ExtraBean bean = extras.get(name);
    return bean == null ? null : bean.getValue().textValue();
  }

  private List<String> getExtraListByString(String name)
  {
    ExtraBean bean = extras.get(name);
    return bean == null ? null : Util.readJsonList(bean.getValue());
  }

  @Override
  public void setExtra(String name, String value)
  {
    if (extras == null)
    {
      extras = new HashMap<>();
    }

    if (StringUtils.isNotBlank(value))
    {
      ExtraBean bean = new ExtraBean();
      bean.setKey(name);
      bean.setValue(value);
      extras.put(name, bean);
    }
    else
    {
      extras.remove(name);
    }
  }

  @Override
  public String getAuthor()
  {
    return metadata.getAuthor();
  }

  @Override
  public double getAverageRating()
  {
    if (!ratingfetched && odrClient != null)
    {
      odrClient.loadRating(this);
      ratingfetched = true;
    }
    return averageRating;
  }

  public void setAverageRating(double averageRating)
  {
    this.averageRating = averageRating;
  }

  @Override
  public int getRatingCount()
  {
    if (!ratingfetched && odrClient != null)
    {
      odrClient.loadRating(this);
      ratingfetched = true;
    }
    return ratingCount;
  }

  public void setRatingCount(int ratingCount)
  {
    this.ratingCount = ratingCount;
  }

  @Override
  public String getModifiedAsString(String pattern)
  {
    SimpleDateFormat format = new SimpleDateFormat(pattern);
    return getModified() == null ? null : format.format(getModified());
  }

  @Override
  public String getPublishedAsString(String pattern)
  {
    SimpleDateFormat format = new SimpleDateFormat(pattern);
    return getPublished() == null ? null : format.format(getPublished());
  }

  /**
   * Flag, if the resources are modified.
   * 
   * @return
   */
  public boolean resourcesModified()
  {
    return resoucesModified;
  }

  @Override
  public void setResourcesModified(boolean b)
  {
    resoucesModified = b;
  }

  @Override
  public void setType(MetadataEnumType type)
  {
    metadata.setType(type.toField());
  }

  @Override
  public String getOwnerOrg()
  {
    return metadata.getOwner_org();
  }

  @Override
  public void setOwnerOrg(String ownerOrg)
  {
    metadata.setOwner_org(ownerOrg);
  }

  @Override
  public String getCreatorUserId()
  {
    return metadata.getCreator_user_id();
  }

  @Override
  public boolean isPrivate()
  {
    return metadata.isPrivate();
  }

  @Override
  public void setPrivate(boolean isPrivate)
  {
    metadata.setPrivate(isPrivate);
  }

  private void setExtraListByString(String field, List<String> list)
  {
    ArrayNode node = OM.createArrayNode();
    for (String text : list)
    {
      if (StringUtils.isNotBlank(text))
      {
        node.add(text);
      }
    }

    try
    {
      setExtra(field, OM.writeValueAsString(node));
    }
    catch (JsonProcessingException e)
    {
      log.error("Error writing " + field + ": ", e);
    }
  }

  @Override
  public String getMaintainer()
  {
    return metadata.getMaintainer();
  }

  @Override
  public void setAuthor(String name)
  {
    metadata.setAuthor(name);
  }

  @Override
  public void setMaintainer(String name)
  {
    metadata.setMaintainer(name);
  }

  @Override
  public void setAuthor_email(String email)
  {
    metadata.setAuthor_email(email);
  }

  @Override
  public void setMaintainer_email(String email)
  {
    metadata.setMaintainer_email(email);
  }

  @Override
  public String getAuthor_email()
  {
    return metadata.getAuthor_email();
  }

  @Override
  public String getMaintainer_email()
  {
    return metadata.getMaintainer_email();
  }

  @Override
  public Date getLastModifiedDate()
  {
    Date result = getModified();
    if (result == null)
    {
      result = getMetadataModified();
    }
    return result;
  }

  @Override
  public List<String> getExtraList(MetadataListExtraFields field)
  {
    return getExtraListByString(field.getField());
  }

  @Override
  public void setExtraList(MetadataListExtraFields field, List<String> list)
  {
    setExtraListByString(field.getField(), list);
  }

  @Override
  public String getExtraString(MetadataStringExtraFields field)
  {
    return getExtra(field.getField());
  }

  @Override
  public void setExtraString(MetadataStringExtraFields field, String value)
  {
    setExtra(field.getField(), value);
  }

  /**
   * Returns the odrClient.
   * 
   * @return the odrClient
   */
  public ODRClient getOdrClient()
  {
    return odrClient;
  }
}
