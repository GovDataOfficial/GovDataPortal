package de.seitenbau.govdata.search.common.cache;

import static org.mockito.Mockito.times;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.apache.http.client.methods.HttpUriRequest;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import de.seitenbau.govdata.search.common.cache.util.PostContainer;

@RunWith(MockitoJUnitRunner.class)
public class MastodonCacheTest extends SocialMediaCacheBase
{
  private static final String MASTODON_TYPE_POST = "post";

  private static final String MASTODON_TYPE_BOOSTED = "boosted";

  private static final String MASTODON_ENDPOINT = "http://mastodon.endpoint";

  private static final String MASTODON_URL = "http://mastodon.url";

  @InjectMocks
  private MastodonCache sut;

  @Before
  public void setup() throws Exception
  {
    ReflectionTestUtils.setField(sut, "mastodonUrl", MASTODON_URL);
    ReflectionTestUtils.setField(sut, "postEndpoint", MASTODON_ENDPOINT);
    Mockito.when(httpClient.execute(Mockito.any(HttpUriRequest.class))).thenReturn(httpResponse);
  }

  @Test
  public void getPostData_entityNull() throws Exception
  {
    /* prepare */

    /* execute */
    PostContainer result = sut.getPostData();

    /* assert */
    Assertions.assertThat(result).isNull();
    Mockito.verify(httpResponse, times(2)).getEntity();
    Mockito.verifyNoInteractions(httpEntity);
  }

  @Test
  public void getPostData_responseNotParsable() throws Exception
  {
    /* prepare */
    Mockito.when(httpResponse.getEntity()).thenReturn(httpEntity);
    String contentString = "[invalidJson]";
    mockHttpEntityMethodsreturnValues(contentString);

    /* execute */
    PostContainer result = sut.getPostData();

    /* assert */
    Assertions.assertThat(result).isNull();
    Mockito.verify(httpResponse, times(2)).getEntity();
  }

  @Test
  public void getPostData_cannotReadLatestPostFromJson() throws Exception
  {
    /* prepare */
    Mockito.when(httpResponse.getEntity()).thenReturn(httpEntity);
    JsonObject json = new JsonObject();
    json.add("id", new JsonPrimitive(POST_ID));
    String contentString = new Gson().toJson(json);
    mockHttpEntityMethodsreturnValues(contentString);

    /* execute */
    PostContainer result = sut.getPostData();

    /* assert */
    Assertions.assertThat(result).isNull();
    Mockito.verify(httpResponse, times(2)).getEntity();
  }

  @Test
  public void getPostData() throws Exception
  {
    /* prepare */
    Mockito.when(httpResponse.getEntity()).thenReturn(httpEntity);
    String createDateString = "2022-03-29T11:43:13.671Z";
    LocalDateTime createDate =
        LocalDateTime.parse(createDateString, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
    JsonArray dataArray = new JsonArray();
    JsonObject objectData = createDefaultJsonObject(createDate);
    objectData.add("content", new JsonPrimitive(POST_TEXT));
    objectData.add("reblog", null);
    dataArray.add(objectData);
    String contentString = new Gson().toJson(dataArray);
    mockHttpEntityMethodsreturnValues(contentString);

    /* execute */
    PostContainer result = sut.getPostData();

    /* assert */
    assertSocialMediaPost(result, createDate, MASTODON_TYPE_POST, MASTODON_URL);
    Mockito.verify(httpResponse, times(2)).getEntity();
  }

  @Test
  public void getPostData_referenced_post() throws Exception
  {
    /* prepare */
    Mockito.when(httpResponse.getEntity()).thenReturn(httpEntity);
    String createDateString = "2022-03-29T11:43:13.671Z";
    LocalDateTime createDate =
        LocalDateTime.parse(createDateString, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
    JsonArray dataArray = new JsonArray();
    JsonObject objectData = createDefaultJsonObject(createDate);
    objectData.add("content", null);
    JsonObject reblogData = new JsonObject();
    reblogData.add("content", new JsonPrimitive(POST_TEXT));
    objectData.add("reblog", reblogData);
    dataArray.add(objectData);
    String contentString = new Gson().toJson(dataArray);
    mockHttpEntityMethodsreturnValues(contentString);

    /* execute */
    PostContainer result = sut.getPostData();

    /* assert */
    assertSocialMediaPost(result, createDate, MASTODON_TYPE_BOOSTED, MASTODON_URL);
    Mockito.verify(httpResponse, times(2)).getEntity();
  }

  @Test
  public void getPostData_createDate_notParsable() throws Exception
  {
    /* prepare */
    Mockito.when(httpResponse.getEntity()).thenReturn(httpEntity);
    JsonArray dataArray = new JsonArray();
    JsonObject objectData = createDefaultJsonObject(null);
    objectData.add("content", new JsonPrimitive(POST_TEXT));
    objectData.add("reblog", null);
    dataArray.add(objectData);
    String contentString = new Gson().toJson(dataArray);
    mockHttpEntityMethodsreturnValues(contentString);

    /* execute */
    PostContainer result = sut.getPostData();

    /* assert */
    assertSocialMediaPost(result, null, MASTODON_TYPE_POST, MASTODON_URL);
    Mockito.verify(httpResponse, times(2)).getEntity();
  }

  private JsonObject createDefaultJsonObject(LocalDateTime createDate)
  {
    JsonObject objectData = new JsonObject();
    objectData.add("id", new JsonPrimitive(POST_ID));
    String createDateValue = "";
    if (createDate != null)
    {
      // convert to UTC
      createDateValue =
          DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneId.of("UTC"))
              .format(createDate.atZone(ZONE_ID_BERLIN).toInstant());
    }
    objectData.add("created_at", new JsonPrimitive(createDateValue));
    JsonObject accountObject = new JsonObject();
    accountObject.add("username", new JsonPrimitive(USER_USERNAME));
    accountObject.add("display_name", new JsonPrimitive(USER_NAME));
    objectData.add("account", accountObject);
    return objectData;
  }

}
