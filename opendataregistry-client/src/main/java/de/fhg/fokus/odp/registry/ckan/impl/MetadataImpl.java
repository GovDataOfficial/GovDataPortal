/**
 * Copyright (c) 2012, 2013 Fraunhofer Institute FOKUS
 *
 * This file is part of Open Data Platform.
 *
 * Open Data Platform is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * Open Data Plaform is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.

 * You should have received a copy of the GNU Affero General Public License
 * along with Open Data Platform.  If not, see <http://www.gnu.org/licenses/agpl-3.0>.
 */

package de.fhg.fokus.odp.registry.ckan.impl;

import static de.fhg.fokus.odp.registry.ckan.Constants.JSON_DATETIME_PATTERN;
import static de.fhg.fokus.odp.registry.ckan.Constants.JSON_FIELD_CATEGORIES;
import static de.fhg.fokus.odp.registry.ckan.Constants.JSON_FIELD_CONTACTS;
import static de.fhg.fokus.odp.registry.ckan.Constants.JSON_FIELD_COVERAGEFROM;
import static de.fhg.fokus.odp.registry.ckan.Constants.JSON_FIELD_COVERAGETO;
import static de.fhg.fokus.odp.registry.ckan.Constants.JSON_FIELD_DATES;
import static de.fhg.fokus.odp.registry.ckan.Constants.JSON_FIELD_EXTRAS;
import static de.fhg.fokus.odp.registry.ckan.Constants.JSON_FIELD_GEOGRANULARITY;
import static de.fhg.fokus.odp.registry.ckan.Constants.JSON_FIELD_IMAGES;
import static de.fhg.fokus.odp.registry.ckan.Constants.JSON_FIELD_LICENCE;
import static de.fhg.fokus.odp.registry.ckan.Constants.JSON_FIELD_RESOURCES;
import static de.fhg.fokus.odp.registry.ckan.Constants.JSON_FIELD_SECTOR;
import static de.fhg.fokus.odp.registry.ckan.Constants.JSON_FIELD_SPATIAL;
import static de.fhg.fokus.odp.registry.ckan.Constants.JSON_FIELD_SPATIALTEXT;
import static de.fhg.fokus.odp.registry.ckan.Constants.JSON_FIELD_SPATIAL_REFERENCE;
import static de.fhg.fokus.odp.registry.ckan.Constants.JSON_FIELD_TAGS;
import static de.fhg.fokus.odp.registry.ckan.Constants.JSON_FIELD_TEMPORALGRANULARITY;
import static de.fhg.fokus.odp.registry.ckan.Constants.JSON_FIELD_TEMPORALGRANULARITY_FACTOR;
import static de.fhg.fokus.odp.registry.ckan.Constants.JSON_FIELD_USEDDATASETS;

import java.io.IOException;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.NumericNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

import de.fhg.fokus.odp.registry.ODRClient;
import de.fhg.fokus.odp.registry.ckan.ODRClientImpl;
import de.fhg.fokus.odp.registry.ckan.json.ContactBean;
import de.fhg.fokus.odp.registry.ckan.json.ExtraBean;
import de.fhg.fokus.odp.registry.ckan.json.GroupBean;
import de.fhg.fokus.odp.registry.ckan.json.LicenceBean;
import de.fhg.fokus.odp.registry.ckan.json.MetadataBean;
import de.fhg.fokus.odp.registry.ckan.json.PolygonBean;
import de.fhg.fokus.odp.registry.ckan.json.ResourceBean;
import de.fhg.fokus.odp.registry.ckan.json.SpatialDataBean;
import de.fhg.fokus.odp.registry.ckan.json.SpatialReferenceBean;
import de.fhg.fokus.odp.registry.ckan.json.TagBean;
import de.fhg.fokus.odp.registry.model.Category;
import de.fhg.fokus.odp.registry.model.Contact;
import de.fhg.fokus.odp.registry.model.Licence;
import de.fhg.fokus.odp.registry.model.Metadata;
import de.fhg.fokus.odp.registry.model.MetadataEnumType;
import de.fhg.fokus.odp.registry.model.Resource;
import de.fhg.fokus.odp.registry.model.RoleEnumType;
import de.fhg.fokus.odp.registry.model.SectorEnumType;
import de.fhg.fokus.odp.registry.model.SpatialData;
import de.fhg.fokus.odp.registry.model.SpatialEnumType;
import de.fhg.fokus.odp.registry.model.SpatialReference;
import de.fhg.fokus.odp.registry.model.Tag;
import de.fhg.fokus.odp.registry.model.TemporalGranularityEnumType;
import de.fhg.fokus.odp.registry.model.exception.OpenDataRegistryException;
import de.fhg.fokus.odp.registry.model.exception.UnknownRoleException;
import de.seitenbau.govdata.date.DateUtil;

