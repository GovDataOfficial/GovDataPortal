package de.seitenbau.govdata.odp.registry;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.apache.commons.lang3.ArrayUtils;
import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import de.seitenbau.govdata.odp.registry.ckan.ODRClientImpl;
import de.seitenbau.govdata.odp.registry.ckan.impl.ContactAddress;
import de.seitenbau.govdata.odp.registry.ckan.impl.MetadataImpl;
import de.seitenbau.govdata.odp.registry.ckan.json.LicenceBean;
import de.seitenbau.govdata.odp.registry.ckan.json.MetadataBean;
import de.seitenbau.govdata.odp.registry.common.CkanResource;
import de.seitenbau.govdata.odp.registry.common.TestBase;
import de.seitenbau.govdata.odp.registry.model.Category;
import de.seitenbau.govdata.odp.registry.model.Contact;
import de.seitenbau.govdata.odp.registry.model.FormatEnumType;
import de.seitenbau.govdata.odp.registry.model.GeoGranularityEnumType;
import de.seitenbau.govdata.odp.registry.model.Licence;
import de.seitenbau.govdata.odp.registry.model.LicenceConformance;
import de.seitenbau.govdata.odp.registry.model.Metadata;
import de.seitenbau.govdata.odp.registry.model.MetadataListExtraFields;
import de.seitenbau.govdata.odp.registry.model.MetadataStringExtraFields;
import de.seitenbau.govdata.odp.registry.model.RoleEnumType;
import de.seitenbau.govdata.odp.registry.model.Tag;
import de.seitenbau.govdata.odp.registry.model.User;
import de.seitenbau.govdata.odp.registry.model.exception.OpenDataRegistryException;


public class ODRClientTest extends TestBase
{
  private static final String TEST_TOKEN =
      "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJqdGkiOiJMU0UtM1E1c0xQTEVMQUl5S3Y0eWhxendhQUUzVy1Jcl9Wdzl0YWpieVVnIiwiaWF0IjoxNjc5NTYxNDE4fQ.MvJCspsvjzEkW3Nrtmf-59tMvs2PTr__oeRIydAPhhE";

  @Before
  public void beforeTest() throws Exception
  {
    setupConfigurationAndHttpServer();
    CkanResource.clearApiMethodsCalled();
  }

  @After
  public void tearDownHttpServer() throws Exception
  {
    stopHttpServer();
  }

  // ODR: listLicenses() - CKAN: license_list
  @Test
  public void listLicences()
  {
    /* execute */
    List<Licence> licences = odrClient.listLicenses();

    /* verify */
    Assertions.assertThat(licences).isNotNull();
    Assertions.assertThat(licences.size()).isEqualTo(52);
    Assertions.assertThat(CkanResource.getApiMethodsCalled().keySet())
        .containsExactlyInAnyOrder("listLicences");

    Licence licence = licences.get(0);
    // name matches field "id" in json response
    Assertions.assertThat(licence.getId()).isEqualTo("http://dcat-ap.de/def/licenses/official-work");
    Assertions.assertThat(licence.getTitle())
        .isEqualTo("Amtliches Werk, lizenzfrei nach §5 Abs. 1 UrhG (ältere DCAT-AP.de Version)");
    Assertions.assertThat(licence.getUrl()).isEqualTo("http://www.gesetze-im-internet.de/urhg/__5.html");
    Assertions.assertThat(licence.isActive()).isFalse();
    Assertions.assertThat(licence.getOdConformance()).isEqualTo(LicenceConformance.APPROVED.getValue());
    Assertions.assertThat(licence.getOsdConformance()).isEqualTo(LicenceConformance.APPROVED.getValue());
    Assertions.assertThat(licence.isOpen()).isTrue();

    licence = licences.get(1);
    // name matches field "id" in json response
    Assertions.assertThat(licence.getId()).isEqualTo("http://dcat-ap.de/def/licenses/other-freeware");
    Assertions.assertThat(licence.getTitle()).isEqualTo("Andere Freeware Lizenz");
    Assertions.assertThat(licence.getUrl()).isEmpty();
    Assertions.assertThat(licence.isActive()).isTrue();
    Assertions.assertThat(licence.getOdConformance()).isEqualTo(LicenceConformance.NOTREVIEWED.getValue());
    Assertions.assertThat(licence.getOsdConformance()).isEqualTo(LicenceConformance.NOTREVIEWED.getValue());
    Assertions.assertThat(licence.isOpen()).isFalse();
  }

