package de.seitenbau.govdata.edit.gui.controller;

import static de.seitenbau.govdata.navigation.GovDataNavigation.FRIENDLY_URL_NAME_SEARCHRESULT_PAGE;
import static de.seitenbau.govdata.navigation.GovDataNavigation.PORTLET_NAME_SEARCHRESULT;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.portlet.PortletRequest;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
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
import com.liferay.portletmvc4spring.test.mock.web.portlet.MockRenderRequest;
import com.liferay.portletmvc4spring.test.mock.web.portlet.MockRenderResponse;

import de.seitenbau.govdata.cache.CategoryCache;
import de.seitenbau.govdata.cache.LicenceCache;
import de.seitenbau.govdata.cache.OrganizationCache;
import de.seitenbau.govdata.date.DateUtil;
import de.seitenbau.govdata.edit.model.EditForm;
import de.seitenbau.govdata.edit.model.Resource;
import de.seitenbau.govdata.fuseki.FusekiClient;
import de.seitenbau.govdata.navigation.GovDataNavigation;
import de.seitenbau.govdata.odp.registry.ODRClient;
import de.seitenbau.govdata.odp.registry.ckan.impl.CategoryImpl;
import de.seitenbau.govdata.odp.registry.ckan.impl.LicenceImpl;
import de.seitenbau.govdata.odp.registry.ckan.impl.MetadataImpl;
import de.seitenbau.govdata.odp.registry.ckan.impl.OrganizationImpl;
import de.seitenbau.govdata.odp.registry.ckan.impl.UserImpl;
import de.seitenbau.govdata.odp.registry.ckan.json.ExtraBean;
import de.seitenbau.govdata.odp.registry.ckan.json.GroupBean;
import de.seitenbau.govdata.odp.registry.ckan.json.LicenceBean;
import de.seitenbau.govdata.odp.registry.ckan.json.MetadataBean;
import de.seitenbau.govdata.odp.registry.ckan.json.OrganizationBean;
import de.seitenbau.govdata.odp.registry.ckan.json.UserBean;
import de.seitenbau.govdata.odp.registry.model.Category;
import de.seitenbau.govdata.odp.registry.model.Licence;
import de.seitenbau.govdata.odp.registry.model.Metadata;
import de.seitenbau.govdata.odp.registry.model.MetadataListExtraFields;
import de.seitenbau.govdata.odp.registry.model.Organization;
import de.seitenbau.govdata.odr.RegistryClient;

@RunWith(MockitoJUnitRunner.class)
public class EditControllerTest
{
  private static final String CONTRIBUTOR_ID_DEFAULT = "contributorID-1";

  private static final String ORG_ID_DEFAULT = "org-id-1";

  private static final String NAME_INDICATES_NEW_DATASET = "Neuer Datensatz";

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
  private LicenceCache licenceCache;

