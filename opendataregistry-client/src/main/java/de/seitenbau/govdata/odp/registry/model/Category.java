/**
 * Copyright (c) 2012, 2013 Fraunhofer Institute FOKUS | 2017 SEITENBAU GmbH
 *
 * This file is part of Open Data Platform.
 *
 * Open Data Platform is free software: you can redistribute it and/or modify it under the terms of
 * the GNU Affero General Public License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 * 
 * Open Data Plaform is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with Open Data
 * Platform. If not, see <http://www.gnu.org/licenses/agpl-3.0>.
 */

package de.seitenbau.govdata.odp.registry.model;

import java.io.Serializable;
import java.util.List;

/**
 * The Interface Category.
 * 
 * @author rnoerenberg
 */
public interface Category extends Serializable
{

    /**
     * Gets the name.
     * 
     * @return the name
     */
    public String getName();

    /**
     * Gets the display name.
     * 
     * @return the display name
     */
    public String getDisplayName();

    /**
     * Gets the title.
     * 
     * @return the title
     */
    public String getTitle();

    /**
     * Gets the description.
     * 
     * @return the description
     */
    public String getDescription();

    /**
     * Gets the type.
     * 
     * @return the type
     */
    public String getType();

    /**
     * Gets the count.
     * 
     * @return the count
     */
    public int getCount();

    /**
     * Gets the sub categories.
     * 
     * @return the sub categories
     */
    public List<Category> getSubCategories();

    /**
     * Gets the datasets.
     * 
     * @return the datasets
     */
    public List<Metadata> getDatasets();

}
