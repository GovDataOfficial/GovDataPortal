package de.seitenbau.govdata.search.geostate.display.cache;

import static de.seitenbau.govdata.navigation.GovDataNavigation.FRIENDLY_URL_NAME_SEARCHRESULT_PAGE;
import static de.seitenbau.govdata.navigation.GovDataNavigation.PORTLET_NAME_SEARCHRESULT;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.google.common.collect.Lists;
import com.liferay.portal.kernel.portlet.DummyPortletURL;

import de.seitenbau.govdata.navigation.GovDataNavigation;
import de.seitenbau.govdata.odp.common.util.GovDataCollectionUtils;
import de.seitenbau.govdata.search.adapter.SearchService;
import de.seitenbau.govdata.search.common.SearchFilterBundle;
import de.seitenbau.govdata.search.common.SearchQuery;
import de.seitenbau.govdata.search.common.searchresult.ParameterProcessing;
import de.seitenbau.govdata.search.common.searchresult.PreparedParameters;
import de.seitenbau.govdata.search.index.model.SearchResultContainer;
import de.seitenbau.govdata.search.sort.Sort;
import de.seitenbau.govdata.search.util.states.StateViewModel;

@RunWith(MockitoJUnitRunner.class)
public class DisplayStateCacheTest
{
  private static final int ALL_STATES_COUNT = 17;

  @Captor
  ArgumentCaptor<PreparedParameters> PreparedParametersCaptor;

  @Mock
  private GovDataNavigation govDataNavigation;

  @Mock
  private SearchService indexService;

  @Mock
  private ParameterProcessing parameterProcessing;

  @InjectMocks
  private DisplayStateCache sut;

  @Test
  public void getStateList() throws Exception
  {
    /* prepare */
    SearchResultContainer searchResultContainer = new SearchResultContainer();
    Mockito.when(parameterProcessing.createFilterBundle(Mockito.any(PreparedParameters.class),
        Mockito.eq(new ArrayList<>()))).thenReturn(new SearchFilterBundle());
    Mockito.when(indexService.search(Mockito.any(SearchQuery.class), Mockito.eq((Integer) null),
        Mockito.any(SearchFilterBundle.class), Mockito.any(Sort.class))).thenReturn(searchResultContainer);
    Mockito
        .when(govDataNavigation.createLinkForSearchResults(Mockito.eq(FRIENDLY_URL_NAME_SEARCHRESULT_PAGE),
            Mockito.eq(PORTLET_NAME_SEARCHRESULT), Mockito.eq(""), Mockito.anyString(),
            Mockito.eq("")))
        .thenReturn(DummyPortletURL.getInstance());

    /* execute */
    List<StateViewModel> result = sut.getStateList("");

    /* assert */
    Assertions.assertThat(result).hasSize(ALL_STATES_COUNT);
    Assertions.assertThat(result).extracting("url").allMatch(s -> !Objects.isNull(s), "not null");
    assertParameterProcessing(result, ALL_STATES_COUNT);
  }

  @Test
  public void getStateList_blockedStateList_null() throws Exception
  {
    /* prepare */
    SearchResultContainer searchResultContainer = new SearchResultContainer();
    Mockito.when(parameterProcessing.createFilterBundle(Mockito.any(PreparedParameters.class),
        Mockito.eq(new ArrayList<>()))).thenReturn(new SearchFilterBundle());
    Mockito.when(indexService.search(Mockito.any(SearchQuery.class), Mockito.eq((Integer) null),
        Mockito.any(SearchFilterBundle.class), Mockito.any(Sort.class))).thenReturn(searchResultContainer);
    Mockito
        .when(govDataNavigation.createLinkForSearchResults(Mockito.eq(FRIENDLY_URL_NAME_SEARCHRESULT_PAGE),
            Mockito.eq(PORTLET_NAME_SEARCHRESULT), Mockito.eq(""), Mockito.anyString(),
            Mockito.eq("")))
        .thenReturn(DummyPortletURL.getInstance());

    /* execute */
    List<StateViewModel> result = sut.getStateList(null);

    /* assert */
    Assertions.assertThat(result).hasSize(ALL_STATES_COUNT);
    Assertions.assertThat(result).extracting("url").allMatch(s -> !Objects.isNull(s), "not null");
    assertParameterProcessing(result, ALL_STATES_COUNT);
  }

