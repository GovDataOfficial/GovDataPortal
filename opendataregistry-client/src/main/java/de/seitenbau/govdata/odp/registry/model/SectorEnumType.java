package de.seitenbau.govdata.odp.registry.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Enum RoleEnumType.
 * 
 * @author sim
 */
public enum SectorEnumType
{

    /** The public. */
    PUBLIC("oeffentlich", "Öffentlich"),

    /** The private. */
    PRIVATE("privat", "Privat"),

    /** The other. */
    OTHER("andere", "Andere");

    /**
     * The Constant log.
     */
    private static final Logger LOG = LoggerFactory.getLogger(SectorEnumType.class);

    /** The field. */
    private String field;

    /** The display name. */
    private String displayName;

    /**
     * Instantiates a new sector enum type.
     * 
     * @param field
     *            the field
     * @param displayName
     *            the display name
     */
    private SectorEnumType(String field, String displayName)
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
     */
    public static SectorEnumType fromField(String type)
    {
      type = type.trim().toLowerCase();
      if (PUBLIC.toField().equals(type))
      {
        return PUBLIC;
      }
      else if (PRIVATE.toField().equals(type))
      {
        return PRIVATE;
      }
      else if (OTHER.toField().equals(type))
      {
        return OTHER;
      }
      else
      {
        LOG.debug("Parsing SectorEnumType: " + type, new IllegalArgumentException(type));
        return OTHER;
      }
    }
}
