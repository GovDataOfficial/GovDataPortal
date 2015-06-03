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

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.codehaus.jackson.JsonNode;

public interface CKANClientUtil {

    @GET
    @Path("/api/2/util/dataset/autocomplete")
    @Produces("application/json")
    public JsonNode getMetadataAutoComplete(@QueryParam("incomplete") String query);

    @GET
    @Path("/api/2/util/tag/autocomplete")
    @Produces("application/json")
    public JsonNode getTagsAutoComplete(@QueryParam("incomplete") String query);

    @GET
    @Path("/api/2/util/resource/format_autocomplete")
    @Produces("application/json")
    public JsonNode getFormatsAutoComplete(@QueryParam("incomplete") String query);

    @GET
    @Path("/api/2/util/dataset/munge_title_to_name")
    @Produces("text/plain")
    public String mungeTitleToName(@QueryParam("title") String title);

}
