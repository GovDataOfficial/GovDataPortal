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
package de.fhg.fokus.odp.portal.datasets;

import java.io.Serializable;
import java.util.List;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import com.liferay.portal.kernel.util.PropsUtil;

import de.fhg.fokus.odp.registry.ODRClient;
import de.fhg.fokus.odp.registry.model.Category;
import de.fhg.fokus.odp.registry.model.Licence;
import de.fhg.fokus.odp.spi.OpenDataRegistry;

/**
 * @author sim
 * 
 */
@ManagedBean
@ApplicationScoped
public class RegistryClient implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4779925095121064872L;

	private static final String PROP_NAME_AUTHORIZATION_KEY = "authenticationKey";
	private static final String PROP_NAME_CKAN_URL = "cKANurl";
	private static final String PROP_NAME_DEFAULT_SORT_METADATA = "sorting.default.metadata";

	private ODRClient client;

	// simple caching for differentiate between groups and subgroups
	private List<Category> categories;

	private List<Licence> licences;

	@PostConstruct
	public void init() {
		client = OpenDataRegistry.getClient();
		Properties props = new Properties();
		props.setProperty("ckan.authorization.key",
				PropsUtil.get(PROP_NAME_AUTHORIZATION_KEY));
		props.setProperty("ckan.url", PropsUtil.get(PROP_NAME_CKAN_URL));
		props.setProperty("sorting.default.metadata",
				PropsUtil.get(PROP_NAME_DEFAULT_SORT_METADATA));
		client.init(props);
	}

	public ODRClient getInstance() {
		return client;
	}

	public List<Category> getCategories() {
		if (categories == null) {
			categories = client.listCategories();
		}
		return categories;
	}

	public List<Licence> getLicences() {
		if (licences == null) {
			licences = client.listLicenses();
		}
		return licences;
	}
}
