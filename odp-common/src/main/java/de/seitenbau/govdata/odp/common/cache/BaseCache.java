package de.seitenbau.govdata.odp.common.cache;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


/**
 * Eine Basisklasse mit gemeinsam genutzten Methoden für die verschiedenen Cache-Implementierungen.
 * 
 * @author rnoerenberg
 *
 */
public abstract class BaseCache
{
  /** The cache name categories grid. */
  public static final String CACHE_NAME_CATEGORIES_GRID = "odp.categoriesgrid";

  /** The cache name boxes. */
  public static final String CACHE_NAME_BOXES = "odp.boxes";

  private static final int MAX_CACHE_TIME_AMOUNT_DEFAULT = 2;

  private static final String DEFAULT_KEY = "default";

  private TemporalUnit cacheTemporalUnit = ChronoUnit.HOURS;

  private int maxCacheTimeAmount = MAX_CACHE_TIME_AMOUNT_DEFAULT;

  private Map<String, Instant> lastUpdatedMap = new HashMap<>();

  protected boolean isCacheExpired()
  {
    return isCacheExpired(DEFAULT_KEY);
  }

  protected void cacheUpdated()
  {
    cacheUpdated(DEFAULT_KEY);
  }

  protected boolean isCacheExpired(String key)
  {
    Instant lastUpdated = lastUpdatedMap.get(key);
    if (Objects.nonNull(lastUpdated))
    {
      Instant maxCacheDate = Instant.now().minus(maxCacheTimeAmount, cacheTemporalUnit);
      return lastUpdated.isBefore(maxCacheDate);
    }
    return true;
  }

  protected void cacheUpdated(String key)
  {
    lastUpdatedMap.put(key, Instant.now());
  }

  /**
   * Gibt die maximale Zeit (in der Einheit {@link cacheTemporalUnit}) zurück, die die Daten gecacht
   * werden sollen.
   * 
   * @return the maxCacheTimeAmount
   */
  public int getMaxCacheTimeAmount()
  {
    return maxCacheTimeAmount;
  }

  /**
   * Sets maxCacheTimeAmount. Default: {@link MAX_CACHE_TIME_AMOUNT_DEFAULT}.
   * 
   * @param maxCacheTimeAmount the maxCacheTimeAmount to set
   */
  public void setMaxCacheTimeAmount(int maxCacheTimeAmount)
  {
    this.maxCacheTimeAmount = maxCacheTimeAmount;
  }

  /**
   * Gets the value for cacheTemporalUnit. Default: {@link ChronoUnit.HOURS}.
   * 
   * @return the cacheTemporalUnit.
   */
  public TemporalUnit getCacheTemporalUnit()
  {
    return cacheTemporalUnit;
  }

  /**
   * Sets cacheTemporalUnit. Default: {@link ChronoUnit.HOURS}.
   * 
   * @param cacheTemporalUnit the cacheTemporalUnit to set
   */
  public void setCacheTemporalUnit(TemporalUnit cacheTemporalUnit)
  {
    this.cacheTemporalUnit = cacheTemporalUnit;
  }

  /**
   * Setzt die maximale Zeit in Stunden, die die Daten gecacht werden sollen.
   * 
   * Deprecated: Use {@link setMaxCacheTimeAmount} in combination with {@link setCacheTemporalUnit}.
   * 
   * @param maxCacheTimeHours the maxCacheTimeHours to set
   */
  @Deprecated
  public void setMaxCacheTimeHours(int maxCacheTimeHours)
  {
    this.cacheTemporalUnit = ChronoUnit.HOURS;
    this.maxCacheTimeAmount = maxCacheTimeHours;
  }

}
