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

package de.fhg.fokus.odp.boxes;

// imports
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liferay.portal.kernel.cache.MultiVMPoolUtil;

import de.fhg.fokus.odp.registry.model.Dataset;

/**
 * The class constitutes a bean that serves as a source for the latest datasets on the start page boxes.
 * 
 * @author Nikolay Tcholtchev, Fraunhofer FOKUS
 * @author Benjamin Dittwald, Fraunhofer FOKUS
 * 
 */
@ManagedBean
@SessionScoped
public class Datasets {

    /** The cache name. */
    private final String CACHE_NAME = "de.fhg.fokus.odp.boxes";

    /** The cache datasets key. */
    private final String CACHE_DATASETS_KEY = "datasets";

    /** The log. */
    private final Logger LOG = LoggerFactory.getLogger(getClass());

    /** The maximum number of the latest datasets to show. */
    private final int maximumNumberOfDatasets = 4;

    /** The datasets. */
    private List<Dataset> datasets;

    /** The registry client. */
    @ManagedProperty("#{registryClient}")
    private RegistryClient registryClient;

    /**
     * An init method for the bean.
     */
    @SuppressWarnings("unchecked")
    @PostConstruct
    public void init() {

        datasets = (List<Dataset>) MultiVMPoolUtil.get(CACHE_NAME, CACHE_DATASETS_KEY);

        if (datasets == null) {
            LOG.info("Empty {} cache, fetching datasets from CKAN.", CACHE_DATASETS_KEY);
            datasets = registryClient.getLatestDatasets(maximumNumberOfDatasets);
            MultiVMPoolUtil.put(CACHE_NAME, CACHE_DATASETS_KEY, datasets);
        }
    }

    /**
     * Gets the datasets.
     * 
     * @return the datasets.
     */
    public List<Dataset> getDatasets() {
        return datasets;
    }

    /**
     * Sets the datasets.
     * 
     * @param datasets
     *            the datasets.
     */
    public void setCategories(List<Dataset> datasets) {
        this.datasets = datasets;
    }

    /**
     * Sets the registry client.
     * 
     * @param registryClient
     *            the registryClient to set
     */
    public void setRegistryClient(RegistryClient registryClient) {
        this.registryClient = registryClient;
    }

}
