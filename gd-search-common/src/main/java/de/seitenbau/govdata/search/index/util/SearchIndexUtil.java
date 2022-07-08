package de.seitenbau.govdata.search.index.util;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.locationtech.jts.io.geojson.GeoJsonReader;
import org.locationtech.jts.io.geojson.GeoJsonWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import de.seitenbau.govdata.common.messaging.Document;
import de.seitenbau.govdata.common.messaging.SearchIndexEntry;
import de.seitenbau.govdata.common.messaging.Section;
import de.seitenbau.govdata.date.DateUtil;
import de.seitenbau.govdata.db.api.model.Showcase;
import de.seitenbau.govdata.db.api.model.ShowcaseImage;
import de.seitenbau.govdata.db.api.model.ShowcaseType;
import de.seitenbau.govdata.odp.common.filter.SearchConsts;
import de.seitenbau.govdata.odp.common.util.GovDataCollectionUtils;
import de.seitenbau.govdata.odp.common.util.ImageUtil;
import de.seitenbau.govdata.search.index.IndexConstants;
import lombok.extern.slf4j.Slf4j;

/**
 * The class provides some methods for adding data to the search index.
 * 
 * @author rnoerenberg
 */
@Slf4j
@Component
public class SearchIndexUtil
{
  private String indexNameShowcases;

  /**
   * Builds a {@link SearchIndexEntry} object from the data of the given {@link Showcase}.
   * 
   * @param showcase the object to save
   * @return
   */
  public SearchIndexEntry buildSearchIndexEntryFromShowcase(Showcase showcase)
  {
    SearchIndexEntry entry = createSearchIndexEntryWithBasicInformationShowcase(showcase.getId());
    Document msgDocument = entry.getDocument();

    // Map document fields. Most are mapped to metadata, but some may be set on specific fields
    Map<String, Object> metadata = new HashMap<String, Object>();

    msgDocument.setTitle(showcase.getTitle());
    msgDocument.setPreamble(showcase.getNotes());

    // Primary showcase type
    Optional<ShowcaseType> primaryShowcaseType =
        GovDataCollectionUtils.collectionToStream(showcase.getShowcaseTypes())
            .filter(t -> t.isPrimaryShowcase()).findFirst();
    if (primaryShowcaseType.isPresent())
    {
      metadata.put(IndexConstants.METADATA_FIELD_PRIMARY_SHOWCASE_TYPE, primaryShowcaseType.get().getName());
    }
    // Private
    metadata.put(IndexConstants.METADATA_FIELD_PRIVATE, showcase.isHidden());

    metadata.put(IndexConstants.METADATA_FIELD_SHOWCASE_CREATED,
        DateUtil.formatDateUTC(showcase.getCreateDate()));
    String technicalModifiedDate = DateUtil.formatDateUTC(showcase.getModifyDate());
    metadata.put(IndexConstants.METADATA_FIELD_SHOWCASE_MODIFIED, technicalModifiedDate);
    String modifiedDateWithFallback = technicalModifiedDate;
    if (Objects.nonNull(showcase.getManualShowcaseCreatedDate()))
    {
      metadata.put(IndexConstants.METADATA_FIELD_ISSUED,
          DateUtil.formatDateUTC(showcase.getManualShowcaseCreatedDate()));
    }
    if (Objects.nonNull(showcase.getManualShowcaseModifiedDate()))
    {
      String manualModifiedDate = DateUtil.formatDateUTC(showcase.getManualShowcaseModifiedDate());
      metadata.put(IndexConstants.METADATA_FIELD_MODIFIED, manualModifiedDate);
      modifiedDateWithFallback = manualModifiedDate;
    }
    metadata.put(IndexConstants.METADATA_FIELD_MODIFIED_FALLBACK_SHOWCASE, modifiedDateWithFallback);

    metadata.put(IndexConstants.METADATA_FIELD_TYPE, IndexConstants.INDEX_TYPE_SHOWCASE);
    // Tags
    String[] tagList = GovDataCollectionUtils.collectionToStream(showcase.getKeywords()).map(o -> o.getName())
        .toArray(String[]::new);
    msgDocument.setTags(tagList);
    // Categories aka groups
    if (CollectionUtils.isNotEmpty(showcase.getCategories()))
    {
      String[] categories = showcase.getCategories().stream().map(o -> o.getName()).toArray(String[]::new);
      metadata.put(IndexConstants.METADATA_FIELD_GROUPS, categories);
    }
    // Platforms
    if (CollectionUtils.isNotEmpty(showcase.getPlatforms()))
    {
      String[] platforms = showcase.getPlatforms().stream().map(o -> o.getName()).toArray(String[]::new);
      metadata.put(IndexConstants.METADATA_FIELD_PLATFORMS, platforms);
    }
    // Used datasets
    if (CollectionUtils.isNotEmpty(showcase.getUsedDatasets()))
    {
      String[] datasets = showcase.getUsedDatasets().stream().map(o -> o.getName()).toArray(String[]::new);
      metadata.put(IndexConstants.METADATA_FIELD_DATASETS, datasets);
    }
    // Additional showcase types
    String[] additionalTypes =
        GovDataCollectionUtils.collectionToStream(showcase.getShowcaseTypes())
            .filter(t -> !t.isPrimaryShowcase()).map(o -> o.getName())
            .toArray(String[]::new);
    if (ArrayUtils.isNotEmpty(additionalTypes))
    {
      metadata.put(IndexConstants.METADATA_FIELD_SHOWCASE_TYPES, additionalTypes);
    }

    // Image -> base64 thumbnail
    Optional<ShowcaseImage> firstImage =
        GovDataCollectionUtils.collectionToStream(showcase.getImages())
            .sorted(Comparator.comparingInt(i -> i.getImageOrderId())).findFirst();
    if (firstImage.isPresent())
    {
      metadata.put(IndexConstants.METADATA_FIELD_IMAGE,
          ImageUtil.convertByteArrayToBase64StringThumbnail(firstImage.get().getImage()));
    }

    // Add spatial field, transform to "boundingbox", "spatial_area", "spatial_center"
    String spatial = StringUtils.trim(showcase.getSpatial());
    processSpatial(metadata, spatial);

    // map metadata object to JSON-String
    msgDocument.setMetadata(new Gson().toJson(metadata));
    entry.setDocument(msgDocument);

    return entry;
  }

