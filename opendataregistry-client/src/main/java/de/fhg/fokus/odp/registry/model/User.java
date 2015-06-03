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

import java.util.List;

// TODO: Auto-generated Javadoc
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

    /**
     * Gets the datasets.
     * 
     * @return the datasets
     */
    List<String> getDatasets();

    /**
     * Rate metadata.
     * 
     * @param metadata
     *            the metadata
     * @param rate
     *            the rate
     */
    void rateMetadata(String metadata, int rate);

    /**
     * Checks if is owner.
     * 
     * @param metadata
     *            the metadata
     * @return true, if is owner
     */
    boolean isOwner(Metadata metadata);

    /**
     * Checks if is editor.
     * 
     * @param metadata
     *            the metadata
     * @return true, if is editor
     */
    boolean isEditor(Metadata metadata);

    /**
     * Checks for role.
     * 
     * @param role
     *            the role
     * @param metadata
     *            the metadata
     * @return true, if successful
     */
    boolean hasRole(String role, Metadata metadata);

    /**
     * Checks for role.
     * 
     * @param role
     *            the role
     * @return true, if successful
     */
    boolean hasRole(String role);

    void updateRole(String role);
 
	void updateRoles(List<String> roles);

    public List<String> showRoles();

}
