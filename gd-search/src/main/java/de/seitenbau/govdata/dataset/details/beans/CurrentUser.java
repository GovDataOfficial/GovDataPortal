/**
 * Copyright (c) 2012, 2013 Fraunhofer Institute FOKUS
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

/**
 * 
 */
package de.seitenbau.govdata.dataset.details.beans;

import java.util.List;

import lombok.Data;

import org.elasticsearch.common.lang3.StringUtils;

import com.liferay.portal.kernel.exception.SystemException;

import de.fhg.fokus.odp.entities.model.MetadataComment;
import de.seitenbau.govdata.odp.registry.model.Metadata;
import de.seitenbau.govdata.odp.registry.model.Organization;
import de.seitenbau.govdata.odp.registry.model.User;
import de.seitenbau.govdata.permission.PermissionUtil;

/**
 * The Class CurrentUser.
 * 
 * @author rnoerenberg
 */
@Data
public class CurrentUser
{
  /** The ckanUser. */
  private User ckanUser;

  /** The Liferay-User. */
  private com.liferay.portal.kernel.model.User liferayUser;

  /** The new metadata button value. */
  private String newMetadataButtonValue;

  /** The list of organizations where the user is editor. */
  private List<Organization> editorOrganizationList;

  /**
   * Checks if is redakteur.
   * 
   * @return true, if is redakteur
   * @throws SystemException the system exception
   */
  public boolean isRedakteur() throws SystemException
  {
    return PermissionUtil.isRedakteur(liferayUser);
  }

  /**
   * Checks if the current user is owner of the metadat comment.
   * 
   * @param metadataComment the metadata comment
   * @return true, if is owner
   */
  public boolean isOwner(MetadataComment metadataComment)
  {
    boolean result = false;

    if (liferayUser != null && metadataComment != null)
    {
      if (metadataComment.getUserLiferayId() == liferayUser.getUserId())
      {
        result = true;
      }
    }

    return result;
  }

  /**
   * Checks if is loggedin.
   * 
   * @return true, if is loggedin
   */
  public boolean isLoggedin()
  {
    return liferayUser != null;
  }

  /**
   * Checks if is creator.
   * 
   * @param metadata the metadata
   * @return true, if is creator
   */
  public boolean isCreator(Metadata metadata)
  {
    boolean result = false;

    if (ckanUser != null && metadata != null)
    {
      result = ckanUser.isCreator(metadata);
    }

    return result;
  }

  /**
   * Checks if is editor.
   * 
   * @param metadata the metadata
   * @return true, if is editor
   */
  public boolean isEditor(Metadata metadata)
  {
    boolean result = false;
    if (metadata != null)
    {
      if (this.editorOrganizationList != null)
      {
        for (Organization org : this.editorOrganizationList)
        {
          if (StringUtils.equals(metadata.getOwnerOrg(), org.getId()))
          {
            result = true;
            break;
          }
        }
      }
    }
    return result;
  }
}
