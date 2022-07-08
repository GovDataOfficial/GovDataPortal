package de.seitenbau.govdata.search.common.searchresult;

import static de.seitenbau.govdata.navigation.GovDataNavigation.FRIENDLY_URL_NAME_SEARCHRESULT_PAGE;
import static de.seitenbau.govdata.navigation.GovDataNavigation.PORTLET_NAME_SEARCHRESULT;

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
import de.seitenbau.govdata.navigation.GovDataNavigation;
import de.seitenbau.govdata.navigation.PortletUtil;
import de.seitenbau.govdata.odp.common.filter.FilterPathUtils;

/**
 * Baut verschiedene URLs zusammen.
 * 
 * @author rnoerenberg
 *
 */
public class UrlBuilder
{
  private Map<String, String> parameters;

  private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");

  /**
   * Konstruktor mit {@link PreparedParameters}.
   * 
   * @param preparm die Parameter aus denen die URL gebildet wird.
   */
  public UrlBuilder(PreparedParameters preparm)
  {
    // initialize map with all values filled in
    parameters = new HashMap<>();
    parameters.put(QueryParamNames.PARAM_PHRASE, preparm.getQuery().getQueryString());

    if (preparm.getSelectedSorting() == null)
    {
      parameters.put(QueryParamNames.PARAM_SORT, "");
    }
    else
    {
      parameters.put(QueryParamNames.PARAM_SORT, preparm.getSelectedSorting().toString());
    }

    if (preparm.getBoundingBox() == null)
    {
      parameters.put(QueryParamNames.PARAM_BOUNDINGBOX, "");
    }
    else
    {
      parameters.put(QueryParamNames.PARAM_BOUNDINGBOX, preparm.getBoundingBox().toString());
    }

    parameters.put(QueryParamNames.PARAM_FILTER,
        FilterPathUtils.serializeFilterMap(preparm.getActiveFilters()));

    if (preparm.getDateFrom() == null)
    {
      parameters.put(QueryParamNames.PARAM_START, "");
    }
    else
    {
      parameters.put(QueryParamNames.PARAM_START, simpleDateFormat.format(preparm.getDateFrom()));
    }

    if (preparm.getDateUntil() == null)
    {
      parameters.put(QueryParamNames.PARAM_END, "");
    }
    else
    {
      parameters.put(QueryParamNames.PARAM_END, simpleDateFormat.format(preparm.getDateUntil()));
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
      if (ArrayUtils.contains(clearedParameters, entry.getKey()))
      {
        PortletUtil.setParameterInPortletUrl(renderUrl, entry.getKey(), "");
      }
      else
      {
        PortletUtil.setParameterInPortletUrl(renderUrl, entry.getKey(), entry.getValue());
      }
    }
    return renderUrl;
  }

  public PortletURL createRedirectToResultsUrl(GovDataNavigation navigationHelper, String target)
      throws SystemException, PortalException
  {
    return createFulloptionUrl(navigationHelper, target, PORTLET_NAME_SEARCHRESULT);
  }

  public PortletURL createAtomFeedUrl(GovDataNavigation navigationHelper) throws SystemException,
      PortalException
  {
    return createFulloptionUrl(navigationHelper, FRIENDLY_URL_NAME_SEARCHRESULT_PAGE, "gdsearchatomfeed");
  }

  public PortletURL createFulloptionUrl(GovDataNavigation navigationHelper, String target, String portletid)
      throws SystemException, PortalException
  {

    return navigationHelper.createLinkForSearchResults(target, portletid,
        getParam(QueryParamNames.PARAM_PHRASE),
        getParam(QueryParamNames.PARAM_FILTER),
        getParam(QueryParamNames.PARAM_SORT),
        getParam(QueryParamNames.PARAM_BOUNDINGBOX),
        getParam(QueryParamNames.PARAM_START),
        getParam(QueryParamNames.PARAM_END));
  }

  /**
   * Convenience Method that will catch all Exceptions and return null if something goes wrong.
   */
  public String createFulloptionUrlOrNull(GovDataNavigation navigationHelper, String target, String portletid)
  {
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
  public List<KeyValuePair> parametersAsList(String[] excludedParameters)
  {
    List<KeyValuePair> result = new ArrayList<>();

    for (Entry<String, String> entry : parameters.entrySet())
    {
      if (!ArrayUtils.contains(excludedParameters, entry.getKey()))
      {
        result.add(new KeyValuePair(entry.getKey(), entry.getValue()));
      }
    }

    return result;
  }

  public String getParam(String key)
  {
    return parameters.get(key);
  }

}
