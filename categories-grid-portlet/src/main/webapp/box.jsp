<%@ taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<li class="category-box design-box">
  <a href="${category.actionURL}" class="h2-tags">
    <span class="kat-img sbi-kat-${category.name.replace('_','-')}"></span>
    <div class="categoriy-data">
      <span class="vertical-align-spacer"></span>
      <div class="inline-block">
        <div class="category-name">${category.title}</div>
        <div><c:out value="(${category.count})" /></div>
      </div>
    </div>
  </a>
</li>