package de.seitenbau.govdata.odp.registry.model;

import de.seitenbau.govdata.odp.registry.model.exception.OpenDataRegistryException;

/**
 * The Enum TemporalGranularityEnumType.
 */
public enum TemporalGranularityEnumType
{
  // CHECKSTYLE:OFF

    /**
     * The second.
     */
    SECOND("sekunde", "Sekunde"),
    /**
     * The minute.
     */
    MINUTE("minute", "Minute"),
    /**
     * The hour.
     */
    HOUR("stunde", "Stunde"),
    /**
     * The day.
     */
    DAY("tag", "Tag"),
    /**
     * The week.
     */
    WEEK("woche", "Woche"),
    /**
     * The month.
     */
    MONTH("monat", "Monat"),
    /**
     * The year.
     */
    YEAR("jahr", "Jahr"), NO_MATCH("nomatch", "Trifft nicht zu");
    /**
     * The field.
     */
    private String field;
    /**
     * The display name.
     */
    private String displayName;

    /**
     * Instantiates a new temporal granularity enum type.
     * 
     * @param field
     *            the field
     * @param displayName
     *            the display name
     */
    private TemporalGranularityEnumType(String field, String displayName)
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
    public static TemporalGranularityEnumType fromField(String type) throws OpenDataRegistryException
    {
      type = type.trim().toLowerCase();
      if (SECOND.toField().equals(type))
      {
        return SECOND;
      }
      else if (MINUTE.toField().equals(type))
      {
        return MINUTE;
      }
      else if (HOUR.toField().equals(type))
      {
        return HOUR;
      }
      else if (DAY.toField().equals(type))
      {
        return DAY;
      }
      else if (WEEK.toField().equals(type))
      {
        return WEEK;
      }
      else if (MONTH.toField().equals(type))
      {
        return MONTH;
      }
      else if (YEAR.toField().equals(type))
      {
        return YEAR;
      }
      else if (NO_MATCH.toField().equals(type))
      {
        return NO_MATCH;
      }
      else
      {
        throw new OpenDataRegistryException(type);
      }
    }
}
