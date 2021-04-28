package de.seitenbau.govdata.odp.boxes;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.context.FacesContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;

import de.seitenbau.govdata.cache.BaseCache;
import de.seitenbau.govdata.servicetracker.MultiVMPoolServiceTracker;

public abstract class BaseBoxesBean<T>
{
  /** The log. */
  private static final Logger LOG = LoggerFactory.getLogger(BaseBoxesBean.class);

  /** The time to live in seconds. */
  protected static final int CLUSTERED_CACHE_TTL_IN_SECONDS = 3600;

  protected MultiVMPoolServiceTracker multiVMPoolTracker;

  /**
   * An init method for all beans.
   */
  @PostConstruct
  public void initBase()
  {
    // read clustered cache service
    multiVMPoolTracker = new MultiVMPoolServiceTracker(this);
    multiVMPoolTracker.open();
    LOG.debug("Initialize complete");
  }

  @PreDestroy
  public void shutdown()
  {
    multiVMPoolTracker.close();
  }

  public ThemeDisplay getThemeDisplay()
  {
    ThemeDisplay themeDisplay = (ThemeDisplay) FacesContext.getCurrentInstance().getExternalContext()
        .getRequestMap().get(WebKeys.THEME_DISPLAY);
    return themeDisplay;
  }

  protected void updateCache(List<T> entities, CacheKey cacheKey)
  {
    PortalCache<String, Serializable> portalCache = getPortalCache();
    if (portalCache != null)
    {
      LOG.debug("Update clustered cache.");
      // expects that the timeToLive parameter are seconds
      portalCache.put(cacheKey.name(), (Serializable) entities, CLUSTERED_CACHE_TTL_IN_SECONDS);
    }
  }

  @SuppressWarnings("unchecked")
  protected List<T> readItemsFromClusteredCache(CacheKey cacheKey)
  {
    final String method = "readBlogsEntriesFromClusteredCache() : ";
    LOG.trace(method + "Start");

    List<T> result = null;
    LOG.debug("Start reading items from clustered cache...");
    MultiVMPool multiVMPool = multiVMPoolTracker.getService();
    if (multiVMPool != null)
    {
      result = (List<T>) getPortalCache().get(cacheKey.name());
    }
    else
    {
      LOG.warn("The required service 'MultiVMPool' is not available.");
    }
    LOG.debug("Finished reading items from clustered cache.");

    LOG.trace(method + "End");
    return result;
  }

  @SuppressWarnings("unchecked")
  private PortalCache<String, Serializable> getPortalCache()
  {
    PortalCache<String, Serializable> portalCache = null;
    MultiVMPool multiVMPool = multiVMPoolTracker.getService();
    if (multiVMPool != null)
    {
      portalCache =
          (PortalCache<String, Serializable>) multiVMPool.getPortalCache(BaseCache.CACHE_NAME_BOXES);
    }
    return portalCache;
  }

}
