package de.seitenbau.govdata.search.common.cache;

import static org.mockito.Mockito.times;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicHeader;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import de.seitenbau.govdata.search.common.cache.util.TweetContainer;

@RunWith(MockitoJUnitRunner.class)
public class TweetCacheTest
{
  private static final ZoneId ZONE_ID_BERLIN = ZoneId.of("Europe/Berlin");

  private static final String USER_NAME = "GKst GovData";

  private static final String USER_USERNAME = "govdata";

  private static final String POST_TEXT = "this is a test!";

  private static final String POST_ID = "testId";

  private static final String TWITTER_TYPE_POST = "post";

  private static final String TWITTER_TYPE_RETWEET = "retweet";

  private static final String BEARER_TOKEN = "12345-67890";

  private static final String TWITTER_ENDPOINT = "http://twitter.endpoint";

  private static final String TWITTER_URL = "http://twitter.url";

  @Mock
  private CloseableHttpResponse httpResponse;

  @Mock
  private HttpEntity httpEntity;

  @Mock
  private CloseableHttpClient httpClient;

  @InjectMocks
  private TweetCache sut;

  @Before
  public void setup() throws Exception
  {
    ReflectionTestUtils.setField(sut, "twitterUrl", TWITTER_URL);
    ReflectionTestUtils.setField(sut, "tweetEndpoint", TWITTER_ENDPOINT);
    ReflectionTestUtils.setField(sut, "twitterApiToken", BEARER_TOKEN);
    Mockito.when(httpClient.execute(Mockito.any(HttpUriRequest.class))).thenReturn(httpResponse);
  }

  @Test
  public void getTweetData_entityNull() throws Exception
  {
    /* prepare */

    /* execute */
    TweetContainer result = sut.getTweetData();

    /* assert */
    Assertions.assertThat(result).isNull();
    Mockito.verify(httpResponse, times(2)).getEntity();
    Mockito.verifyNoInteractions(httpEntity);
  }

  @Test
  public void getTweetData_responseNotParsable() throws Exception
  {
    /* prepare */
    Mockito.when(httpResponse.getEntity()).thenReturn(httpEntity);
    String contentString = "invalidJson";
    InputStream content = IOUtils.toInputStream(contentString, StandardCharsets.UTF_8);
    Header contentTypeHeader =
        new BasicHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.toString());
    Mockito.when(httpEntity.getContentType()).thenReturn(contentTypeHeader);
    Mockito.when(httpEntity.getContent()).thenReturn(content);
    Mockito.when(httpEntity.getContentLength())
        .thenReturn((long) IOUtils.length(contentString.getBytes(StandardCharsets.UTF_8)));

    /* execute */
    TweetContainer result = sut.getTweetData();

