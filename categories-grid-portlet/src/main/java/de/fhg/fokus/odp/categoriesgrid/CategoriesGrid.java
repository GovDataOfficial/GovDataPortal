/**
 * Copyright (c) 2012, 2013 Fraunhofer Institute FOKUS | 2017 SEITENBAU GmbH
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

package de.fhg.fokus.odp.categoriesgrid;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;

import de.fhg.fokus.odp.categoriesgrid.model.CategoryViewModel;
import de.seitenbau.govdata.cache.BaseCache;
import de.seitenbau.govdata.cache.CategoryCache;
import de.seitenbau.govdata.navigation.GovDataNavigation;
import de.seitenbau.govdata.odp.common.filter.FilterPathUtils;
import de.seitenbau.govdata.odp.common.filter.SearchConsts;
import de.seitenbau.govdata.odp.registry.model.Category;
import de.seitenbau.govdata.servicetracker.MultiVMPoolServiceTracker;

/**
 * The class constitutes a bean that serves as a source for the categories on the categories-grid
 * portlet.
 * 
 * @author rnoerenberg, SEITENBAU GmbH
 * @author Benjamin Dittwald, Fraunhofer FOKUS
 */
@Controller(value = "categoriesGridController")
@RequestMapping("VIEW")
public class CategoriesGrid implements Serializable
{
  /** The logger. */
  private static final Logger LOG = LoggerFactory.getLogger(CategoriesGrid.class);

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1L;

  /** The time to live in seconds. */
  private static final int CLUSTERED_CACHE_TTL_IN_SECONDS = 7200;

  /** The cache categories key. */
  private final String CACHE_CATEGORIES_KEY = "categories";

  private MultiVMPool multiVMPool;

  private GovDataNavigation govDataNavigation;

  private MultiVMPoolServiceTracker multiVMPoolTracker;

  private boolean clusteredCacheAvailable = true;

  private CategoryCache categoryCache;

  @PostConstruct
  public void init()
  {
    // read clustered cache service
    multiVMPoolTracker = new MultiVMPoolServiceTracker(this);
    multiVMPoolTracker.open();
    multiVMPool = multiVMPoolTracker.getService();
    if (multiVMPool == null)
    {
      clusteredCacheAvailable = false;
      LOG.warn("The required service 'MultiVMPool' is not available.");
    }
    // set TTL for fallback category cache
    categoryCache.setMaxCacheTimeHours(1);
    LOG.debug("Initialize complete");
  }

  @PreDestroy
  public void shutdown()
  {
    multiVMPoolTracker.close();
  }

  /**
   * Erstellt die Objekte für die Anzeige.
   * 
   * @param request der Request.
   * @param response die Response.
   * @param model das Model für die Anzeige.
   * @return der View-Name.
   * @throws PortalException
   * @throws SystemException
   */
  @RenderMapping
  public String showSearchResults(
      RenderRequest request,
      RenderResponse response,
      Model model) throws PortalException, SystemException
  {
    final String method = "showSearchResults() : ";
    LOG.trace(method + "Start");

    List<Category> categories = getCategories();
    List<CategoryViewModel> categoryViewModels = mapToCategoryViewModels(categories);

    model.addAttribute("categories", categoryViewModels);
    LOG.trace(method + "End");
    return "grid";
  }

  /**
   * Gets the categories.
   * 
   * @return the categories
   */
  private List<Category> getCategories()
  {
    final String method = "getCategories() : ";
    LOG.trace(method + "Start");

    List<Category> categories = readCategoriesFromClusteredCache();

    if (categories == null)
    {
      LOG.info("Clustered cache empty or expired. Re-reading categories...");
      categories = categoryCache.getCategoriesSortedByTitle();
      updateCache(categories);
    }

    LOG.trace(method + "End");
    return categories;
    }

  private void updateCache(List<Category> categories)
  {
    if (clusteredCacheAvailable)
    {
      LOG.debug("Update clustered cache.");
      // expects that the timeToLive parameter are seconds
      getPortalCache().put(CACHE_CATEGORIES_KEY, (Serializable) categories, CLUSTERED_CACHE_TTL_IN_SECONDS);
    }
  }

  @SuppressWarnings("unchecked")
  private List<Category> readCategoriesFromClusteredCache()
  {
    final String method = "readCategoriesFromClusteredCache() : ";
    LOG.trace(method + "Start");

    List<Category> categories = null;
    if (clusteredCacheAvailable)
    {
      LOG.debug("Start reading categories from clustered cache...");
      categories = (List<Category>) getPortalCache().get(CACHE_CATEGORIES_KEY);
      LOG.debug("Finished reading categories from clustered cache.");
    }
    else
    {
      LOG.info("Clustered cache service MultiVMPool is unavailable. Using not clustered cache as fallback.");
    }
    LOG.trace(method + "End");
    return categories;
  }

  @SuppressWarnings("unchecked")
  private PortalCache<String, Serializable> getPortalCache()
  {
    PortalCache<String, Serializable> portalCache =
        (PortalCache<String, Serializable>) multiVMPool
            .getPortalCache(BaseCache.CACHE_NAME_CATEGORIES_GRID);
    return portalCache;
  }

  /**
   * Map to CategoryViewModels and add actionURLs
   * @param categories list of ODP-categories
   * @param renderUrl
   * @return list of categoryViewModels
   * @throws PortalException
   * @throws SystemException
   */
  private List<CategoryViewModel> mapToCategoryViewModels(List<Category> categories) throws SystemException
  {
    final String method = "mapToCategoryViewModels() : ";
    LOG.trace(method + "Start");

    List<CategoryViewModel> result = new ArrayList<>();

    String redirectUrlString = null;
    for (Category category : categories)
    {
      try
      {
        // set current category as filter
        String filterParam =
            FilterPathUtils.serializeFilter(SearchConsts.FACET_GROUPS, category.getName());
        PortletURL redirectUrl = govDataNavigation
            .createLinkForSearchResults("suchen", "gdsearchresult", "", filterParam, "", "");
        redirectUrlString = redirectUrl.toString();
      }
      catch (PortalException | IllegalArgumentException e)
      {
        LOG.warn(method + "Fehler beim Erstellen der Filter-URL für die Kategorien. Fehler: {}"
            + e.getMessage());
      }

      result.add(CategoryViewModel.builder()
          .name(category.getName())
          .title(category.getTitle())
          .count(category.getCount())
          .actionURL(redirectUrlString).build());
    }

    LOG.trace(method + "End");
    return result;

  }

  @Autowired
  public void setGovDataNavigation(GovDataNavigation govDataNavigation)
  {
    this.govDataNavigation = govDataNavigation;
  }

  /**
   * @param categoryCache the categoryCache to set
   */
  @Autowired
  public void setCategoryCache(CategoryCache categoryCache)
  {
    this.categoryCache = categoryCache;
  }

}
