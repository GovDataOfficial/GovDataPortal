/**
 * Copyright (c) 2015 SEITENBAU GmbH
 *
 * This file is part of Open Data Platform.
 *
 * Open Data Platform is free software: you can redistribute it and/or modify it under the terms of
 * the GNU Affero General Public License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 * 
 * Open Data Plaform is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with Open Data
 * Platform. If not, see <http://www.gnu.org/licenses/agpl-3.0>.
 */

package de.seitenbau.govdata.permission;

import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;

/**
 * Enthält Methoden für die Berechtigungsprüfungen.
 * 
 * @author rnoerenberg
 *
 */
public abstract class PermissionUtil
{
  private static final Logger log = LoggerFactory.getLogger(PermissionUtil.class);

  private static final String[] rolesWithPermission = new String[] {"Redakteur", "Chefredakteur",
      "Administrator"};

  /**
   * Prüft, ob der übergebene Benutzer die Rechte besitzt einen Kommentar zu erstellen.
   * 
   * @param liferayUser das User-Objekt.
   * @return true, falls der Benutzer die Rechte besitzt, ansonsten false.
   */
  public static boolean hasCreateCommentPermission(User liferayUser)
  {
    boolean result = false;
    // Alle registrierten Benutzer dürfen Kommentare editieren
    if (liferayUser != null)
    {
      result = true;
    }
    return result;
  }

  /**
   * Prüft, ob der übergebene Benutzer das Recht hat das Objekt zu editieren. Dazu sind besondere
   * Rechte (Redakteur oder Administrator) nötig oder der Benutzer muss ist der Eigentümer des
   * Objekts.
   * 
   * @param liferayUser das User-Objekt des aktuellen Benutzers.
   * @param ownerLiferayUserId die Liferay-UserId des Besitzers des Objekts.
   * @return
   */
  public static boolean hasEditCommentPermission(User liferayUser, long ownerLiferayUserId)
  {
    boolean result = false;

    if (PermissionUtil.isRedakteur(liferayUser) || isOwner(liferayUser, ownerLiferayUserId))
    {
      result = true;
    }

    return result;
  }

  /**
   * Prüft, ob der übergebene Benutzer das Recht hat das Objekt zu löschen. Dazu sind besondere
   * Rechte (Redakteur oder Administrator) nötig oder der Benutzer muss ist der Eigentümer des
   * Objekts.
   * 
   * @param liferayUser das User-Objekt des aktuellen Benutzers.
   * @param ownerLiferayUserId die Liferay-UserId des Besitzers des Objekts.
   * @return
   */
  public static boolean hasDeleteCommentPermission(User liferayUser, long ownerLiferayUserId)
  {
    boolean result = false;

    if (PermissionUtil.isRedakteur(liferayUser) || isOwner(liferayUser, ownerLiferayUserId))
    {
      result = true;
    }

    return result;
  }

  private static boolean isOwner(User liferayUser, long commentOwnerLiferayUserId)
  {
    return (liferayUser != null && liferayUser.getUserId() == commentOwnerLiferayUserId);
  }

  /**
   * Checks if is redakteur.
   * 
   * @return true, if is redakteur
   * @throws SystemException the system exception
   */
  public static boolean isRedakteur(User liferayUser)
  {
    final String method = "isRedakteur() : ";
    log.trace(method + "Start");

    boolean result = false;

    if (liferayUser != null)
    {
      List<Role> roles;
      try
      {
        roles = RoleLocalServiceUtil.getUserRoles(liferayUser.getUserId());
        for (Role role : roles)
        {
          String name = role.getName();
          if (ArrayUtils.contains(rolesWithPermission, name))
          {
            log.debug(method + "Current user has role editor or admin.");
            result = true;
          }
        }
      }
      catch (SystemException e)
      {
        log.warn(method + e.getMessage());
      }
    }
    log.trace(method + "End");
    return result;
  }
}
