package de.seitenbau.govdata.search.common.searchresult;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.portlet.MimeResponse;
import javax.portlet.PortletURL;

import org.apache.commons.lang3.ArrayUtils;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.KeyValuePair;

import de.seitenbau.govdata.constants.QueryParamNames;
import de.seitenbau.govdata.filter.FilterPathUtils;
import de.seitenbau.govdata.navigation.GovDataNavigation;
import de.seitenbau.govdata.navigation.PortletUtil;

public class UrlBuilder
{
  Map<String, String> parameters;
  SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
  
  public UrlBuilder(PreparedParameters preparm)
  {
    // initialize map with all values filled in
    parameters = new HashMap<>();
    parameters.put(QueryParamNames.PARAM_PHRASE, preparm.getQuery());
    
    if(preparm.getSelectedSorting() == null) {
      parameters.put(QueryParamNames.PARAM_SORT, "");
    } else {
      parameters.put(QueryParamNames.PARAM_SORT, preparm.getSelectedSorting().toString());
    }
    
    if(preparm.getBoundingBox() == null) {
      parameters.put(QueryParamNames.PARAM_BOUNDINGBOX, "");
    } else {
      parameters.put(QueryParamNames.PARAM_BOUNDINGBOX, preparm.getBoundingBox().toString());
    }
    
    parameters.put(QueryParamNames.PARAM_FILTER, FilterPathUtils.serializeFilterMap(preparm.getActiveFilters()));
    
    if(preparm.getDateFrom() == null) {
      parameters.put(QueryParamNames.PARAM_FROM, "");
    } else {
      parameters.put(QueryParamNames.PARAM_FROM, simpleDateFormat.format(preparm.getDateFrom()));
    }
    
    if(preparm.getDateUntil() == null) {
      parameters.put(QueryParamNames.PARAM_UNTIL, "");
    } else {
      parameters.put(QueryParamNames.PARAM_UNTIL, simpleDateFormat.format(preparm.getDateUntil()));
    }
  }
  /**
   * Generates a PortletURL containing the parameters.
   * @param response required for building the URL
   * @param clearedParameters array of parameter-keys. Values will be set to empty string
   * @return
   */
  public PortletURL createUrl(MimeResponse response, String[] clearedParameters)
  {
    PortletURL renderUrl = response.createRenderURL();
    for (Entry<String, String> entry : parameters.entrySet())
    {
      if(ArrayUtils.contains(clearedParameters, entry.getKey())) {
        PortletUtil.setParameterInPortletUrl(renderUrl, entry.getKey(), "");
      } else {
        PortletUtil.setParameterInPortletUrl(renderUrl, entry.getKey(), entry.getValue());
      }
    }
    return renderUrl;
  }
  
  public PortletURL createRedirectToResultsUrl(GovDataNavigation navigationHelper, String target) throws SystemException, PortalException {
    return createFulloptionUrl(navigationHelper, target, "gdsearchresult");
  }
  
  public PortletURL createAtomFeedUrl(GovDataNavigation navigationHelper) throws SystemException, PortalException {
    return createFulloptionUrl(navigationHelper, "suchen", "gdsearchatomfeed");
  }
  
  public PortletURL createFulloptionUrl(GovDataNavigation navigationHelper, String target, String portletid) throws SystemException, PortalException {
    return navigationHelper.createLinkForSearchResults(target, portletid,
        getParam(QueryParamNames.PARAM_PHRASE),
        getParam(QueryParamNames.PARAM_FILTER),
        getParam(QueryParamNames.PARAM_SORT),
        getParam(QueryParamNames.PARAM_BOUNDINGBOX),
        getParam(QueryParamNames.PARAM_FROM),
        getParam(QueryParamNames.PARAM_UNTIL));
  }
  
  /**
   * Convenience Method that will catch all Exceptions and return null if something goes wrong.
   */
  public String createFulloptionUrlOrNull(GovDataNavigation navigationHelper, String target, String portletid) {
    try
    {
      return createFulloptionUrl(navigationHelper, target, portletid).toString();
    }
    catch (SystemException | PortalException e)
    {
      return null;
    }
  }
  
  /**
   * Generates a List of Key/Value Pairs containing the parameters.
   * @param excludedParameters array of parameter keys. Those parameters will not be included.
   * @return
   */
  public List<KeyValuePair> parametersAsList(String[] excludedParameters) {
    List<KeyValuePair> result = new ArrayList<>();
    
    for(Entry<String, String> entry : parameters.entrySet()) {
      if(!ArrayUtils.contains(excludedParameters, entry.getKey())) {
        result.add(new KeyValuePair(entry.getKey(), entry.getValue()));
      }
    }
    
    return result;
  }
  
  
  
  public String getParam(String key) {
    return parameters.get(key);
  }
}
