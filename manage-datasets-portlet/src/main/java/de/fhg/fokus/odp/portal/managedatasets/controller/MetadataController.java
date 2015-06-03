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

package de.fhg.fokus.odp.portal.managedatasets.controller;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liferay.faces.portal.context.LiferayFacesContext;

import de.fhg.fokus.odp.registry.model.Metadata;

/**
 * @author Benjamin Dittwald
 * 
 */
@ManagedBean(name = "metadataController")
@ViewScoped
public class MetadataController implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The logger. */
	private final Logger LOG = LoggerFactory.getLogger(getClass());

	/** The metadata. */
	private Metadata metadata;

	@PostConstruct
	public void init() {
		LiferayFacesContext lfc = LiferayFacesContext.getInstance();
		PortletRequest request = (PortletRequest) lfc.getExternalContext()
				.getRequest();
		PortletSession session = request.getPortletSession();
		metadata = (Metadata) session.getAttribute("metadata");
		if (metadata != null)
			LOG.debug("Loaded metadata: {}", metadata.getName());
	}

	public Metadata getMetadata() {
		return metadata;
	}

	public void setMetadata(Metadata metadata) {
		this.metadata = metadata;
	}

}
