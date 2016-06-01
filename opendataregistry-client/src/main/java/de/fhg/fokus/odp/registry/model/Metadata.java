/**
 * Copyright (c) 2012, 2013 Fraunhofer Institute FOKUS
 * Copyright (c) 2015 SEITENBAU GmbH
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

package de.fhg.fokus.odp.registry.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import de.fhg.fokus.odp.registry.model.exception.OpenDataRegistryException;

/**
 * The Interface Metadata.
 * 
 * @author sim
 * @author rnoerenberg
 */
public interface Metadata extends Serializable
{
  /**
   * Gets the id.
   * 
   * @return the id
   */
  String getId();

	/**
	 * Gets the author.
	 * 
	 * @return the author
	 */
	String getAuthor();

	/**
	 * Gets the name.
	 * 
	 * @return the name
	 */
	String getName();

	/**
	 * Sets the name.
	 * 
	 * @param name
	 *            the new name
	 */
	void setName(String name);

	/**
	 * Gets the title.
	 * 
	 * @return the title
	 */
	String getTitle();

	/**
	 * Sets the title.
	 * 
	 * @param title
	 *            the new title
	 */
	void setTitle(String title);

	/**
	 * Gets the url.
	 * 
	 * @return the url
	 */
	String getUrl();

	/**
	 * Sets the url.
	 * 
	 * @param url
	 *            the new url
	 */
	void setUrl(String url);

	/**
	 * Gets the created.
	 * 
	 * @return the created
	 */
	Date getCreated();

	/**
	 * Gets the created as string.
	 * 
	 * @param pattern
	 *            the pattern
	 * @return the created as string
	 */
	String getCreatedAsString(String pattern);

	/**
	 * Gets the modified.
	 * 
	 * @return the modified
	 */
	Date getModified();

	/**
	 * Gets the modified as string.
	 * 
	 * @param pattern
	 *            the pattern
	 * @return the modified as string
	 */
	String getModifiedAsString(String pattern);

	/**
	 * Sets the modified.
	 * 
	 * @param modified
	 *            the new modified
	 */
	void setModified(Date modified);

	/**
	 * Gets the published.
	 * 
	 * @return the published
	 */
	Date getPublished();

	/**
	 * Gets the published as string.
	 * 
	 * @param pattern
	 *            the pattern
	 * @return the published as string
	 */
	String getPublishedAsString(String pattern);

	/**
	 * Sets the published.
	 * 
	 * @param published
	 *            the new published
	 */
	void setPublished(Date published);

	/**
	 * Checks if is open.
	 * 
	 * @return true, if is open
	 */
	boolean isOpen();

	/**
	 * Gets the contacts.
	 * 
	 * @return the contacts
	 */
	List<Contact> getContacts();

	/**
	 * Gets the contact.
	 * 
	 * @param role
	 *            the role
	 * @return the contact
	 * @throws OpenDataRegistryException
	 *             the open data registry exception
	 */
	Contact getContact(RoleEnumType role);

	/**
	 * New contact.
	 * 
	 * @param role
	 *            the role
	 * @return the contact
	 */
	Contact newContact(RoleEnumType role);

	/**
	 * Gets the temporal coverage from.
	 * 
	 * @return the temporal coverage from
	 */
	Date getTemporalCoverageFrom();

	/**
	 * Sets the temporal coverage from.
	 * 
	 * @param from
	 *            the new temporal coverage from
	 */
	void setTemporalCoverageFrom(Date from);

	/**
	 * Gets the temporal coverage to.
	 * 
	 * @return the temporal coverage to
	 */
	Date getTemporalCoverageTo();

	/**
	 * Sets the temporal coverage to.
	 * 
	 * @param to
	 *            the new temporal coverage to
	 */
	void setTemporalCoverageTo(Date to);

	/**
	 * Gets the licence.
	 * 
	 * @return the licence
	 */
	Licence getLicence();

	/**
	 * Gets the metadata_modified.
	 * 
	 * @return the metadata_modified
	 */
	Date getMetadataModified();

  /**
   * Gets the creator_user_id.
   * 
   * @return the creator_user_id
   */
  String getCreatorUserId();

	/**
	 * Sets the licence.
	 * 
	 * @param licence
	 *            the new licence
	 */
	void setLicence(Licence licence);

	/**
	 * Gets the notes.
	 * 
	 * @return the notes
	 */
	String getNotes();

	/**
	 * Sets the notes.
	 * 
	 * @param notes
	 *            the new notes
	 */
	void setNotes(String notes);

	/**
	 * Gets the tags.
	 * 
	 * @return the tags
	 */
	List<Tag> getTags();

	/**
	 * New tag.
	 * 
	 * @param name
	 *            the name
	 * @return the tag
	 */
	Tag newTag(String name);

	/**
	 * Gets the resources.
	 * 
	 * @return the resources
	 */
	List<Resource> getResources();

