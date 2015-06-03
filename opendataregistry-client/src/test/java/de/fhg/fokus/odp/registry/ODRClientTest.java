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

package de.fhg.fokus.odp.registry;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import de.fhg.fokus.odp.registry.ckan.ODRClientImpl;
import de.fhg.fokus.odp.registry.ckan.impl.LicenceImpl;
import de.fhg.fokus.odp.registry.ckan.impl.MetadataImpl;
import de.fhg.fokus.odp.registry.ckan.json.LicenceBean;
import de.fhg.fokus.odp.registry.ckan.json.SpatialDataBean;
import de.fhg.fokus.odp.registry.model.Application;
import de.fhg.fokus.odp.registry.model.Category;
import de.fhg.fokus.odp.registry.model.Contact;
import de.fhg.fokus.odp.registry.model.Dataset;
import de.fhg.fokus.odp.registry.model.Document;
import de.fhg.fokus.odp.registry.model.GeoGranularityEnumType;
import de.fhg.fokus.odp.registry.model.Licence;
import de.fhg.fokus.odp.registry.model.Metadata;
import de.fhg.fokus.odp.registry.model.MetadataEnumType;
import de.fhg.fokus.odp.registry.model.RoleEnumType;
import de.fhg.fokus.odp.registry.model.SectorEnumType;
import de.fhg.fokus.odp.registry.model.SpatialEnumType;
import de.fhg.fokus.odp.registry.model.Tag;
import de.fhg.fokus.odp.registry.model.TemporalGranularityEnumType;
import de.fhg.fokus.odp.registry.model.User;
import de.fhg.fokus.odp.registry.model.exception.OpenDataRegistryException;
import de.fhg.fokus.odp.registry.model.exception.UnknownRoleException;
import de.fhg.fokus.odp.registry.queries.Query;
import de.fhg.fokus.odp.registry.queries.QueryFacet;
import de.fhg.fokus.odp.registry.queries.QueryFacetItem;
import de.fhg.fokus.odp.registry.queries.QueryModeEnumType;
import de.fhg.fokus.odp.registry.queries.QueryResult;
import de.fhg.fokus.odp.spi.OpenDataRegistry;

public class ODRClientTest {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private ODRClient odr;

    @BeforeTest
    public void beforeTest() throws OpenDataRegistryException {
        odr = OpenDataRegistry.getClient();
        odr.init();
    }

    @Test
    public void listCategories() {
        List<Category> categories = odr.listCategories();
        if (categories != null) {
            for (Category category : categories) {
                log.info("category: {} ({})", category.getName(), category.getCount());
                log.info("category type: {}", category.getType());
            }
        }
        assert categories != null : "could not access registry!";
    }

    @Test
    public void listTags() {
        List<Tag> tags = odr.listTags();
        assert tags != null : "could not access registry!";
    }

    @Test
    public void getTagCounts() {
        List<Tag> tags = odr.getTagCounts();
        for (Tag tag : tags) {
            log.info("{} : {}", tag.getName(), tag.getCount());
        }
        assert tags != null : "could not access registry!";
    }

    @Test
    public void listLicences() {
        List<Licence> licences = odr.listLicenses();
        assert licences != null : "could not access registry!";
    }

    @Test
    public void queryMetadata() {
        Query query = new Query();
        // query.getCategories().add("geo");
        // query.getTypes().add(MetadataEnumType.DATASET);
        // query.setIsOpen(true);
        query.setMax(3);

        QueryResult<Metadata> result = odr.queryMetadata(query);
        List<Metadata> metadata = result.getResult();
        for (QueryFacet facet : result.getFacets().values()) {
            log.info("facet name: {}", facet.getName());
            for (QueryFacetItem item : facet.getItems()) {
                log.info("item {} - {}", item.getDisplayName(), item.getCount());
            }
        }
        for (Metadata set : metadata) {
            log.info("metadata: {}", set.getTitle());
        }
        assert metadata != null : "could not access registry!";
    }

    @Test
    public void querySearchTerm() {
        Query query = new Query();
        query.setSearchterm("Ulm");
        QueryResult<Metadata> result = odr.queryMetadata(query);
        if (result.isSuccess()) {
            List<Metadata> metadata = result.getResult();
            for (Metadata set : metadata) {
                log.info("metadata: {}", set.getTitle());
            }
        } else {
            log.info("no success: {}", result.getErrorMessage());
        }
        assert result.isSuccess() : "could not access registry!";
    }