  @Test
  public void listLicences_cached()
  {
    /* execute */
    List<Licence> licences = odrClient.listLicenses();
    List<Licence> licences2 = odrClient.listLicenses();

    /* assert */
    Assertions.assertThat(CkanResource.getApiMethodsCalled().keySet())
        .containsExactlyInAnyOrder("listLicences");
    Assertions.assertThat(licences).isNotNull().isSameAs(licences2);
  }

  @Test
  public void dateDeserialization_validValues()
  {
    //prepare
    String[] testDates = {"2015-11-10", "10.11.2015", "10.11.2015 2:10:55", "10.11.2015T2:10:55",
        "10.11.2015T2:10:55+02:00", "2017-08-16T11:01:06+02:00", "10.07.2015T2:10:55+0200",
        "2022-02-07T10:44:05.247974", "2022-02-07T10:44:05.247974+0100"};
    JsonNode jsonNode;
    MetadataBean metadata;

    //check if date has been parsed correctly
    for (String date : testDates)
    {
      jsonNode = this.getJsonNodeWithDate(date);

      metadata = ODRClientImpl.convert(jsonNode, MetadataBean.class);

      Assertions.assertThat(metadata.getMetadata_modified()).isNotNull();
      Assertions.assertThat(metadata.getMetadata_created()).isNotNull();

      metadata.getResources().stream().forEach(resource -> {
        Assertions.assertThat(resource.getIssued()).isNotNull();
        Assertions.assertThat(resource.getCreated()).isNotNull();
        Assertions.assertThat(resource.getLast_modified()).isNotNull();
        Assertions.assertThat(resource.getModified()).isNotNull();
      });
    }
  }

  @Test
  public void dateDeserialization_invalidValues()
  {
    //prepare
    String date = "Wrong date";
    JsonNode jsonNode =  this.getJsonNodeWithDate(date);
    MetadataBean metadata;

    //execute
    metadata = ODRClientImpl.convert(jsonNode, MetadataBean.class);

    //check if date could not be parsed
    Assertions.assertThat(metadata.getMetadata_modified()).isNull();
    Assertions.assertThat(metadata.getMetadata_created()).isNull();
    metadata.getResources().stream().forEach(resource -> {
      Assertions.assertThat(resource.getIssued()).isNull();
      Assertions.assertThat(resource.getCreated()).isNull();
      Assertions.assertThat(resource.getLast_modified()).isNull();
      Assertions.assertThat(resource.getModified()).isNull();
    });
  }

  // ODR: getMetadata() - CKAN: package_show
  @Test
  public void getMetadata() throws OpenDataRegistryException
  {
    Metadata metadata = odrClient.getMetadata(null, "13dfb16a-c4f1-36b4-eda2-01ff5b1b294f");

    Assertions.assertThat(metadata).isNotNull();
    Assertions.assertThat(CkanResource.getApiMethodsCalled().keySet())
        .containsExactlyInAnyOrder("showMetadata");

    // Tests if subset of response data is correct
    Assertions.assertThat(metadata.getAuthor()).isEqualTo("Landesamt f\u00fcr Geologie und Bergbau");

    // Number of "groups"
    Assertions.assertThat(metadata.getCategories()).hasSize(2);
    // 2 contacts (author and maintainer)
    Assertions.assertThat(metadata.getContacts()).hasSize(2);
    // 3 resources
    Assertions.assertThat(metadata.getResources()).hasSize(3);
    // No subgroups in response
    Assertions.assertThat(metadata.getTags()).hasSize(21);

    // Rating count from dataset_response.json "ratings_count"
    Assertions.assertThat(metadata.getRatingCount()).isEqualTo(0);

    Assertions.assertThat(metadata.isOpen()).isFalse();
  }

