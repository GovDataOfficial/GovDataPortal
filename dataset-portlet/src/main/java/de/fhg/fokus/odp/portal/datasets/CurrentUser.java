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

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.portlet.ActionResponse;
import javax.portlet.PortletRequest;
import javax.xml.namespace.QName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liferay.faces.portal.context.LiferayFacesContext;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.model.Layout;
import com.liferay.portal.model.Role;
import com.liferay.portal.service.RoleLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;

import de.fhg.fokus.odp.registry.model.Metadata;
import de.fhg.fokus.odp.registry.model.MetadataEnumType;
import de.fhg.fokus.odp.registry.model.User;
import de.fhg.fokus.odp.registry.model.exception.OpenDataRegistryException;

/**
 * The Class CurrentUser.
 * 
 * @author sim
 */
@ManagedBean
@SessionScoped
public class CurrentUser implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3271983798009799515L;
	private static final Logger log = LoggerFactory
			.getLogger(CurrentUser.class);

	/** The user. */
	private User user;

	/** The registry client. */
	@ManagedProperty("#{registryClient}")
	private RegistryClient registryClient;

	/** The creator. */
	private Boolean creator;

	/** The new metadata button value. */
	private String newMetadataButtonValue;

	/**
	 * Sets the registry client.
	 * 
	 * @param registryClient
	 *            the new registry client
	 */
	public void setRegistryClient(RegistryClient registryClient) {
		this.registryClient = registryClient;
	}

	/**
	 * Inits the.
	 */
	@PostConstruct
	public void init() {
		PortletRequest request = (PortletRequest) FacesContext
				.getCurrentInstance().getExternalContext().getRequest();
		@SuppressWarnings("unchecked")
		Map<String, String> userInfo = (Map<String, String>) request
				.getAttribute(PortletRequest.USER_INFO);
		if (userInfo != null) {
			String loginid = userInfo.get("user.login.id");
			if (loginid != null) {
				user = registryClient.getInstance().findUser(
						loginid.toLowerCase());
				if (user == null) {
					user = registryClient.getInstance().createUser(
							loginid.toLowerCase(), "dummy@test.org", "dummy");
				}
			}
		}
	}

	/**
	 * Gets the user.
	 * 
	 * @return the user
	 */
	public User getUser() {
		return user;
	}

	/**
	 * Checks if is redakteur.
	 * 
	 * @return true, if is redakteur
	 * @throws SystemException
	 *             the system exception
	 */
	public boolean isRedakteur() throws SystemException {
		boolean result = false;
		ThemeDisplay themeDisplay = LiferayFacesContext.getInstance()
				.getThemeDisplay();
		com.liferay.portal.model.User user = themeDisplay.getUser();

		List<Role> roles = RoleLocalServiceUtil.getUserRoles(user.getUserId());

		String name;
		for (int i = 0; i < roles.size(); ++i) {
			name = roles.get(i).getName();
			if (name.equals("Redakteur")) {
				result = true;
			}
		}
		return result;
	}

	/**
	 * Checks if is loggedin.
	 * 
	 * @return true, if is loggedin
	 */
	public boolean isLoggedin() {
		return user != null;
	}

	/**
	 * Checks if is owner.
	 * 
	 * @param metadata
	 *            the metadata
	 * @return true, if is owner
	 */
	public boolean isOwner(Metadata metadata) {
		boolean result = false;

		if (user != null && metadata != null) {
			result = user.isOwner(metadata);
		}

		return result;
	}

	/**
	 * Checks if is editor.
	 * 
	 * @param metadata
	 *            the metadata
	 * @return true, if is editor
	 */
	public boolean isEditor(Metadata metadata) {
		if (metadata == null) {
			return false;
		}
		return user != null ? user.isEditor(metadata) : false;
	}

	/**
	 * Checks if is creator.
	 * 
	 * @return true, if is creator
	 */
	public boolean isCreator() {
		if (user != null && creator == null) {
			if (user.hasRole("editor")) {
				creator = Boolean.TRUE;
			} else {
				creator = Boolean.FALSE;
			}
		}
		return creator != null ? creator : false;
	}

	/**
	 * New metadata.
	 * 
	 * @param actionEvent
	 *            the action event
	 */
	public void newMetadata(ActionEvent actionEvent) {
		Metadata metadata;

		ThemeDisplay themeDisplay = LiferayFacesContext.getInstance()
				.getThemeDisplay();
		String currentPage = themeDisplay.getLayout().getFriendlyURL();
		if (currentPage.equals("/daten")) {
			metadata = registryClient.getInstance().createMetadata(
					MetadataEnumType.DATASET);
		} else if (currentPage.equals("/apps")) {
			metadata = registryClient.getInstance().createMetadata(
					MetadataEnumType.APPLICATION);
		} else {
			metadata = registryClient.getInstance().createMetadata(
					MetadataEnumType.DOCUMENT);
		}

		FacesContext facesContext = FacesContext.getCurrentInstance();
		Object responseObject = facesContext.getExternalContext().getResponse();
		if (responseObject != null && responseObject instanceof ActionResponse) {
			ActionResponse actionResponse = (ActionResponse) responseObject;
			actionResponse.setEvent(new QName(
					"http://fokus.fraunhofer.de/odplatform", "metadata"),
					metadata);
		}

		String location = themeDisplay.getPortalURL();

		Layout layout = themeDisplay.getLayout();
		try {
			if (layout.isPublicLayout()) {
				location += themeDisplay.getPathFriendlyURLPublic();
			}
			if (layout.hasScopeGroup()) {
				location += layout.getScopeGroup().getFriendlyURL();
			} else {
				location += layout.getGroup().getFriendlyURL();
			}

			location += "/bearbeiten";
		} catch (PortalException e) {
			e.printStackTrace();
		} catch (SystemException e) {
			e.printStackTrace();
		}

		try {
			facesContext.getExternalContext().redirect(location);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gets the new metadata button value.
	 * 
	 * @return the new metadata button value
	 */
	public String getNewMetadataButtonValue() {
		return newMetadataButtonValue;
	}

	/**
	 * Sets the new metadata button value.
	 * 
	 * @param newMetadataButtonValue
	 *            the new new metadata button value
	 */
	public void setNewMetadataButtonValue(String newMetadataButtonValue) {
		this.newMetadataButtonValue = newMetadataButtonValue;
	}

	/**
	 * Gets and build the metadatas list for user.
	 * 
	 * @return the metadatas list
	 * 
	 *         public List<Metadata> getMetadatas() {
	 * 
	 *         List<Metadata> metadatas = new ArrayList<Metadata>();
	 *         List<String> datasets = user.getDatasets(); for (String dataset :
	 *         datasets) { try { Metadata metadata =
	 *         registryClient.getInstance().getMetadata( user, dataset);
	 *         metadatas.add(metadata); } catch (OpenDataRegistryException e) {
	 *         e.printStackTrace(); } } return metadatas; }
	 */

	/**
	 * Gets and build the metadatas list for user.
	 * 
	 * @return the metadatas list
	 */
	public List<Metadata> getUserMetadatas(List<String> datasets) {

		List<Metadata> metadatas = new ArrayList<Metadata>();
		for (String dataset : datasets) {
			try {
				Metadata metadata = registryClient.getInstance().getMetadata(
						user, dataset);
				metadatas.add(metadata);
			} catch (OpenDataRegistryException e) {
				e.printStackTrace();
			}
		}
		return metadatas;
	}

	/**
	 * Gets and build the metadatas list for user.
	 * 
	 * @return the metadatas list
	 */
	public List<String> getUserDatasets() {

		return user.getDatasets();

	}

}