    @Test
    public void extendedQueryMetadata() {
        Query query = new Query(QueryModeEnumType.EXTENDED);
        // query.getTypes().add(MetadataEnumType.APPLICATION);
        // query.setSearchterm("Bodenrichtwerte");
        // query.getCategories().add("geo");
        // query.setIsOpen(true);
        // List<Category> categories = odr.listCategories();
        // for (Category category : categories) {
        // query.getCategories().add(category.getName());
        // }
        // query.getTypes().add(MetadataEnumType.APPLICATION);
        // query.getTypes().add(MetadataEnumType.DOCUMENT);
        // query.getTypes().add(MetadataEnumType.DATASET);
        query.setMax(90);
        // query.setOffset(75);
        // query.getFormats().add("ASCII");
        // query.getSortFields().add("dates desc");

        QueryResult<Metadata> result = odr.queryMetadata(query);
        List<Metadata> metadata = result.getResult();
        log.info("metadata count: {}", metadata.size());
        for (Metadata set : metadata) {
            log.info("metadata typ {}: {} (open: {})",
                    new String[] { set.getType().toField(), set.getTitle(), String.valueOf(set.isOpen()) });
            for (Contact contact : set.getContacts()) {
                try {
                    log.info("{}: {}", contact.getRole().getDisplayName(), contact.getName());
                } catch (UnknownRoleException e) {
                	log.error("UnknownRoleException", e.getMessage());
                }
            }
            // log.info("created: {}", set.getCreatedAsString("dd.MM.yyyy"));
            // log.info("published: {}", set.getPublishedAsString("dd.MM.yyyy"));
            // log.info("modified: {}", set.getModifiedAsString("dd.MM.yyyy"));
            // String averageRating = set.getExtra("rateSum");
            // log.info("average rating is {}", averageRating);
        }
        assert metadata != null : "could not access registry!";
    }

    @Test
    public void getTag() {
        Tag tag = odr.getTag("Ozon");
        assert tag != null : "tag name 'Ozon' unknown!";
    }

    @Test
    public void userShow() {
        User user = odr.findUser("portal");
        assert user != null : "user 'portalnutzera' unknown!";
    }

    @Test
    public void status() {
        String status = odr.status();
        log.info("status of open data registry is: {}", status);
    }

