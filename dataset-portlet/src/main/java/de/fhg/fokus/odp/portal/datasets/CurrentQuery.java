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
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liferay.faces.portal.context.LiferayFacesContext;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.theme.ThemeDisplay;

import de.fhg.fokus.odp.registry.model.Metadata;
import de.fhg.fokus.odp.registry.model.MetadataEnumType;
import de.fhg.fokus.odp.registry.queries.Query;
import de.fhg.fokus.odp.registry.queries.QueryResult;

/**
 * The Class CurrentQuery.
 * 
 * @author sim
 * @author msg
 */
@ManagedBean
@ViewScoped
public class CurrentQuery implements Serializable {

	private static final String PROP_DEFAULT_SORT_METADATA = "sorting.default.metadata";
	private static final String PROP_DATA_FORMAT_MAPPING = "data.format.mapping";

	/** The log. */
	private final Logger log = LoggerFactory.getLogger(getClass());

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 34176132756895396L;

	/** The query. */
	private Query query;

	/** The rss url. */
	private String rssUrl = "none";

	/** The query manager. */
	@ManagedProperty("#{queryManager}")
	private QueryManager queryManager;

	/** The last result. */
	private QueryResult<?> lastResult;

	/** The registry client. */
	@ManagedProperty("#{registryClient}")
	private RegistryClient registryClient;

	/** The last owner Metadata list */
	private List<Metadata> ownMetadatas;
	private OwnMetadataPages m_pageable;

	/**
	 * Inits the.
	 */
	@PostConstruct
	public void init() {
		if (queryManager.getQuery() != null) {
			Query tmpQuery = queryManager.getQuery();
			try {
				if (tmpQuery.getCategories().size() > 0) {
					String category = tmpQuery.getCategories().get(0);
					Calendar cal = Calendar.getInstance();
					cal.setTimeInMillis(Long.parseLong(category.split(":#:")[1]));
					Date now = new Date();

					if (!now.after(cal.getTime())) {
						query = queryManager.removeQuery();
					} else {
						tmpQuery.getCategories().set(0,
								category.split(":#:")[0]);
						query = tmpQuery;
					}
				} else if (tmpQuery.getTags().size() > 0) {
					String tag = tmpQuery.getTags().get(0);
					Calendar cal = Calendar.getInstance();
					cal.setTimeInMillis(Long.parseLong(tag.split(":#:")[1]));
					Date now = new Date();

					if (!now.after(cal.getTime())) {
						query = queryManager.removeQuery();
					} else {
						tmpQuery.getTags().set(0, tag.split(":#:")[0]);
						query = tmpQuery;
					}
				}
			} catch (IndexOutOfBoundsException ex) {
				log.info("No timestamp found in category query");
				query = queryManager.removeQuery();
			}
		} else if (query == null) {
			query = new Query();
		}
		ThemeDisplay themeDisplay = LiferayFacesContext.getInstance()
				.getThemeDisplay();
		String page = themeDisplay.getLayout().getFriendlyURL();
		if (query == null) {
			log.debug("init:query == null");
			query = new Query();
		}
		if ("/daten".equals(page)) {

			query.getTypes().add(MetadataEnumType.DATASET);
			log.info("querying:DATASET");
		} else if ("/apps".equals(page)) {
			query.getTypes().add(MetadataEnumType.APPLICATION);
			log.info("querying:APPLICATION");
		} else if ("/dokumente".equals(page)) {
			query.getTypes().add(MetadataEnumType.DOCUMENT);
			log.info("querying:DOCUMENT");
		} else if ("/suchen".equals(page)) {

		}
	}

	/**
	 * Gets the rss url.
	 * 
	 * @return the rss url
	 */
	public String getRssUrl() {
		ThemeDisplay themeDisplay = LiferayFacesContext.getInstance()
				.getThemeDisplay();
		rssUrl = themeDisplay.getURLPortal()
				+ "/rss-servlet/webresources/rssservice" + "?";
		if (query.getSearchterm() != null) {
			rssUrl += "q=" + query.getSearchterm() + "&";
		}
		for (String cat : query.getCategories()) {
			rssUrl += "groups=" + cat + "&";
		}
		for (String tag : query.getTags()) {
			rssUrl += "tags=" + tag + "&";
		}
		for (String lic : query.getLicences()) {
			rssUrl += "license_id=" + lic + "&";
		}
		for (String format : query.getFormats()) {
			rssUrl += "license_id=" + format + "&";
		}
		for (MetadataEnumType type : query.getTypes()) {
			rssUrl += "type=" + type.toField() + "&";
		}
		if (query.getIsOpen() != null) {
			rssUrl += "isopen=" + query.getIsOpen().toString();
		}

		if (rssUrl.endsWith("&")) {
			rssUrl = rssUrl.substring(0, rssUrl.length() - 1);
		}
		return rssUrl;
	}

	/**
	 * Sets the rss url.
	 * 
	 * @param rssUrl
	 *            the new rss url
	 */
	public void setRssUrl(String rssUrl) {
		this.rssUrl = rssUrl;
	}

	/**
	 * Gets the metadata.
	 * 
	 * @return the datasets
	 */
	@SuppressWarnings("unchecked")
	public List<Metadata> getMetadata() {
		if (ownMetadatas != null)
			return ownMetadatas;
		else
			return (List<Metadata>) getLastResult().getResult();
	}

