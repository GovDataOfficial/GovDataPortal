package de.seitenbau.govdata.cache;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.commons.lang3.time.DateUtils;


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

  private static final int MAX_CACHE_TIME_HOURS_DEFAULT = 2;

  private Date lastUpdated = new Date();
  
  private int maxCacheTimeHours = MAX_CACHE_TIME_HOURS_DEFAULT;

  protected boolean isCacheExpired()
  {
    Calendar maxCacheDate = new GregorianCalendar();
    maxCacheDate.add(Calendar.HOUR_OF_DAY, -maxCacheTimeHours);
    return DateUtils.toCalendar(lastUpdated).before(maxCacheDate);
  }

  protected void cacheUpdated()
  {
    lastUpdated = new Date();
  }

  /**
   * Gibt die maximale Zeit in Stunden zurück, die die Liste an Dateiformaten gecacht werden soll.
   * 
   * @return the maxCacheTimeHours
   */
  public int getMaxCacheTimeHours()
  {
    return maxCacheTimeHours;
  }

  /**
   * Setzt die maximale Zeit in Stunden, die die Liste an Dateiformaten gecacht werden soll.
   * 
   * @param maxCacheTimeHours the maxCacheTimeHours to set
   */
  public void setMaxCacheTimeHours(int maxCacheTimeHours)
  {
    this.maxCacheTimeHours = maxCacheTimeHours;
  }

}
