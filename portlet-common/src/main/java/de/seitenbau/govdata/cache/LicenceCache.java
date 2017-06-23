package de.seitenbau.govdata.cache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Repository;

import de.fhg.fokus.odp.registry.model.Licence;
import de.seitenbau.govdata.comparator.LicencesTitleComparator;

/**
 * Liefert die gecachte Liste der Lizenzen.
 * 
 * @author rnoerenberg
 */
@Slf4j
@Repository
public class LicenceCache extends BaseRegistryClientCache
{
  private Map<String, Licence> licenceMap = null;

  private List<Licence> licenceMapSortedByTitle = null;

  /**
   * Initialisiert die Klasse mit individuellen Parametern.
   */
  @PostConstruct
  public void init()
  {
    setMaxCacheTimeHours(12);
  }

  /**
   * Gibt die Lizenzen in einer Map zurück. Der Key der Map ist der Name der Lizenzen.
   * 
   * @return die Map mit den Lizenzen.
   */
  public Map<String, Licence> getLicenceMap()
  {
    final String method = "getLicenceMap() : ";
    log.trace(method + "Start");

    // fill internal cache if not initialized or cache expired
    if (licenceMap == null || isCacheExpired())
    {
      licenceMap = new HashMap<>();

      List<Licence> licencesTemp = getLicences();
      if (CollectionUtils.isNotEmpty(licencesTemp))
      {
        for (Licence licence : licencesTemp)
        {
          licenceMap.put(licence.getName(), licence);
        }
      }
    }

    log.trace(method + "End");
    return licenceMap;
  }

  /**
   * Gibt die Lizenzen in einer sortierten Liste zurück.
   * 
   * @return die sortierte Liste der Lizenzen.
   */
  public List<Licence> getLicenceListSortedByTitle()
  {
    final String method = "getLicenceListSortedByTitle() : ";
    log.trace(method + "Start");

    // fill internal cache if not initialized or cache expired
    if (licenceMapSortedByTitle == null || isCacheExpired())
    {
      List<Licence> licencesTemp = getLicences();
      if (licencesTemp != null)
      {
        // Avoid ConcurrentModificationException on same list object when modifying list in Java 8
        licenceMapSortedByTitle = new ArrayList<Licence>(licencesTemp);
        Collections.sort(licenceMapSortedByTitle, new LicencesTitleComparator());
      }
    }

    log.trace(method + "End");
    return licenceMapSortedByTitle;
  }

  private List<Licence> getLicences()
  {
    final String method = "getLicences() : ";
    log.trace(method + "Start");

    log.info("{}Empty or expired licence cache, fetching licences from CKAN.", method);

    List<Licence> licences = new ArrayList<>();
    List<Licence> licensesFromCkan = getRegistryClient().getInstance().listLicenses();
    if (CollectionUtils.isNotEmpty(licensesFromCkan))
    {
      for (Licence licence : licensesFromCkan)
      {
        if (licence != null)
        {
          licences.add(licence);
        }
      }
    }
    cacheUpdated();

    log.trace(method + "End");
    return licences;
  }
}
