/**
 * Copyright (c) 2012, 2013 Fraunhofer Institute FOKUS
 *
 * This file is part of Open Data Platform.
 *
 * Open Data Platform is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * Open Data Plaform is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.

 * You should have received a copy of the GNU Affero General Public License
 * along with Open Data Platform.  If not, see <http://www.gnu.org/licenses/agpl-3.0>.
 */

/**
 * 
 */
package de.fhg.fokus.odp.registry.ckan.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import de.fhg.fokus.odp.registry.ckan.ODRClientImpl;
import de.fhg.fokus.odp.registry.ckan.json.ContactBean;
import de.fhg.fokus.odp.registry.model.Contact;
import de.fhg.fokus.odp.registry.model.RoleEnumType;
import de.fhg.fokus.odp.registry.model.exception.UnknownRoleException;

/**
 * @author sim
 * 
 */
public class ContactImpl implements Contact, Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 8803813082217604877L;

    private final ContactBean contact;

    public ContactImpl(ContactBean contact) {
        this.contact = contact;
    }

    @Override
    public String getName() {
        return contact.getName();
    }

    @Override
    public void setName(String name) {
        contact.setName(name);
    }

    @Override
    public RoleEnumType getRole() throws UnknownRoleException {
        String role = contact.getRole();
        return RoleEnumType.fromField(role);
    }

    @Override
    public void setRole(RoleEnumType role) {
        contact.setRole(role.toField());
    }

    @Override
    public String getUrl() {
        return contact.getUrl();
    }

    @Override
    public void setUrl(String url) {
        contact.setUrl(url);
    }

    @Override
    public String getEmail() {
        return contact.getEmail();
    }

    @Override
    public void setEmail(String email) {
        contact.setEmail(email);
    }

    @Override
    public String getAddress() {
        return contact.getAddress();
    }

    @Override
    public void setAddress(String address) {
        contact.setAddress(address);
    }

    public static List<Contact> read(JsonNode contacts) {
        List<Contact> contactList = new ArrayList<Contact>();

        if (contacts != null && contacts.isArray()) {
            for (JsonNode contactNode : contacts) {
                ContactBean contactBean = ODRClientImpl.convert(contactNode, ContactBean.class);
                contactList.add(new ContactImpl(contactBean));
            }
        }
        return contactList;
    }
    
    public String toString() {
      try
      {
        return "{name: «" +this.getName() + "», email: «" + this.getEmail() + "», role: «" + this.getRole() + "»}";
      }
      catch (UnknownRoleException e)
      {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      return null;
    }

  public JsonNode write()
  {
    return ODRClientImpl.convert(contact);
  }

}
