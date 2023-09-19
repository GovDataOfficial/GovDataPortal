package de.seitenbau.govdata.search.common.cache;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicHeader;
import org.assertj.core.api.Assertions;
import org.mockito.Mock;
import org.mockito.Mockito;

import de.seitenbau.govdata.search.common.cache.util.PostContainer;

public class SocialMediaCacheBase
{
  protected static final ZoneId ZONE_ID_BERLIN = ZoneId.of("Europe/Berlin");

  protected static final String USER_NAME = "GKst GovData";

  protected static final String USER_USERNAME = "govdata";

  protected static final String POST_TEXT = "this is a test!";

  protected static final String POST_ID = "testId";

  @Mock
  protected CloseableHttpResponse httpResponse;

  @Mock
  protected HttpEntity httpEntity;

  @Mock
  protected CloseableHttpClient httpClient;

  protected void assertSocialMediaPost(PostContainer result, LocalDateTime createDate, String postType,
      String socialMediaUrl)
  {
    Assertions.assertThat(result).isNotNull();
    Assertions.assertThat(result.getId()).isEqualTo(POST_ID);
    Assertions.assertThat(result.getName()).isEqualTo(USER_NAME);
    Assertions.assertThat(result.getText()).isEqualTo(POST_TEXT);
    Assertions.assertThat(result.getTimestamp()).isEqualTo(createDate);
    Assertions.assertThat(result.getType()).isEqualTo(postType);
    Assertions.assertThat(result.getUrl()).isEqualTo(socialMediaUrl);
    Assertions.assertThat(result.getUsername()).isEqualTo(USER_USERNAME);
  }

  protected void mockHttpEntityMethodsreturnValues(String contentString) throws Exception
  {
    InputStream content = IOUtils.toInputStream(contentString, StandardCharsets.UTF_8);
    Header contentTypeHeader =
        new BasicHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.toString());
    Mockito.when(httpEntity.getContentType()).thenReturn(contentTypeHeader);
    Mockito.when(httpEntity.getContent()).thenReturn(content);
    Mockito.when(httpEntity.getContentLength())
        .thenReturn((long) IOUtils.length(contentString.getBytes(StandardCharsets.UTF_8)));
  }
}