	/**
	 * Update query.
	 * 
	 * @param query
	 *            the query
	 */
	public void updateQuery(Query query) {
		this.query = query;
		lastResult = null;
	}

	/**
	 * Gets the query.
	 * 
	 * @return the query
	 */
	public Query getQuery() {
		return query;
	}

	/**
	 * Gets the last result.
	 * 
	 * @return the last result
	 */
	public QueryResult<?> getLastResult() {
		if (lastResult == null) {
			log.debug("querying: {}", query.dump());
			lastResult = registryClient.getInstance().queryMetadata(query);
		}
		return lastResult;
	}

	public void setLastResult(QueryResult<?> lastResult) {
		this.lastResult = lastResult;
	}

	/**
	 * Sort.
	 * 
	 * @param sorting
	 *            the sorting
	 */
	public void sort(String sorting) {
		String current = query.getSortFields().isEmpty() ? "" : query
				.getSortFields().get(0);
		if (query.getSortFields().isEmpty()) {
			query.getSortFields().add(sorting + " asc");
		} else if (!current.startsWith(sorting)) {
			query.getSortFields().set(0, sorting + " asc");
		} else if (current.endsWith(" asc")) {
			current = current.replace(" asc", " desc");
			query.getSortFields().set(0, current);
		} else {
			current = current.replace(" desc", " asc");
			query.getSortFields().set(0, current);
		}

		lastResult = null;
	}

	/**
	 * Gets the resource format description.
	 * 
	 * @return the sorting
	 */
	public String getResourceFormatDes(String format) {

		String formatDesStr = PropsUtil.get(PROP_DATA_FORMAT_MAPPING + "."
				+ format.toLowerCase());
		if (formatDesStr == null)
			formatDesStr = format;

		return formatDesStr + " Format.";
	}

	/**
	 * Gets the sorting.
	 * 
	 * @return the sorting
	 */
	public String getSorting() {
		if (query.getSortFields().isEmpty()) {
			// add default sorting
			String defaultSortStr = PropsUtil.get(PROP_DEFAULT_SORT_METADATA);
			if (defaultSortStr == null)
				defaultSortStr = "score desc";
			query.getSortFields().add(defaultSortStr);
		}
		return query.getSortFields().get(0);
	}

	/**
	 * Gets the pages.
	 * 
	 * @return the pages
	 */
	public List<Integer> getPages() {
		if (ownMetadatas != null) {
			/**
			 * The list for owner list pages with only one page /*
			 * ArrayList<Integer> pagesForOwnMetadatas = new
			 * ArrayList<Integer>();
			 * pagesForOwnMetadatas.add(Integer.valueOf(1)); return
			 * pagesForOwnMetadatas;
			 */
			return m_pageable.getPages();
		}
		List<Integer> pages = new ArrayList<Integer>();
		int num = 0;
		if (getLastResult().getCount() != 0 && getLastResult().getLimit() != 0)
			num = (int) getLastResult().getCount() / getLastResult().getLimit();

		if (((int) getLastResult().getCount() % query.getMax()) > 0) {
			num++;
		}
		for (int i = 1; i <= num; ++i) {
			pages.add(Integer.valueOf(i));
		}
		return pages;
	}

	public void setOwnMetadatas(List<Metadata> metadatas) {
		ownMetadatas = metadatas;
	}

	/**
	 * Show page.
	 * 
	 * @param page
	 *            the page
	 */
	public void showPage(Integer page) {
		// log.info("showPage:" + page);
		if (ownMetadatas != null) {
			m_pageable.setPage(page);
			ownMetadatas = m_pageable.getMetadatas();
		} else {
			query.setOffset((page - 1) * query.getMax());
			lastResult = null;
		}
	}

	/**
	 * Gets the page.
	 * 
	 * @return the page
	 */
	public int getPage() {
		// log.info("getPage...");
		if (ownMetadatas != null) {
			return ((int) m_pageable.getOffset() / query.getMax()) + 1;
		}
		return ((int) getLastResult().getOffset() / query.getMax()) + 1;
	}

	/**
	 * Sets the query manager.
	 * 
	 * @param queryManager
	 *            the queryManager to set
	 */
	public void setQueryManager(QueryManager queryManager) {
		this.queryManager = queryManager;
	}

	/**
	 * Sets the registry client.
	 * 
	 * @param registryClient
	 *            the new registry client
	 */
	public void setRegistryClient(RegistryClient registryClient) {
		this.registryClient = registryClient;
	}

	/**
	 * Gets the searchterm.
	 * 
	 * @return the searchterm
	 */
	public String getSearchterm() {
		return query.getSearchterm();
	}

	/**
	 * Sets the searchterm.
	 * 
	 * @param searchterm
	 *            the new searchterm
	 */
	public void setSearchterm(String searchterm) {
		query.setSearchterm(searchterm);
	}

	public String getSearchcategory() {
		return query.getCategories().size() > 0 ? query.getCategories().get(0)
				: null;
	}

	public void setCategory(String searchcategory) {
		query.getCategories().clear();
		query.getCategories().add(searchcategory);
	}

	public void setPageable(OwnMetadataPages pageable) {
		m_pageable = pageable;

	}

}
