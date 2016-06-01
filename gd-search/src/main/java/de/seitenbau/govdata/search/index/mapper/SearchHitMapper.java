package de.seitenbau.govdata.search.index.mapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.elasticsearch.common.lang3.StringUtils;
import org.elasticsearch.search.SearchHit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.fhg.fokus.odp.registry.ckan.impl.ContactImpl;
import de.fhg.fokus.odp.registry.ckan.json.ContactBean;
import de.fhg.fokus.odp.registry.model.Contact;
import de.fhg.fokus.odp.registry.model.RoleEnumType;
import de.fhg.fokus.odp.registry.model.exception.UnknownRoleException;
import de.seitenbau.govdata.dataset.details.beans.CurrentMetadataContact;
import de.seitenbau.govdata.date.DateUtil;
import de.seitenbau.govdata.search.index.IndexConstants;
import de.seitenbau.govdata.search.index.model.HitDto;
import de.seitenbau.govdata.search.index.model.ResourceDto;

@Component
public class SearchHitMapper
{
  private static final Logger log = LoggerFactory.getLogger(SearchHitMapper.class);
  
  private static final ObjectMapper OM = new ObjectMapper();

  /** Date pattern for the modified date, e.g. 2014-04-29T13:27:48.132894Z. */
  private static final String MODIFIED_DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";

  private static final String TEMPORAL_COVERAGE_PATTERN = "yyyy-MM-dd HH:mm:ss";

  @SuppressWarnings("unchecked")
  public HitDto mapToHitDto(SearchHit searchHit)
  {
    final String method = "mapToHitDto() : ";
    log.trace(method + "Start");

    Map<String, Object> data = searchHit.getSource();
    Map<String, Object> metadata = (Map<String, Object>) data.get("metadata");

    String lastModified = null;
    Date lastModifiedDate = null;
    if (StringUtils.equals(searchHit.getType(), IndexConstants.INDEX_TYPE_PORTAL))
    {
      lastModified = getFieldValueString(metadata, "modified", "");
      lastModifiedDate = DateUtil.parseDateString(lastModified, MODIFIED_DATE_PATTERN);
    }
    else
    {
      lastModified = getFieldValueString(metadata, "metadata_modified", "");
      lastModifiedDate = DateUtil.parseDateString(lastModified, MODIFIED_DATE_PATTERN);
    }
    
    String temporalCoverageFrom = getFieldValueString(metadata, "temporal_coverage_from", null);
    Date temporalCoverageFromDate = null;
    if(temporalCoverageFrom != null) {
      temporalCoverageFromDate = DateUtil.parseDateString(temporalCoverageFrom, TEMPORAL_COVERAGE_PATTERN);
    }
    String temporalCoverageTo = getFieldValueString(metadata, "temporal_coverage_to", null);
    Date temporalCoverageToDate = null;
    if(temporalCoverageTo != null) {
      temporalCoverageToDate = DateUtil.parseDateString(temporalCoverageTo, TEMPORAL_COVERAGE_PATTERN);
    }
    
    // find the correct publisher to use
    String[] contactNameAndEmail = extractContact(metadata);
    //List<Contact> contactsList = ContactImpl.read(extraToJson(contacts.getValue()));

    HitDto mappedHit = HitDto.builder()
        .id(searchHit.getId())
        .name(getFieldValueString(metadata, "name", searchHit.getId()))
        .type(getFieldValueString(metadata, "type", ""))
        .title(getFieldValueString(data, "title", ""))
        .content(getFieldValueString(data, "preamble", ""))
        .lastModified(lastModifiedDate)
        .temporalCoverageFrom(temporalCoverageFromDate)
        .temporalCoverageTo(temporalCoverageToDate)
        .contact(contactNameAndEmail[0])
        .contactEmail(contactNameAndEmail[1])
        .groups(getFieldValueAsStringList(metadata, "groups"))
        .articleId(getFieldValueString(metadata, "articleId", ""))
        .groupId(Long.parseLong(getFieldValueString(metadata, "groupId", "0")))
        .entryClassPK(getFieldValueString(metadata, "entryClassPK", ""))
        .portletId(getFieldValueString(metadata, "portletId", ""))
        .isOpen((Boolean) metadata.get("isopen"))
        .ownerOrg(getFieldValueString(metadata, "owner_org", ""))
        .resources(extractResources(metadata.get("resources")))
        .build();

    log.trace(method + "End");
    return mappedHit;
  }

