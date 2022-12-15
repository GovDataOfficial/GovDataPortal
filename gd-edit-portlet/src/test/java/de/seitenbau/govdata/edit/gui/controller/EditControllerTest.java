package de.seitenbau.govdata.edit.gui.controller;

import static de.seitenbau.govdata.navigation.GovDataNavigation.FRIENDLY_URL_NAME_SEARCHRESULT_PAGE;
import static de.seitenbau.govdata.navigation.GovDataNavigation.PORTLET_NAME_SEARCHRESULT;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.ext.com.google.common.collect.Lists;
import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.BindingResult;

import com.google.gson.Gson;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.DummyPortletURL;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portletmvc4spring.test.mock.web.portlet.MockActionRequest;
import com.liferay.portletmvc4spring.test.mock.web.portlet.MockActionResponse;

import de.seitenbau.govdata.cache.OrganizationCache;
import de.seitenbau.govdata.date.DateUtil;
import de.seitenbau.govdata.edit.model.EditForm;
import de.seitenbau.govdata.edit.model.Resource;
import de.seitenbau.govdata.fuseki.FusekiClient;
import de.seitenbau.govdata.navigation.GovDataNavigation;
import de.seitenbau.govdata.odp.registry.ODRClient;
import de.seitenbau.govdata.odp.registry.ckan.impl.MetadataImpl;
import de.seitenbau.govdata.odp.registry.ckan.impl.OrganizationImpl;
import de.seitenbau.govdata.odp.registry.ckan.impl.UserImpl;
import de.seitenbau.govdata.odp.registry.ckan.json.ExtraBean;
import de.seitenbau.govdata.odp.registry.ckan.json.MetadataBean;
import de.seitenbau.govdata.odp.registry.ckan.json.OrganizationBean;
import de.seitenbau.govdata.odp.registry.ckan.json.UserBean;
import de.seitenbau.govdata.odp.registry.model.Metadata;
import de.seitenbau.govdata.odp.registry.model.MetadataListExtraFields;
import de.seitenbau.govdata.odp.registry.model.Organization;
import de.seitenbau.govdata.odr.RegistryClient;

@RunWith(MockitoJUnitRunner.class)
public class EditControllerTest
{
  private static final String NAME_INICATES_NEW_DATASET = "Neuer Datensatz";

  @Mock
  private FusekiClient fusekiClient;

  @Mock
  private RegistryClient registryClient;

  @Mock
  private ODRClient odrClient;

  @Mock
  private GovDataNavigation gdNavigation;

  @Mock
  private OrganizationCache organizationCache;

  @Mock
  private BindingResult bindingResult;

  @Mock
  private User user;

  @Mock
  private Portal portal;

  @InjectMocks
  private EditController sut;

  @Before
  public void setup()
  {
    new PortalUtil().setPortal(portal);
  }

  @After
  public void tearDown()
  {
    new PortalUtil().setPortal(null);
  }

  @Test
  public void deleteDataset() throws Exception
  {
    /* prepare */
    String name = "name-1";
    EditForm editForm = new EditForm();
    editForm.setName(name);
    String organizationId = "org-id-1";
    editForm.setOrganizationId(organizationId);

    MockActionResponse response = new MockActionResponse();
    MockActionRequest request = new MockActionRequest();

    de.seitenbau.govdata.odp.registry.model.User ckanUser = mockUser(request);

    List<Organization> organizationList = new ArrayList<>();
    OrganizationBean orgBean = new OrganizationBean();
    orgBean.setId(organizationId);
    Organization org = new OrganizationImpl(orgBean);
    organizationList.add(org);
    Mockito.when(odrClient.getOrganizationsForUser(ckanUser, "create_dataset")).thenReturn(organizationList);

    Metadata metadata = createMetadata(name);

    Mockito.when(odrClient.getMetadata(ckanUser, editForm.getName())).thenReturn(metadata);
    Mockito.when(odrClient.deleteMetadata(ckanUser, editForm.getName())).thenReturn(true);

    Mockito.when(gdNavigation.createLinkForSearchResults(FRIENDLY_URL_NAME_SEARCHRESULT_PAGE,
        PORTLET_NAME_SEARCHRESULT, "")).thenReturn(DummyPortletURL.getInstance());

    /* execute */
    sut.deleteDataset(editForm, bindingResult, response, request);

    /* assert */
    Assertions.assertThat(editForm.getName()).isEqualTo(name);
    Mockito.verify(gdNavigation).createLinkForSearchResults(FRIENDLY_URL_NAME_SEARCHRESULT_PAGE,
        PORTLET_NAME_SEARCHRESULT, "");
    Mockito.verify(fusekiClient).deleteDataset(ckanUser, metadata.getId(), metadata.getId());
  }

