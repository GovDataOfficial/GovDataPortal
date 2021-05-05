/**
 * Copyright (c) 2012, 2013 Fraunhofer Institute FOKUS
 *
 * This file is part of Open Data Platform.
 *
 * Open Data Platform is free software: you can redistribute it and/or modify it under the terms of
 * the GNU Affero General Public License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 * 
 * Open Data Plaform is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with Open Data
 * Platform. If not, see <http://www.gnu.org/licenses/agpl-3.0>.
 */

package de.seitenbau.govdata.odp.registry.ckan.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.databind.JsonNode;

public interface CKANClientAction
{

  @POST
  @Path("/api/3/action/status_show")
  @Produces("application/json")
  @Consumes("application/json")
  public JsonNode status(JsonNode body);

  @POST
  @Path("/api/3/action/package_search")
  @Produces("application/json")
  @Consumes("application/json")
  public Response metadataSearch(JsonNode body);

  @POST
  @Path("/api/3/action/user_list")
  @Produces("application/json")
  @Consumes("application/json")
  public JsonNode listUsers(JsonNode body);

  @POST
  @Path("/api/3/action/user_show")
  @Produces("application/json")
  @Consumes("application/json")
  public Response showUser(@HeaderParam("Authorization") String authenticationKey, JsonNode body);

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
  public Response createUser(@HeaderParam("Authorization") String authenticationKey, JsonNode body);
  
  @POST
  @Path("/api/3/action/user_update")
  @Produces("application/json")
  @Consumes("application/json")
  public Response updateUser(@HeaderParam("Authorization") String authenticationKey, JsonNode body);

  @POST
  @Path("/api/3/action/group_list")
  @Produces("application/json")
  @Consumes("application/json")
  public Response listGroups(JsonNode body);

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
  @Path("/api/3/action/license_list")
  @Produces("application/json")
  @Consumes("application/json")
  public JsonNode listLicences(JsonNode body);

  @POST
  @Path("/api/3/action/user_delete")
  @Consumes("application/json")
  public Response deleteUser(@HeaderParam("Authorization") String authenticationKey, JsonNode body);
  
  @POST
  @Path("/api/3/action/package_delete")
  @Consumes("application/json")
  public Response deleteMetadata(@HeaderParam("Authorization") String authenticationKey, JsonNode body);

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
  @Path("/api/3/action/dcat_dataset_show")
  @Produces("application/json")
  @Consumes("application/json")
  public JsonNode showDcatDataset(@HeaderParam("Authorization") String authenticationKey, JsonNode body);

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
  public Response listRelationships(@HeaderParam("Authorization") String authenticationKey, JsonNode body);
  
  @GET
  @Path("/api/3/action/organization_list_for_user")
  @Produces("application/json")
  public Response getOrganisationsForUser(@HeaderParam("Authorization") String authenticationKey, @QueryParam("permission") String permission);

  @GET
  @Path("/api/3/action/organization_list")
  @Produces("application/json")
  public Response getOrganisations(@QueryParam("all_fields") boolean allfields, @QueryParam("include_extras") boolean includeextras);
}
