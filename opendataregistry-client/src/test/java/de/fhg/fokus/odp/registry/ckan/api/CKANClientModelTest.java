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
public class CKANClientModelTest {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private CKANClientModel ckanClientModel;

    private final Properties properties = new Properties();

  @Before
    public void beforeTest() {
        RegisterBuiltin.register(ResteasyProviderFactory.getInstance());
        ResteasyClient client = new ResteasyClientBuilder().build();
        try {
            properties.load(
                Thread.currentThread().getContextClassLoader().getResourceAsStream(PROPERTIES_FILENAME));
            ResteasyWebTarget target = client.target((String) properties.get(PROPERTY_NAME_CKAN_URL));
            ckanClientModel = target.proxy(CKANClientModel.class);
        } catch (IOException e) {
            log.error("loading properties file", e);
            ResteasyWebTarget target = client.target("http://localhost:5000");
            ckanClientModel = target.proxy(CKANClientModel.class);
        }
    }

    @Test
    public void getGeneric() {
        JsonNode node = ckanClientModel.getGenericModel("dataset");
        log.info(node.toString());
    }

    @Test
    public void getTags() {
        JsonNode node = ckanClientModel.getTags();
        log.info(node.toString());
    }

    @Test
    public void getGroups() {
        JsonNode node = ckanClientModel.getGroups();
        assert (node.isArray());

        for (JsonNode rev : node) {
            assert (rev.isTextual());
            log.info("group: {}", rev.textValue());
        }

        log.info("received {} groups", node.size());
    }

    @Test
    public void getGroup() {
        JsonNode node = ckanClientModel.getGroup("70d83689-c55d-4953-965e-623c0f8bcefd");
        log.info("group: {}", node.toString());
    }

    @Test
    public void getRating() {
        JsonNode node = ckanClientModel.getRatings();
        log.info("rating: {}", node.toString());
    }

    @Test
    public void getDataset() {
        JsonNode node = ckanClientModel.getDataset("", "abfallbeseitigung_berlin_2007-2008");
        log.info("dataset: {}", node.toString());
        JsonNode count = node.get("ratings_count");
        if (count != null && count.isNumber()) {
            log.info("ratings count: {}", count.numberValue().intValue());
        }
        JsonNode average = node.get("ratings_average");
        if (average != null && average.isDouble()) {
            log.info("ratings average: {}", average.doubleValue());
        }
        JsonNode extras = node.get("extras");
        log.info("extras: {}", extras.toString());
    }
}
