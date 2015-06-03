/**
 * Copyright (c) 2012, 2014 Fraunhofer Institute FOKUS
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
package de.fhg.fokus.odp.registry.ckan.impl;

import java.io.Serializable;

import de.fhg.fokus.odp.registry.ckan.json.SpatialReferenceBean;
import de.fhg.fokus.odp.registry.model.SpatialReference;

/**
 * @author msg
 * 
 */
public class SpatialReferenceImpl implements SpatialReference, Serializable {

	/**
     * 
     */
	private static final long serialVersionUID = 9014868608456372491L;

	/** The spatialreference. */
	private final SpatialReferenceBean spatialreference;

	/**
	 * Instantiates a new spatialreference impl.
	 * 
	 * @param spatialreference
	 *            the spatialreference
	 */
	public SpatialReferenceImpl(SpatialReferenceBean spatialreference) {
		this.spatialreference = spatialreference;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.fhg.fokus.odp.registry.model.SpatialReference#getAgs()
	 */
	@Override
	public String getAgs() {
		return spatialreference.getAgs();
	}
	@Override
	public void setAgs(String ags) {
		 spatialreference.setAgs(ags);
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.fhg.fokus.odp.registry.model.SpatialReference#getNuts()
	 */
	@Override
	public String getNuts() {
		return spatialreference.getNuts();
	}

	@Override
	public void setNuts(String nuts) {
		 spatialreference.setNuts(nuts);
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.fhg.fokus.odp.registry.model.SpatialReference#getUri()
	 */
	@Override
	public String getUri() {
		return spatialreference.getUri();
	}

	@Override
	public void setUri(String uri) {
		 spatialreference.setUri(uri);
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.fhg.fokus.odp.registry.model.SpatialReference#getText()
	 */
	@Override
	public String getText() {
		return spatialreference.getText();
	}
	
	@Override
	public void setText(String text) {
		 spatialreference.setText(text);
	}
}
