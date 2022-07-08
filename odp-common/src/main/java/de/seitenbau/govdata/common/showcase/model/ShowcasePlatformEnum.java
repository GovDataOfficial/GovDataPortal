package de.seitenbau.govdata.common.showcase.model;

import de.seitenbau.govdata.common.model.exception.UnknownShowcasePlatformException;
import lombok.Getter;

/**
 * The Enum ShowcasePlatformEnum.
 *
 * @author sgebhart
 */
public enum ShowcasePlatformEnum
{

  /** Android. */
  ANDROID("android", "Android"),

  /** iOS */
  IOS("ios", "iOS"),

  /** Web. */
  WEB("web", "Web"),

  /** Linux. */
  LINUX("linux", "Linux"),

  /** Sonstige. */
  OTHER("other", "Sonstige");


  @Getter
  private String field;

  @Getter
  private String displayName;

  private ShowcasePlatformEnum(String field, String displayName)
  {
    this.field = field;
    this.displayName = displayName;
  }

  /**
   * Get {@link ShowcasePlatformEnum} from field name.
   * 
   * @param field name of the field
   * @return the corresponding {@link ShowcasePlatformEnum} to the <b>field</b>
   * @throws ShowcasePlatformEnum if the given field name does not match to any
   *         {@link ShowcasePlatformEnum}
   */
  public static ShowcasePlatformEnum fromField(String field) throws UnknownShowcasePlatformException
  {
    for (ShowcasePlatformEnum platform : ShowcasePlatformEnum.values())
    {
      if (platform.getField().equals(field))
      {
        return platform;
      }
    }

    throw new UnknownShowcasePlatformException(
        "Showcase-platform with the fieldname '" + field + "' is not known.");
  }
}