  @Mock
  private CategoryCache categoryCache;

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
    String organizationId = ORG_ID_DEFAULT;
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
  public void showForm() throws Exception
  {
    /* prepare */
    MockRenderResponse response = new MockRenderResponse();
    MockRenderRequest request = new MockRenderRequest();
    Model model = new ExtendedModelMap();
    String editFormName = "test-name";
    EditForm editForm = buildEditForm(editFormName);

    de.seitenbau.govdata.odp.registry.model.User ckanUser = mockUser(request);

    List<Organization> organizations = createOrganizationList(ORG_ID_DEFAULT, CONTRIBUTOR_ID_DEFAULT);

    List<Licence> licences = createLicenceList("TEST-URL");

    List<Category> categories = createCategoryList("TEST-soci");

    Mockito.when(odrClient.getOrganizationsForUser(ckanUser, "create_dataset")).thenReturn(organizations);
    Mockito.when(licenceCache.getLicenceListSortedByTitle()).thenReturn(licences);
    Mockito.when(categoryCache.getCategoriesSortedByTitle()).thenReturn(categories);

    // When hideContributorId is false: show contributor ids
    sut.setHideContributorId("false");

    /* execute */
    sut.showForm("Test-dataset", editForm, bindingResult, request, response, model);

    /* assert */
    Map<String, Object> modelMap = model.asMap();

    Assertions.assertThat(modelMap).hasSize(11);
    Assertions.assertThat(modelMap.get("contributorIdSelectList")).asList().isNotEmpty();
    EditForm editFormResult = (EditForm) modelMap.get("editForm");
    Assertions.assertThat(editFormResult).isNotNull();
    Assertions.assertThat(editFormResult.getName()).isEqualTo(editFormName);

    List<?> licencesList = (List<?>) modelMap.get("licenseList");
    Assertions.assertThat(licencesList).isSameAs(licences);

    List<?> categoryList = (List<?>) modelMap.get("categoryList");
    Assertions.assertThat(categoryList).isSameAs(categories);

    List<?> organizationList = (List<?>) modelMap.get("organizationList");
    Assertions.assertThat(organizationList).isSameAs(organizations);

    Assertions.assertThat(Boolean.valueOf(modelMap.get("hideContributorId").toString())).isFalse();
    Assertions.assertThat(modelMap.get("message")).isNull();
    Assertions.assertThat(modelMap.get("messageType")).isNull();
    Assertions.assertThat(modelMap.get("metadataUrl")).isNull();
    Assertions.assertThat(Boolean.valueOf(modelMap.get("userCanEditDataset").toString())).isTrue();

    // When hideContributorId is true: hide contributor ids
    sut.setHideContributorId("true");
    // Create organization without contributor id
    organizations = createOrganizationList(ORG_ID_DEFAULT, null);
    Mockito.when(odrClient.getOrganizationsForUser(ckanUser, "create_dataset")).thenReturn(organizations);

    /* execute */
    sut.showForm("Test-dataset", editForm, bindingResult, request, response, model);

    /* assert */
    modelMap = model.asMap();

    Assertions.assertThat(modelMap.get("contributorIdSelectList")).asList().isEmpty();
    Assertions.assertThat(Boolean.valueOf(modelMap.get("hideContributorId").toString())).isTrue();

  }

  @Test
  public void submitForm_new() throws Exception
  {
    /* prepare */
    String titleMunged = "new-title-1";
    EditForm editForm = buildEditForm();
    // resources
    Resource res = new Resource();
    res.setName("res name");
    res.setDescription("res description");
    res.setUrl("res url");
    res.setIssued("2022-10-20");
    res.setModified("2022-11-222");
    editForm.setResources(List.of(res));

    MockActionResponse response = new MockActionResponse();
    MockActionRequest request = new MockActionRequest();

    de.seitenbau.govdata.odp.registry.model.User ckanUser = mockUser(request);

    mockOrganizationList(ORG_ID_DEFAULT, CONTRIBUTOR_ID_DEFAULT);

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
        metadata.getOwnerOrg(), CONTRIBUTOR_ID_DEFAULT);
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
    EditForm editForm = buildEditForm();

    MockActionResponse response = new MockActionResponse();
    MockActionRequest request = new MockActionRequest();

    de.seitenbau.govdata.odp.registry.model.User ckanUser = mockUser(request);

    mockOrganizationList(ORG_ID_DEFAULT, CONTRIBUTOR_ID_DEFAULT);

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
  public void submitForm_new_persist_authorization_error_de() throws Exception
  {
    /* prepare */
    String titleMunged = "new-title-1";
    EditForm editForm = buildEditForm();

    MockActionResponse response = new MockActionResponse();
    MockActionRequest request = new MockActionRequest();

    de.seitenbau.govdata.odp.registry.model.User ckanUser = mockUser(request);

    mockOrganizationList(ORG_ID_DEFAULT, CONTRIBUTOR_ID_DEFAULT);
    String msgEntity = "{"
        + "\"help\": \"http://example.com\","
        + " \"error\": {"
        + "\"__type\": \"Authorization Error\","
        + " \"message\": \"Zugriff verweigert: Benutzer datenbereitstellera hat keine Berechtigung diese"
        + " Gruppen zu bearbeiten\""
        + "},"
        + " \"success\": false"
        + "}";
    Response mockResponse =
        buildMockResponse(Status.FORBIDDEN, msgEntity);
    ClientErrorException e = new ClientErrorException(mockResponse);

    Metadata metadata = createMetadata(titleMunged);
    Mockito.when(odrClient.createMetadata()).thenReturn(metadata);
    Mockito.when(odrClient.persistMetadata(ckanUser, metadata)).thenThrow(e);

    /* execute */
    sut.submitForm(editForm, bindingResult, response, request);

    Mockito.verify(odrClient).persistMetadata(ckanUser, metadata);
    Mockito.verify(mockResponse, Mockito.times(1)).readEntity(String.class);
    Assertions.assertThat(response.getRenderParameter("message"))
        .isEqualTo("od.editform.save.error.forbidden.groups");
    Assertions.assertThat(editForm.getName()).isEqualTo(NAME_INDICATES_NEW_DATASET);
  }

