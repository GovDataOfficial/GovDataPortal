package de.seitenbau.govdata.odp.registry.ckan.impl;

import org.apache.commons.lang3.StringUtils;

import de.seitenbau.govdata.odp.registry.model.Contact;
import de.seitenbau.govdata.odp.registry.model.RoleEnumType;

/**
 * Provides a wrapper for accessing and manipulating a contact.
 */
public class ContactImpl implements Contact
{
  private static final String CONTACT_URL_SUFFIX = "_url";

  private static final String CONTACT_NAME_SUFFIX = "_name";

  private static final String CONTACT_EMAIL_SUFFIX = "_email";

  private RoleEnumType role;

  private MetadataImpl metadataImpl;

  private ContactAddress contactAddress;

  ContactImpl(MetadataImpl metadata, RoleEnumType role)
  {
    this.metadataImpl = metadata;
    this.role = role;
  }

  @Override
  public String getName()
  {
    if (role == RoleEnumType.CREATOR)
    {
      return metadataImpl.getAuthor();
    }
    else if (role == RoleEnumType.MAINTAINER)
    {
      return metadataImpl.getMaintainer();
    }
    else
    {
      return metadataImpl.getExtra(role.getField() + CONTACT_NAME_SUFFIX);
    }
  }

  @Override
  public void setName(String name)
  {
    if (role == RoleEnumType.CREATOR)
    {
      metadataImpl.setAuthor(name);
    }
    else if (role == RoleEnumType.MAINTAINER)
    {
      metadataImpl.setMaintainer(name);
    }
    else
    {
      metadataImpl.setExtra(role.getField() + CONTACT_NAME_SUFFIX, name);
    }
  }

  @Override
  public RoleEnumType getRole()
  {
    return role;
  }

  @Override
  public void setRole(RoleEnumType role)
  {
    this.role = role;
  }

  @Override
  public String getUrl()
  {
    return metadataImpl.getExtra(role.getField() + CONTACT_URL_SUFFIX);
  }

  @Override
  public void setUrl(String url)
  {
    metadataImpl.setExtra(role.getField() + CONTACT_URL_SUFFIX, url);
  }

  @Override
  public String getEmail()
  {
    if (role == RoleEnumType.CREATOR)
    {
      return metadataImpl.getAuthor_email();
    }
    else if (role == RoleEnumType.MAINTAINER)
    {
      return metadataImpl.getMaintainer_email();
    }
    else
    {
      return metadataImpl.getExtra(role.getField() + CONTACT_EMAIL_SUFFIX);
    }
  }

  @Override
  public void setEmail(String email)
  {
    if (role == RoleEnumType.CREATOR)
    {
      metadataImpl.setAuthor_email(email);
    }
    else if (role == RoleEnumType.MAINTAINER)
    {
      metadataImpl.setMaintainer_email(email);
    }
    else
    {
      metadataImpl.setExtra(role.getField() + CONTACT_EMAIL_SUFFIX, email);
    }
  }

  @Override
  public ContactAddress getAddress()
  {
    // return facade for the address of this contact
    if (contactAddress == null)
    {
      contactAddress = new ContactAddress(metadataImpl, role);
    }

    return contactAddress;
  }

  @Override
  public boolean exists()
  {
    // ignore Address in this check, it will never appear standalone.
    return StringUtils.isNotEmpty(getName()) || StringUtils.isNotEmpty(getEmail())
        || StringUtils.isNotEmpty(getUrl());
  }

  @Override
  public String toString()
  {
    return "{name: «" + this.getName() + "», email: «" + this.getEmail() + "», role: «" + this.getRole()
        + "»}";
  }
}