public class MetadataImpl implements Metadata, Serializable {

  private static final String ERSTELLT = "erstellt";

  private static final String VEROEFFENTLICHT = "veroeffentlicht";

  private static final String AKTUALISIERT = "aktualisiert";

  /**
     * 
     */
  private static final long serialVersionUID = -374425005119368545L;

  private static final Logger log = LoggerFactory
      .getLogger(MetadataImpl.class);

  private static final ObjectMapper OM = new ObjectMapper();

  protected MetadataBean metadata;

  private List<Resource> resources;

  private List<Tag> tags;

  private List<Contact> contacts;

  private List<Category> categories;

  private List<Category> subCategories;

  private Licence licence;

  private Date temporalCoverageFrom;

  private Date temporalCoverageTo;

  private SectorEnumType sector;

  private String geoGranularity;

  private TemporalGranularityEnumType temporalGranularity;

  private int temporalGranularityFactor = 1;

  private List<String> usedDatasets;

  private Date created;

  private Date published;

  private Date modified;

  private String spatialText;

  private SpatialData spatialData;
  private String spatialDataText;

  private SpatialReference spatialReference;

  private List<String> images;

  private Map<String, ExtraBean> extras = new HashMap<String, ExtraBean>();

  // private String author;

  private double averageRating;

  private int ratingCount;

  private boolean ratingfetched = false;

  protected transient ODRClient odr;

  private boolean resoucesModified;

  public MetadataImpl(MetadataBean metadata, ODRClient odr) {
    this.odr = odr;
    this.metadata = metadata;
    this.metadata.setType(MetadataEnumType.DATASET.toField());

    LicenceBean licenceBean = new LicenceBean();
    licenceBean.setId(metadata.getLicense_id());
    licenceBean.setTitle(metadata.getLicense_title());
    licenceBean.setUrl(metadata.getLicense_url());
    licence = new LicenceImpl(licenceBean);

    extractExtras();
    
    // overwrite 'veroeffentlichende_stelle' with author values !
    if (metadata.getAuthor() != null && !metadata.getAuthor().isEmpty()) {
      addContactIfNotPresent(RoleEnumType.PUBLISHER, metadata.getAuthor(), metadata.getAuthor_email());
      
      // create extras->contact for role "author" when there is currently none
      addContactIfNotPresent(RoleEnumType.AUTHOR, metadata.getAuthor(), metadata.getAuthor_email());
    }

    // overwrite 'ansprechpartner' with maintainer values !
    if (metadata.getMaintainer() != null && !metadata.getMaintainer().isEmpty()) {
      addContactIfNotPresent(RoleEnumType.MAINTAINER, metadata.getMaintainer(), metadata.getMaintainer_email());
    }

  }

  private void addContactIfNotPresent(RoleEnumType type, String name, String email)
  {
    Contact contact = getContact(type);
    if (contact == null) {
      ContactBean bean = new ContactBean();
      bean.setRole(type.toField());
      bean.setName(name);
      bean.setEmail(email);
      getContacts().add(new ContactImpl(bean));
    }
  }

