package de.seitenbau.govdata.search.filter;

import java.io.IOException;

import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.common.geo.ShapeRelation;
import org.elasticsearch.geometry.Rectangle;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import de.seitenbau.govdata.search.util.states.BoundingBoxContainer;

/**
 * data structure to hold a rectangular box, defined by its topleft (1) and bottomright (2) points.
 * @author tscheffler
 *
 */
public class BoundingBox extends BaseFilter
{
  private Double topleft_lat;
  private Double topleft_lon;
  private Double bottomright_lat;
  private Double bottomright_lon;

  private ShapeRelation shapeRelation;

  /**
   * Create a BoundingBox from OpenLayers3 "extent" coordinates.
   * @param bbox
   * @param relation intersect or within
   * @throws NumberFormatException if the coordinates are not well formatted
   * @throws ArrayIndexOutOfBoundsException if there are not exactly 4 parameters
   */
  public BoundingBox(String elasticSearchField, String filterFragmentName, String bbox,
      ShapeRelation relation)
      throws NumberFormatException, ArrayIndexOutOfBoundsException
  {
    
    super(elasticSearchField, filterFragmentName);
    String[] parts = bbox.split(",");
    
    // input: 9.010694329052733,47.62843023858082,9.304647270947264,47.71165977896959
    // don't be confused by the indices, we are given the topright and bottomleft input
    topleft_lon = Double.parseDouble(parts[0]);
    topleft_lat = Double.parseDouble(parts[3]);
    bottomright_lon = Double.parseDouble(parts[2]);
    bottomright_lat = Double.parseDouble(parts[1]);
    shapeRelation = relation;
  }

  /**
   * Create a BoundingBox from OpenLayers3 "extent" coordinates.
   * @param bbox
   * @throws NumberFormatException if the coordinates are not well formatted
   * @throws ArrayIndexOutOfBoundsException if there are not exactly 4 parameters
   */
  public BoundingBox(String elasticSearchField, String filterFragmentName, String bbox)
      throws NumberFormatException, ArrayIndexOutOfBoundsException
  {

    this(elasticSearchField, filterFragmentName, bbox, ShapeRelation.INTERSECTS);
  }

  @Override
  public String toString()
  {
    return topleft_lon + "," + bottomright_lat + "," + bottomright_lon + "," + topleft_lat;
  }

  @Override
  public QueryBuilder createFilter()
  {
    try
    {
      return QueryBuilders
          .geoShapeQuery(getElasticSearchField(),
              new Rectangle(topleft_lon, bottomright_lon, topleft_lat, bottomright_lat))
          .relation(shapeRelation);
    }
    catch (IOException e)
    {
      throw new RuntimeException("Failed to create a boundingbox filter!", e);
    }
  }

  @Override
  public String getLabel()
  {
    return this.toString();
  }

  /**
   * Create a BoundingBox from OpenLayers3 "extent" coordinates.
   * @param bbox
   * @param relation intersect or within
   */
  public BoundingBox(String elasticSearchField, String filterFragmentName, BoundingBoxContainer bbox,
      ShapeRelation relation)
  {
    super(elasticSearchField, filterFragmentName);
    topleft_lon = bbox.getLongitudeMin();
    topleft_lat = bbox.getLatitudeMax();
    bottomright_lon = bbox.getLongitudeMax();
    bottomright_lat = bbox.getLatitudeMin();
    shapeRelation = relation;
  }

  /**
   * Create a BoundingBox from OpenLayers3 "extent" coordinates.
   * @param bbox
   */
  public BoundingBox(String elasticSearchField, String filterFragmentName, BoundingBoxContainer bbox)
  {
    this(elasticSearchField, filterFragmentName, bbox, ShapeRelation.INTERSECTS);
  }

  public GeoPoint getCenter()
  {
    return new GeoPoint((bottomright_lat + topleft_lat) / 2, (bottomright_lon + topleft_lon) / 2);
  }
}
