<%@include file="/portlet-config/init.jsp" %>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %>

<liferay-portlet:actionURL portletConfiguration="true" var="configurationURL" />

<%
String blockedStates = GetterUtil.getString(portletPreferences.getValue("blockedStates", ""));
%>

<aui:form action="<%= configurationURL %>" method="post" name="fm">
    <aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= Constants.UPDATE %>" />
    
    <aui:input label="Blockiere die Suche für die folgenden Bundesländer (Komma separiert):" name="preferences--blockedStates--" type="text" value="<%= blockedStates %>" />

    <aui:button-row>
       <aui:button type="submit" />
    </aui:button-row>
</aui:form>