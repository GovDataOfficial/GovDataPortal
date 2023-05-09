package de.seitenbau.govdata.search.common.searchresult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.liferay.portal.kernel.util.Props;
import com.liferay.portal.kernel.util.PropsUtil;

import de.seitenbau.govdata.odp.common.util.GovDataCollectionUtils;
import de.seitenbau.govdata.search.common.ESFieldConsts;
import de.seitenbau.govdata.search.common.SearchFilterBundle;
import de.seitenbau.govdata.search.filter.BaseFilter;
import de.seitenbau.govdata.search.sort.Sort;
import de.seitenbau.govdata.search.sort.SortType;

@RunWith(MockitoJUnitRunner.class)
public class ParameterProcessingTest
{
  final String DEFAULT_TYPE = "all";

  final Sort DEFAULT_SORT = new Sort(SortType.RELEVANCE, false);

  final String DATA_PAGE = "/daten";

  final String HIGH_VALUE_DATASET_TAGS = "hvd,highvaluedataset,highvaluedatasets";

  @Mock
  private Props propsMock;

  @Before
  public void setUp()
  {
    PropsUtil.setProps(propsMock);
    Mockito.when(propsMock.get("elasticsearch.high.value.dataset.tags"))
        .thenReturn(HIGH_VALUE_DATASET_TAGS);
  }

  @After
  public void tearDown()
  {
    PropsUtil.setProps(null);
  }

  /**
   * Prepare params without any active filters.
   */
  @Test
  public void prepareParameters_empty_params() throws Exception
  {
    /* prepare */
    Map<String, String[]> parameterMapInput = new HashMap<>();

    /* execute */
    PreparedParameters result = ParameterProcessing.prepareParameters(parameterMapInput, "/home");

    /* verify */
    Assertions.assertThat(result).isNotNull();
    Assertions.assertThat(result.getActiveFilters()).hasSize(0);
    Assertions.assertThat(result.getQuery().getQueryString()).isNull();
    Assertions.assertThat(result.getSelectedSorting()).isEqualTo(DEFAULT_SORT);
    Assertions.assertThat(result.getType()).isEqualTo(DEFAULT_TYPE);
  }

  /**
   * Prepare params for all available filters and sort by newest asc.
   */
  @Test
  public void prepareParameters() throws Exception
  {
    /* prepare */
    Map<String, String[]> parameterMapInput = prepareParameterInputMap();

    /* execute */
    PreparedParameters result = ParameterProcessing.prepareParameters(parameterMapInput, DATA_PAGE);

    /* verify */
    Assertions.assertThat(result).isNotNull();
    Assertions.assertThat(result.getActiveFilters()).hasSize(18);
    Assertions.assertThat(result.getActiveFilters().get("type")).hasSize(1).contains("dataset");
    Assertions.assertThat(result.getActiveFilters().get("title")).hasSize(1).contains("testTitle");
    Assertions.assertThat(result.getActiveFilters().get("notes")).hasSize(1).contains("testNotes");
    Assertions.assertThat(result.getActiveFilters().get("groups")).hasSize(1).contains("educ");
    Assertions.assertThat(result.getActiveFilters().get("tags")).hasSize(2)
        .containsExactlyInAnyOrder("testTag", "testTag2");
    Assertions.assertThat(result.getActiveFilters().get("openness")).hasSize(1).contains("has_open");
    Assertions.assertThat(result.getActiveFilters().get("licence")).hasSize(1).contains("testLicence");
    Assertions.assertThat(result.getActiveFilters().get("format")).hasSize(1).contains("json");
    Assertions.assertThat(result.getActiveFilters().get("sourceportal")).hasSize(1).contains("testSourceportal");
    Assertions.assertThat(result.getActiveFilters().get("maintainer")).hasSize(1).contains("testMaintainer");
    Assertions.assertThat(result.getActiveFilters().get("publisher")).hasSize(1).contains("testPublisher");
    Assertions.assertThat(result.getActiveFilters().get("hvd")).hasSize(1).contains("is_hvd");
    Assertions.assertThat(result.getActiveFilters().get("dataservice")).hasSize(1).contains("has_data_service");
    Assertions.assertThat(result.getActiveFilters().get("state")).hasSize(1).contains("08");
    Assertions.assertThat(result.getActiveFilters().get("platforms")).hasSize(1).contains("web");
    Assertions.assertThat(result.getActiveFilters().get("showcase_types")).hasSize(1).contains("concept");
    Assertions.assertThat(result.getQuery().getQueryString()).isEqualTo(parameterMapInput.get("q")[0]);
    Assertions.assertThat(result.getType()).isEqualTo("dataset");
    Assertions.assertThat(result.getDateFrom()).isEqualTo("2015-01-01");
    Assertions.assertThat(result.getDateUntil()).isEqualTo("2025-02-02");
    Assertions.assertThat(result.getBoundingBox().getLabel()).isEqualTo("1.0,3.0,2.0,4.0");
    Assertions.assertThat(result.getBoundingBox().getCenter().toString()).isEqualTo("3.5, 1.5");
    Assertions.assertThat(result.getSelectedSorting()).isEqualTo(new Sort(SortType.LASTMODIFICATION, true));
  }