    /* assert */
    Assertions.assertThat(result).isNull();
    Mockito.verify(httpResponse, times(2)).getEntity();
  }

  @Test
  public void getTweetData_cannotReadLatestPostFromJson() throws Exception
  {
    /* prepare */
    Mockito.when(httpResponse.getEntity()).thenReturn(httpEntity);
    JsonObject json = new JsonObject();
    json.add("id", new JsonPrimitive(POST_ID));
    String contentString = new Gson().toJson(json);
    InputStream content = IOUtils.toInputStream(contentString, StandardCharsets.UTF_8);
    Header contentTypeHeader =
        new BasicHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.toString());
    Mockito.when(httpEntity.getContentType()).thenReturn(contentTypeHeader);
    Mockito.when(httpEntity.getContent()).thenReturn(content);
    Mockito.when(httpEntity.getContentLength())
        .thenReturn((long) IOUtils.length(contentString.getBytes(StandardCharsets.UTF_8)));

    /* execute */
    TweetContainer result = sut.getTweetData();

    /* assert */
    Assertions.assertThat(result).isNull();
    Mockito.verify(httpResponse, times(2)).getEntity();
  }

  @Test
  public void getTweetData() throws Exception
  {
    /* prepare */
    Mockito.when(httpResponse.getEntity()).thenReturn(httpEntity);
    String createDateString = "2022-03-29T11:43:13.671Z";
    LocalDateTime createDate =
        LocalDateTime.parse(createDateString, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
    JsonObject json = createDefaultJsonResponse(createDate);
    String contentString = new Gson().toJson(json);
    InputStream content = IOUtils.toInputStream(contentString, StandardCharsets.UTF_8);
    Header contentTypeHeader =
        new BasicHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.toString());
    Mockito.when(httpEntity.getContentType()).thenReturn(contentTypeHeader);
    Mockito.when(httpEntity.getContent()).thenReturn(content);
    Mockito.when(httpEntity.getContentLength())
        .thenReturn((long) IOUtils.length(contentString.getBytes(StandardCharsets.UTF_8)));

    /* execute */
    TweetContainer result = sut.getTweetData();

    /* assert */
    Assertions.assertThat(result).isNotNull();
    assertTwitterPost(result, createDate, TWITTER_TYPE_POST);
    Mockito.verify(httpResponse, times(2)).getEntity();
  }

  @Test
  public void getTweetData_referenced_tweets() throws Exception
  {
    /* prepare */
    Mockito.when(httpResponse.getEntity()).thenReturn(httpEntity);
    String createDateString = "2022-03-29T11:43:13.671Z";
    LocalDateTime createDate =
        LocalDateTime.parse(createDateString, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
    JsonObject json = createDefaultJsonResponse(createDate);
    JsonArray dataArray = json.getAsJsonArray("data");
    JsonObject latestPostObject = dataArray.get(0).getAsJsonObject();
    JsonArray refTweetArray = new JsonArray();
    JsonObject objectRefTweets = new JsonObject();
    objectRefTweets.add("type", new JsonPrimitive(TWITTER_TYPE_RETWEET));
    refTweetArray.add(objectRefTweets);
    latestPostObject.add("referenced_tweets", refTweetArray);
    dataArray.add(latestPostObject);
    json.add("data", dataArray);
    String contentString = new Gson().toJson(json);
    InputStream content = IOUtils.toInputStream(contentString, StandardCharsets.UTF_8);
    Header contentTypeHeader =
        new BasicHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.toString());
    Mockito.when(httpEntity.getContentType()).thenReturn(contentTypeHeader);
    Mockito.when(httpEntity.getContent()).thenReturn(content);
    Mockito.when(httpEntity.getContentLength())
        .thenReturn((long) IOUtils.length(contentString.getBytes(StandardCharsets.UTF_8)));

    /* execute */
    TweetContainer result = sut.getTweetData();

    /* assert */
    Assertions.assertThat(result).isNotNull();
    assertTwitterPost(result, createDate, TWITTER_TYPE_RETWEET);
    Mockito.verify(httpResponse, times(2)).getEntity();
  }

  @Test
  public void getTweetData_createDate_notParsable() throws Exception
  {
    /* prepare */
    Mockito.when(httpResponse.getEntity()).thenReturn(httpEntity);
    JsonObject json = createDefaultJsonResponse(null);
    String contentString = new Gson().toJson(json);
    InputStream content = IOUtils.toInputStream(contentString, StandardCharsets.UTF_8);
    Header contentTypeHeader =
        new BasicHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.toString());
    Mockito.when(httpEntity.getContentType()).thenReturn(contentTypeHeader);
    Mockito.when(httpEntity.getContent()).thenReturn(content);
    Mockito.when(httpEntity.getContentLength())
        .thenReturn((long) IOUtils.length(contentString.getBytes(StandardCharsets.UTF_8)));

    /* execute */
    TweetContainer result = sut.getTweetData();

    /* assert */
    Assertions.assertThat(result).isNotNull();
    assertTwitterPost(result, null, TWITTER_TYPE_POST);
    Mockito.verify(httpResponse, times(2)).getEntity();
  }

  private JsonObject createDefaultJsonResponse(LocalDateTime createDate)
  {
    JsonObject json = new JsonObject();
    JsonArray dataArray = new JsonArray();
    JsonObject objectData = new JsonObject();
    objectData.add("id", new JsonPrimitive(POST_ID));
    objectData.add("text", new JsonPrimitive(POST_TEXT));
    String createDateValue = "";
    if (createDate != null)
    {
      // convert to UTC
      createDateValue =
          DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneId.of("UTC"))
          .format(createDate.atZone(ZONE_ID_BERLIN).toInstant());
    }
    objectData.add("created_at", new JsonPrimitive(createDateValue));
    dataArray.add(objectData);
    json.add("data", dataArray);
    JsonObject includesObject = new JsonObject();
    JsonArray usersArray = new JsonArray();
    JsonObject userObject = new JsonObject();
    userObject.add("username", new JsonPrimitive(USER_USERNAME));
    userObject.add("name", new JsonPrimitive(USER_NAME));
    usersArray.add(userObject);
    includesObject.add("users", usersArray);
    json.add("includes", includesObject);
    return json;
  }

  private void assertTwitterPost(TweetContainer result, LocalDateTime createDate, String postType)
  {
    Assertions.assertThat(result.getId()).isEqualTo(POST_ID);
    Assertions.assertThat(result.getName()).isEqualTo(USER_NAME);
    Assertions.assertThat(result.getText()).isEqualTo(POST_TEXT);
    Assertions.assertThat(result.getTimestamp()).isEqualTo(createDate);
    Assertions.assertThat(result.getType()).isEqualTo(postType);
    Assertions.assertThat(result.getUrl()).isEqualTo(TWITTER_URL);
    Assertions.assertThat(result.getUsername()).isEqualTo(USER_USERNAME);
  }
}
