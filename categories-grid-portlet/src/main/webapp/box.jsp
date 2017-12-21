<%@ taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<li class="category-box design-box">
  <a href="${category.actionURL}" class="h2-tags">
    <img class="kat-img kat-img-normal" alt="" src="/govdatastyle-theme/images/categories/icon_kat_${category.name}.svg" width="60" height="60" />
    <!-- Since IE10 / IE11 can not invert using CSS, we use a proxy SVG with filter to invert the image -->
    <svg xmlns="http://www.w3.org/2000/svg" class="kat-img kat-img-inverted" viewBox="0 0 60 60" width="60" height="60">
      <defs>
        <filter id="filterCategory_${category.name}">
          <feColorMatrix in="SourceGraphic" type="matrix" values="-1 0 0 0 1 0 -1 0 0 1 0 0 -1 0 1 0 0 0 1 0" />
        </filter>
      </defs>
      <image filter="url(&quot;#filterCategory_${category.name}&quot;)" x="0" y="0" width="60" height="60"
             xmlns:xlink="http://www.w3.org/1999/xlink"
             xlink:href="/govdatastyle-theme/images/categories/icon_kat_${category.name}.svg" />
    </svg>
    <div class="category-data">
      <span class="vertical-align-spacer"></span>
      <div class="inline-block">
        <div class="category-name">${category.title}</div>
        <div><c:out value="(${category.count})" /></div>
      </div>
    </div>
  </a>
</li>