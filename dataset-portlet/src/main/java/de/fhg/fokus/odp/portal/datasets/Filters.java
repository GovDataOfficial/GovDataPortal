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
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.ValueChangeEvent;
import javax.portlet.ActionResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liferay.portal.kernel.util.PropsUtil;

import de.fhg.fokus.odp.registry.model.Category;
import de.fhg.fokus.odp.registry.model.Metadata;
import de.fhg.fokus.odp.registry.model.MetadataEnumType;
import de.fhg.fokus.odp.registry.queries.Query;
import de.fhg.fokus.odp.registry.queries.QueryFacet;
import de.fhg.fokus.odp.registry.queries.QueryFacetItem;
import de.fhg.fokus.odp.registry.queries.QueryResult;

/**
 * @author sim
 * @author msg
 * 
 */
@ManagedBean
@ViewScoped
public class Filters implements Serializable {

	private static int MAX_QUERY = 15;

	/**
	 * The count of the configuration max query display.max.query.pro.page
	 */
	private static final String PROP_DISPLAY_MAX_QUERY_PRO_PAGE = "display.max.query.pro.page";
	/**
	 * 
	 */
	private static final long serialVersionUID = -6771223667157622098L;
	private static final Logger log = LoggerFactory.getLogger(Filters.class);

	private QueryFacet categories;
	private QueryFacet types;
	private QueryFacet formats;
	private QueryFacet tags;
	private List<QueryFacetItem> alltags;
	private List<Metadata> ownermetadatas;

	// private int ownerMetaDatasSum;

	public List<QueryFacetItem> getAlltags() {
		return alltags;
	}

	public void setAlltags(List<QueryFacetItem> alltags) {
		this.alltags = alltags;
	}

	public List<QueryFacetItem> getAllformats() {
		return allformats;
	}

	public void setAllformats(List<QueryFacetItem> allformats) {
		this.allformats = allformats;
	}

	private List<QueryFacetItem> allformats;

	private QueryFacet licences;
	private QueryFacet openess;

	private List<String> selectedTypes;

	private List<String> selectedCategories;

	private List<String> selectedTags;

	private List<String> selectedFormats;

	private List<String> selectedLicences;

	private String selectedOpeness;

	private boolean showAllTags;
	private boolean showAllFormats;
	private boolean showMyDatasets;
	private long hitscount;

	public boolean isShowAllTags() {
		return showAllTags;
	}

	public void setShowAllTags(boolean showAllTags) {
		this.showAllTags = showAllTags;
	}

	public boolean isShowAllFormats() {
		return showAllFormats;
	}

	public void setShowAllFormats(boolean showAllFormats) {
		this.showAllFormats = showAllFormats;
	}

	public void changeViewMyDatasets() {
		filter(new ActionEvent(null));
	}

	public void changeViewTags() {
		if (showAllTags)
			showAllTags();
		else
			showLessTags();
	}

	public void changeViewFormats() {
		if (showAllFormats)
			showAllFormats();
		else
			showLessFormats();
	}

	public void showAllFormats() {
		if (allformats != null) {
			Collections.sort(allformats, new FilterCountComparator());
			if (allformats.size() > 0) {
				formats.getItems().clear();
				formats.getItems().addAll(allformats);
			}
		}
	}

	public void showLessFormats() {
		if (allformats != null) {
			Collections.sort(allformats, new FilterCountComparator());

			if (allformats.size() > 4) {
				List<QueryFacetItem> tmpList = new ArrayList<QueryFacetItem>(
						allformats.subList(0, 5));
				formats.getItems().clear();
				formats.getItems().addAll(tmpList);
			}
		}
	}

	public void showAllTags() {
		if (alltags != null) {
			Collections.sort(alltags, new FilterCountComparator());
			if (alltags.size() > 0) {
				tags.getItems().clear();
				tags.getItems().addAll(alltags);
			}
		}
	}

	public void showLessTags() {
		if (alltags != null) {
			Collections.sort(alltags, new FilterCountComparator());

			if (alltags.size() > 4) {
				List<QueryFacetItem> tmpList = new ArrayList<QueryFacetItem>(
						alltags.subList(0, 5));
				tags.getItems().clear();
				tags.getItems().addAll(tmpList);
			}
		}
	}

