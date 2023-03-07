package de.seitenbau.govdata.search.adapter;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.search.TotalHits;
import org.apache.lucene.search.TotalHits.Relation;
import org.assertj.core.api.Assertions;
import org.assertj.core.data.MapEntry;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.CountResponse;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.filter.ParsedFilter;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.junit.Before;
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

import de.seitenbau.govdata.cache.CategoryCache;
import de.seitenbau.govdata.odp.registry.model.Category;
import de.seitenbau.govdata.search.common.SearchFilterBundle;
import de.seitenbau.govdata.search.common.SearchQuery;
import de.seitenbau.govdata.search.filter.util.FilterUtil;
import de.seitenbau.govdata.search.index.mapper.SearchHitMapper;
import de.seitenbau.govdata.search.index.model.FilterListDto;
import de.seitenbau.govdata.search.index.model.HitDto;
import de.seitenbau.govdata.search.index.model.SearchResultContainer;
import de.seitenbau.govdata.search.sort.Sort;
import de.seitenbau.govdata.search.sort.SortType;
import de.seitenbau.govdata.search.test.util.IndexName;

@RunWith(MockitoJUnitRunner.class)
public class SearchServiceElasticsearchImplTest
{
  @Captor
  ArgumentCaptor<IndexRequest> indexRequestCaptor;

  @Captor
  ArgumentCaptor<SearchRequest> searchRequestCaptor;

  @Captor
  ArgumentCaptor<SearchScrollRequest> searchScrollRequestCaptor;

  @Mock
  private RestHighLevelClient clientMock;

  @Mock
  private FilterUtil filterUtilMock;

  @InjectMocks
  private SearchServiceElasticsearchImpl sut;

  @Before
  public void setup()
  {
    ReflectionTestUtils.setField(sut, "searchhistoryIndexName", IndexName.SEARCHHISTORY.getIndex());
    ReflectionTestUtils.setField(sut, "liferayIndexName", IndexName.LIFERAY.getIndex());
    ReflectionTestUtils.setField(sut, "metricIndexName", IndexName.METRICS.getIndex());
    ReflectionTestUtils.setField(sut, "indexNames", new String[] {IndexName.LIFERAY.getIndex(),
        IndexName.CKAN.getIndex(), IndexName.SHOWCASES.getIndex()});
    sut.setHighValueDatasetTags("hvd,highvaluedataset,highvaluedatasets");
    Mockito.when(filterUtilMock.getDefaultTypeFilterValues())
        .thenReturn(Lists.newArrayList("article", "blog", "dataset", "showcase"));
  }

  @Test
  public void recordSearchPhrase_null() throws Exception
  {
    /* prepare */
    String phrase = null;

    /* execute */
    sut.recordSearchPhrase(phrase);

    /* verify */
    Mockito.verifyNoInteractions(clientMock);
  }

  @Test
  public void recordSearchPhrase_only_spaces() throws Exception
  {
    /* prepare */
    String phrase = "   ";

    /* execute */
    sut.recordSearchPhrase(phrase);

    /* verify */
    Mockito.verifyNoInteractions(clientMock);
  }

  @Test
  public void recordSearchPhrase_ok() throws Exception
  {
    /* prepare */
    String phrase = "te";
    // return null, because the response was ignored
    Mockito.when(clientMock.index(Mockito.any(), Mockito.any())).thenReturn(null);

    /* execute */
    sut.recordSearchPhrase(phrase);

    /* verify */
    Mockito.verify(clientMock).index(indexRequestCaptor.capture(), Mockito.eq(RequestOptions.DEFAULT));
    Assertions.assertThat(indexRequestCaptor.getAllValues()).hasSize(1);
    IndexRequest ir = indexRequestCaptor.getValue();
    Assertions.assertThat(ir.sourceAsMap()).hasSize(2);
    Assertions.assertThat(ir.sourceAsMap()).contains(MapEntry.entry("phrase", phrase));
    Assertions.assertThat(ir.sourceAsMap()).containsKey("timestamp");
    // "2019-12-19T12:09:20.712Z"
    String timestampString = (String) ir.sourceAsMap().get("timestamp");
    Date actualDate = Date.from(OffsetDateTime.parse(timestampString).toInstant());
    Date now = Date.from(OffsetDateTime.now(ZoneOffset.UTC).toInstant());
    Assertions.assertThat(actualDate).isInThePast().isCloseTo(now, 5000);
  }