  @Test
  public void submitForm_new_persist_authorization_error_de_reworded() throws Exception
  {
    /* prepare */
    String titleMunged = "new-title-1";
    EditForm editForm = buildEditForm();

    MockActionResponse response = new MockActionResponse();
    MockActionRequest request = new MockActionRequest();

    de.seitenbau.govdata.odp.registry.model.User ckanUser = mockUser(request);

    mockOrganizationList(ORG_ID_DEFAULT, CONTRIBUTOR_ID_DEFAULT);
    String msgEntity = "{"
        + "\"help\": \"http://example.com\","
        + " \"error\": {"
        + "\"__type\": \"Authorization Error\","
        + " \"message\": \"Zugriff verweigert: Gruppen: Benutzer datenbereitstellera hat keine Berechtigung"
        + " diese zu bearbeiten\""
        + "},"
        + " \"success\": false"
        + "}";
    Response mockResponse =
        buildMockResponse(Status.FORBIDDEN, msgEntity);
    ClientErrorException e = new ClientErrorException(mockResponse);

    Metadata metadata = createMetadata(titleMunged);
    Mockito.when(odrClient.createMetadata()).thenReturn(metadata);
    Mockito.when(odrClient.persistMetadata(ckanUser, metadata)).thenThrow(e);

    /* execute */
    sut.submitForm(editForm, bindingResult, response, request);

    Mockito.verify(odrClient).persistMetadata(ckanUser, metadata);
    Mockito.verify(mockResponse, Mockito.times(1)).readEntity(String.class);
    Assertions.assertThat(response.getRenderParameter("message"))
        .isEqualTo("od.editform.save.error.forbidden.groups");
    Assertions.assertThat(editForm.getName()).isEqualTo(NAME_INDICATES_NEW_DATASET);
  }

  @Test
  public void submitForm_new_persist_authorization_error_de_only_one_match_without_groups() throws Exception
  {
    /* prepare */
    String titleMunged = "new-title-1";
    EditForm editForm = buildEditForm();

    MockActionResponse response = new MockActionResponse();
    MockActionRequest request = new MockActionRequest();

    de.seitenbau.govdata.odp.registry.model.User ckanUser = mockUser(request);

    mockOrganizationList(ORG_ID_DEFAULT, CONTRIBUTOR_ID_DEFAULT);
    String msgEntity = "{"
        + "\"help\": \"http://example.com\","
        + " \"error\": {"
        + "\"__type\": \"Authorization Error\","
        + " \"message\": \"Zugriff verweigert: Benutzer datenbereitstellera hat keine Berechtigung diese Texte"
        + " zu bearbeiten\""
        + "},"
        + " \"success\": false"
        + "}";
    Response mockResponse =
        buildMockResponse(Status.FORBIDDEN, msgEntity);
    ClientErrorException e = new ClientErrorException(mockResponse);

    Metadata metadata = createMetadata(titleMunged);
    Mockito.when(odrClient.createMetadata()).thenReturn(metadata);
    Mockito.when(odrClient.persistMetadata(ckanUser, metadata)).thenThrow(e);

    /* execute */
    sut.submitForm(editForm, bindingResult, response, request);

    Mockito.verify(odrClient).persistMetadata(ckanUser, metadata);
    Mockito.verify(mockResponse, Mockito.times(1)).readEntity(String.class);
    Assertions.assertThat(response.getRenderParameter("message"))
        .isEqualTo("od.editform.save.error.forbidden");
    Assertions.assertThat(editForm.getName()).isEqualTo(NAME_INDICATES_NEW_DATASET);
  }