	@ManagedProperty("#{currentQuery}")
	private CurrentQuery currentQuery;

	@ManagedProperty("#{registryClient}")
	private RegistryClient registryClient;

	@ManagedProperty("#{currentUser}")
	private CurrentUser currentUser;

	@PostConstruct
	public void init() {

		String MAX_QUERY_Str = PropsUtil.get(PROP_DISPLAY_MAX_QUERY_PRO_PAGE);
		try {
			MAX_QUERY = Integer.parseInt(MAX_QUERY_Str);
		} catch (NumberFormatException e) {
			MAX_QUERY = 15;
		}

		QueryResult<?> lastResult = currentQuery.getLastResult();

		selectedTags = currentQuery.getQuery().getTags();
		selectedCategories = currentQuery.getQuery().getCategories();
		selectedTypes = new ArrayList<String>();
		for (MetadataEnumType type : currentQuery.getQuery().getTypes()) {
			selectedTypes.add(type.toField());
		}
		selectedFormats = currentQuery.getQuery().getFormats();
		selectedLicences = currentQuery.getQuery().getLicences();
		Boolean isopen = currentQuery.getQuery().getIsOpen();
		if (isopen == null) {
			selectedOpeness = "0";
		} else if (isopen) {
			selectedOpeness = "true";
		} else {
			selectedOpeness = "false";
		}

		if (lastResult != null) {
			categories = lastResult.getFacets().get("groups");
			if (categories != null && !categories.getItems().isEmpty()) {
				Collections.sort(categories.getItems(),
						new FilterCountComparator());
				for (Category category : registryClient.getCategories()) {
					Iterator<QueryFacetItem> it = categories.getItems()
							.iterator();
					while (it.hasNext()) {
						QueryFacetItem item = it.next();
						if (item.getName().equals(category.getName())
								&& category.getType().equals("subgroup")) {
							it.remove();
						}
					}
				}
			}

			openess = lastResult.getFacets().get("isopen");
			if (openess != null) {

			}

			types = lastResult.getFacets().get("type");

			if (types != null) {
				Collections.sort(types.getItems(), new FilterCountComparator());
			}

			formats = lastResult.getFacets().get("res_format");

			if (formats != null) {
				if (formats.getItems().size() > 0) {
					allformats = new ArrayList<QueryFacetItem>(
							formats.getItems());
				}
				changeViewFormats();
			}

			tags = lastResult.getFacets().get("tags");
			if (tags != null) {
				if (tags.getItems().size() > 0) {
					alltags = new ArrayList<QueryFacetItem>(tags.getItems());
				}
				changeViewTags();
			}

			licences = lastResult.getFacets().get("license_id");
			if (licences != null) {
				Collections.sort(licences.getItems(),
						new FilterCountComparator());
			}

			updateHitsCount();

		}
	}

	public void changeShowmydatasets(AjaxBehaviorEvent vcEvent) {
		updateHitsCount();
	} // end of changeShowmydatasets

	/*
	 * msg this method update the hitCounts if show mydataset toggle button is
	 * selected and current user is a creator and logged in.
	 */
	private void updateHitsCount() {
		int resultCount = (int) (long) currentQuery.getLastResult().getCount();
		if (showMyDatasets && currentUser.isLoggedin()
				&& currentUser.isCreator()) {
			setOwnermetadatas(new LinkedList<Metadata>());
			setHitscount(0);
			currentQuery.setLastResult(null);
			// ownerMetaDatasSum = 0;
			// List<Metadata> metadatas = currentUser.getMetadatas();
			// currentQuery.setOwnMetadatas(metadatas);
			// setHitscount(getHitscount() + metadatas.size());

			List<String> datasets = currentUser.getUserDatasets();
			setHitscount(getHitscount() + datasets.size());
			OwnMetadataPages pageable = new OwnMetadataPages(currentUser);
			pageable.setPage(pageable.getPage());
			List<Metadata> metadata = currentUser.getUserMetadatas(pageable
					.getOwnMetadataForPage());
			currentQuery.setOwnMetadatas(metadata);
			currentQuery.setPageable(pageable);

		} else {
			currentQuery.setOwnMetadatas(null);
			// msg 21.05.2014 cancel reload query from ckan
			// currentQuery.setLastResult(null);
			currentQuery.getQuery().setMax(MAX_QUERY);
			setHitscount(resultCount);
		}
	}