  private String[] extractContact(Map<String, Object> metadata)
  {
    // highest priority: author
    String author = getFieldValueString(metadata, "author", null);
    String author_email = getFieldValueString(metadata, "author_email", null);
    
    // backup: maintainer
    String maintainer = getFieldValueString(metadata, "maintainer", null);
    String maintainerEmail = getFieldValueString(metadata, "maintainer_email", null);
    
    // Read extras->contacts and prepare List
    List<Contact> contactsFromHit = getContactsFromHit(metadata);
    
    // add publisher and maintainer from ckan-data (like MetadataImpl does)
    if(!hasRoleInContacts(contactsFromHit, RoleEnumType.PUBLISHER) && StringUtils.isNotEmpty(author)) {
      contactsFromHit.add(createContact(author, author_email, RoleEnumType.PUBLISHER));
    }
    
    if(!hasRoleInContacts(contactsFromHit, RoleEnumType.MAINTAINER) && StringUtils.isNotEmpty(maintainer)) {
      contactsFromHit.add(createContact(maintainer, maintainerEmail, RoleEnumType.MAINTAINER));
    }
   
    // use the same code as details-view for determining the contact
    CurrentMetadataContact currentMetadataContact = new CurrentMetadataContact(contactsFromHit, author, author_email);
    
    return new String[] {currentMetadataContact.getName(), currentMetadataContact.getEmail()};
  }
  
  private ContactImpl createContact(String name, String email, RoleEnumType role) {
    ContactBean bean = new ContactBean();
    bean.setRole(role.toField());
    bean.setName(name);
    bean.setEmail(email);
    return new ContactImpl(bean);
  }
  
  private boolean hasRoleInContacts(List<Contact> contactsList, RoleEnumType role) {
    try {
      for(Contact c : contactsList) {
        if(c.getRole() == role) {
          return true;
        }
      }
      return false;
    } catch (UnknownRoleException e) {
      log.error("Unknown role: " + role.getDisplayName());
      return false;
    }
  }
  
  private List<Contact> getContactsFromHit(Map<String, Object> metadata) {
    String contactJSON = null;
    List<Contact> contactsList = new ArrayList<Contact>();

    // extract contacts from extras
    @SuppressWarnings("unchecked") // because elasticSearch will always return that structure
    List<HashMap<String, String>> extras = (ArrayList<HashMap<String, String>>) metadata.get("extras");
    
    if(extras != null) {
      for(HashMap<String, String> extra : extras ) {
        if(StringUtils.equals(extra.get("key"), "contacts")) {
          contactJSON = extra.get("value");
          break; // found contact, no need to go further
        }
      }
    }
    
    if(contactJSON != null) {
      try {
        contactsList = ContactImpl.read(OM.readTree(contactJSON));
      } catch (IOException e) {
        log.error("failed parsing extras->contacts in " + metadata.get("id"));
        e.printStackTrace();
      }
    }
    return contactsList;
  }

  protected String getFieldValueString(Map<?, ?> data, String name, String defaultValue)
  {
    String result = defaultValue;
    if (data != null)
    {
      Object field = data.get(name);
      if (field instanceof String)
      {
        result = (String) field;
      }
    }
    return result;
  }

  @SuppressWarnings("unchecked")
  protected List<String> getFieldValueAsStringList(Map<String, Object> data, String name)
  {
    List<String> stringArray = (List<String>) data.get(name);
    if (null != stringArray)
    {
      return stringArray;
    }
    return new ArrayList<String>();
  }

  private List<ResourceDto> extractResources(Object resourcesListAsObject)
  {
    List<ResourceDto> result = new ArrayList<ResourceDto>();
    if (resourcesListAsObject != null)
    {
      List<?> resources = (List<?>) resourcesListAsObject;
      for (Object resObject : resources)
      {
        if (resObject instanceof HashMap)
        {
          Map<?, ?> resMap = (HashMap<?, ?>) resObject;
          ResourceDto resource =
              ResourceDto.builder()
                  .description(getFieldValueString(resMap, "description", ""))
                  .format(getFieldValueString(resMap, "format", ""))
                  .hash(getFieldValueString(resMap, "hash", ""))
                  .id(getFieldValueString(resMap, "id", ""))
                  .language(getFieldValueString(resMap, "language", ""))
                  .type(getFieldValueString(resMap, "type", ""))
                  .url(getFieldValueString(resMap, "url", ""))
                  .build();
          result.add(resource);
        }
      }
    }
    return result;
  }
}
