<%@include file="/portlet-config/init.jsp" %>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %>

<liferay-portlet:actionURL portletConfiguration="true" var="configurationURL" />

<%
String showcaseId = GetterUtil.getString(portletPreferences.getValue("showcaseId", ""));
boolean showShowroomButton = GetterUtil.getBoolean(portletPreferences.getValue("showShowroomButton", StringPool.TRUE));
%>

<aui:form action="<%= configurationURL %>" method="post" name="fm">
    <aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= Constants.UPDATE %>" />
    
    <aui:input label="Zeige Link zum Showroom" name="preferences--showShowroomButton--" type="checkbox" value="<%= showShowroomButton %>" />
    <aui:input label="ID des Featuted Showcase angeben" name="preferences--showcaseId--" type="text" value="<%= showcaseId %>" />

    <aui:button-row>
       <aui:button type="submit" />
    </aui:button-row>
</aui:form>