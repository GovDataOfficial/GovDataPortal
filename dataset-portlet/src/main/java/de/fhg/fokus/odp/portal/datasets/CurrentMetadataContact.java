package de.fhg.fokus.odp.portal.datasets;

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
 * CurrentMetadataContact.
 * 
 * @author bdi,msg
 */

import de.fhg.fokus.odp.registry.model.Metadata;
import de.fhg.fokus.odp.registry.model.RoleEnumType;
import de.fhg.fokus.odp.registry.model.exception.OpenDataRegistryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class ODRClientImpl.
 * 
 * @author msg
 */
public class CurrentMetadataContact {

	private static final String EMPTY_STRING = "";
	public String name = EMPTY_STRING;
	public String email = EMPTY_STRING;

	private static final Logger log = LoggerFactory
			.getLogger(CurrentMetadataContact.class);

	public static boolean isNullOrEmpty(String s) {
		return s == null || s.length() == 0;
	}

	public CurrentMetadataContact(Metadata metadata)
			throws OpenDataRegistryException {
		try {
			// AUTHOR
			if (metadata != null && !isNullOrEmpty(metadata.getAuthor())
					&& metadata.getContact(RoleEnumType.AUTHOR) != null) {
				if (!isNullOrEmpty(metadata.getAuthor())) {
					this.name = metadata.getAuthor();
				} else if (!isNullOrEmpty(metadata.getContact(
						RoleEnumType.AUTHOR).getName())) {
					this.name = metadata.getContact(RoleEnumType.AUTHOR)
							.getName();
				}
				if (!isNullOrEmpty(metadata.getContact(RoleEnumType.AUTHOR)
						.getEmail())) {
					this.email = metadata.getContact(RoleEnumType.AUTHOR)
							.getEmail();
				}
				// PUBLISHER
			} else if (metadata != null
					&& metadata.getContact(RoleEnumType.PUBLISHER) != null) {
				if (!isNullOrEmpty(metadata.getContact(RoleEnumType.PUBLISHER)
						.getName())) {
					this.name = metadata.getContact(RoleEnumType.PUBLISHER)
							.getName();
				}
				if (!isNullOrEmpty(metadata.getContact(RoleEnumType.PUBLISHER)
						.getEmail())) {
					this.email = metadata.getContact(RoleEnumType.PUBLISHER)
							.getEmail();
				}
				// MAINTAINER
			} else if (metadata != null
					&& metadata.getContact(RoleEnumType.MAINTAINER) != null) {
				if (!isNullOrEmpty(metadata.getContact(RoleEnumType.MAINTAINER)
						.getName())) {
					this.name = metadata.getContact(RoleEnumType.MAINTAINER)
							.getName();
				}
				if (!isNullOrEmpty(metadata.getContact(RoleEnumType.MAINTAINER)
						.getEmail())) {
					this.email = metadata.getContact(RoleEnumType.MAINTAINER)
							.getEmail();
				}
			}
		} catch (OpenDataRegistryException ex) {
			log.error("Error while processing metadata contact: ", ex);
		}
	}

	@Override
	public String toString() {
		return this.name + " " + this.email;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}
