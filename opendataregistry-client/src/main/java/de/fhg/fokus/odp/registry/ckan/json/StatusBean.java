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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class StatusBean implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -4770874544412725609L;

    @JsonProperty
    private String ckan_version;

    @JsonProperty
    private String site_url;

    @JsonProperty
    private String site_description;

    @JsonProperty
    private String site_title;

    @JsonProperty
    private String error_emails_to;

    @JsonProperty
    private String locale_default;

    /**
     * @return the ckan_version
     */
    public String getCkan_version() {
        return ckan_version;
    }

    /**
     * @param ckan_version
     *            the ckan_version to set
     */
    public void setCkan_version(String ckan_version) {
        this.ckan_version = ckan_version;
    }

    /**
     * @return the site_url
     */
    public String getSite_url() {
        return site_url;
    }

    /**
     * @param site_url
     *            the site_url to set
     */
    public void setSite_url(String site_url) {
        this.site_url = site_url;
    }

    /**
     * @return the site_description
     */
    public String getSite_description() {
        return site_description;
    }

    /**
     * @param site_description
     *            the site_description to set
     */
    public void setSite_description(String site_description) {
        this.site_description = site_description;
    }

    /**
     * @return the site_title
     */
    public String getSite_title() {
        return site_title;
    }

    /**
     * @param site_title
     *            the site_title to set
     */
    public void setSite_title(String site_title) {
        this.site_title = site_title;
    }

    /**
     * @return the error_emails_to
     */
    public String getError_emails_to() {
        return error_emails_to;
    }

    /**
     * @param error_emails_to
     *            the error_emails_to to set
     */
    public void setError_emails_to(String error_emails_to) {
        this.error_emails_to = error_emails_to;
    }

    /**
     * @return the locale_default
     */
    public String getLocale_default() {
        return locale_default;
    }

    /**
     * @param locale_default
     *            the locale_default to set
     */
    public void setLocale_default(String locale_default) {
        this.locale_default = locale_default;
    }

}
