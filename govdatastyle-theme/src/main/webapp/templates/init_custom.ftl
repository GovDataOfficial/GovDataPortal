<#assign
  root_css_class = "aui " + root_css_class
  nav_css_class = nav_css_class + " navbar site-navigation"
  logo_govdata_src = images_folder + "/logo.png"
  groupService = serviceLocator.findService("com.liferay.portal.kernel.service.GroupLocalService")
  layoutService = serviceLocator.findService("com.liferay.portal.kernel.service.LayoutLocalService")
  defaultSiteGroup = groupService.getFriendlyURLGroup(theme_display.getCompanyId(), "/guest")
  checklistsLayout = (layoutService.fetchLayoutByFriendlyURL(defaultSiteGroup.getGroupId(), false, "/checklisten"))!""
  permission_checker = theme_display.getPermissionChecker()
  hasChecklistViewPermission = false
  showcontrolmenu = false
/>

<#assign
  sign_in_register_text = languageUtil.get(locale, "sign-in-register")
  sign_in_text = languageUtil.get(locale, "sign-in")
  sign_in_url = htmlUtil.escape(theme_display.getURLSignIn())
/>

<#if checklistsLayout?has_content>
  <#assign
    hasChecklistViewPermission = layoutPermission.contains(permission_checker, checklistsLayout, "VIEW")
  />
</#if>

<#if is_signed_in>
  <#assign
    roles = user.getRoles()
    roles_control_menu_allowed = ["Chefredakteur", "Redakteur", "Geschaeftsstelle"]
  />
  <#list roles as role>
    <#if permission_checker.isOmniadmin() || permission_checker.isCompanyAdmin() || roles_control_menu_allowed?seq_contains(role.getName()) >
        <#assign showcontrolmenu = true />
        <#break>
    </#if>
  </#list>
</#if>