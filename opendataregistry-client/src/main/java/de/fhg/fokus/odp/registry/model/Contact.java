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

import de.fhg.fokus.odp.registry.model.exception.OpenDataRegistryException;
import de.fhg.fokus.odp.registry.model.exception.UnknownRoleException;

// TODO: Auto-generated Javadoc
/**
 * The Interface Contact.
 * 
 * @author sim
 */
public interface Contact {

    /**
     * Gets the name.
     * 
     * @return the name
     */
    String getName();

    /**
     * Sets the name.
     * 
     * @param name
     *            the new name
     */
    void setName(String name);

    /**
     * Gets the role.
     * 
     * @return the role
     * @throws OpenDataRegistryException
     *             the open data registry exception
     */
    RoleEnumType getRole() throws UnknownRoleException;

    /**
     * Sets the role.
     * 
     * @param role
     *            the new role
     */
    void setRole(RoleEnumType role);

    /**
     * Gets the url.
     * 
     * @return the url
     */
    String getUrl();

    /**
     * Sets the url.
     * 
     * @param url
     *            the new url
     */
    void setUrl(String url);

    /**
     * Gets the email.
     * 
     * @return the email
     */
    String getEmail();

    /**
     * Sets the email.
     * 
     * @param email
     *            the new email
     */
    void setEmail(String email);

    /**
     * Gets the address.
     * 
     * @return the address
     */
    String getAddress();

    /**
     * Sets the address.
     * 
     * @param address
     *            the new address
     */
    void setAddress(String address);

}
