package de.seitenbau.govdata.odp.common.filter;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import lombok.extern.slf4j.Slf4j;

/**
 * Serializes and deserializes filter url params.
 * 
 * @author tscheffler
 *
 */
@Slf4j
public final class FilterPathUtils
{
  private static final String GROUP_DELIMITER = ",";

  private static final String KEY_VAL_DELIMITER = ":";

  private FilterPathUtils()
  {
    // disallow instances
  }
  /**
   * Decodes path fragment (like key:val,key:val
   * @param pathFragment serialized filterstring
   * @return Map of active filternames grouped by filtertype
   */
  public static Map<String, List<String>> deserializeFilter(String pathFragment)
  {
    // TODO validate pathFragment syntax via regex

    Map<String, List<String>> activeFilterMap = new HashMap<>();

    try
    {
      if (pathFragment != null)
      {
        String[] groups = pathFragment.split(GROUP_DELIMITER);
        for (String group : groups)
        {
          int splitIndex = group.indexOf(KEY_VAL_DELIMITER);
          String key = group.substring(0, splitIndex);
          String val = group.substring(splitIndex + 1);

          String decodedValue = decodeFilterValue(val);

          // Add new List if not exist
          List<String> filterList;
          if (activeFilterMap.containsKey(key))
          {
            filterList = activeFilterMap.get(key);
          }
          else
          {
            filterList = new ArrayList<>();
            activeFilterMap.put(key, filterList);
          }

          filterList.add(decodedValue);
        }
      }
    }
    catch (Exception e)
    {
      log.debug("Error at processing filters: " + e.getMessage());
      return new HashMap<>(); // don't process filters if the string is invalid
    }

    return activeFilterMap;
  }

  /**
   * Serialize a filter map.
   * @param activeFilterMap
   * @return
   */
  public static String serializeFilterMap(Map<String, List<String>> activeFilterMap)
  {
    StringBuilder sb = new StringBuilder();
    for (Entry<String, List<String>> entry : activeFilterMap.entrySet())
    {
      for (String val : entry.getValue())
      {
        sb.append(serializeFilter(entry.getKey(), val));
      }
    }

    // has a trailing ; - but that's intended!
    return sb.toString();
  }

  /**
   * Serialize a filter value.
   * @param key
   * @param val
   * @return
   */
  public static String serializeFilter(String key, String val)
  {
    return key + KEY_VAL_DELIMITER + encodeFilterValue(val) + GROUP_DELIMITER;
  }

  /**
   * Searialize a filter key.
   * @param key
   * @return
   */
  public static String serializeFilterMatchAllRegex(String key)
  {
    return key + KEY_VAL_DELIMITER + ".+?" + GROUP_DELIMITER;
  }

  /**
   * Decode a filter value.
   * @param encodedValue
   * @return
   */
  public static String decodeFilterValue(String encodedValue)
  {
    try
    {
      return URLDecoder.decode(encodedValue, "UTF-8");
    }
    catch (UnsupportedEncodingException e)
    {
      log.error("Decoding of licence parameter failed!", e);
      return encodedValue;
    }
  }

  /**
   * Encodes a filter value.
   * @param filter
   * @return
   */
  public static String encodeFilterValue(final String filter)
  {
    try
    {
      return URLEncoder.encode(filter, "UTF-8");
    }
    catch (UnsupportedEncodingException e)
    {
      log.error("Encoding of parameter failed: " + filter, e);
      return filter;
    }
  }
}
