package de.seitenbau.govdata.odp.registry.ckan.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.seitenbau.govdata.date.DateUtil;
import de.seitenbau.govdata.odp.registry.ODRClient;
import de.seitenbau.govdata.odp.registry.ckan.Constants;
import de.seitenbau.govdata.odp.registry.ckan.json.ExtraBean;
import de.seitenbau.govdata.odp.registry.ckan.json.GroupBean;
import de.seitenbau.govdata.odp.registry.ckan.json.LicenceBean;
import de.seitenbau.govdata.odp.registry.ckan.json.MetadataBean;
import de.seitenbau.govdata.odp.registry.model.Category;
import de.seitenbau.govdata.odp.registry.model.Contact;
import de.seitenbau.govdata.odp.registry.model.Licence;
import de.seitenbau.govdata.odp.registry.model.LicenceConformance;
import de.seitenbau.govdata.odp.registry.model.MetadataEnumType;
import de.seitenbau.govdata.odp.registry.model.MetadataListExtraFields;
import de.seitenbau.govdata.odp.registry.model.MetadataStringExtraFields;
import de.seitenbau.govdata.odp.registry.model.Resource;
import de.seitenbau.govdata.odp.registry.model.RoleEnumType;
import de.seitenbau.govdata.odp.registry.model.Tag;
import de.seitenbau.govdata.odp.registry.model.exception.OpenDataRegistryException;

@RunWith(MockitoJUnitRunner.class)
public class MetadataImplTest {
  private static final String DATE_WITH_TIMEZONE_VALID = "2.10.2015T3:30:55";

  private static final String DATE_WITH_TIMEZONE_WITH_HOURS_VALID = DATE_WITH_TIMEZONE_VALID + "+02:00";

  private static final String COVERAGEFROM_VALID = "2.10.2015 3:30:55";

  private static final String COVERAGETO_VALID = "12.11.2015 10:20:33";

  private static final String DATE_PATTERN = "dd.MM.yyyy HH:mm:ss";

  private static final String DATE_PATTERN_WITH_TIMEZONE = "dd.MM.yyyy'T'HH:mm:ss";

  private static final String DATE_PATTERN_WITH_TIMEZONE_AND_HOURS = "dd.MM.yyyy'T'HH:mm:ssX";

  private static final ObjectMapper OM = new ObjectMapper();

  @Mock
  private ODRClient odrClient;

  private MetadataImpl target;

  @Before
  public void setup() throws Exception
  {
    // Setup odrClient Mock
    expectListLicences(LicenceConformance.APPROVED.getValue());
  }

  @Test
  public void getIdentifierWithFallback_null() throws Exception
  {
    /* prepare */
    MetadataBean metadataBean = createDefaultMetadataBean();

    /* execute */
    target = new MetadataImpl(metadataBean, odrClient);

    /* verify */
    assertThat(target.getIdentifierWithFallback()).isNull();
  }

  @Test
  public void getIdentifierWithFallback_onlyMetadataId() throws Exception
  {
    /* prepare */
    MetadataBean metadataBean = createDefaultMetadataBean();
    metadataBean.setId("ID");

    /* execute */
    target = new MetadataImpl(metadataBean, odrClient);

    /* verify */
    assertThat(target.getIdentifierWithFallback()).isEqualTo("ID");
  }

  @Test
  public void getIdentifierWithFallback_MetadataId_and_Guid() throws Exception
  {
    /* prepare */
    MetadataBean metadataBean = createDefaultMetadataBean();
    metadataBean.setId("ID");
    metadataBean.getExtras().add(
        createExtraBean(MetadataStringExtraFields.GUID.getField(), "guid"));

    /* execute */
    target = new MetadataImpl(metadataBean, odrClient);

    /* verify */
    assertThat(target.getIdentifierWithFallback()).isEqualTo("guid");
  }

