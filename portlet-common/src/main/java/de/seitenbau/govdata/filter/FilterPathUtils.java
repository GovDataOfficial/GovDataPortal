package de.seitenbau.govdata.filter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FilterPathUtils {
   private final static String groupDelimiter = ",";
   private final static String keyValDelimiter = ":";
   
  /**
   * Decodes path fragment (like key:val,key:val
   * @param pathFragment serialized filterstring
   * @return Map of active filternames grouped by filtertype
   */
  public static Map<String, List<String>> deserializeFilter(String pathFragment) {
    // TODO validate pathFragment syntax via regex
    
    Map<String, List<String>> activeFilterMap = new HashMap<String, List<String>>();
    
    try {
      if(pathFragment != null) {
        String[] groups= pathFragment.split(groupDelimiter);
        for(String group : groups) {
          int splitIndex = group.indexOf(keyValDelimiter);
          String key = group.substring(0, splitIndex);
          String val = group.substring(splitIndex+1);
          
          // Add new List if not exist
          List<String> filterList;
          if(activeFilterMap.containsKey(key)) {
            filterList = activeFilterMap.get(key);
          } else {
            filterList = new ArrayList<String>();
            activeFilterMap.put(key, filterList);
          }
          
          filterList.add(val);
        }
      }
    } catch (Exception e) {
      log.debug("Error at processing filters: " + e.getMessage());
      return new HashMap<String, List<String>>(); // don't process filters if the string is invalid
    }
    
    return activeFilterMap;
  }
  
  public static String serializeFilterMap(Map<String, List<String>> activeFilterMap) {
    StringBuilder sb = new StringBuilder();
    for(Entry<String, List<String>> entry : activeFilterMap.entrySet()) {
      for(String val : entry.getValue()) {
        sb.append(serializeFilter(entry.getKey(), val));
      }
    }
    
    // has a trailing ; - but that's intended!
    return sb.toString();
  }
  
  public static String serializeFilter(String key, String val) {
    return key + keyValDelimiter + val + groupDelimiter;
  }
  
  public static String serializeFilterMatchAllRegex(String key) {
    return key + keyValDelimiter + ".+?" + groupDelimiter;
  }
}
