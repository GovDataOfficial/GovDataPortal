/**
 * Copyright (c) 2012, 2013 Fraunhofer Institute FOKUS
 * <p>
 * This file is part of Open Data Platform.
 * <p>
 * Open Data Platform is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * Open Data Plaform is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Affero General Public License
 * along with Open Data Platform.  If not, see <http://www.gnu.org/licenses/agpl-3.0>.
 */

package de.seitenbau.govdata.odp.registry.model;

import de.seitenbau.govdata.odp.registry.ckan.impl.ContactAddress;


/**
 * The Interface Contact.
 * @author sim
 */
public interface Contact
{

  ContactAddress getAddress();

  String getEmail();

  String getName();

  RoleEnumType getRole();

  String getUrl();

  void setEmail(String email);

  void setName(String name);

  void setRole(RoleEnumType role);

  void setUrl(String url);

  /**
   * Checks if there are any values set for this role.
   *
   * @return true if there are values, so the contact exists. False if there is
   *         no data.
   */
  boolean exists();

}