  private void extractExtras() {
    for (ExtraBean bean : metadata.getExtras()) {
      extras.put(bean.getKey(), bean);
    }

    ExtraBean contacts = extras.remove(JSON_FIELD_CONTACTS);
    if (contacts != null) {
      List<Contact> contactsList = ContactImpl.read(extraToJson(contacts
          .getValue()));
      getContacts().addAll(contactsList);
    }

    ExtraBean licenceBean = extras.remove(JSON_FIELD_LICENCE);
    if (licenceBean != null) {
      Licence extraLicence = LicenceImpl.read(extraToJson(licenceBean
          .getValue()));
      if (extraLicence.getName() != null
          && extraLicence.getName().startsWith("other-")) {
        extraLicence.setTitle(extraLicence.getOther());
        setLicence(extraLicence);
      } else if (licence.getName() != null
          && licence.getName().equalsIgnoreCase(
              extraLicence.getName())) {
        licence.setOther(extraLicence.getOther());
      } else if (extraLicence.getName() != null) {
        setLicence(extraLicence);
      }
    }

    ExtraBean coverageFrom = extras.remove(JSON_FIELD_COVERAGEFROM);
    if (coverageFrom != null) {
      if (coverageFrom.getValue().isTextual()) {
        String value = coverageFrom.getValue().textValue();
        setTemporalCoverageFrom(toDate(value));
      }
    }

    ExtraBean coverageTo = extras.remove(JSON_FIELD_COVERAGETO);
    if (coverageTo != null) {
      if (coverageTo.getValue().isTextual()) {
        String value = coverageTo.getValue().textValue();
        setTemporalCoverageTo(toDate(value));
      }
    }

    ExtraBean sector = extras.remove(JSON_FIELD_SECTOR);
    if (sector != null) {
      String value = sector.getValue().textValue();
      setSector(SectorEnumType.fromField(value.toLowerCase()));
    }

    /* SPATIAL */
    ExtraBean spatial = extras.remove(JSON_FIELD_SPATIAL);
    if (spatial != null) {
      JsonNode jsonNode = spatial.getValue();
      if (jsonNode != null) {
        JsonNode spatialNode = extraToJson(jsonNode);
        setSpatialDataImpl(spatialNode);
      }
    }

    /* SPATIAL REFERENCE */
    ExtraBean spatialreference = extras
        .remove(JSON_FIELD_SPATIAL_REFERENCE);
    if (spatialreference != null) {
      JsonNode spatialReferenceNode = extraToJson(spatialreference
          .getValue());

      SpatialReferenceBean spatialReferenceBean = new SpatialReferenceBean();

      if (spatialReferenceNode.get("ags") != null) {
        spatialReferenceBean.setAgs(spatialReferenceNode.get("ags")
            .textValue());
      }
      if (spatialReferenceNode.get("nuts") != null) {
        spatialReferenceBean.setNuts(spatialReferenceNode.get("nuts")
            .textValue());
      }
      if (spatialReferenceNode.get("uri") != null) {
        spatialReferenceBean.setUri(spatialReferenceNode.get("uri")
            .textValue());
      }
      if (spatialReferenceNode.get("text") != null) {
        spatialReferenceBean.setText(spatialReferenceNode.get("text")
            .textValue());
      }
      SpatialReferenceImpl spatialReferenceImpl = null;

      spatialReferenceImpl = new SpatialReferenceImpl(
          spatialReferenceBean);

      setSpatialReference(spatialReferenceImpl);
    }

    ExtraBean spatialText = extras.remove(JSON_FIELD_SPATIALTEXT);
    if (spatialText != null) {
      JsonNode node = spatialText.getValue();
      String value = node.textValue();
      setGeoCoverage(value);
    }

    ExtraBean geoGranularity = extras.remove(JSON_FIELD_GEOGRANULARITY);
    if (geoGranularity != null) {
      String value = geoGranularity.getValue().textValue();
      setGeoGranularity(value);
    }

    ExtraBean temporalGranularity = extras
        .remove(JSON_FIELD_TEMPORALGRANULARITY);
    if (temporalGranularity != null) {
      String value = temporalGranularity.getValue().textValue();
      /* msg 1.12.2014 begin */
      if (!value.equals(""))
        /* msg 1.12.2014 end */
        try {
          setTemporalGranularity(TemporalGranularityEnumType
              .fromField(value.toLowerCase()));
        } catch (OpenDataRegistryException e) {
          log.debug("invalid temporal granularity of '{}': '{}'",
              metadata.getName(), e.getMessage());
          // log.error("temporal granularity", e);
        }
    }

    ExtraBean temporalGranularityFactor = extras
        .remove(JSON_FIELD_TEMPORALGRANULARITY_FACTOR);
    if (temporalGranularityFactor != null) {
      int value = temporalGranularityFactor.getValue().intValue();
      /* msg 1.12.2014 begin */
      if (value != 0)
        /* msg 1.12.2014 end */
        setTemporalGranularityFactor(value);
    }

    ExtraBean usedDatasets = extras.remove(JSON_FIELD_USEDDATASETS);
    if (usedDatasets != null) {
      JsonNode values = extraToJson(usedDatasets.getValue());
      if (values.isArray()) {
        for (JsonNode value : values) {
          getUsedDatasets().add(value.textValue());
        }
      }
    }

    ExtraBean dates = extras.remove(JSON_FIELD_DATES);
    if (dates != null) {
      JsonNode datesNode = extraToJson(dates.getValue());
      if (datesNode.isArray()) {
        for (JsonNode dateNode : datesNode) {
          String role = dateNode.path("role").textValue();
          String date = dateNode.path("date").textValue();
          if (ERSTELLT.equalsIgnoreCase(role)) {
            setCreated(toDate(date));
          } else if (VEROEFFENTLICHT.equalsIgnoreCase(role)) {
            setPublished(toDate(date));
          } else if (AKTUALISIERT.equalsIgnoreCase(role)) {
            setModified(toDate(date));
          }
        }
      }
    }

    ExtraBean images = extras.remove(JSON_FIELD_IMAGES);
    if (images != null) {
      JsonNode imagesNode = extraToJson(images.getValue());
      if (imagesNode.isArray()) {
        for (JsonNode image : imagesNode) {
          getImages().add(image.textValue());
        }
      }
    }

  }

