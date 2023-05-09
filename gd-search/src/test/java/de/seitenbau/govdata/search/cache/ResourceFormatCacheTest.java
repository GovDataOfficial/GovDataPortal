package de.seitenbau.govdata.search.cache;

import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import de.seitenbau.govdata.search.adapter.SearchService;

/**
 * Tests für die Klasse {@link ResourceFormatCache}.
 * 
 * @author rnoerenberg
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class ResourceFormatCacheTest
{
  private static final int RESULTS_LIMIT = 48;

  @Mock
  private SearchService indexServiceMock;

  @InjectMocks
  private ResourceFormatCache target;

  @Test
  public void getFormats_indexService_null() throws Exception
  {
    /* prepare */
    Mockito.when(indexServiceMock.getResourceFormats(RESULTS_LIMIT)).thenReturn(null);

    /* execute */
    List<String> result = target.getFormats();

    /* verify */
    Assertions.assertThat(result).isNull();
  }

  @Test
  public void getFormats_indexService_emptyList() throws Exception
  {
    /* prepare */
    Mockito.when(indexServiceMock.getResourceFormats(RESULTS_LIMIT)).thenReturn(new ArrayList<String>());

    /* execute */
    List<String> result = target.getFormats();

    /* verify */
    Assertions.assertThat(result).isEmpty();
  }

  @Test
  public void getFormats_indexService() throws Exception
  {
    /* prepare */
    ArrayList<String> resourceFormatList = new ArrayList<String>();
    resourceFormatList.add("one");
    resourceFormatList.add("two");
    resourceFormatList.add("three");
    Mockito.when(indexServiceMock.getResourceFormats(RESULTS_LIMIT)).thenReturn(resourceFormatList);

    /* execute */
    List<String> result = target.getFormats();

    /* verify */
    Assertions.assertThat(result).containsExactly(resourceFormatList.toArray(new String[0]));
  }

  @Test
  public void getFormats_cached() throws Exception
  {
    /* prepare */
    ArrayList<String> resourceFormatList = new ArrayList<String>();
    resourceFormatList.add("one");
    resourceFormatList.add("two");
    resourceFormatList.add("three");
    Mockito.when(indexServiceMock.getResourceFormats(RESULTS_LIMIT)).thenReturn(resourceFormatList);

    /* execute */
    List<String> result = target.getFormats();
    Object cachedObject = ReflectionTestUtils.getField(target, "formats");
    List<String> resultCached = target.getFormats();
    Object cachedObject2 = ReflectionTestUtils.getField(target, "formats");

    /* verify */
    Assertions.assertThat(result).containsExactly(resourceFormatList.toArray(new String[0]));
    Assertions.assertThat(cachedObject).isNotNull().isSameAs(cachedObject2);
    Assertions.assertThat(resultCached).isNotNull().isNotSameAs(result);
    Assertions.assertThat(resultCached).containsExactlyInAnyOrderElementsOf(result);
  }

  @Test
  public void getFormats_cache_expired() throws Exception
  {
    /* prepare */
    // deactivate caching
    target.setMaxCacheTimeHours(-1);
    ArrayList<String> resourceFormatList = new ArrayList<String>();
    resourceFormatList.add("one");
    resourceFormatList.add("two");
    resourceFormatList.add("three");
    // new instance for second call
    ArrayList<String> resourceFormatList2 = new ArrayList<String>(resourceFormatList);
    Mockito.when(indexServiceMock.getResourceFormats(RESULTS_LIMIT))
        .thenReturn(resourceFormatList)
        .thenReturn(resourceFormatList2);

    /* execute */
    List<String> result = target.getFormats();
    List<String> resultCached = target.getFormats();

    /* verify */
    Assertions.assertThat(result).containsExactly(resourceFormatList.toArray(new String[0]));
    Assertions.assertThat(result == resultCached).describedAs("Not same objects").isFalse();
    Mockito.verify(indexServiceMock, Mockito.times(2)).getResourceFormats(RESULTS_LIMIT);
  }

  @Test
  public void getFormatsSorted_indexService_null() throws Exception
  {
    /* prepare */
    Mockito.when(indexServiceMock.getResourceFormats(RESULTS_LIMIT)).thenReturn(null);

    /* execute */
    List<String> result = target.getFormatsSorted();

    /* verify */
    Assertions.assertThat(result).isNull();
  }

  @Test
  public void getFormatsSorted_indexService_emptyList() throws Exception
  {
    /* prepare */
    Mockito.when(indexServiceMock.getResourceFormats(RESULTS_LIMIT)).thenReturn(new ArrayList<String>());

    /* execute */
    List<String> result = target.getFormatsSorted();

    /* verify */
    Assertions.assertThat(result).isEmpty();
  }

  @Test
  public void getFormatsSorted() throws Exception
  {
    /* prepare */
    ArrayList<String> resourceFormatList = new ArrayList<String>();
    resourceFormatList.add("one");
    resourceFormatList.add("two");
    resourceFormatList.add("three");
    Mockito.when(indexServiceMock.getResourceFormats(RESULTS_LIMIT)).thenReturn(resourceFormatList);

    /* execute */
    List<String> result = target.getFormatsSorted();

    /* verify */
    ArrayList<String> expected = new ArrayList<String>();
    expected.add("one");
    expected.add("three");
    expected.add("two");
    Assertions.assertThat(result).containsExactly(expected.toArray(new String[0]));
  }

  @Test
  public void getFormatsSorted_cached() throws Exception
  {
    /* prepare */
    ArrayList<String> resourceFormatList = new ArrayList<String>();
    resourceFormatList.add("one");
    resourceFormatList.add("two");
    resourceFormatList.add("three");
    Mockito.when(indexServiceMock.getResourceFormats(RESULTS_LIMIT)).thenReturn(resourceFormatList);

    /* execute */
    List<String> result = target.getFormatsSorted();
    Object cachedObject = ReflectionTestUtils.getField(target, "formatsSorted");
    List<String> resultCached = target.getFormatsSorted();
    Object cachedObject2 = ReflectionTestUtils.getField(target, "formatsSorted");

    /* verify */
    ArrayList<String> expected = new ArrayList<String>();
    expected.add("one");
    expected.add("three");
    expected.add("two");
    Assertions.assertThat(result).containsExactly(expected.toArray(new String[0]));
    Assertions.assertThat(resultCached).isNotNull().isNotSameAs(result);
    Assertions.assertThat(cachedObject).isNotNull().isSameAs(cachedObject2);
    Assertions.assertThat(resultCached).containsExactlyInAnyOrderElementsOf(result);
  }

  @Test
  public void getFormatsSorted_cache_expired() throws Exception
  {
    /* prepare */
    // deactivate caching
    target.setMaxCacheTimeHours(-1);
    ArrayList<String> resourceFormatList = new ArrayList<String>();
    resourceFormatList.add("one");
    resourceFormatList.add("two");
    resourceFormatList.add("three");
    // new instance for second call
    ArrayList<String> resourceFormatList2 = new ArrayList<String>(resourceFormatList);
    Mockito.when(indexServiceMock.getResourceFormats(RESULTS_LIMIT))
        .thenReturn(resourceFormatList)
        .thenReturn(resourceFormatList2);

    /* execute */
    List<String> result = target.getFormatsSorted();
    List<String> resultCached = target.getFormatsSorted();

    /* verify */
    ArrayList<String> expected = new ArrayList<String>();
    expected.add("one");
    expected.add("three");
    expected.add("two");
    Assertions.assertThat(result).containsExactly(expected.toArray(new String[0]));
    Assertions.assertThat(result == resultCached).describedAs("Not same objects").isFalse();
    Mockito.verify(indexServiceMock, Mockito.times(2)).getResourceFormats(RESULTS_LIMIT);
  }

  @Test
  public void getFormats_cache_expired_invalidates_sortedCache() throws Exception
  {
    /* prepare */
    ArrayList<String> resourceFormatList = new ArrayList<String>();
    resourceFormatList.add("one");
    resourceFormatList.add("two");
    resourceFormatList.add("three");
    // new instance for second call
    ArrayList<String> resourceFormatList2 = new ArrayList<String>(resourceFormatList);
    Mockito.when(indexServiceMock.getResourceFormats(RESULTS_LIMIT))
        .thenReturn(resourceFormatList)
        .thenReturn(resourceFormatList2);

    /* execute */
    List<String> result = target.getFormatsSorted();
    List<String> resultUnsorted = target.getFormats();

    /* verify */
    Assertions.assertThat(resultUnsorted).containsExactly(resourceFormatList.toArray(new String[0]));
    ArrayList<String> expectedSorted = new ArrayList<String>();
    expectedSorted.add("one");
    expectedSorted.add("three");
    expectedSorted.add("two");
    Assertions.assertThat(result).containsExactly(expectedSorted.toArray(new String[0]));

    /* prepare (2) */
    // deactivate caching
    target.setMaxCacheTimeHours(-1);

    /* execute (2) */
    List<String> resultUnsorted2 = target.getFormats(); // invalidates sorted cache
    // activate caching
    target.setMaxCacheTimeHours(1);
    List<String> result2 = target.getFormatsSorted();

    /* verify (2) */
    Assertions.assertThat(result == result2).describedAs("Not same objects").isFalse();
    Assertions.assertThat(resultUnsorted == resultUnsorted2).describedAs("Not same objects").isFalse();
  }
}
