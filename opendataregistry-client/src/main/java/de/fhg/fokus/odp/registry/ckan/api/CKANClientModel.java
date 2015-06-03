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

import java.util.Date;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.codehaus.jackson.JsonNode;

public interface CKANClientModel {

    @GET
    @Path("/api/2/rest/{path}")
    @Produces("application/json")
    public JsonNode getGenericModel(@PathParam("path") String path);

    @GET
    @Path("/api/2/rest/revision")
    @Produces("application/json")
    public JsonNode getRevisions();

    @POST
    @Path("/api/2/rest/revision")
    @Produces("application/json")
    public JsonNode getRevisions(@QueryParam("since_time") Date since);

    @GET
    @Path("/api/2/rest/revision/{revision}")
    @Produces("application/json")
    public JsonNode getRevision(@PathParam("revision") String revision);

    @GET
    @Path("/api/2/rest/group")
    @Produces("application/json")
    public JsonNode getGroups();

    @GET
    @Path("/api/2/rest/group/{group}")
    @Produces("application/json")
    public JsonNode getGroup(@PathParam("group") String group);

    @GET
    @Path("/api/2/rest/dataset/")
    @Produces("application/json")
    public JsonNode getDatasets();

    @GET
    @Path("/api/2/rest/dataset/{dataset}")
    @Produces("application/json")
    public JsonNode getDataset(@PathParam("dataset") String dataset);

    @GET
    @Path("/api/2/rest/tag")
    @Produces("application/json")
    public JsonNode getTags();

    @GET
    @Path("/api/2/rest/tag/{tag}")
    @Produces("application/json")
    public JsonNode getTag(@PathParam("tag") String name);

    @GET
    @Path("/api/2/rest/rating/{dataset}")
    @Produces("application/json")
    public JsonNode getRating(@PathParam("dataset") String name);

    @GET
    @Path("/api/2/rest/rating")
    @Produces("application/json")
    public JsonNode getRatings();

}
