package de.seitenbau.govdata.search.adapter;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Date;

import org.assertj.core.api.Assertions;
import org.assertj.core.data.MapEntry;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.client.transport.TransportClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SearchServiceElasticsearchImplTest
{
  @Mock
  private TransportClient clientMock;

  @InjectMocks
  private SearchServiceElasticsearchImpl sut;

  @Test
  public void recordSearchPhrase_null() throws Exception
  {
    /* prepare */
    String phrase = null;

    /* execute */
    sut.recordSearchPhrase(phrase);

    /* verify */
    Mockito.verifyZeroInteractions(clientMock);
  }

  @Test
  public void recordSearchPhrase_only_spaces() throws Exception
  {
    /* prepare */
    String phrase = "   ";

    /* execute */
    sut.recordSearchPhrase(phrase);

    /* verify */
    Mockito.verifyZeroInteractions(clientMock);
  }

  @Test
  public void recordSearchPhrase_ok() throws Exception
  {
    /* prepare */
    String phrase = "te";
    IndexRequestBuilder irb = new IndexRequestBuilder(clientMock);
    Mockito.when(clientMock.prepareIndex(Mockito.anyString(), Mockito.anyString())).thenReturn(irb);

    /* execute */
    sut.recordSearchPhrase(phrase);

    /* verify */
    Mockito.verify(clientMock).prepareIndex(Mockito.anyString(), Mockito.anyString());
    Assertions.assertThat(irb.request().sourceAsMap()).hasSize(2);
    Assertions.assertThat(irb.request().sourceAsMap()).contains(MapEntry.entry("phrase", phrase));
    Assertions.assertThat(irb.request().sourceAsMap()).containsKey("timestamp");
    // "2019-12-19T12:09:20.712Z"
    String timestampString = (String) irb.request().sourceAsMap().get("timestamp");
    Date actualDate = Date.from(OffsetDateTime.parse(timestampString).toInstant());
    Date now = Date.from(OffsetDateTime.now(ZoneOffset.UTC).toInstant());
    Assertions.assertThat(actualDate).isInThePast().isCloseTo(now, 5000);
  }
}
