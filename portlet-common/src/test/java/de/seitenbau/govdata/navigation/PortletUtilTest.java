package de.seitenbau.govdata.navigation;

import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.liferay.portal.kernel.util.Props;
import com.liferay.portal.kernel.util.PropsUtil;

@RunWith(MockitoJUnitRunner.class)
public class PortletUtilTest
{
  private static final String SHACL_VALIDATOR_PROFILE = "dashboard_live";

  private static final String SHACL_VALIDATOR_ENDPOINT = "http://shacl-validator-endpoint.de/";

  private static final String FUSEKI_SHACL_DATASTORE_NAME = "mqa";

  private static final String FUSEKI_DATASTORE_NAME = "ds";

  private static final String CKAN_URL_FRIENDLY = "http://ckan-url-friendly.de";

  private static final String FUSEKI_SPARQL_ENDPOINT = "http://fuseki-sparql-endpoint.de/";

  private static final String FUSEKI_URL = "http://fuseki-url.de";

  private static final String FUSEKI_SHACL_SPARQL_ENDPOINT = "http://fuseki-shacl-sparql-endpoint.de/";

  @Mock
  private Props props;

  @Before
  public void setUp()
  {
    Mockito.when(props.get("fusekiUrl")).thenReturn(FUSEKI_URL);
    Mockito.when(props.get("fusekiSparqlEndpoint")).thenReturn(FUSEKI_SPARQL_ENDPOINT);
    Mockito.when(props.get("fusekiShaclSparqlEndpoint")).thenReturn(FUSEKI_SHACL_SPARQL_ENDPOINT);
    Mockito.when(props.get("cKANurlFriendly")).thenReturn(CKAN_URL_FRIENDLY);
    Mockito.when(props.get("fusekiDatastoreName")).thenReturn(FUSEKI_DATASTORE_NAME);
    Mockito.when(props.get("fusekiShaclDatastoreName")).thenReturn(FUSEKI_SHACL_DATASTORE_NAME);
    Mockito.when(props.get("shaclValidatorEndpoint")).thenReturn(SHACL_VALIDATOR_ENDPOINT);
    Mockito.when(props.get("shaclValidatorProfileType")).thenReturn(SHACL_VALIDATOR_PROFILE);
    PropsUtil.setProps(props);
  }

  @After
  public void tearDown()
  {
    PropsUtil.setProps(null);
  }

  @Test
  public void getLinkToDatasetDetailsRawFormatBaseUrl() throws Exception
  {
    /* prepare */

    /* execute */
    String result = PortletUtil.getLinkToDatasetDetailsRawFormatBaseUrl();

    /* assert */
    Assertions.assertThat(result).isEqualTo(StringUtils.appendIfMissing(CKAN_URL_FRIENDLY, "/") + "dataset/");
  }

  @Test
  public void getLinkToFusekiTriplestoreUrl() throws Exception
  {
    /* prepare */

    /* execute */
    String result = PortletUtil.getLinkToFusekiTriplestoreUrl();

    /* assert */
    Assertions.assertThat(result)
        .isEqualTo(StringUtils.appendIfMissing(FUSEKI_URL, "/") + FUSEKI_DATASTORE_NAME);
  }

  @Test
  public void getLinkToFusekiTriplestoreShaclValidationUrl() throws Exception
  {
    /* prepare */

    /* execute */
    String result = PortletUtil.getLinkToFusekiTriplestoreShaclValidationUrl();

    /* assert */
    Assertions.assertThat(result)
        .isEqualTo(StringUtils.appendIfMissing(FUSEKI_URL, "/") + FUSEKI_SHACL_DATASTORE_NAME);
  }

  @Test
  public void getLinkToFusekiSparqlEndpoint() throws Exception
  {
    /* prepare */

    /* execute */
    String result = PortletUtil.getLinkToFusekiSparqlEndpoint();

    /* assert */
    Assertions.assertThat(result).isEqualTo(StringUtils.removeEnd(FUSEKI_SPARQL_ENDPOINT, "/"));
  }

  @Test
  public void getLinkToFusekiShaclSparqlEndpoint() throws Exception
  {
    /* prepare */

    /* execute */
    String result = PortletUtil.getLinkToFusekiShaclSparqlEndpoint();

    /* assert */
    Assertions.assertThat(result).isEqualTo(StringUtils.removeEnd(FUSEKI_SHACL_SPARQL_ENDPOINT, "/"));
  }

  @Test
  public void getLinkToShaclValidatorEndpoint() throws Exception
  {
    /* prepare */

    /* execute */
    String result = PortletUtil.getLinkToShaclValidatorEndpoint();

    /* assert */
    Assertions.assertThat(result).isEqualTo(StringUtils.removeEnd(SHACL_VALIDATOR_ENDPOINT, "/"));
  }

  @Test
  public void getValidatorProfileType() throws Exception
  {
    /* prepare */

    /* execute */
    String result = PortletUtil.getValidatorProfileType();

    /* assert */
    Assertions.assertThat(result).isEqualTo(SHACL_VALIDATOR_PROFILE);
  }
}
