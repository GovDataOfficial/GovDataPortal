package de.seitenbau.govdata.odp.registry.model;

import java.util.List;

/**
 * The Interface AccessService
 * 
 * @author youalad
 *
 */
public interface AccessService
{
  /**
   * Get the availability
   *
   * @return the availability
   */
  String getAvailability();

  /**
   * Get the title
   *
   * @return the title
   */
  String getTitle();

  /**
   * Get the description
   *
   * @return the description
   */
  String getDescription();

  /**
   * Get the endpoint description
   *
   * @return the endpoint description
   */
  String getEndpointDescription();

  /**
   * Get the access rights
   *
   * @return the access rights
   */
  String getAccessRights();

  /**
   * Get the license
   *
   * @return the license
   */
  Licence getLicense();

  /**
   * Get the license attribution by text
   *
   * @return the license attribution by text
   */
  String getLicenseAttributionByText();

  /**
   * Checks if the license is open
   *
   * @return true if the license is open. False if its restricted
   */
  Boolean isOpen();

  /**
   * Get the endpoint urls
   *
   * @return the endpoint urls
   */
  List<String> getEndpointUrls();

  /**
   * Get serves dataset
   *
   * @return serves dataset
   */
  List<String> getServesDataset();
}
