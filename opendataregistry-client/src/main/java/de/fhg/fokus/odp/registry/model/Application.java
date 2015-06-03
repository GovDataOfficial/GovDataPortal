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

package de.fhg.fokus.odp.registry.model;

import java.util.List;

/**
 * The Interface Application. Application is the typed Metadata with a few additional item relations (related Datasets).
 * 
 * @author sim
 * 
 * @see Metadata
 */
public interface Application extends Metadata {

    /**
     * Gets the used datasets as urls (OGDD schema).
     * 
     * @return the used datasets as urls
     */
    public List<String> getUsedDatasets();

    /**
     * Gets the related datasets from within this repository.
     * 
     * @return the related datasets
     */
    public List<Dataset> getRelatedDatasets();

}
