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

package de.seitenbau.govdata.odp.registry.model;

// TODO: Auto-generated Javadoc
/**
 * The Interface Licence.
 */
public interface Licence {

	/**
	 * Gets the title.
	 * 
	 * @return the title
	 */
	String getTitle();

	/**
	 * Gets the name.
	 * 
	 * @return the name
	 */
	String getName();

	/**
	 * Gets the url.
	 * 
	 * @return the url
	 */
	String getUrl();

	/**
	 * Gets the other.
	 * 
	 * @return the other
	 */
	String getOther();

	/**
	 * Checks if is domain content.
	 * 
	 * @return true, if is domain content
	 */
	boolean isDomainContent();

	/**
	 * Checks if is domain data.
	 * 
	 * @return true, if is domain data
	 */
	boolean isDomainData();

	/**
	 * Checks if is domain software.
	 * 
	 * @return true, if is domain software
	 */
	boolean isDomainSoftware();

	/**
	 * Checks if is okd compliant.
	 * 
	 * @return true, if is okd compliant
	 */
	boolean isOkdCompliant();

	/**
	 * Checks if is osi compliant.
	 * 
	 * @return true, if is osi compliant
	 */
	boolean isOsiCompliant();

	/**
	 * Checks if is open.
	 * 
	 * @return true, if is open
	 */
	boolean isOpen();

	/**
	 * Sets the other.
	 * 
	 * @param other
	 *            the new other
	 */
	void setOther(String other);

	/**
	 * Sets the title.
	 * 
	 * @param title
	 *            the new title
	 */
	void setTitle(String title);

	/**
	 * Sets the url.
	 * 
	 * @param url
	 *            the new url
	 */
	void setUrl(String url);

}
