package de.seitenbau.govdata.search.index.mapper;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.assertj.core.util.Maps;
import org.elasticsearch.common.collect.Lists;
import org.elasticsearch.search.SearchHit;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import de.seitenbau.govdata.search.index.model.HitDto;

/**
 * Tests für @link {@link SearchHitMapper}.
 * 
 * @author rnoerenberg
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class SearchHitMapperTest
{
  private SearchHitMapper target;

  @Mock
  private SearchHit searchHit;

  @Before
  public void setup()
  {
    target = new SearchHitMapper();
  }

  @Test
  public void test_empty() throws Exception
  {
    /* prepare */
    Map<String, Object> sourceMap = new LinkedHashMap<>();
    Map<String, Object> metadataMap = new LinkedHashMap<>();
    sourceMap.put("metadata", metadataMap);
    Mockito.when(searchHit.getId()).thenReturn("test-id");
    Mockito.when(searchHit.getSource()).thenReturn(sourceMap);

    /* execute */
    HitDto result = target.mapToHitDto(searchHit);

    /* verify */
    Assertions.assertThat(result).isNotNull();
    Assertions.assertThat(result.getId()).isEqualTo("test-id");
    Assertions.assertThat(result.getType()).isEmpty();
    Assertions.assertThat(result.getTitle()).isEmpty();
    Assertions.assertThat(result.getContent()).isEmpty();
    Assertions.assertThat(result.getName()).isEqualTo("test-id");
    Assertions.assertThat(result.getContact()).isNull();
    Assertions.assertThat(result.getContactEmail()).isNull();
  }

  @Test
  public void test_contacts_metadata_extras_empty() throws Exception
  {
    /* prepare */
    Map<String, Object> sourceMap = new LinkedHashMap<>();
    Map<String, Object> metadataMap = new LinkedHashMap<>();
    metadataMap.put("name", "metadata-name");
    metadataMap.put("author", "author-name");
    metadataMap.put("author_email", "author@test.de");
    metadataMap.put("maintainer", "maintainer-name");
    metadataMap.put("maintainer_email", "maintainer@test.de");
    List<Map<String, String>> extras = Lists.newArrayList();
    metadataMap.put("extras", extras);
    sourceMap.put("metadata", metadataMap);
    Mockito.when(searchHit.getId()).thenReturn("test-id");
    Mockito.when(searchHit.getSource()).thenReturn(sourceMap);

    /* execute */
    HitDto result = target.mapToHitDto(searchHit);

    /* verify */
    Assertions.assertThat(result).isNotNull();
    Assertions.assertThat(result.getId()).isEqualTo("test-id");
    Assertions.assertThat(result.getType()).isEmpty(); // Fallback / Default
    Assertions.assertThat(result.getTitle()).isEmpty(); // Fallback / Default
    Assertions.assertThat(result.getContent()).isEmpty(); // Fallback / Default
    Assertions.assertThat(result.getName()).isEqualTo("metadata-name");
    Assertions.assertThat(result.getContact()).isNull();
    Assertions.assertThat(result.getContactEmail()).isNull();
  }

  @Test
  public void test_contacts_extras() throws Exception
  {
    /* prepare */
    Map<String, Object> sourceMap = new LinkedHashMap<>();
    Map<String, Object> metadataMap = new LinkedHashMap<>();
    metadataMap.put("name", "metadata-name");
    metadataMap.put("name", "metadata-name");
    metadataMap.put("author", "author-name");
    metadataMap.put("author_email", "author@test.de");
    metadataMap.put("maintainer", "maintainer-name");
    metadataMap.put("maintainer_email", "maintainer@test.de");
    Map<String, String> publisher = Maps.newHashMap();
    publisher.put("key", "publisher_name");
    publisher.put("value", "publisher-name");
    Map<String, String> publisherEmail = Maps.newHashMap();
    publisherEmail.put("key", "publisher_email");
    publisherEmail.put("value", "publisher@test.de");
    List<Map<String, String>> extras = Lists.newArrayList(publisher, publisherEmail);
    metadataMap.put("extras", extras);
    sourceMap.put("metadata", metadataMap);
    Mockito.when(searchHit.getId()).thenReturn("test-id");
    Mockito.when(searchHit.getSource()).thenReturn(sourceMap);

    /* execute */
    HitDto result = target.mapToHitDto(searchHit);

    /* verify */
    Assertions.assertThat(result).isNotNull();
    Assertions.assertThat(result.getId()).isEqualTo("test-id");
    Assertions.assertThat(result.getType()).isEmpty(); // Fallback / Default
    Assertions.assertThat(result.getTitle()).isEmpty(); // Fallback / Default
    Assertions.assertThat(result.getContent()).isEmpty(); // Fallback / Default
    Assertions.assertThat(result.getName()).isEqualTo("metadata-name");
    Assertions.assertThat(result.getContact()).isEqualTo("publisher-name");
    Assertions.assertThat(result.getContactEmail()).isEqualTo("publisher@test.de");
  }
}
