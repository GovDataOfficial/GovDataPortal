/**
 * Copyright (c) 2012, 2015 Fraunhofer Institute FOKUS
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
package de.seitenbau.govdata.odp.registry.ckan;

/**
 * The Interface Constants.
 * 
 * @author sim, msg
 */
public interface Constants
{

  /** The Constant PROPERTIES_FILENAME. */
  String PROPERTIES_FILENAME = "ckan.properties";

  /** The Constant PROPERTY_NAME_CKAN_URL. */
  String PROPERTY_NAME_CKAN_URL = "ckan.url";

  /** The Constant PROPERTY_NAME_CKAN_AUTHORIZATION_KEY. */
  String PROPERTY_NAME_CKAN_AUTHORIZATION_KEY = "ckan.authorization.key";

  /** The Constant OPEN_DATA_PROVIDER_NAME. */
  String OPEN_DATA_PROVIDER_NAME = "CKAN";

  /** The Constant JSON_FIELD_EXTRAS. */
  String JSON_FIELD_EXTRAS = "extras";

  /** The Constant JSON_FIELD_CONTACTS. */
  String JSON_FIELD_CONTACTS = "contacts";

  /** The Constant JSON_FIELD_RESOURCES. */
  String JSON_FIELD_RESOURCES = "resources";

  /** The Constant JSON_FIELD_TAGS. */
  String JSON_FIELD_TAGS = "tags";

  /** The Constant JSON_FIELD_CATEGORIES. */
  String JSON_FIELD_CATEGORIES = "groups";

  /** The Constant JSON_FIELD_LICENCE. */
  String JSON_FIELD_LICENCE = "terms_of_use";

  /** The Constant JSON_FIELD_COVERAGEFROM. */
  String JSON_FIELD_COVERAGEFROM = "temporal_start";

  /** The Constant JSON_FIELD_COVERAGETO. */
  String JSON_FIELD_COVERAGETO = "temporal_end";

  /** The Constant JSON_FIELD_politicalGeocodingLevelURI. */
  String JSON_FIELD_politicalGeocodingLevelURI = "politicalGeocodingLevelURI";

  /** The Constant JSON_FIELD_TEMPORALGRANULARITY. */
  String JSON_FIELD_TEMPORALGRANULARITY = "temporal_granularity";

  /** The Constant JSON_FIELD_TEMPORALGRANULARITY_FACTOR. */
  String JSON_FIELD_TEMPORALGRANULARITY_FACTOR = "temporal_granularity_factor";

  /** The Constant JSON_FIELD_ALL_FIELDS. */
  String JSON_FIELD_ALL_FIELDS = "all_fields";

  /** The Constant JSON_DATETIME_PATTERN. */
  String JSON_DATETIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";

  /** The Constant JSON_FIELD_PUBLISHED. */
  String JSON_FIELD_PUBLISHED = "issued";

  /** The Constant JSON_FIELD_MODIFIED. */
  String JSON_FIELD_MODIFIED = "modified";
}
