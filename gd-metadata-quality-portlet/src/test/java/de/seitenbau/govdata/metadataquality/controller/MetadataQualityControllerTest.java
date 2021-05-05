package de.seitenbau.govdata.metadataquality.controller;

import static de.seitenbau.govdata.metadataquality.common.MetadataQualityConstants.LABELS;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.portlet.PortletURL;

import org.assertj.core.api.Assertions;
import org.elasticsearch.search.SearchHits;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Lists;
import com.liferay.portal.kernel.portlet.DummyPortletURL;

import de.seitenbau.govdata.cache.LicenceCache;
import de.seitenbau.govdata.cache.OrganizationCache;
import de.seitenbau.govdata.metadataquality.util.MetricsParser;
import de.seitenbau.govdata.odp.registry.ckan.impl.LicenceImpl;
import de.seitenbau.govdata.odp.registry.ckan.impl.OrganizationImpl;
import de.seitenbau.govdata.odp.registry.ckan.json.LicenceBean;
import de.seitenbau.govdata.odp.registry.ckan.json.OrganizationBean;
import de.seitenbau.govdata.odp.registry.model.Licence;
import de.seitenbau.govdata.odp.registry.model.Organization;
import de.seitenbau.govdata.search.gui.model.FilterViewModel;

@RunWith(MockitoJUnitRunner.class)
public class MetadataQualityControllerTest
{
  private static final String TOP_LICENSES = "top_licenses";

  private static final String ALL_PUBLISHERS = "govdata";

  @Mock
  private LicenceCache licenceCache;

  @Mock
  private OrganizationCache organizationCache;

  @Mock
  private MetricsParser metricsParser;

  @Mock
  private SearchHits searchHits;

  @InjectMocks
  private MetadataQualityController sut;

  @Test
  public void retrieveOrganizationFilterList_publisher_selected() throws Exception
  {
    /* prepare */
    int numberAvailablePublishers = 2;
    String filterIdActive = "id-2";
    String clearFilterUrl = "http://www.govdata.de/top";
    PortletURL baseUrl = DummyPortletURL.getInstance();

    expectRetrieveOrgFilterList(numberAvailablePublishers);

    /* execute */
    List<FilterViewModel> result =
        this.sut.retrieveOrganizationFilterList(filterIdActive, baseUrl, clearFilterUrl);

    /* verify */
    Assertions.assertThat(result).hasSize(numberAvailablePublishers);
    assertFilterViewModel(result, "id-1", "displayname-1", false);
    assertFilterViewModel(result, "id-2", "displayname-2", true);
    assertSortByDocCountDesc(result);
  }

  @Test
  public void retrieveOrganizationFilterList_govdata() throws Exception
  {
    /* prepare */
    int numberAvailablePublishers = 2;
    String clearFilterUrl = "http://www.govdata.de/top";
    PortletURL baseUrl = DummyPortletURL.getInstance();

    expectRetrieveOrgFilterList(numberAvailablePublishers);

    /* execute */
    List<FilterViewModel> result =
        this.sut.retrieveOrganizationFilterList(ALL_PUBLISHERS, baseUrl, clearFilterUrl);

    /* verify */
    Assertions.assertThat(result).hasSize(numberAvailablePublishers);
    assertFilterViewModel(result, "id-1", "displayname-1", false);
    assertFilterViewModel(result, "id-2", "displayname-2", false);
    assertSortByDocCountDesc(result);
  }

  @Test
  public void createLicenceMapWithTranslatedLabels_licenceCache_empty() throws Exception
  {
    /* prepare */
    Map<String, Map<String, List<?>>> topLicences = new HashMap<>();
    Map<String, List<?>> topLicencesGovData =
        createLicenceMap("licence-id-1", "licence-id-2", "licence-id-3");
    Map<String, List<?>> topLicencesPublisher = createLicenceMap("licence-id-1", "licence-id-5");
    topLicences.put(ALL_PUBLISHERS, topLicencesGovData);
    topLicences.put("publisher", topLicencesPublisher);

    Mockito.when(metricsParser.getValuesForType(TOP_LICENSES)).thenReturn(topLicences);

    /* execute */
    Map<String, Map<String, List<?>>> result = this.sut.createLicenceMapWithTranslatedLabels();

    /* verify */
    Assertions.assertThat(result).hasSize(topLicences.size());
    assertLicenceLabels(result.get(ALL_PUBLISHERS), topLicencesGovData);
    assertLicenceLabels(result.get("publisher"), topLicencesPublisher);
  }

