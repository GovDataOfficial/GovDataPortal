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
package de.fhg.fokus.odp.registry.ckan;

/**
 * The Interface Constants.
 * 
 * @author sim, msg
 */
public interface Constants {

	/** The Constant PROPERTIES_FILENAME. */
	static final String PROPERTIES_FILENAME = "ckan.properties";

	/** The Constant PROPERTY_NAME_CKAN_URL. */
	static final String PROPERTY_NAME_CKAN_URL = "ckan.url";

	/** The Constant PROPERTY_NAME_CKAN_AUTHORIZATION_KEY. */
	static final String PROPERTY_NAME_CKAN_AUTHORIZATION_KEY = "ckan.authorization.key";

	static final String PROP_NAME_DEFAULT_SORT_METADATA = "sorting.default.metadata";

	/** The Constant OPEN_DATA_PROVIDER_NAME. */
	static final String OPEN_DATA_PROVIDER_NAME = "CKAN";

	/** The Constant JSON_FIELD_EXTRAS. */
	static final String JSON_FIELD_EXTRAS = "extras";

	/** The Constant JSON_FIELD_CONTACTS. */
	static final String JSON_FIELD_CONTACTS = "contacts";

	/** The Constant JSON_FIELD_RESOURCES. */
	static final String JSON_FIELD_RESOURCES = "resources";

	/** The Constant JSON_FIELD_TAGS. */
	static final String JSON_FIELD_TAGS = "tags";

	/** The Constant JSON_FIELD_CATEGORIES. */
	static final String JSON_FIELD_CATEGORIES = "groups";

	/** The Constant JSON_FIELD_LICENCE. */
	static final String JSON_FIELD_LICENCE = "terms_of_use";

	/** The Constant JSON_FIELD_COVERAGEFROM. */
	static final String JSON_FIELD_COVERAGEFROM = "temporal_coverage_from";

	/** The Constant JSON_FIELD_COVERAGETO. */
	static final String JSON_FIELD_COVERAGETO = "temporal_coverage_to";

	static final String JSON_FIELD_SECTOR = "sector";

	static final String JSON_FIELD_SPATIAL = "spatial";

	static final String JSON_FIELD_SPATIAL_REFERENCE = "spatial_reference";

	static final String JSON_FIELD_SPATIALTEXT = "spatial-text";

	static final String JSON_FIELD_IMAGES = "images";

	static final String JSON_FIELD_GEOGRANULARITY = "geographical_granularity";

	static final String JSON_FIELD_TEMPORALGRANULARITY = "temporal_granularity";

	static final String JSON_FIELD_TEMPORALGRANULARITY_FACTOR = "temporal_granularity_factor";

	static final String JSON_FIELD_DATES = "dates";

	static final String JSON_FIELD_USEDDATASETS = "used_datasets";

	static final String JSON_FIELD_OGD_VERSION = "ogd_version";

	/** The Constant JSON_FIELD_ALL_FIELDS. */
	static final String JSON_FIELD_ALL_FIELDS = "all_fields";

	static final String JSON_DATETIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";

	static final String[] dateFormats = { "yyyy-MM-dd HH:mm:ss",
			"yyyy-MM-dd HH:mm:ssZ", "yyyy-MM-dd HH:mm:ss Z",
			"yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd'T'HH:mm:ssZ", "yyyy-MM-dd",
			"dd.MM.yyyy" };

}