  @Test
  public void getStateList_blockedStateList_changes_updating_cache() throws Exception
  {
    /* prepare */
    SearchResultContainer searchResultContainer = new SearchResultContainer();
    Mockito.when(parameterProcessing.createFilterBundle(Mockito.any(PreparedParameters.class),
        Mockito.eq(new ArrayList<>()))).thenReturn(new SearchFilterBundle());
    Mockito.when(indexService.search(Mockito.any(SearchQuery.class), Mockito.eq((Integer) null),
        Mockito.any(SearchFilterBundle.class), Mockito.any(Sort.class))).thenReturn(searchResultContainer);
    Mockito
        .when(govDataNavigation.createLinkForSearchResults(Mockito.eq(FRIENDLY_URL_NAME_SEARCHRESULT_PAGE),
            Mockito.eq(PORTLET_NAME_SEARCHRESULT), Mockito.eq(""), Mockito.anyString(),
            Mockito.eq("")))
        .thenReturn(DummyPortletURL.getInstance());
    String blockedStatesString = "1,2,3";
    String blockedStatesStringNew = "1,2";

    /* execute */
    List<StateViewModel> result = sut.getStateList(blockedStatesString);
    assertParameterProcessing(result, ALL_STATES_COUNT);
    List<StateViewModel> resultNew = sut.getStateList(blockedStatesStringNew);

    /* assert */
    Assertions.assertThat(result).hasSize(ALL_STATES_COUNT);
    // List is the same
    Assertions.assertThat(result).isEqualTo(resultNew);
    // but the cache is updated and returns a new object
    Assertions.assertThat(result).isNotSameAs(resultNew);
    assertParameterProcessing(resultNew, ALL_STATES_COUNT * 2);
  }

  @Test
  public void getStateList_blockedStateList_only_order_changes_not_updating_cache() throws Exception
  {
    /* prepare */
    SearchResultContainer searchResultContainer = new SearchResultContainer();
    Mockito.when(parameterProcessing.createFilterBundle(Mockito.any(PreparedParameters.class),
        Mockito.eq(new ArrayList<>()))).thenReturn(new SearchFilterBundle());
    Mockito.when(indexService.search(Mockito.any(SearchQuery.class), Mockito.eq((Integer) null),
        Mockito.any(SearchFilterBundle.class), Mockito.any(Sort.class))).thenReturn(searchResultContainer);
    Mockito
        .when(govDataNavigation.createLinkForSearchResults(Mockito.eq(FRIENDLY_URL_NAME_SEARCHRESULT_PAGE),
            Mockito.eq(PORTLET_NAME_SEARCHRESULT), Mockito.eq(""), Mockito.anyString(),
            Mockito.eq("")))
        .thenReturn(DummyPortletURL.getInstance());
    String blockedStatesString = "1,2,3";
    String blockedStatesStringNew = "3,2,1";

    /* execute */
    List<StateViewModel> result = sut.getStateList(blockedStatesString);
    assertParameterProcessing(result, ALL_STATES_COUNT);
    Object cachedObject = ReflectionTestUtils.getField(sut, "stateList");
    List<StateViewModel> resultNew = sut.getStateList(blockedStatesStringNew);
    Object cachedObject2 = ReflectionTestUtils.getField(sut, "stateList");

    /* assert */
    Assertions.assertThat(result).hasSize(ALL_STATES_COUNT);
    Assertions.assertThat(resultNew).isNotNull().isNotSameAs(result);
    Assertions.assertThat(cachedObject).isNotNull().isSameAs(cachedObject2);
    Assertions.assertThat(resultNew).containsExactlyInAnyOrderElementsOf(result);
    assertParameterProcessing(resultNew, ALL_STATES_COUNT);
  }

  @Test
  public void getStateList_blockedStates_markedAsBlocked() throws Exception
  {
    /* prepare */
    SearchResultContainer searchResultContainer = new SearchResultContainer();
    Mockito.when(parameterProcessing.createFilterBundle(Mockito.any(PreparedParameters.class),
        Mockito.eq(new ArrayList<>()))).thenReturn(new SearchFilterBundle());
    Mockito.when(indexService.search(Mockito.any(SearchQuery.class), Mockito.eq((Integer) null),
        Mockito.any(SearchFilterBundle.class), Mockito.any(Sort.class))).thenReturn(searchResultContainer);
    Mockito
        .when(govDataNavigation.createLinkForSearchResults(Mockito.eq(FRIENDLY_URL_NAME_SEARCHRESULT_PAGE),
            Mockito.eq(PORTLET_NAME_SEARCHRESULT), Mockito.eq(""), Mockito.anyString(),
            Mockito.eq("")))
        .thenReturn(DummyPortletURL.getInstance());
    String blockedStatesString = "Hessen,Sachsen";

    /* execute */
    List<StateViewModel> result = sut.getStateList(blockedStatesString);

    /* assert */
    Assertions.assertThat(result).hasSize(ALL_STATES_COUNT);
    assertStatesBlocked(result, blockedStatesString);
    assertParameterProcessing(result, ALL_STATES_COUNT);
  }

  @Test
  public void calculateAndSetCssClass_null() throws Exception
  {
    /* prepare */

    /* execute */
    sut.calculateAndSetCssClass(null);

    /* assert */
  }

