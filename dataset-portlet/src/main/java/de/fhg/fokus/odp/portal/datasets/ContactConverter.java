/**
 * Copyright (c) 2012, 2015 Fraunhofer Institute FOKUS
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
 * Contact Converter.
 * 
 * @author bdi,msg
 */
package de.fhg.fokus.odp.portal.datasets;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fhg.fokus.odp.registry.model.Metadata;
import de.fhg.fokus.odp.registry.model.exception.OpenDataRegistryException;

@FacesConverter("contactConverter")
public class ContactConverter implements Converter {

	private static final Logger log = LoggerFactory
			.getLogger(ContactConverter.class);

	@Override
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2) {
		   return null;
	}

	@Override
	public String getAsString(FacesContext arg0, UIComponent arg1, Object arg2) {

		CurrentMetadataContact contact = null;

		try {
			contact = new CurrentMetadataContact((Metadata) arg2);
		} catch (OpenDataRegistryException e) {
			log.error("Cannot convert from metadata to contact: ", e);
		}

		return contact.toString();
	}

}
