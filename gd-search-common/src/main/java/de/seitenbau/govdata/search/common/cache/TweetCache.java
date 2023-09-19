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

import org.apache.commons.lang3.StringUtils;
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

import de.seitenbau.govdata.odp.common.cache.BaseCache;
import de.seitenbau.govdata.search.common.cache.util.PostContainer;
import lombok.extern.slf4j.Slf4j;

/***
 * Liefert den gecachten Tweet.
 * 
 * @author sgebhart
 */
@Slf4j
@Repository
public class TweetCache extends BaseCache
{
  private PostContainer tweet;

  @Value("${govdata.twitter.url}")
  private String twitterUrl;

  @Value("${govdata.twitter.api.endpoint.tweets}")
  private String tweetEndpoint;

  @Value("${govdata.twitter.api.bearer.token}")
  private String twitterApiToken;

  private CloseableHttpClient httpClient;

  private static final String TWEET_DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

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
   * Gibt die Daten aus dem Cache zurück. Falls keine vorliegen werden diese erst geladen.
   * @return
   */
  public PostContainer getTweetData()
  {
    final String method = "getTweetData() : ";
    log.trace("{}Start", method);

    if (tweet == null || isCacheExpired())
    {
      log.info("{}Empty or expired tweet-data cache. Getting data from Twitter API...", method);
      tweet = getNewestTweet();
      cacheUpdated();
    }

    log.trace("{}End", method);
    return tweet;
  }

  /**
   * Frage den aktuellen Tweet von der Twitter API ab.
   * @return
   */
  private PostContainer getNewestTweet()
  {
    final String method = "getNewestTweet() : ";
    log.trace(method + "Start");

    PostContainer result;
    String response = sendRequest();
    if (response == null)
    {
      log.warn("{}Did not receive data from Twitter!", method);
      log.trace(method + "End");
      return null;
    }

    try
    {
      JSONObject json = (JSONObject) new JSONParser().parse(response);
      JSONObject latestPost = readLatestPostFromJson(json);
      if (Objects.isNull(latestPost))
      {
        log.warn("{}Could not read latest twitter post from JSON response!", method);
        log.trace(method + "End");
        return null;
      }

      String tweetType = "post";
      if (latestPost.containsKey(TwitterApiKeys.POST_REFERENCED_TWEETS.getKey()))
      {
        // check if the post is a retweet
        JSONArray referencedTweets =
            (JSONArray) latestPost.get(TwitterApiKeys.POST_REFERENCED_TWEETS.getKey());
        JSONObject referencedTweet = (JSONObject) referencedTweets.get(0);
        tweetType = referencedTweet.get("type").toString();
      }

      LocalDateTime tweetDate = null;
      if (latestPost.containsKey(TwitterApiKeys.POST_CREATED_AT.getKey()))
      {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(TWEET_DATE_PATTERN);
        try
        {
          // parse date
          LocalDateTime parsedDate =
              LocalDateTime.parse(latestPost.get(TwitterApiKeys.POST_CREATED_AT.getKey()).toString(), dtf);
          log.debug("parsedDate: {}", parsedDate);
          tweetDate = parsedDate.atZone(ZoneId.of("UTC")).withZoneSameInstant(ZoneId.of("Europe/Berlin"))
              .toLocalDateTime();
          log.debug("tweetDate: {}", tweetDate);
        }
        catch (DateTimeParseException ex)
        {
          log.warn("{} Could not parse Tweet date: {}", method, ex.getMessage());
        }
      }

      JSONObject jsonUser = readUserFromJson(json);
      result = PostContainer.builder()
          .text(latestPost.get(TwitterApiKeys.POST_TEXT.getKey()).toString())
          .id(latestPost.get(TwitterApiKeys.POST_ID.getKey()).toString())
          .url(twitterUrl)
          .username(jsonUser.get(TwitterApiKeys.USER_USERNAME.getKey()).toString())
          .name(jsonUser.get(TwitterApiKeys.USER_NAME.getKey()).toString())
          .type(tweetType)
          .timestamp(tweetDate).build();
    }
    catch (ParseException e)
    {
      log.warn("{}Could not parse data from Twitter: {}", method, e.getMessage());
      log.trace(method + "End");
      return null;
    }

    log.trace(method + "End");

    return result;
  }

  private JSONObject readLatestPostFromJson(JSONObject json)
  {
    JSONObject latestPost = null;
    if (Objects.nonNull(json))
    {
      JSONArray jsonData = (JSONArray) json.get(TwitterApiKeys.DATA.getKey());
      if (Objects.nonNull(jsonData))
      {
        latestPost = (JSONObject) jsonData.get(0);
      }
    }
    return latestPost;
  }

  private JSONObject readUserFromJson(JSONObject json)
  {
    JSONObject jsonUser = null;
    if (Objects.nonNull(json))
    {
      JSONObject jsonInclude = (JSONObject) json.get(TwitterApiKeys.INCLUDES.getKey());
      if (Objects.nonNull(jsonInclude))
      {
        JSONArray jsonUsers = (JSONArray) jsonInclude.get(TwitterApiKeys.USERS.getKey());
        if (Objects.nonNull(jsonUsers))
        {
          jsonUser = (JSONObject) jsonUsers.get(0);
        }
      }
    }
    return jsonUser;
  }

  /**
   * Stelle Request an die Twitter API.
   */
  private String sendRequest()
  {
    String searchResponse = null;
    HttpResponse response = null;
    HttpEntity entity = null;
    try
    {
      URIBuilder uriBuilder = new URIBuilder(tweetEndpoint);
      List<NameValuePair> queryParameters = new ArrayList<>();
      queryParameters.add(new BasicNameValuePair("max_results", "5")); // 5 is minimum
      queryParameters.add(new BasicNameValuePair("exclude", TwitterApiKeys.REPLIES.getKey()));
      queryParameters.add(new BasicNameValuePair("expansions", TwitterApiKeys.AUTHOR_ID.getKey()));
      queryParameters.add(new BasicNameValuePair("user.fields", TwitterApiKeys.POST_CREATED_AT.getKey()));
      queryParameters.add(new BasicNameValuePair("tweet.fields", StringUtils.joinWith(",",
          TwitterApiKeys.POST_REFERENCED_TWEETS.getKey(), TwitterApiKeys.POST_CREATED_AT.getKey())));
      uriBuilder.addParameters(queryParameters);

      HttpGet httpGet = new HttpGet(uriBuilder.build());
      httpGet.setHeader(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", twitterApiToken));
      httpGet.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.toString());

      response = httpClient.execute(httpGet);
      entity = response.getEntity();
      if (null != entity)
      {
        searchResponse = EntityUtils.toString(entity, StandardCharsets.UTF_8);
      }
    }
    catch (URISyntaxException | IOException ex)
    {
      log.warn("Problem while requesting data from Twitter API: {}", ex.getMessage());
    }
    finally
    {
      EntityUtils.consumeQuietly(entity);
      HttpClientUtils.closeQuietly(response);
    }
    return searchResponse;
  }

  private enum TwitterApiKeys
  {
    AUTHOR_ID("author_id"),
    USERS("users"),
    INCLUDES("includes"),
    DATA("data"),
    POST_CREATED_AT("created_at"),
    POST_REFERENCED_TWEETS("referenced_tweets"),
    POST_TEXT("text"),
    POST_ID("id"),
    REPLIES("replies"),
    USER_USERNAME("username"),
    USER_NAME("name");

    private String key;

    private TwitterApiKeys(String key)
    {
      this.key = key;
    }

    public String getKey()
    {
      return key;
    }
  }

}

