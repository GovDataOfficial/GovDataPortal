package de.seitenbau.govdata.odp.registry.model;

import de.seitenbau.govdata.odp.registry.model.exception.OpenDataRegistryException;

/**
 * The Enum RoleEnumType.
 * 
 * @author sim
 */
public enum GeoGranularityEnumType
{

    /** The germanfederation. */
    GERMANFEDERATION("bund", "Bund"),

    /** The state. */
    STATE("land", "Land"),

    /** The municipality. */
    MUNICIPALITY("kommune", "Kommune"),

    /** The city. */
    CITY("stadt", "Stadt");

    /** The field. */
    private String field;

    /** The display name. */
    private String displayName;

    /**
     * Instantiates a new geo granularity enum type.
     * 
     * @param field
     *            the field
     * @param displayName
     *            the display name
     */
    private GeoGranularityEnumType(String field, String displayName)
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
     * @param type
     *            the type
     * @return the metadata enum type
     * @throws OpenDataRegistryException
     *             the open data registry exception
     */
    public static GeoGranularityEnumType fromField(String type) throws OpenDataRegistryException
    {
      type = type.trim().toLowerCase();
      if (GERMANFEDERATION.toField().equals(type))
      {
        return GERMANFEDERATION;
      }
      else if (STATE.toField().equals(type))
      {
        return STATE;
      }
      else if (MUNICIPALITY.toField().equals(type))
      {
        return MUNICIPALITY;
      }
      else if (CITY.toField().equals(type))
      {
        return CITY;
      }
      else
      {
        throw new OpenDataRegistryException(type);
      }
    }
}
