/**
 * Copyright (c) 2012, 2013 Fraunhofer Institute FOKUS
 * <p>
 * This file is part of Open Data Platform.
 * <p>
 * Open Data Platform is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * Open Data Plaform is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Affero General Public License
 * along with Open Data Platform.  If not, see <http://www.gnu.org/licenses/agpl-3.0>.
 */

package de.seitenbau.govdata.odp.registry.ckan.json;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

/**
 * The Class LicenceBean.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Data
public class LicenceBean implements Serializable
{
  private static final long serialVersionUID = 7316836055751291814L;

  @JsonProperty
  private String id;

  @JsonProperty
  private String title;

  @JsonProperty
  private String url;

  @JsonProperty
  private String status;

  @JsonProperty
  private String maintainer;

  @JsonProperty
  private String family;

  @JsonProperty
  private boolean domain_content;

  @JsonProperty
  private boolean domain_data;

  @JsonProperty
  private boolean domain_software;

  @JsonProperty
  private boolean is_okd_compliant;

  @JsonProperty
  private boolean is_osi_compliant;
}