  /**
   * Creates a {@link SearchIndexEntry} with basic information for a {@link Showcase} entity.
   * 
   * @param showcaseId the ID of the showcase
   * @return
   */
  public SearchIndexEntry createSearchIndexEntryWithBasicInformationShowcase(Long showcaseId)
  {
    SearchIndexEntry entry = createSearchIndexEntryWithBasicInformation(Long.toString(showcaseId),
        indexNameShowcases, IndexConstants.INDEX_TYPE_SHOWCASE);

    return entry;
  }

  /**
   * Creates a {@link SearchIndexEntry} with basic information for a {@link Showcase} entity.
   * 
   * @param id the ID of the object in the search index
   * @param indexName the index name
   * @param indexType the index type
   * @return
   */
  public SearchIndexEntry createSearchIndexEntryWithBasicInformation(String id, String indexName,
      String indexType)
  {
    Document msgDocument = new Document();
    // set basic document information
    msgDocument.setId(id);
    msgDocument.setMandant(IndexConstants.INDEX_MANDANT);
    // required, otherwise service call will fail
    msgDocument.setTags(new String[0]);
    msgDocument.setSections(new ArrayList<Section>());

    SearchIndexEntry entry = SearchIndexEntry.builder()
        .indexName(indexName).document(msgDocument)
        .build();
    return entry;
  }

  private void processSpatial(Map<String, Object> metadata, String spatial)
  {
    if (StringUtils.isNotEmpty(spatial))
    {
      metadata.put(IndexConstants.METADATA_FIELD_SPATIAL, spatial);
      // read a geometry from a GeoJson or WKT string (using the default geometry factory)
      Geometry geometry = getGeometryFromSpatial(spatial);
      if (Objects.nonNull(geometry) && geometry.isValid())
      {
        // Calculates the envelope (bounding box) of this Geometry.
        GeoJsonWriter geoJsonWriter = new GeoJsonWriter();
        geoJsonWriter.setEncodeCRS(false);
        String geojson = geoJsonWriter.write(geometry.getEnvelope());
        // The elasticsearch type "geo_shape" needs an object and not a string
        Type shapeMapType = new TypeToken<Map<String, Object>>() {}.getType();
        Map<String, Object> bboxMap = new Gson().fromJson(geojson, shapeMapType);
        metadata.put(IndexConstants.METADATA_FIELD_BOUNDING_BOX, bboxMap);
        // Calculates the center point of the given Polygon and returns the coordinates
        Point center = geometry.getCentroid();
        Map<String, Double> geoPoint = new HashMap<>();
        geoPoint.put("lat", center.getY()); // y
        geoPoint.put("lon", center.getX()); // x
        metadata.put(IndexConstants.METADATA_FIELD_SPATIAL_CENTER, geoPoint);
        // Calculates the area of the spatial feature
        metadata.put(IndexConstants.METADATA_FIELD_SPATIAL_AREA, geometry.getArea());
      }
    }
  }

  private Geometry getGeometryFromSpatial(String spatial)
  {
    Geometry geometry = null;
    try
    {
      geometry = new GeoJsonReader().read(spatial);
      if (Objects.isNull(geometry))
      {
        geometry = new WKTReader().read(spatial);
      }
    }
    catch (ParseException e)
    {
      log.warn("Could not parse the value '{}' in field 'spatial' as geometry.", spatial);
    }
    return geometry;
  }

  @Value(SearchConsts.CONFIG_ELASTICSEARCH_SHOWCASES_INDEX_NAME)
  public void setIndexNameShowcases(String indexNameShowcases)
  {
    this.indexNameShowcases = indexNameShowcases;
  }
}
