/**
 * Copyright (c) 2012, 2013 Fraunhofer Institute FOKUS
 *
 * This file is part of Open Data Platform.
 *
 * Open Data Platform is free software: you can redistribute it and/or modify it under the terms of
 * the GNU Affero General Public License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 * 
 * Open Data Plaform is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with Open Data
 * Platform. If not, see <http://www.gnu.org/licenses/agpl-3.0>.
 */

package de.seitenbau.govdata.odp.registry;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import de.seitenbau.govdata.odp.registry.ckan.ODRClientImpl;
import de.seitenbau.govdata.odp.registry.ckan.impl.ContactAddress;
import de.seitenbau.govdata.odp.registry.ckan.impl.MetadataImpl;
import de.seitenbau.govdata.odp.registry.ckan.json.LicenceBean;
import de.seitenbau.govdata.odp.registry.ckan.json.MetadataBean;
import de.seitenbau.govdata.odp.registry.common.TestBase;
import de.seitenbau.govdata.odp.registry.model.Category;
import de.seitenbau.govdata.odp.registry.model.Contact;
import de.seitenbau.govdata.odp.registry.model.FormatEnumType;
import de.seitenbau.govdata.odp.registry.model.GeoGranularityEnumType;
import de.seitenbau.govdata.odp.registry.model.Licence;
import de.seitenbau.govdata.odp.registry.model.Metadata;
import de.seitenbau.govdata.odp.registry.model.MetadataListExtraFields;
import de.seitenbau.govdata.odp.registry.model.MetadataStringExtraFields;
import de.seitenbau.govdata.odp.registry.model.RoleEnumType;
import de.seitenbau.govdata.odp.registry.model.Tag;
import de.seitenbau.govdata.odp.registry.model.User;
import de.seitenbau.govdata.odp.registry.model.exception.OpenDataRegistryException;
import de.seitenbau.govdata.odp.registry.queries.Query;
import de.seitenbau.govdata.odp.registry.queries.QueryModeEnumType;
import de.seitenbau.govdata.odp.registry.queries.QueryResult;


public class ODRClientTest extends TestBase
{
  private final Logger log = LoggerFactory.getLogger(getClass());

  @Before
  public void beforeTest() throws Exception
  {
    setupConfigurationAndHttpServer();
  }

  @After
  public void tearDownHttpServer() throws Exception
  {
    stopHttpServer();
  }
  

  /*
   * Most important tests: listLicences(), getMetadata(), rateMetadata(), queryMetadata(),
   * listCategories(), userShow()
   */

  // ODR: listLicenses() - CKAN: license_list
  @Test
  public void listLicences()
  {
    List<Licence> licences = odrClient.listLicenses();

    Assertions.assertThat(licences).isNotNull();
    Assertions.assertThat(licences.size()).isEqualTo(37);

    Licence licence = licences.get(0);

    // name matches field "id" in json response
    Assertions.assertThat(licence.getName()).isEqualTo("dl-de-by-1.0");
    Assertions.assertThat(licence.getTitle()).isEqualTo("Datenlizenz Deutschland Namensnennung");
    Assertions.assertThat(licence.getUrl()).isEqualTo("https://www.govdata.de/dl-de/by-1-0");
    Assertions.assertThat(licence.getOther()).isNull();
    Assertions.assertThat(licence.isActive()).isTrue();
    Assertions.assertThat(licence.isDomainContent()).isTrue();
    Assertions.assertThat(licence.isDomainData()).isTrue();
    Assertions.assertThat(licence.isDomainSoftware()).isFalse();
    Assertions.assertThat(licence.isOkdCompliant()).isTrue();
    Assertions.assertThat(licence.isOpen()).isTrue();
    Assertions.assertThat(licence.isOsiCompliant()).isFalse();
  }

  @Test
  public void listLicences_cached()
  {
    /* execute */
    List<Licence> licences = odrClient.listLicenses();
    List<Licence> licences2 = odrClient.listLicenses();

    /* assert */
    Assertions.assertThat(licences).isNotNull().isSameAs(licences2);
  }

  private JsonNode getJsonNodeWithDate(String date) {

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

    String xmlString = result.path("result").asText();
    Assertions.assertThat(xmlString).contains("<rdf:RDF");
    Assertions.assertThat(xmlString).contains(
        "<dcat:Dataset rdf:about=\"https://www.opendata-hro.de/dataset/a4bc37fd-6e7c-450a-b783-5bc201174d3a\">");
    Assertions.assertThat(xmlString).contains("Karte nach Schmettau 1788");
    Assertions.assertThat(xmlString).contains("<dcat:keyword>karte</dcat:keyword>");
    Assertions.assertThat(xmlString).contains("<dcat:Distribution");
  }

  // ODR: queryMetadata() - CKAN: package_search
  @Test
  public void queryMetadata()
  {
    Query query = new Query();
    QueryResult<Metadata> result = odrClient.queryMetadata(query);
    List<Metadata> metadata = result.getResult();

    Assertions.assertThat(result).isNotNull();
    Assertions.assertThat(metadata).isNotNull();
    // Query has no facets
    Assertions.assertThat(result.getFacets().size()).isEqualTo(0);
    Assertions.assertThat(metadata.size()).isEqualTo(10);
  }

