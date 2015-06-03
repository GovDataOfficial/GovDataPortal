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

import org.codehaus.jackson.JsonNode;
import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class CKANClientModelTest {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private CKANClientModel client;

    private final Properties properties = new Properties();

    @BeforeTest
    public void beforeTest() {
        RegisterBuiltin.register(ResteasyProviderFactory.getInstance());
        try {
            properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(PROPERTIES_FILENAME));
            client = ProxyFactory.create(CKANClientModel.class, (String) properties.get(PROPERTY_NAME_CKAN_URL));
        } catch (IOException e) {
            log.error("loading properties file", e);
            client = ProxyFactory.create(CKANClientModel.class, "http://localhost:5000");
        }
    }

    @Test
    public void getGeneric() {
        JsonNode node = client.getGenericModel("dataset");
        log.info(node.toString());
    }

    @Test
    public void getTags() {
        JsonNode node = client.getTags();
        log.info(node.toString());
    }

    @Test
    public void getGroups() {
        JsonNode node = client.getGroups();
        assert (node.isArray());

        for (JsonNode rev : node) {
            assert (rev.isTextual());
            log.info("group: {}", rev.getTextValue());
        }

        log.info("received {} groups", node.size());
    }

    @Test
    public void getGroup() {
        JsonNode node = client.getGroup("70d83689-c55d-4953-965e-623c0f8bcefd");
        log.info("group: {}", node.toString());
    }

    @Test
    public void getRating() {
        JsonNode node = client.getRatings();
        log.info("rating: {}", node.toString());
    }

    @Test
    public void getDataset() {
        JsonNode node = client.getDataset("abfallbeseitigung_berlin_2007-2008");
        log.info("dataset: {}", node.toString());
        JsonNode count = node.get("ratings_count");
        if (count != null && count.isNumber()) {
            log.info("ratings count: {}", count.getNumberValue().intValue());
        }
        JsonNode average = node.get("ratings_average");
        if (average != null && average.isDouble()) {
            log.info("ratings average: {}", average.getDoubleValue());
        }
        JsonNode extras = node.get("extras");
        log.info("extras: {}", extras.toString());
    }
}