  /**
   * Create FilterBundle without any active filters.
   */
  @Test
  public void createFilterBundle_emptyFilters() throws Exception
  {
    /* prepare */
    Map<String, String[]> parameterMapInput = new HashMap<>();
    PreparedParameters preparedParameters = ParameterProcessing.prepareParameters(parameterMapInput, "/home");

    /* execute */
    SearchFilterBundle bundle =
        ParameterProcessing.createFilterBundle(preparedParameters, new ArrayList<>(), new ArrayList<>());

    /* verify */
    Assertions.assertThat(bundle).isNotNull();
    Assertions.assertThat(bundle.getFilters()).hasSize(0);
    Assertions.assertThat(bundle.getBoostSpatialRelevance()).isFalse();
    Assertions.assertThat(bundle.getHidePrivateDatasets()).isTrue();
    Assertions.assertThat(bundle.getShowOnlyPrivateShowcases()).isFalse();
  }

  /**
   * Create FilterBundle with all available filters.
   */
  @Test
  public void createFilterBundle() throws Exception
  {
    /* prepare */
    Map<String, String[]> parameterMapInput = prepareParameterInputMap();
    PreparedParameters preparedParameters =
        ParameterProcessing.prepareParameters(parameterMapInput, DATA_PAGE);

    /* execute */
    SearchFilterBundle bundle =
        ParameterProcessing.createFilterBundle(preparedParameters, Arrays.asList("testOrganizationId"),
            new ArrayList<>());

    /* verify */
    Assertions.assertThat(bundle).isNotNull();
    Assertions.assertThat(bundle.getFilters()).hasSize(20);
    Assertions.assertThat(bundle.getBoostSpatialRelevance()).isTrue();
    Assertions.assertThat(bundle.getHidePrivateDatasets()).isFalse();
    Assertions.assertThat(bundle.getShowOnlyPrivateShowcases()).isTrue();
    Assertions.assertThat(bundle.getFilters()).extracting("fragmentName")
        .containsExactlyInAnyOrder(getDefaultFilterFragmentNameList());
    Assertions.assertThat(bundle.getFilters()).extracting("label")
        .containsExactlyInAnyOrder(getDefaultFilterLabelList());

    BaseFilter stateFilter = bundle.getFilters().stream().filter(f -> "state".equals(f.getFragmentName()))
        .findFirst().orElse(null);
    Assertions.assertThat(stateFilter).isNotNull();
    BaseFilter[] stateFilters = (BaseFilter[]) ReflectionTestUtils.getField(stateFilter, "filters");
    Assertions.assertThat(stateFilters).extracting("elasticSearchField").containsExactlyInAnyOrder(
        ESFieldConsts.FIELD_TITLE_SEARCH_WORD, ESFieldConsts.FIELD_DESCRIPTION_SEARCH_WORD,
        ESFieldConsts.FIELD_TAGS_SEARCH, ESFieldConsts.FIELD_CONTRIBUTOR_ID_RAW,
        ESFieldConsts.FIELD_GEOCODING_URI_RAW, ESFieldConsts.FIELD_GEOCODING_TEXT, ESFieldConsts.BOUNDINGBOX);

    BaseFilter showcaseTypeFilter = bundle.getFilters().stream()
        .filter(f -> "showcase_types".equals(f.getFragmentName())).findFirst().orElse(null);
    Assertions.assertThat(showcaseTypeFilter).isNotNull();
    BaseFilter[] showcaseTypeFilters =
        (BaseFilter[]) ReflectionTestUtils.getField(showcaseTypeFilter, "filters");
    Assertions.assertThat(showcaseTypeFilters).extracting("elasticSearchField").containsExactlyInAnyOrder(
        ESFieldConsts.FIELD_PRIMARY_SHOWCASE_TYPE, ESFieldConsts.FIELD_ADDITIONAL_SHOWCASE_TYPE);
    Assertions.assertThat(showcaseTypeFilters).extracting("label").containsExactlyInAnyOrder(
        "concept", "concept");
  }

