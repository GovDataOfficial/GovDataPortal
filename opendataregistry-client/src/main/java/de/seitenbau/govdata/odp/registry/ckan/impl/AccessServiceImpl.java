package de.seitenbau.govdata.odp.registry.ckan.impl;

import java.io.Serializable;
import java.util.List;

import de.seitenbau.govdata.odp.registry.ODRClient;
import de.seitenbau.govdata.odp.registry.ckan.Util;
import de.seitenbau.govdata.odp.registry.ckan.json.AccessServiceBean;
import de.seitenbau.govdata.odp.registry.model.AccessService;
import de.seitenbau.govdata.odp.registry.model.Licence;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of AccessServices.
 * 
 * @author youalad
 *
 */
@Slf4j
public class AccessServiceImpl implements AccessService, Serializable
{

  private static final long serialVersionUID = 3300557600407930602L;

  /** Stores the odrClient client for retrieving additional information like licenses. */
  private transient ODRClient odrClient;

  private AccessServiceBean accessService;

  private Licence license;

  /**
   * Creates a new AccessServiceImpl to implement AccessService Interface.
   *
   * @param odrClient
   * @param accessService
   */
  public AccessServiceImpl(ODRClient odrClient, AccessServiceBean accessService)
  {
    this.odrClient = odrClient;
    this.accessService = accessService;
  }

  /**
   * Creates a new AccessServiceImpl to implement AccessService Interface.
   *
   * @param accessService
   */
  public AccessServiceImpl(AccessServiceBean accessService)
  {
    this.accessService = accessService;
  }

  @Override
  public String getAvailability()
  {
    return accessService.getAvailability();
  }

  /**
   * Sets the value for the field availability
   *
   * @param text
   */
  public void setAvailability(String text)
  {
    accessService.setAvailability(text);
  }

  @Override
  public String getTitle()
  {
    return accessService.getTitle();
  }

  /**
   * Sets the value for the field title
   *
   * @param text
   */
  public void setTitle(String text)
  {
    accessService.setTitle(text);
  }

  @Override
  public String getDescription()
  {
    return accessService.getDescription();
  }

  /**
   * Sets the value for the field description
   *
   * @param description
   */
  public void setDescription(String description)
  {
    accessService.setDescription(description);
  }

  /**
   * Sets the value for the field endpointDescription
   *
   * @param description
   */
  public void setEndpointDescription(String description)
  {
    accessService.setEndpointDescription(description);
  }

  @Override
  public String getEndpointDescription()
  {
    return accessService.getEndpointDescription();
  }

  /**
   * Sets the value for the field accessRights
   *
   * @param text
   */
  public void setAccessRights(String text)
  {
    accessService.setAccessRights(text);
  }

  @Override
  public String getAccessRights()
  {
    return accessService.getAccessRights();
  }

  @Override
  public Licence getLicense()
  {
    this.license = Util.initLicense(license, accessService.getLicense(), odrClient);
    return license;
  }

  /**
   * Sets the value for the field license
   *
   * @param license
   */
  public void setLicense(String license)
  {
    accessService.setLicense(license);
    this.license = null;
  }

  @Override
  public Boolean isOpen()
  {
    this.license = Util.initLicense(license, accessService.getLicense(), odrClient);
    return license != null && license.isOpen();
  }

  @Override
  public String getLicenseAttributionByText()
  {
    return accessService.getLicenseAttributionByText();
  }

  /**
   * Sets the value for the field licenseAttributionByText
   *
   * @param text
   */
  public void setLicenseAttributionByText(String text)
  {
    accessService.setLicenseAttributionByText(text);
  }

  @Override
  public List<String> getEndpointUrls()
  {
    return accessService.getEndpointUrls();
  }

  /**
   * Sets the value for the field endpointUrls
   *
   * @param list
   */
  public void setEndpointUrls(List<String> list)
  {
    accessService.setEndpointUrls(list);
  }

  @Override
  public List<String> getServesDataset()
  {
    return accessService.getServesDataset();
  }

  /**
   * Sets the value for the field serveDataset
   *
   * @param list
   */
  public void setServesDataset(List<String> list)
  {
    accessService.setServesDataset(list);
  }

  /**
   * Returns the code value for availability if a value is present and represents a valid URI.
   *
   * @return the code value of the availability, otherwise null.
   */
  public String getShortendAvailability()
  {
    return Util.getShortendAvailability(this.getAvailability());
  }

}
