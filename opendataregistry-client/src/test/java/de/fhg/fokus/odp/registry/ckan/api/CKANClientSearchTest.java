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

public class CKANClientSearchTest {
    private final Logger log = LoggerFactory.getLogger(getClass());

    private CKANClientSearch client;

    private Properties properties = new Properties();

    @BeforeTest
    public void beforeTest() {
        RegisterBuiltin.register(ResteasyProviderFactory.getInstance());
        try {
            properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(PROPERTIES_FILENAME));
            client = ProxyFactory.create(CKANClientSearch.class, (String) properties.get(PROPERTY_NAME_CKAN_URL));
        } catch (IOException e) {
            log.error("loading properties file", e);
            client = ProxyFactory.create(CKANClientSearch.class, "http://localhost:5000");
        }
    }

    @Test
    public void getTagCounts() {
        JsonNode node = client.getTagCounts();
        log.info(node.toString());
    }

}
