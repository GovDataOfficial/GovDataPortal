/**
 * Copyright (c) 2012, 2013 Fraunhofer Institute FOKUS
 * <p>
 * This file is part of Open Data Platform.
 * <p>
 * Open Data Platform is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * Open Data Plaform is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Affero General Public License
 * along with Open Data Platform.  If not, see <http://www.gnu.org/licenses/agpl-3.0>.
 */

package de.seitenbau.govdata.odp.registry.ckan.impl;

import static de.seitenbau.govdata.odp.registry.ckan.Constants.JSON_DATETIME_PATTERN;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.TextNode;

import de.seitenbau.govdata.clean.StringCleaner;
import de.seitenbau.govdata.dcatde.ViewUtil;
import de.seitenbau.govdata.odp.registry.ODRClient;
import de.seitenbau.govdata.odp.registry.ckan.ODRClientImpl;
import de.seitenbau.govdata.odp.registry.ckan.Util;
import de.seitenbau.govdata.odp.registry.ckan.json.LicenceBean;
import de.seitenbau.govdata.odp.registry.ckan.json.ResourceBean;
import de.seitenbau.govdata.odp.registry.ckan.json.ResourceExportBean;
import de.seitenbau.govdata.odp.registry.model.Licence;
import de.seitenbau.govdata.odp.registry.model.Resource;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of Resources.
 */
@Slf4j
public class ResourceImpl implements Resource, Serializable
{
  private static final long serialVersionUID = -4236686335813667952L;

  /** Stores the odrClient client for retrieving additional information like licenses. */
  private transient ODRClient odrClient;

  private ResourceBean resource;

  private Licence license;

  /**
   * Creates a new ResourceImpl to implement Resource Interface.
   *
   * @param odrClient reference to the ODRClient
   * @param resource bean containing all data
   */
  public ResourceImpl(ODRClient odrClient, ResourceBean resource)
  {
    this.odrClient = odrClient;
    this.resource = resource;
  }

  @Override
  public String getUrl()
  {
    return resource.getUrl();
  }

  @Override
  public void setUrl(String url)
  {
    resource.setUrl(url);
  }

  @Override
  public String getFormat()
  {
    return resource.getFormat();
  }

  @Override
  public void setFormat(String format)
  {
    resource.setFormat(format);
  }

  @Override
  public String getFormatShort()
  {
    /*
     * This whole thing should be in thymeleaf instead inside the OGD-Client...
     * but the possibilities using thymeleaf are thing and preparing Strings
     * outside this class alongside it seems not very nice...
     * Maybe if we had a dialect of thymeleaf, but they only start to get nice from version 3 forward.
     */
    return ViewUtil.getShortenedFormatRef(resource.getFormat());
  }

  @Override
  public String getDescription()
  {
    return resource.getDescription();
  }

  @Override
  public String getDescriptionOnlyText()
  {
    return getOnlyText(getDescription());
  }

  @Override
  public void setDescription(String description)
  {
    resource.setDescription(description);
  }

  @Override
  public List<String> getLanguage()
  {
    if (resource.getLanguage() == null)
    {
      return new ArrayList<>();
    }

    return Util.readJsonList(new TextNode(resource.getLanguage()));
  }

  @Override
  public void setLanguage(List<String> list)
  {
    try
    {
      resource.setLanguage(Util.writeJsonList(list));
    }
    catch (JsonProcessingException e)
    {
      log.error("Resource: Could not write language: ", e);
    }
  }

  @Override
  public String getHash()
  {
    return resource.getHash();
  }

  @Override
  public void setHash(String hash)
  {
    resource.setHash(hash);
  }

  ResourceBean getBean()
  {
    return resource;
  }

  @Override
  public String getId()
  {
    return resource.getId();
  }

  /**
   * Create a new object to convert into a JsonNode, because
   * the write-structure must have __extras, but the read-structure
   * need to contain all extra-values merged with standard fields.
   *
   * @return constructed JsonNode containing all data that should be saved to ckan.
   */
  public JsonNode write()
  {
    SimpleDateFormat formatter = new SimpleDateFormat(JSON_DATETIME_PATTERN);

    ResourceExportBean bean = new ResourceExportBean();
    bean.setCreated(resource.getCreated());
    bean.setDescription(resource.getDescription());
    bean.setFormat(resource.getFormat());
    bean.setHash(resource.getHash());
    bean.setId(resource.getId());
    bean.setLast_modified(resource.getLast_modified());
    bean.setName(resource.getName());
    bean.setResource_type(resource.getResource_type());
    bean.setSize(resource.getSize());
    bean.setState(resource.getState());
    bean.setUrl(resource.getUrl());

    HashMap<String, String> extras = new HashMap<>();
    addIfNotNull(extras, "language", resource.getLanguage());
    addIfNotNull(extras, "license", resource.getLicense());
    addIfNotNull(extras, "rights", resource.getRights());
    addIfNotNull(extras, "status", resource.getStatus());
    addIfNotNull(extras, "hash_algorithm", resource.getHash_algorithm());
    addIfNotNull(extras, "documentation", resource.getDocumentation());
    addIfNotNull(extras, "download_url", resource.getDownload_url());
    addIfNotNull(extras, "conforms_to", resource.getConforms_to());

    if (resource.getIssued() != null)
    {
      extras.put("issued", formatter.format(resource.getIssued()));
    }

    if (resource.getModified() != null)
    {
      extras.put("modified", formatter.format(resource.getModified()));
    }

    addIfNotNull(extras, "plannedAvailability", resource.getPlannedAvailability());
    addIfNotNull(extras, "licenseAttributionByText", resource.getLicenseAttributionByText());
    bean.set__extras(extras);

    return ODRClientImpl.convert(bean);
  }

