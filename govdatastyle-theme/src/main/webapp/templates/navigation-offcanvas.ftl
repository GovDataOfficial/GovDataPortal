<#ftl output_format="HTML" auto_esc=false>
<div class="oc-languagechooser">
  <!--<span class="buttongroup">
    <a class="linkbutton selected" href="#"><@liferay.language key="od.language.de" /></a><a class="linkbutton" href="#"><@liferay.language key="od.language.en" /></a>
  </span>-->
  <#if is_signed_in>
    <a class="linkbutton rounded" href="${sign_out_url}"><@liferay.language key="sign-out" /></a>
  <#else>
    <a class="linkbutton rounded" href="${sign_in_url}"><@liferay.language key="sign-in-register" /></a>
  </#if>
</div>

<#if is_signed_in>
  <div class="oc-accountinfo">
      <div class="oc-username">${user_name?esc}</div>
      <#if show_my_account>
      <a class="oc-profile-sub" href="${my_account_url}" title='<@liferay.language key="manage-my-account" />'>${my_account_text}</a>
      </#if>

      <#assign
        editDatasetLayout = (layoutService.fetchLayoutByFriendlyURL(theme_display.getSiteGroupId(), false, "/bearbeiten"))!""
      />
      <#if editDatasetLayout?has_content && layoutPermission.contains(permissionChecker, editDatasetLayout, "VIEW")>
        <a class="oc-profile-sub" href="/web/guest/suchen/-/searchresult/f/onlyEditorMetadata%3AonlyEditorMetadata%2C" title='<@liferay.language key="od.page.showOwnDatasets" />'>
          <@liferay.language key="od.page.showOwnDatasets" />
        </a>
      </#if>

  </div>
</#if>

<ul aria-label="<@liferay.language key="site-pages" />" class="off-canvas-nav" role="menubar">
  <#foreach nav_item in nav_items>
    <#assign
      nav_item_attr_selected=""
      nav_item_css_class=""
      nav_item_lang=""
    />

    <#if nav_item.isSelected()>
      <#assign
        nav_item_attr_selected="aria-selected='true'"
        nav_item_css_class="${nav_item_css_class} selected open"
      />
    </#if>

    <#if nav_item.getName() == "Blog" || nav_item.getName() == "Apps">
      <#assign nav_item_lang=" lang='en'" />
    </#if>

    <#if nav_item.hasChildren()>

      <li class="${nav_item_css_class} dropdown" ${nav_item_attr_selected} role="presentation">
        <button aria-haspopup='true' class="dropdown-toggle" title="${nav_item.getName()?esc}" role="menuitem">
          <span>${nav_item.iconURL()} ${nav_item.getName()?esc}</span>
        </button>

        <ul class="child-menu" role="menu">
          <#foreach nav_child in nav_item.getChildren()>
            <#assign
              nav_child_attr_selected=""
              nav_child_css_class=""
            />

            <#if nav_child.isSelected()>
              <#assign
                nav_child_attr_selected="aria-selected='true'"
                nav_child_css_class="selected"
              />
            </#if>

            <li class="${nav_child_css_class}" ${nav_child_attr_selected} role="presentation">
              <a title="${nav_child.getName()?esc}" href="${nav_child.getURL()}" ${nav_child.getTarget()} role="menuitem">${nav_child.getName()?esc}</a>
            </li>
          </#foreach>
        </ul>
      </li>

    <#else>

      <li class="${nav_item_css_class}" ${nav_item_attr_selected} role="presentation">
        <a title="${nav_item.getName()?esc}" ${nav_item_lang} href="${nav_item.getURL()}" ${nav_item.getTarget()} role="menuitem">
          <span>${nav_item.iconURL()} ${nav_item.getName()?esc}</span>
        </a>
      </li>

    </#if>
  </#foreach>
</ul>

<ul class="oc-meta-menu">
  <li><a href="/"><@liferay.language key="od.page.start" /></a></li>
  <li><a href="/web/guest/faq"><@liferay.language key="od.page.faq" /></a></li>
  <li><a href="/web/guest/kontakt"><@liferay.language key="od.page.contact" /></a></li>
</ul>