  @Test
  public void submitForm_new_persist_authorization_error_en() throws Exception
  {
    /* prepare */
    String titleMunged = "new-title-1";
    EditForm editForm = buildEditForm();

    MockActionResponse response = new MockActionResponse();
    MockActionRequest request = new MockActionRequest();

    de.seitenbau.govdata.odp.registry.model.User ckanUser = mockUser(request);

    mockOrganizationList(ORG_ID_DEFAULT, CONTRIBUTOR_ID_DEFAULT);
    String msgEntity = "{"
        + "\"help\": \"http://example.com\","
        + " \"error\": {"
        + "\"__type\": \"Authorization Error\","
        + " \"message\": \"Access denied: User datenbereitstellera not authorized to edit these groups\""
        + "},"
        + " \"success\": false"
        + "}";
    Response mockResponse =
        buildMockResponse(Status.FORBIDDEN, msgEntity);
    ClientErrorException e = new ClientErrorException(mockResponse);

    Metadata metadata = createMetadata(titleMunged);
    Mockito.when(odrClient.createMetadata()).thenReturn(metadata);
    Mockito.when(odrClient.persistMetadata(ckanUser, metadata)).thenThrow(e);

    /* execute */
    sut.submitForm(editForm, bindingResult, response, request);

    Mockito.verify(odrClient).persistMetadata(ckanUser, metadata);
    Mockito.verify(mockResponse, Mockito.times(1)).readEntity(String.class);
    Assertions.assertThat(response.getRenderParameter("message"))
        .isEqualTo("od.editform.save.error.forbidden.groups");
    Assertions.assertThat(editForm.getName()).isEqualTo(NAME_INDICATES_NEW_DATASET);
  }

  @Test
  public void submitForm_new_persist_authorization_error_message_null() throws Exception
  {
    /* prepare */
    String titleMunged = "new-title-1";
    EditForm editForm = buildEditForm();

    MockActionResponse response = new MockActionResponse();
    MockActionRequest request = new MockActionRequest();

    de.seitenbau.govdata.odp.registry.model.User ckanUser = mockUser(request);

    mockOrganizationList(ORG_ID_DEFAULT, CONTRIBUTOR_ID_DEFAULT);
    String msgEntity = "{"
        + "\"help\": \"http://example.com\","
        + " \"error\": {\"__type\": \"Authorization Error\"},"
        + " \"success\": false"
        + "}";
    Response mockResponse =
        buildMockResponse(Status.FORBIDDEN, msgEntity);
    ClientErrorException e = new ClientErrorException(mockResponse);

    Metadata metadata = createMetadata(titleMunged);
    Mockito.when(odrClient.createMetadata()).thenReturn(metadata);
    Mockito.when(odrClient.persistMetadata(ckanUser, metadata)).thenThrow(e);

    /* execute */
    sut.submitForm(editForm, bindingResult, response, request);

    Mockito.verify(odrClient).persistMetadata(ckanUser, metadata);
    Mockito.verify(mockResponse, Mockito.times(1)).readEntity(String.class);
    Assertions.assertThat(response.getRenderParameter("message"))
        .isEqualTo("od.editform.save.error.forbidden");
    Assertions.assertThat(editForm.getName()).isEqualTo(NAME_INDICATES_NEW_DATASET);
  }

  @Test
  public void submitForm_new_persist_authorization_error_message_parse_exception() throws Exception
  {
    /* prepare */
    String titleMunged = "new-title-1";
    EditForm editForm = buildEditForm();

    MockActionResponse response = new MockActionResponse();
    MockActionRequest request = new MockActionRequest();

    de.seitenbau.govdata.odp.registry.model.User ckanUser = mockUser(request);

    mockOrganizationList(ORG_ID_DEFAULT, CONTRIBUTOR_ID_DEFAULT);
    String msgEntityInvalidJson = "{"
        + "\"help\": \"http://example.com\","
        + " \"error\": {\"__type\": \"Authorization Error\"},"
        + " \"success\": false";
    Response mockResponse =
        buildMockResponse(Status.FORBIDDEN, msgEntityInvalidJson);
    ClientErrorException e = new ClientErrorException(mockResponse);

    Metadata metadata = createMetadata(titleMunged);
    Mockito.when(odrClient.createMetadata()).thenReturn(metadata);
    Mockito.when(odrClient.persistMetadata(ckanUser, metadata)).thenThrow(e);

    /* execute */
    sut.submitForm(editForm, bindingResult, response, request);

    Mockito.verify(odrClient).persistMetadata(ckanUser, metadata);
    Mockito.verify(mockResponse, Mockito.times(1)).readEntity(String.class);
    Assertions.assertThat(response.getRenderParameter("message"))
        .isEqualTo("od.editform.save.error.forbidden");
    Assertions.assertThat(editForm.getName()).isEqualTo(NAME_INDICATES_NEW_DATASET);
  }

