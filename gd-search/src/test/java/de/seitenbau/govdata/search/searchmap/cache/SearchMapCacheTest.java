package de.seitenbau.govdata.search.searchmap.cache;

import static org.mockito.Mockito.times;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

@RunWith(MockitoJUnitRunner.class)
public class SearchMapCacheTest
{

  private static final String APP_ID = "TestAppId";

  private static final String APP_DOMAIN = "TestAppDomain";

  private static final String REQUEST_URL =
      "https://sg.geodatenzentrum.de/gdz_getSession?bkg_appid=" + APP_ID + "&domain=" + APP_DOMAIN;

  @Mock
  private CloseableHttpResponse httpResponse;

  @Mock
  private HttpEntity httpEntity;

  @Mock
  private CloseableHttpClient httpClient;

  @InjectMocks
  private SearchMapCache sut;

  @Before
  public void setup() throws Exception
  {
    ReflectionTestUtils.setField(sut, "appId", APP_ID);
    ReflectionTestUtils.setField(sut, "appDomain", APP_DOMAIN);
    ReflectionTestUtils.setField(sut, "requestUrl", REQUEST_URL);
    Mockito.when(httpClient.execute(Mockito.any(HttpUriRequest.class))).thenReturn(httpResponse);
  }

  @Test
  public void getSessionId() throws Exception
  {
    /* prepare */
    Mockito.when(httpResponse.getEntity()).thenReturn(httpEntity);
    String expectedSessionId = "Test-Session-Id";
    InputStream content = getInputStreamFromString(expectedSessionId);
    Mockito.when(httpEntity.getContent()).thenReturn(content);

    /* execute */
    String result = sut.getSessionId();
    String resultCached = sut.getSessionId();

    /* assert */
    Assertions.assertThat(result).isNotNull();
    Assertions.assertThat(resultCached).isNotNull();
    Assertions.assertThat(result).isEqualTo(expectedSessionId);
    Assertions.assertThat(result).isEqualTo(resultCached);
    Mockito.verify(httpResponse, times(2)).getEntity();
  }

  private InputStream getInputStreamFromString(String text)
  {
    return IOUtils.toInputStream(text, StandardCharsets.UTF_8);
  }
}
