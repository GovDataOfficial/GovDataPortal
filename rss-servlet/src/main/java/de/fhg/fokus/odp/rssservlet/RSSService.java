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

package de.fhg.fokus.odp.rssservlet;

// imports
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fhg.fokus.odp.rssservlet.utils.AbderaSupport;
import de.fhg.fokus.odp.rssservlet.utils.HibernateConfig;
import de.fhg.fokus.odp.rssservlet.utils.LiferayPropsUtil;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

/**
 * The class implements the RSS service.
 * 
 * @author Nikolay Tcholtchev, nikolay.tcholtchev@fokus.fraunhofer.de
 */
@Path("/rssservice")
public class RSSService {

    /** The local RSS handler instance. */
    RSSHandler instance = RSSHandler.getInstance();

    /** The path for the atom generation. */
    private static final String CKAN_ATOM_PATH = "feeds/custom.atom";
    // TODO: make the above constant configurable over a properties file

    /** The logger for the class. */
    private final Logger log = LoggerFactory.getLogger(getClass());

    /** Local variable for the servlet context. */
    @Context
    ServletContext context;

    /**
     * The method gets the RSS feed behind a query.
     * 
     * @return the RSS feed adopted with the links of the platform.
     */
    @GET
    @Produces(MediaType.APPLICATION_ATOM_XML)
    public String getRSSFeed(@Context UriInfo uriInfo) {

        // get the CKAN RSS feed
        String ckanRSSFeed = getCKANRssFeed(uriInfo.getRequestUri().getQuery());
        if (ckanRSSFeed == null) {
            return " ";
        }

        log.debug("RSS feed received ...");

        // obtain the processed RSS feed
        String toreturn = instance.convertAtomFeed(ckanRSSFeed);
        if (toreturn == null) {
            return " ";
        }

        return toreturn;
    }

    @GET
    @Produces(MediaType.APPLICATION_ATOM_XML)
    @Path("comments")
    public Feed getAllCommentRssFeeds(@Context UriInfo uriInfo) {

        Calendar cal = Calendar.getInstance();
        Date date = new Date();
        cal.setTime(date);

        Configuration conf = HibernateConfig.INSTANCE.getConnection(context);

        ServiceRegistryBuilder srb = new ServiceRegistryBuilder().applySettings(conf.getProperties());
        SessionFactory sf = conf.buildSessionFactory(srb.buildServiceRegistry());
        Session session = sf.openSession();

        Transaction tx = session.beginTransaction();

        @SuppressWarnings("unchecked")
        List<CommentaryEntity> queryResults = session.getNamedQuery("getAllCommentsOrderedByCreatedDesc").list();

        tx.commit();
        session.close();

        Feed feed = AbderaSupport.getAbdera().getFactory().newFeed();

        feed.setId("tag:localhost:8080," + cal.get(Calendar.YEAR) + ":/govdata/comments");
        feed.setTitle("GovData Kommentare zu Metadatenbeschreibungen");
        feed.setUpdated(date);
        feed.addAuthor("GovData.de");
        URI feedLink = uriInfo.getRequestUri();
        feed.addLink(feedLink.toString(), "self");

        int i = 1;
        for (CommentaryEntity comment : queryResults) {
            Entry entry = feed.addEntry();
            entry.setId("tag:localhost:8080," + cal.get(Calendar.YEAR) + ":/govdata/comments/entries" + i);
            entry.setTitle("Kommentar zu http://localhost:8080/web/guest/daten/-/details/" + comment.getMetadataname());
            entry.setPublished(comment.getCreated());
            entry.setUpdated(comment.getCreated());
            entry.addAuthor(comment.getLiferayScreenName());
            entry.addLink(feedLink.toString(), "self");

            StringBuilder sb = new StringBuilder();
            sb.append("<div>");
            sb.append("<p>").append(comment.getText()).append("</p>");
            sb.append("<p>").append("Dieser Kommentar wurde erstellt von: " + comment.getLiferayScreenName()).append("</p>");
            sb.append("</div>");

            sb.append("<div>");
            sb.append("<a href=\"http://localhost:8080/web/guest/daten/-/details/" + comment.getMetadataname()
                    + "\">http://localhost:8080/web/guest/daten/-/details/" + comment.getMetadataname() + "</a>");
            sb.append("</div>");

            entry.setContentAsHtml(sb.toString());

            i++;
        }

        return feed;
    }

    /**
     * The method gets the RSS feed of CKAN.
     * 
     * @param propertiesPath
     *            the path to the properties file containing the CKAN url.
     * 
     * @param query
     *            the query for CKAN.
     * 
     * @return the CKAN RSS feed.
     */
    private String getCKANRssFeed(String query) {

        // get the ckan url
        String ckanUrl = LiferayPropsUtil.getValueFromKey("cKANurl", context);
        if (ckanUrl == null) {
            return null;
        }

        if (!ckanUrl.endsWith("/")) {
            ckanUrl = ckanUrl + "/";
        }

        // read from the CKAN API
        URL CKANurlInstance;
        String returnStr = "";

        try {
            // open a connection to the CKAN API
            CKANurlInstance = new URL(ckanUrl + CKAN_ATOM_PATH + "?" + query);
            URLConnection CKANconnection = CKANurlInstance.openConnection();

            log.debug("RSS feed:" + ckanUrl + CKAN_ATOM_PATH + "?" + query);

            // read the output from the API
            BufferedReader in = new BufferedReader(new InputStreamReader(CKANconnection.getInputStream()));

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                returnStr += inputLine + System.getProperty("line.separator");
            }
            in.close();

        }
        // process potential exceptions
        catch (MalformedURLException e) {
            log.debug(e.getMessage());
            return null;
        } catch (IOException e) {
            log.debug(e.getMessage());
            return null;
        }

        return returnStr;
    }

}
