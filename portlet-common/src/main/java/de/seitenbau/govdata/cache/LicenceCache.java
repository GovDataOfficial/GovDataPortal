package de.seitenbau.govdata.cache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Repository;

import de.fhg.fokus.odp.registry.model.Licence;
import de.seitenbau.govdata.comparator.LicencesTitleComparator;

@Slf4j
@Repository
public class LicenceCache extends BaseRegistryClientCache
{
  private List<Licence> licences = null;

  private Map<String, Licence> licenceMap = null;

  private List<Licence> licenceMapSortedByTitle = null;

  public Map<String, Licence> getLicenceMap()
  {
    // fill internal cache if not initialized
    if (licenceMap == null)
    {
      licenceMap = new HashMap<>();

      for (Licence licence : getLicences())
      {
        licenceMap.put(licence.getName(), licence);
      }
    }

    return licenceMap;
  }

  public List<Licence> getLicenceListSortedByTitle()
  {
    if (licenceMapSortedByTitle == null)
    {
      // Avoid ConcurrentModificationException on same list object when modifying list in Java 8
      List<Licence> licencesTemp = getLicences();
      if (licencesTemp != null)
      {
        licenceMapSortedByTitle = new ArrayList<Licence>(licencesTemp);
        Collections.sort(licenceMapSortedByTitle, new LicencesTitleComparator());
      }
    }

    return licenceMapSortedByTitle;
  }

  private List<Licence> getLicences()
  {
    if (licences == null)
    {
      log.info("Empty licence cache, fetching licences from CKAN.");
      licences = new ArrayList<>();
      for (Licence licence : getRegistryClient().getInstance().listLicenses())
      {
        if (licence != null)
        {
          licences.add(licence);
        }
      }
    }

    return licences;
  }
}
