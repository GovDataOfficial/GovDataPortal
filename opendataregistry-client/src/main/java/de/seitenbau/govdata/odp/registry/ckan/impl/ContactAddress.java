package de.seitenbau.govdata.odp.registry.ckan.impl;

import de.seitenbau.govdata.odp.registry.model.RoleEnumType;

/**
 * Wrapper to access the address of a contact.
 */
public class ContactAddress
{

  private static final String COUNTRY = "_country";

  private static final String CITY = "_city";

  private static final String ZIP = "_zip";

  private static final String STREET = "_street";

  private static final String DETAILS = "_details";

  private static final String ADDRESSEE = "_addressee";

  private MetadataImpl metadataImpl;

  private RoleEnumType role;

  /**
   * Creates a new wrapper.
   * @param metadataImpl reference to the metadataImpl holding the real data.
   * @param role the address of this role will be wrapped.
   */
  public ContactAddress(MetadataImpl metadataImpl, RoleEnumType role)
  {
    this.metadataImpl = metadataImpl;
    this.role = role;
  }

  public String getAddressee()
  {
    return metadataImpl.getExtra(role.getField() + ADDRESSEE);
  }

  public String getDetails()
  {
    return metadataImpl.getExtra(role.getField() + DETAILS);
  }

  public String getStreet()
  {
    return metadataImpl.getExtra(role.getField() + STREET);
  }

  public String getZIP()
  {
    return metadataImpl.getExtra(role.getField() + ZIP);
  }

  public String getCity()
  {
    return metadataImpl.getExtra(role.getField() + CITY);
  }

  public String getCountry()
  {
    return metadataImpl.getExtra(role.getField() + COUNTRY);
  }

  public void setAddressee(String s)
  {
    metadataImpl.setExtra(role.getField() + ADDRESSEE, s);
  }

  public void setDetails(String s)
  {
    metadataImpl.setExtra(role.getField() + DETAILS, s);
  }

  public void setStreet(String s)
  {
    metadataImpl.setExtra(role.getField() + STREET, s);
  }

  public void setZIP(String s)
  {
    metadataImpl.setExtra(role.getField() + ZIP, s);
  }

  public void setCity(String s)
  {
    metadataImpl.setExtra(role.getField() + CITY, s);
  }

  public void setCountry(String s)
  {
    metadataImpl.setExtra(role.getField() + COUNTRY, s);
  }
}