  @Test
  public void getIdentifierWithFallback_MetadataId_Guid_and_Identifier() throws Exception
  {
    /* prepare */
    MetadataBean metadataBean = createDefaultMetadataBean();
    metadataBean.setId("ID");
    metadataBean.getExtras().add(
        createExtraBean(MetadataStringExtraFields.GUID.getField(), "guid"));
    metadataBean.getExtras().add(
        createExtraBean(MetadataStringExtraFields.IDENTIFIER.getField(), "identifier"));

    /* execute */
    target = new MetadataImpl(metadataBean, odrClient);

    /* verify */
    assertThat(target.getIdentifierWithFallback()).isEqualTo("identifier");
  }

  @Test
  public void parsingDate_valid_datetime() throws Exception {
    /* prepare */
    Date expectedFrom = parseDate(COVERAGEFROM_VALID);
    Date expectedTo = parseDate(COVERAGETO_VALID);

    /* execute */
    target = new MetadataImpl(createDefaultMetadataBean(), odrClient);

    /* verify */
    assertThat(target.getTemporalCoverageFrom()).isEqualToIgnoringMillis(expectedFrom);
    assertThat(target.getTemporalCoverageTo()).isEqualToIgnoringMillis(expectedTo);
  }

  @Test
  public void parsingDate_valid_datetime_with_timezone_utc() throws Exception {
    /* prepare */
    Date expectedFrom = parseDate(DATE_WITH_TIMEZONE_VALID, DATE_PATTERN_WITH_TIMEZONE);
    Date expectedTo = parseDate(DATE_WITH_TIMEZONE_VALID, DATE_PATTERN_WITH_TIMEZONE);

    /* execute */
    target = new MetadataImpl(createDefaultMetadataBean(
            DATE_WITH_TIMEZONE_VALID, DATE_WITH_TIMEZONE_VALID), odrClient);

    /* verify */
    assertThat(target.getTemporalCoverageFrom()).isEqualToIgnoringMillis(expectedFrom);
    assertThat(target.getTemporalCoverageTo()).isEqualToIgnoringMillis(expectedTo);
  }

  @Test
  public void parsingDate_valid_datetime_with_timezone_plusTwoHours() throws Exception {
    /* prepare */
    Date expectedFrom = parseDate(DATE_WITH_TIMEZONE_WITH_HOURS_VALID, DATE_PATTERN_WITH_TIMEZONE_AND_HOURS);
    Date expectedTo = parseDate(DATE_WITH_TIMEZONE_WITH_HOURS_VALID, DATE_PATTERN_WITH_TIMEZONE_AND_HOURS);

    /* execute */
    target = new MetadataImpl(createDefaultMetadataBean(
            DATE_WITH_TIMEZONE_WITH_HOURS_VALID, DATE_WITH_TIMEZONE_WITH_HOURS_VALID), odrClient);

    /* verify */
    assertThat(target.getTemporalCoverageFrom()).isEqualToIgnoringMillis(expectedFrom);
    assertThat(target.getTemporalCoverageTo()).isEqualToIgnoringMillis(expectedTo);
  }

  @Test
  public void parsingDate_invalid_datetime() throws Exception {
    /* prepare */
    MetadataBean metadataBean = createMetadataBeanWithInvalidTemporalCoverage();

    /* execute */
    target = new MetadataImpl(metadataBean, odrClient);

    /* verify */
    assertThat(target.getTemporalCoverageFrom()).isNull();
    assertThat(target.getTemporalCoverageTo()).isNull();
  }

