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

/**
 * 
 */
package de.fhg.fokus.odp.registry.ckan.json;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import de.fhg.fokus.odp.registry.ckan.impl.ResourceImpl;
import de.fhg.fokus.odp.registry.model.Resource;

/**
 * @author sim
 * 
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class MetadataBean implements Serializable {

	/**
     * 
     */
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

	@JsonProperty
	private Date metadata_created;

	@JsonProperty
	private Date metadata_modified;

	@JsonProperty
	private boolean isopen;

	@JsonProperty
	private String author;

	@JsonProperty
	private String author_email;

	@JsonProperty
	private String notes;

	@JsonProperty
	private String state;

	@JsonProperty
	private String license_id;

	@JsonProperty
	private String license_title;

	@JsonProperty
	private String license_url;

	@JsonProperty
	private String type;

	@JsonProperty
	private String url;

	@JsonProperty
	private List<ExtraBean> extras;

	@JsonProperty
	private List<TagBean> tags;

	@JsonProperty
	private List<ResourceBean> resources;

	@JsonProperty
	private List<GroupBean> groups;

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title
	 *            the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the maintainer
	 */
	public String getMaintainer() {
		return maintainer;
	}

	/**
	 * @param maintainer
	 *            the maintainer to set
	 */
	public void setMaintainer(String maintainer) {
		this.maintainer = maintainer;
	}

	/**
	 * @return the maintainer_email
	 */
	public String getMaintainer_email() {
		return maintainer_email;
	}

	/**
	 * @param maintainer_email
	 *            the maintainer_email to set
	 */
	public void setMaintainer_email(String maintainer_email) {
		this.maintainer_email = maintainer_email;
	}

	/**
	 * @return the metadata_created
	 */
	public Date getMetadata_created() {
		return metadata_created == null ? null : (Date) metadata_created
				.clone();
	}

	/**
	 * @param metadata_created
	 *            the metadata_created to set
	 */
	public void setMetadata_created(Date metadata_created) {
		this.metadata_created = metadata_created == null ? null
				: (Date) metadata_created.clone();
	}

	/**
	 * @return the metadata_modified
	 */
	public Date getMetadata_modified() {
		return metadata_modified == null ? null : (Date) metadata_modified
				.clone();
	}

	/**
	 * @param metadata_modified
	 *            the metadata_modified to set
	 */
	public void setMetadata_modified(Date metadata_modified) {
		this.metadata_modified = metadata_modified == null ? null
				: (Date) metadata_modified.clone();
	}

	/**
	 * @return the author
	 */
	public String getAuthor() {
		return author;
	}

	/**
	 * @param author
	 *            the author to set
	 */
	public void setAuthor(String author) {
		this.author = author;
	}

	/**
	 * @return the author_email
	 */
	public String getAuthor_email() {
		return author_email;
	}

	/**
	 * @param author_email
	 *            the author_email to set
	 */
	public void setAuthor_email(String author_email) {
		this.author_email = author_email;
	}

	/**
	 * @return the notes
	 */
	public String getNotes() {
		return notes;
	}

	/**
	 * @param notes
	 *            the notes to set
	 */
	public void setNotes(String notes) {
		this.notes = notes;
	}

	/**
	 * @return the state
	 */
	public String getState() {
		return state;
	}

	/**
	 * @param state
	 *            the state to set
	 */
	public void setState(String state) {
		this.state = state;
	}

	/**
	 * @return the license_id
	 */
	public String getLicense_id() {
		return license_id;
	}

	/**
	 * @param license_id
	 *            the license_id to set
	 */
	public void setLicense_id(String license_id) {
		this.license_id = license_id;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url
	 *            the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @return the license_title
	 */
	public String getLicense_title() {
		return license_title;
	}

	/**
	 * @param license_title
	 *            the license_title to set
	 */
	public void setLicense_title(String license_title) {
		this.license_title = license_title;
	}

	/**
	 * @return the isopen
	 */
	public boolean isIsopen() {
		return isopen;
	}

	/**
	 * @param isopen
	 *            the isopen to set
	 */
	public void setIsopen(boolean isopen) {
		this.isopen = isopen;
	}

	/**
	 * @return the license_url
	 */
	public String getLicense_url() {
		return license_url;
	}

	/**
	 * @param license_url
	 *            the license_url to set
	 */
	public void setLicense_url(String license_url) {
		this.license_url = license_url;
	}

	/**
	 * @return the extras
	 */
	@JsonIgnore
	public List<ExtraBean> getExtras() {
		if (extras == null) {
			extras = new ArrayList<ExtraBean>();
		}
		return extras;
	}

	/**
	 * @param extras
	 *            the extras to set
	 */
	public void setExtras(List<ExtraBean> extras) {
		this.extras = extras;
	}

	/**
	 * @return the tags
	 */
	@JsonIgnore
	public List<TagBean> getTags() {
		if (tags == null) {
			tags = new ArrayList<TagBean>();
		}
		return tags;
	}

	/**
	 * @param tags
	 *            the tags to set
	 */
	public void setTags(List<TagBean> tags) {
		this.tags = tags;
	}

	/**
	 * @return the resources
	 */
	public List<ResourceBean> getResources() {
		if (resources == null) {
			resources = new ArrayList<ResourceBean>();
		}
		return resources;
	}

	/**
	 * @param resources
	 *            the resources to set
	 */
	public void setResources(List<ResourceBean> resources) {
		this.resources = resources;
	}

	/**
	 * @return the groups
	 */
	@JsonIgnore
	public List<GroupBean> getGroups() {
		if (groups == null) {
			groups = new ArrayList<GroupBean>();
		}
		return groups;
	}

	/**
	 * @param groups
	 *            the groups to set
	 */
	public void setGroups(List<GroupBean> groups) {
		this.groups = groups;
	}

}
