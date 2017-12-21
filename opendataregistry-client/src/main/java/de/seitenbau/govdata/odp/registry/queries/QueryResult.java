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

package de.seitenbau.govdata.odp.registry.queries;

import java.util.List;
import java.util.Map;

// TODO: Auto-generated Javadoc
/**
 * The Interface QueryResult.
 * 
 * @param <T>
 *            the generic type
 * @author sim
 */
public interface QueryResult<T> {

	/**
	 * Checks if is success.
	 * 
	 * @return true, if is success
	 */
	boolean isSuccess();

	/**
	 * Gets the error message.
	 * 
	 * @return the error message
	 */
	String getErrorMessage();

	/**
	 * Gets the result.
	 * 
	 * @return the result
	 */
	List<T> getResult();

	/**
	 * Gets the count.
	 * 
	 * @return the count
	 */
	long getCount();

	/**
	 * Gets the offset.
	 * 
	 * @return the offset
	 */
	long getOffset();

	/**
	 * Gets the page offset.
	 * 
	 * @return the page offset
	 */
	long getPageOffset();

	/**
	 * Gets the limit.
	 * 
	 * @return the limit
	 */
	int getLimit();

	/**
	 * Gets the facets.
	 * 
	 * @return the facets
	 */
	Map<String, QueryFacet> getFacets();

}