  @Test
  public void readMetadataAndVerify() throws IOException, ParseException {
    MetadataImpl metadataImpl = createMetadataImplFromJson();

    // Check all values
    assertThat(metadataImpl.getId()).isEqualTo("c9e92d9b-5437-47bb-9f64-b3f9400d8e53");
    assertThat(metadataImpl.getName()).isEqualTo("metadata_max");
    assertThat(metadataImpl.getTitle()).isEqualTo("Naturräume Geest und Marsch");
    assertThat(metadataImpl.getUrl()).isEqualTo("https://www.govdata.de/web/guest/daten/-/details/naturraume-geest-und-marsch3");
    assertThat(metadataImpl.getNotes()).isEqualTo("Die Zuordnung des Hamburger Stadtgebietes zu den Naturräumen Geest und Marsch wird dargestellt. Die Karte ist auch als Hilfskarte für den Kontext -Einbau Ersatzbaustoffe- zu verstehen. Hier gibt es unterschiedliche Richtlinien je nachdem, ob ein Bauvorhaben in bzw. auf Geest- oder Marsch-Boden geplant ist. Die Daten werden als WMS-Darstellungsdienst und als WFS-Downloaddienst bereitgestellt.");

    assertThat(metadataImpl.getMetadataModified()).isNotNull();
    assertThat(metadataImpl.getModified()).isNotNull();
    assertThat(metadataImpl.getPublished()).isNotNull();

    assertThat(metadataImpl.getTemporalCoverageFrom()).isNotNull();
    assertThat(metadataImpl.getTemporalCoverageTo()).isNotNull();

    assertThat(metadataImpl.getExtraList(MetadataListExtraFields.GEOCODING_TEXT)).containsExactly("Eine Stadt");
    assertThat(metadataImpl.getExtraList(MetadataListExtraFields.CONTRIBUTOR_ID)).containsExactly("http://dcat-ap.de/def/contributors/transparenzportalHamburg");

    assertThat(metadataImpl.getModifiedAsString("HH:mm:ss")).isEqualTo("11:10:00");
    assertThat(metadataImpl.getPublishedAsString("HH:mm:ss")).isEqualTo("10:10:00");

    assertThat(metadataImpl.getExtraList(MetadataListExtraFields.POLITICAL_GEOCODING_URI)).containsExactly("http://dcat-ap.de/def/politicalGeocoding/regionalKey/020000000000", "http://dcat-ap.de/def/politicalGeocoding/stateKey/02");
    assertThat(metadataImpl.getExtraString(MetadataStringExtraFields.POLITICAL_GEOCODING_LEVEL_URI)).isEqualTo("http://dcat-ap.de/def/politicalGeocoding/Level/state");
    assertThat(metadataImpl.getExtraString(MetadataStringExtraFields.SPATIAL)).isEqualTo("{\"type\":\"Polygon\",\"coordinates\":[[[10.3263,53.3949],[10.3263,53.9641],[8.4205,53.9641],[8.4205,53.3949],[10.3263,53.3949]]]}");
    assertThat(metadataImpl.getExtraString(MetadataStringExtraFields.IDENTIFIER))
        .isEqualTo("A635D337-4805-4C32-A211-13F8C038BF27");
    assertThat(metadataImpl.getExtraString(MetadataStringExtraFields.GUID))
        .isEqualTo("http://domain.com/datset/guid");

    assertThat(metadataImpl.getIdentifierWithFallback()).isEqualTo("A635D337-4805-4C32-A211-13F8C038BF27");
    assertThat(metadataImpl.getOwnerOrg()).isEqualTo("30b7e9ef-327c-4f32-9591-70a88bcbdda4");
    assertThat(metadataImpl.isOpen()).isTrue();
    assertThat(metadataImpl.getType()).isEqualTo(MetadataEnumType.DATASET);
    assertThat(metadataImpl.getState()).isEqualTo("active");
    assertThat(metadataImpl.isNew()).isFalse();

    // contacts (only some. Address for maintainer, as it is the contactPoint)
    assertContact(metadataImpl.getContact(RoleEnumType.CREATOR), RoleEnumType.CREATOR);
    assertContact(metadataImpl.getContact(RoleEnumType.MAINTAINER), RoleEnumType.MAINTAINER);
    assertContact(metadataImpl.getContact(RoleEnumType.PUBLISHER), RoleEnumType.PUBLISHER);

    assertAddress(metadataImpl.getContact(RoleEnumType.MAINTAINER).getAddress(), RoleEnumType.MAINTAINER);

    // get Contacts
    List<Contact> contacts = metadataImpl.getContacts();
    assertRoleInContactList(contacts, RoleEnumType.CREATOR);
    assertRoleInContactList(contacts, RoleEnumType.MAINTAINER);
    assertRoleInContactList(contacts, RoleEnumType.PUBLISHER);
    assertThat(contacts).hasSize(3);


    // tags
    List<Tag> tags = metadataImpl.getTags();
    assertTagInTagList(tags, "Bodenschutz");
    assertTagInTagList(tags, "Geodaten");
    assertTagInTagList(tags, "Grundwasser");
    assertTagInTagList(tags, "Karte");
    assertTagInTagList(tags, "Thematische Karte");
    assertTagInTagList(tags, "Umwelt und Klima");
    assertTagInTagList(tags, "hmbtg");
    assertTagInTagList(tags, "hmbtg_09_geodaten");
    assertTagInTagList(tags, "opendata");

    // resources
    List<Resource> resources = metadataImpl.getResources();
    assertThat(resources).hasSize(2);

    // Resource 0 (Resource 1 analog)
    Resource res = resources.get(0);
    assertThat(res.getId()).isEqualTo("5ba8c170-f4b4-411a-b3a4-a117cf6e2687");
    assertThat(res.getName()).isEqualTo("Download WFS Naturräume Geest und Marsch (GML)");
    assertThat(res.getFormat()).isEqualTo("https://www.iana.org/assignments/media-types/application/gml+xml");
    assertThat(res.getUrl()).isEqualTo("http://geodienste.hamburg.de/darf_nicht_die_gleiche_url_wie_downloadurl_sein_da_es_sonst_nicht_angezeigt_wird");
    assertThat(res.getDescription()).isEqualTo("Das ist eine deutsche Beschreibung der Distribution 1");
    assertThat(res.getHash()).isEqualTo("5bcc814127be171c75595d419f371c74c9cf041419c45d6e8d2c789e5c303b47");
    assertThat(res.getLicense().getName()).isEqualTo("http://dcat-ap.de/def/licenses/dl-by-de/2.0");
    assertThat(res.getLanguage()).containsExactly("de");
    assertThat(res.getIssued()).isEqualTo("2017-02-27");
    assertThat(res.getModified()).isEqualTo("2017-03-07T10:00:00");


    // categories
    List<Category> categories = metadataImpl.getCategories();
    assertThat(categories).hasSize(1);
    Category cat = categories.get(0);
    assertThat(cat.getName()).isEqualTo("envi");
    assertThat(cat.getTitle()).isEqualTo("Umwelt");
  }

