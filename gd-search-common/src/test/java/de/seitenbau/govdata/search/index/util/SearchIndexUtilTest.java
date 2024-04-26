package de.seitenbau.govdata.search.index.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.locationtech.jts.io.geojson.GeoJsonReader;
import org.locationtech.jts.io.geojson.GeoJsonWriter;

import com.google.gson.Gson;

import de.seitenbau.govdata.common.json.DateUtil;
import de.seitenbau.govdata.common.messaging.SearchIndexEntry;
import de.seitenbau.govdata.db.api.model.Showcase;
import de.seitenbau.govdata.db.api.model.ShowcaseImage;
import de.seitenbau.govdata.odp.common.util.ImageUtil;
import de.seitenbau.govdata.testing.db.api.testdata.TestDataFactory;

public class SearchIndexUtilTest
{
  private static final String INDEX_NAME_SHOWCASE = "govdata-showcases-de";

  private static final String INDEX_TYPE_SHOWCASE = "showcase";

  private TestDataFactory testDataFactory = new TestDataFactory();

  private SearchIndexUtil sut;

  @Before
  public void setup()
  {
    sut = new SearchIndexUtil();
    sut.setIndexNameShowcases(INDEX_NAME_SHOWCASE);
  }

  @Test
  public void createSearchIndexEntryWithBasicInformation_String()
  {
    /* prepare */
    String id = "id1";
    String indexName = "index1";
    String indexType = "type1";

    /* execute */
    SearchIndexEntry result = sut.createSearchIndexEntryWithBasicInformation(id, indexName, indexType);

    /* assert */
    Assertions.assertThat(result).isNotNull();
    Assertions.assertThat(result.getIndexName()).isEqualTo(indexName);
    Assertions.assertThat(result.getType()).isNull();
    Assertions.assertThat(result.getDocument()).isNotNull();
    Assertions.assertThat(result.getDocument().getId()).isEqualTo(id);
    Assertions.assertThat(result.getDocument().getSections()).isEmpty();
    Assertions.assertThat(result.getDocument().getMandant()).isEqualTo("1");
    Assertions.assertThat(result.getDocument().getTitle()).isNull();
    Assertions.assertThat(result.getDocument().getPreamble()).isNull();
    Assertions.assertThat(result.getDocument().getTags()).isEmpty();
    Assertions.assertThat(result.getDocument().getMetadata()).isNull();
  }

  @Test
  public void createSearchIndexEntryWithBasicInformationShowcase()
  {
    /* prepare */
    long id = 2L;

    /* execute */
    SearchIndexEntry result = sut.createSearchIndexEntryWithBasicInformationShowcase(id);

    /* assert */
    Assertions.assertThat(result).isNotNull();
    Assertions.assertThat(result.getIndexName()).isEqualTo(INDEX_NAME_SHOWCASE);
    Assertions.assertThat(result.getType()).isNull();
    Assertions.assertThat(result.getDocument()).isNotNull();
    Assertions.assertThat(result.getDocument().getId()).isEqualTo(Long.toString(id));
    Assertions.assertThat(result.getDocument().getSections()).isEmpty();
    Assertions.assertThat(result.getDocument().getMandant()).isEqualTo("1");
    Assertions.assertThat(result.getDocument().getTitle()).isNull();
    Assertions.assertThat(result.getDocument().getPreamble()).isNull();
    Assertions.assertThat(result.getDocument().getTags()).isEmpty();
    Assertions.assertThat(result.getDocument().getMetadata()).isNull();
  }

  @Test
  public void buildSearchIndexEntryFromShowcase_min()
  {
    /* prepare */
    Showcase showcase = new Showcase();
    boolean hidden = showcase.isHidden();
    long id = 11L;
    showcase.setId(id);

    /* execute */
    SearchIndexEntry result = sut.buildSearchIndexEntryFromShowcase(showcase);

    /* assert */
    Assertions.assertThat(result).isNotNull();
    Assertions.assertThat(result.getIndexName()).isEqualTo(INDEX_NAME_SHOWCASE);
    Assertions.assertThat(result.getType()).isNull();
    Assertions.assertThat(result.getDocument()).isNotNull();
    Assertions.assertThat(result.getDocument().getId()).isEqualTo(Long.toString(id));
    Assertions.assertThat(result.getDocument().getSections()).isEmpty();
    Assertions.assertThat(result.getDocument().getMandant()).isEqualTo("1");
    Assertions.assertThat(result.getDocument().getTitle()).isNull();
    Assertions.assertThat(result.getDocument().getPreamble()).isNull();
    Assertions.assertThat(result.getDocument().getTags()).isEmpty();
    Assertions.assertThat(result.getDocument().getMetadata()).isNotNull();
    Assertions.assertThat(result.getDocument().getMetadata())
        .isEqualTo("{\"private\":" + hidden + ",\"type\":\"" + INDEX_TYPE_SHOWCASE + "\"}");
  }

