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

import de.seitenbau.govdata.odp.registry.model.exception.OpenDataRegistryException;

// TODO: Auto-generated Javadoc
/**
 * The Enum TemporalGranularityEnumType.
 */
public enum TemporalGranularityEnumType {

    /**
     * The second.
     */
    SECOND("sekunde", "Sekunde"),
    /**
     * The minute.
     */
    MINUTE("minute", "Minute"),
    /**
     * The hour.
     */
    HOUR("stunde", "Stunde"),
    /**
     * The day.
     */
    DAY("tag", "Tag"),
    /**
     * The week.
     */
    WEEK("woche", "Woche"),
    /**
     * The month.
     */
    MONTH("monat", "Monat"),
    /**
     * The year.
     */
    YEAR("jahr", "Jahr"), NO_MATCH("nomatch", "Trifft nicht zu");
    /**
     * The field.
     */
    private String field;
    /**
     * The display name.
     */
    private String displayName;

    /**
     * Instantiates a new temporal granularity enum type.
     * 
     * @param field
     *            the field
     * @param displayName
     *            the display name
     */
    private TemporalGranularityEnumType(String field, String displayName) {
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
     * @throws OpenDataRegistryException
     *             the open data registry exception
     */
    public static TemporalGranularityEnumType fromField(String type) throws OpenDataRegistryException {
        type = type.trim().toLowerCase();
        if (SECOND.toField().equals(type)) {
            return SECOND;
        } else if (MINUTE.toField().equals(type)) {
            return MINUTE;
        } else if (HOUR.toField().equals(type)) {
            return HOUR;
        } else if (DAY.toField().equals(type)) {
            return DAY;
        } else if (WEEK.toField().equals(type)) {
            return WEEK;
        } else if (MONTH.toField().equals(type)) {
            return MONTH;
        } else if (YEAR.toField().equals(type)) {
            return YEAR;
        } else if (NO_MATCH.toField().equals(type)) {
            return NO_MATCH;
        } else {
            throw new OpenDataRegistryException(type);
        }
    }
}