  @Test
  public void writeMetadataAndVerify() throws OpenDataRegistryException, JsonProcessingException {
    // ### New Metadata: set all available fields to sensible values
    MetadataBean metadataBean = new MetadataBean();
    MetadataImpl metadata = new MetadataImpl(metadataBean, odrClient);

    metadata.setTitle("title");
    metadata.setName("name");
    metadata.setNotes("notes");
    metadata.setPrivate(false);
    metadata.setUrl("url");
    metadata.setTemporalCoverageFrom(DateUtil.parseDateString("2017-09-18T10:00:00"));
    metadata.setTemporalCoverageTo(DateUtil.parseDateString("2018-09-18T10:00:00"));

    // contacts
    Contact creator = metadata.getContact(RoleEnumType.CREATOR);
    creator.setName("creator");
    creator.setEmail("creator@email.de");
    creator.setUrl("creator url");

    Contact maintainer = metadata.getContact(RoleEnumType.MAINTAINER);
    maintainer.setName("maintainer");
    maintainer.setEmail("maintainer@email.de");
    maintainer.setUrl("maintainer url");
    ContactAddress maintainerAddress = maintainer.getAddress();
    maintainerAddress.setAddressee("maintainer addressee");
    maintainerAddress.setCity("maintainer city");
    maintainerAddress.setCountry("maintainer country");
    maintainerAddress.setDetails("maintainer details");
    maintainerAddress.setStreet("maintainer street");
    maintainerAddress.setZIP("maintainer zip");

    Contact publisher = metadata.getContact(RoleEnumType.PUBLISHER);
    publisher.setName("publisher");
    publisher.setEmail("publisher email");
    publisher.setUrl("publisher url");

    // tags
    metadata.newTag("Boing");

    // groups
    GroupBean groupBean = new GroupBean();
    groupBean.setName("envi");
    groupBean.setId("envi");
    groupBean.setTitle("Environment");
    CategoryImpl category = new CategoryImpl(groupBean);
    metadata.getCategories().add(category);

    // extras fields
    metadata.setExtraString(MetadataStringExtraFields.POLITICAL_GEOCODING_LEVEL_URI, "politicalGeocodingLevelURI");
    metadata.setExtraList(MetadataListExtraFields.POLITICAL_GEOCODING_URI, Collections.singletonList("politicalGeocodingURI"));

    // resources
    ResourceImpl resource = metadata.newResource();
    resource.setName("resource name");
    resource.setDescription("resource desc");
    resource.setFormat("resource format");
    resource.setHash("resource hash");
    resource.setLanguage(Collections.singletonList("resource lang"));
    resource.setLicense("resource license");
    resource.setUrl("resource url");
    resource.setIssued(DateUtil.parseDateString("2017-11-28T12:20:00"));
    resource.setModified(DateUtil.parseDateString("2017-12-08T11:10:00"));

    // ### call write on metadataimpl
    JsonNode jsonNode = metadata.write(false);
    String serializedMetadata = OM.writeValueAsString(jsonNode);

    // ### check that everything was serialized the right way
    System.out.println(serializedMetadata);
    assertThat(serializedMetadata).contains("\"title\":\"title\"");
    assertThat(serializedMetadata).contains("\"name\":\"name\"");
    assertThat(serializedMetadata).contains("\"notes\":\"notes\"");
    assertThat(serializedMetadata).contains("\"private\":false");
    assertThat(serializedMetadata).contains("\"url\":\"url\"");
    assertThat(serializedMetadata).contains("{\"key\":\"temporal_end\",\"value\":\"2018-09-18T10:00:00\"}");
    assertThat(serializedMetadata).contains("{\"key\":\"temporal_start\",\"value\":\"2017-09-18T10:00:00\"}");

    // contacts
    assertThat(serializedMetadata).contains("\"author\":\"creator\"");
    assertThat(serializedMetadata).contains("\"author_email\":\"creator@email.de\"");
    assertThat(serializedMetadata).contains("{\"key\":\"author_url\",\"value\":\"creator url\"}");

    assertThat(serializedMetadata).contains("\"maintainer\":\"maintainer\"");
    assertThat(serializedMetadata).contains("\"maintainer_email\":\"maintainer@email.de\"");
    assertThat(serializedMetadata).contains("{\"key\":\"maintainer_url\",\"value\":\"maintainer url\"}");
    assertThat(serializedMetadata).contains("{\"key\":\"maintainer_addressee\",\"value\":\"maintainer addressee\"}");
    assertThat(serializedMetadata).contains("{\"key\":\"maintainer_city\",\"value\":\"maintainer city\"}");
    assertThat(serializedMetadata).contains("{\"key\":\"maintainer_country\",\"value\":\"maintainer country\"}");
    assertThat(serializedMetadata).contains("{\"key\":\"maintainer_details\",\"value\":\"maintainer details\"}");
    assertThat(serializedMetadata).contains("{\"key\":\"maintainer_street\",\"value\":\"maintainer street\"}");
    assertThat(serializedMetadata).contains("{\"key\":\"maintainer_zip\",\"value\":\"maintainer zip\"}");

    assertThat(serializedMetadata).contains("{\"key\":\"publisher_name\",\"value\":\"publisher\"}");
    assertThat(serializedMetadata).contains("{\"key\":\"publisher_email\",\"value\":\"publisher email\"}");
    assertThat(serializedMetadata).contains("{\"key\":\"publisher_url\",\"value\":\"publisher url\"}");

    // tags
    assertThat(serializedMetadata).contains("\"tags\":[{\"name\":\"Boing\"}]");

    // groups
    assertThat(serializedMetadata).contains("\"groups\":[{\"name\":\"envi\"}]");

    // extras fields
    assertThat(serializedMetadata).contains("{\"key\":\"politicalGeocodingLevelURI\",\"value\":\"politicalGeocodingLevelURI\"}");
    assertThat(serializedMetadata).contains("{\"key\":\"politicalGeocodingURI\",\"value\":\"[\\\"politicalGeocodingURI\\\"]\"}");

    // resources
    assertThat(serializedMetadata).contains("\"name\":\"resource name\"");
    assertThat(serializedMetadata).contains("\"description\":\"resource desc\"");
    assertThat(serializedMetadata).contains("\"format\":\"resource format\"");
    assertThat(serializedMetadata).contains("\"hash\":\"resource hash\"");
    assertThat(serializedMetadata)
        .contains(
            "\"__extras\":{\"license\":\"resource license\",\"modified\":\"2017-12-08T11:10:00\","
                + "\"language\":\"[\\\"resource lang\\\"]\",\"issued\":\"2017-11-28T12:20:00\"}");
    assertThat(serializedMetadata).contains("\"url\":\"resource url\"");
  }

