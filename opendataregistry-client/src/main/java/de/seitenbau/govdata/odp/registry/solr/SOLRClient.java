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

package de.seitenbau.govdata.odp.registry.solr;

import java.util.List;
import java.util.Properties;

import de.seitenbau.govdata.odp.registry.solr.impl.MString;

/**
 * The Interface SOLRClient.
 * 
 * @author msg
 */
public interface SOLRClient {

	/**
	 * Inits the client implementation.
	 * 
	 * @throws SOLRException
	 *             If the implementation could not be initialized properly.
	 */
	void init() throws SolrException;

	/**
	 * Inits the client implementation with a set of properties. In case of the
	 * standard SOLR implementation the following properties can be set:
	 * 
	 * <pre>
	 *   ckan.url
	 *   ckan.authorization.key
	 * </pre>
	 * 
	 * @param props
	 *            the list of properties.
	 */
	void init(Properties props);

	/**
	 * Status. General status as String. The format and the contained
	 * information depend on the implementation. In case of the default SOLR
	 * implementation this is a JSON Object with standard SOLR Server
	 * information.
	 * 
	 * @return the status string
	 */

	/**
	 * Auto suggest solr.
	 * 
	 * @param fragment
	 *            the fragment
	 * @return the list
	 */
	List<MString> spellingSuggestions(String fragment);
}
