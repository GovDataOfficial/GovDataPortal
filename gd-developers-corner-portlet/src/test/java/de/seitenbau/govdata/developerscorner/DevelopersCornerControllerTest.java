package de.seitenbau.govdata.developerscorner;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.MessageSource;
import org.springframework.mock.web.portlet.MockRenderRequest;
import org.springframework.mock.web.portlet.MockRenderResponse;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import com.liferay.portal.kernel.util.Props;
import com.liferay.portal.kernel.util.PropsUtil;

import de.seitenbau.govdata.developerscorner.util.SparqlEndpoint;

@RunWith(MockitoJUnitRunner.class)
public class DevelopersCornerControllerTest
{
  private static final String SPARQL_SHACL_ENDPOINT_NAME = "Validierungsdaten";

  private static final String SPARQL_ENDPOINT_NAME = "Metadaten";

  private static final String FUSEKI_SPARQL_ENDPOINT = "http://fuseki-sparql-endpoint.de/";

  private static final String FUSEKI_SHACL_SPARQL_ENDPOINT = "http://fuseki-shacl-sparql-endpoint.de/";

  private static final String SPARQL_DATASET_TYPE_METADATA = "ds";

  private static final String SPARQL_DATASET_TYPE_MQA = "mqa";

  @Mock
  private Props props;

  @Mock
  private MessageSource messageSource;

  @InjectMocks
  private DevelopersCornerController sut;

  @Before
  public void setUp()
  {
    // liferay properties
    PropsUtil.setProps(props);
    Mockito.when(props.get("fusekiSparqlEndpoint")).thenReturn(FUSEKI_SPARQL_ENDPOINT);
    // message source
    Mockito.when(messageSource.getMessage("od.sparql.query.endpoint.name." + SPARQL_DATASET_TYPE_METADATA,
        ArrayUtils.EMPTY_OBJECT_ARRAY, null)).thenReturn(SPARQL_ENDPOINT_NAME);
    Mockito.when(messageSource.getMessage("od.sparql.query.endpoint.name." + SPARQL_DATASET_TYPE_MQA,
        ArrayUtils.EMPTY_OBJECT_ARRAY, null)).thenReturn(SPARQL_SHACL_ENDPOINT_NAME);
  }

  @After
  public void tearDown()
  {
    PropsUtil.setProps(null);
  }

  @Test
  public void showDashboard() throws Exception
  {
    /* prepare */
    MockRenderResponse response = new MockRenderResponse();
    MockRenderRequest request = new MockRenderRequest();
    Model model = new ExtendedModelMap();

    Mockito.when(props.get("fusekiShaclSparqlEndpoint")).thenReturn(FUSEKI_SHACL_SPARQL_ENDPOINT);

    sut.init();

    /* execute */
    String result = sut.showDashboard(request, response, model);

    /* assert */
    Assertions.assertThat(result).isNotEmpty();
    Map<String, Object> modelMap = model.asMap();
    Assertions.assertThat(modelMap).hasSize(2);
    Assertions.assertThat(modelMap).containsKeys("fusekiEndpoints", "themeDisplay");
    List<?> sparqlEndpoints = (List<?>) modelMap.get("fusekiEndpoints");
    Assertions.assertThat(sparqlEndpoints).hasSize(2);
    assertSparqlEndpoint(sparqlEndpoints, SPARQL_DATASET_TYPE_METADATA, SPARQL_ENDPOINT_NAME,
        FUSEKI_SPARQL_ENDPOINT);
    assertSparqlEndpoint(sparqlEndpoints, SPARQL_DATASET_TYPE_MQA, SPARQL_SHACL_ENDPOINT_NAME,
        FUSEKI_SHACL_SPARQL_ENDPOINT);
  }

  @Test
  public void showDashboard_without_mqa() throws Exception
  {
    /* prepare */
    MockRenderResponse response = new MockRenderResponse();
    MockRenderRequest request = new MockRenderRequest();
    Model model = new ExtendedModelMap();

    sut.init();

    /* execute */
    String result = sut.showDashboard(request, response, model);

    /* assert */
    Assertions.assertThat(result).isNotEmpty();
    Map<String, Object> modelMap = model.asMap();
    Assertions.assertThat(modelMap).hasSize(2);
    Assertions.assertThat(modelMap).containsKeys("fusekiEndpoints", "themeDisplay");
    List<?> sparqlEndpoints = (List<?>) modelMap.get("fusekiEndpoints");
    Assertions.assertThat(sparqlEndpoints).hasSize(1);
    assertSparqlEndpoint(sparqlEndpoints, SPARQL_DATASET_TYPE_METADATA, SPARQL_ENDPOINT_NAME,
        FUSEKI_SPARQL_ENDPOINT);
  }

  private void assertSparqlEndpoint(List<?> sparqlEndpoints, String type, String sparqlEndpointName,
      String fusekiSparqlEndpointUrl)
  {
    boolean found = false;
    for (Object element : sparqlEndpoints)
    {
      SparqlEndpoint endpoint = (SparqlEndpoint) element;
      if (endpoint.getType().equals(type))
      {
        found = true;
        Assertions.assertThat(endpoint.getName()).isEqualTo(sparqlEndpointName);
        Assertions.assertThat(endpoint.getUrl())
            .isEqualTo(StringUtils.removeEnd(fusekiSparqlEndpointUrl, "/"));
        break;
      }
    }
    Assertions.assertThat(found).isTrue();
  }
}
