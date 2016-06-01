package de.seitenbau.govdata.dataset.details.beans;

import java.util.List;

/**
 * Copyright (c) 2012, 2015 Fraunhofer Institute FOKUS
 *
 * This file is part of Open Data Platform.
 *
 * Open Data Platform is free software: you can redistribute it and/or modify it under the terms of
 * the GNU Affero General Public License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 * 
 * Open Data Plaform is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with Open Data
 * Platform. If not, see <http://www.gnu.org/licenses/agpl-3.0>.
 */

/**
 * CurrentMetadataContact.
 * 
 * @author bdi,msg
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fhg.fokus.odp.registry.model.Contact;
import de.fhg.fokus.odp.registry.model.Metadata;
import de.fhg.fokus.odp.registry.model.RoleEnumType;
import de.fhg.fokus.odp.registry.model.exception.OpenDataRegistryException;
import de.fhg.fokus.odp.registry.model.exception.UnknownRoleException;

/**
 * The Class ODRClientImpl.
 * 
 * @author msg
 */
public class CurrentMetadataContact
{

  private static final String EMPTY_STRING = "";

  public String name = EMPTY_STRING;

  public String email = EMPTY_STRING;

  private static final Logger log = LoggerFactory.getLogger(CurrentMetadataContact.class);

  // this is the order in which the contact is searched. The first available is taken.
  private final static RoleEnumType[] roles = new RoleEnumType[] {RoleEnumType.AUTHOR, RoleEnumType.PUBLISHER, RoleEnumType.MAINTAINER};

  public static boolean isNullOrEmpty(String s)
  {
    return s == null || s.length() == 0;
  }
  
  public CurrentMetadataContact(List<Contact> contacts, String author, String author_email) {
    try
    {
      findContact(contacts, author, author_email);
    }
    catch (OpenDataRegistryException | UnknownRoleException e)
    {
      log.error("Error while processing metadata contact: ", e);
    }
  }
  
  public CurrentMetadataContact(Metadata metadata)
  {
    try
    {
      if (metadata != null)
      {
       findContact(metadata.getContacts(), metadata.getAuthor(), null);
      }
    }
    catch (OpenDataRegistryException | UnknownRoleException e)
    {
      log.error("Error while processing metadata contact: ", e);
    }
  }
  
  private void findContact(List<Contact> contacts, String author, String authorEmail)
      throws OpenDataRegistryException, UnknownRoleException {
    for (RoleEnumType role : roles)
    {
      if (tryFetchContact(contacts, author, authorEmail, role))
      {
        break; // we have found our contact, let's stop.
      }
    }
  }

  private boolean tryFetchContact(List<Contact> contacts, String author, String authorEmail, RoleEnumType role)
      throws OpenDataRegistryException, UnknownRoleException
  {
    for(Contact contact : contacts) {
      if(contact.getRole() == role) {
        // consider special case: The author of metadata has priority
        if (role == RoleEnumType.AUTHOR && !isNullOrEmpty(author))
        {
          this.name = author;
        }
        else
        {
          this.name = (!isNullOrEmpty(contact.getName()) ? contact.getName() : "");
        }

        this.email = (!isNullOrEmpty(contact.getEmail()) ? contact.getEmail() : "");
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
