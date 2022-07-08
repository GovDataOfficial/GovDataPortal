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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import de.seitenbau.govdata.odp.registry.date.DateDeserializer;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
@Data
public class ResourceBean implements Serializable
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
    private String language;

    @JsonProperty
    @JsonDeserialize(using = DateDeserializer.class)
    private Date created;

    @JsonProperty
    @JsonDeserialize(using = DateDeserializer.class)
    private Date last_modified;

    @JsonProperty
    private String url;

    @JsonProperty
    private String hash;

    @JsonProperty
    private long size;

    @JsonProperty
    private String state;
    
    @JsonProperty
    private String license;
    
    @JsonProperty
    @JsonDeserialize(using = DateDeserializer.class)
    private Date issued;
    
    @JsonProperty
    @JsonDeserialize(using = DateDeserializer.class)
    private Date modified;
    
    @JsonProperty
    private String licenseAttributionByText;
    
    @JsonProperty
    private String plannedAvailability;
    
    @JsonProperty
    private String documentation;
    
    @JsonProperty
    private String conforms_to;
    
    @JsonProperty
    private String download_url;
    
    @JsonProperty
    private String status;
    
    @JsonProperty
    private String mimetype;
    
    @JsonProperty
    private String rights;
    
    @JsonProperty
    private String hash_algorithm;

}
