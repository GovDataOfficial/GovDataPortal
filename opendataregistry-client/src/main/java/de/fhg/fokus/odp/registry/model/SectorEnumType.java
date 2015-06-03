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

package de.fhg.fokus.odp.registry.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Enum RoleEnumType.
 * 
 * @author sim
 */
public enum SectorEnumType {

    /** The public. */
    PUBLIC("oeffentlich", "Öffentlich"),

    /** The private. */
    PRIVATE("privat", "Privat"),

    /** The other. */
    OTHER("andere", "Andere");

    /**
     * The Constant log.
     */
    private static final Logger log = LoggerFactory.getLogger(SectorEnumType.class);

    /** The field. */
    private String field;

    /** The display name. */
    private String displayName;

    /**
     * Instantiates a new sector enum type.
     * 
     * @param field
     *            the field
     * @param displayName
     *            the display name
     */
    private SectorEnumType(String field, String displayName) {
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
     * @return the metadata enum type
     */
    public static SectorEnumType fromField(String type) {
        type = type.trim().toLowerCase();
        if (PUBLIC.toField().equals(type)) {
            return PUBLIC;
        } else if (PRIVATE.toField().equals(type)) {
            return PRIVATE;
        } else if (OTHER.toField().equals(type)) {
            return OTHER;
        } else {
            log.warn("Parsing SectorEnumType: " + type, new IllegalArgumentException(type));
            return OTHER;
        }
    }
}
