<#ftl output_format="HTML" auto_esc=false>
<nav class="${nav_css_class}" id="navigation" role="navigation">
  <h2 class="offscreen">Navigation</h2>
  <div class="navbar-inner">
    <div class="collapse nav-collapse">
      <a class="brand" href="/"><img alt="<@liferay.language key="od.page.navigate-to-start" />" height="${site_logo_height}" src="${logo_govdata_src}" width="${site_logo_width}" /></a>

      <ul aria-label="<@liferay.language key="site-pages" />" class="nav nav-collapse" role="menubar">
        <#foreach nav_item in nav_items>
          <#assign
            nav_item_attr_selected=""
            nav_item_attr_has_popup=""
            nav_item_caret=""
            nav_item_css_class="lfr-nav-item"
            nav_item_link_css_class=""
            nav_item_lang=""
          />

          <#if nav_item.isSelected()>
            <#assign
              nav_item_attr_selected="aria-selected='true'"
              nav_item_css_class="${nav_item_css_class} selected active"
            />
          </#if>

          <#if nav_item.getName() == "Blog" || nav_item.getName() == "Apps">
            <#assign
              nav_item_lang=" lang='en'"
            />
          </#if>

          <li class="${nav_item_css_class}" id="layout_${nav_item.getLayoutId()}" ${nav_item_attr_selected} role="presentation">
            <a aria-labelledby="layout_${nav_item.getLayoutId()}" ${nav_item_lang} ${nav_item_attr_has_popup} class="${nav_item_link_css_class}" title="${nav_item.getName()?esc}" href="${nav_item.getURL()}" ${nav_item.getTarget()} role="menuitem">
              <span>${nav_item.iconURL()} ${nav_item.getName()?esc} ${nav_item_caret}</span>
            </a>
          </li>
        </#foreach>
      </ul>
    </div>
  </div>
</nav>