  @Test
  public void submitForm_update_public() throws Exception
  {
    /* prepare */
    String name = "name-1";
    EditForm editForm = buildEditForm(name);

    MockActionResponse response = new MockActionResponse();
    MockActionRequest request = new MockActionRequest();

    de.seitenbau.govdata.odp.registry.model.User ckanUser = mockUser(request);

    mockOrganizationList(ORG_ID_DEFAULT, CONTRIBUTOR_ID_DEFAULT);

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
        metadata.getOwnerOrg(), CONTRIBUTOR_ID_DEFAULT);
  }

  @Test
  public void submitForm_update_private() throws Exception
  {
    /* prepare */
    String name = "name-1";
    EditForm editForm = buildEditForm(name);
    editForm.setPrivate(true);

    MockActionResponse response = new MockActionResponse();
    MockActionRequest request = new MockActionRequest();

    de.seitenbau.govdata.odp.registry.model.User ckanUser = mockUser(request);

    mockOrganizationList(ORG_ID_DEFAULT, CONTRIBUTOR_ID_DEFAULT);

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

  private Response buildMockResponse(Status status, String msgEntity)
  {
    Response mockResponse = mock(Response.class);
    Mockito.when(mockResponse.readEntity(String.class)).thenReturn(msgEntity);
    Mockito.when(mockResponse.getStatus()).thenReturn(status.getStatusCode());
    Mockito.when(mockResponse.getStatusInfo()).thenReturn(status);

    return mockResponse;
  }

  private EditForm buildEditForm()
  {
    return buildEditForm(NAME_INDICATES_NEW_DATASET);
  }

  private EditForm buildEditForm(String name)
  {
    EditForm editForm = new EditForm();
    editForm.setName(name);
    editForm.setTitle("New Title 1");
    editForm.setOrganizationId(ORG_ID_DEFAULT);
    editForm.setContributorId(CONTRIBUTOR_ID_DEFAULT);
    return editForm;
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

  private de.seitenbau.govdata.odp.registry.model.User mockUser(PortletRequest request)
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
    List<Organization> organizationList = createOrganizationList(organizationId, contributorID);
    Mockito.when(organizationCache.getOrganizationsSorted()).thenReturn(organizationList);
  }

  private List<Organization> createOrganizationList(String organizationId, String contributorID)
  {
    List<Organization> organizationList = new ArrayList<>();
    OrganizationBean orgBean = new OrganizationBean();
    orgBean.setId(organizationId);
    List<ExtraBean> extras = new ArrayList<>();
    ExtraBean contributorIdExtra = new ExtraBean();
    if (Objects.nonNull(contributorID))
    {
      contributorIdExtra.setKey(MetadataListExtraFields.CONTRIBUTOR_ID.getField());
      contributorIdExtra.setValue(new Gson().toJson(List.of(contributorID)));
      extras.add(contributorIdExtra);
    }
    orgBean.setExtras(extras);
    Organization org = new OrganizationImpl(orgBean);
    organizationList.add(org);
    return organizationList;
  }

  private List<Category> createCategoryList(String... groupIds)
  {
    List<Category> categories = new ArrayList<>();
    for (String groupId : groupIds)
    {
      GroupBean groupBean = new GroupBean();
      groupBean.setId(groupId);
      Category category = new CategoryImpl(groupBean);
      categories.add(category);
    }
    return categories;
  }

  private List<Licence> createLicenceList(String... licenceUrls)
  {
    List<Licence> licences = new ArrayList<>();
    for (String licenceUrl : licenceUrls)
    {
      LicenceBean licenceBean = new LicenceBean();
      licenceBean.setUrl(licenceUrl);
      Licence licence = new LicenceImpl(licenceBean);
      licences.add(licence);
    }
    return licences;
  }
}
