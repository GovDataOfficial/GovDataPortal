<%@include file="/portlet-config/init.jsp" %>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %>

<liferay-portlet:actionURL portletConfiguration="true" var="configurationURL" />

<%
boolean showDatasetButton = GetterUtil.getBoolean(portletPreferences.getValue("showDatasetButton", StringPool.TRUE));
String datasetId1 = GetterUtil.getString(portletPreferences.getValue("datasetId1", ""));
String datasetId2 = GetterUtil.getString(portletPreferences.getValue("datasetId2", ""));
String datasetId3 = GetterUtil.getString(portletPreferences.getValue("datasetId3", ""));
%>

<aui:form action="<%= configurationURL %>" method="post" name="fm">
    <aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= Constants.UPDATE %>" />
    
    <aui:input label="Zeige Link zu den Daten" name="preferences--showDatasetButton--" type="checkbox" value="<%= showDatasetButton %>" />
    <aui:input label="ID des ersten Datensatzes angeben" name="preferences--datasetId1--" type="text" value="<%= datasetId1 %>" />
    <aui:input label="ID des zweiten Datensatzes angeben" name="preferences--datasetId2--" type="text" value="<%= datasetId2 %>" />
    <aui:input label="ID des dritten Datensatzes angeben" name="preferences--datasetId3--" type="text" value="<%= datasetId3 %>" />

    <aui:button-row>
       <aui:button type="submit" />
    </aui:button-row>
</aui:form>