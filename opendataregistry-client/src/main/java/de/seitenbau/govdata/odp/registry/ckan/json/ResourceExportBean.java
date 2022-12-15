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

package de.seitenbau.govdata.odp.registry.ckan.json;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
@Data
public class ResourceExportBean implements Serializable
{
    private static final long serialVersionUID = -1474098456725033104L;

    @JsonProperty
    private String id;

    @JsonProperty
    private String name;

    @JsonProperty
    private String resource_type;

    @JsonProperty
    private String description;

    @JsonProperty
    private String format;

    @JsonProperty
    private Date created;

    @JsonProperty
    private String url;

    @JsonProperty
    private String hash;

    @JsonProperty
    private long size;

    @JsonProperty
    private String state;

    /**
     * Will contain the key:value pairs for:
         private String language
         private String license
         private Date issued
         private Date modified;
         private String licenseAttributionByText;
         private String plannedAvailability;
         private String documentation;
         private String conforms_to;
         private String download_url;
         private String status;
         private String mimetype;
         private String rights;
         private String hash_algorithm;
     */
    @JsonProperty
    private Map<String, String> __extras;
}
