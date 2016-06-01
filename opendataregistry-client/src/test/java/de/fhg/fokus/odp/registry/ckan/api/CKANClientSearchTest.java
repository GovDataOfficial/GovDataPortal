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

package de.fhg.fokus.odp.registry.ckan.api;

import static de.fhg.fokus.odp.registry.ckan.Constants.PROPERTIES_FILENAME;
import static de.fhg.fokus.odp.registry.ckan.Constants.PROPERTY_NAME_CKAN_URL;

import java.io.IOException;
import java.util.Properties;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;

@Ignore("Implement HTTP-Server-Mock")
public class CKANClientSearchTest {
    private final Logger log = LoggerFactory.getLogger(getClass());

    private CKANClientSearch ckanClientSearch;

    private Properties properties = new Properties();

  @Before
    public void beforeTest() {
        RegisterBuiltin.register(ResteasyProviderFactory.getInstance());
        ResteasyClient client = new ResteasyClientBuilder().build();
        try {
            properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(PROPERTIES_FILENAME));
            ResteasyWebTarget target = client.target((String) properties.get(PROPERTY_NAME_CKAN_URL));
            ckanClientSearch = target.proxy(CKANClientSearch.class);
        } catch (IOException e) {
            log.error("loading properties file", e);
            ResteasyWebTarget target = client.target("http://localhost:5000");
            ckanClientSearch = target.proxy(CKANClientSearch.class);
        }
    }

    @Test
    public void getTagCounts() {
        JsonNode node = ckanClientSearch.getTagCounts();
        log.info(node.toString());
    }

}
