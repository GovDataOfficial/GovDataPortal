package de.seitenbau.govdata.search.showcase.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.inject.Inject;

import org.springframework.stereotype.Repository;

import de.seitenbau.govdata.dataset.details.beans.SelectedShowcase;
import de.seitenbau.govdata.db.api.ShowcaseResource;
import de.seitenbau.govdata.db.api.model.Showcase;
import de.seitenbau.govdata.edit.mapper.ShowcaseMapper;
import de.seitenbau.govdata.edit.model.ShowcaseViewModel;
import de.seitenbau.govdata.odp.common.cache.BaseCache;
import lombok.extern.slf4j.Slf4j;

/**
 * Cache für das Features Showcase portlet. Es können mehrere dieser Portlets mit unterschiedlichen
 * Showcases existieren.
 * 
 * @author sgebahrt
 */
@Slf4j
@Repository
public class FeaturedShowcaseCache extends BaseCache
{
  @Inject
  private ShowcaseResource showcaseResource;

  private Map<String, SelectedShowcase> showcaseCacheMap = new HashMap<>();

  /**
   * Gibt den gecachten Wert zurück und erneuert ihn falls nötig.
   * @param key
   * @param showcaseId
   * @return
   */
  public SelectedShowcase getShowcaseForKey(String key, long showcaseId)
  {
    final String method = "getShowcaseForKey() : ";
    log.trace(method + "Start");

    boolean updateCache = false;
    // if key is missing || id has changed || showcase is private || cache expired
    SelectedShowcase cachedShowcase = showcaseCacheMap.get(key);
    if (cachedShowcase == null || isCacheExpired(key))
    {
      log.info(method + "Cache is empty or expired. Update featured showcase cache for key '{}' and "
          + "showcaseId '{}'.", key, showcaseId);
      updateCache = true;
    }
    else if (cachedShowcase.getShowcase().getId() != showcaseId)
    {
      log.info(method + "Showcase Id has changed! Update featured showcase cache for key '{}' and "
          + "showcaseId '{}'.", key, showcaseId);
      updateCache = true;
    }
    else if (cachedShowcase.getShowcase().isPrivate())
    {
      log.debug(method + "Cached showcase is marked as private! Update featured showcase cache for key '{}' "
          + "and showcaseId '{}'.", key, showcaseId);
      updateCache = true;
    }

    if (updateCache)
    {
      updateFeaturedShowcaseCache(key, showcaseId);
    }

    log.trace(method + "End");
    return showcaseCacheMap.get(key);
  }

  /**
   * Frage einen Showcase bei der Datenbank an und speichere ihn.
   * @param key
   * @param showcaseId
   */
  private void updateFeaturedShowcaseCache(String key, Long showcaseId)
  {
    final String method = "updateFeaturedShowcaseCache() : ";
    log.trace(method + "Start");

    Showcase showcase;
    try
    {
      // read showcase from db
      showcase = showcaseResource.read(showcaseId);
      Objects.requireNonNull(showcase, "showcase is null!");
    }
    catch (Exception ex)
    {
      showcaseCacheMap.remove(key);
      log.warn(method + "Error while updating Cache: " + ex.getMessage());
      return;
    }

    // showcase mapping
    ShowcaseViewModel showcaseViewModel = ShowcaseMapper.mapShowcaseEntityToViewModel(showcase);
    SelectedShowcase selectedShowcase = new SelectedShowcase(showcaseViewModel, null, null);

    // save in cache
    showcaseCacheMap.put(key, selectedShowcase);
    cacheUpdated(key);

    log.trace(method + "End");
  }
}
