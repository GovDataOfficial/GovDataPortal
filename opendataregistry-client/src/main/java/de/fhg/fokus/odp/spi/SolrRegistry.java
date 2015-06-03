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

package de.fhg.fokus.odp.spi;

import java.util.ServiceLoader;
import de.fhg.fokus.odp.registry.solr.Constants;
import de.fhg.fokus.odp.registry.solr.SOLRClient;

/**
 * The Class SolrRegistry is the factory for the Solr Registry provider
 * interface.
 * 
 * <p>
 * Usage: <blockquote>
 * 
 * <pre>
 * SOLRClient client = SolrRegistry.getClient();
 * client.init();
 * </pre>
 * 
 * </blockquote> or: <blockquote>
 * 
 * <pre>
 * SOLRClient client = SolrRegistry.getClient(&quot;CKAN&quot;);
 * client.init();
 * </pre>
 * 
 * </blockquote>
 * 
 * @see SOLRClient
 * @author msg
 */

public abstract class SolrRegistry {


	/** The solr loader. */
	private static ServiceLoader<SolrRegistry> solrLoader = ServiceLoader
			.load(SolrRegistry.class);

	/**
	 * Gets the provider implementation for a given name of the solr client
	 * interface.
	 * 
	 * @param name
	 *            the name of the provider implementation
	 * @return the client interface
	 */
	public static synchronized SOLRClient getClient(String name) {
		if (name != null) {
			for (SolrRegistry op : solrLoader) {
				if (name.equals(op.getName())) {
					return op.createClient();
				}
			}
		}
		return null;
	}

	/**
	 * Gets the default provider implementation of the solr client interface,
	 * which is 'SOLR'.
	 * 
	 * @return the client interface
	 */
	public static synchronized SOLRClient getClient() {
		SOLRClient client = getClient(Constants.SOLR_PROVIDER_NAME);
		if (client == null) {
			for (SolrRegistry op : solrLoader) {
				client = op.createClient();
			}
		}
		return client;
	}

	/**
	 * Gets the name of the current solr provider implementation. Default
	 * provider is 'SOLR'.
	 * 
	 * @return the name of the current provider
	 */
	public abstract String getName();

	/**
	 * Creates the client.
	 * 
	 * @return the SOLR client
	 */
	public abstract SOLRClient createClient();

}
