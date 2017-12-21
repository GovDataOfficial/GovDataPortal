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
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import com.liferay.portal.kernel.cache.MultiVMPoolUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.PropsUtil;

import de.fhg.fokus.odp.categoriesgrid.model.CategoryViewModel;
import de.seitenbau.govdata.cache.BaseCache;
import de.seitenbau.govdata.filter.FilterPathUtils;
import de.seitenbau.govdata.filter.SearchConsts;
import de.seitenbau.govdata.navigation.GovDataNavigation;
import de.seitenbau.govdata.odp.registry.ODRClient;
import de.seitenbau.govdata.odp.registry.ckan.Constants;
import de.seitenbau.govdata.odp.registry.model.Category;
import de.seitenbau.govdata.odp.spi.OpenDataRegistry;

/**
 * The class constitutes a bean that serves as a source for the categories on the categories-grid
 * portlet.
 * 
 * @author rnoerenberg, SEITENBAU GmbH
 * @author Benjamin Dittwald, Fraunhofer FOKUS
 */
@Controller(value = "categoriesGridController")
@RequestMapping("VIEW")
@SessionAttributes({ "categories" })
public class CategoriesGrid implements Serializable
{
    /** The logger. */
    private static final Logger LOG = LoggerFactory.getLogger(CategoriesGrid.class);

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The prop name authorization key. */
    private final String PROP_NAME_AUTHORIZATION_KEY = "authenticationKey";

    /** The prop name ckan url. */
    private final String PROP_NAME_CKAN_URL = "cKANurl";

    /** The cache categories key. */
    private final String CACHE_CATEGORIES_KEY = "categories";

    /** The odr. */
    private ODRClient odr;
    
    private GovDataNavigation govDataNavigation;

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
      
      List<Category> categories = getCategories();
      List<CategoryViewModel> categoryViewModels = mapToCategoryViewModels(categories);
      
      model.addAttribute("categories", categoryViewModels);
      return "view";
    }

  /**
   * Gets the categories.
   * 
   * @return the categories
   */
  @SuppressWarnings("unchecked")
  private List<Category> getCategories()
  {
    List<Category> categories =
        (List<Category>) MultiVMPoolUtil.getCache(BaseCache.CACHE_NAME_CATEGORIES_GRID)
            .get(CACHE_CATEGORIES_KEY);

    if (categories == null)
    {

      LOG.info("Empty {} cache, fetching categories from CKAN.", CACHE_CATEGORIES_KEY);
      Properties props = new Properties();
      props.setProperty("ckan.authorization.key", PropsUtil.get(PROP_NAME_AUTHORIZATION_KEY));
      props.setProperty("ckan.url", PropsUtil.get(PROP_NAME_CKAN_URL));

      odr = OpenDataRegistry.getClient(Constants.OPEN_DATA_PROVIDER_NAME);
      odr.init(props);

      List<Category> list = odr.listCategories();
      categories = new ArrayList<Category>();

      for (Category category : list)
      {
        if ("group".equalsIgnoreCase(category.getType()))
        {
          categories.add(category);
        }
      }
      Collections.sort(categories, new CategoriesTitleComparator());
      // safe cast: LinkedList
      MultiVMPoolUtil.getCache(BaseCache.CACHE_NAME_CATEGORIES_GRID)
          .put(CACHE_CATEGORIES_KEY, (Serializable) categories);
    }

    return categories;
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

  public void setGovDataNavigation(GovDataNavigation govDataNavigation)
  {
    this.govDataNavigation = govDataNavigation;
  }
}
