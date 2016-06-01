package de.seitenbau.govdata.search.cache;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Repository;

import de.seitenbau.govdata.cache.BaseCache;
import de.seitenbau.govdata.search.adapter.SearchService;

/**
 * Liest die Dateiformate aus Elasticsearch und gibt diese zurück. Dabei wird die Liste an
 * Dateiformaten eine bestimmte Zeit gecacht. Default sind {@value #MAX_CACHE_TIME_HOURS_DEFAULT}
 * Stunden.
 * 
 * @author tscheffler
 * @author rnoerenberg
 *
 */
@Slf4j
@Repository
public class ResourceFormatCache extends BaseCache
{
  @Inject
  private SearchService indexService;
  
  private List<String> formats;

  private List<String> formatsSorted;

  /**
   * Liest die vorhandenen Dateiformate aus Elasticsearch aus und gibt diese unverändert zurück.
   * 
   * @return die Liste an Dateiformaten.
   */
  public List<String> getFormats()
  {
    if (formats == null || isCacheExpired())
    {
      log.info("Empty or expired resource format cache, fetching resource formats from Elasticsearch.");
      // 48 can be divided by 6,4 and 2, important for responsive view.
      formats = indexService.getResourceFormats(48);
      cacheUpdated();
      // invalidate sorted cache
      formatsSorted = null;
    }

    return formats;
  }

  /**
   * Liest die vorhandenen Dateiformate aus Elasticsearch aus und gibt diese nach dem Titel
   * aufsteigend sortiert unverändert zurück.
   * 
   * @return die sortierte Liste an Dateiformaten.
   */
  public List<String> getFormatsSorted()
  {
    if (formatsSorted == null || isCacheExpired())
    {
      // Avoid ConcurrentModificationException on same list object when modifying list in Java 8
      List<String> formatsTemp = getFormats();
      if (formatsTemp != null)
      {
        formatsSorted = new ArrayList<String>(formatsTemp);
        Collator collator = Collator.getInstance(Locale.GERMAN);
        collator.setStrength(Collator.SECONDARY);// a == A, a < Ä
        Collections.sort(formatsSorted, collator);
      }
    }

    return formatsSorted;
  }
}
