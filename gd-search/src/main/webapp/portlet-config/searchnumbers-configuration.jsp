<%@include file="/portlet-config/init.jsp" %>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %>

<liferay-portlet:actionURL portletConfiguration="true" var="configurationURL" />

<%
boolean showTwitter = GetterUtil.getBoolean(portletPreferences.getValue("showTwitter", StringPool.FALSE));
%>

<aui:form action="<%= configurationURL %>" method="post" name="fm">
    <aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= Constants.UPDATE %>" />
    
    <aui:input label="Zeige die aktuellste Twittermeldung" name="preferences--showTwitter--" type="checkbox" value="<%= showTwitter %>" />

    <aui:button-row>
       <aui:button type="submit" />
    </aui:button-row>
</aui:form>