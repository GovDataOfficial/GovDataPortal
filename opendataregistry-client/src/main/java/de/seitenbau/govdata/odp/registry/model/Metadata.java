/**
 * Copyright (c) 2012, 2013 Fraunhofer Institute FOKUS
 * Copyright (c) 2015 SEITENBAU GmbH
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

package de.seitenbau.govdata.odp.registry.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * The Interface Metadata.
 *
 * @author sim
 * @author rnoerenberg
 */
public interface Metadata extends Serializable
{
  // CHECKSTYLE:OFF
  List<String> getExtraList(MetadataListExtraFields field);

  /**
   * Set an Extra-Field that is supposed to be a list.
   * If there are no entries in the given list, an empty list will be stored.
   * @param field name name of the extra-field
   * @param list new list
   */
  void setExtraList(MetadataListExtraFields field, List<String> list);

  String getExtraString(MetadataStringExtraFields field);

  void setExtraString(MetadataStringExtraFields field, String value);

  String getIdentifierWithFallback();

  enum State
  {
    ACTIVE("active"),

    DELETED("deleted");

    private String value;

    State(String value)
    {
      this.value = value;
    }

    public String getValue()
    {
      return this.value;
    }
  }

  void cleanResources();

  String getAuthor();

  double getAverageRating();

  List<Category> getCategories();

  Contact getContact(RoleEnumType role);

  /**
   * Creates a Set of all licenses used by resources of this metadata.
   * @return The set.
   */
  Set<Licence> getResourcesLicenses();

  List<Contact> getContacts();

  String getCreatorUserId();

  String getExtra(String name);

  String getId();

  Date getMetadataModified();

  Date getModified();

  String getModifiedAsString(String pattern);

  String getName();

  String getNotes();

  String getOwnerOrg();

  Date getPublished();

  String getPublishedAsString(String pattern);

  int getRatingCount();

  List<Resource> getResources();

  String getState();

  List<Tag> getTags();

  Date getTemporalCoverageFrom();

  Date getTemporalCoverageTo();

  String getTitle();

  MetadataEnumType getType();

  String getUrl();

  /**
   * Openness of all licenses of resources of this metadata.
   * If at least one open license is found, this will return true.
   * @return true if open licenses were found, false if there are no licenses or only closed licenses.
   */
  boolean isOpen();

  boolean isPrivate();

  Category newCategory();

  Resource newResource();

  Tag newTag(String name);

  void removeResource(Resource r);

  /**
   * Set an Extra-Field.
   * If the value is null or blank (empty string or only whitespace), the extra-entry will be deleted.
   * @param name name of the extra-field
   * @param value new value
   */
  void setExtra(String name, String value);

  void setModified(Date modified);

  void setName(String name);

  void setNotes(String notes);

  void setOwnerOrg(String ownerOrg);

  void setPrivate(boolean isPrivate);

  void setPublished(Date published);

  void setResourcesModified(boolean b);

  void setState(String state);

  void setTemporalCoverageFrom(Date from);

  void setTemporalCoverageTo(Date to);

  void setTitle(String title);

  void setType(MetadataEnumType type);

  void setUrl(String url);

  String getMaintainer();

  void setAuthor(String name);

  void setMaintainer(String name);

  void setAuthor_email(String email);

  void setMaintainer_email(String email);

  String getAuthor_email();

  String getMaintainer_email();

  /**
   * Gets the last modified date.
   * 
   * @return the last modified date.
   */
  Date getLastModifiedDate();
}