  @Test
  public void calculateAndSetCssClass_emptyList() throws Exception
  {
    /* prepare */
    ArrayList<StateViewModel> stateList = new ArrayList<>();

    /* execute */
    sut.calculateAndSetCssClass(stateList);

    /* assert */
    Assertions.assertThat(stateList).isEmpty();
  }

  @Test
  public void calculateAndSetCssClass() throws Exception
  {
    /* prepare */
    List<Integer> resultCounts = Lists.newArrayList(100, 10, 30, 50, 200, 60, 80, 1200, 210, 20, 300, 240);
    List<StateViewModel> stateList = createStateList(resultCounts);
    StateViewModel blocked = createStateViewModel(100, 0);
    stateList.add(blocked);
    int expectedSize = stateList.size();

    /* execute */
    sut.calculateAndSetCssClass(stateList);

    /* assert */
    Assertions.assertThat(stateList).hasSize(expectedSize);
    List<StateViewModel> stateListExpected = createStateList(resultCounts);
    stateListExpected.add(blocked);
    updateExpectedStateList(stateListExpected);
    for (StateViewModel actual : stateList)
    {
      Assertions.assertThat(actual).isNotNull();
      StateViewModel expect =
          stateListExpected.stream().filter(svm -> svm.getId() == actual.getId()).findFirst().get();
      Assertions.assertThat(expect).isNotNull();
      Assertions.assertThat(actual.getCssClass()).as("Expect same CSS class").isEqualTo(expect.getCssClass());
    }
  }

  private void updateExpectedStateList(List<StateViewModel> stateList)
  {
    List<StateViewModel> stateListNotBlocked = GovDataCollectionUtils.collectionToStream(stateList)
        .filter(svm -> !svm.isBlocked()).collect(Collectors.toList());
    // sort by result count descending order
    Collections.sort(stateListNotBlocked, Comparator.comparing(StateViewModel::getResultCount).reversed());
    Assertions.assertThat(stateListNotBlocked.get(0).getResultCount()).as("Expect descending order")
        .isGreaterThanOrEqualTo(stateListNotBlocked.get(stateListNotBlocked.size() - 1).getResultCount());

    int i = 0;
    for (StateViewModel state : stateListNotBlocked)
    {
      if (i < 5)
      {
        state.setCssClass("100"); // Top 5
      }
      else if (i < 10)
      {
        state.setCssClass("80"); // Top 6-10
      }
      else
      {
        state.setCssClass("60");
      }
      i++;
    }
  }

  private List<StateViewModel> createStateList(List<Integer> resultCounts)
  {
    List<StateViewModel> result = new ArrayList<>();
    int i = 1;
    for (Integer count : resultCounts)
    {
      result.add(createStateViewModel(i, count));
      i++;
    }
    return result;
  }

  private StateViewModel createStateViewModel(int id, long resultCount)
  {
    StateViewModel result = new StateViewModel();
    result.setStateKey("stateKey" + id);
    result.setId(id);
    result.setName("name" + id);
    result.setDisplayName("displayName" + id);
    result.setResultCount(resultCount);
    return result;
  }

  private void assertStatesBlocked(List<StateViewModel> actual, String blockedStatesString)
  {
    List<String> blockedStateList =
        Stream.of(StringUtils.splitByWholeSeparator(blockedStatesString, ",")).collect(Collectors.toList());
    for (String blockedState : blockedStateList)
    {
      List<StateViewModel> candidates =
          actual.stream().filter(svm -> svm.getName().equals(blockedState)).collect(Collectors.toList());
      Assertions.assertThat(candidates).describedAs("Expects exactly one entry with name '%s'!", blockedState)
          .hasSize(1);
      Assertions.assertThat(candidates.get(0).isBlocked()).isTrue();
      Assertions.assertThat(candidates.get(0).getCssClass()).isNull();
    }
    // assert that all other states are not blocked
    actual.stream().filter(svm -> !blockedStateList.contains(svm.getName())).allMatch(svm -> svm.isBlocked());
    actual.stream().filter(svm -> !blockedStateList.contains(svm.getName()))
        .allMatch(svm -> StringUtils.isNotEmpty(svm.getCssClass()));
  }

  private void assertParameterProcessing(List<StateViewModel> result, int numberOfInvocations)
  {
    Mockito.verify(parameterProcessing, Mockito.times(numberOfInvocations))
        .createFilterBundle(PreparedParametersCaptor.capture(), Mockito.eq(new ArrayList<>()));
    List<PreparedParameters> actual = PreparedParametersCaptor.getAllValues();
    List<String> statekeys =
        actual.stream().map(s -> s.getActiveFilters().values().stream()
            .flatMap(List::stream)
            .collect(Collectors.toList())).flatMap(List::stream).collect(Collectors.toList());
    Assertions.assertThat(result).extracting("stateKey").allMatch(s -> statekeys.contains(s));
  }
}