  @Test
  public void findPortalContentIdsByPortletId() throws Exception
  {
    /* prepare */
    String portletId = "poertlet-id-1";
    SearchResponse searchResponseMock = Mockito.mock(SearchResponse.class);
    String scrollId = "scrollId1";
    Mockito.when(searchResponseMock.getScrollId()).thenReturn(scrollId);
    Mockito.when(clientMock.search(Mockito.any(), Mockito.any())).thenReturn(searchResponseMock);
    Mockito.when(clientMock.searchScroll(Mockito.any(), Mockito.any())).thenReturn(searchResponseMock);
    SearchHits searchHitsMock = Mockito.mock(SearchHits.class);
    Mockito.when(searchResponseMock.getHits()).thenReturn(searchHitsMock);
    SearchHit[] searchHits = new SearchHit[] {createSearchHit(1, "1"), createSearchHit(2, "2")};
    // returns no hits on the second search
    Mockito.when(searchHitsMock.getHits()).thenReturn(searchHits).thenReturn(new SearchHit[0]);

    /* execute */
    List<String> result = sut.findPortalContentIdsByPortletId(portletId);

    /* verify */
    Assertions.assertThat(result)
        .containsOnlyOnce(Arrays.stream(searchHits).map(hit -> hit.getId()).toArray(String[]::new));
    Mockito.verify(clientMock).search(searchRequestCaptor.capture(), Mockito.eq(RequestOptions.DEFAULT));
    Mockito.verify(clientMock).searchScroll(searchScrollRequestCaptor.capture(),
        Mockito.eq(RequestOptions.DEFAULT));
    Assertions.assertThat(searchRequestCaptor.getAllValues()).hasSize(1);
    SearchRequest sr = searchRequestCaptor.getValue();
    Assertions.assertThat(sr.indices()).containsOnlyOnce(IndexName.LIFERAY.getIndex());
    Assertions.assertThat(sr.scroll()).isNotNull();
    Assertions.assertThat(searchScrollRequestCaptor.getAllValues()).hasSize(1);
    SearchScrollRequest ssr = searchScrollRequestCaptor.getValue();
    Assertions.assertThat(ssr.scroll()).isNotNull();
    Assertions.assertThat(ssr.scrollId()).isEqualTo(scrollId);
  }

  @Test
  public void getMetrics() throws Exception
  {
    /* prepare */
    SearchResponse latestDateSearchResponseMock = Mockito.mock(SearchResponse.class);
    SearchHits latestDateSearchHitsMock = Mockito.mock(SearchHits.class);
    SearchHit latestDateSearchHitMock = Mockito.mock(SearchHit.class);
    Map<String, Object> sourceMap = new HashMap<>();
    sourceMap.put("date", "2022-05-02T13:30:01");
    Mockito.when(latestDateSearchHitMock.getSourceAsMap()).thenReturn(sourceMap);
    Mockito.when(latestDateSearchHitsMock.getHits()).thenReturn(new SearchHit[] {latestDateSearchHitMock});
    Mockito.when(latestDateSearchResponseMock.getHits()).thenReturn(latestDateSearchHitsMock);
    SearchResponse searchResponseMock = Mockito.mock(SearchResponse.class);
    String scrollId = "scrollId1";
    Mockito.when(searchResponseMock.getScrollId()).thenReturn(scrollId);
    Mockito.when(clientMock.search(Mockito.any(), Mockito.any())).thenReturn(latestDateSearchResponseMock,
        searchResponseMock);
    Mockito.when(clientMock.searchScroll(Mockito.any(), Mockito.any())).thenReturn(searchResponseMock);
    SearchHits searchHitsMock = Mockito.mock(SearchHits.class);
    Mockito.when(searchResponseMock.getHits()).thenReturn(searchHitsMock);
    SearchHit[] searchHits = new SearchHit[] {createSearchHit(1, "1"), createSearchHit(2, "2")};
    // returns no hits on the second search
    Mockito.when(searchHitsMock.getHits()).thenReturn(searchHits).thenReturn(new SearchHit[0]);

    /* execute */
    SearchHits result = sut.getMetrics();

    /* verify */
    Assertions.assertThat(result).containsOnlyOnce(searchHits);
    Mockito.verify(clientMock, Mockito.times(2)).search(searchRequestCaptor.capture(),
        Mockito.eq(RequestOptions.DEFAULT));
    Mockito.verify(clientMock).searchScroll(searchScrollRequestCaptor.capture(),
        Mockito.eq(RequestOptions.DEFAULT));
    Assertions.assertThat(searchRequestCaptor.getAllValues()).hasSize(2);
    SearchRequest sr = searchRequestCaptor.getValue(); // Gets the last one
    Assertions.assertThat(sr.indices()).containsOnlyOnce(IndexName.METRICS.getIndex());
    Assertions.assertThat(sr.scroll()).isNotNull();
    Assertions.assertThat(searchScrollRequestCaptor.getAllValues()).hasSize(1);
    SearchScrollRequest ssr = searchScrollRequestCaptor.getValue();
    Assertions.assertThat(ssr.scroll()).isNotNull();
    Assertions.assertThat(ssr.scrollId()).isEqualTo(scrollId);
  }

