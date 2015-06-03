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
package de.fhg.fokus.odp.registry.ckan.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.fhg.fokus.odp.registry.ODRClient;
import de.fhg.fokus.odp.registry.ckan.json.UserBean;
import de.fhg.fokus.odp.registry.model.Metadata;
import de.fhg.fokus.odp.registry.model.User;

/**
 * @author sim
 * 
 */
public class UserImpl implements User, Serializable {

	/**
     * 
     */
	private static final long serialVersionUID = -9199121089537222860L;

	private UserBean user;

	private transient ODRClient client;

	private List<String> datasets;

	public UserImpl(UserBean user, ODRClient client) {
		this.user = user;
		this.client = client;
	}

	// @Override
	public String getApikey() {
		return user.getApikey();
	}

	@Override
	public String getName() {
		return user.getName();
	}

	@Override
	public String getFullname() {
		return user.getFullname();
	}

	@Override
	public String getDisplayName() {
		return user.getDisplay_name();
	}

	@Override
	public String getEmail() {
		return user.getEmail();
	}

	@Override
	public List<String> getDatasets() {
		if (datasets == null) {
			datasets = new ArrayList<String>();
		}
		return datasets;
	}

	@Override
	public void rateMetadata(String metadata, int rate) {
		client.rateMetadata(this, metadata, rate);
	}

	@Override
	public boolean isOwner(Metadata metadata) {
		return getDatasets().contains(metadata.getName());
	}

	@Override
	public boolean isEditor(Metadata metadata) {
		return hasRole("admin", metadata);
	}

	@Override
	public boolean hasRole(String role, Metadata metadata) {
		List<String> roles = client.showRoles(this, metadata.getName());
		return roles.contains(role);
	}

	@Override
	public boolean hasRole(String role) {
		List<String> roles = client.showRoles(this, "system");
		return roles.contains(role);
	}

	@Override
	public void updateRole(String role) {
		client.updateRoles(this, "system", Collections.singletonList(role));
	}

	@Override
	public void updateRoles(List<String> roles) {
		client.updateRoles(this, "system", roles);
	}

	@Override
	public List<String> showRoles() {
		List<String> roles = client.showRoles(this, "system");
		return roles;
	}

}
