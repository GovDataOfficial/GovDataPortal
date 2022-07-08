package de.seitenbau.govdata.common.showcase.model;

import de.seitenbau.govdata.common.model.exception.UnknownShowcaseTypeException;
import lombok.Getter;

/**
 * The Enum ShowcaseTypeEnumType.
 *
 * @author sgebhart
 */
public enum ShowcaseTypeEnum
{

  /** Konzepte. */
  CONCEPT("concept", "Konzept"),

  /** Wissenschaftliche Publikationen. */
  PUBLICATION("publication", "Wissenschaftliche Publikation"),

  /** Visualisierung. */
  VISUALIZATION("visualization", "Visualisierung"),

  /** Webseiten. */
  WEBSITE("website", "Webseite"),

  /** Tools. */
  TOOL("tool", "Tool"),

  /** Mobile Apps. */
  APP("mobile_app", "Mobile App"),

  /** Sonstiges. */
  OTHER("other", "Sonstiges");

  @Getter
  private String field;

  @Getter
  private String displayName;

  private ShowcaseTypeEnum(String field, String displayName)
  {
    this.field = field;
    this.displayName = displayName;
  }

  /**
   * Get {@link ShowcaseTypeEnum} from field name.
   * 
   * @param field name of the field
   * @return the corresponding {@link ShowcaseTypeEnum} to the <b>field</b>
   * @throws UnknownShowcaseTypeException if the given field name does not match to any
   *         {@link ShowcaseTypeEnum}
   */
  public static ShowcaseTypeEnum fromField(String field) throws UnknownShowcaseTypeException
  {
    for (ShowcaseTypeEnum type : ShowcaseTypeEnum.values())
    {
      if (type.getField().equals(field))
      {
        return type;
      }
    }

    throw new UnknownShowcaseTypeException("Showcase-type with the fieldname '" + field + "' is not known.");
  }
}