  @Test
  public void createLicenceMapWithTranslatedLabels() throws Exception
  {
    /* prepare */
    Map<String, Map<String, List<?>>> topLicences = new HashMap<>();
    String[] licenceIdsArray = new String[] {"licence-id-1", "licence-id-2", "licence-id-3"};
    Map<String, List<?>> topLicencesGovData = createLicenceMap(licenceIdsArray);
    Map<String, List<?>> topLicencesPublisher = createLicenceMap("licence-id-1", "licence-id-5");
    topLicences.put(ALL_PUBLISHERS, topLicencesGovData);
    topLicences.put("publisher", topLicencesPublisher);
    Map<String, Licence> licenceMap = new HashMap<>();

    Mockito.when(metricsParser.getValuesForType(TOP_LICENSES)).thenReturn(topLicences);

    for (String licenceId : Lists.newArrayList(licenceIdsArray))
    {
      licenceMap.put(licenceId, createLicence(licenceId, "name-" + licenceId));
    }

    Mockito.when(licenceCache.getLicenceMap()).thenReturn(licenceMap);

    /* execute */
    Map<String, Map<String, List<?>>> result = this.sut.createLicenceMapWithTranslatedLabels();

    /* verify */
    Assertions.assertThat(result).hasSize(topLicences.size());
    assertLicenceLabels(result.get(ALL_PUBLISHERS), topLicencesGovData, licenceMap);
    assertLicenceLabels(result.get("publisher"), topLicencesPublisher, licenceMap);
  }

  private Licence createLicence(String licenceId, String title)
  {
    LicenceBean bean = new LicenceBean();
    bean.setId(licenceId);
    bean.setTitle(title);
    Licence result = new LicenceImpl(bean);
    return result;
  }

  private Organization createOrg(String id, String name, String displayName)
  {
    OrganizationBean bean = new OrganizationBean();
    bean.setId(id);
    bean.setName(name);
    bean.setDisplay_name(displayName);
    Organization result = new OrganizationImpl(bean);
    return result;
  }

  private Map<String, List<?>> createLicenceMap(String... licenceIds)
  {
    Map<String, List<?>> result = new HashMap<>();
    result.put(LABELS, Collections.unmodifiableList(Lists.newArrayList(licenceIds)));
    return Collections.unmodifiableMap(result);
  }

  private void assertLicenceLabels(Map<String, List<?>> actual, Map<String, List<?>> expected)
  {
    assertLicenceLabels(actual, expected, new HashMap<>());
  }

  @SuppressWarnings("unchecked")
  private void assertLicenceLabels(Map<String, List<?>> actual, Map<String, List<?>> expected,
      Map<String, Licence> licenceMap)
  {
    Assertions.assertThat(actual).hasSize(expected.size());
    List<String> labelListActual = (List<String>) actual.get(LABELS);
    List<String> expectedLabels = new ArrayList<>();
    for (Object licenceId : expected.get(LABELS))
    {
      Licence licence = licenceMap.get(licenceId);
      if (Objects.nonNull(licence))
      {
        expectedLabels.add(licence.getTitle());
      }
      else
      {
        expectedLabels.add((String) licenceId);
      }
    }
    Assertions.assertThat(labelListActual).containsExactly(expectedLabels.toArray(new String[0]));
  }

  private void expectRetrieveOrgFilterList(int numberAvailablePublishers)
  {
    List<Organization> organizationList = new ArrayList<>();
    for (int i = 1; i <= numberAvailablePublishers; i++)
    {
      organizationList.add(createOrg("id-" + i, "name-" + i, "displayname-" + i));
    }
    organizationList.add(createOrg("id-all", ALL_PUBLISHERS, "displayname-govdata"));
    Map<String, Long> availablePublishers = new LinkedHashMap<>();
    long i = 1;
    for (Organization org : organizationList)
    {
      availablePublishers.put(org.getId(), i);
      i++;
    }
    // Not available publisher
    organizationList.add(createOrg("id-not-available", "name-not-available", "displayname-not-available"));

    Mockito.when(metricsParser.getAvailablePublishers()).thenReturn(availablePublishers);
    Mockito.when(organizationCache.getOrganizationsSorted()).thenReturn(organizationList);
  }

  private void assertFilterViewModel(List<FilterViewModel> actual, String filterId, String displayName,
      boolean active)
  {
    boolean found = false;
    for (FilterViewModel filter : actual)
    {
      if (filter.getName().equals(filterId))
      {
        found = true;
        Assertions.assertThat(filter.getDisplayName()).isEqualTo(displayName);
        Assertions.assertThat(filter.isActive()).isEqualTo(active);
      }
    }

    Assertions.assertThat(found).as("No Filter with ID '%s' found!", filterId).isTrue();
  }

  private void assertSortByDocCountDesc(List<FilterViewModel> actual)
  {
    Iterator<FilterViewModel> actualIt = actual.iterator();
    while (actualIt.hasNext())
    {
      FilterViewModel higher = actualIt.next();
      if (actualIt.hasNext())
      {
        FilterViewModel lower = actualIt.next();
        Assertions.assertThat(higher.getDocCount()).isGreaterThan(lower.getDocCount());
      }
    }
  }

}
