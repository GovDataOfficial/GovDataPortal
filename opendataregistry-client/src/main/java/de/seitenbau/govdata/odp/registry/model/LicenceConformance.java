package de.seitenbau.govdata.odp.registry.model;

/**
 * Enum for licence conformance values
 *
 */
public enum LicenceConformance
{
  /** approved */
  APPROVED("approved"),
  /** not reviewed */
  NOTREVIEWED("not reviewed");

  private String value;

  LicenceConformance(String value)
  {
    this.value = value;
  }

  public String getValue()
  {
    return value;
  }
}
