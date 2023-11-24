package de.seitenbau.govdata.odp.registry.model;

import de.seitenbau.govdata.odp.registry.ckan.impl.ContactAddress;


/**
 * The Interface Contact.
 * @author sim
 */
public interface Contact
{
  // CHECKSTYLE:OFF

  ContactAddress getAddress();

  String getEmail();

  String getName();

  RoleEnumType getRole();

  String getUrl();

  void setEmail(String email);

  void setName(String name);

  void setRole(RoleEnumType role);

  void setUrl(String url);

  /**
   * Checks if there are any values set for this role.
   *
   * @return true if there are values, so the contact exists. False if there is
   *         no data.
   */
  boolean exists();

}
