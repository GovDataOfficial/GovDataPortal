package de.seitenbau.govdata.odp.registry.model;

import java.util.Date;
import java.util.List;

/**
 * The Interface Resource.
 *
 * @author sim
 */
public interface Resource
{
  // CHECKSTYLE:OFF
  String getId();

  String getUrl();

  String getFormat();

  /**
   * Helper method to get a abbreviated format string.
   * @return shorter format string.
   */
  String getFormatShort();

  String getDescription();

  String getDescriptionOnlyText();

  List<String> getLanguage();

  String getHash();

  /**
   * Name of the Resource (not OGD-Standard, but CKAN supported).
   */
  String getName();

  String getNameOnlyText();

  Licence getLicense();

  Boolean isOpen();

  String getLicenseAttributionByText();

  String getPlannedAvailability();

  String getAvailability();

  String getDocumentation();

  List<String> getConformsTo();

  String getDownload_url();

  String getStatus();

  String getMimetype();

  String getRights();

  String getHash_algorithm();

  Date getModified();

  Date getIssued();

  List<AccessService> getAccessServices();

  List<String> getApplicableLegislation();

  Boolean isHvd();

}