  @Test
  public void cleanResources() throws IOException, ParseException
  {
    /* prepare */
    MetadataImpl target = createMetadataImplFromJson();
    assertThat(target.getResources()).isNotEmpty();

    /* execute */
    target.cleanResources();

    /* verify */
    assertThat(target.getResources()).isEmpty();
  }

  @Test
  public void isOpen_with_resources_and_licence_open() throws IOException, ParseException
  {
    /* prepare */
    expectListLicences(LicenceConformance.APPROVED.getValue());
    MetadataImpl target = createMetadataImplFromJson();
    assertThat(target.getResources()).isNotEmpty();

    /* execute */
    boolean result = target.isOpen();

    /* verify */
    assertThat(result).isTrue();
  }

  @Test
  public void isOpen_with_resources_and_licence_open_with_unknown_licence() throws IOException, ParseException
  {
    /* prepare */
    expectListLicences(LicenceConformance.APPROVED.getValue());
    MetadataImpl target = createMetadataImplFromJson("/dcatap-dataset-only-unknown-licence.json");
    assertThat(target.getResources()).isNotEmpty();

    /* execute */
    boolean result = target.isOpen();

    /* verify */
    assertThat(result).isFalse();
  }

  @Test
  public void isOpen_with_resources_and_licence_closed() throws IOException, ParseException
  {
    /* prepare */
    expectListLicences(LicenceConformance.NOTREVIEWED.getValue());
    MetadataImpl target = createMetadataImplFromJson();
    assertThat(target.getResources()).isNotEmpty();

    /* execute */
    boolean result = target.isOpen();

    /* verify */
    assertThat(result).isFalse();
  }