  @Test
  public void buildSearchIndexEntryFromShowcase_sort_date_withoutFallback()
  {
    /* prepare */
    Showcase showcase = new Showcase();
    boolean hidden = showcase.isHidden();
    long id = 11L;
    showcase.setId(id);
    Date modifyDate = testDataFactory.createDate(LocalDateTime.of(2021, Month.MAY, 31, 10, 8, 54));
    showcase.setModifyDate(modifyDate);
    Date dateModifiedExpected = new Date(modifyDate.getTime());
    Date modifiedDate = testDataFactory.createDate(LocalDate.of(2021, Month.MAY, 25));
    showcase.setManualShowcaseModifiedDate(modifiedDate);
    Date dateExpected = new Date(modifiedDate.getTime());

    /* execute */
    SearchIndexEntry result = sut.buildSearchIndexEntryFromShowcase(showcase);

    /* assert */
    Assertions.assertThat(result).isNotNull();
    Assertions.assertThat(result.getIndexName()).isEqualTo(INDEX_NAME_SHOWCASE);
    Assertions.assertThat(result.getType()).isNull();
    Assertions.assertThat(result.getDocument()).isNotNull();
    Assertions.assertThat(result.getDocument().getId()).isEqualTo(Long.toString(id));
    Assertions.assertThat(result.getDocument().getSections()).isEmpty();
    Assertions.assertThat(result.getDocument().getMandant()).isEqualTo("1");
    Assertions.assertThat(result.getDocument().getTitle()).isNull();
    Assertions.assertThat(result.getDocument().getPreamble()).isNull();
    Assertions.assertThat(result.getDocument().getTags()).isEmpty();
    Assertions.assertThat(result.getDocument().getMetadata()).isNotNull();
    Assertions.assertThat(result.getDocument().getMetadata())
        .isEqualTo("{\"modified_fallback_showcase_modified\":\"" + DateUtil.formatDateUTC(dateExpected)
            + "\",\"private\":" + hidden
            + ",\"showcase_modified\":\"" + DateUtil.formatDateUTC(dateModifiedExpected)
            + "\",\"modified\":\"" + DateUtil.formatDateUTC(dateExpected)
            + "\",\"type\":\"" + INDEX_TYPE_SHOWCASE + "\"}");
  }

  @Test
  public void buildSearchIndexEntryFromShowcase_sort_date_fallback()
  {
    /* prepare */
    Showcase showcase = new Showcase();
    boolean hidden = showcase.isHidden();
    long id = 11L;
    showcase.setId(id);
    Date modifyDate = testDataFactory.createDate(LocalDateTime.of(2021, Month.MAY, 31, 10, 8, 54));
    showcase.setModifyDate(modifyDate);
    Date dateExpected = new Date(modifyDate.getTime());

    /* execute */
    SearchIndexEntry result = sut.buildSearchIndexEntryFromShowcase(showcase);

    /* assert */
    Assertions.assertThat(result).isNotNull();
    Assertions.assertThat(result.getIndexName()).isEqualTo(INDEX_NAME_SHOWCASE);
    Assertions.assertThat(result.getType()).isNull();
    Assertions.assertThat(result.getDocument()).isNotNull();
    Assertions.assertThat(result.getDocument().getId()).isEqualTo(Long.toString(id));
    Assertions.assertThat(result.getDocument().getSections()).isEmpty();
    Assertions.assertThat(result.getDocument().getMandant()).isEqualTo("1");
    Assertions.assertThat(result.getDocument().getTitle()).isNull();
    Assertions.assertThat(result.getDocument().getPreamble()).isNull();
    Assertions.assertThat(result.getDocument().getTags()).isEmpty();
    Assertions.assertThat(result.getDocument().getMetadata()).isNotNull();
    Assertions.assertThat(result.getDocument().getMetadata())
        .isEqualTo("{\"modified_fallback_showcase_modified\":\"" + DateUtil.formatDateUTC(dateExpected)
            + "\",\"private\":" + hidden
            + ",\"showcase_modified\":\"" + DateUtil.formatDateUTC(dateExpected)
            + "\",\"type\":\"" + INDEX_TYPE_SHOWCASE + "\"}");
  }

