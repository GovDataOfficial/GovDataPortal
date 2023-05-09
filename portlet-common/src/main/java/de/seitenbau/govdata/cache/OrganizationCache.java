package de.seitenbau.govdata.cache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Repository;

import de.seitenbau.govdata.odp.common.util.GovDataCollectionUtils;
import de.seitenbau.govdata.odp.registry.model.Organization;

/**
 * Liefert die gecachte Liste der Organisationen.
 * 
 * @author rnoerenberg
 */
@Slf4j
@Repository
public class OrganizationCache extends BaseRegistryClientCache
{
  private Map<String, Organization> organizationMap = null;
  private List<Organization> organizationsSorted;

  /**
   * Gibt die Organisationen in einer Map zurück. Der Key der Map ist die ID der Organisation.
   * 
   * @return die Map mit den Organisationen.
   */
  public Map<String, Organization> getOrganizationMap()
  {
    final String method = "getOrganizationMap() : ";
    log.trace(method + "Start");

    // fill internal cache if not initialized or cache expired
    if (organizationMap == null || isCacheExpired())
    {
      log.info("{}Empty or expired organization cache, fetching organizations from CKAN.", method);
      organizationMap = new HashMap<>();

      List<Organization> organizationsFromCkan = getRegistryClient().getInstance().getOrganizations();
      if (CollectionUtils.isNotEmpty(organizationsFromCkan))
      {
        for (Organization organization : organizationsFromCkan)
        {
          if (organization != null)
          {
            organizationMap.put(organization.getId(), organization);
          }
        }
      }
      cacheUpdated();
    }

    log.trace(method + "End");
    return new HashMap<String, Organization>(organizationMap);
  }
  
  /**
   * Gibt die Organisationen in einer sortierten Liste zurück.
   * 
   * @return die sortierte Liste der Organisationen.
   */
  public List<Organization> getOrganizationsSorted()
  {
    final String method = "getOrganizationsSorted() : ";
    log.trace(method + "Start");

    // fill internal cache if not initialized or cache expired
    if (organizationsSorted == null || isCacheExpired())
    {
      // Avoid ConcurrentModificationException on same list object when modifying list in Java 8
      ArrayList<Organization> tmpList = new ArrayList<Organization>(getOrganizationMap().values());
      Collections.sort(tmpList);
      organizationsSorted = tmpList;
    }

    log.trace(method + "End");
    return GovDataCollectionUtils.getCopyOfList(organizationsSorted);
  }
}
