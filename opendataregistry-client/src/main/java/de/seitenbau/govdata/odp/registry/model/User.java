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


/**
 * The Interface User.
 * 
 * @author sim
 */
public interface User {

    /**
     * Gets the apikey.
     * 
     * @return the apikey
     */
    // String getApikey();

    /**
     * Gets the name.
     * 
     * @return the name
     */
    String getName();

    /**
     * Gets the fullname.
     * 
     * @return the fullname
     */
    String getFullname();

    /**
     * Gets the display name.
     * 
     * @return the display name
     */
    String getDisplayName();

    /**
     * Gets the email.
     * 
     * @return the email
     */
    String getEmail();
    
    String getId();

    /**
   * Checks if is creator.
   * 
   * @param metadata the metadata
   * @return true, if is creator
   */
  boolean isCreator(Metadata metadata);

}