  @Test
  public void isOpen_with_no_resources() throws IOException, ParseException
  {
    /* prepare */
    MetadataImpl target = createMetadataImplFromJson();
    target.cleanResources();
    assertThat(target.getResources()).isEmpty();

    /* execute */
    boolean result = target.isOpen();

    /* verify */
    assertThat(result).isFalse();
  }

  @Test
  public void isOpen_with_resources_and_no_licence() throws IOException, ParseException
  {
    /* prepare */
    MetadataImpl target = createMetadataImplFromJson();
    assertThat(target.getResources()).isNotEmpty();
    target.getResources().stream().forEach(r -> ((ResourceImpl) r).setLicense(null));

    /* execute */
    boolean result = target.isOpen();

    /* verify */
    assertThat(result).isFalse();
  }

  private void expectListLicences(String odConformance)
  {
    List<Licence> licenceList = new ArrayList<>();
    LicenceBean licenceBean = new LicenceBean();
    licenceBean.setId("http://dcat-ap.de/def/licenses/dl-by-de/2.0");
    licenceBean.setOd_conformance(odConformance);
    licenceList.add(new LicenceImpl(licenceBean));
    when(odrClient.listLicenses()).thenReturn(licenceList);
  }

  private MetadataImpl createMetadataImplFromJson() throws IOException, JsonParseException,
      JsonMappingException
  {
    return createMetadataImplFromJson("/dcatap-dataset.json");
  }

