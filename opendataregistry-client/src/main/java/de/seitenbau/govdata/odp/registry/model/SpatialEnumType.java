package de.seitenbau.govdata.odp.registry.model;

import de.seitenbau.govdata.odp.registry.model.exception.OpenDataRegistryException;

/**
 * The Enum SpatialEnumType.
 */
public enum SpatialEnumType
{

    /** The polygon. */
    POLYGON("polygon", "Polygon");

    /** The field. */
    private String field;

    /** The display name. */
    private String displayName;

    /**
     * Instantiates a new spatial enum type.
     * 
     * @param field
     *            the field
     * @param displayName
     *            the display name
     */
    private SpatialEnumType(String field, String displayName)
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
     * @return the spatial enum type
     * @throws OpenDataRegistryException
     *             the open data registry exception
     */
    public static SpatialEnumType fromField(String type) throws OpenDataRegistryException
    {
      type = type.trim().toLowerCase();
      if (POLYGON.toField().equals(type))
      {
        return POLYGON;
      }
      else
      {
        throw new OpenDataRegistryException(type);
      }
    }
}