	public void setSelectedTypes(List<String> selectedTypes) {
		this.selectedTypes = selectedTypes;
	}

	public List<String> getSelectedTypes() {
		return selectedTypes;
	}

	public void openessValueChanged(ValueChangeEvent event) {

	}

	@SuppressWarnings("unchecked")
	public void typeValueChanged(ValueChangeEvent event) {
		Query query = currentQuery.getQuery();

		List<String> oldItems = new ArrayList<String>();
		oldItems.addAll((List<String>) event.getOldValue());

		List<String> newItems = new ArrayList<String>();
		newItems.addAll((List<String>) event.getNewValue());

		query.getTypes().clear();
		for (String type : newItems) {
			query.getTypes().add(MetadataEnumType.fromField(type));
		}

		if (oldItems.size() < newItems.size()) {
			newItems.removeAll(oldItems);
		} else {
			oldItems.removeAll(newItems);
		}
		// currentQuery.updateQuery(query);
		// init();
	}

	public void setSelectedLicences(List<String> selectedLicences) {
		this.selectedLicences = selectedLicences;
	}

	public List<String> getSelectedLicences() {
		return selectedLicences;
	}

	@SuppressWarnings("unchecked")
	public void licenceValueChanged(ValueChangeEvent event) {
		Query query = currentQuery.getQuery();

		List<String> oldItems = new ArrayList<String>();
		oldItems.addAll((List<String>) event.getOldValue());

		List<String> newItems = new ArrayList<String>();
		newItems.addAll((List<String>) event.getNewValue());

		query.getLicences().clear();
		for (String licence : newItems) {
			query.getLicences().add(licence);
		}

		if (oldItems.size() < newItems.size()) {
			newItems.removeAll(oldItems);
		} else {
			oldItems.removeAll(newItems);
		}
		// currentQuery.updateQuery(query);
		// init();
	}

	public void setSelectedTags(List<String> selectedTags) {
		this.selectedTags = selectedTags;
	}

	public List<String> getSelectedTags() {
		return selectedTags;
	}

	@SuppressWarnings("unchecked")
	public void tagValueChanged(ValueChangeEvent event) {
		Query query = currentQuery.getQuery();

		List<String> oldItems = new ArrayList<String>();
		oldItems.addAll((List<String>) event.getOldValue());

		List<String> newItems = new ArrayList<String>();
		newItems.addAll((List<String>) event.getNewValue());

		query.getTags().clear();
		for (String tag : newItems) {
			query.getTags().add(tag);
		}

		if (oldItems.size() < newItems.size()) {
			newItems.removeAll(oldItems);
		} else {
			oldItems.removeAll(newItems);
		}
		// currentQuery.updateQuery(query);
		// init();
	}

	public void setSelectedCategories(List<String> selectedCategories) {
		this.selectedCategories = selectedCategories;
	}

	public List<String> getSelectedCategories() {
		return selectedCategories;
	}

	@SuppressWarnings("unchecked")
	public void categoryValueChanged(ValueChangeEvent event) {
		Query query = currentQuery.getQuery();

		List<String> oldItems = new ArrayList<String>();
		oldItems.addAll((List<String>) event.getOldValue());

		List<String> newItems = new ArrayList<String>();
		newItems.addAll((List<String>) event.getNewValue());

		query.getCategories().clear();
		for (String category : newItems) {
			query.getCategories().add(category);
		}

		if (oldItems.size() < newItems.size()) {
			newItems.removeAll(oldItems);
		} else {
			oldItems.removeAll(newItems);
		}
		// currentQuery.updateQuery(query);
		// init();
	}

	public void setSelectedFormats(List<String> selectedFormats) {
		this.selectedFormats = selectedFormats;
	}

	public List<String> getSelectedFormats() {
		return selectedFormats;
	}

