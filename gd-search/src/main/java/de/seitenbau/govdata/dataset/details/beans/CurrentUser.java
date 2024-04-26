package de.seitenbau.govdata.dataset.details.beans;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.liferay.portal.kernel.exception.SystemException;

import de.fhg.fokus.odp.entities.model.MetadataComment;
import de.seitenbau.govdata.odp.registry.model.Metadata;
import de.seitenbau.govdata.odp.registry.model.Organization;
import de.seitenbau.govdata.permission.PermissionUtil;
import lombok.Data;

/**
 * The Class CurrentUser.
 * 
 * @author rnoerenberg
 */
@Data
public class CurrentUser
{
  /** The ckanUser. */
  private String ckanUser;

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
