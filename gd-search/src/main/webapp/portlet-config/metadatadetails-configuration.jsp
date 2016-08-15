<%@include file="/portlet-config/init.jsp" %>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %>

<liferay-portlet:actionURL portletConfiguration="true" var="configurationURL" />

<%
boolean ratingsEnabled = GetterUtil.getBoolean(portletPreferences.getValue("ratingsEnabled", StringPool.TRUE));
boolean commentsEnabled = GetterUtil.getBoolean(portletPreferences.getValue("commentsEnabled", StringPool.TRUE));
boolean guestCommentsEnabled = GetterUtil.getBoolean(portletPreferences.getValue("guestCommentsEnabled", StringPool.TRUE));
boolean showBrokenLinksHint = GetterUtil.getBoolean(portletPreferences.getValue("showBrokenLinksHint", StringPool.TRUE));
%>

<aui:form action="<%= configurationURL %>" method="post" name="fm">
    <aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= Constants.UPDATE %>" />
    
    <aui:input label="Bewertungen aktivieren" name="preferences--ratingsEnabled--" type="checkbox" value="<%= ratingsEnabled %>" />
    <aui:input label="Kommentare aktivieren" name="preferences--commentsEnabled--" type="checkbox" value="<%= commentsEnabled %>" />
    <aui:input label="Gäste dürfen kommentieren" name="preferences--guestCommentsEnabled--" type="checkbox" value="<%= guestCommentsEnabled %>" />
    <aui:input label="Hinweis zu nicht erreichbaren Links anzeigen" name="preferences--showBrokenLinksHint--" type="checkbox" value="<%= showBrokenLinksHint %>" />

    <aui:button-row>
       <aui:button type="submit" />
    </aui:button-row>
</aui:form>