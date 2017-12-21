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
package de.seitenbau.govdata.odp.registry.ckan.json;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import de.seitenbau.govdata.odp.registry.model.Coordinate;

/**
 * @author msg
 * 
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class PolygonBean implements Serializable {

	/**
     * 
     */
	private static final long serialVersionUID = -4626804446948451352L;

	//private static final Logger log = LoggerFactory.getLogger(PolygonBean.class);

	@JsonProperty
	private List<Coordinate> coordinates;

	/**
	 * @return the coordinates
	 */
	public List<Coordinate> getCoordinates() {
		if (coordinates == null) {
			coordinates = new ArrayList<Coordinate>();
		}
		return coordinates;
	}

	/**
	 * @param coordinates
	 *            the coordinates to set
	 */
	public void setCoordinates(List<Coordinate> coordinates) {
		this.coordinates = coordinates;
	}

	public void addCoordinate(Coordinate coordinate) {
		if (coordinates == null) {			
			coordinates = new ArrayList<Coordinate>();
		}
		this.coordinates.add(coordinate);
	}
}