  private MetadataImpl createMetadataImplFromJson(String path) throws IOException, JsonParseException,
      JsonMappingException
  {
    File file = new File(this.getClass().getResource(path).getFile());
    MetadataBean readValue = OM.readValue(file, MetadataBean.class);
    MetadataImpl metadataImpl = new MetadataImpl(readValue, odrClient);
    return metadataImpl;
  }

  private void assertContact(Contact contact, RoleEnumType role) {
    assertThat(contact.getName()).isEqualTo(role.getField() + "_name");
    assertThat(contact.getEmail()).isEqualTo(role.getField() + "_email");
    assertThat(contact.getUrl()).isEqualTo(role.getField() + "_url");
  }

  private void assertAddress(ContactAddress address, RoleEnumType role) {
    assertThat(address.getAddressee()).isEqualTo(role.getField() + "_addressee");
    assertThat(address.getCity()).isEqualTo(role.getField() + "_city");
    assertThat(address.getCountry()).isEqualTo(role.getField() + "_country");
    assertThat(address.getDetails()).isEqualTo(role.getField() + "_details");
    assertThat(address.getStreet()).isEqualTo(role.getField() + "_street");
    assertThat(address.getZIP()).isEqualTo(role.getField() + "_zip");
  }

  private void assertRoleInContactList(List<Contact> list, RoleEnumType role) {
    assertThat(list.stream().anyMatch(x -> x.getRole() == role)).isTrue();
  }

  private void assertTagInTagList(List<Tag> list, String name) {
    assertThat(list.stream().anyMatch(x -> x.getName().equals(name))).isTrue();
  }

  private MetadataBean createDefaultMetadataBean() {
    return createDefaultMetadataBean(COVERAGEFROM_VALID, COVERAGETO_VALID);
  }

  private MetadataBean createDefaultMetadataBean(String coverageFrom, String coverageTo) {
    MetadataBean result = new MetadataBean();
    result.getExtras().add(createExtraBean(Constants.JSON_FIELD_COVERAGEFROM, coverageFrom));
    result.getExtras().add(createExtraBean(Constants.JSON_FIELD_COVERAGETO, coverageTo));
    return result;
  }

  private MetadataBean createMetadataBeanWithInvalidTemporalCoverage() {
    MetadataBean result = new MetadataBean();
    result.getExtras().add(createExtraBean(Constants.JSON_FIELD_COVERAGEFROM, "2015"));
    result.getExtras().add(createExtraBean(Constants.JSON_FIELD_COVERAGETO, "unknown"));
    return result;
  }

  private ExtraBean createExtraBean(String key, String value) {
    ExtraBean result = new ExtraBean();
    result.setKey(key);
    result.setValue(value);
    return result;
  }

  private Date parseDate(String dateString, String datePattern) throws ParseException {
    DateFormat df = new SimpleDateFormat(datePattern);
    df.setLenient(false);

    return df.parse(dateString);
  }

  private Date parseDate(String dateString) throws ParseException {
    return parseDate(dateString, DATE_PATTERN);
  }
}
