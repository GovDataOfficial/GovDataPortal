package de.seitenbau.govdata.search.index.mapper;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.elasticsearch.search.SearchHit;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import de.seitenbau.govdata.date.DateUtil;
import de.seitenbau.govdata.search.index.model.HitDto;
import de.seitenbau.govdata.search.test.util.IndexName;

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
    ReflectionTestUtils.setField(target, "showcaseIndexName", IndexName.SHOWCASES.getIndex());
    ReflectionTestUtils.setField(target, "liferayIndexName", IndexName.LIFERAY.getIndex());
  }

  @Test
  public void test_empty() throws Exception
  {
    /* prepare */
    Map<String, Object> sourceMap = new LinkedHashMap<>();
    Map<String, Object> metadataMap = new LinkedHashMap<>();
    sourceMap.put("metadata", metadataMap);
    Mockito.when(searchHit.getId()).thenReturn("test-id");
    Mockito.when(searchHit.getIndex()).thenReturn(IndexName.CKAN.getIndex());
    Mockito.when(searchHit.getSourceAsMap()).thenReturn(sourceMap);

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
    Assertions.assertThat(result.getLastModified()).isNull();
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
    Mockito.when(searchHit.getIndex()).thenReturn(IndexName.CKAN.getIndex());
    Mockito.when(searchHit.getSourceAsMap()).thenReturn(sourceMap);

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
    Mockito.when(searchHit.getIndex()).thenReturn(IndexName.CKAN.getIndex());
    Mockito.when(searchHit.getSourceAsMap()).thenReturn(sourceMap);

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

  @Test
  public void test_sortDate_showcase() throws Exception
  {
    /* prepare */
    Map<String, Object> sourceMap = new LinkedHashMap<>();
    String sortDateDateString = "2021-04-10 5:34";
    sourceMap.put("sort_date", sortDateDateString);
    String sortDateDctDateString = "2021-04-10 6:34";
    sourceMap.put("sort_date_dct", sortDateDctDateString);
    String title = "title 1";
    sourceMap.put("title", title);
    String notes = "the notes";
    sourceMap.put("preamble", notes);
    Map<String, Object> metadataMap = new LinkedHashMap<>();
    String modifiedShowcaseDateString = "2021-04-10 7:34";
    metadataMap.put("modified_fallback_showcase_modified", modifiedShowcaseDateString);
    String modifiedDatePortalString = "2021-04-11 7:34";
    metadataMap.put("modified", modifiedDatePortalString);
    String modifiedCkanDateString = "2021-04-12 7:34";
    metadataMap.put("dct_modified_fallback_ckan", modifiedCkanDateString);
    String type = "showcase";
    metadataMap.put("type", type);
    sourceMap.put("metadata", metadataMap);
    Mockito.when(searchHit.getId()).thenReturn("test-id");
    Mockito.when(searchHit.getIndex()).thenReturn(IndexName.SHOWCASES.getIndex());
    Mockito.when(searchHit.getSourceAsMap()).thenReturn(sourceMap);

    /* execute */
    HitDto result = target.mapToHitDto(searchHit);

    /* verify */
    Assertions.assertThat(result).isNotNull();
    Assertions.assertThat(result.getId()).isEqualTo("test-id");
    Assertions.assertThat(result.getType()).isEqualTo(type);
    Assertions.assertThat(result.getTitle()).isEqualTo(title);
    Assertions.assertThat(result.getContent()).isEqualTo(notes);
    Assertions.assertThat(result.getName()).isEqualTo("test-id");
    Assertions.assertThat(result.getLastModified())
        .isEqualTo(DateUtil.parseDateString(modifiedShowcaseDateString));
  }

  @Test
  public void test_sortDate_portal() throws Exception
  {
    /* prepare */
    Map<String, Object> sourceMap = new LinkedHashMap<>();
    String sortDateDateString = "2021-04-10 5:34";
    sourceMap.put("sort_date", sortDateDateString);
    String sortDateDctDateString = "2021-04-10 6:34";
    sourceMap.put("sort_date_dct", sortDateDctDateString);
    String title = "title 1";
    sourceMap.put("title", title);
    String notes = "the notes";
    sourceMap.put("preamble", notes);
    Map<String, Object> metadataMap = new LinkedHashMap<>();
    String modifiedShowcaseDateString = "2021-04-10 7:34";
    metadataMap.put("modified_fallback_showcase_modified", modifiedShowcaseDateString);
    String modifiedDatePortalString = "2021-04-11 7:34";
    metadataMap.put("modified", modifiedDatePortalString);
    String modifiedCkanDateString = "2021-04-12 7:34";
    metadataMap.put("dct_modified_fallback_ckan", modifiedCkanDateString);
    String type = "portal";
    metadataMap.put("type", type);
    sourceMap.put("metadata", metadataMap);
    Mockito.when(searchHit.getId()).thenReturn("test-id");
    Mockito.when(searchHit.getIndex()).thenReturn(IndexName.LIFERAY.getIndex());
    Mockito.when(searchHit.getSourceAsMap()).thenReturn(sourceMap);

    /* execute */
    HitDto result = target.mapToHitDto(searchHit);

    /* verify */
    Assertions.assertThat(result).isNotNull();
    Assertions.assertThat(result.getId()).isEqualTo("test-id");
    Assertions.assertThat(result.getType()).isEqualTo(type);
    Assertions.assertThat(result.getTitle()).isEqualTo(title);
    Assertions.assertThat(result.getContent()).isEqualTo(notes);
    Assertions.assertThat(result.getName()).isEqualTo("test-id");
    Assertions.assertThat(result.getLastModified())
        .isEqualTo(DateUtil.parseDateString(modifiedDatePortalString));
  }

  @Test
  public void test_sortDate_ckan() throws Exception
  {
    /* prepare */
    Map<String, Object> sourceMap = new LinkedHashMap<>();
    String sortDateDateString = "2021-04-10 5:34";
    sourceMap.put("sort_date", sortDateDateString);
    String sortDateDctDateString = "2021-04-10 6:34";
    sourceMap.put("sort_date_dct", sortDateDctDateString);
    String title = "title 1";
    sourceMap.put("title", title);
    String notes = "the notes";
    sourceMap.put("preamble", notes);
    Map<String, Object> metadataMap = new LinkedHashMap<>();
    String modifiedShowcaseDateString = "2021-04-10 7:34";
    metadataMap.put("modified_fallback_showcase_modified", modifiedShowcaseDateString);
    String modifiedDatePortalString = "2021-04-11 7:34";
    metadataMap.put("modified", modifiedDatePortalString);
    String modifiedCkanDateString = "2021-04-12 7:34";
    metadataMap.put("dct_modified_fallback_ckan", modifiedCkanDateString);
    String type = "dataset";
    metadataMap.put("type", type);
    sourceMap.put("metadata", metadataMap);
    Mockito.when(searchHit.getId()).thenReturn("test-id");
    Mockito.when(searchHit.getIndex()).thenReturn(IndexName.CKAN.getIndex());
    Mockito.when(searchHit.getSourceAsMap()).thenReturn(sourceMap);

    /* execute */
    HitDto result = target.mapToHitDto(searchHit);

    /* verify */
    Assertions.assertThat(result).isNotNull();
    Assertions.assertThat(result.getId()).isEqualTo("test-id");
    Assertions.assertThat(result.getType()).isEqualTo(type);
    Assertions.assertThat(result.getTitle()).isEqualTo(title);
    Assertions.assertThat(result.getContent()).isEqualTo(notes);
    Assertions.assertThat(result.getName()).isEqualTo("test-id");
    Assertions.assertThat(result.getLastModified())
        .isEqualTo(DateUtil.parseDateString(modifiedCkanDateString));
  }
}
