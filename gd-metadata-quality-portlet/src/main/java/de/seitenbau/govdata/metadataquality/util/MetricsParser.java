package de.seitenbau.govdata.metadataquality.util;

import static de.seitenbau.govdata.metadataquality.common.MetadataQualityConstants.DATA;
import static de.seitenbau.govdata.metadataquality.common.MetadataQualityConstants.LABELS;
import static de.seitenbau.govdata.metadataquality.common.MetadataQualityConstants.PERCENTAGE;
import static de.seitenbau.govdata.metadataquality.common.MetadataQualityConstants.PUBLISHER;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.seitenbau.govdata.odp.common.cache.BaseCache;

public class MetricsParser extends BaseCache
{
  private static final String PUBLISHERS_CACHE_KEY = "publishers";

  private SearchHits metricData;

  private Map<String, Map<String, Map<String, List<?>>>> valuesByTypeCache = new HashMap<>();

  private Map<String, Long> publishersCache;

  private static final Logger LOG = LoggerFactory.getLogger(MetricsParser.class);

  /**
   * Default constructor.
   */
  public MetricsParser()
  {
    setMaxCacheTimeAmount(1);
  }

  /**
   * Gets the values for the given type.
   * 
   * @param type
   * @return
   */
  public Map<String, Map<String, List<?>>> getValuesForType(String type)
  {
    final String method = "getValuesForType() : ";
    LOG.trace(method + "Start");

    Map<String, Map<String, List<?>>> valuesPerPublisherDict = new HashMap<>();
    if (valuesByTypeCache.get(type) == null || isCacheExpired(type))
    {
      LOG.info("{}Empty or expired metric values cache for type '{}', refreshing data.", method, type);
      SearchHit[] hitArray = this.metricData.getHits();

      for (SearchHit searchHit : hitArray)
      {
        Map<String, Object> hit = searchHit.getSourceAsMap();

        if (hit.get("name").equals(type))
        {
          String publisher = Objects.toString(hit.get(PUBLISHER));
          Map<String, List<?>> valuesDict = createValuesDict(hit);
          if (valuesPerPublisherDict.containsKey(publisher))
          {
            LOG.warn("The metrics map already contains a key with the value {}. Overriding existent values!",
                publisher);
          }
          valuesPerPublisherDict.put(publisher, valuesDict);
        }
      }
      valuesByTypeCache.put(type, valuesPerPublisherDict);
      cacheUpdated(type);
    }

    LOG.trace(method + "End");
    return valuesByTypeCache.get(type);
  }

  @SuppressWarnings("unchecked")
  private Map<String, List<?>> createValuesDict(Map<String, Object> hit)
  {

    Map<String, List<?>> valuesDict = new HashMap<>();

    List<String> labelList = (List<String>) hit.get(LABELS);
    valuesDict.put(LABELS, labelList);
    List<Integer> dataList = (List<Integer>) hit.get(DATA);
    valuesDict.put(DATA, dataList);
    List<Double> percentageList = (List<Double>) hit.get(PERCENTAGE);
    valuesDict.put(PERCENTAGE, percentageList);

    return valuesDict;
  }

  /**
   * Get a list with all publishers that appear in metricData
   * @return
   */
  public Map<String, Long> getAvailablePublishers()
  {
    final String method = "getAvailablePublishers() : ";
    LOG.trace(method + "Start");

    Map<String, Long> publishers = new HashMap<>();
    if (publishersCache == null || isCacheExpired(PUBLISHERS_CACHE_KEY))
    {
      LOG.info("{}Empty or expired metric publishers cache, refreshing data.", method);
      SearchHit[] hitArray = this.metricData.getHits();

      for (SearchHit searchHit : hitArray)
      {
        Map<String, Object> hit = searchHit.getSourceAsMap();
        // Get the publisher max doc count value
        Long hitValue = Long.parseLong(Objects.toString(hit.getOrDefault("total_count", 0)));
        if (!publishers.containsKey(hit.get(PUBLISHER)) || publishers.get(hit.get(PUBLISHER)) < hitValue)
        {
          publishers.put(Objects.toString(hit.get(PUBLISHER)), hitValue);
        }
      }
      publishersCache = publishers;
      cacheUpdated(PUBLISHERS_CACHE_KEY);
    }

    LOG.trace(method + "End");
    return publishersCache;
  }

  /**
   * Set the data for parsing
   * @param metricData
   */
  public void setData(SearchHits metricData)
  {
    this.metricData = metricData;
  }
}