  @Test
  public void buildSearchIndexEntryFromShowcase_max()
  {
    /* prepare */
    Showcase showcase = testDataFactory.getTestShowcaseEntityWithIds();
    ShowcaseImage image = new ShowcaseImage();
    image.setImageOrderId(2);
    showcase.getImages().add(image);

    /* execute */
    SearchIndexEntry result = sut.buildSearchIndexEntryFromShowcase(showcase);

    /* assert */
    Showcase showcaseExpected = testDataFactory.getTestShowcaseEntityWithIds();
    Assertions.assertThat(result).isNotNull();
    Assertions.assertThat(result.getIndexName()).isEqualTo(INDEX_NAME_SHOWCASE);
    Assertions.assertThat(result.getType()).isNull();
    Assertions.assertThat(result.getDocument()).isNotNull();
    Assertions.assertThat(result.getDocument().getId()).isEqualTo(Long.toString(showcaseExpected.getId()));
    Assertions.assertThat(result.getDocument().getSections()).isEmpty();
    Assertions.assertThat(result.getDocument().getMandant()).isEqualTo("1");
    Assertions.assertThat(result.getDocument().getTitle()).isEqualTo(showcaseExpected.getTitle());
    Assertions.assertThat(result.getDocument().getPreamble()).isEqualTo(showcaseExpected.getNotes());
    Assertions.assertThat(result.getDocument().getTags())
        .isEqualTo(showcaseExpected.getKeywords().stream().map(k -> k.getName()).toArray(String[]::new));
    Assertions.assertThat(result.getDocument().getMetadata()).isNotNull();
    // The base64 encoded string in "image" has to be encoded as it is done on writing data to
    // search index
    Assertions.assertThat(result.getDocument().getMetadata())
        .isEqualTo("{\"modified_fallback_showcase_modified\":\""
            + DateUtil.formatDateUTC(showcaseExpected.getManualShowcaseModifiedDate())
            + "\",\"image\":" + new Gson().toJson(ImageUtil.convertByteArrayToBase64StringThumbnail(
                showcaseExpected.getImages().stream().findFirst().get().getImage()))
            + ",\"private\":" + showcaseExpected.isHidden()
            + ",\"showcase_created\":\"" + DateUtil.formatDateUTC(showcaseExpected.getCreateDate())
            + "\",\"showcase_modified\":\"" + DateUtil.formatDateUTC(showcaseExpected.getModifyDate())
            + "\",\"groups\":["
            + StringUtils.join(showcaseExpected.getCategories().stream().map(k -> "\"" + k.getName() + "\"")
                .toArray(String[]::new), ",")
            + "],\"primary_showcase_type\":\""
            + showcaseExpected.getShowcaseTypes().stream().filter(t -> t.isPrimaryShowcase())
                .findFirst().get().getName()
            + "\",\"type\":\"" + INDEX_TYPE_SHOWCASE
            + "\",\"used_datasets\":["
            + StringUtils.join(showcaseExpected.getUsedDatasets().stream().map(k -> "\"" +
                k.getName() + "\"")
                .toArray(String[]::new), ",")
            + "],\"platforms\":["
            + StringUtils.join(showcaseExpected.getPlatforms().stream().map(k -> "\"" + k.getName() + "\"")
                .toArray(String[]::new), ",")
            + "],\"modified\":\"" + DateUtil.formatDateUTC(showcaseExpected.getManualShowcaseModifiedDate())
            + "\",\"issued\":\"" + DateUtil.formatDateUTC(showcaseExpected.getManualShowcaseCreatedDate())
            + "\",\"showcase_types\":["
            + StringUtils
                .join(showcaseExpected.getShowcaseTypes().stream().filter(t -> !t.isPrimaryShowcase())
                    .map(t -> "\"" + t.getName() + "\"").toArray(String[]::new), ",")
            + "],\"spatial\":\"" + showcaseExpected.getSpatial()
            + "\"}");
  }

