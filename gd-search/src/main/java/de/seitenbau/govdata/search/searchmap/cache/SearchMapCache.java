package de.seitenbau.govdata.search.searchmap.cache;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import de.seitenbau.govdata.odp.common.cache.BaseCache;
import lombok.extern.slf4j.Slf4j;

/***
 * Gets the cached session ID or create a new one, if the cached value has expired.
 */
@Slf4j
@Repository
public class SearchMapCache extends BaseCache
{
  private String sessionId;

  @Value("${mapsearch.sessionid.request.url:}")
  private String requestUrl;

  @Value("${mapsearch.app.id:}")
  private String appId;

  @Value("${mapsearch.app.domain:}")
  private String appDomain;

  private CloseableHttpClient httpClient;

  /**
   * Initializes required components and sets configuration parameters.
   */
  @PostConstruct
  public void init()
  {
    // 6 Hours
    setMaxCacheTimeAmount(6);
    httpClient = HttpClients.custom().useSystemProperties().build();
  }

  /**
   * Getter for the field "sessionId".
   * 
   * @return the session ID
   */
  public String getSessionId()
  {
    final String method = "getSessionId() : ";
    log.trace(method + "Start");

    if (sessionId == null || isCacheExpired())
    {
      log.info("{} Empty or expired Session ID cache for search map. Creating new Session ID...", method);
      sessionId = getNewSessionId();
      cacheUpdated();
    }

    log.trace("{}End", method);
    return sessionId;
  }

  private String getNewSessionId()
  {
    final String method = "getNewSessionId() : ";
    log.trace(method + "Start");

    String response = sendRequest();

    if (response == null)
    {
      log.warn("{} Did not receive data from BKG!", method);
      log.trace(method + "End");
      return null;
    }

    log.trace("{} End", method);
    return response;
  }

  private String sendRequest()
  {
    String sessionId = null;
    HttpResponse response = null;
    HttpEntity entity = null;

    if (StringUtils.isNotEmpty(requestUrl))
    {
      try
      {
        URIBuilder uriBuilder = new URIBuilder(requestUrl);
        List<NameValuePair> queryParameters = new ArrayList<>();
        queryParameters.add(new BasicNameValuePair("bkg_appid", appId));
        queryParameters.add(new BasicNameValuePair("domain", appDomain));

        uriBuilder.addParameters(queryParameters);

        HttpGet httpGet = new HttpGet(uriBuilder.build());

        response = httpClient.execute(httpGet);
        entity = response.getEntity();
        if (entity != null)
        {
          sessionId = EntityUtils.toString(entity, StandardCharsets.UTF_8);
        }
      }
      catch (URISyntaxException | IOException ex)
      {
        log.warn("Problem while requesting data from BKG service: {}", ex.getMessage());
      }
      finally
      {
        EntityUtils.consumeQuietly(entity);
        HttpClientUtils.closeQuietly(response);
      }
    }
    return sessionId;
  }
}
