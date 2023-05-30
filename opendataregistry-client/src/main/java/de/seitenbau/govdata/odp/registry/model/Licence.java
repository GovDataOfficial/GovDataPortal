package de.seitenbau.govdata.odp.registry.model;

/**
 * The Interface Licence.
 */
public interface Licence
{

  /**
   * Gets the title.
   * 
   * @return the title
   */
  String getTitle();

  /**
   * Gets the id.
   * 
   * @return the id
   */
  String getId();

  /**
   * Gets the url.
   * 
   * @return the url
   */
  String getUrl();

  /**
   * Gets the od conformance.
   * 
   * @return the od conformancer
   */
  String getOdConformance();

  /**
   * Gets the osd conformance.
   * 
   * @return the osd conformance
   */
  String getOsdConformance();

  /**
   * True if the license is included in the current version of DCAT-AP.de
   *
   * @return
   */
  boolean isActive();

  /**
   * Checks if is open.
   * 
   * @return true, if is open
   */
  boolean isOpen();

}
