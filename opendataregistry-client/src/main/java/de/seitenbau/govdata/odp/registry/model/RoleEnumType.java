package de.seitenbau.govdata.odp.registry.model;

import de.seitenbau.govdata.odp.registry.model.exception.UnknownRoleException;
import lombok.Getter;

/**
 * The Enum RoleEnumType.
 *
 * @author rnoerenberg
 * @author sim, msg
 */
public enum RoleEnumType
{

  /** Der Autor. */
  CREATOR("author", "Autor"),

  /** Der Herausgeber. */
  PUBLISHER("publisher", "Herausgeber"),

  /** Der Ansprechpartner. */
  MAINTAINER("maintainer", "Ansprechpartner"),

  /** Der Bearbeiter. */
  CONTRIBUTOR("contributor", "Bearbeiter"),

  /** Der Urheber. */
  ORIGINATOR("originator", "Urheber");

  @Getter
  private String field;

  @Getter
  private String displayName;

  private RoleEnumType(String field, String displayName)
  {
    this.field = field;
    this.displayName = displayName;
  }

  /**
   * Get @link RoleEnumType from field name.
   * 
   * @param field name of the field
   * @return the corresponding {@link RoleEnumType} to the <b>field</b>
   * @throws UnknownRoleException if the given field name does not match to any {@link RoleEnumType}
   */
  public static RoleEnumType fromField(String field) throws UnknownRoleException
  {
    for (RoleEnumType role : RoleEnumType.values())
    {
      if (role.getField().equals(field))
      {
        return role;
      }
    }

    throw new UnknownRoleException("Role with the fieldname '" + field + "' is not known.");
  }
}