  @Test
  public void submitForm_new() throws Exception
  {
    /* prepare */
    String titleMunged = "new-title-1";
    EditForm editForm = new EditForm();
    editForm.setName(NAME_INICATES_NEW_DATASET);
    editForm.setTitle("New Title 1");
    String organizationId = "org-id-1";
    editForm.setOrganizationId(organizationId);
    String contributorID = "contributorID-1";
    editForm.setContributorId(contributorID);
    // resources
    Resource res = new Resource();
    res.setName("res name");
    res.setDescription("res description");
    res.setUrl("res url");
    res.setIssued("2022-10-20");
    res.setModified("2022-11-222");
    editForm.setResources(Lists.newArrayList(res));

    MockActionResponse response = new MockActionResponse();
    MockActionRequest request = new MockActionRequest();

    de.seitenbau.govdata.odp.registry.model.User ckanUser = mockUser(request);

    mockOrganizationList(organizationId, contributorID);

    Metadata metadata = createMetadata(titleMunged);
    Mockito.when(odrClient.createMetadata()).thenReturn(metadata);
    Mockito.when(odrClient.getMetadata(ckanUser, titleMunged)).thenReturn(metadata);

    Mockito.when(odrClient.persistMetadata(ckanUser, metadata)).thenReturn(true);

    /* execute */
    sut.submitForm(editForm, bindingResult, response, request);

    /* assert */
    Assertions.assertThat(editForm.getName()).isEqualTo(titleMunged);
    Mockito.verify(odrClient).persistMetadata(ckanUser, metadata);
    // after persist + load dataset
    Mockito.verify(odrClient, Mockito.times(2)).getMetadata(ckanUser, titleMunged);
    Mockito.verify(fusekiClient).updateOrCreateDataset(ckanUser, metadata.getId(), metadata.getId(),
        metadata.getOwnerOrg(), contributorID);
    Assertions.assertThat(metadata.getResources()).extracting("name").containsExactly("res name");
    Assertions.assertThat(metadata.getResources()).extracting("description")
        .containsExactly("res description");
    Assertions.assertThat(metadata.getResources()).extracting("url").containsExactly("res url");
    Assertions.assertThat(metadata.getResources()).extracting("issued").containsExactly(
        DateUtil.parseDateString(editForm.getResources().stream().findFirst().get().getIssued()));
    Assertions.assertThat(metadata.getResources()).extracting("modified").containsExactly(
        DateUtil.parseDateString(editForm.getResources().stream().findFirst().get().getModified()));
  }

  @Test
  public void submitForm_new_persist_no_success() throws Exception
  {
    /* prepare */
    String titleMunged = "new-title-1";
    EditForm editForm = new EditForm();
    editForm.setName(NAME_INICATES_NEW_DATASET);
    editForm.setTitle("New Title 1");
    String organizationId = "org-id-1";
    editForm.setOrganizationId(organizationId);
    String contributorID = "contributorID-1";
    editForm.setContributorId(contributorID);

    MockActionResponse response = new MockActionResponse();
    MockActionRequest request = new MockActionRequest();

    de.seitenbau.govdata.odp.registry.model.User ckanUser = mockUser(request);

    mockOrganizationList(organizationId, contributorID);

    Metadata metadata = createMetadata(titleMunged);
    Mockito.when(odrClient.createMetadata()).thenReturn(metadata);

    /* execute */
    sut.submitForm(editForm, bindingResult, response, request);

    /* assert */
    Assertions.assertThat(editForm.getName()).isEqualTo(titleMunged);
    Mockito.verify(odrClient).persistMetadata(ckanUser, metadata);
    Mockito.verify(odrClient).getMetadata(ckanUser, titleMunged); // load dataset
  }

