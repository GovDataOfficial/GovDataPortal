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
package de.seitenbau.govdata.odp.registry.ckan.impl;

import java.io.Serializable;

import de.seitenbau.govdata.odp.registry.ckan.json.FacetItemBean;
import de.seitenbau.govdata.odp.registry.queries.QueryFacetItem;

/**
 * @author sim
 * 
 */
public class QueryFacetItemImpl implements QueryFacetItem, Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -6954967650410867661L;

    private FacetItemBean facetItem;

    public QueryFacetItemImpl(FacetItemBean facetItem) {
        this.facetItem = facetItem;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.seitenbau.govdata.odp.registry.queries.QueryFacetItem#getName()
     */
    @Override
    public String getName() {
        return facetItem.getName();
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.seitenbau.govdata.odp.registry.queries.QueryFacetItem#getDisplayName()
     */
    @Override
    public String getDisplayName() {
        return facetItem.getDisplay_name();
    }

    public void setDisplayName(String displayName) {
        facetItem.setDisplay_name(displayName);
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.seitenbau.govdata.odp.registry.queries.QueryFacetItem#getCount()
     */
    @Override
    public int getCount() {
        return facetItem.getCount();
    }

}
