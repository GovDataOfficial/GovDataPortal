package de.seitenbau.govdata.dataset.details.beans;

import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import de.seitenbau.govdata.odp.registry.model.Contact;
import de.seitenbau.govdata.odp.registry.model.Metadata;
import de.seitenbau.govdata.odp.registry.model.RoleEnumType;

/**
 * CurrentMetadataContact.
 * 
 * @author rnoerenberg
 * @author bdi,msg
 */
public class CurrentMetadataContact
{
  private String name;

  private String email;

  /** this is the order in which the contact is searched. The first available is taken. */
  private final RoleEnumType[] roles = new RoleEnumType[] {RoleEnumType.PUBLISHER};
  
  /**
   * Konstruktur mit einem Parameter {@link Metadata}.
   * 
   * @param metadata das {@link Metadata}-Objekt, aus dem die Kontakte gelesen werden sollen.
   */
  public CurrentMetadataContact(Metadata metadata)
  {
    if (metadata != null)
    {
      for (RoleEnumType role : roles)
      {
        if (tryFetchContact(metadata.getContacts(), role))
        {
          break; // we have found our contact, let's stop.
        }
      }
    }
  }

  private boolean tryFetchContact(List<Contact> contacts, RoleEnumType role)
  {
    for (Contact contact : contacts)
    {
      if (Objects.nonNull(role) && Objects.equals(contact.getRole(), role))
      {
        if (StringUtils.isNotEmpty(contact.getName()))
        {
          this.name =  contact.getName();
        }
        if (StringUtils.isNotEmpty(contact.getEmail()))
        {
          this.email = contact.getEmail();
        }
        return true; // we found it!
      }
    }
    return false;
  }

  @Override
  public String toString()
  {
    return this.name;
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public String getEmail()
  {
    return email;
  }

  public void setEmail(String email)
  {
    this.email = email;
  }
}