  @Test
  public void submitForm_update_public() throws Exception
  {
    /* prepare */
    String name = "name-1";
    EditForm editForm = new EditForm();
    editForm.setName(name);
    String organizationId = "org-id-1";
    editForm.setOrganizationId(organizationId);
    String contributorID = "contributorID-1";
    editForm.setContributorId(contributorID);

    MockActionResponse response = new MockActionResponse();
    MockActionRequest request = new MockActionRequest();

    de.seitenbau.govdata.odp.registry.model.User ckanUser = mockUser(request);

    mockOrganizationList(organizationId, contributorID);

    MetadataBean metadataBean = new MetadataBean();
    metadataBean.setId("id-1");
    metadataBean.setOwner_org("owner_org");
    metadataBean.setName(name);
    Metadata metadata = new MetadataImpl(metadataBean, odrClient);
    Mockito.when(odrClient.getMetadata(ckanUser, name)).thenReturn(metadata);

    Mockito.when(odrClient.persistMetadata(ckanUser, metadata)).thenReturn(true);

    /* execute */
    sut.submitForm(editForm, bindingResult, response, request);

    /* assert */
    Assertions.assertThat(editForm.getName()).isEqualTo(name);
    Mockito.verify(odrClient).persistMetadata(ckanUser, metadata);
    Mockito.verify(fusekiClient).updateOrCreateDataset(ckanUser, metadata.getId(), metadata.getId(),
        metadata.getOwnerOrg(), contributorID);
  }

  @Test
  public void submitForm_update_private() throws Exception
  {
    /* prepare */
    String name = "name-1";
    EditForm editForm = new EditForm();
    editForm.setPrivate(true);
    editForm.setName(name);
    String organizationId = "org-id-1";
    editForm.setOrganizationId(organizationId);
    String contributorID = "contributorID-1";
    editForm.setContributorId(contributorID);
    MockActionResponse response = new MockActionResponse();
    MockActionRequest request = new MockActionRequest();

    de.seitenbau.govdata.odp.registry.model.User ckanUser = mockUser(request);

    mockOrganizationList(organizationId, contributorID);

    MetadataBean metadataBean = new MetadataBean();
    metadataBean.setId("id-1");
    metadataBean.setOwner_org("owner_org");
    metadataBean.setName(name);
    Metadata metadata = new MetadataImpl(metadataBean, odrClient);
    Mockito.when(odrClient.getMetadata(ckanUser, name)).thenReturn(metadata);

    Mockito.when(odrClient.persistMetadata(ckanUser, metadata)).thenReturn(true);

    /* execute */
    sut.submitForm(editForm, bindingResult, response, request);

    /* assert */
    Assertions.assertThat(editForm.getName()).isEqualTo(name);
    Mockito.verify(odrClient).persistMetadata(ckanUser, metadata);
    Mockito.verify(fusekiClient).deleteDataset(ckanUser, metadata.getId(), metadataBean.getId());
  }

  private Metadata createMetadata(String titleMunged)
  {
    MetadataBean metadataBean = new MetadataBean();
    metadataBean.setId("id-1");
    metadataBean.setOwner_org("owner_org");
    metadataBean.setName(titleMunged);
    Metadata metadata = new MetadataImpl(metadataBean, odrClient);
    return metadata;
  }

  private de.seitenbau.govdata.odp.registry.model.User mockUser(MockActionRequest request)
      throws PortalException
  {
    request.setAttribute(WebKeys.USER, user);
    String liferayUserName = "LiferayUser-name";
    Mockito.when(user.getScreenName()).thenReturn(liferayUserName);
    Mockito.when(portal.getUser(request)).thenReturn(user);

    Mockito.when(registryClient.getInstance()).thenReturn(odrClient);
    UserBean userBean = new UserBean();
    userBean.setName(liferayUserName.toLowerCase());
    userBean.setDisplay_name("ckan user display name");
    de.seitenbau.govdata.odp.registry.model.User ckanUser = new UserImpl(userBean);
    Mockito.when(odrClient.findUser(liferayUserName.toLowerCase())).thenReturn(ckanUser);
    return ckanUser;
  }

  private void mockOrganizationList(String organizationId, String contributorID)
  {
    List<Organization> organizationList = new ArrayList<>();
    OrganizationBean orgBean = new OrganizationBean();
    orgBean.setId(organizationId);
    List<ExtraBean> extras = new ArrayList<>();
    ExtraBean contributorIdExtra = new ExtraBean();
    contributorIdExtra.setKey(MetadataListExtraFields.CONTRIBUTOR_ID.getField());
    contributorIdExtra.setValue(new Gson().toJson(Lists.newArrayList(contributorID)));
    extras.add(contributorIdExtra);
    orgBean.setExtras(extras);
    Organization org = new OrganizationImpl(orgBean);
    organizationList.add(org);
    Mockito.when(organizationCache.getOrganizationsSorted()).thenReturn(organizationList);
  }
}
