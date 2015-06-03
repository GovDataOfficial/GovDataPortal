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
 * 
 */
package de.fhg.fokus.odp.portal.searchgui;

import java.io.Serializable;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

import com.liferay.portal.kernel.util.PropsUtil;

import de.fhg.fokus.odp.registry.solr.SOLRClient;
import de.fhg.fokus.odp.spi.SolrRegistry;

/**
 * @author msg
 * 
 */
@ManagedBean
@ApplicationScoped
public class SolrClient implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4778825096121064872L;

	private static final String PROPERTY_NAME_SOLR_URL = "sOLRurl";

	private SOLRClient solrclient;

	@PostConstruct
	public void init() {
		solrclient = SolrRegistry.getClient();
		Properties props = new Properties();
		props.setProperty("solr.url", PropsUtil.get(PROPERTY_NAME_SOLR_URL));
		solrclient.init(props);
	}

	public SOLRClient getInstance() {
		return solrclient;
	}

}
