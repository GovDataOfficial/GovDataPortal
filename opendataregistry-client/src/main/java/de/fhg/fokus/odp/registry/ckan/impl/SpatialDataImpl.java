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
import java.util.List;

import org.codehaus.jackson.JsonNode;

import de.fhg.fokus.odp.registry.ckan.ODRClientImpl;
import de.fhg.fokus.odp.registry.ckan.json.PolygonBean;
import de.fhg.fokus.odp.registry.ckan.json.SpatialDataBean;
import de.fhg.fokus.odp.registry.model.Coordinate;
import de.fhg.fokus.odp.registry.model.SpatialData;
import de.fhg.fokus.odp.registry.model.SpatialEnumType;

/**
 * @author sim
 * @author msg
 * 
 */
public class SpatialDataImpl implements SpatialData, Serializable {

	/**
     * 
     */
	private static final long serialVersionUID = 8914868607356372491L;

	/** The spatialdata. */
	private final SpatialDataBean spatialdata;

	/**
	 * Instantiates a new spatialdata impl.
	 * 
	 * @param spatialdata
	 *            the spatialdata
	 */
	public SpatialDataImpl(SpatialDataBean spatialdata) {
		this.spatialdata = spatialdata;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.fhg.fokus.odp.registry.model.SpatialData#getType()
	 */
	@Override
	public SpatialEnumType getType() {
		return spatialdata.getType();
	}

	/**
	 * Sets the type.
	 * 
	 * @param type
	 *            the new type
	 * @Override public void setType(String type) { spatialdata.setType(type); }
	 */

	@Override
	public void setType(SpatialEnumType fromField) {
		spatialdata.setType(fromField);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.fhg.fokus.odp.registry.model.SpatialData#getPolygons()
	 */
	@Override
	public List<PolygonBean> getPolygons() {
		return spatialdata.getCoordinates();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.fhg.fokus.odp.registry.model.SpatialData#addPolygon(PolygonBean)
	 */
	@Override
	public PolygonBean addPolygon() {
		PolygonBean polygon = new PolygonBean();
		spatialdata.addPolygon(polygon);
		return polygon;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.fhg.fokus.odp.registry.model.SpatialData#addCoordinate(int,
	 * double, double)
	 */
	@Override
	public Coordinate addCoordinate(int index, double x, double y) {
		Coordinate coordinate = new CoordinateImpl(x, y);
		spatialdata.addCoordinate(index, coordinate);
		return coordinate;
	}

	/**
	 * write
	 * 
	 * @return
	 */
	public JsonNode write() {
		return ODRClientImpl.convert(spatialdata);
	}

}
