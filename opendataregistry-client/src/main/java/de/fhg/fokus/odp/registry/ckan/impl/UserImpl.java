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

/**
 * 
 */
package de.fhg.fokus.odp.registry.ckan.impl;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

import de.fhg.fokus.odp.registry.ckan.json.UserBean;
import de.fhg.fokus.odp.registry.model.Metadata;
import de.fhg.fokus.odp.registry.model.User;

/**
 * @author sim
 * 
 */
public class UserImpl implements User, Serializable {

  /**
     * 
     */
  private static final long serialVersionUID = -9199121089537222860L;

  private UserBean user;

  public UserImpl(UserBean user)
  {
    this.user = user;
  }

  // @Override
  public String getApikey()
  {
    return user.getApikey();
  }

  @Override
  public String getName()
  {
    return user.getName();
  }

  @Override
  public String getFullname()
  {
    return user.getFullname();
  }

  @Override
  public String getDisplayName()
  {
    return user.getDisplay_name();
  }

  @Override
  public String getEmail()
  {
    return user.getEmail();
  }

  @Override
  public boolean isCreator(Metadata metadata)
  {
    return StringUtils.equals(this.user.getId(), metadata.getCreatorUserId());
  }
  
  @Override
  public String toString()
  {
    return user.getId() + ": " + user.getName() + " / " + user.getDisplay_name() + " / " + user.getApikey();
  }

  @Override
  public String getId()
  {
    return user.getId();
  }

}