  // ODR: listCategories() - CKAN: group_list
  @Test
  public void listCategories()
  {
    List<Category> categories = odrClient.listCategories();

    Assertions.assertThat(categories).isNotNull();
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
    Assertions.assertThat(user.getName()).isEqualTo("bremen");
  }

  @Test
  public void listTags()
  {
    List<Tag> tags = odrClient.listTags();

    Assertions.assertThat(tags).isNotNull();
  }

  @Test
  public void querySearchTerm()
  {
    Query query = new Query();
    query.setSearchterm("Ulm");
    QueryResult<Metadata> result = odrClient.queryMetadata(query);

    if (result.isSuccess())
    {
      List<Metadata> metadata = result.getResult();

      for (Metadata set : metadata)
      {
        log.info("metadata: {}", set.getTitle());
      }
    }
    else
    {
      log.info("no success: {}", result.getErrorMessage());
    }

    Assertions.assertThat(result.isSuccess()).isTrue();
  }

  @Test
  public void extendedQueryMetadata()
  {
    Query query = new Query(QueryModeEnumType.EXTENDED);
    // query.getTypes().add(MetadataEnumType.APPLICATION);
    // query.setSearchterm("Bodenrichtwerte");
    // query.getCategories().add("geo");
    // query.setIsOpen(true);
    // List<Category> categories = odr.listCategories();
    // for (Category category : categories) {
    // query.getCategories().add(category.getName());
    // }
    // query.getTypes().add(MetadataEnumType.DATASET);
    query.setMax(90);
    // query.setOffset(75);
    // query.getFormats().add("ASCII");
    // query.getSortFields().add("dates desc");

    QueryResult<Metadata> result = odrClient.queryMetadata(query);
    List<Metadata> metadata = result.getResult();
    log.info("metadata count: {}", metadata.size());

    for (Metadata set : metadata)
    {
      log.info("metadata typ {}: {} (open: {})",
          set.getType().toField(), set.getTitle(), String.valueOf(set.isOpen()));

      for (Contact contact : set.getContacts())
      {
        log.info("{}: {}", contact.getRole().getDisplayName(), contact.getName());
      }
      // log.info("created: {}", set.getCreatedAsString("dd.MM.yyyy"));
      // log.info("published: {}", set.getPublishedAsString("dd.MM.yyyy"));
      // log.info("modified: {}", set.getModifiedAsString("dd.MM.yyyy"));
      // String averageRating = set.getExtra("rateSum");
      // log.info("average rating is {}", averageRating);
    }

    Assertions.assertThat(metadata).isNotNull();
  }

  @Test
  public void getTag()
  {
    Tag tag = odrClient.getTag("Ozon");

    Assertions.assertThat(tag).isNotNull();
  }

  @Test
  public void status()
  {
    String status = odrClient.status();

    log.info("status of open data registry is: {}", status);
  }

  @Test
  public void createMetadata()
  {
    MetadataImpl impl = (MetadataImpl) odrClient.createMetadata();
    impl.setTitle("Test Create Metadata V");

    impl.setExtraList(MetadataListExtraFields.POLITICAL_GEOCODING_URI, Arrays.asList("sumpfgebiete"));
    impl.setExtraString(MetadataStringExtraFields.POLITICAL_GEOCODING_LEVEL_URI, GeoGranularityEnumType.CITY.toField());
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
      e.printStackTrace();
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
      odrClient.persistMetadata(user, impl);
    }
    catch (OpenDataRegistryException e)
    {
      e.printStackTrace();
    }
  }

  @Test
  public void createUser()
  {
    String name = "testnutzer13";
    User user = odrClient.findUser(name);

    if (user == null)
    {
      log.info("User {} not found, creating...", name);
      user = odrClient.createUser(name, name + "@ogdd.de", name);
      if (user != null)
      {
        log.info("... done");
      }
    }
    else
    {
      log.info("User {} already exists", name);
    }
  }

  @Test
  public void listRelationships()
  {
    ((ODRClientImpl) odrClient).listRelationships("de-hh-inspire-flurstueck", null);
  }

  @Test
  public void fetchAll()
  {
    Query query = new Query();
    query.setMax(50);
    long start = System.currentTimeMillis();
    QueryResult<Metadata> result = odrClient.queryMetadata(query);
    long count = result.getCount();

    if (result.isSuccess())
    {
      long pages = result.getCount() / 50;

      for (int i = 1; i < pages; i++)
      {
        query.setPageoffset(i);
        result = odrClient.queryMetadata(query);
      }
    }
    log.info("fetched {} metadata in {} ms", count, System.currentTimeMillis() - start);
    System.out.println("fetched " + count + " metadata in " + (System.currentTimeMillis() - start) + " ms");
  }

  @Test
  public void getLatestDatasets()
  {
    Query query = new Query();

    query.getSortFields().add("metadata_modified desc");
    query.setMax(5);

    QueryResult<Metadata> result = odrClient.queryDatasets(query);

    Assertions.assertThat(result).isNotNull();
    Assertions.assertThat(result.isSuccess()).isTrue();
  }

}