  private JsonNode extraToJson(JsonNode node) {
    JsonNode result = node;
    while (result.isTextual() && !result.textValue().isEmpty())
    {
      try {
        result = OM.readTree(result.textValue());
      } catch (JsonProcessingException e) {
        log.error("extra to json", e.getMessage());
        return null;
      } catch (IOException e) {
        log.error("extra to json", e.getMessage());
        return null;
      }
    }
    return result;
  }

  private Date toDate(String value) {
    if (value != null) {
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
  public String getName() {
    return metadata.getName();
  }

  @Override
  public void setName(String name) {
    metadata.setName(name);
  }

  @Override
  public String getTitle() {
    return metadata.getTitle();
  }

  @Override
  public void setTitle(String title) {
    metadata.setTitle(title);
  }

  @Override
  public String getUrl() {
    return metadata.getUrl();
  }

  @Override
  public void setUrl(String url) {
    metadata.setUrl(url);
  }

  @Override
  public Licence getLicence() {
    return licence;
  }

  @Override
  public void setLicence(Licence licence) {
    this.licence = licence;
  }

  @Override
  public String getNotes() {
    return metadata.getNotes();
  }

  @Override
  public void setNotes(String notes) {
    metadata.setNotes(notes);
  }

  @Override
  public Date getMetadataModified() {
    return metadata.getMetadata_modified();
  }

  @Override
  public Date getModified() {
    return modified == null ? null : (Date) modified.clone();
  }

  @Override
  public void setModified(Date modified) {
    this.modified = modified == null ? null : (Date) modified.clone();
  }

  @Override
  public Date getCreated() {
    return created == null ? null : (Date) created.clone();
  }

  @Override
  public void setCreated(Date created) {
    this.created = created == null ? null : (Date) created.clone();
  }

  @Override
  public Date getPublished() {
    return published == null ? null : (Date) published.clone();
  }

  @Override
  public void setPublished(Date published) {
    this.published = published == null ? null : (Date) published.clone();
  }

  @Override
  public boolean isOpen() {
    return metadata.isIsopen();
  }

  @Override
  public List<Contact> getContacts() {
    if (contacts == null) {
      contacts = new ArrayList<Contact>();
    }
    return contacts;
  }

  @Override
  public Contact getContact(RoleEnumType role) {
    for (Contact contact : getContacts()) {
      try {
        if (contact.getRole() == role) {
          return contact;
        }
      } catch (UnknownRoleException e) {
        log.debug("getContact: UnknownRoleException: {}", e.getMessage());
      }
    }
    return null;
  }

  @Override
  public Contact newContact(RoleEnumType role) {
    ContactBean bean = new ContactBean();
    bean.setRole(role.toField());
    Contact contact = new ContactImpl(bean);
    getContacts().add(contact);
    return contact;
  }

  @Override
  public List<Tag> getTags() {
    if (tags == null) {
      tags = new ArrayList<Tag>();
      for (TagBean bean : metadata.getTags()) {
        tags.add(new TagImpl(bean));
      }
    }
    return tags;
  }

  @Override
  public Tag newTag(String name) {
    TagBean bean = new TagBean();
    bean.setName(name);
    bean.setDisplay_name(name);
    Tag tag = new TagImpl(bean);
    getTags().add(tag);
    return tag;
  }

  @Override
  public List<Resource> getResources() {
    if (resources == null) {
      resources = new ArrayList<Resource>();
      for (ResourceBean bean : metadata.getResources()) {
        resources.add(new ResourceImpl(bean));
      }
      // log.info("resources == null getResources:" + resources.size());
    }
    // log.info("getResources:" + resources.size());
    return resources;
  }

  @Override
  public void cleanResources() {
    getResources().clear();
  }

  @Override
  /**
   * msg to remove the resource
   */
  public void removeResource(Resource r) {
    setResoucesModified(true);
    getResources().remove(r);
    metadata.getResources().remove(((ResourceImpl) r).getBean());
  }

  @Override
  public Resource newResource() {
    Resource resource = new ResourceImpl(new ResourceBean());
    getResources().add(resource);
    return resource;
  }

  @Override
  public List<Category> getCategories() {
    if (categories == null) {
      categories = new ArrayList<Category>();
      // there are groups available, initialize from these groups
      if (metadata.getGroups() != null) {
        for (GroupBean bean : metadata.getGroups()) {
          categories.add(new CategoryImpl(bean));
        }
      }
    }
    return categories;
  }

  @Override
  public Category newCategory() {
    Category category = new CategoryImpl(new GroupBean());
    getCategories().add(category);
    return null;
  }

  @Override
  public List<Category> getSubCategories() {
    if (subCategories == null) {
      subCategories = new ArrayList<Category>();
      if (metadata.getGroups() != null) {
        for (GroupBean bean : metadata.getGroups()) {
          if ("subgroup".equals(bean.getType())) {
            subCategories.add(new CategoryImpl(bean));
          }
        }
      }
    }
    return subCategories;
  }

  @Override
  public SectorEnumType getSector() {
    return sector;
  }

  @Override
  public void setSector(SectorEnumType sector) {
    this.sector = sector;
  }

  @Override
  public MetadataEnumType getType() {
    return MetadataEnumType.fromField(metadata.getType());
  }

  @Override
  public Date getTemporalCoverageFrom() {
    return temporalCoverageFrom == null ? null
        : (Date) temporalCoverageFrom.clone();
  }

  @Override
  public void setTemporalCoverageFrom(Date temporalCoverageFrom) {
    this.temporalCoverageFrom = temporalCoverageFrom == null ? null
        : (Date) temporalCoverageFrom.clone();
  }

  @Override
  public Date getTemporalCoverageTo() {
    return temporalCoverageTo == null ? null : (Date) temporalCoverageTo
        .clone();
  }

  @Override
  public void setTemporalCoverageTo(Date temporalCoverageTo) {
    this.temporalCoverageTo = temporalCoverageTo == null ? null
        : (Date) temporalCoverageTo.clone();
  }

  @Override
  public String getGeoGranularity() {
    return geoGranularity;
  }

  @Override
  public void setGeoGranularity(String geoGranularity) {
    this.geoGranularity = geoGranularity;
  }

  public boolean isNew() {
    return metadata.getId() == null || metadata.getId().isEmpty();
  }

  public JsonNode write(boolean clearResources)
      throws OpenDataRegistryException {

    // harmonize contacts fields
    for (Contact contact : contacts) {
      try {
        switch (contact.getRole()) {
        /* msg 10.10.2014 begin */
        case AUTHOR: // AUTHOR("autor", "Autor")
          metadata.setAuthor(contact.getName());
          metadata.setAuthor_email(contact.getEmail());
          break;
        /* msg 10.10.2014 end */
        case PUBLISHER: // PUBLISHER("veroeffentlichende_stelle",
          // "Veröffentlichende Stelle"),
          metadata.setAuthor(contact.getName());
          metadata.setAuthor_email(contact.getEmail());
          break;
        case MAINTAINER:// MAINTAINER("ansprechpartner",
                // "Ansprechpartner"),
          metadata.setMaintainer(contact.getName());
          metadata.setMaintainer_email(contact.getEmail());
          break;
        default:
          break;
        }
      } catch (UnknownRoleException e) {
        log.warn("write: UnknownRoleException.", e.getMessage());
      }
    }

    // harmonize license fields
    metadata.setLicense_id(licence.getName());
    metadata.setLicense_title(licence.getTitle());
    metadata.setLicense_url(licence.getUrl());

    metadata.getResources().clear();
    for (Resource resource : getResources()) {
      metadata.getResources().add(((ResourceImpl) resource).getBean());
    }

    JsonNode node = ODRClientImpl.convert(metadata);

    ArrayNode groups = OM.createArrayNode();
    for (Category category : getCategories()) {
      ObjectNode group = OM.createObjectNode();
      group.put("name", ((CategoryImpl) category).getName());
      groups.add(group);
    }
    if (groups.size() > 0) {
      ((ObjectNode) node).set(JSON_FIELD_CATEGORIES, groups);
    }
    /* msg 10.04.2014 */
    if (clearResources) {
      ArrayNode resourcesNode = OM.createArrayNode();
      ((ObjectNode) node).set(JSON_FIELD_RESOURCES, resourcesNode);
    }

    ArrayNode tagsNode = OM.createArrayNode();
    for (Tag tag : getTags()) {
      ObjectNode tagNode = OM.createObjectNode();
      tagNode.put("name", tag.getName());
      tagsNode.add(tagNode);
    }
    if (tagsNode.size() > 0) {
      ((ObjectNode) node).set(JSON_FIELD_TAGS, tagsNode);
    }

    ArrayNode extrasNode = OM.createArrayNode();

    ArrayNode contactsNode = OM.createArrayNode();
    for (Contact contact : getContacts()) {
      JsonNode cont = ((ContactImpl) contact).write();
      contactsNode.add(cont);
    }

    if (contactsNode.size() > 0) {
      addExtras(extrasNode, JSON_FIELD_CONTACTS, contactsNode);
    }

    ObjectNode licenceNode = OM.createObjectNode();
    licenceNode.put("license_id", licence.getName());
    licenceNode.put("license_url", licence.getUrl());
    if (licence.getOther() != null) {
      licenceNode.put("other", licence.getOther());
    }

    addExtras(extrasNode, JSON_FIELD_LICENCE, licenceNode);

    ArrayNode datesNode = OM.createArrayNode();

    DateFormat formatter = new SimpleDateFormat(JSON_DATETIME_PATTERN);

    if (created != null) {
      ObjectNode createdNode = OM.createObjectNode();
      createdNode.put("role", ERSTELLT);
      createdNode.put("date", formatter.format(created));
      datesNode.add(createdNode);
    }

    if (modified != null) {
      ObjectNode modifiedNode = OM.createObjectNode();
      modifiedNode.put("role", AKTUALISIERT);
      modifiedNode.put("date", formatter.format(modified));
      datesNode.add(modifiedNode);
    }

    if (published != null) {
      ObjectNode publishedNode = OM.createObjectNode();
      publishedNode.put("role", VEROEFFENTLICHT);
      publishedNode.put("date", formatter.format(published));
      datesNode.add(publishedNode);
    }

    addExtras(extrasNode, JSON_FIELD_DATES, datesNode);

    if (spatialData != null) {
      ObjectNode spatialDataNode = getSpatialDataObjectNode();
      addExtras(extrasNode, JSON_FIELD_SPATIAL, spatialDataNode);
    }

    else {
      // log.info("write:" + spatialDataText);
      TextNode spatialTextNode = OM.createObjectNode().textNode(
          spatialDataText);
      addExtras(extrasNode, JSON_FIELD_SPATIAL, spatialTextNode);
    }

    if (spatialText != null) {
      TextNode spatialTextNode = OM.createObjectNode().textNode(
          spatialText);
      addExtras(extrasNode, JSON_FIELD_SPATIALTEXT, spatialTextNode);
    }

    if (spatialReference != null) {
      ObjectNode spatialReferenceNode = OM.createObjectNode();
      spatialReferenceNode.put("ags", spatialReference.getAgs());
      spatialReferenceNode.put("nuts", spatialReference.getNuts());
      spatialReferenceNode.put("uri", spatialReference.getUri());
      spatialReferenceNode.put("text", spatialReference.getText());

      addExtras(extrasNode, JSON_FIELD_SPATIAL_REFERENCE,
          spatialReferenceNode);
    }

    if (!getImages().isEmpty()) {
      ArrayNode imagesNode = OM.createArrayNode();
      for (String image : getImages()) {
        imagesNode.add(image);
      }
      addExtras(extrasNode, JSON_FIELD_IMAGES, imagesNode);
    }

    // subCategories (seems not to be not necessary)

    // temporalCoverageFrom
    if (temporalCoverageFrom != null) {
      TextNode temporalCoverageFromNode = OM.createObjectNode().textNode(
          formatter.format(temporalCoverageFrom));
      addExtras(extrasNode, JSON_FIELD_COVERAGEFROM,
          temporalCoverageFromNode);
    }

    // temporalCoverageTo
    if (temporalCoverageTo != null) {
      TextNode temporalCoverageToNode = OM.createObjectNode().textNode(
          formatter.format(temporalCoverageTo));
      addExtras(extrasNode, JSON_FIELD_COVERAGETO, temporalCoverageToNode);
    }

    // sector
    if (sector != null) {
      TextNode sectorNode = OM.createObjectNode().textNode(
          sector.toField());
      addExtras(extrasNode, JSON_FIELD_SECTOR, sectorNode);
    }

    // geoGranularity
    if (geoGranularity != null) {
      TextNode geoGranularityNode = OM.createObjectNode().textNode(
          geoGranularity);
      addExtras(extrasNode, JSON_FIELD_GEOGRANULARITY, geoGranularityNode);
    }

    // temporalGranularity
    if (temporalGranularity != null) {
      TextNode temporalGranularityNode = OM.createObjectNode().textNode(
          temporalGranularity.toField());
      addExtras(extrasNode, JSON_FIELD_TEMPORALGRANULARITY,
          temporalGranularityNode);
      NumericNode temporalGranularityFactorNode = OM.createObjectNode()
          .numberNode(temporalGranularityFactor);
      addExtras(extrasNode, JSON_FIELD_TEMPORALGRANULARITY_FACTOR,
          temporalGranularityFactorNode);
    }

    // usedDatasets
    if (!getUsedDatasets().isEmpty()) {
      ArrayNode usedDatasetsNode = OM.createArrayNode();
      for (String usedDataset : usedDatasets) {
        usedDatasetsNode.add(usedDataset);
      }
      addExtras(extrasNode, JSON_FIELD_USEDDATASETS, usedDatasetsNode);
    }

    // other extras
    if (extras != null && !extras.isEmpty()) {
      for (ExtraBean bean : extras.values()) {
        addExtras(extrasNode, bean.getKey(), bean.getValue());
      }
    }

    if (extrasNode.size() > 0) {
      ((ObjectNode) node).set(JSON_FIELD_EXTRAS, extrasNode);
    }

    return node;
  }

  private void addExtras(ArrayNode extras, String key, JsonNode value) {
    if (key != null && value != null)
    {
      ObjectNode extra = OM.createObjectNode();
      extra.put("key", key);
      
      // we do not want extra quotes for textnodes
      if(value instanceof TextNode) {
        extra.put("value", ((TextNode)value).textValue());
      } else {
        extra.put("value", value.toString());
      }
      
      extras.add(extra);
    }
  }

  @Override
  public String getState() {
    return metadata.getState();
  }

  @Override
  public void setState(String state) {
    metadata.setState(state);
  }

  @Override
  public TemporalGranularityEnumType getTemporalGranularity() {
    return temporalGranularity;
  }

  @Override
  public void setTemporalGranularity(
      TemporalGranularityEnumType temporalGranularity) {
    this.temporalGranularity = temporalGranularity;
  }

  @Override
  public int getTemporalGranularityFactor() {
    return temporalGranularityFactor;
  }

  @Override
  public void setTemporalGranularityFactor(int temporalGranularityFactor) {
    this.temporalGranularityFactor = temporalGranularityFactor;
  }

  public List<String> getUsedDatasets() {
    if (usedDatasets == null) {
      usedDatasets = new ArrayList<String>();
    }
    return usedDatasets;
  }

  @Override
  public String getGeoCoverage() {
    return spatialText;
  }

  @Override
  public void setGeoCoverage(String geoCoverage) {
    this.spatialText = geoCoverage;
  }

  @Override
  public List<String> getImages() {
    if (images == null) {
      images = new ArrayList<String>();
    }
    return images;
  }

  @Override
  public SpatialData getSpatialData() {
    if (spatialData == null) {
      spatialData = new SpatialDataImpl(new SpatialDataBean());
    }
    return spatialData;
  }

  @Override
  public String getSpatialDataValue() {
    if (spatialData != null) {
      ObjectNode spatialDataNode = getSpatialDataObjectNode();

      return spatialDataNode.toString();
    } else {
      return spatialDataText;
    }
  }

  private ObjectNode getSpatialDataObjectNode() {
    ObjectNode spatialDataNode = OM.createObjectNode();
    spatialDataNode.put("type", spatialData.getType().toField());
    ArrayNode polygonsNode = OM.createArrayNode();
    if (spatialData.getPolygons().size() >= 1)

      for (int i = 0; i < spatialData.getPolygons().size(); i++) {
        ArrayNode polygonNode = OM.createArrayNode();
        PolygonBean polygonBean = spatialData.getPolygons().get(i);
        for (int j = 0; j < polygonBean.getCoordinates().size(); j++) {
          ArrayNode coordinateNode = OM.createArrayNode();
          coordinateNode.add(polygonBean.getCoordinates().get(j)
              .getX());
          coordinateNode.add(polygonBean.getCoordinates().get(j)
              .getY());
          polygonNode.add(coordinateNode);
        }
        polygonsNode.add(polygonNode);
      }

    spatialDataNode.set("coordinates", polygonsNode);
    return spatialDataNode;
  }

  @Override
  public void setSpatialDataValue(String value) {
    spatialDataText = value;
    // log.info("setSpatialDataValue=" + spatialDataText);
    if (value != null && !value.isEmpty()) {
      ObjectMapper mapper = new ObjectMapper();
      JsonNode spatialNode = null;
      try {
        spatialNode = mapper.readTree(value);
      } catch (JsonProcessingException e1) {
        setSpatialDataNotValid();
      } catch (IOException e1) {
        setSpatialDataNotValid();
      }
      if (spatialNode != null) {
        setSpatialDataImpl(spatialNode);
      } else
        setSpatialDataNotValid();
    } else
      setSpatialDataNotValid();
  }

  private void setSpatialDataNotValid() {
    this.spatialData = null;
  }

  private void setSpatialDataImpl(JsonNode spatialNode) {
    try {
      if (spatialNode != null) {
        JsonNode typeNode = spatialNode.get("type");
        if (typeNode != null) {
          String type = typeNode.textValue();
          if (type != null && !type.isEmpty()) {
            SpatialDataImpl spatialDataImpl = null;
            spatialDataImpl = new SpatialDataImpl(
                new SpatialDataBean());
            spatialDataImpl.setType(SpatialEnumType.fromField(type
                .toLowerCase()));
            JsonNode coordinatesNode = spatialNode
                .get("coordinates");
            if (coordinatesNode != null) {
              if (coordinatesNode.isArray()
                  && coordinatesNode.size() > 0) {
                for (int i = 0; i < coordinatesNode.size(); i++) {
                  JsonNode inner = coordinatesNode.get(i);
                  for (JsonNode coordinate : inner) {
                    if (coordinate.isArray()) {
                      JsonNode xNode = coordinate.get(0);
                      JsonNode yNode = coordinate.get(1);
                      spatialDataImpl.addCoordinate(i,
                          xNode.doubleValue(),
                          yNode.doubleValue());
                    }
                  }
                }
              }

            }
            setSpatialData(spatialDataImpl);
          }
        }
      } // if
    } catch (OpenDataRegistryException e) {
      log.debug("Unbekannter Spatial-Typ: {}", e.getMessage());
    }
  }

  public void setSpatialData(SpatialData spatialData) {
    this.spatialData = spatialData;
  }

  @Override
  public SpatialReference getSpatialReference() {
    if (spatialReference == null) {
      spatialReference = new SpatialReferenceImpl(
          new SpatialReferenceBean());
    }
    return spatialReference;
  }

  public void setSpatialReference(SpatialReference spatialReference) {
    this.spatialReference = spatialReference;
  }

  public void setSpatialReferenceAgs(String ags) {
    this.spatialReference.setAgs(ags);
  }

  public void setSpatialReferenceNuts(String nuts) {
    this.spatialReference.setNuts(nuts);
  }

  public void setSpatialReferenceUri(String uri) {
    this.spatialReference.setUri(uri);
  }

  public void setSpatialReferenceText(String text) {
    this.spatialReference.setText(text);
  }

  @Override
  public String getExtra(String name) {
    ExtraBean bean = extras.get(name);
    return bean == null ? null : bean.getValue().textValue();
  }

  @Override
  public void setExtra(String name, String value) {
    if (extras == null) {
      extras = new HashMap<String, ExtraBean>();
    }

    if (value == null) {
      extras.remove(name);
    } else {
      ExtraBean bean = new ExtraBean();
      bean.setKey(name);
      TextNode text = OM.createObjectNode().textNode(value);
      bean.setValue(text.toString());
      extras.put(name, bean);
    }
  }

  @Override
  public String getAuthor() {
    return metadata.getAuthor();
  }

  @Override
  public double getAverageRating() {
    if (!ratingfetched && odr != null) {
      odr.loadRating(this);
      ratingfetched = true;
    }
    return averageRating;
  }

  public void setAverageRating(double averageRating) {
    this.averageRating = averageRating;
  }

  @Override
  public int getRatingCount() {
    if (!ratingfetched && odr != null) {
      odr.loadRating(this);
      ratingfetched = true;
    }
    return ratingCount;
  }

  public void setRatingCount(int ratingCount) {
    this.ratingCount = ratingCount;
  }

  @Override
  public String getCreatedAsString(String pattern) {
    SimpleDateFormat format = new SimpleDateFormat(pattern);
    return getCreated() != null ? format.format(getCreated()) : null;
  }

  @Override
  public String getModifiedAsString(String pattern) {
    SimpleDateFormat format = new SimpleDateFormat(pattern);
    return getModified() != null ? format.format(getModified()) : null;
  }

  @Override
  public String getPublishedAsString(String pattern) {
    SimpleDateFormat format = new SimpleDateFormat(pattern);
    return getPublished() != null ? format.format(getPublished()) : null;
  }

  public boolean resoucesModified() {
    return resoucesModified;
  }

  @Override
  public void setResoucesModified(boolean b) {
    resoucesModified = b;
  }

  @Override
  public String getSpatialReferenceAgs() {
    if (spatialReference == null) {
      spatialReference = new SpatialReferenceImpl(
          new SpatialReferenceBean());
    }
    return spatialReference.getAgs();
  }

  @Override
  public String getSpatialReferenceNuts() {
    if (spatialReference == null) {
      spatialReference = new SpatialReferenceImpl(
          new SpatialReferenceBean());
    }
    return spatialReference.getNuts();
  }

  @Override
  public String getSpatialReferenceUri() {
    if (spatialReference == null) {
      spatialReference = new SpatialReferenceImpl(
          new SpatialReferenceBean());
    }
    return spatialReference.getUri();
  }

  @Override
  public String getSpatialReferenceText() {
    if (spatialReference == null) {
      spatialReference = new SpatialReferenceImpl(
          new SpatialReferenceBean());
    }
    return spatialReference.getText();
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

}
