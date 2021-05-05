package de.seitenbau.govdata.metadataquality.cache;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.elasticsearch.search.SearchHits;
import org.springframework.stereotype.Repository;

import de.seitenbau.govdata.cache.BaseRegistryClientCache;
import de.seitenbau.govdata.search.adapter.SearchService;
import lombok.extern.slf4j.Slf4j;

/**
 * Liefert die gecachten Werte für das Metadataquality portlet.
 * 
 * @author sgebhart
 */
@Slf4j
@Repository
public class MetricDataCache extends BaseRegistryClientCache
{
  private SearchHits rawMetricData;

  @Inject
  private SearchService indexService;

  /**
   * Initializes required components and sets configuration parameters.
   */
  @PostConstruct
  public void init()
  {
    setMaxCacheTimeHours(6);
  }

  /**
   * Gibt die Daten aus dem Cache zurück. Falls keine vorliegen werden diese erst geladen.
   * @return
   */
  public SearchHits getRawMetricData()
  {
    if (rawMetricData == null || isCacheExpired())
    {
      rawMetricData = getMetricData();
    }

    return rawMetricData;
  }

  /**
   * Lädt aktuelle Daten aus Elasticsearch
   * @return
   */
  private SearchHits getMetricData()
  {
    final String method = "getMetricData() : ";
    log.trace(method + "Start");

    log.info("{}Empty or expired metric-data cache, fetching data from elastic search.", method);

    SearchHits updatedMetricData = indexService.getMetrics();
    cacheUpdated();

    log.trace(method + "End");

    return updatedMetricData;
  }

}
