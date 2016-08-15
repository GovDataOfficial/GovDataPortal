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
import java.util.List;

import de.fhg.fokus.odp.registry.ckan.json.GroupBean;
import de.fhg.fokus.odp.registry.model.Category;
import de.fhg.fokus.odp.registry.model.Dataset;

/**
 * @author sim
 * 
 */
public class CategoryImpl implements Category, Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 2655069689597975300L;

    private GroupBean group;

    public CategoryImpl(GroupBean group) {
        this.group = group;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.fhg.fokus.odp.registry.Category#getName()
     */
    @Override
    public String getName() {
        return group.getName();
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.fhg.fokus.odp.registry.Category#getDisplayName()
     */
    @Override
    public String getDisplayName() {
        return group.getDisplayName();
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.fhg.fokus.odp.registry.Category#getTitle()
     */
    @Override
    public String getTitle() {
        return group.getTitle();
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.fhg.fokus.odp.registry.Category#getDescription()
     */
    @Override
    public String getDescription() {
        return group.getDescription();
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.fhg.fokus.odp.registry.Category#getType()
     */
    @Override
    public String getType() {
        return group.getType();
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.fhg.fokus.odp.registry.Category#getDatasets()
     */
    @Override
    public List<Dataset> getDatasets() {
        return null;
    }

    @Override
    public int getCount() {
        return Integer.parseInt(group.getPackageCount());
    }

    @Override
    public List<Category> getSubCategories() {
        return null;
    }

}