  @Test
  public void getMetadata_availabilityMapping() throws OpenDataRegistryException
  {
    //prepare
    final String stableReourceId = "eadc92ac-dfd3-4860-a508-7ada3a4c6aaf";
    final String availableReourceId = "b4db736e-46ae-406d-91a4-99d45942a700";

    //execute
    Metadata metadata = odrClient.getMetadata(null, "metadata_max");

    Assertions.assertThat(metadata).isNotNull();
    Assertions.assertThat(CkanResource.getApiMethodsCalled().keySet())
        .containsExactlyInAnyOrder("showMetadata");

    // Tests if subset of response data is correct
    Assertions.assertThat(metadata.getAuthor()).isEqualTo("Landesamt f\u00fcr Geologie und Bergbau");

    // 3 resources
    Assertions.assertThat(metadata.getResources()).hasSize(3);

    metadata.getResources().stream().forEach(resource -> {

      switch(resource.getId()) 
      {
        // only planned_availability is provided 
        case stableReourceId:
          Assertions.assertThat(resource.getAvailability())
          .isEqualTo("http://publications.europa.eu/resource/authority/planned-availability/STABLE");
          // planned_availability has to be set to null after the mapping
          Assertions.assertThat(resource.getPlannedAvailability()).isNull();
        break;
        case availableReourceId:
          // only availability is already provided
          Assertions.assertThat(resource.getAvailability())
          .isEqualTo("http://publications.europa.eu/resource/authority/planned-availability/AVAILABLE");
          // planned_availability has to be set to null after the mapping
          Assertions.assertThat(resource.getPlannedAvailability()).isNull();
        break;
        default:
          // planned_availability and availability are provided
          Assertions.assertThat(resource.getAvailability())
          .isEqualTo("http://publications.europa.eu/resource/authority/planned-availability/TEMPORARY");
          // planned_availability is provided and should not be null
          Assertions.assertThat(resource.getPlannedAvailability()).isNotNull();
      }
    });

  }

  // ODR: getJsonLdMetadata - CKAN: dcat_dataset_show
  @Test
  public void getJsonLdMetadata()
  {
    String result = odrClient.getJsonLdMetadata(null, "13dfb16a-c4f1-36b4-eda2-01ff5b1b294f",
        "http://test-portal.com/dataset", "http://test-portal.com/results");

    Assertions.assertThat(result).isNotNull();
    Assertions.assertThat(CkanResource.getApiMethodsCalled().keySet())
        .containsExactlyInAnyOrder("showDcatDataset");

    Assertions.assertThat(result).contains("\"@context\":");
    Assertions.assertThat(result).contains("schema:");
    Assertions.assertThat(result).contains("Metadaten zur WMS Kartenebene");
    Assertions.assertThat(result).contains("http://test-portal.com/dataset");
    Assertions.assertThat(result).contains("http://test-portal.com/results");
  }

  // ODR: getDcatDataset - CKAN: dcat_dataset_show
  @Test
  public void getDcatDataset_XML()
  {
    JsonNode result = odrClient.getDcatDataset(null, "13dfb16a-c4f1-36b4-eda2-01ff5b1b294f",
        FormatEnumType.XML, ArrayUtils.EMPTY_STRING_ARRAY);

    Assertions.assertThat(result).isNotNull();
    Assertions.assertThat(CkanResource.getApiMethodsCalled().keySet())
        .containsExactlyInAnyOrder("showDcatDataset");

    String xmlString = result.path("result").asText();
    Assertions.assertThat(xmlString).contains("<rdf:RDF");
    Assertions.assertThat(xmlString).contains(
        "<dcat:Dataset rdf:about=\"https://www.opendata-hro.de/dataset/a4bc37fd-6e7c-450a-b783-5bc201174d3a\">");
    Assertions.assertThat(xmlString).contains("Karte nach Schmettau 1788");
    Assertions.assertThat(xmlString).contains("<dcat:keyword>karte</dcat:keyword>");
    Assertions.assertThat(xmlString).contains("<dcat:Distribution");
  }