    @Test
    public void createMetadata() {
        MetadataImpl impl = (MetadataImpl) odr.createMetadata(MetadataEnumType.DATASET);
        impl.setTitle("Test Create Metadata V");

        impl.setCreated(new Date());
        impl.setGeoCoverage("sumpfgebiete");
        impl.setGeoGranularity(GeoGranularityEnumType.CITY.toField());
        impl.setModified(new Date());
        impl.setNotes("Simple Metadata for testing.");
        impl.setPublished(new Date());
        impl.setSector(SectorEnumType.OTHER);
        impl.setTemporalGranularity(TemporalGranularityEnumType.MONTH);
        impl.setTemporalGranularityFactor(3);
        impl.setUrl("http://www.fokus.fraunhofer.de/elan");
        try {
            impl.setTemporalCoverageFrom(DateFormat.getInstance().parse("2012-06-01 00:00:00"));
            impl.setTemporalCoverageTo(DateFormat.getInstance().parse("2013-06-01 00:00:00"));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        LicenceBean bean = new LicenceBean();
        bean.setTitle("Creative Commons Attribution Share-Alike");
        bean.setId("cc-by-sa");
        bean.setUrl("http://creativecommons.org/licenses/by-sa/3.0/de");
        impl.setLicence(new LicenceImpl(bean));

        Contact publisher = impl.newContact(RoleEnumType.PUBLISHER);
        publisher.setAddress("Kaiserin-Augusta-Allee 31, 10589 Berlin, Deutschland");
        publisher.setEmail("publisher@fokus.fraunhofer.de");
        publisher.setName("I'm the Publisher");

        impl.getSpatialData().addPolygon();
        impl.getSpatialData().addCoordinate(0,10.0000, 8.0000);
        impl.getSpatialData().addCoordinate(0,10.3000, 8.5000);
        impl.getSpatialData().addCoordinate(0,10.0000, 8.5000);
        impl.getSpatialData().addCoordinate(0,10.3000, 8.0000);
        
        

        impl.setExtra("extra1", "value for extra 1");
        impl.setExtra("extra2", "value for extra 2");

        impl.newTag("test-create-4");

        User user = odr.findUser("sim");

        try {
            odr.persistMetadata(user, impl);
        } catch (OpenDataRegistryException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void rateMetadata() {
        String name = "masteruser";
        String metadata = "de-hh-download-inspire-a3-2-gebaeude";
        User user = odr.findUser(name);
        try {
            Metadata m = odr.getMetadata(user, metadata);
            odr.rateMetadata(user, m.getName(), 1);
            odr.loadRating(m);
            log.info("Rated Metadata {}", m.getName());
            log.info("Average rating: {}", m.getAverageRating());
            log.info("Rating count: {}", m.getRatingCount());
        } catch (OpenDataRegistryException e) {
            log.error("getting metadata", e);
        }
    }

    @Test
    public void createUser() {
        String name = "testnutzer13";
        User user = odr.findUser(name);
        if (user == null) {
            log.info("User {} not found, creating...", name);
			user = odr.createUser(name, name + "@ogdd.de", name);
            if (user != null) {
                log.info("... done");
            }
        } else {
            log.info("User {} already exists", name);
        }
    }

    @Test
    public void getMetadata() {
        try {
            Metadata m = odr.getMetadata(null, "2226-wakendorf");
        } catch (OpenDataRegistryException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getDataset() {
        try {
            Metadata m = odr.getMetadata(null, "berlin-osm-ortsteile");
            log.debug("metadata {} is of type {}", m.getName(), m.getType().toField());
            for (Application app : ((Dataset) m).getRelatedApplications()) {
                log.debug("related application -> {}", app.getTitle());
            }
        } catch (OpenDataRegistryException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getApplication() {
        try {
            Metadata m = odr.getMetadata(null, "de-hh-wms-inspire-a1-6-flurstueck");
            log.debug("metadata {} is of type {}", m.getName(), m.getType().toField());
            for (Dataset data : ((Application) m).getRelatedDatasets()) {
                log.debug("related application -> {}", data.getTitle());
            }
        } catch (OpenDataRegistryException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void listRelationships() {
        ((ODRClientImpl) odr).listRelationships("de-hh-inspire-flurstueck", null);
    }

    @Test
    public void fetchAll() {
        Query query = new Query();
        query.setMax(50);
        long start = System.currentTimeMillis();
        QueryResult<Metadata> result = odr.queryMetadata(query);
        long count = result.getCount();
        if (result.isSuccess()) {
            long pages = result.getCount() / 50;
            for (int i = 1; i < pages; i++) {
                query.setPageoffset(i);
                result = odr.queryMetadata(query);
                for (Metadata m : result.getResult()) {
                    if (m.getLicence().getName() == null) {
                        log.info("Found Metadata with null license: {} - {}", m.getName(), m.getTitle());
                    } else if (m.getLicence().getName().isEmpty()) {
                        log.info("Found Metadata with empty license id: {} - {}", m.getName(), m.getTitle());
                    }
                }
            }
        }
        log.info("fetched {} metadata in {} ms", count, System.currentTimeMillis() - start);
        System.out.println("fetched " + count + " metadata in " + (System.currentTimeMillis() - start) + " ms");
    }

    @Test
    public void getLatestDatasets() {
        Query query = new Query();

        query.getSortFields().add("metadata_modified desc");
        query.setMax(5);

        QueryResult<Dataset> result = odr.queryDatasets(query);
        if (result.isSuccess()) {
            for (Dataset dataset : result.getResult()) {
                log.info("name - title: {} - {}", dataset.getName(), dataset.getTitle());
            }
        }
    }

    @Test
    public void getLatestDocuments() {
        Query query = new Query();

        query.getSortFields().add("metadata_modified desc");
        query.setMax(5);

        QueryResult<Document> result = odr.queryDocuments(query);
        if (result.isSuccess()) {
            for (Document document : result.getResult()) {
                log.info("name+title: {} - {}", document.getName(), document.getTitle());
            }
        }
    }

    @Test
    public void getLatestApplications() {
        Query query = new Query();

        query.getSortFields().add("metadata_modified desc");
        query.setMax(5);

        QueryResult<Application> result = odr.queryApplications(query);
        if (result.isSuccess()) {
            for (Application application : result.getResult()) {
                log.info("name+title: {} - {}", application.getName(), application.getTitle());
            }
        }
    }

}
