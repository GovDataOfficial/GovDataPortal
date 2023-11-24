package de.seitenbau.govdata.odp.registry.model;

/**
 * The Enum MetadataEnumType.
 * 
 * @author sim
 * @author rnoerenberg
 */
public enum MetadataEnumType
{

  /** The dataset. */
  DATASET("dataset", "Datensatz"),
  /** The unknown. */
  UNKNOWN("unbekannt", "Unbekannt");

  /** The field. */
  private String field;

  /** The display name. */
  private String displayName;

  /**
   * Instantiates a new metadata enum type.
   * 
   * @param field the field
   * @param displayName the display name
   */
  private MetadataEnumType(String field, String displayName)
  {
    this.field = field;
    this.displayName = displayName;
  }

  /**
   * To field.
   * 
   * @return the string
   */
  public String toField()
  {
    return field;
  }

  /**
   * Gets the display name.
   * 
   * @return the display name
   */
  public String getDisplayName()
  {
    return displayName;
  }

  /**
   * From field.
   * 
   * @param type the type
   * @return the metadata enum type
   */
  public static MetadataEnumType fromField(String type)
  {
    type = type.trim().toLowerCase();
    if (DATASET.toField().equals(type))
    {
      return DATASET;
    }
    else
    {
      return UNKNOWN;
    }
  }

}
