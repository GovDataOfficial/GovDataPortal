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
package de.fhg.fokus.odp.portal.datasets;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import com.liferay.faces.portal.context.LiferayFacesContext;
import com.liferay.portal.theme.ThemeDisplay;

import de.fhg.fokus.odp.registry.model.MetadataEnumType;
import de.fhg.fokus.odp.registry.model.Resource;
import de.fhg.fokus.odp.registry.queries.Query;

/**
 * The Class QueryManager.
 * 
 * @author sim
 */
@ManagedBean
@SessionScoped
public class QueryManager implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1078471631436677467L;

	/** The queries. */
	private final Map<String, CurrentQuery> queries = new HashMap<String, CurrentQuery>();

	private Query query;

	/**
	 * Gets the current.
	 * 
	 * @return the current
	 */
	public CurrentQuery getCurrent() {
		ThemeDisplay themeDisplay = LiferayFacesContext.getInstance()
				.getThemeDisplay();
		String currentPage = themeDisplay.getLayout().getFriendlyURL();
		CurrentQuery query = queries.get(currentPage);
		if (query == null) {
			query = addQuery(currentPage, new Query());
		}

		return query;
	}

	/**
	 * Gets the resource different types.
	 * 
	 * @param resources
	 *            the resources
	 * @return the resource different types
	 */
	public List<String> getResourceDifferentTypes(List<Resource> resources) {
		List<String> result = new ArrayList<String>();

		if (resources != null) {
			Set<String> resultSet = new HashSet<String>();
			for (Resource r : resources) {
				resultSet.add(r.getFormat().toUpperCase());
			}
			for (String format : resultSet) {
				result.add(format);
			}
		}

		return result;
	}

	/**
	 * Adds the query.
	 * 
	 * @param page
	 *            the page
	 * @param query
	 *            the query
	 * @return the current query
	 */
	public CurrentQuery addQuery(String page, Query query) {
		if ("/daten".equals(page)) {
			query.getTypes().add(MetadataEnumType.DATASET);
		} else if ("/apps".equals(page)) {
			query.getTypes().add(MetadataEnumType.APPLICATION);
		} else if ("/dokumente".equals(page)) {
			query.getTypes().add(MetadataEnumType.DOCUMENT);
		}
		// CurrentQuery currentQuery = new CurrentQuery(query, registryClient);

		// if (query.getCategories().size() > 0) {
		// currentQuery.updateQuery(query, query.getCategories().get(0));
		// }
		//
		// queries.put(page, currentQuery);
		// return currentQuery;
		return null;
	}

	/**
	 * @return the query
	 */
	public Query getQuery() {

		return query;
	}

	/**
	 * @param query
	 *            the query to set
	 */
	public void setQuery(Query query) {
		this.query = query;
	}

	public Query removeQuery() {
		Query result = query;
		query = null;
		return result;
	}
}
