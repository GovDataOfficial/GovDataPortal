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

package de.fhg.fokus.odp.registry.solr;

import static de.fhg.fokus.odp.registry.solr.Constants.*;
import de.fhg.fokus.odp.registry.solr.impl.SOLRClientImpl;
import de.fhg.fokus.odp.spi.SolrRegistry;

/**
 * The Class SolrProvider.
 * 
 * @author msg
 */
public class SolrProvider extends SolrRegistry {

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.fhg.fokus.odp.spi.SolrRegistry#getName()
	 */
	@Override
	public String getName() {
		return SOLR_PROVIDER_NAME;
	}

	public SOLRClient createClient() {
		return new SOLRClientImpl();
	}

}
