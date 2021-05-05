<#ftl output_format="HTML" auto_esc=false>
<!DOCTYPE html>

<#include init />

<html class="${root_css_class}" dir="<@liferay.language key="lang.dir" />" lang="${w3c_language_id}">

<head>
  <title>${html_title}</title>

  <meta content="initial-scale=1.0, width=device-width" name="viewport" />

  <@liferay_util["include"] page=top_head_include />
  <script src="${javascript_folder}/modernizr.js"></script>
</head>

<body class="${css_class}">
  <div class="off-canvas-wrap" data-offcanvas>
    <div class="inner-wrap">
      <h1 class="offscreen">Govdata - Das Datenportal für Deutschland</h1>

      <aside class="right-off-canvas-menu">
        <h2 class="offscreen">Off-Canvas Seitenmenü</h2>
        <div class="off-canvas-inner-wrap">
          <a class="off-canvas-close sbi-close" href="#">
            <span class="offscreen">close menu</span>
          </a>
          <div class="off-canvas-container" id="off-canvas-container"></div>
          <div class="off-canvas-container" id="off-canvas-mainmenu">
            <#if has_navigation && is_setup_complete>
              <#include "${full_templates_path}/navigation-offcanvas.ftl" />
            </#if>
          </div>
        </div>
      </aside>

<@liferay_ui["quick-access"] contentId="#main-content" />

<@liferay_util["include"] page=body_top_include />

<#if showcontrolmenu>
  <@liferay.control_menu />
</#if>

      <header id="banner" role="banner">
        <div class="portlet-dockbar">
          <div class="navbar navbar-top dockbar">
            <div class="navbar-inner">
              <div class="container">
                <span>
                  <a id="_145_navSiteNavigationNavbarBtn" class="btn btn-navbar" tabindex="0">
                    <i class="icon-reorder"></i>
                  </a>
                </span>
                <span>
                  <a class="brand show-for-medium-down" href="/">
                    <img alt="<@liferay.language key="od.page.navigate-to-start" />" height="${site_logo_height}" src="${logo_govdata_src}" width="${site_logo_width}" />
                  </a>
                  <div class="collapse nav-collapse">
                    <ul aria-label="Werkzeugleiste" class="nav nav-account-controls" role="menubar">
                      <li id="topnavi-home"><a href="/"><@liferay.language key="od.page.start" /></a></li>
                      <#if hasChecklistViewPermission>
                        <li id="topnavi-checklists"><a href="/checklisten"><@liferay.language key="od.page.checklists" /></a></li>
                      </#if>
                      <#if !is_signed_in>
                        <li class="sign-in">
                          <a data-redirect="${is_login_redirect_required?string}" href="${sign_in_url}" id="sign-in" class="sign-in" rel="nofollow" title="${sign_in_register_text}">
                            <i class="sbi-eingeloggter-user"></i>
                            <span class="nav-item-label">${sign_in_register_text}</span>
                          </a>
                        </li>
                      <#else>
                        <li>
                          <@liferay.user_personal_bar />
                        </li>
                      </#if>
                      <li id="topnavi-faq"><a href="/web/guest/faq"><@liferay.language key="od.page.faq" /></a></li>
                      <li id="topnavi-contact"><a href="/web/guest/kontakt"><@liferay.language key="od.page.contact" /></a></li>
                    </ul>
                  </div>
                </span>
              </div>
            </div>
          </div>
        </div>
  
        <#if has_navigation && is_setup_complete>
          <#include "${full_templates_path}/navigation.ftl" />
        </#if>
      </header>

      <section id="content">
        <h2 class="hide-accessible" role="heading" aria-level="1">${the_title?esc}</h2>

        <#if selectable>
          <@liferay_util["include"] page=content_include />
        <#else>
          ${portletDisplay.recycle()}

          ${portletDisplay.setTitle(the_title)}

          <@liferay_theme["wrap-portlet"] page="portlet.ftl">
            <@liferay_util["include"] page=content_include />
          </@>
        </#if>
      </section>

      <div class="share-section row">
        <div class="share-borderbox">
          <div class="share-container">
            <h2 class="share-label"><@liferay.language key="od.share" /></h2>
            <span class="shariff"
              data-services="[&quot;twitter&quot;, &quot;facebook&quot;, &quot;diaspora&quot;, &quot;mail&quot;]"
              data-title="${the_title?esc} - ${company_name?esc}"
              data-mail-url="mailto:"
              data-mail-subject="${the_title?esc} - ${company_name?esc}"></span>
          </div>
          <div class="clearfix"></div>
        </div>
      </div>

      <footer>
        <h2 class="offscreen">weiterführende Links / Soziales</h2>
        <div class="row row-margin">
          <div class="column small-12 medium-7 medium-push-5">
            <ul class="footer-links h2-metainfo">
              <li>
                <a href="/web/guest/nutzungsbestimmungen">Nutzungshinweise</a>
              </li>
              <li>
                <a href="/web/guest/datenschutz">Datenschutzerklärung</a>
              </li>
              <li>
                <a href="/web/guest/impressum">Impressum</a>
              </li>
              <li>
                <a href="/web/guest/sitemap"><@liferay.language key="sitemap" /></a>
              </li>
            </ul>
          </div>
          <div class="column small-12 medium-5 medium-pull-7">
            <h2 class="footer-text">
              Besuchen Sie unsere Social-Media-Kanäle
            </h2>
            <ul class="social-icons">
              <li>
                <a href="https://twitter.com/govdata_de" target="_blank" title="Twitter">
                  <i class="icon-twitter"></i>
                  <span class="offscreen">Twitter</span>
                </a>
              </li>
              <li>
                <a href="https://www.facebook.com/govdata.de/" target="_blank" title="Facebook">
                  <i class="icon-facebook"></i>
                  <span class="offscreen">Facebook</span>
                </a>
              </li>
            </ul>
          </div>
        </div>
      </footer>

      <a class="exit-off-canvas">
        <span class="offscreen">close menu</span>
      </a>

      <script src="${javascript_folder}/jquery.js"></script>
      <script src="${javascript_folder}/foundation.js"></script>
      <script src="${javascript_folder}/foundation.offcanvas.js"></script>
      <script src="${javascript_folder}/foundation.dropdown.js"></script>
      <script>
        $(document).foundation();
      </script>
      <script src="${javascript_folder}/shariff.min.js"></script>
      <script src="${javascript_folder}/customize.js?t=${theme_timestamp}"></script>
    </div>
  </div>

<@liferay_util["include"] page=body_bottom_include />

<@liferay_util["include"] page=bottom_include />

</body>

<#include "${full_templates_path}/_version.ftl" />

</html>