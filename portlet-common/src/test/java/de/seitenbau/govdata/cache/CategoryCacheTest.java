package de.seitenbau.govdata.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import de.seitenbau.govdata.odp.registry.ODRClient;
import de.seitenbau.govdata.odp.registry.ckan.impl.CategoryImpl;
import de.seitenbau.govdata.odp.registry.ckan.json.GroupBean;
import de.seitenbau.govdata.odp.registry.model.Category;
import de.seitenbau.govdata.odr.RegistryClient;

/**
 * Tests für die Klasse {@link CategoryCache}.
 * 
 * @author rnoerenberg
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class CategoryCacheTest
{
  @Mock
  private RegistryClient registryClientMock;

  @Mock
  private ODRClient odrClientMock;

  @InjectMocks
  private CategoryCache target;

  @Test
  public void getCategoryMap_registryClient_null() throws Exception
  {
    /* prepare */
    Mockito.when(registryClientMock.getInstance()).thenReturn(odrClientMock);
    Mockito.when(odrClientMock.listCategories()).thenReturn(null);

    /* execute */
    Map<String, Category> result = target.getCategoryMap();

    /* verify */
    Assertions.assertThat(result).isEmpty();
  }

  @Test
  public void getCategoryMap_registryClient_emptyList() throws Exception
  {
    /* prepare */
    Mockito.when(registryClientMock.getInstance()).thenReturn(odrClientMock);
    Mockito.when(odrClientMock.listCategories()).thenReturn(new ArrayList<Category>());

    /* execute */
    Map<String, Category> result = target.getCategoryMap();

    /* verify */
    Assertions.assertThat(result).isEmpty();
  }

  @Test
  public void getCategoryMap_registryClient() throws Exception
  {
    /* prepare */
    List<Category> categoryList = new ArrayList<Category>();
    categoryList.add(createCategory("one"));
    categoryList.add(createCategory("two"));
    categoryList.add(createCategory("three"));
    Mockito.when(registryClientMock.getInstance()).thenReturn(odrClientMock);
    Mockito.when(odrClientMock.listCategories()).thenReturn(categoryList);

    /* execute */
    Map<String, Category> result = target.getCategoryMap();

    /* verify */
    Assertions.assertThat(result).hasSize(categoryList.size());
    Assertions.assertThat(result.values()).containsOnly(categoryList.toArray(new Category[0]));
  }

  @Test
  public void getCategoryMap_cached() throws Exception
  {
    /* prepare */
    List<Category> categoryList = new ArrayList<Category>();
    categoryList.add(createCategory("one"));
    categoryList.add(createCategory("two"));
    categoryList.add(createCategory("three"));
    Mockito.when(registryClientMock.getInstance()).thenReturn(odrClientMock);
    Mockito.when(odrClientMock.listCategories()).thenReturn(categoryList);

    /* execute */
    Map<String, Category> result = target.getCategoryMap();
    Object cachedObject = ReflectionTestUtils.getField(target, "categoryMap");
    Map<String, Category> resultCached = target.getCategoryMap();
    Object cachedObject2 = ReflectionTestUtils.getField(target, "categoryMap");

    /* verify */
    Assertions.assertThat(result).hasSize(categoryList.size());
    Assertions.assertThat(result.values()).containsOnly(categoryList.toArray(new Category[0]));
    Assertions.assertThat(cachedObject).isNotNull().isSameAs(cachedObject2);
    Assertions.assertThat(resultCached).isNotNull().isNotSameAs(result);
    Assertions.assertThat(resultCached).containsExactlyInAnyOrderEntriesOf(result);
  }

  @Test
  public void getCategoryMap_cache_expired() throws Exception
  {
    /* prepare */
    // deactivate caching
    target.setMaxCacheTimeHours(-1);
    List<Category> categoryList = new ArrayList<Category>();
    categoryList.add(createCategory("one"));
    categoryList.add(createCategory("two"));
    categoryList.add(createCategory("three"));
    // new instance for second call
    Mockito.when(registryClientMock.getInstance()).thenReturn(odrClientMock);
    List<Category> categoryList2 = new ArrayList<Category>(categoryList);
    Mockito.when(odrClientMock.listCategories())
        .thenReturn(categoryList)
        .thenReturn(categoryList2);

    /* execute */
    Map<String, Category> result = target.getCategoryMap();
    Map<String, Category> resultCached = target.getCategoryMap();

    /* verify */
    Assertions.assertThat(result).hasSize(categoryList.size());
    Assertions.assertThat(result.values()).containsOnly(categoryList.toArray(new Category[0]));
    Assertions.assertThat(result == resultCached).describedAs("Not same objects").isFalse();
    Mockito.verify(odrClientMock, Mockito.times(2)).listCategories();
  }

  private CategoryImpl createCategory(String name)
  {
    GroupBean bean = new GroupBean();
    bean.setId("id:" + name);
    bean.setType("group");
    bean.setName(name);
    return new CategoryImpl(bean);
  }
}
