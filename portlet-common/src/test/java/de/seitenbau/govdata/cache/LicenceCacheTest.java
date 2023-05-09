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
import de.seitenbau.govdata.odp.registry.ckan.impl.LicenceImpl;
import de.seitenbau.govdata.odp.registry.ckan.json.LicenceBean;
import de.seitenbau.govdata.odp.registry.model.Licence;
import de.seitenbau.govdata.odr.RegistryClient;

/**
 * Tests für die Klasse {@link LicenceCache}.
 * 
 * @author rnoerenberg
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class LicenceCacheTest
{
  @Mock
  private RegistryClient registryClientMock;

  @Mock
  private ODRClient odrClientMock;

  @InjectMocks
  private LicenceCache target;

  @Test
  public void getLicenceMap_registryClient_null() throws Exception
  {
    /* prepare */
    Mockito.when(registryClientMock.getInstance()).thenReturn(odrClientMock);
    Mockito.when(odrClientMock.listLicenses()).thenReturn(null);

    /* execute */
    Map<String, Licence> result = target.getLicenceMap();

    /* verify */
    Assertions.assertThat(result).isEmpty();
  }

  @Test
  public void getLicenceMap_registryClient_emptyList() throws Exception
  {
    /* prepare */
    Mockito.when(registryClientMock.getInstance()).thenReturn(odrClientMock);
    Mockito.when(odrClientMock.listLicenses()).thenReturn(new ArrayList<Licence>());

    /* execute */
    Map<String, Licence> result = target.getLicenceMap();

    /* verify */
    Assertions.assertThat(result).isEmpty();
  }

  @Test
  public void getLicenceMap_registryClient() throws Exception
  {
    /* prepare */
    List<Licence> licenceList = new ArrayList<Licence>();
    licenceList.add(createLicence("one"));
    licenceList.add(createLicence("two"));
    licenceList.add(createLicence("three"));
    Mockito.when(registryClientMock.getInstance()).thenReturn(odrClientMock);
    Mockito.when(odrClientMock.listLicenses()).thenReturn(licenceList);

    /* execute */
    Map<String, Licence> result = target.getLicenceMap();

    /* verify */
    Assertions.assertThat(result).hasSize(licenceList.size());
    Assertions.assertThat(result.values()).containsOnly(licenceList.toArray(new Licence[0]));
  }

  @Test
  public void getLicenceMap_cached() throws Exception
  {
    /* prepare */
    List<Licence> licenceList = new ArrayList<Licence>();
    licenceList.add(createLicence("one"));
    licenceList.add(createLicence("two"));
    licenceList.add(createLicence("three"));
    Mockito.when(registryClientMock.getInstance()).thenReturn(odrClientMock);
    Mockito.when(odrClientMock.listLicenses()).thenReturn(licenceList);

    /* execute */
    Map<String, Licence> result = target.getLicenceMap();
    Object cachedObject = ReflectionTestUtils.getField(target, "licenceMap");
    Map<String, Licence> resultCached = target.getLicenceMap();
    Object cachedObject2 = ReflectionTestUtils.getField(target, "licenceMap");

    /* verify */
    Assertions.assertThat(result).hasSize(licenceList.size());
    Assertions.assertThat(result.values()).containsOnly(licenceList.toArray(new Licence[0]));
    Assertions.assertThat(cachedObject).isNotNull().isSameAs(cachedObject2);
    Assertions.assertThat(resultCached).isNotNull().isNotSameAs(result);
    Assertions.assertThat(resultCached).containsExactlyInAnyOrderEntriesOf(result);
  }

  @Test
  public void getLicenceMap_cache_expired() throws Exception
  {
    /* prepare */
    // deactivate caching
    target.setMaxCacheTimeHours(-1);
    List<Licence> licenceList = new ArrayList<Licence>();
    licenceList.add(createLicence("one"));
    licenceList.add(createLicence("two"));
    licenceList.add(createLicence("three"));
    // new instance for second call
    Mockito.when(registryClientMock.getInstance()).thenReturn(odrClientMock);
    List<Licence> licenceList2 = new ArrayList<Licence>(licenceList);
    Mockito.when(odrClientMock.listLicenses())
        .thenReturn(licenceList)
        .thenReturn(licenceList2);

    /* execute */
    Map<String, Licence> result = target.getLicenceMap();
    Map<String, Licence> resultCached = target.getLicenceMap();

    /* verify */
    Assertions.assertThat(result).hasSize(licenceList.size());
    Assertions.assertThat(result.values()).containsOnly(licenceList.toArray(new Licence[0]));
    Assertions.assertThat(result == resultCached).describedAs("Not same objects").isFalse();
    Mockito.verify(odrClientMock, Mockito.times(2)).listLicenses();
  }

  @Test
  public void getActiveLicenceListSortedByTitle_registryClient()
  {
    /* prepare */
    List<Licence> licenceList = new ArrayList<Licence>();
    licenceList.add(createLicence("aa"));
    licenceList.add(createLicence("xy"));
    licenceList.add(createLicence("c", "inactive"));
    licenceList.add(createLicence("bb"));
    Mockito.when(registryClientMock.getInstance()).thenReturn(odrClientMock);
    Mockito.when(odrClientMock.listLicenses()).thenReturn(licenceList);

    /* execute */
    List<Licence> result = target.getActiveLicenceListSortedByTitle();

    /* verify */
    Assertions.assertThat(result).hasSize(licenceList.size() - 1);
    Assertions.assertThat(result.get(0).getTitle()).isEqualTo("aa");
    Assertions.assertThat(result.get(1).getTitle()).isEqualTo("bb");
    Assertions.assertThat(result.get(2).getTitle()).isEqualTo("xy");
  }

  private LicenceImpl createLicence(String title)
  {
    return createLicence(title, "active");
  }

  private LicenceImpl createLicence(String title, String status)
  {
    LicenceBean bean = new LicenceBean();
    bean.setId("id:" + title);
    bean.setTitle(title);
    bean.setUrl("http://" + title + ".de");
    bean.setStatus(status);
    return new LicenceImpl(bean);
  }
}
