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

package de.seitenbau.govdata.odp.registry.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.seitenbau.govdata.odp.registry.model.exception.OpenDataRegistryException;

/**
 * The Enum SpatialEnumType.
 */
public enum SpatialEnumType {

    /** The polygon. */
    POLYGON("polygon", "Polygon");

    /**
     * The Constant log.
     */
    private static final Logger log = LoggerFactory.getLogger(SpatialEnumType.class);

    /** The field. */
    private String field;

    /** The display name. */
    private String displayName;

    /**
     * Instantiates a new spatial enum type.
     * 
     * @param field
     *            the field
     * @param displayName
     *            the display name
     */
    private SpatialEnumType(String field, String displayName) {
        this.field = field;
        this.displayName = displayName;
    }

    /**
     * To field.
     * 
     * @return the string
     */
    public String toField() {
        return field;
    }

    /**
     * Gets the display name.
     * 
     * @return the display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * From field.
     * 
     * @param type
     *            the type
     * @return the spatial enum type
     * @throws OpenDataRegistryException
     *             the open data registry exception
     */
    public static SpatialEnumType fromField(String type) throws OpenDataRegistryException {
        type = type.trim().toLowerCase();
        if (POLYGON.toField().equals(type)) {
            return POLYGON;
        } else {
            throw new OpenDataRegistryException(type);
        }
    }
}
