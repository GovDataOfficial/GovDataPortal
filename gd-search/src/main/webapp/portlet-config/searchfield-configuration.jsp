<%@include file="/portlet-config/init.jsp" %>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %>

<liferay-portlet:actionURL portletConfiguration="true" var="configurationURL" />

<%
boolean showBigHeader = GetterUtil.getBoolean(portletPreferences.getValue("showBigHeader", StringPool.FALSE));
boolean showSearch = GetterUtil.getBoolean(portletPreferences.getValue("showSearch", StringPool.TRUE));
String backgroundImage = GetterUtil.getString(portletPreferences.getValue("backgroundImage", "general"));
%>

<aui:form action="<%= configurationURL %>" method="post" name="fm">
    <aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= Constants.UPDATE %>" />
    
    <aui:input label="Großen Header anzeigen" name="preferences--showBigHeader--" type="checkbox" value="<%= showBigHeader %>" />
    <aui:input label="Suchfeld anzeigen" name="preferences--showSearch--" type="checkbox" value="<%= showSearch %>" />
    <aui:select label="Farbschema des Hintergrundbildes (nur für den kleinen Header)" name="preferences--backgroundImage--">
    <aui:option value="general" label="general" selected="<%= backgroundImage.equals(\"general\") %>" />
      <aui:option value="app" label="app" selected="<%= backgroundImage.equals(\"app\") %>" />
      <aui:option value="blog" label="blog" selected="<%= backgroundImage.equals(\"blog\") %>" />
      <aui:option value="data" label="data" selected="<%= backgroundImage.equals(\"data\") %>" />
      <aui:option value="document" label="document" selected="<%= backgroundImage.equals(\"document\") %>" />
      <aui:option value="info" label="info" selected="<%= backgroundImage.equals(\"info\") %>" />
      <aui:option value="devcorner" label="devcorner" selected="<%= backgroundImage.equals(\"devcorner\") %>" />
    </aui:select>

    <aui:button-row>
       <aui:button type="submit" />
    </aui:button-row>
</aui:form>