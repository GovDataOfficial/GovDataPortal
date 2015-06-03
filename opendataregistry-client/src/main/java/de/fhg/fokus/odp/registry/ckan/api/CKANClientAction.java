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

import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.codehaus.jackson.JsonNode;
import org.jboss.resteasy.client.ClientResponse;

public interface CKANClientAction {

    @POST
    @Path("/api/3/action/status_show")
    @Produces("application/json")
    @Consumes("application/json")
    public JsonNode status(JsonNode body);

    @POST
    @Path("/api/3/action/package_search")
    @Produces("application/json")
    @Consumes("application/json")
    public ClientResponse<JsonNode> metadataSearch(JsonNode body);

    @POST
    @Path("/api/3/action/user_list")
    @Produces("application/json")
    @Consumes("application/json")
    public JsonNode listUsers(JsonNode body);

    @POST
    @Path("/api/3/action/user_show")
    @Produces("application/json")
    @Consumes("application/json")
    public ClientResponse<JsonNode> showUser(@HeaderParam("Authorization") String authenticationKey, JsonNode body);

    
    @POST
    @Path("/api/3/action/roles_show")
    @Produces("application/json")
    @Consumes("application/json")
    public JsonNode showRoles(JsonNode body);

    @POST
    @Path("/api/3/action/user_role_update")
    @Produces("application/json")
    @Consumes("application/json")
    public JsonNode updateRoles(@HeaderParam("Authorization") String authenticationKey, JsonNode body);

    @POST
    @Path("/api/3/action/user_create")
    @Produces("application/json")
    @Consumes("application/json")
    public ClientResponse<JsonNode> createUser(@HeaderParam("Authorization") String authenticationKey, JsonNode body);

    @POST
    @Path("/api/3/action/group_list")
    @Produces("application/json")
    @Consumes("application/json")
    public ClientResponse<JsonNode> listGroups(JsonNode body);

    @POST
    @Path("/api/3/action/group_show")
    @Produces("application/json")
    @Consumes("application/json")
    public JsonNode showGroup(JsonNode body);

    @POST
    @Path("/api/3/action/tag_list")
    @Produces("application/json")
    @Consumes("application/json")
    public JsonNode listTags(JsonNode body);

    @POST
    @Path("/api/3/action/licence_list")
    @Produces("application/json")
    @Consumes("application/json")
    public JsonNode listLicences(JsonNode body);

    @POST
    @Path("/api/3/action/delete_package")
    @Produces("application/json")
    @Consumes("application/json")
    public JsonNode deleteMetadata(JsonNode body);

    @POST
    @Path("/api/3/action/package_autocomplete")
    @Produces("application/json")
    @Consumes("application/json")
    public JsonNode metadataAutocomplete(JsonNode body);

    @POST
    @Path("/api/3/action/rating_create")
    @Produces("application/json")
    @Consumes("application/json")
    public JsonNode createMetadataRating(@HeaderParam("Authorization") String authenticationKey, JsonNode body);

    @POST
    @Path("/api/3/action/rating_show")
    @Produces("application/json")
    @Consumes("application/json")
    public JsonNode getMetadataRating(@HeaderParam("Authorization") String authenticationKey, JsonNode body);

    @POST
    @Path("/api/3/action/tag_show")
    @Produces("application/json")
    @Consumes("application/json")
    public JsonNode showTag(JsonNode body);

    @POST
    @Path("/api/3/action/package_show")
    @Produces("application/json")
    @Consumes("application/json")
    public JsonNode showMetadata(@HeaderParam("Authorization") String authenticationKey, JsonNode body);

    @POST
    @Path("/api/3/action/package_create")
    @Produces("application/json")
    @Consumes("application/json")
    public JsonNode createMetadata(@HeaderParam("Authorization") String authenticationKey, JsonNode body);

    @POST
    @Path("/api/3/action/package_update")
    @Produces("application/json")
    @Consumes("application/json")
    public JsonNode updateMetadata(@HeaderParam("Authorization") String authenticationKey, JsonNode body);

    @POST
    @Path("/api/3/action/package_relationships_list")
    @Produces("application/json")
    @Consumes("application/json")
    public ClientResponse<JsonNode> listRelationships(@HeaderParam("Authorization") String authenticationKey, JsonNode body);

}