	/**
	 * clean resources.
	 * 
	 */
	void cleanResources();

	/**
	 * New resource.
	 * 
	 * @return the resource
	 */
	Resource newResource();

	/**
	 * Gets the categories.
	 * 
	 * @return the categories
	 */
	List<Category> getCategories();

	/**
	 * New category.
	 * 
	 * @return the category
	 */
	Category newCategory();

	// extras

	/**
	 * Gets the sub categories.
	 * 
	 * @return the sub categories
	 */
	List<Category> getSubCategories();

	/**
	 * Gets the sector.
	 * 
	 * @return the sector
	 */
	SectorEnumType getSector();

	/**
	 * Sets the sector.
	 * 
	 * @param sector
	 *            the new sector
	 */
	void setSector(SectorEnumType sector);

	/**
	 * Gets the geo granularity.
	 * 
	 * @return the geo granularity
	 */
	String getGeoGranularity();

	/**
	 * Sets the geo granularity.
	 * 
	 * @param geoGranularity
	 *            the new geo granularity
	 */
	void setGeoGranularity(String geoGranularity);

	/**
	 * Gets the temporal granularity.
	 * 
	 * @return the temporal granularity
	 */
	TemporalGranularityEnumType getTemporalGranularity();

	/**
	 * Sets the temporal granularity.
	 * 
	 * @param temporalGranularity
	 *            the new temporal granularity
	 */
	void setTemporalGranularity(TemporalGranularityEnumType temporalGranularity);

	/**
	 * Gets the temporal granularity factor.
	 * 
	 * @return the temporal granularity factor
	 */
	int getTemporalGranularityFactor();

	/**
	 * Sets the temporal granularity factor.
	 * 
	 * @param factor
	 *            the new temporal granularity factor
	 */
	void setTemporalGranularityFactor(int factor);

	/**
	 * Gets the state.
	 * 
	 * @return the state
	 */
	String getState();

	/**
	 * Sets the state.
	 * 
	 * @param state
	 *            the new state
	 */
	void setState(String state);

	/**
	 * Gets the geo coverage.
	 * 
	 * @return the geo coverage
	 */
	String getGeoCoverage();

	/**
	 * Sets the geo coverage.
	 * 
	 * @param geoCoverage
	 *            the new geo coverage
	 */
	void setGeoCoverage(String geoCoverage);

	/**
	 * Gets the images.
	 * 
	 * @return the images
	 */
	List<String> getImages();

	/**
	 * Gets the spatial data.
	 * 
	 * @return the spatial data
	 */
	SpatialData getSpatialData();
	
	/**
	 * Gets the spatial data value.
	 * 
	 * @return the spatial data value
	 */
	String getSpatialDataValue();

	/**
	 * Gets the extra.
	 * 
	 * @param name
	 *            the name
	 * @return the extra
	 */
	String getExtra(String name);

	/**
	 * Sets the extra.
	 * 
	 * @param name
	 *            the name
	 * @param value
	 *            the value
	 */
	void setExtra(String name, String value);

	/**
	 * Gets the type.
	 * 
	 * @return the type
	 */
	MetadataEnumType getType();

	/**
	 * Gets the average rating.
	 * 
	 * @return the average rating
	 */
	double getAverageRating();

	/**
	 * Gets the rating count.
	 * 
	 * @return the rating count
	 */
	int getRatingCount();

	/**
	 * set Resouces Modified flag
	 * 
	 */
	void setResoucesModified(boolean b);

	/**
	 * remove Resource from list
	 * 
	 */
	void removeResource(Resource r);

	/**
	 * Gets the spatial Reference.
	 * 
	 * @return the spatial Reference
	 */
	SpatialReference getSpatialReference();

	/**
	 * Gets the spatial Reference Ags.
	 * 
	 * @return the spatial Reference Ags
	 */
	String getSpatialReferenceAgs();

	/**
	 * Gets the spatial Reference Nuts.
	 * 
	 * @return the spatial Reference Nuts
	 */
	String getSpatialReferenceNuts();

	/**
	 * Gets the spatial Reference Uri.
	 * 
	 * @return the spatial Reference Uri
	 */
	String getSpatialReferenceUri();

	/**
	 * Gets the spatial Reference Text.
	 * 
	 * @return the spatial Reference Text
	 */
	String getSpatialReferenceText();

	void setSpatialDataValue(String value);

  public enum State
  {
    ACTIVE("active"),

    DELETED("deleted");

    private String value;

    private State(String value)
    {
      this.value = value;
    }

    public String getValue()
    {
      return this.value;
    }
  }

  void setType(MetadataEnumType type);

  void setCreated(Date date);
  
  String getOwnerOrg();

  void setOwnerOrg(String ownerOrg);
  
  boolean isPrivate();
  
  void setPrivate(boolean isPrivate);
}
