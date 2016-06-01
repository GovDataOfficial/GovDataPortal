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

import java.io.Serializable;

import com.fasterxml.jackson.databind.JsonNode;

import de.fhg.fokus.odp.registry.ckan.ODRClientImpl;
import de.fhg.fokus.odp.registry.ckan.json.ResourceBean;
import de.fhg.fokus.odp.registry.model.Resource;

public class ResourceImpl implements Resource, Serializable {

	/**
     * 
     */
	private static final long serialVersionUID = -4236686335813667952L;

	ResourceBean resource;

	public ResourceImpl(ResourceBean resource) {
		this.resource = resource;
	}

	@Override
	public String getUrl() {
		return resource.getUrl();
	}

	@Override
	public void setUrl(String url) {
		resource.setUrl(url);
	}

	@Override
	public String getFormat() {
		return resource.getFormat();
	}

	@Override
	public void setFormat(String format) {
		resource.setFormat(format);
	}

	@Override
	public String getDescription() {
		return resource.getDescription();
	}

	@Override
	public void setDescription(String description) {
		resource.setDescription(description);
	}

	@Override
	public String getLanguage() {
		return resource.getLanguage();
	}

	@Override
	public void setLanguage(String language) {
		resource.setLanguage(language);
	}

	@Override
	public String getHash() {
		return resource.getHash();
	}

	@Override
	public void setHash(String hash) {
		resource.setHash(hash);
	}

	@Override
	public String getType() {
		return resource.getResource_type();
	}

	@Override
	public void setType(String type) {
		resource.setResource_type(type);
	}

	public ResourceBean getBean() {
		return resource;
	}

	/* msg 10.04.2014 */
	@Override
	public String getId() {
		return resource.getId();
	}

	/* msg 10.04.2014 */
	/**
	 * write 
	 * @return
	 */
	public JsonNode write() {
		return ODRClientImpl.convert(resource);
	}

  @Override
  public String getName()
  {
    return resource.getName();
  }

  @Override
  public void setName(String name)
  {
    resource.setName(name);
  }
}
