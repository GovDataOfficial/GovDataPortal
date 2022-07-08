package de.seitenbau.govdata.search.common.cache;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.springframework.stereotype.Repository;

import de.seitenbau.govdata.odp.common.cache.BaseCache;
import de.seitenbau.govdata.search.adapter.SearchService;
import de.seitenbau.govdata.search.index.model.SearchResultContainer;
import lombok.extern.slf4j.Slf4j;

/***
 * Liefert die gecachten Werte der Anzahl von Datasets, Anwendungen und Blogbeiträgen.
 * 
 * @author sgebhart
 */
@Slf4j
@Repository
public class NumberCache extends BaseCache
{
  private SearchResultContainer rawNumberData;

  @Inject
  private SearchService indexService;

  /**
   * Initializes required components and sets configuration parameters.
   */
  @PostConstruct
  public void init()
  {
    setMaxCacheTimeAmount(1);
  }

  /**
   * Gibt die Daten aus dem Cache zurück. Falls keine vorliegen werden diese erst geladen.
   * @return
   */
  public SearchResultContainer getRawNumberData()
  {
    if (rawNumberData == null || isCacheExpired())
    {
      rawNumberData = getNumberData();
    }

    return rawNumberData;
  }

  /**
   * Lädt aktuelle Daten aus Elasticsearch
   * @return
   */
  private SearchResultContainer getNumberData()
  {
    final String method = "getNumberData() : ";
    log.trace(method + "Start");

    log.info("{}Empty or expired number-data cache, fetching data from elastic search.", method);

    SearchResultContainer result = indexService.getNumbers();
    cacheUpdated();

    log.trace(method + "End");

    return result;
  }

}