  @Test
  public void getMetrics_noData() throws Exception
  {
    /* prepare */
    SearchResponse latestDateSearchResponseMock = Mockito.mock(SearchResponse.class);
    SearchHits latestDateSearchHitsMock = Mockito.mock(SearchHits.class);
    Mockito.when(latestDateSearchHitsMock.getHits()).thenReturn(new SearchHit[] {});
    Mockito.when(latestDateSearchResponseMock.getHits()).thenReturn(latestDateSearchHitsMock);
    Mockito.when(clientMock.search(Mockito.any(), Mockito.any())).thenReturn(latestDateSearchResponseMock);

    /* execute */
    SearchHits result = sut.getMetrics();

    /* verify */
    Assertions.assertThat(result).isEmpty();
    Mockito.verify(clientMock).search(searchRequestCaptor.capture(), Mockito.eq(RequestOptions.DEFAULT));
    SearchRequest sr = searchRequestCaptor.getValue();
    Assertions.assertThat(sr.indices()).containsOnlyOnce(IndexName.METRICS.getIndex());
    Assertions.assertThat(sr.scroll()).isNull();
  }

  @Test
  public void getNumbers() throws Exception
  {
    /* prepare */
    final long[] expectedCounts = {247L, 5L, 16L, 3L};
    CountResponse countResponseMock = Mockito.mock(CountResponse.class);
    Mockito.when(countResponseMock.getCount()).thenReturn(expectedCounts[0], expectedCounts[1],
        expectedCounts[2], expectedCounts[3]);
    Mockito.when(clientMock.count(Mockito.any(), Mockito.any())).thenReturn(countResponseMock);

    /* execute */
    SearchResultContainer result = sut.getNumbers();

    /* verify */
    FilterListDto resultTypeFilters = result.getFilterMap().get("type");
    for (int i = 0; i < expectedCounts.length; i++)
    {
      Assertions.assertThat(resultTypeFilters.get(i).getDocCount()).isEqualTo(expectedCounts[i]);
    }
  }

  @Test
  public void search_empty_on_empty_datastore() throws Exception
  {
    /* prepare */
    SearchHit[] hitResponses = new SearchHit[] {};
    SearchHits searchHitsMock = Mockito.mock(SearchHits.class);
    SearchResponse searchResponseMock = Mockito.mock(SearchResponse.class);

    Mockito.when(searchHitsMock.getHits()).thenReturn(hitResponses);
    Mockito.when(searchResponseMock.getHits()).thenReturn(searchHitsMock);
    Mockito.when(clientMock.search(Mockito.any(), Mockito.any())).thenReturn(searchResponseMock);

    /* execute */
    SearchResultContainer result =
        sut.search(new SearchQuery(""), null, new SearchFilterBundle(), new Sort(SortType.RELEVANCE, false));

    /* verify */
    Assertions.assertThat(result).isNotNull();
  }

  @Test
  public void search_empty_on_empty_datastore_hvd_tags_null() throws Exception
  {
    /* prepare */
    SearchHit[] hitResponses = new SearchHit[] {};
    SearchHits searchHitsMock = Mockito.mock(SearchHits.class);
    SearchResponse searchResponseMock = Mockito.mock(SearchResponse.class);

    Mockito.when(searchHitsMock.getHits()).thenReturn(hitResponses);
    Mockito.when(searchResponseMock.getHits()).thenReturn(searchHitsMock);
    Mockito.when(clientMock.search(Mockito.any(), Mockito.any())).thenReturn(searchResponseMock);

    sut.setHighValueDatasetTags(null);

    /* execute */
    SearchResultContainer result =
        sut.search(new SearchQuery(""), null, new SearchFilterBundle(), new Sort(SortType.RELEVANCE, false));

    /* verify */
    Assertions.assertThat(result).isNotNull();
  }

