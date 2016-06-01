package de.seitenbau.govdata.edit.validator;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang3.StringUtils;
import org.geojson.GeoJsonObject;
import org.geojson.LngLatAlt;
import org.geojson.Polygon;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GeoJSONPolygonValidator implements ConstraintValidator<GeoJSONPolygon, String>
{
  @Override
  public void initialize(GeoJSONPolygon constraintAnnotation)
  {
  }

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context)
  {
    // if we have nothing to validate, that's okay.
    if(StringUtils.isEmpty(value)) {
      return true;
    }
    
    try
    {
      // because "Polygon" will be valid, but "polygon" is accepted by OGD SChema
      value = value.replace("polygon", "Polygon").trim();
      
      // read GeoJSON and detect trailing garbage
      ObjectMapper mapper = new ObjectMapper();
      JsonParser jp = mapper.getFactory().createParser(value);
      
      GeoJsonObject object = mapper.readValue(jp, GeoJsonObject.class);
      if(jp.nextToken() != null) {
        throw new IOException("there was trailing garbage");
      }
      
      if (object instanceof Polygon) { // we only accept polygons (OGD-Schema)
        // check for additional characters at the end of the proper GeoJSON-Definition
        // (jackson will not complain, will just not map them and accept the payload anyways)
        
        
        // check if any internal ring has at most 1 point shared with the outer polygon
        // (ElasticSearch requirement)
        Polygon geometry = (Polygon) object;
        Set<LngLatAlt> exteriorCoordinates = new HashSet<LngLatAlt>(geometry.getExteriorRing());
        
        for(List<LngLatAlt> interiorRing : geometry.getInteriorRings()) {
          int sharedCoordinates = 0;
          for(LngLatAlt interiorCoordinate : interiorRing) {
            if(exteriorCoordinates.contains(interiorCoordinate) && ++sharedCoordinates == 2) {
              return false; // more than 1 common coordinate
            };
          }
        }
        
        return true;
      }
      return false;
    }
    catch (IOException e)
    {
      // probably syntax or structure error in GeoJSON, in any case: not valid
      return false;
    }
  }

}