	@SuppressWarnings("unchecked")
	public void formatValueChanged(ValueChangeEvent event) {
		Query query = currentQuery.getQuery();

		List<String> oldItems = new ArrayList<String>();
		oldItems.addAll((List<String>) event.getOldValue());

		List<String> newItems = new ArrayList<String>();
		newItems.addAll((List<String>) event.getNewValue());

		query.getFormats().clear();
		for (String format : newItems) {
			query.getFormats().add(format);
		}

		if (oldItems.size() < newItems.size()) {
			newItems.removeAll(oldItems);
		} else {
			oldItems.removeAll(newItems);
		}
		// currentQuery.updateQuery(query);
		// init();
	}

	/**
	 * @return the categories
	 */
	public List<QueryFacetItem> getCategories() {
		return categories == null ? new ArrayList<QueryFacetItem>()
				: categories.getItems();
	}

	/**
	 * @return the categories
	 */
	public List<QueryFacetItem> getTypes() {
		return types == null ? new ArrayList<QueryFacetItem>() : types
				.getItems();
	}

	public List<QueryFacetItem> getFormats() {
		return formats == null ? new ArrayList<QueryFacetItem>() : formats
				.getItems();
	}

	public List<QueryFacetItem> getTags() {
		return tags == null ? new ArrayList<QueryFacetItem>() : tags.getItems();
	}

	public List<QueryFacetItem> getLicences() {
		return licences == null ? new ArrayList<QueryFacetItem>() : licences
				.getItems();
	}

	public List<QueryFacetItem> getOpeness() {
		return openess == null ? new ArrayList<QueryFacetItem>() : openess
				.getItems();
	}

	/**
	 * @param queryResult
	 *            the queryResult to set
	 */
	public void setCurrentQuery(CurrentQuery currentQuery) {
		this.currentQuery = currentQuery;
	}

	public void setRegistryClient(RegistryClient registryClient) {
		this.registryClient = registryClient;
	}

	/**
	 * @param currentUser
	 *            the currentUser to set
	 */
	public void setCurrentUser(CurrentUser currentUser) {
		this.currentUser = currentUser;
	}

	public void filter(ActionEvent event) {
		ActionResponse response = (ActionResponse) FacesContext
				.getCurrentInstance().getExternalContext().getResponse();
		response.removePublicRenderParameter("searchcategory");

		Query query = currentQuery.getQuery();

		if (selectedTypes != null && !selectedTypes.isEmpty()) {
			query.getTypes().clear();
			for (String type : selectedTypes) {
				query.getTypes().add(MetadataEnumType.fromField(type));
			}
		}

		if ("0".equals(selectedOpeness)) {
			query.setIsOpen(null);
		} else if ("true".equals(selectedOpeness)) {
			query.setIsOpen(true);
		} else if ("false".equals(selectedOpeness)) {
			query.setIsOpen(false);
		}

		query.getCategories().clear();
		if (selectedCategories != null)
			query.getCategories().addAll(selectedCategories);

		query.getTags().clear();
		if (selectedTags != null)
			query.getTags().addAll(selectedTags);

		query.getFormats().clear();
		if (selectedFormats != null)
			query.getFormats().addAll(selectedFormats);

		query.getLicences().clear();
		if (selectedLicences != null)
			query.getLicences().addAll(selectedLicences);

		currentQuery.updateQuery(query);
		init();
	}

	public void setFilterType(String type) {
		Query query = currentQuery.getQuery();
		query.getTypes().add(MetadataEnumType.fromField(type));

		// currentQuery.updateQuery(query);
		// init();
	}

	/**
	 * @return the selectedOpeness
	 */
	public String getSelectedOpeness() {
		return selectedOpeness;
	}

	/**
	 * @param selectedOpeness
	 *            the selectedOpeness to set
	 */
	public void setSelectedOpeness(String selectedOpeness) {
		this.selectedOpeness = selectedOpeness;
	}

	public boolean isShowMyDatasets() {
		return showMyDatasets;
	}

	public void setShowMyDatasets(boolean showMyDatasets) {
		this.showMyDatasets = showMyDatasets;
	}

	public long getHitscount() {
		return hitscount;
	}

	public void setHitscount(long hitscount) {
		this.hitscount = hitscount;
	}

	public List<Metadata> getOwnermetadatas() {
		return ownermetadatas;
	}

	public void setOwnermetadatas(List<Metadata> ownermetadatas) {
		this.ownermetadatas = ownermetadatas;
	}

}
