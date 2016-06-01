package de.seitenbau.govdata.search.filter;

import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.common.geo.ShapeRelation;
import org.elasticsearch.common.geo.builders.ShapeBuilder;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;

/**
 * data structure to hold a rectangular box, defined by its topleft (1) and bottomright (2) points.
 * @author tscheffler
 *
 */
public class BoundingBox extends BaseFilter {
  private Double topleft_lat;
  private Double topleft_lon;
  private Double bottomright_lat;
  private Double bottomright_lon;
  
  /**
   * Create a BoundingBox from OpenLayers3 "extent" coordinates.
   * @param bbox
   * @throws NumberFormatException if the coordinates are not well formatted
   * @throws ArrayIndexOutOfBoundsException if there are not exactly 4 parameters
   */
  public BoundingBox(String elasticSearchField, String filterFragmentName, String bbox)
      throws NumberFormatException, ArrayIndexOutOfBoundsException {
    
    super(elasticSearchField, filterFragmentName);
    String[] parts = bbox.split(",");
    
    // input: 9.010694329052733,47.62843023858082,9.304647270947264,47.71165977896959
    // don't be confused by the indices, we are given the topright and bottomleft input
    topleft_lon = Double.parseDouble(parts[0]);
    topleft_lat = Double.parseDouble(parts[3]);
    bottomright_lon = Double.parseDouble(parts[2]);
    bottomright_lat = Double.parseDouble(parts[1]);
  }
  
  public String toString() {
    return topleft_lon + "," + bottomright_lat + "," + bottomright_lon + "," + topleft_lat;
  }

  @Override
  public FilterBuilder createFilter()
  {
    return FilterBuilders.geoShapeFilter(elasticSearchField, // special field in EC-index containing a Geo Shape Type 
        ShapeBuilder.newEnvelope()
          .topLeft(topleft_lon, topleft_lat)
          .bottomRight(bottomright_lon, bottomright_lat),
          ShapeRelation.INTERSECTS);
  }

  @Override
  public String getLabel()
  {
    return this.toString();
  }

  public GeoPoint getCenter()
  {
    return new GeoPoint((bottomright_lat + topleft_lat) / 2, (bottomright_lon + topleft_lon) / 2);
  }
}
