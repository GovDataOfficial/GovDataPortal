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

import org.codehaus.jackson.JsonNode;

import de.fhg.fokus.odp.registry.ckan.json.LicenceBean;
import de.fhg.fokus.odp.registry.model.Licence;

// TODO: Auto-generated Javadoc
/**
 * The Class LicenceImpl.
 * 
 * @author sim
 */
public class LicenceImpl implements Licence, Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -6730255931161315851L;

	/** The licence. */
	private final LicenceBean licence;

	/** The other. */
	private String other;

	/**
	 * Instantiates a new licence impl.
	 * 
	 * @param licence
	 *            the licence
	 */
	public LicenceImpl(LicenceBean licence) {
		this.licence = licence;
	}

	/**
	 * Gets the title.
	 * 
	 * @return the title
	 */
	@Override
	public String getTitle() {
		return licence.getTitle();
	}

	/**
	 * Gets the name.
	 * 
	 * @return the name
	 */
	@Override
	public String getName() {
		return licence.getId();
	}

	/**
	 * Gets the url.
	 * 
	 * @return the url
	 */
	@Override
	public String getUrl() {
		return licence.getUrl();
	}

	/**
	 * Gets the other.
	 * 
	 * @return the other
	 */
	@Override
	public String getOther() {
		return other;
	}

	/**
	 * Sets the other.
	 * 
	 * @param other
	 *            the other to set
	 */
	@Override
	public void setOther(String other) {
		this.other = other;
	}

	/**
	 * Read.
	 * 
	 * @param licence
	 *            the licence
	 * @return the licence
	 */
	public static Licence read(JsonNode licence) {
		LicenceBean bean = new LicenceBean();

		if (licence != null) {
			JsonNode id = licence.get("license_id");
			bean.setId(id != null ? id.getTextValue() : null);

			// just mirror the id :-(
			bean.setTitle(bean.getId());

			JsonNode url = licence.get("license_url");
			bean.setUrl(url != null ? url.getTextValue() : null);

			// JsonNode is_free_to_use = licence.get("is_free_to_use");
			// if (is_free_to_use != null) {
			// bean.setIs_okd_compliant(is_free_to_use.getBooleanValue());
			// }
		}

		LicenceImpl impl = new LicenceImpl(bean);
		if (licence != null && licence.get("other") != null) {
			impl.setOther(licence.get("other").getTextValue());
		}

		return impl;
	}

	/**
	 * Sets the title.
	 * 
	 * @param title
	 *            the new title
	 */
	@Override
	public void setTitle(String title) {
		licence.setTitle(title);
	}

	/**
	 * Checks if is domain content.
	 * 
	 * @return true, if is domain content
	 */
	@Override
	public boolean isDomainContent() {
		return licence.isDomain_content();
	}

	/**
	 * Checks if is domain data.
	 * 
	 * @return true, if is domain data
	 */
	@Override
	public boolean isDomainData() {
		return licence.isDomain_data();
	}

	/**
	 * Checks if is domain software.
	 * 
	 * @return true, if is domain software
	 */
	@Override
	public boolean isDomainSoftware() {
		return licence.isDomain_software();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.fhg.fokus.odp.registry.model.Licence#isOkdCompliant()
	 */
	@Override
	public boolean isOkdCompliant() {
		return licence.isIs_okd_compliant();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.fhg.fokus.odp.registry.model.Licence#isOsiCompliant()
	 */
	@Override
	public boolean isOsiCompliant() {
		return licence.isIs_osi_compliant();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.fhg.fokus.odp.registry.model.Licence#isOpen()
	 */
	@Override
	public boolean isOpen() {
		return isOkdCompliant() || isOsiCompliant();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.fhg.fokus.odp.registry.model.Licence#setUrl(java.lang.String)
	 */
	@Override
	public void setUrl(String url) {
		licence.setUrl(url);

	}

}
