package de.seitenbau.govdata.redis.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import de.seitenbau.govdata.redis.adapter.RedisClientAdapter;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;

/**
 * Tests für die Klasse {@link RedisReportUtil}.
 * 
 * @author rnoerenberg
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class RedisReportUtilTest
{
  private String reportPythonDictString;

  @Mock
  private RedisClientAdapter redisClientAdapterMock;

  @Mock
  private StatefulRedisConnection<String, String> redisConnection;

  @Mock
  private RedisCommands<String, String> redisCommands;

  @Before
  public void setup() throws Exception
  {
    reportPythonDictString = readFileFromClasspathToString("python-dict-string.txt");
  }

  /**
   * Test für das korrekte Parsen eines Report-Eintrags zu einem Metadatensatz im Redis.
   */
  @Test
  public void parseReportPythonDictStringToJson() throws Exception
  {
    /* prepare */

    /* execute */
    String result = RedisReportUtil.parseReportPythonDictStringToJson(reportPythonDictString);

    /* verify */
    String expected = readFileFromClasspathToString("python-dict-string-parsing-expected.txt");
    Assertions.assertThat(result).isEqualTo(expected);
  }

  /**
   * Test für das korrekte Parsen eines Report-Eintrags zu einem Metadatensatz im Redis.
   */
  @Test
  public void parseReportPythonDictStringToJson_type_none() throws Exception
  {
    /* prepare */
    String input = readFileFromClasspathToString("python-dict-string-type-none.txt");

    /* execute */
    String result = RedisReportUtil.parseReportPythonDictStringToJson(input);

    /* verify */
    String expected = readFileFromClasspathToString("python-dict-string-type-none-expected.txt");
    Assertions.assertThat(result).isEqualTo(expected);
  }

  /**
   * Test für das korrekte Einlesen der nicht erreichbaren Verweise.
   * <ul>
   * <li>GOVDATA-1098: Die hexadezimale Darstellung von Umlauten, z.B f\xfcr (für) möchte der
   * JSON-Parser standardmäßig nicht haben.</li>
   * </ul>
   */
  @Test
  public void readUnavailableResourceLinks() throws Exception
  {
    /* prepare */
    String metadataId = "100";
    Mockito.when(redisClientAdapterMock.getConnection()).thenReturn(redisConnection);
    Mockito.when(redisConnection.sync()).thenReturn(redisCommands);
    Mockito.when(redisCommands.get(metadataId)).thenReturn(reportPythonDictString);

    /* execute */
    Set<String> result = RedisReportUtil.readUnavailableResourceLinks(metadataId, redisClientAdapterMock);

    /* verify */
    Assertions
        .assertThat(result)
        .containsOnly(
            "ftp://www.geoportal.rlp.de/mapbender/php/mod_showMetadata.php?languageCode=de&resource=layer"
                + "&layout=tabs&id=35236",
            "ftps://www.geoportal.rlp.de:80/portal/karten.html?LAYER[zoom]=1&LAYER[id]=35236",
            "ftp://www.geoportal.rlp.de:80/portal/karten.html?LAYER[zoom]=1&LAYER[id]=35236",
            "ftps://www.geoportal.rlp.de/mapbender/php/wms.php?layer_id=35236&REQUEST=GetCapabilities&"
                + "VERSION=1.1.1&SERVICE=WMS");
  }

  private String readFileFromClasspathToString(String fileName) throws IOException
  {
    return IOUtils.toString(getClass().getResourceAsStream(fileName), StandardCharsets.UTF_8);
  }

}