  /**
   * Create FilterBundle with all available filters, but the hvd tags config is null.
   */
  @Test
  public void createFilterBundle_hvd_tags_null() throws Exception
  {
    /* prepare */
    Mockito.reset(propsMock);

    Map<String, String[]> parameterMapInput = prepareParameterInputMap();
    PreparedParameters preparedParameters =
        ParameterProcessing.prepareParameters(parameterMapInput, DATA_PAGE);

    /* execute */
    SearchFilterBundle bundle =
        ParameterProcessing.createFilterBundle(preparedParameters, Arrays.asList("testOrganizationId"),
            new ArrayList<>());

    /* verify */
    Assertions.assertThat(bundle).isNotNull();
    Assertions.assertThat(bundle.getFilters()).hasSize(19);
    Assertions.assertThat(bundle.getBoostSpatialRelevance()).isTrue();
    Assertions.assertThat(bundle.getHidePrivateDatasets()).isFalse();
    Assertions.assertThat(bundle.getShowOnlyPrivateShowcases()).isTrue();
    Assertions.assertThat(bundle.getFilters()).extracting("fragmentName")
        .containsExactlyInAnyOrder(getDefaultFilterFragmentNameList("hvd"));
    Assertions.assertThat(bundle.getFilters()).extracting("label")
        .containsExactlyInAnyOrder(getDefaultFilterLabelList(prepareHighValueDatasetTags().toString()));
  }

  /**
   * Create FilterBundle with all available filters, but the hvd tags config is null.
   */
  @Test
  public void createFilterBundle_hvd_tags_empty() throws Exception
  {
    /* prepare */
    Mockito.reset(propsMock);
    Mockito.when(propsMock.get("elasticsearch.high.value.dataset.tags")).thenReturn("");

    Map<String, String[]> parameterMapInput = prepareParameterInputMap();
    PreparedParameters preparedParameters =
        ParameterProcessing.prepareParameters(parameterMapInput, DATA_PAGE);

    /* execute */
    SearchFilterBundle bundle =
        ParameterProcessing.createFilterBundle(preparedParameters, Arrays.asList("testOrganizationId"),
            new ArrayList<>());

    /* verify */
    Assertions.assertThat(bundle).isNotNull();
    Assertions.assertThat(bundle.getFilters()).hasSize(19);
    Assertions.assertThat(bundle.getBoostSpatialRelevance()).isTrue();
    Assertions.assertThat(bundle.getHidePrivateDatasets()).isFalse();
    Assertions.assertThat(bundle.getShowOnlyPrivateShowcases()).isTrue();
    Assertions.assertThat(bundle.getFilters()).extracting("fragmentName")
        .containsExactlyInAnyOrder(getDefaultFilterFragmentNameList("hvd"));
    Assertions.assertThat(bundle.getFilters()).extracting("label")
        .containsExactlyInAnyOrder(getDefaultFilterLabelList(prepareHighValueDatasetTags().toString()));
  }

  /**
   * Check if filters are removed from the bundle if they contain an invalid value.
   */
  @Test
  public void createFilterBundle_cleanInvalidFilters() throws Exception
  {
    /* prepare */
    Map<String, String[]> parameterMapInput = new HashMap<>();
    parameterMapInput.put("f", new String[] {
        "openness:invalidOpennessFilter,tags:testTag,hvd:invalidHvdFilter,"
            + "dataservice:invalidDataserviceFilter"});
    PreparedParameters preparedParameters =
        ParameterProcessing.prepareParameters(parameterMapInput, DATA_PAGE);

    /* execute */
    SearchFilterBundle bundle =
        ParameterProcessing.createFilterBundle(preparedParameters, Arrays.asList("testOrganizationId"),
            new ArrayList<>());

    /* verify */
    Assertions.assertThat(bundle).isNotNull();
    Assertions.assertThat(bundle.getFilters()).hasSize(1);
    Assertions.assertThat(bundle.getBoostSpatialRelevance()).isFalse();
    Assertions.assertThat(bundle.getHidePrivateDatasets()).isTrue();
    Assertions.assertThat(bundle.getShowOnlyPrivateShowcases()).isFalse();
    Assertions.assertThat(bundle.getFilters()).extracting("fragmentName").containsExactlyInAnyOrder("tags");
    Assertions.assertThat(bundle.getFilters()).extracting("label").containsExactlyInAnyOrder("testTag");
  }

