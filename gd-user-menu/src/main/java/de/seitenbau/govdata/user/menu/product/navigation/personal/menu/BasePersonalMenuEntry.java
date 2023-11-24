package de.seitenbau.govdata.user.menu.product.navigation.personal.menu;

import javax.portlet.PortletRequest;

import com.liferay.portal.kernel.exception.NoSuchLayoutException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.permission.LayoutPermissionUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.product.navigation.personal.menu.PersonalMenuEntry;

/**
 * Class BasePersonalMenuEntry
 * @author rnoerenberg
 */
public abstract class BasePersonalMenuEntry implements PersonalMenuEntry
{
  /**
   * Friendly URL for edit dataset form
   */
  protected static final String FRIENDLY_URL_PAGE_CREATE_METADATA = "/bearbeiten";

  /**
   * Friendly URL for edit showcase form
   */
  protected static final String FRIENDLY_URL_PAGE_CREATE_SHOWCASE = "/showcase-bearbeiten";

  /**
   * Check edit permissions for dataset
   * @param portletRequest
   * @param permissionChecker
   * @param layoutLocalService
   * @param groupLocalService
   * @return
   * @throws PortalException
   */
  public boolean canEditDatasets(PortletRequest portletRequest, PermissionChecker permissionChecker,
      LayoutLocalService layoutLocalService, GroupLocalService groupLocalService) throws PortalException
  {
    return hasPermissions(portletRequest, permissionChecker, layoutLocalService, groupLocalService,
        FRIENDLY_URL_PAGE_CREATE_METADATA);
  }

  /**
   * Check access permissions for dataset
   * @param portletRequest
   * @param permissionChecker
   * @param layoutLocalService
   * @param groupLocalService
   * @param friendlyURL
   * @return
   * @throws PortalException
   */
  public boolean hasPermissions(PortletRequest portletRequest, PermissionChecker permissionChecker,
      LayoutLocalService layoutLocalService, GroupLocalService groupLocalService, String friendlyURL)
      throws PortalException
  {
    boolean hasAccess = false;
    ThemeDisplay themeDisplay = (ThemeDisplay) portletRequest.getAttribute(WebKeys.THEME_DISPLAY);
    Group defaultSiteGroup = groupLocalService.getFriendlyURLGroup(themeDisplay.getCompanyId(), "/guest");
    try
    {
      Layout friendlyURLLayout = layoutLocalService.getFriendlyURLLayout(defaultSiteGroup.getGroupId(), false,
          friendlyURL);
      hasAccess =
        LayoutPermissionUtil.contains(permissionChecker, friendlyURLLayout, ActionKeys.VIEW);
    }
    catch (NoSuchLayoutException ex)
    {
      // do nothing
    }

    return hasAccess;
  }

}