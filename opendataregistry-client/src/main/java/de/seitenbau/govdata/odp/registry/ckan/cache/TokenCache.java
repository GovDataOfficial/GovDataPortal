package de.seitenbau.govdata.odp.registry.ckan.cache;

import java.util.HashMap;
import java.util.Map;

import de.seitenbau.govdata.odp.common.cache.BaseCache;

/**
 * Cache for CKAN API token management.
 * @author sgebhart
 *
 */
public class TokenCache extends BaseCache
{
  private Map<String, String> tokenCacheMap = new HashMap<>();

  /**
   * Constructor.
   */
  public TokenCache()
  {
    setMaxCacheTimeAmount(24); // 24 hours
  }

  /**
   * Return the cached token for a user.
   * @param userId
   * @return
   */
  public String getTokenForUser(String userId)
  {
    if (isCacheExpired(userId))
    {
      return null;
    }
    return tokenCacheMap.get(userId);
  }

  /**
   * Update the cached token for a user.
   * @param token
   * @param userId
   */
  public void updateCache(String token, String userId)
  {
    tokenCacheMap.put(userId, token);
    cacheUpdated(userId);
  }
}
