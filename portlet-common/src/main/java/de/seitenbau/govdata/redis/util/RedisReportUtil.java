package de.seitenbau.govdata.redis.util;

import java.io.IOException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.seitenbau.govdata.exception.RedisNotAvailableException;
import de.seitenbau.govdata.redis.adapter.RedisClientAdapter;
import de.seitenbau.govdata.redis.bean.MetadataValidatonReportBean;
import io.lettuce.core.RedisException;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import lombok.extern.slf4j.Slf4j;

/**
 * Eine Hilfsklasse für den Zugriff auf die Redis-Datenbank.
 * 
 * @author rnoerenberg
 *
 */
@Slf4j
public abstract class RedisReportUtil
{
  /**
   * Liest die nicht erreichbaren Verweise zu dem Metadatensatz mit der übergebenen ID aus der
   * Redis-Datenbank für die Reports aus.
   * 
   * @param metadataId die ID des Metadatensatzes zu dem die nicht erreichbaren Verweise ausgelesen
   *        werden sollen.
   * @param redisClientAdapter der Redis-Client-Adapter, der zum Auslesen der Daten genutzt wird.
   * @return eine Liste an nicht erreichbaren URLs oder eine leere Liste, falls keine nicht
   *         erreichbaren Verweise existieren.
   */
  public static final Set<String> readUnavailableResourceLinks(
      final String metadataId, final RedisClientAdapter redisClientAdapter)
  {
    final String method = "readUnavailableResourceLinks() : ";
    log.trace(method + "Start");

    Set<String> result = new HashSet<String>();

    StatefulRedisConnection<String, String> redisConnection = null;
    try
    {
      redisConnection = redisClientAdapter.getConnection();
      RedisCommands<String, String> commands = redisConnection.sync();
      String datasetRecord = commands.get(metadataId);
      if (Objects.nonNull(datasetRecord))
      {
        log.debug("raw redis entry: " + datasetRecord);
        datasetRecord = parseReportPythonDictStringToJson(datasetRecord);
        log.debug("processed redis entry: " + datasetRecord);
        ObjectMapper om = new ObjectMapper();
        om.configure(Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER, true);
        try
        {
          MetadataValidatonReportBean reportBean =
              om.readValue(datasetRecord, MetadataValidatonReportBean.class);
          if (reportBean.getUrls() != null)
          {
            result = reportBean.getUrls().keySet();
          }
        }
        catch (IOException e)
        {
          log.warn(method + e.getMessage());
        }
      }
    }
    catch (RedisNotAvailableException e)
    {
      log.warn(method + e.getMessage());
    }
    catch (RedisException e)
    {
      log.warn(method + e.getMessage());
    }
    log.trace(method + "End");
    return result;
  }

  /**
   * Parst einen als Python Dictionary String vorhandenen Report-Eintrag zu einem JSON validen
   * String.
   * 
   * @param reportPythonDictString der Report-Eintrag als Python Dictionary String.
   * @return der geparste JSON-String.
   */
  static final String parseReportPythonDictStringToJson(final String reportPythonDictString)
  {
    String result = reportPythonDictString;
    // replace python unicode character "u"
    result = StringUtils.replace(result, "u'", "'");
    // replace single quotes with double quotes, where needed
    result =
        StringUtils.replaceEachRepeatedly(
            result,
            new String[] {"{'", "'}", ", ['", "[['", ", '", "', \"", "':", ": '"},
            new String[] {"{\"", "\"}", ", [\"", "[[\"", ", \"", "\", \"", "\":", ": \""});
    // replace "None" with "null"
    result = StringUtils.replace(result, ": None", ": null");
    // convert python unicode hex values to characters
    try
    {
      result = StringUtils.replace(result, "\\x", "\\u00");
      result = StringEscapeUtils.unescapeJava(result);
    }
    catch (Exception e)
    {
      log.warn("Error while unescaping: {}", e.getMessage());
    }
    return result;
  }
}