  // ODR: listCategories() - CKAN: group_list
  @Test
  public void listCategories()
  {
    List<Category> categories = odrClient.listCategories();

    Assertions.assertThat(categories).isNotNull();
    Assertions.assertThat(CkanResource.getApiMethodsCalled().keySet())
        .containsExactlyInAnyOrder("listGroups");
    // 14 different groups
    Assertions.assertThat(categories.size()).isEqualTo(14);

    for (Category category : categories)
    {
      Assertions.assertThat(category.getType()).isEqualTo("group");
      Assertions.assertThat(category.getCount()).isGreaterThanOrEqualTo(0);
    }
  }

  // ODR: findUser() - CKAN: user_show
  @Test
  public void userShow()
  {
    User user = odrClient.findUser("bremen");

    Assertions.assertThat(user).isNotNull();
    Assertions.assertThat(CkanResource.getApiMethodsCalled().keySet())
        .containsExactlyInAnyOrder("showUser", "getTokenList", "createApiToken");
    Assertions.assertThat(user.getName()).isEqualTo("bremen");
    Assertions.assertThat(user.getApiToken()).isEqualTo(TEST_TOKEN);
  }

  @Test
  public void listTags()
  {
    List<Tag> tags = odrClient.listTags();

    Assertions.assertThat(tags).isNotNull();
    Assertions.assertThat(CkanResource.getApiMethodsCalled().keySet())
    .containsExactlyInAnyOrder("listTags");
  }

  @Test
  public void getTag()
  {
    Tag tag = odrClient.getTag("Ozon");

    Assertions.assertThat(tag).isNotNull();
    Assertions.assertThat(CkanResource.getApiMethodsCalled().keySet())
        .containsExactlyInAnyOrder("showTag");
  }

  @Test
  public void status()
  {
    String status = odrClient.status();

    Assertions.assertThat(status).isEqualTo("{\"success\":true}");
    Assertions.assertThat(CkanResource.getApiMethodsCalled().keySet())
    .containsExactlyInAnyOrder("status");
  }

  @Test
  public void createMetadata()
  {
    Metadata result = odrClient.createMetadata();

    Assertions.assertThat(result).isNotNull();
    Assertions.assertThat(result.getId()).isNull();
    Assertions.assertThat(result.getTitle()).isNull();
    Assertions.assertThat(result.getResources()).isEmpty();
  }

  @Test
  public void persistMetadata()
  {
    MetadataImpl impl = (MetadataImpl) odrClient.createMetadata();

    impl.setTitle("Test Create Metadata V");
    impl.setExtraList(MetadataListExtraFields.POLITICAL_GEOCODING_URI,
        Arrays.asList("sumpfgebiete"));
    impl.setExtraString(MetadataStringExtraFields.POLITICAL_GEOCODING_LEVEL_URI,
        GeoGranularityEnumType.CITY.toField());
    impl.setModified(new Date());
    impl.setNotes("Simple Metadata for testing.");
    impl.setPublished(new Date());
    impl.setUrl("http://www.fokus.fraunhofer.de/elan");

    try
    {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");
      impl.setTemporalCoverageFrom(sdf.parse("2012-06-01 00:00:00"));
      impl.setTemporalCoverageTo(sdf.parse("2013-06-01 00:00:00"));
    }
    catch (ParseException e)
    {
      Assertions.fail(e.getMessage());
    }

    LicenceBean bean = new LicenceBean();
    bean.setTitle("Creative Commons Attribution Share-Alike");
    bean.setId("cc-by-sa");
    bean.setUrl("http://creativecommons.org/licenses/by-sa/3.0/de");

    Contact publisher = impl.getContact(RoleEnumType.PUBLISHER);
    ContactAddress address = publisher.getAddress();
    address.setStreet("Kaiserin-Augusta-Allee 31");
    address.setZIP("10589");
    address.setCity("Berlin");
    address.setCountry("Deutschland");
    publisher.setEmail("publisher@fokus.fraunhofer.de");
    publisher.setName("I'm the Publisher");

    impl.setExtra("extra1", "value for extra 1");
    impl.setExtra("extra2", "value for extra 2");

    impl.newTag("test-create-4");

    User user = odrClient.findUser("sim");

    try
    {
      boolean success = odrClient.persistMetadata(user, impl);
      Assertions.assertThat(success).isTrue();
    }
    catch (OpenDataRegistryException e)
    {
      Assertions.fail(e.getMessage());
    }

    Assertions.assertThat(CkanResource.getApiMethodsCalled().keySet())
        .containsExactlyInAnyOrder("createMetadata", "showUser", "getTokenList", "createApiToken");
    JsonNode params = CkanResource.getApiMethodsCalled().get("createMetadata");
    Assertions.assertThat(params.get("title").textValue()).isEqualTo(impl.getTitle());
    Assertions.assertThat(params.get("notes").textValue()).isEqualTo(impl.getNotes());
    Assertions
        .assertThat(
            StreamSupport.stream(params.get("tags").spliterator(), false).map(t -> t.get("name").textValue())
            .collect(Collectors.toList()))
        .containsExactlyInAnyOrder(impl.getTags().stream().map(t -> t.getName()).toArray(String[]::new));
  }

