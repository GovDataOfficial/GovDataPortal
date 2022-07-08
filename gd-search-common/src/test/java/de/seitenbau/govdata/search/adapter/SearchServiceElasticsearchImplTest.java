package de.seitenbau.govdata.search.adapter;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.assertj.core.data.MapEntry;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
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

import de.seitenbau.govdata.search.test.util.IndexName;

@RunWith(MockitoJUnitRunner.class)
public class SearchServiceElasticsearchImplTest
{
  @Captor
  ArgumentCaptor<IndexRequest> indexRequestCaptor;

  @Captor
  ArgumentCaptor<SearchRequest> searchRequestCaptor;

  @Mock
  private RestHighLevelClient clientMock;

  @InjectMocks
  private SearchServiceElasticsearchImpl sut;

  @Before
  public void setup()
  {
    ReflectionTestUtils.setField(sut, "searchhistoryIndexName", IndexName.SEARCHHISTORY.getIndex());
    ReflectionTestUtils.setField(sut, "liferayIndexName", IndexName.LIFERAY.getIndex());
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
    Mockito.when(searchResponseMock.getScrollId()).thenReturn("scrollId1");
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
    Assertions.assertThat(searchRequestCaptor.getAllValues()).hasSize(1);
    SearchRequest sr = searchRequestCaptor.getValue();
    Assertions.assertThat(sr.indices()).containsOnlyOnce(IndexName.LIFERAY.getIndex());
    Assertions.assertThat(sr.scroll()).isNotNull();
  }

  private SearchHit createSearchHit(int docId, String id)
  {
    return new SearchHit(docId, id, new Text("_doc"), Collections.emptyMap(), Collections.emptyMap());
  }
}
