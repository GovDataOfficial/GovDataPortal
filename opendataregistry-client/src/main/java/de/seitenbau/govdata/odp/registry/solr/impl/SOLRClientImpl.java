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

package de.seitenbau.govdata.odp.registry.solr.impl;

import static de.seitenbau.govdata.odp.registry.ckan.Constants.PROPERTIES_FILENAME;
import static de.seitenbau.govdata.odp.registry.solr.Constants.PROPERTY_NAME_SOLR_URL;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.client.jaxrs.engines.ApacheHttpClient4Engine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.seitenbau.govdata.odp.registry.solr.SOLRClient;
import de.seitenbau.govdata.odp.registry.solr.SolrException;
import de.seitenbau.govdata.odp.registry.solr.api.SOLRClientUtil;

/**
 * The SOLR Client Impl.
 * 
 * @author msg
 */
public class SOLRClientImpl implements SOLRClient {

	private static final Logger log = LoggerFactory
			.getLogger(SOLRClientImpl.class);

	private SOLRClientUtil solrutil;

	private static final ObjectMapper OM = new ObjectMapper();

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.seitenbau.govdata.odp.registry.ODRClient#init()
	 */
	@Override
	public void init() throws SolrException {
		Properties props = new Properties();
		try {
			props.load(Thread.currentThread().getContextClassLoader()
					.getResourceAsStream(PROPERTIES_FILENAME));
		} catch (IOException e) {
			log.error("init solr client implementation", e);
			throw new SolrException(
					"Could not find or read properies file 'ckan.properties'.");
		}
		init(props);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.seitenbau.govdata.odp.registry.ODRClient#init(java.util.Properties)
	 */
	@Override
	public void init(Properties props) {

    String solrurl = props.getProperty(PROPERTY_NAME_SOLR_URL);

    CloseableHttpClient httpClient =
        HttpClientBuilder.create().setConnectionManager(new PoolingHttpClientConnectionManager()).build();
    ApacheHttpClient4Engine engine = new ApacheHttpClient4Engine(httpClient);
    ResteasyClient client = new ResteasyClientBuilder().httpEngine(engine).build();
    ResteasyWebTarget target = client.target(solrurl);
    solrutil = target.proxy(SOLRClientUtil.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.seitenbau.govdata.odp.registry.ODRClient#spellingSuggestions(java.lang.String)
	 */
	@Override
	public List<MString> spellingSuggestions(String fragment) {
		List<MString> spellingSuggesList = new ArrayList<MString>();

		if (fragment != null && !fragment.isEmpty()) {
			JsonNode result = null;
			log.debug(
					"spellingSuggestions:REST > calling solr util solr/spell?: {}",
					fragment);
			long start = System.currentTimeMillis();
			try {
				String resultStr = solrutil.getSpellingCorrection(fragment,
						"json", "1", "false", "default");
				log.debug("/spell: {}ms", System.currentTimeMillis() - start);
				ObjectMapper mapper = new ObjectMapper();

				result = mapper.readTree(resultStr);
			} catch (JsonProcessingException je) {
				log.error("spellingSuggestions:JsonProcessingException:"
						+ je.getMessage());
			} catch (IOException e1) {
				log.error("spellingSuggestions:IOException:" + e1.getMessage());
			} catch (Exception e) {
				log.error("spellingSuggestions:Exception:" + e.getMessage());
			}
			if (result != null) {
				JsonNode collationNode = getSuggestionsResultList(result);
        String collation = collationNode.textValue();
				if (collation != null && !collation.isEmpty())
					spellingSuggesList.add(new MString(collation));
			}
		}

		return spellingSuggesList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.seitenbau.govdata.odp.registry.ODRClientImpl#getSuggestionsResultList(JsonNode
	 * )
	 */
	private JsonNode getSuggestionsResultList(JsonNode result) {
		JsonNode resultSpellcheck = result.get("spellcheck");
		if (resultSpellcheck != null) {
			JsonNode resultSuggestions = resultSpellcheck.get("suggestions");
			if (resultSuggestions != null) {
				if (resultSuggestions.size() == 0) {
					// No suggestions
					return OM.createArrayNode();
				}
				int collationIndex = resultSuggestions.size() - 1;
				if (resultSuggestions.size() >= 2
						&& resultSuggestions.get(collationIndex) != null) {
					JsonNode collation = resultSuggestions.get(collationIndex);
					if (collation != null) {
						// log.info("solr collation=" + collation);
						return collation;
					} else {
						log.error("solr json node parse error collation is null");
						return OM.createArrayNode();
					}
				} else {
					log.error("solr json node parse error first element of suggestions is null");
					return OM.createArrayNode();
				}
			} else {
				log.error("solr json node parse error suggestions is null");
				return OM.createArrayNode();
			}
		} else {
			log.error("solr json node parse error spellcheck is null");
			return OM.createArrayNode();
		}
	}

}
