package de.seitenbau.govdata.user.menu.product.navigation.personal.menu;

import javax.portlet.PortletRequest;

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
 * @author rnoerenberg
 */
public abstract class BasePersonalMenuEntry implements PersonalMenuEntry
{
  protected static final String FRIENDLY_URL_PAGE_CREATE_METADATA = "/bearbeiten";

  public boolean canEditDatasets(PortletRequest portletRequest, PermissionChecker permissionChecker,
      LayoutLocalService layoutLocalService, GroupLocalService groupLocalService) throws PortalException
  {
    ThemeDisplay themeDisplay = (ThemeDisplay) portletRequest.getAttribute(WebKeys.THEME_DISPLAY);
    Group defaultSiteGroup = groupLocalService.getFriendlyURLGroup(themeDisplay.getCompanyId(), "/guest");
    Layout editDatasetLayout = layoutLocalService.getFriendlyURLLayout(defaultSiteGroup.getGroupId(), false,
        FRIENDLY_URL_PAGE_CREATE_METADATA);
    boolean canEditDatasets =
        LayoutPermissionUtil.contains(permissionChecker, editDatasetLayout, ActionKeys.VIEW);

    return canEditDatasets;
  }

}