  /**
   * Check if invalid filters are removed and valid filters are kept.
   */
  @Test
  public void createFilterBundle_cleanInvalidFilters2() throws Exception
  {
    /* prepare */
    Map<String, String[]> parameterMapInput = new HashMap<>();
    parameterMapInput.put("f", new String[] {
        "openness:invalidOpennessFilter,hvd:invalidHvdFilter,dataservice:invalidDataserviceFilter,"
            + "openness:has_open,hvd:is_hvd,dataservice:has_data_service"});
    PreparedParameters preparedParameters =
        ParameterProcessing.prepareParameters(parameterMapInput, DATA_PAGE);

    /* execute */
    SearchFilterBundle bundle =
        ParameterProcessing.createFilterBundle(preparedParameters, Arrays.asList("testOrganizationId"),
            new ArrayList<>());

    /* verify */
    Assertions.assertThat(bundle).isNotNull();
    Assertions.assertThat(bundle.getFilters()).hasSize(3);
    Assertions.assertThat(bundle.getBoostSpatialRelevance()).isFalse();
    Assertions.assertThat(bundle.getHidePrivateDatasets()).isTrue();
    Assertions.assertThat(bundle.getShowOnlyPrivateShowcases()).isFalse();
    Assertions.assertThat(bundle.getFilters()).extracting("fragmentName").containsExactlyInAnyOrder("hvd",
        "openness", "dataservice");
    Assertions.assertThat(bundle.getFilters()).extracting("label")
        .containsExactlyInAnyOrder(prepareHighValueDatasetTags().toString(), "true", "true");
  }

  private Object[] getDefaultFilterFragmentNameList(String... remove)
  {
    return Stream.of("hvd", "dataservice", "openness", "tags", "tags", "groups", "sourceportal", "platforms",
        "licence", "maintainer", "publisher", "title", "notes", "format", "start", "end", "state",
        "showcase_types", "onlyEditorMetadata", "boundingbox")
        .filter(s -> ArrayUtils.isEmpty(remove) || !Stream.of(remove).anyMatch(s::equals))
        .toArray(String[]::new);
  }

  private Object[] getDefaultFilterLabelList(String... remove)
  {
    return Stream.of(prepareHighValueDatasetTags().toString(), "true", "true", "educ", "testTag", "testTag2",
        "testSourceportal", "web", "testLicence", "testMaintainer", "testPublisher", "testTitle", "testNotes",
        "json", "01.01.2015", "02.02.2025", "OrFilterPhrase", "OrFilterPhrase", "[testOrganizationId]",
        "1.0,3.0,2.0,4.0")
        .filter(s -> ArrayUtils.isEmpty(remove) || !Stream.of(remove).anyMatch(s::equals))
        .toArray(String[]::new);
  }

  private Map<String, String[]> prepareParameterInputMap()
  {
    Map<String, String[]> parameterMapInput = new HashMap<>();
    parameterMapInput.put("q", new String[] {"test"});
    parameterMapInput.put("s", new String[] {"lastmodification_asc"});
    parameterMapInput.put("start", new String[] {"01.01.2015"});
    parameterMapInput.put("end", new String[] {"02.02.2025"});
    parameterMapInput.put("boundingbox", new String[] {"1.0,3.0,2.0,4.0"});
    parameterMapInput.put("f", new String[] {
        "openness:has_open,format:json,groups:educ,type:dataset,sourceportal:testSourceportal,"
            + "tags:testTag,tags:testTag2,licence:testLicence,hvd:is_hvd,dataservice:has_data_service,"
            + "maintainer:testMaintainer,publisher:testPublisher,platforms:web,title:testTitle,"
            + "notes:testNotes,state:08,showcase_types:concept,onlyEditorMetadata:onlyEditorMetadata,"
            + "onlyPrivateShowcases:onlyPrivateShowcases"});
    return parameterMapInput;
  }

  private List<String> prepareHighValueDatasetTags()
  {
    return GovDataCollectionUtils.convertStringListToLowerCase(
        Arrays.asList(StringUtils.stripAll(StringUtils.splitByWholeSeparator(
            HIGH_VALUE_DATASET_TAGS, ","))));
  }
}
