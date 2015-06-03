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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.fhg.fokus.odp.registry.queries.QueryFacet;
import de.fhg.fokus.odp.registry.queries.QueryResult;

/**
 * @author sim
 * 
 */
public class QueryResultImpl<T> implements QueryResult<T>, Serializable {

	/**
     * 
     */
	private static final long serialVersionUID = -7197778635040570499L;

	private List<T> data;

	private long count;

	private long offset;

	private long pageoffset;

	private int limit;

	private boolean success = true;

	private String errorMessage;

	private Map<String, QueryFacet> facets;

	@Override
	public List<T> getResult() {
		return data;
	}

	public void setResult(List<T> data) {
		this.data = data;
	}

	@Override
	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}

	@Override
	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	@Override
	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	@Override
	public long getOffset() {
		return offset;
	}

	public void setOffset(long offset) {
		this.offset = offset;
	}

	@Override
	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	@Override
	public Map<String, QueryFacet> getFacets() {
		if (facets == null) {
			facets = new HashMap<String, QueryFacet>();
		}
		return facets;
	}

	/**
	 * @return the pageoffset
	 */
	@Override
	public long getPageOffset() {
		return pageoffset;
	}

	/**
	 * @param pageoffset
	 *            the pageoffset to set
	 */
	public void setPageOffset(long pageoffset) {
		this.pageoffset = pageoffset;
	}

}
