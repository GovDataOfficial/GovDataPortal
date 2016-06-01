package de.seitenbau.govdata.cache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import de.fhg.fokus.odp.registry.model.Organization;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class OrganizationCache extends BaseRegistryClientCache
{
  private Map<String, Organization> organizationMap = null;
  private List<Organization> organizationsSorted;

  public Map<String, Organization> getOrganizationMap()
  {
    // fill internal cache if not initialized or cache expired
    if (organizationMap == null || isCacheExpired())
    {
      log.info("Empty or expired organization cache, fetching organizations from CKAN.");
      organizationMap = new HashMap<>();

      List<Organization> organizationsFromCkan = getRegistryClient().getInstance().getOrganizations();
      if (organizationsFromCkan != null)
      {
        for (Organization organization : organizationsFromCkan)
        {
          organizationMap.put(organization.getId(), organization);
        }
      }
      cacheUpdated();
    }

    return organizationMap;
  }
  
  public List<Organization> getOrganizationsSorted()
  {
    if (organizationsSorted == null || isCacheExpired())
    {
      // Avoid ConcurrentModificationException on same list object when modifying list in Java 8
      ArrayList<Organization> tmpList = new ArrayList<Organization>(getOrganizationMap().values());
      Collections.sort(tmpList);
      organizationsSorted = tmpList;
    }

    return organizationsSorted;
  }
}