  @Test
  public void buildSearchIndexEntryFromShowcase_spatial_invalid_geometryNull()
  {
    /* prepare */
    Showcase showcase = new Showcase();
    boolean hidden = showcase.isHidden();
    long id = 11L;
    showcase.setId(id);
    String spatial = "[\n"
        + "            [\n"
        + "                [8.031005859375, 51.24128576954669],\n"
        + "                [10.3656005859375, 51.436888577204996],\n"
        + "                [10.4974365234375, 50.1135334310997],\n"
        + "                [8.4320068359375, 50.0289165635219],\n"
        + "                [8.031005859375, 51.24128576954669]\n"
        + "            ]\n"
        + "        ]";
    showcase.setSpatial(spatial);

    /* execute */
    SearchIndexEntry result = sut.buildSearchIndexEntryFromShowcase(showcase);

    /* assert */
    Assertions.assertThat(result.getIndexName()).isEqualTo(INDEX_NAME_SHOWCASE);
    Assertions.assertThat(result.getType()).isNull();
    Assertions.assertThat(result.getDocument()).isNotNull();
    Assertions.assertThat(result.getDocument().getId()).isEqualTo(Long.toString(id));
    Assertions.assertThat(result.getDocument().getSections()).isEmpty();
    Assertions.assertThat(result.getDocument().getMandant()).isEqualTo("1");
    Assertions.assertThat(result.getDocument().getTitle()).isNull();
    Assertions.assertThat(result.getDocument().getPreamble()).isNull();
    Assertions.assertThat(result.getDocument().getTags()).isEmpty();
    Assertions.assertThat(result.getDocument().getMetadata()).isNotNull();
    Assertions.assertThat(result.getDocument().getMetadata())
        .isEqualTo("{\"private\":" + hidden + ",\"type\":\"" + INDEX_TYPE_SHOWCASE
            + "\",\"spatial\":" + new Gson().toJson(spatial)
            + "}");
  }

  @Test
  public void buildSearchIndexEntryFromShowcase_spatial() throws Exception
  {
    /* prepare */
    Showcase showcase = new Showcase();
    boolean hidden = showcase.isHidden();
    long id = 11L;
    showcase.setId(id);
    String spatial = "{\n"
        + "        \"type\": \"Polygon\",\n"
        + "        \"coordinates\": [\n"
        + "            [\n"
        + "                [8.031005859375, 51.24128576954669],\n"
        + "                [10.3656005859375, 51.436888577204996],\n"
        + "                [10.4974365234375, 50.1135334310997],\n"
        + "                [8.4320068359375, 50.0289165635219],\n"
        + "                [8.031005859375, 51.24128576954669]\n"
        + "            ],\n"
        + "            [\n"
        + "                [8.7451171875, 51.020666012558095],\n"
        + "                [9.9700927734375, 51.02412130394265],\n"
        + "                [9.9481201171875, 50.2682767372753],\n"
        + "                [8.800048828125, 50.306884231551166],\n"
        + "                [8.7451171875, 51.020666012558095]\n"
        + "            ],\n"
        + "            [\n"
        + "                [9.667968749999998, 51.26878915771344],\n"
        + "                [10.0634765625, 51.327179239685634],\n"
        + "                [10.1348876953125, 51.037939894299356],\n"
        + "                [9.755859375, 51.089722918116344],\n"
        + "                [9.667968749999998, 51.26878915771344]\n"
        + "            ]\n"
        + "        ]\n"
        + "}";
    showcase.setSpatial(spatial);

    /* execute */
    SearchIndexEntry result = sut.buildSearchIndexEntryFromShowcase(showcase);

    /* assert */
    Assertions.assertThat(result.getIndexName()).isEqualTo(INDEX_NAME_SHOWCASE);
    Assertions.assertThat(result.getType()).isNull();
    Assertions.assertThat(result.getDocument()).isNotNull();
    Assertions.assertThat(result.getDocument().getId()).isEqualTo(Long.toString(id));
    Assertions.assertThat(result.getDocument().getSections()).isEmpty();
    Assertions.assertThat(result.getDocument().getMandant()).isEqualTo("1");
    Assertions.assertThat(result.getDocument().getTitle()).isNull();
    Assertions.assertThat(result.getDocument().getPreamble()).isNull();
    Assertions.assertThat(result.getDocument().getTags()).isEmpty();
    Assertions.assertThat(result.getDocument().getMetadata()).isNotNull();
    GeoJsonWriter geoJsonWriter = new GeoJsonWriter();
    geoJsonWriter.setEncodeCRS(false);
    Assertions.assertThat(result.getDocument().getMetadata())
        .isEqualTo("{\"boundingbox\":"
            + geoJsonWriter.write(new GeoJsonReader().read(spatial).getEnvelope())
            + ",\"private\":" + hidden
            + ",\"spatial_center\":" + "{\"lon\":9.299973888122487,\"lat\":50.7257369963881}"
            + ",\"spatial_area\":" + "1.8641136750279301"
            + ",\"type\":\"" + INDEX_TYPE_SHOWCASE
            + "\",\"spatial\":" + new Gson().toJson(spatial)
            + "}");
  }
}
