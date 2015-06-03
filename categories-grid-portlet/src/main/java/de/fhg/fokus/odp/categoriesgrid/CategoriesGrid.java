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

package de.fhg.fokus.odp.categoriesgrid;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.xml.namespace.QName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import com.liferay.portal.kernel.cache.MultiVMPoolUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.Layout;
import com.liferay.portal.theme.ThemeDisplay;

import de.fhg.fokus.odp.registry.ODRClient;
import de.fhg.fokus.odp.registry.ckan.Constants;
import de.fhg.fokus.odp.registry.model.Category;
import de.fhg.fokus.odp.registry.queries.Query;
import de.fhg.fokus.odp.spi.OpenDataRegistry;

/**
 * The class constitutes a bean that serves as a source for the categories on the categories-grid portlet.
 * 
 * @author Benjamin Dittwald, Fraunhofer FOKUS
 */
@Controller(value = "categoriesGridController")
@RequestMapping("VIEW")
@SessionAttributes({ "categories" })
public class CategoriesGrid implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The prop name authorization key. */
    private final String PROP_NAME_AUTHORIZATION_KEY = "authenticationKey";

    /** The prop name ckan url. */
    private final String PROP_NAME_CKAN_URL = "cKANurl";

    /** The cache name. */
    private final String CACHE_NAME = "de.fhg.fokus.odp.categoriesgrid";

    /** The cache categories key. */
    private final String CACHE_CATEGORIES_KEY = "categories";

    /** The odr. */
    private ODRClient odr;

    /** The log. */
    private final Logger LOG = LoggerFactory.getLogger(getClass());

    /**
     * View list default.
     * 
     * @return the string
     */
    @RenderMapping
    public String viewListDefault() {
        return "view";
    }

    /**
     * Gets the categories.
     * 
     * @return the categories
     */
    @SuppressWarnings("unchecked")
    @ModelAttribute(value = "categories")
    public List<Category> getCategories() {

        List<Category> categories = (List<Category>) MultiVMPoolUtil.get(CACHE_NAME, CACHE_CATEGORIES_KEY);

        if (categories == null) {

            LOG.info("Empty {} cache, fetching categories from CKAN.", CACHE_CATEGORIES_KEY);
            Properties props = new Properties();
            props.setProperty("ckan.authorization.key", PropsUtil.get(PROP_NAME_AUTHORIZATION_KEY));
            props.setProperty("ckan.url", PropsUtil.get(PROP_NAME_CKAN_URL));

            odr = OpenDataRegistry.getClient(Constants.OPEN_DATA_PROVIDER_NAME);
            odr.init(props);

            List<Category> list = odr.listCategories();
            categories = new ArrayList<Category>();

            for (Category category : list) {
                if ("group".equalsIgnoreCase(category.getType())) {
                    categories.add(category);
                }
            }
            Collections.sort(categories, new CategoriesTitleComparator());
            MultiVMPoolUtil.put(CACHE_NAME, CACHE_CATEGORIES_KEY, categories);
        }

        return categories;
    }

    /**
     * Search.
     * 
     * @param response
     *            the response
     * @param request
     *            the request
     */
    @ActionMapping(params = "action=categorySearch")
    public void search(ActionResponse response, ActionRequest request) {
        String categoryName = request.getParameter("categoryName");

        Query query = new Query();
        Calendar cal = Calendar.getInstance();
        query.getCategories().add(categoryName + ":#:" + cal.getTimeInMillis());
        response.setEvent(new QName("http://fokus.fraunhofer.de/odplatform", "querydatasets"), query);

        ThemeDisplay td = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);

        String location = td.getPortalURL();
        Layout layout = td.getLayout();

        try {
            if (layout.isPublicLayout()) {
                location += td.getPathFriendlyURLPublic();
            }

            location += layout.hasScopeGroup() ? layout.getScopeGroup().getFriendlyURL() : layout.getGroup().getFriendlyURL();
            location += "/suchen";
            response.sendRedirect(location);
        } catch (PortalException e) {
            LOG.error(e.getMessage());
        } catch (SystemException e) {
            LOG.error(e.getMessage());
        } catch (IOException e) {
            LOG.error(e.getMessage());
        }
    }
}
