package de.seitenbau.govdata.search.geostate.cache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Repository;

import de.seitenbau.govdata.odp.common.util.GovDataCollectionUtils;
import de.seitenbau.govdata.search.util.GeoStateParser;
import de.seitenbau.govdata.search.util.states.StateContainer;
import lombok.extern.slf4j.Slf4j;

/**
 * Cache for the state list.
 * 
 * @author rnoerenberg
 *
 */
@Slf4j
@Repository
public class GeoStateCache
{
  private List<StateContainer> stateList;

  /**
   * Gibt die Daten aus dem Cache zurück. Falls keine gültigen vorliegen werden diese erst geladen.
   * @return
   */
  public List<StateContainer> getStateList()
  {
    final String method = "getStateList() : ";
    log.trace(method + "Start");

    if (stateList == null)
    {
      log.info("{}Empty state-cache, building new List from source files.", method);

      List<StateContainer> geoStateList = new GeoStateParser().getStateList();
      if (geoStateList == null || geoStateList.isEmpty())
      {
        log.info(method + "Trying to use geo-state data. But it's empty.");
        log.trace(method + "End");
        return Collections.emptyList();
      }
      stateList = new ArrayList<>(geoStateList);
    }

    log.trace(method + "End");
    return GovDataCollectionUtils.getCopyOfList(stateList);
  }
}
