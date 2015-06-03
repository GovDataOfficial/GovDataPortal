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
package de.fhg.fokus.odp.registry.model;

import java.util.List;

import de.fhg.fokus.odp.registry.ckan.json.PolygonBean;

/**
 * The Interface SpatialData.
 * 
 * @author sim
 * @author msg
 */
public interface SpatialData {

	/**
	 * Gets the type.
	 * 
	 * @return the type
	 */
	SpatialEnumType getType();

	void setType(SpatialEnumType type);

	/**
	 * Gets the coordinates.
	 * 
	 * @return the coordinates
	 */
	List<PolygonBean> getPolygons();

	/**
	 * Adds the coordinate.
	 * 
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 * @return the coordinate
	 */
	Coordinate addCoordinate(int index, double x, double y);
	 PolygonBean addPolygon();
}
