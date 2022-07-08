/**
 * 
 */
package de.seitenbau.govdata.navigation;

import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;

import org.apache.commons.lang3.StringUtils;

import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsUtil;

import de.seitenbau.govdata.constants.GovDataConfigParam;

/**
 * Enthält Hilfsmethoden, die an mehreren Stellen im Code genutzt werden.
 * 
 * @author rnoerenberg
 *
 */
public abstract class PortletUtil
{
  private static final String CKAN_TYPE_DATASET = "dataset";

  private static final String SLASH = "/";

  /**
   * Extrahiert die Angabe zum FriendlyUrlMapping aus der aktuellen URL und gibt diese zurück.
   * 
   * @param request der RenderRequest.
   * @return der extrahierte String aus der URL.
   */
  public static String extractFriendlyUrlMappingFromRequestUrl(RenderRequest request)
  {
    String url = PortalUtil.getCurrentURL(PortalUtil.getHttpServletRequest(request));
    return StringUtils.substringBefore(StringUtils.substringAfter(url, "/-/"), SLASH);
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

  /**
   * Gibt die Basis-URL für den Link zu den Details im Raw-Format zurück. Die URL endet mit einem
   * Slash, so dass der Metadatenname nur noch angehängt werden muss.
   * 
   * @return
   */
  public static String getLinkToDatasetDetailsRawFormatBaseUrl()
  {
    String ckanFriendlyUrl =
        StringUtils.appendIfMissing(PropsUtil.get(GovDataConfigParam.CKAN_URL_FRIENDLY), SLASH);
    return ckanFriendlyUrl + CKAN_TYPE_DATASET + SLASH;
  }

  /**
   * Gibt den Link für den Fuseki Datastore ds zurück. Die URL endet ohne Slash.
   * 
   * @return
   */
  public static String getLinkToFusekiTriplestoreUrl()
  {
    String fusekiUrl = StringUtils.appendIfMissing(PropsUtil.get(GovDataConfigParam.FUSEKI_URL), SLASH);
    return fusekiUrl + PropsUtil.get(GovDataConfigParam.FUSEKI_DATASTORE_NAME);
  }

  /**
   * Gibt den Link für den Fuseki Datastore mqa zurück. Die URL endet ohne Slash.
   * 
   * @return
   */
  public static String getLinkToFusekiTriplestoreShaclValidationUrl()
  {
    String fusekiUrl = StringUtils.appendIfMissing(PropsUtil.get(GovDataConfigParam.FUSEKI_URL), SLASH);
    return fusekiUrl + PropsUtil.get(GovDataConfigParam.FUSEKI_SHACL_DATASTORE_NAME);
  }

  /**
   * Gibt den Link für den Fuseki Triplestore Datastore zurück. Die URL endet ohne Slash.
   * 
   * @return
   */
  public static String getLinkToFusekiSparqlEndpoint()
  {
    return StringUtils.removeEnd(PropsUtil.get(GovDataConfigParam.FUSEKI_SPARQL_ENDPOINT), SLASH);
  }

  /**
   * Gibt den Link für den Fuseki Triplestore SHACL-Datastore(MQA) zurück. Die URL endet ohne Slash.
   * 
   * @return
   */
  public static String getLinkToFusekiShaclSparqlEndpoint()
  {
    return StringUtils.removeEnd(PropsUtil.get(GovDataConfigParam.FUSEKI_SHACL_SPARQL_ENDPOINT), SLASH);
  }

  /**
   * Gibt den Link für den SHACL Validator zurück. Die URL endet ohne Slash.
   * 
   * @return
   */
  public static String getLinkToShaclValidatorEndpoint()
  {
    return StringUtils.removeEnd(PropsUtil.get(GovDataConfigParam.SHACL_VALIDATOR_ENDPOINT), SLASH);
  }

  /**
   * Gibt das ausgewählte Profil für den SHACL Validator zurück.
   * 
   * @return
   */
  public static String getValidatorProfileType()
  {
    return PropsUtil.get(GovDataConfigParam.SHACL_VALIDATOR_PROFILE_TYPE);
  }
}
