package de.seitenbau.govdata.search.common.cache;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import de.seitenbau.govdata.clean.StringCleaner;
import de.seitenbau.govdata.odp.common.cache.BaseCache;
import de.seitenbau.govdata.search.common.cache.util.PostContainer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class MastodonCache extends BaseCache
{
  private PostContainer post;

  @Value("${govdata.mastodon.url}")
  private String mastodonUrl;

  @Value("${govdata.mastodon.api.endpoint.posts}")
  private String postEndpoint;

  private CloseableHttpClient httpClient;

  private static final String POST_DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
  /**
   * Initializes required components and sets configuration parameters.
   */
  @PostConstruct
  public void init()
  {
    setMaxCacheTimeAmount(1);
    httpClient = HttpClients.custom().useSystemProperties()
        .setDefaultRequestConfig(RequestConfig.custom()
            .setCookieSpec(CookieSpecs.STANDARD).build())
        .build();
  }

  /**
   * Closes all open resources.
   */
  @PreDestroy
  public void destory()
  {
    HttpClientUtils.closeQuietly(httpClient);
  }

  /**
   * Returns the data in the cache. Otherwise the data will be loaded first.
   * @return
   */
  public PostContainer getPostData()
  {
    final String method = "getPostData() : ";
    log.trace("{}Start", method);

    if (post == null || isCacheExpired())
    {
      log.info("{}Empty or expired tweet-data cache. Getting data from Mastodon API...", method);
      post = getNewestPost();
      cacheUpdated();
    }

    log.trace("{}End", method);
    return post;
  }

  private PostContainer getNewestPost()
  {
    final String method = "getNewestPost() : ";
    log.trace(method + "Start");

    PostContainer result;
    String response = sendRequest();
    if (response == null)
    {
      log.warn("{}Did not receive data from Mastodon!", method);
      log.trace(method + "End");
      return null;
    }

    JSONArray json;
    try
    {
      json = (JSONArray) new JSONParser().parse(response);
      JSONObject latestPost = readLatestPostFromJson(json);
      if (Objects.isNull(latestPost))
      {
        log.warn("{}Could not read latest mastodon post from JSON response!", method);
        log.trace(method + "End");
        return null;
      }

      String postType = "post";
      String content = readContentFromJson(latestPost);
      if (latestPost.containsKey(MastodonApiKeys.REBLOG.getKey()))
      {
        JSONObject referencedPost =
            (JSONObject) latestPost.get(MastodonApiKeys.REBLOG.getKey());
        // check if the post is a geboosted
        if (Objects.nonNull(referencedPost))
        {
          postType = "boosted";
          content = readContentFromJson(referencedPost);
        }
      }

      LocalDateTime postDate = null;
      if (latestPost.containsKey(MastodonApiKeys.POST_CREATED_AT.getKey()))
      {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(POST_DATE_PATTERN);
        try
        {
          // parse date
          LocalDateTime parsedDate =
              LocalDateTime.parse(latestPost.get(MastodonApiKeys.POST_CREATED_AT.getKey()).toString(), dtf);
          log.debug("ParsedDate: {}", parsedDate);
          postDate = parsedDate.atZone(ZoneId.of("UTC")).withZoneSameInstant(ZoneId.of("Europe/Berlin"))
              .toLocalDateTime();
          log.debug("PostDate: {}", postDate);
        }
        catch (DateTimeParseException ex)
        {
          log.warn("{} Could not parse Post date: {}", method, ex.getMessage());
        }
      }

      JSONObject jsonAccount = (JSONObject) latestPost.get(MastodonApiKeys.ACCOUNT.getKey());
      result = PostContainer.builder()
          .text(content)
          .id(latestPost.get(MastodonApiKeys.POST_ID.getKey()).toString())
          .url(mastodonUrl)
          .username(jsonAccount.get(MastodonApiKeys.USER_USERNAME.getKey()).toString())
          .name(jsonAccount.get(MastodonApiKeys.DISPLAY_NAME.getKey()).toString())
          .type(postType)
          .timestamp(postDate).build();

    }
    catch (ParseException e)
    {
      log.warn("{}Could not parse data from Mastodon: {}", method, e.getMessage());
      log.trace(method + "End");
      return null;
    }
    catch (ClassCastException ex)
    {
      log.warn("{}Could not cast response: {}", method, ex.getMessage());
      log.trace(method + "End");
      return null;
    }

    log.trace(method + "End");

    return result;
  }

  private JSONObject readLatestPostFromJson(JSONArray json)
  {
    JSONObject latestPost = null;
    if (Objects.nonNull(json))
    {
      latestPost = (JSONObject) json.get(0);
    }
    return latestPost;
  }

  private String readContentFromJson(JSONObject json)
  {
    String content = null;
    if (json.containsKey(MastodonApiKeys.POST_CONTENT.getKey())
        && Objects.nonNull(json.get(MastodonApiKeys.POST_CONTENT.getKey())))
    {
      content = json.get(MastodonApiKeys.POST_CONTENT.getKey()).toString();
    }
    return StringCleaner.trimAndFilterString(content);
  }

  private String sendRequest()
  {
    String searchResponse = null;
    HttpResponse response = null;
    HttpEntity entity = null;

    try
    {
      URIBuilder uriBuilder = new URIBuilder(postEndpoint);
      List<NameValuePair> queryParameters = new ArrayList<>();
      queryParameters.add(new BasicNameValuePair("limit", "5"));
      queryParameters.add(new BasicNameValuePair("exclude_replies", "true"));

      uriBuilder.addParameters(queryParameters);

      HttpGet httpGet = new HttpGet(uriBuilder.build());
      httpGet.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.toString());
      response = httpClient.execute(httpGet);
      entity = response.getEntity();
      if (null != entity)
      {
        searchResponse = EntityUtils.toString(entity, StandardCharsets.UTF_8);
      }
    }
    catch (URISyntaxException | IOException e)
    {
      log.warn("Problem while requesting data from Mastodon API: {}", e.getMessage());
    }
    finally
    {
      EntityUtils.consumeQuietly(entity);
      HttpClientUtils.closeQuietly(response);
    }

    return searchResponse;
  }

  private enum MastodonApiKeys
  {
    POST_CREATED_AT("created_at"),
    POST_CONTENT("content"),
    POST_ID("id"),
    POST_URL("url"),
    USER_USERNAME("username"),
    DISPLAY_NAME("display_name"),
    REBLOG("reblog"),
    ACCOUNT("account");

    private String key;

    private MastodonApiKeys(String key)
    {
      this.key = key;
    }

    public String getKey()
    {
      return key;
    }
  }
}
