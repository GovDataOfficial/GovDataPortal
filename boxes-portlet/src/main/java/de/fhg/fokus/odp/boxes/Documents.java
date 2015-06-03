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

import de.fhg.fokus.odp.registry.model.Document;

/**
 * The class constitutes a bean that serves as a source for the latest document on the start page boxes.
 * 
 * @author Nikolay Tcholtchev, Fraunhofer FOKUS
 * @author Benjamin Dittwald, Fraunhofer FOKUS
 * 
 */
@ManagedBean
@SessionScoped
public class Documents {

    /** The cache name. */
    private final String CACHE_NAME = "de.fhg.fokus.odp.boxes";

    /** The cache datasets key. */
    private final String CACHE_DOCUMENTS_KEY = "documents";

    /** The log. */
    private final Logger LOG = LoggerFactory.getLogger(getClass());

    /** The maximum number of latest documents to show. */
    private static final int maximumNumberOfDocuments = 4;

    /** The documents. */
    private List<Document> documents;

    @ManagedProperty("#{registryClient}")
    private RegistryClient registryClient;

    /**
     * An init method for the bean.
     */
    @SuppressWarnings("unchecked")
    @PostConstruct
    public void init() {

        documents = (List<Document>) MultiVMPoolUtil.get(CACHE_NAME, CACHE_DOCUMENTS_KEY);

        if (documents == null) {
            LOG.info("Empty {} cache, fetching documents from CKAN.", CACHE_DOCUMENTS_KEY);
            documents = registryClient.getLatestDocuments(maximumNumberOfDocuments);
            MultiVMPoolUtil.put(CACHE_NAME, CACHE_DOCUMENTS_KEY, documents);
        }
    }

    /**
     * Gets the documents.
     * 
     * @return the documents.
     */
    public List<Document> getDocuments() {
        return documents;
    }

    /**
     * Sets the documents.
     * 
     * @param documents
     *            the documents.
     */
    public void setCategories(List<Document> documents) {
        this.documents = documents;
    }

    /**
     * @param registryClient
     *            the registryClient to set
     */
    public void setRegistryClient(RegistryClient registryClient) {
        this.registryClient = registryClient;
    }

}