  @Test
  public void search_empty_on_empty_datastore_hvd_tags_empty() throws Exception
  {
    /* prepare */
    SearchHit[] hitResponses = new SearchHit[] {};
    SearchHits searchHitsMock = Mockito.mock(SearchHits.class);
    SearchResponse searchResponseMock = Mockito.mock(SearchResponse.class);

    Mockito.when(searchHitsMock.getHits()).thenReturn(hitResponses);
    Mockito.when(searchResponseMock.getHits()).thenReturn(searchHitsMock);
    Mockito.when(clientMock.search(Mockito.any(), Mockito.any())).thenReturn(searchResponseMock);

    sut.setHighValueDatasetTags("");

    /* execute */
    SearchResultContainer result =
        sut.search(new SearchQuery(""), null, new SearchFilterBundle(), new Sort(SortType.RELEVANCE, false));

    /* verify */
    Assertions.assertThat(result).isNotNull();
  }

  @Test
  public void search_empty() throws Exception
  {
    /* prepare */
    final long expectedCount = 1L;
    final String expectedScollId = "ABC123";
    final Map<String, Category> responseCategoryMock = new HashMap<>();
    final HitDto responseHitDtoMock = new HitDto();

    SearchResponse searchResponseMock = Mockito.mock(SearchResponse.class);
    SearchHits searchHitsMock = Mockito.mock(SearchHits.class);
    SearchHit searchHitMock = Mockito.mock(SearchHit.class);
    CountResponse countResponseMock = Mockito.mock(CountResponse.class);
    Aggregations aggregationsResponseMock = Mockito.mock(Aggregations.class);
    CategoryCache categoryCacheMock = Mockito.mock(CategoryCache.class);
    SearchHitMapper searchHitMapperMock = Mockito.mock(SearchHitMapper.class);

    Mockito.when(categoryCacheMock.getCategoryMap()).thenReturn(responseCategoryMock);
    ReflectionTestUtils.setField(sut, "categoryCache", categoryCacheMock);

    Mockito.when(searchHitMapperMock.mapToHitDto(Mockito.any())).thenReturn(responseHitDtoMock);
    ReflectionTestUtils.setField(sut, "searchHitMapper", searchHitMapperMock);

    SearchHit[] hitResponses = new SearchHit[] {searchHitMock};
    Mockito.when(searchHitsMock.getHits()).thenReturn(hitResponses);
    Mockito.when(searchHitsMock.getTotalHits())
        .thenReturn(new TotalHits(hitResponses.length, Relation.EQUAL_TO));
    Mockito.when(searchResponseMock.getHits()).thenReturn(searchHitsMock);
    Mockito.when(searchResponseMock.getScrollId()).thenReturn(expectedScollId);
    Mockito.when(searchResponseMock.getAggregations()).thenReturn(aggregationsResponseMock);
    Mockito.when(countResponseMock.getCount()).thenReturn(expectedCount);
    Mockito.when(aggregationsResponseMock.get(Mockito.any())).thenReturn(new ParsedStringTerms(),
        new ParsedStringTerms(), new ParsedStringTerms(), new ParsedStringTerms(), new ParsedStringTerms(),
        new ParsedStringTerms(), new ParsedFilter(), new ParsedFilter(), new ParsedFilter(),
        new ParsedFilter(), new ParsedStringTerms());

    Mockito.when(clientMock.search(Mockito.any(), Mockito.any())).thenReturn(searchResponseMock);
    Mockito.when(clientMock.count(Mockito.any(), Mockito.any())).thenReturn(countResponseMock);

    /* execute */
    SearchResultContainer result =
        sut.search(new SearchQuery(""), null, new SearchFilterBundle(), new Sort(SortType.RELEVANCE, false));

    /* verify */
    Assertions.assertThat(result).isNotNull();
    Assertions.assertThat(result.getHitsTotal()).isEqualTo(1);
    Assertions.assertThat(result.getHits()).hasSize(1);
    Assertions.assertThat(result.isMoreNextHitsAvailable()).isEqualTo(false);
    Assertions.assertThat(result.getPageSize()).isEqualTo(10);
    Assertions.assertThat(result.getScrollId()).isEqualTo(expectedScollId);
  }

  private SearchHit createSearchHit(int docId, String id)
  {
    return new SearchHit(docId, id, new Text("_doc"), Collections.emptyMap(), Collections.emptyMap());
  }
}
