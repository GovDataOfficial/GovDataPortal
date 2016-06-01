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

import java.io.Serializable;
// imports
import java.util.List;
import java.util.Properties;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liferay.portal.kernel.util.PropsUtil;

import de.fhg.fokus.odp.registry.ODRClient;
import de.fhg.fokus.odp.registry.model.Application;
import de.fhg.fokus.odp.registry.model.Dataset;
import de.fhg.fokus.odp.registry.model.Document;
import de.fhg.fokus.odp.registry.queries.Query;
import de.fhg.fokus.odp.registry.queries.QueryResult;
import de.fhg.fokus.odp.spi.OpenDataRegistry;

/**
 * The registry client for accessing CKAN and filling the boxes.
 * 
 * @author Nikolay Tcholtchev, Fraunhofer FOKUS
 */
@ManagedBean
@SessionScoped
public class RegistryClient implements Serializable {
	private static final long serialVersionUID = 705645042441128266L;

	private final static Logger log = LoggerFactory.getLogger(RegistryClient.class);

    /** The name of the configuration property for the authentication key. */
    private static final String PROP_NAME_AUTHORIZATION_KEY = "authenticationKey";

    /** The name of the configuration property for the CKAN url. */
    private static final String PROP_NAME_CKAN_URL = "cKANurl";

    /** The local instance. */
    private final ODRClient clientInstance;

    /**
     * Constructor for the singleton pattern.
     */
    public RegistryClient() {
        clientInstance = OpenDataRegistry.getClient();
        Properties props = new Properties();
        props.setProperty("ckan.authorization.key", PropsUtil.get(PROP_NAME_AUTHORIZATION_KEY));
        props.setProperty("ckan.url", PropsUtil.get(PROP_NAME_CKAN_URL));
        clientInstance.init(props);
    }

    /**
     * The method takes care of obtaining the latest datasets from the registry.
     * 
     * @param numberOfDatasets
     *            the number of datasets.
     * 
     * @return a list with the latest datasets.
     */
    public List<Dataset> getLatestDatasets(int numberOfDatasets) {
        Query query = new Query();

        query.getSortFields().add("metadata_modified desc");
        query.setMax(numberOfDatasets);

        long start = System.currentTimeMillis();
        QueryResult<Dataset> result = clientInstance.queryDatasets(query);
        log.debug("queryDatasets duration: {} ms", System.currentTimeMillis() - start);

        return result.getResult();
    }

    /**
     * The method takes care of obtaining the latest apps from the registry.
     * 
     * @param numberOfApps
     *            the number of apps.
     * 
     * @return a list with the latest apps.
     */
    public List<Application> getLatestApps(int numberOfApps) {
        Query query = new Query();

        query.getSortFields().add("metadata_modified desc");
        query.setMax(numberOfApps);

        long start = System.currentTimeMillis();
        QueryResult<Application> result = clientInstance.queryApplications(query);
        log.debug("queryApplictions duration: {} ms", System.currentTimeMillis() - start);

        return result.getResult();
    }

    /**
     * The method takes care of obtaining the latest docs from the registry.
     * 
     * @param numberOfDocs
     *            the number of docs.
     * 
     * @return a list with the latest docs.
     */
    public List<Document> getLatestDocuments(int maximumNumberOfDocs) {
        Query query = new Query();

        query.getSortFields().add("metadata_modified desc");
        query.setMax(maximumNumberOfDocs);

        long start = System.currentTimeMillis();
        QueryResult<Document> result = clientInstance.queryDocuments(query);
        log.debug("queryDocuments duration: {} ms", System.currentTimeMillis() - start);

        return result.getResult();
    }

    public ODRClient getOdrClient() {
        return clientInstance;
    }

}
