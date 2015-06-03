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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.codehaus.jackson.JsonNode;

import de.fhg.fokus.odp.registry.ckan.ODRClientImpl;
import de.fhg.fokus.odp.registry.ckan.json.FacetItemBean;
import de.fhg.fokus.odp.registry.model.MetadataEnumType;
import de.fhg.fokus.odp.registry.queries.QueryFacet;
import de.fhg.fokus.odp.registry.queries.QueryFacetItem;

/**
 * @author sim
 * 
 */
public class QueryFacetImpl implements QueryFacet, Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1694190996684193243L;

    private List<QueryFacetItem> items;

    private String name;

    @Override
    public String getName() {
        return name;
    }

    private void setName(String name) {
        this.name = name;
    }

    @Override
    public List<QueryFacetItem> getItems() {
        if (items == null) {
            items = new ArrayList<QueryFacetItem>();
        }
        return items;
    }

    public static Map<String, QueryFacet> read(JsonNode result) {
        Map<String, QueryFacet> facets = new HashMap<String, QueryFacet>();
        if (result != null) {
            Iterator<Entry<String, JsonNode>> it = result.getFields();
            while (it.hasNext()) {
                Entry<String, JsonNode> entry = it.next();
                JsonNode value = entry.getValue();
                JsonNode items = value.get("items");
                if (items != null && items.isArray()) {
                    QueryFacetImpl facet = new QueryFacetImpl();
                    facet.setName(entry.getKey());
                    for (JsonNode item : items) {
                        FacetItemBean bean = ODRClientImpl.convert(item, FacetItemBean.class);
                        if (facet.getName().equals("type")) {
                            bean.setDisplay_name(MetadataEnumType.fromField(bean.getName()).getDisplayName());
                        }
                        if (facet.getName().equals("isopen")) {
                            bean.setDisplay_name("true".equals(bean.getName()) ? "Freie Nutzung" : "Eingeschränkte Nutzung");
                        }
                        facet.getItems().add(new QueryFacetItemImpl(bean));
                    }

                    facets.put(facet.getName(), facet);
                }
            }
        }
        return facets;
    }

}
