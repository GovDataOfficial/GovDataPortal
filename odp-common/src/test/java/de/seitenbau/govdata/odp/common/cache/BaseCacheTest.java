package de.seitenbau.govdata.odp.common.cache;

import java.time.temporal.ChronoUnit;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests für die Klasse {@link BaseCache}.
 * 
 * @author rnoerenberg
 *
 */
public class BaseCacheTest
{
  private BaseCacheClass target;

  @Before
  public void setup()
  {
    target = new BaseCacheClass();
  }

  @Test
  public void isCacheExpired_firstCall_withoutCacheUpdatedBefore() throws Exception
  {
    /* prepare */

    /* execute */
    boolean result = target.isCacheExpired();

    /* verify */
    Assertions.assertThat(result).isTrue();
  }

  @Test
  public void isCacheExpired_withKey_firstCall_withoutCacheUpdatedBefore() throws Exception
  {
    /* prepare */
    String key = "test";

    /* execute */
    boolean result = target.isCacheExpired(key);

    /* verify */
    Assertions.assertThat(result).isTrue();
  }

  @Test
  public void isCacheExpired_firstCall_after_cacheUpdatedWithoutKey() throws Exception
  {
    /* prepare */
    target.cacheUpdated();

    /* execute */
    boolean result = target.isCacheExpired();

    /* verify */
    Assertions.assertThat(result).isFalse();
  }

  @Test
  public void isCacheExpired_withKey_firstCall_after_cacheUpdatedWithoutKey() throws Exception
  {
    /* prepare */
    target.cacheUpdated();
    String key = "test";

    /* execute */
    boolean result = target.isCacheExpired(key);

    /* verify */
    Assertions.assertThat(result).isTrue();
  }

  @Test
  public void isCacheExpired_withKey_firstCall_after_cacheUpdatedWithKey() throws Exception
  {
    /* prepare */
    String key = "test";
    target.cacheUpdated(key);

    /* execute */
    boolean result = target.isCacheExpired(key);

    /* verify */
    Assertions.assertThat(result).isFalse();
  }

  @Test
  public void isCacheExpired_ttl_working() throws Exception
  {
    /* prepare */
    target.setCacheTemporalUnit(ChronoUnit.SECONDS);
    target.setMaxCacheTimeAmount(1);
    String key = "test";
    target.cacheUpdated(key);

    /* execute */
    boolean result = target.isCacheExpired(key);

    /* verify */
    Assertions.assertThat(result).isFalse();

    // Wait until cache expires
    Thread.sleep(1100);

    /* execute */
    result = target.isCacheExpired(key);

    /* verify */
    Assertions.assertThat(result).isTrue();
  }

  @Test
  public void isCacheExpired_ttl_cacheKey_independent() throws Exception
  {
    /* prepare */
    String key = "test";
    target.cacheUpdated(key);

    /* execute */
    boolean result = target.isCacheExpired(key);

    /* verify */
    Assertions.assertThat(result).isFalse();

    /* execute */
    result = target.isCacheExpired("otherKey");

    /* verify */
    Assertions.assertThat(result).isTrue();
  }

  private class BaseCacheClass extends BaseCache
  {

  }
}
