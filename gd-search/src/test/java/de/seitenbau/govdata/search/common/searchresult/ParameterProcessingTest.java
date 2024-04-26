package de.seitenbau.govdata.search.common.searchresult;

import java.util.HashMap;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.liferay.portal.kernel.util.Props;
import com.liferay.portal.kernel.util.PropsUtil;

import de.seitenbau.govdata.search.filter.util.FilterUtil;
import de.seitenbau.govdata.search.sort.Sort;
import de.seitenbau.govdata.search.sort.SortType;

@RunWith(MockitoJUnitRunner.class)
public class ParameterProcessingTest
{
  @InjectMocks
  private ParameterProcessing sut;

  final String DEFAULT_TYPE = "all";

  final Sort DEFAULT_SORT = new Sort(SortType.RELEVANCE, false);

  final String DATA_PAGE = "/daten";

  @Mock
  private Props propsMock;

  @Mock
  private FilterUtil filterUtil;

  @Before
  public void setUp()
  {
    PropsUtil.setProps(propsMock);
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
    PreparedParameters result = sut.prepareParameters(parameterMapInput, "/home");

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
    PreparedParameters result = sut.prepareParameters(parameterMapInput, DATA_PAGE);

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
    Assertions.assertThat(result.getBoundingBox()).isNotNull();
    Assertions.assertThat(result.getBoundingBox()).isEqualTo("1.0,3.0,2.0,4.0");
    Assertions.assertThat(result.getSelectedSorting()).isEqualTo(new Sort(SortType.LASTMODIFICATION, true));
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
}