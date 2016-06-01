/**
 * 
 */
package de.seitenbau.govdata.navigation;

import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;

import org.elasticsearch.common.lang3.StringUtils;

import com.liferay.portal.util.PortalUtil;

/**
 * Enthält Hilfsmethoden, die an mehreren Stellen im Code genutzt werden.
 * 
 * @author rnoerenberg
 *
 */
public abstract class PortletUtil
{
  /**
   * Extrahiert die Angabe zum FriendlyUrlMapping aus der aktuellen URL und gibt diese zurück.
   * 
   * @param request der RenderRequest.
   * @return der extrahierte String aus der URL.
   */
  public static String extractFriendlyUrlMappingFromRequestUrl(RenderRequest request)
  {
    String url = PortalUtil.getCurrentURL(PortalUtil.getHttpServletRequest(request));
    return StringUtils.substringBefore(StringUtils.substringAfter(url, "/-/"), "/");
  }

  /**
   * Fügt einen Parameter der übergebenen PortletURL hinzu.
   * 
   * @param url die PortletURL.
   * @param paramName der Name des Parameters.
   * @param value der Wert des Parameters.
   */
  public static void setParameterInPortletUrl(PortletURL url, String paramName, String value)
  {
    if (value != null && StringUtils.isNotEmpty(paramName))
    {
      url.setParameter(paramName, value);
    }
  }
}