  private void addIfNotNull(HashMap<String, String> extras, String key, String value)
  {
    if (value != null)
    {
      extras.put(key, value);
    }
  }

  @Override
  public String getName()
  {
    return resource.getName();
  }

  @Override
  public String getNameOnlyText()
  {
    return getOnlyText(getName());
  }

  @Override
  public void setName(String name)
  {
    resource.setName(name);
  }

  @Override
  public Licence getLicense()
  {
    initLicense(resource.getLicense());
    return license;
  }

  @Override
  public void setLicense(String license)
  {
    resource.setLicense(license);
    this.license = null; // so it get's reinitialized should someone request "isOpen".
  }

  @Override
  public Boolean isOpen()
  {
    initLicense(resource.getLicense());
    return license != null && license.isOpen();
  }

  private void initLicense(String license)
  {
    if (this.license == null)
    {
      if (license != null)
      {
        Licence licenceIfUnknown = createLicence(license);
        List<Licence> licences = odrClient.listLicenses();
        this.license =
            licences.stream().filter(l -> l.getName().equals(license)).findFirst().orElse(licenceIfUnknown);
      }
      else
      {
        this.license = createLicence("license-id-not-set");
      }
    }
  }
  
  private Licence createLicence(String licenceId)
  {
    LicenceBean bean = new LicenceBean();
    bean.setId(licenceId);
    return new LicenceImpl(bean);
  }

  @Override
  public String getLicenseAttributionByText()
  {
    return resource.getLicenseAttributionByText();
  }

  @Override
  public void setLicenseAttributionByText(String text)
  {
    resource.setLicenseAttributionByText(text);
  }

  @Override
  public String getPlannedAvailability()
  {
    return resource.getPlannedAvailability();
  }

  @Override
  public void setPlannedAvailability(String text)
  {
    resource.setPlannedAvailability(text);
  }

  @Override
  public String getDocumentation()
  {
    return resource.getDocumentation();
  }

  @Override
  public void SetDocumentation(String text)
  {
    resource.setPlannedAvailability(text);
  }

  @Override
  public List<String> getConformsTo()
  {
    return Util.readJsonList(new TextNode(resource.getConforms_to()));
  }

  @Override
  public void SetDocumentation(List<String> list) throws JsonProcessingException
  {
    resource.setConforms_to(Util.writeJsonList(list));
  }

  @Override
  public String getDownload_url()
  {
    return resource.getDownload_url();
  }

  @Override
  public void setDownload_url(String text)
  {
    resource.setDownload_url(text);
  }

  @Override
  public String getStatus()
  {
    return resource.getStatus();
  }

  @Override
  public void setStatus(String text)
  {
    resource.setStatus(text);
  }

  @Override
  public String getMimetype()
  {
    return resource.getMimetype();
  }

  @Override
  public void setMimetype(String text)
  {
    resource.setMimetype(text);
  }

  @Override
  public String getRights()
  {
    return resource.getRights();
  }

  @Override
  public void setRights(String text)
  {
    resource.setRights(text);
  }

  @Override
  public String getHash_algorithm()
  {
    return resource.getHash_algorithm();
  }

  @Override
  public void setHash_algorithm(String text)
  {
    resource.setHash_algorithm(text);
  }

  @Override
  public Date getLast_modified()
  {
    // Prefer the explicitly set modified-date over the ckan-induced one.
    if (resource.getModified() != null)
    {
      return resource.getModified();
    }
    else
    {
      return resource.getLast_modified();
    }
  }

  @Override
  public void setLast_modified(Date date)
  {
    resource.setLast_modified(date);
  }

  private String getOnlyText(String input)
  {
    String result = "";
    if (StringUtils.isNotBlank(input))
    {
      result = StringCleaner.trimAndFilterString(input);
    }
    return result;
  }
}
