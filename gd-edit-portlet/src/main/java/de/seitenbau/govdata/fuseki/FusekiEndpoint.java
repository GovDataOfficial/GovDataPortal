package de.seitenbau.govdata.fuseki;

import org.apache.commons.lang3.StringUtils;

/**
 * Liefert die URLs für die verschiedenen Endpunkte des Fuseki-Servers
 *
 */
public enum FusekiEndpoint
{
  /** Der Endpunkt "update". */
  UPDATE("update"),

  /** Der Endpunkt "query". */
  QUERY("query"),

  /** Der Endpunkt "data". */
  DATA("data"),

  /** Der Endpunkt "ping". */
  PING("$/ping");

  private String value;

  private FusekiEndpoint(String value)
  {
    this.value = value;
  }

  public String getValue()
  {
    return value;
  }

  /**
   * Erstellt aus der baseUrl eine URL zu dem entsprechenden Fuseki-Endpunkt
   * @param baseUrl URL für den Triplestore
   * @return
   */
  public String getFusekiEndpoint(String baseUrl)
  {
    return StringUtils.appendIfMissing(baseUrl, "/") + getValue();
  }
}