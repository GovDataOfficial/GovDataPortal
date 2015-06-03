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

package de.fhg.fokus.odp.registry.model;

import de.fhg.fokus.odp.registry.model.exception.UnknownRoleException;

/**
 * The Enum RoleEnumType.
 * 
 * @author sim,msg
 */
public enum RoleEnumType {

	/** The author. */
	AUTHOR("autor", "Autor"),
	/** The publication. */
	PUBLISHER("veroeffentlichende_stelle", "Veröffentlichende Stelle"),
	/** The maintainer. */
	MAINTAINER("ansprechpartner", "Ansprechpartner"),
	/** The distribution. */
	DISTRIBUTOR("vertrieb", "Vertrieb");

	/** The field. */
	private String field;

	/** The display name. */
	private String displayName;

	/**
	 * Instantiates a new role enum type.
	 * 
	 * @param field
	 *            the field
	 * @param displayName
	 *            the display name
	 */
	private RoleEnumType(String field, String displayName) {
		this.field = field;
		this.displayName = displayName;
	}

	/**
	 * To field.
	 * 
	 * @return the string
	 */
	public String toField() {
		return field;
	}

	/**
	 * Gets the display name.
	 * 
	 * @return the display name
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * From field.
	 * 
	 * @param type
	 *            the type
	 * @return the metadata enum type
	 * @throws UnknownRoleException
	 *             the Unknown Role Exception
	 */
	public static RoleEnumType fromField(String type)
			throws UnknownRoleException {
		if (type.isEmpty())
			throw new UnknownRoleException("-> role type is empty <-");
		type = type.trim().toLowerCase();
		if (AUTHOR.toField().equals(type)) {
			return AUTHOR;
		} else if (PUBLISHER.toField().equals(type)) {
			return PUBLISHER;
		} else if (MAINTAINER.toField().equals(type)) {
			return MAINTAINER;
		} else if (DISTRIBUTOR.toField().equals(type)) {
			return DISTRIBUTOR;
		} else {
			throw new UnknownRoleException("->" + type + "<-");
		}
	}
}
