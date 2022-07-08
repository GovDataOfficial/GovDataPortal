package de.seitenbau.govdata.search.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.WKTReader;
import org.locationtech.jts.io.geojson.GeoJsonReader;

import de.seitenbau.govdata.search.util.states.BoundingBoxContainer;
import de.seitenbau.govdata.search.util.states.StateContainer;
import lombok.extern.slf4j.Slf4j;

/**
 * Parses the state list source files into objects.
 * 
 * @author rnoerenberg
 *
 */
@Slf4j
public class GeoStateParser
{
  private static final String PATH_TO_STATES_FILE = "data/bundeslaender.geojson";

  private static final String PATH_TO_PROPERTIES_FILE = "data/statesearchcriteria.properties";

  private JSONObject geoJsonObject = null;

  private List<StateContainer> stateList = new ArrayList<>();

  /**
   * Constructor: Read in file
   */
  public GeoStateParser()
  {
    JSONParser jsonParser = new JSONParser();
    InputStreamReader reader = null;
    try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(PATH_TO_STATES_FILE))
    {
      reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
      geoJsonObject = (JSONObject) jsonParser.parse(reader);
      reader.close();
    }
    catch (IOException | ParseException e)
    {
      log.warn("Problem reading in geojson file: " + e.getMessage());
    }
    finally
    {
      IOUtils.closeQuietly(reader);
    }

    Properties properties = new Properties();
    try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(PATH_TO_PROPERTIES_FILE))
    {
      reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
      properties.load(reader);
    }
    catch (IOException e)
    {
      log.warn("Problem reading in state-properties file: " + e.getMessage());
    }
    finally
    {
      IOUtils.closeQuietly(reader);
    }

    if (geoJsonObject != null)
    {
      JSONArray stateJsonArray = (JSONArray) geoJsonObject.get("features");

      for (int i = 0; i < stateJsonArray.size(); i++)
      {
        JSONObject stateJsonObject = (JSONObject) stateJsonArray.get(i);
        JSONObject stateJsonProperties = (JSONObject) stateJsonObject.get("properties");
        JSONObject stateJsonGeometry = (JSONObject) stateJsonObject.get("geometry");
        StateContainer state = new StateContainer();
        String stateName = stateJsonProperties.get("name").toString();
        state.setId(stateJsonObject.get("id").toString());
        state.setName(stateName);
        String keyPrefix = stateName.toLowerCase();
        state.setDisplayId(Integer.parseInt(properties.getProperty(keyPrefix + ".display.id")));
        state.setDisplayName(properties.getProperty(keyPrefix + ".display.name").replace("-", "-\n"));
        state.setBoundingBox(getBoundingBox(stateJsonGeometry));
        state.setFilterTitle(readListFromProperties(properties, keyPrefix + ".title"));
        state.setFilterTags(readListFromProperties(properties, keyPrefix + ".tags"));
        state.setFilterDescription(readListFromProperties(properties, keyPrefix + ".description"));
        state.setFilterSpatial(Boolean.parseBoolean(properties.getProperty(keyPrefix + ".spatial")));
        state.setFilterGeocodingUri(readListFromProperties(properties, keyPrefix + ".geocoding.uri"));
        state.setFilterGeocodingLevelUri(
            readListFromProperties(properties, keyPrefix + ".geocoding.level.uri"));
        state.setFilterGeocodingDescription(
            readListFromProperties(properties, keyPrefix + ".geocoding.description"));
        state.setFilterContributorIds(readListFromProperties(properties, keyPrefix + ".contributor.id"));
        stateList.add(state);
      }
    }
  }

  /**
   * Getter for raw json
   * @return
   */
  public JSONObject getGeoJson()
  {
    return geoJsonObject;
  }

  /**
   * Getter for StateList
   */
  public List<StateContainer> getStateList()
  {
    return stateList;
  }

  /**
   * Calculate the boundingbox for the given geometry
   * @param geometryObject
   * @return BoundingBoxContainer
   */
  private BoundingBoxContainer getBoundingBox(JSONObject geometryObject)
  {
    Geometry geometry = null;
    try
    {
      geometry = new GeoJsonReader().read(geometryObject.toJSONString());
      if (Objects.isNull(geometry))
      {
        geometry = new WKTReader().read(geometryObject.toJSONString());
      }
    }
    catch (org.locationtech.jts.io.ParseException e)
    {
      log.warn("ERROR: Cannot parse GeoJson: " + e.getMessage());
    }
    if (Objects.nonNull(geometry) && geometry.isValid())
    {
      Envelope envelope = geometry.getEnvelopeInternal();
      return BoundingBoxContainer.builder().latitudeMax(envelope.getMaxY()).latitudeMin(envelope.getMinY())
          .longitudeMax(envelope.getMaxX()).longitudeMin(envelope.getMinX()).build();
    }
    return null;
  }

  /**
   * Read, format and trim comma separated values from properties file.
   * @param properties
   * @param propertyName
   * @return
   */
  private List<String> readListFromProperties(Properties properties, String propertyName)
  {
    String[] list =
        StringUtils.stripAll(StringUtils.splitByWholeSeparator(properties.getProperty(propertyName), ","));
    return Stream.of(list).collect(Collectors.toList());
  }

}