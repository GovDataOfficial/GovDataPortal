package de.seitenbau.govdata.odp.registry.model;

/**
 * Enum for format types
 *
 */
public enum FormatEnumType
{
  /** xml */
  XML("xml"),
  /** jsonld */
  JSONLD("jsonld");

  private String key;

  FormatEnumType(String key)
  {
    this.key = key;
  }

  public String getKey()
  {
    return key;
  }
}