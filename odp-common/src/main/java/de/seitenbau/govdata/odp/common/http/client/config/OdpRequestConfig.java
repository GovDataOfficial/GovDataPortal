package de.seitenbau.govdata.odp.common.http.client.config;

import org.apache.http.client.config.RequestConfig;

/**
 * The default request configuration for the open data portal.
 */
public final class OdpRequestConfig
{
  private OdpRequestConfig()
  {
    // no instances
  }

  /**
   * The default request configuration.
   */
  public static final RequestConfig REQUEST_CONFIG =
      RequestConfig.custom().setConnectTimeout(15 * 1000).setConnectionRequestTimeout(15 * 1000)
          .setSocketTimeout(20 * 1000).build();
}
