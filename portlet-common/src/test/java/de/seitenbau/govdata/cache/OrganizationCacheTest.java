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
import org.mockito.runners.MockitoJUnitRunner;

import de.seitenbau.govdata.odp.registry.ODRClient;
import de.seitenbau.govdata.odp.registry.ckan.impl.OrganizationImpl;
import de.seitenbau.govdata.odp.registry.ckan.json.OrganizationBean;
import de.seitenbau.govdata.odp.registry.model.Organization;
import de.seitenbau.govdata.odr.RegistryClient;

/**
 * Tests für die Klasse {@link OrganizationCache}.
 * 
 * @author rnoerenberg
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class OrganizationCacheTest
{
  @Mock
  private RegistryClient registryClientMock;

  @Mock
  private ODRClient odrClientMock;

  @InjectMocks
  private OrganizationCache target;

  @Test
  public void getOrganizationMap_registryClient_null() throws Exception
  {
    /* prepare */
    Mockito.when(registryClientMock.getInstance()).thenReturn(odrClientMock);
    Mockito.when(odrClientMock.getOrganizations()).thenReturn(null);

    /* execute */
    Map<String, Organization> result = target.getOrganizationMap();

    /* verify */
    Assertions.assertThat(result).isEmpty();
  }

  @Test
  public void getOrganizationMap_registryClient_emptyList() throws Exception
  {
    /* prepare */
    Mockito.when(registryClientMock.getInstance()).thenReturn(odrClientMock);
    Mockito.when(odrClientMock.getOrganizations()).thenReturn(new ArrayList<Organization>());

    /* execute */
    Map<String, Organization> result = target.getOrganizationMap();

    /* verify */
    Assertions.assertThat(result).isEmpty();
  }

  @Test
  public void getOrganizationMap_registryClient() throws Exception
  {
    /* prepare */
    List<Organization> organizationList = new ArrayList<Organization>();
    organizationList.add(createOrganization("one"));
    organizationList.add(createOrganization("two"));
    organizationList.add(createOrganization("three"));
    Mockito.when(registryClientMock.getInstance()).thenReturn(odrClientMock);
    Mockito.when(odrClientMock.getOrganizations()).thenReturn(organizationList);

    /* execute */
    Map<String, Organization> result = target.getOrganizationMap();

    /* verify */
    Assertions.assertThat(result).hasSize(organizationList.size());
    Assertions.assertThat(result.values()).containsOnly(organizationList.toArray(new Organization[0]));
  }

  @Test
  public void getOrganizationMap_cached() throws Exception
  {
    /* prepare */
    List<Organization> organizationList = new ArrayList<Organization>();
    organizationList.add(createOrganization("one"));
    organizationList.add(createOrganization("two"));
    organizationList.add(createOrganization("three"));
    Mockito.when(registryClientMock.getInstance()).thenReturn(odrClientMock);
    Mockito.when(odrClientMock.getOrganizations()).thenReturn(organizationList);

    /* execute */
    Map<String, Organization> result = target.getOrganizationMap();
    Map<String, Organization> resultCached = target.getOrganizationMap();

    /* verify */
    Assertions.assertThat(result).hasSize(organizationList.size());
    Assertions.assertThat(result.values()).containsOnly(organizationList.toArray(new Organization[0]));
    Assertions.assertThat(result == resultCached).describedAs("Same objects").isTrue();
  }

  @Test
  public void getOrganizationMap_cache_expired() throws Exception
  {
    /* prepare */
    // deactivate caching
    target.setMaxCacheTimeHours(-1);
    List<Organization> organizationList = new ArrayList<Organization>();
    organizationList.add(createOrganization("one"));
    organizationList.add(createOrganization("two"));
    organizationList.add(createOrganization("three"));
    // new instance for second call
    Mockito.when(registryClientMock.getInstance()).thenReturn(odrClientMock);
    List<Organization> organizationList2 = new ArrayList<Organization>(organizationList);
    Mockito.when(odrClientMock.getOrganizations())
        .thenReturn(organizationList)
        .thenReturn(organizationList2);

    /* execute */
    Map<String, Organization> result = target.getOrganizationMap();
    Map<String, Organization> resultCached = target.getOrganizationMap();

    /* verify */
    Assertions.assertThat(result).hasSize(organizationList.size());
    Assertions.assertThat(result.values()).containsOnly(organizationList.toArray(new Organization[0]));
    Assertions.assertThat(result == resultCached).describedAs("Not same objects").isFalse();
    Mockito.verify(odrClientMock, Mockito.times(2)).getOrganizations();
  }

  private OrganizationImpl createOrganization(String name)
  {
    OrganizationBean bean = new OrganizationBean();
    bean.setId("id:" + name);
    bean.setName(name);
    return new OrganizationImpl(bean);
  }
}
