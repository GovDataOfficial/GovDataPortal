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

package de.fhg.fokus.odp.spi;

import java.util.ServiceLoader;

import de.fhg.fokus.odp.registry.ODRClient;
import de.fhg.fokus.odp.registry.ckan.Constants;

/**
 * The Class OpenDataRegistry is the factory for the Open Data Registry provider interface.
 * 
 * <p>
 * Usage: <blockquote>
 * 
 * <pre>
 * ODRClient client = OpenDataRegistry.getClient();
 * client.init();
 * </pre>
 * 
 * </blockquote> or: <blockquote>
 * 
 * <pre>
 * ODRClient client = OpenDataRegistry.getClient(&quot;CKAN&quot;);
 * client.init();
 * </pre>
 * 
 * </blockquote>
 * 
 * @see ODRClient
 */
public abstract class OpenDataRegistry {

    /** The odr loader. */
    private static ServiceLoader<OpenDataRegistry> odrLoader = ServiceLoader.load(OpenDataRegistry.class);

    /**
     * Gets the provider implementation for a given name of the open data registry client interface.
     * 
     * @param name
     *            the name of the provider implementation
     * @return the client interface
     */
    public static synchronized ODRClient getClient(String name) {
        if (name != null) {
            for (OpenDataRegistry op : odrLoader) {
                if (name.equals(op.getName())) {
                    return op.createClient();
                }
            }
        }
        return null;
    }

    /**
     * Gets the default provider implementation of the open data registry client interface, which is 'CKAN'.
     * 
     * @return the client interface
     */
    public static synchronized ODRClient getClient() {
        ODRClient client = getClient(Constants.OPEN_DATA_PROVIDER_NAME);
        if (client == null) {
            for (OpenDataRegistry op : odrLoader) {
                client = op.createClient();
                // if there a several implementations available, check, if this
                // is a suitable one
            }
        }
        return client;
    }

    /**
     * Gets the name of the current open data registry provider implementation. Default provider is 'CKAN'.
     * 
     * @return the name of the current provider
     */
    public abstract String getName();

    /**
     * Creates the client.
     * 
     * @return the oDR client
     */
    public abstract ODRClient createClient();

}
