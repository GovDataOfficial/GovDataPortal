<%@include file="/portlet-config/init.jsp" %>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %>

<liferay-portlet:actionURL portletConfiguration="true" var="configurationURL" />

<%
String selectedPage = GetterUtil.getString(portletPreferences.getValue("selectedPage", "quality-features"));
%>

<aui:form action="<%= configurationURL %>" method="post" name="fm">
    <aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= Constants.UPDATE %>" />
    
    <aui:select label="Auf welcher Seite soll das Portlet aufgesetzt werden?" name="preferences--selectedPage--">
      <aui:option value="quality-features" label="quality-features-label" selected="<%= selectedPage.equals("quality-features") %>" />
      <aui:option value="tops" label="tops-label" selected="<%= selectedPage.equals("tops") %>"/>
    </aui:select>

    <aui:button-row>
       <aui:button type="submit" />
    </aui:button-row>
</aui:form>