  @Test
  public void createUser()
  {
    /* prepare */
    String name = "testuser123";

    /* execute */
    User result = odrClient.createUser(name, name + "@ogdd.de", name);

    /* verify */
    Assertions.assertThat(result).isNotNull();
    Assertions.assertThat(CkanResource.getApiMethodsCalled().keySet())
        .containsExactlyInAnyOrder("createUser", "getTokenList", "createApiToken");
    Assertions.assertThat(result.getDisplayName()).isEqualTo("created");
    Assertions.assertThat(result.getName()).isEqualTo(name);
  }

  @Test
  public void renameUser()
  {
    String name = "bremen";
    String newName = "testnutzer14";

    User user = odrClient.findUser(name);

    Assertions.assertThat(user).isNotNull();
    Assertions.assertThat(user.getName()).isEqualTo(name);
    Assertions.assertThat(user.getId()).isEqualTo(user.getId());

    User renamedUser = odrClient.renameUser(user, newName);

    Assertions.assertThat(CkanResource.getApiMethodsCalled().keySet())
        .containsExactlyInAnyOrder("updateUser", "showUser", "createApiToken", "getTokenList");
    Assertions.assertThat(renamedUser.getName()).isEqualTo(newName);
    Assertions.assertThat(renamedUser.getId()).isEqualTo(user.getId());
  }

  @Test
  public void createApiTokenForUser()
  {
    String userName = "userName";
    String tokenName = "tokenName";
    String token = odrClient.createApiTokenForUser(userName, tokenName);
    Assertions.assertThat(token).isEqualTo(TEST_TOKEN);
    Assertions.assertThat(CkanResource.getApiMethodsCalled().keySet())
        .containsExactlyInAnyOrder("createApiToken");
    JsonNode params = CkanResource.getApiMethodsCalled().get("createApiToken");
    Assertions.assertThat(params.get("name").textValue()).isEqualTo(tokenName);
    Assertions.assertThat(params.get("user").textValue()).isEqualTo(userName);

  }

  @Test
  public void revokeApiTokenById()
  {
    String tokenId = "tokenId";
    odrClient.revokeApiTokenById(tokenId);

    Assertions.assertThat(CkanResource.getApiMethodsCalled().keySet())
        .containsExactlyInAnyOrder("revokeApiToken");
    JsonNode params = CkanResource.getApiMethodsCalled().get("revokeApiToken");
    Assertions.assertThat(params.get("jti").textValue()).isEqualTo(tokenId);
  }

  private JsonNode getJsonNodeWithDate(String date)
  {

    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode jsonNode = objectMapper.createObjectNode();

    ((ObjectNode) jsonNode).put("id", "11a18249-447c-4fa6-89b5-9c5a239af94b");
    ((ObjectNode) jsonNode).put("metadata_modified", date);
    ((ObjectNode) jsonNode).put("metadata_created", date);
    ObjectNode resourcesNode = objectMapper.createObjectNode();
    resourcesNode.put("created", date);
    resourcesNode.put("issued", date);
    resourcesNode.put("modified", date);
    resourcesNode.put("last_modified", date);

    ArrayNode arrayNode = objectMapper.createArrayNode();
    arrayNode.add(resourcesNode);
    ((ObjectNode) jsonNode).set("resources", arrayNode);

    return jsonNode;
  }
}
