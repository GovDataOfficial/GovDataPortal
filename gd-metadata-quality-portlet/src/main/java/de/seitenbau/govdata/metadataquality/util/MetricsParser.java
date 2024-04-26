package de.seitenbau.govdata.metadataquality.util;

import static de.seitenbau.govdata.metadataquality.common.MetadataQualityConstants.DATA;
import static de.seitenbau.govdata.metadataquality.common.MetadataQualityConstants.LABELS;
import static de.seitenbau.govdata.metadataquality.common.MetadataQualityConstants.PERCENTAGE;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.seitenbau.govdata.odp.common.cache.BaseCache;
import de.seitenbau.govdata.search.api.model.metrics.dto.MetricsHitDto;

public class MetricsParser extends BaseCache
{
  private static final String PUBLISHERS_CACHE_KEY = "publishers";

  private List<MetricsHitDto> metricData;

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

      for (MetricsHitDto metricsHitDto : metricData)
      {
        if (metricsHitDto.getName().equals(type))
        {
          String publisher = Objects.toString(metricsHitDto.getPublisher());
          Map<String, List<?>> valuesDict = createValuesDict(metricsHitDto);
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
  private Map<String, List<?>> createValuesDict(MetricsHitDto metricsHitDto)
  {

    Map<String, List<?>> valuesDict = new HashMap<>();

    List<String> labelList = metricsHitDto.getLabels();
    valuesDict.put(LABELS, labelList);
    List<Integer> dataList = metricsHitDto.getData();
    valuesDict.put(DATA, dataList);
    List<Double> percentageList = metricsHitDto.getData_percent();
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
      LOG.info("{}Get {} hits.", method, metricData.size());

      for (MetricsHitDto metricsHitDto : metricData)
      {
        // Get the publisher max doc count value
        Long hitValue = Long.parseLong(Objects.toString(metricsHitDto.getTotal_count()));
        String publisherKey = metricsHitDto.getPublisher();
        LOG.debug("{}Process publisher {}", method, publisherKey);
        if (!publishers.containsKey(publisherKey) || publishers.get(publisherKey) < hitValue)
        {
          publishers.put(Objects.toString(publisherKey), hitValue);
        }
      }
      publishersCache = publishers;
      cacheUpdated(PUBLISHERS_CACHE_KEY);
      LOG.info("{}Updated metric publishers cache. Publisher count: {}", method, publishersCache.size());
    }

    LOG.trace(method + "End");
    return new HashMap<>(publishersCache);
  }

  /**
   * Set the data for parsing
   * @param metricData
   */
  public void setData(List<MetricsHitDto> metricData)
  {
    this.metricData = metricData;
  }
}
