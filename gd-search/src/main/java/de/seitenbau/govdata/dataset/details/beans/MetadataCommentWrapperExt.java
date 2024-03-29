package de.seitenbau.govdata.dataset.details.beans;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserConstants;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;

import de.fhg.fokus.odp.entities.model.MetadataComment;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MetadataCommentWrapperExt extends de.fhg.fokus.odp.entities.model.MetadataCommentWrapper
{
  /** serialVersionUID. */
  private static final long serialVersionUID = 5020210607704452320L;

  private User owner;

  private ThemeDisplay themeDisplay;

  /**
   * the constructor.
   * @param metadataComment
   */
  public MetadataCommentWrapperExt(MetadataComment metadataComment)
  {
    super(metadataComment);
    long userLiferayId = getUserLiferayId();
    try
    {
      owner = UserLocalServiceUtil.getUser(userLiferayId);
    }
    catch (PortalException | SystemException e)
    {
      log.debug("No owner found for Liferay ID {} in comment {}", userLiferayId, getPrimaryKey());
    }
  }

  /**
   * Get the screen name for the author.
   * @return
   */
  public String getAuthorScreenName()
  {
    final String method = "getAuthorScreenName() : ";
    log.trace(method + "Start");

    String result = LanguageUtil.get(themeDisplay.getLocale(), "anonymous");
    if (!isGuest())
    {
      result = owner.getScreenName();
    }

    log.trace(method + "End");
    return result;
  }

  /**
   * Get to full name for the author.
   * @return
   */
  public String getAuthorFullName()
  {
    final String method = "getAuthorFullName() : ";
    log.trace(method + "Start");

    String result = LanguageUtil.get(themeDisplay.getLocale(), "anonymous");
    if (!isGuest())
    {
      result = owner.getFullName();
    }

    log.trace(method + "End");
    return result;
  }

  /**
   * Get a link to the author portrait.
   * @return
   */
  public String getAuthorPortraitUrl()
  {
    final String method = "getAuthorPortraitUrl() : ";
    log.trace(method + "Start");
    
    String result = null;
    if (themeDisplay == null)
    {
      log.debug(method + "themeDisplay is null!");
    }
    else
    {
      // Set default image as fallback
      result = UserConstants.getPortraitURL(themeDisplay.getPathImage(), true, 0, null);

      if (!isGuest())
      {
        try
        {
          result = owner.getPortraitURL(themeDisplay);
        }
        catch (PortalException | SystemException e)
        {
          log.debug(method + "Cannot retrieve portrait URL.");
        }
      }

    }

    log.trace(method + "End");
    return result;
  }

  private boolean isGuest()
  {
    return owner == null || !owner.isActive();
  }

  public void setThemeDisplay(ThemeDisplay themeDisplay)
  {
    this.themeDisplay = themeDisplay;
  }
}
