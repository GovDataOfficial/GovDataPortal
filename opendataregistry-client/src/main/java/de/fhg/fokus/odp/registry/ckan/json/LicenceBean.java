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

package de.fhg.fokus.odp.registry.ckan.json;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

// TODO: Auto-generated Javadoc
/**
 * The Class LicenceBean.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class LicenceBean implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 7316836055751291814L;

    /** The id. */
    @JsonProperty
    private String id;

    /** The title. */
    @JsonProperty
    private String title;

    /** The url. */
    @JsonProperty
    private String url;

    /** The status. */
    @JsonProperty
    private String status;

    /** The maintainer. */
    @JsonProperty
    private String maintainer;

    /** The family. */
    @JsonProperty
    private String family;

    /** The domain_content. */
    @JsonProperty
    private boolean domain_content;

    /** The domain_data. */
    @JsonProperty
    private boolean domain_data;

    /** The domain_software. */
    @JsonProperty
    private boolean domain_software;

    /** The is_okd_compliant. */
    @JsonProperty
    private boolean is_okd_compliant;

    /** The is_osi_compliant. */
    @JsonProperty
    private boolean is_osi_compliant;

    /**
     * Checks if is domain_content.
     * 
     * @return true, if is domain_content
     */
    public boolean isDomain_content() {
        return domain_content;
    }

    /**
     * Sets the domain_content.
     * 
     * @param domain_content
     *            the new domain_content
     */
    public void setDomain_content(boolean domain_content) {
        this.domain_content = domain_content;
    }

    /**
     * Checks if is domain_data.
     * 
     * @return true, if is domain_data
     */
    public boolean isDomain_data() {
        return domain_data;
    }

    /**
     * Sets the domain_data.
     * 
     * @param domain_data
     *            the new domain_data
     */
    public void setDomain_data(boolean domain_data) {
        this.domain_data = domain_data;
    }

    /**
     * Checks if is domain_software.
     * 
     * @return true, if is domain_software
     */
    public boolean isDomain_software() {
        return domain_software;
    }

    /**
     * Sets the domain_software.
     * 
     * @param domain_software
     *            the new domain_software
     */
    public void setDomain_software(boolean domain_software) {
        this.domain_software = domain_software;
    }

    /**
     * Gets the id.
     * 
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the id.
     * 
     * @param id
     *            the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the title.
     * 
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title.
     * 
     * @param title
     *            the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets the url.
     * 
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets the url.
     * 
     * @param url
     *            the url to set
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Gets the status.
     * 
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the status.
     * 
     * @param status
     *            the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Gets the maintainer.
     * 
     * @return the maintainer
     */
    public String getMaintainer() {
        return maintainer;
    }

    /**
     * Sets the maintainer.
     * 
     * @param maintainer
     *            the maintainer to set
     */
    public void setMaintainer(String maintainer) {
        this.maintainer = maintainer;
    }

    /**
     * Gets the family.
     * 
     * @return the family
     */
    public String getFamily() {
        return family;
    }

    /**
     * Sets the family.
     * 
     * @param family
     *            the family to set
     */
    public void setFamily(String family) {
        this.family = family;
    }

    /**
     * Checks if is is_okd_compliant.
     * 
     * @return true, if is is_okd_compliant
     */
    public boolean isIs_okd_compliant() {
        return is_okd_compliant;
    }

    /**
     * Sets the is_okd_compliant.
     * 
     * @param is_okd_compliant
     *            the new is_okd_compliant
     */
    public void setIs_okd_compliant(boolean is_okd_compliant) {
        this.is_okd_compliant = is_okd_compliant;
    }

    /**
     * Checks if is is_osi_compliant.
     * 
     * @return true, if is is_osi_compliant
     */
    public boolean isIs_osi_compliant() {
        return is_osi_compliant;
    }

    /**
     * Sets the is_osi_compliant.
     * 
     * @param is_osi_compliant
     *            the new is_osi_compliant
     */
    public void setIs_osi_compliant(boolean is_osi_compliant) {
        this.is_osi_compliant = is_osi_compliant;
    }

}
