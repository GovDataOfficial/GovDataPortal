/**
 * Copyright (c) 2012, 2015 Fraunhofer Institute FOKUS
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

package de.fhg.fokus.odp.registry.solr.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

/**
 * The SOLR Client util.
 * 
 * @author msg
 */

public interface SOLRClientUtil {

	@GET
	@Path("/spell")
	@Produces({ "text/plain;charset=utf-8" })
	public String getSpellingCorrection(@QueryParam("q") final String query,
			@QueryParam("wt") final String format, 
			@QueryParam("spellcheck.count") final String count, 
			@QueryParam("spellcheck.extendedResults") final String extendedResults, 
			@QueryParam("spellcheck.dictionary") final String dictionary);

}
