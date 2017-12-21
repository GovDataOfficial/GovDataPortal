/**
 * Copyright (c) 2012, 2013 Fraunhofer Institute FOKUS | 2017 SEITENBAU GmbH
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

package de.seitenbau.govdata.odp.registry.ckan;

import static de.seitenbau.govdata.odp.registry.ckan.Constants.JSON_DATETIME_PATTERN;
import static de.seitenbau.govdata.odp.registry.ckan.Constants.JSON_FIELD_ALL_FIELDS;
import static de.seitenbau.govdata.odp.registry.ckan.Constants.PROPERTIES_FILENAME;
import static de.seitenbau.govdata.odp.registry.ckan.Constants.PROPERTY_NAME_CKAN_AUTHORIZATION_KEY;
import static de.seitenbau.govdata.odp.registry.ckan.Constants.PROPERTY_NAME_CKAN_URL;
import static de.seitenbau.govdata.odp.registry.ckan.Constants.PROP_NAME_DEFAULT_SORT_METADATA;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.client.jaxrs.engines.ApacheHttpClient4Engine;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

import de.seitenbau.govdata.odp.registry.ODRClient;
import de.seitenbau.govdata.odp.registry.ckan.api.CKANClientAction;
import de.seitenbau.govdata.odp.registry.ckan.api.CKANClientModel;
import de.seitenbau.govdata.odp.registry.ckan.api.CKANClientUtil;
import de.seitenbau.govdata.odp.registry.ckan.impl.CategoryImpl;
import de.seitenbau.govdata.odp.registry.ckan.impl.LicenceImpl;
import de.seitenbau.govdata.odp.registry.ckan.impl.MetadataImpl;
import de.seitenbau.govdata.odp.registry.ckan.impl.OrganizationImpl;
import de.seitenbau.govdata.odp.registry.ckan.impl.QueryFacetImpl;
import de.seitenbau.govdata.odp.registry.ckan.impl.QueryFacetItemImpl;
import de.seitenbau.govdata.odp.registry.ckan.impl.QueryResultImpl;
import de.seitenbau.govdata.odp.registry.ckan.impl.ResourceImpl;
import de.seitenbau.govdata.odp.registry.ckan.impl.TagImpl;
import de.seitenbau.govdata.odp.registry.ckan.impl.UserImpl;
import de.seitenbau.govdata.odp.registry.ckan.json.GroupBean;
import de.seitenbau.govdata.odp.registry.ckan.json.LicenceBean;
import de.seitenbau.govdata.odp.registry.ckan.json.MetadataBean;
import de.seitenbau.govdata.odp.registry.ckan.json.OrganizationBean;
import de.seitenbau.govdata.odp.registry.ckan.json.RelationshipBean;
import de.seitenbau.govdata.odp.registry.ckan.json.ResourceBean;
import de.seitenbau.govdata.odp.registry.ckan.json.StatusBean;
import de.seitenbau.govdata.odp.registry.ckan.json.TagBean;
import de.seitenbau.govdata.odp.registry.ckan.json.UserBean;
import de.seitenbau.govdata.odp.registry.model.Category;
import de.seitenbau.govdata.odp.registry.model.Licence;
import de.seitenbau.govdata.odp.registry.model.Metadata;
import de.seitenbau.govdata.odp.registry.model.MetadataEnumType;
import de.seitenbau.govdata.odp.registry.model.Organization;
import de.seitenbau.govdata.odp.registry.model.Resource;
import de.seitenbau.govdata.odp.registry.model.Tag;
import de.seitenbau.govdata.odp.registry.model.User;
import de.seitenbau.govdata.odp.registry.model.exception.OpenDataRegistryException;
import de.seitenbau.govdata.odp.registry.queries.Query;
import de.seitenbau.govdata.odp.registry.queries.QueryFacet;
import de.seitenbau.govdata.odp.registry.queries.QueryFacetItem;
import de.seitenbau.govdata.odp.registry.queries.QueryResult;

/**
 * The Class ODRClientImpl.
 * 
 * @author sim, msg
 * @author rnoerenberg
 */
public class ODRClientImpl implements ODRClient
{

  private static final Logger LOG = LoggerFactory.getLogger(OpenDataRegistryProvider.class);

  private CKANClientModel model;

  private CKANClientAction action;

  private CKANClientUtil util;

  private String authorizationKey;

  private String defaultSortStr;

  private StatusBean status;

  private static final ObjectMapper OM = new ObjectMapper();

  private static final ObjectNode ALL_FIELDS = OM.createObjectNode();

  private List<Licence> licencesCache = null;

  /*
   * (non-Javadoc)
   * 
   * @see de.seitenbau.govdata.odp.registry.ODRClient#init()
   */
  @Override
  public void init() throws OpenDataRegistryException
  {
    Properties props = new Properties();
    try
    {
      props.load(Thread.currentThread().getContextClassLoader()
          .getResourceAsStream(PROPERTIES_FILENAME));
    }
    catch (IOException e)
    {
      LOG.error("init open data registry client implementation (CKAN)", e);
      throw new OpenDataRegistryException(
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
  public void init(Properties props)
  {

    authorizationKey = props
        .getProperty(PROPERTY_NAME_CKAN_AUTHORIZATION_KEY);
    defaultSortStr = props.getProperty(PROP_NAME_DEFAULT_SORT_METADATA);
    String url = props.getProperty(PROPERTY_NAME_CKAN_URL);
    RegisterBuiltin.register(ResteasyProviderFactory.getInstance());

    CloseableHttpClient httpClient =
        HttpClientBuilder.create().setConnectionManager(new PoolingHttpClientConnectionManager()).build();
    ApacheHttpClient4Engine engine = new ApacheHttpClient4Engine(httpClient);
    ResteasyClient client = new ResteasyClientBuilder().httpEngine(engine).build();
    ResteasyWebTarget target = client.target(url);

    action = target.proxy(CKANClientAction.class);
    util = target.proxy(CKANClientUtil.class);
    model = target.proxy(CKANClientModel.class);

    ALL_FIELDS.put(JSON_FIELD_ALL_FIELDS, true);

    LOG.trace("REST > calling action api 'status' with nothing");
    long start = System.currentTimeMillis();
    JsonNode node = action.status(OM.createObjectNode());
    LOG.debug("/api/3/action/status_show: {}ms", System.currentTimeMillis()
        - start);
    LOG.trace("REST < returns: {}", node);
    if (isSuccess(node))
    {
      status = convert(getResultObject(node), StatusBean.class);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.seitenbau.govdata.odp.registry.ODRClient#listCategories()
   */
  @Override
  public List<Category> listCategories()
  {

    List<Category> all = new ArrayList<>();

    Response response = null;
    try
    {
      LOG.trace("REST > calling action api 'group_list' with: {}",
          ALL_FIELDS);
      long start = System.currentTimeMillis();
      response = action.listGroups(ALL_FIELDS);
      LOG.debug("/api/3/action/group_list: {}ms",
          System.currentTimeMillis() - start);
      LOG.trace("REST < returns: {}", response);

      if (response.getStatusInfo() == Status.OK)
      {
        JsonNode nodes = response.readEntity(JsonNode.class);
        LOG.trace("REST < returns: {}", nodes);

        if (isSuccess(nodes))
        {
          JsonNode result = getResultList(nodes);
          for (JsonNode node : result)
          {
            all.add(new CategoryImpl(convert(node, GroupBean.class)));
          }

        }
      }
      else
      {
        LOG.trace("REST < (http) returns: {} - {}", response
            .getStatusInfo().getFamily().name(), response
                .getStatusInfo().getReasonPhrase());
      }
    }
    finally
    {
      if (response != null)
      {
        response.close();
      }
    }

    return all;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.seitenbau.govdata.odp.registry.ODRClient#listTags()
   */
  @Override
  public List<Tag> listTags()
  {

    List<Tag> tags = new ArrayList<>();

    LOG.trace("REST > calling action api 'tag_list' with: {}", ALL_FIELDS);
    long start = System.currentTimeMillis();
    JsonNode result = action.listTags(ALL_FIELDS);
    LOG.debug("/api/3/action/tag_list: {}ms", System.currentTimeMillis()
        - start);
    LOG.trace("REST < returns: {}", result);

    JsonNode list = getResultList(result);
    for (JsonNode tagNode : list)
    {
      tags.add(new TagImpl(convert(tagNode, TagBean.class)));
    }

    return tags;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.seitenbau.govdata.odp.registry.ODRClient#listLicences()
   */
  @Override
  public List<Licence> listLicenses()
  {
    // Use the cache if possible.
    if (licencesCache == null)
    {
      licencesCache = new ArrayList<>();

      LOG.trace("REST > calling action api 'licence_list' with nothing");
      long start = System.currentTimeMillis();
      JsonNode result = action.listLicences(OM.createObjectNode());
      LOG.debug("/api/3/action/licence_list: {}ms", System.currentTimeMillis() - start);
      LOG.trace("REST < returns: {}", result);

      JsonNode list = getResultList(result);
      for (JsonNode entry : list)
      {
        licencesCache.add(new LicenceImpl(convert(entry, LicenceBean.class)));
      }
    }
    return licencesCache;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.seitenbau.govdata.odp.registry.ODRClient#autoSuggestMetadata(java.lang.String)
   */
  @Override
  public List<String> autoSuggestMetadata(String fragment)
  {

    LOG.trace("REST > calling util api 'dataset/autocomplete' with: {}",
        fragment);
    long start = System.currentTimeMillis();
    JsonNode result = util.getMetadataAutoComplete(fragment);
    LOG.debug("/api/2/util/dataset/autocomplete: {}ms",
        System.currentTimeMillis() - start);
    LOG.trace("REST < returns: {}", result);

    JsonNode list = getAutoCompleteResultList(result);

    List<String> autocom = new ArrayList<>();
    for (JsonNode node : list)
    {
      autocom.add(node.get("title").textValue());
    }
    return autocom;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.seitenbau.govdata.odp.registry.ODRClient#autoSuggestTags(java.lang.String)
   */
  @Override
  public List<String> autoSuggestTags(String fragment)
  {

    LOG.trace("REST > calling util api 'tag/autocomplete' with: {}",
        fragment);
    long start = System.currentTimeMillis();
    JsonNode result = util.getTagsAutoComplete(fragment);
    LOG.debug("/api/2/util/tag/autocomplete: {}ms",
        System.currentTimeMillis() - start);
    LOG.trace("REST < returns: {}", result);

    JsonNode list = getAutoCompleteResultList(result);

    List<String> autocom = new ArrayList<>();
    for (JsonNode node : list)
    {
      autocom.add(node.get("Name").textValue());
    }
    return autocom;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.seitenbau.govdata.odp.registry.ODRClient#autoSuggestFormats(java.lang.String)
   */
  @Override
  public List<String> autoSuggestFormats(String fragment)
  {

    LOG.trace("REST > calling util api 'format_autocomplete' with: {}",
        fragment);
    long start = System.currentTimeMillis();
    JsonNode result = util.getFormatsAutoComplete(fragment);
    LOG.debug("/api/2/util/resource/format_autocomplete: {}ms",
        System.currentTimeMillis() - start);
    LOG.trace("REST < returns: {}", result);

    JsonNode list = getAutoCompleteResultList(result);

    List<String> autocom = new ArrayList<>();
    for (JsonNode node : list)
    {
      autocom.add(node.get("Format").textValue());
    }
    return autocom;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.seitenbau.govdata.odp.registry.ODRClient#queryMetadata(de.seitenbau.govdata.odp.registry
   * .queries.Query)
   */
  @Override
  public QueryResult<Metadata> queryMetadata(Query query)
  {
    return query(query);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.seitenbau.govdata.odp.registry.ODRClient#queryDatasets(de.seitenbau.govdata.odp.registry
   * .queries.Query)
   */
  @Override
  public QueryResult<Metadata> queryDatasets(Query query)
  {
    query.getTypes().clear();
    query.getTypes().add(MetadataEnumType.DATASET);
    return query(query);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.seitenbau.govdata.odp.registry.ODRClient#getTag(java.lang.String)
   */
  @Override
  public Tag getTag(String name)
  {
    ObjectNode obj = OM.createObjectNode();
    obj.put("id", name);

    LOG.trace("REST > calling action api 'tag_show' with {}", obj);
    long start = System.currentTimeMillis();
    JsonNode result = action.showTag(obj);
    LOG.debug("/api/3/action/tag_show: {}ms", System.currentTimeMillis()
        - start);
    LOG.trace("REST < returns: {}", result);

    JsonNode tag = getResultObject(result);

    // List<Metadata> packages = MetadataImpl.read(tag.get("packages"));
    TagBean bean = convert(tag, TagBean.class);
    // bean.setMetadatas(packages);

    return new TagImpl(bean);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.seitenbau.govdata.odp.registry.ODRClient#findUser(java.lang.String)
   */
  @Override
  public User findUser(String name)
  {
    ObjectNode userName = OM.createObjectNode();
    userName.put("id", name);

    UserImpl impl = null;

    Response response = null;
    try
    {
      LOG.trace("REST > calling action api 'user_show' with: {}",
          userName);
      long start = System.currentTimeMillis();
      response = action.showUser(authorizationKey, userName);
      LOG.debug("/api/3/action/user_show: {}ms",
          System.currentTimeMillis() - start);

      if (response.getStatus() == Response.Status.OK.getStatusCode())
      {
        JsonNode node = response.readEntity(JsonNode.class);
        LOG.trace("REST < returns: {}", node);

        JsonNode userNode = node.get("result");

        UserBean userBean = convert(userNode, UserBean.class);
        impl = new UserImpl(userBean);
        if (userBean == null)
        {
          LOG.warn("findUser: Could not fetch user object!");
        }
        else if (userBean.getApikey() == null)
        {
          LOG.warn("findUser: Could not fetch apikey for user! Maybe wrong apikey in config file?");
        }
      }
      else
      {
        LOG.trace("REST < (http) returns: {} - {}", response
            .getStatusInfo().getFamily().name(), response
                .getStatusInfo().getReasonPhrase());
      }
    }
    finally
    {
      if (response != null)
      {
        response.close();
      }
    }
    return impl;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.seitenbau.govdata.odp.registry.ODRClient#createUser(java.lang.String, java.lang.String,
   * java.lang.String)
   */
  @Override
  public User createUser(String name, String email, String password)
  {
    ObjectNode user = OM.createObjectNode();
    user.put("name", name);
    user.put("email", email);
    user.put("password", password);
    UserImpl impl = null;

    Response response = null;
    try
    {
      LOG.trace("REST > calling action api 'user_create' with: {}", user);
      long start = System.currentTimeMillis();
      response = action.createUser(authorizationKey, user);
      LOG.debug("/api/3/action/user_create: {}ms",
          System.currentTimeMillis() - start);
      if (response.getStatus() == Response.Status.OK.getStatusCode())
      {
        JsonNode result = response.readEntity(JsonNode.class);
        LOG.trace("REST < returns: {}", response);

        JsonNode userNode = result.get("result");
        impl = new UserImpl(convert(userNode, UserBean.class));
      }
      else
      {
        LOG.trace("REST < (http) returns: {} - {}", response
            .getStatusInfo().getFamily().name(), response
                .getStatusInfo().getReasonPhrase());
      }
    }
    finally
    {
      if (response != null)
      {
        response.close();
      }
    }
    return impl;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.seitenbau.govdata.odp.registry.ODRClient#status()
   */
  @Override
  public String status()
  {
    LOG.trace("REST > calling action api 'status' with nothing");
    long start = System.currentTimeMillis();
    JsonNode node = action.status(OM.createObjectNode());
    LOG.debug("/api/3/action/status_show: {}ms", System.currentTimeMillis()
        - start);
    LOG.trace("REST < returns: {}", node);
    return node.toString();
  }

  /**
   * Convert.
   * 
   * @param <T> the generic type
   * @param node the node
   * @param clazz the clazz
   * @return the t
   */
  public static <T> T convert(JsonNode node, Class<T> clazz)
  {
    T obj = null;
    try
    {
      obj = OM.readValue(node.traverse(), clazz);
    }
    catch (JsonParseException e)
    {
      LOG.error("deserialize json node ({}): {}", e.getMessage(), node);
    }
    catch (JsonMappingException e)
    {
      LOG.error("deserialize json node({}): {}", e.getMessage(), node);
    }
    catch (IOException e)
    {
      LOG.error("deserialize json node ({}): {}", e.getMessage(), node);
    }
    return obj;
  }

  /**
   * Konvertiert ein Java-Objekt zu einem {@link JsonNode}.
   * 
   * @param obj das zu konvertierende Objekt.
   * @return das erzeugte {@link JsonNode}-Objekt.
   */
  public static <T> JsonNode convert(T obj)
  {
    ObjectMapper m = new ObjectMapper();
    m.setDateFormat(new SimpleDateFormat(JSON_DATETIME_PATTERN));

    return m.valueToTree(obj);
  }

  @SuppressWarnings("unchecked")
  private <T> QueryResult<T> query(Query query)
  {
    List<Metadata> all = new LinkedList<>();

    JsonNode param = queryToJson(query);

    Map<String, QueryFacet> facets = null;

    QueryResultImpl<T> queryResult = new QueryResultImpl<>();

    Response response = null;
    try
    {
      LOG.info("REST > calling action api 'package_search' with: {}",
          param);
      long start = System.currentTimeMillis();
      response = action.metadataSearch(param);
      LOG.debug("/api/3/action/package_search: {}ms",
          System.currentTimeMillis() - start);

      if (response.getStatusInfo() == Status.OK)
      {
        JsonNode result = response.readEntity(JsonNode.class);
        LOG.trace("REST < returns: {}", result);

        JsonNode list = getSearchResult(result);
        for (JsonNode node : list)
        {
          MetadataImpl impl;
          try
          {
            impl = generateImpl(node);
          }
          catch (OpenDataRegistryException e)
          {
            LOG.error("Mapping metadata: {}", node);
            continue;
          }

          all.add(impl);

          JsonNode res = result.get("result");
          if (res != null)
          {
            JsonNode searchFacets = res.get("search_facets");
            facets = QueryFacetImpl.read(searchFacets);
          }
        }

        LOG.debug(
            "parsed and added {} metadata objects to the result list",
            all.size());

        queryResult.setResult((List<T>) all);
        queryResult.setCount(getCount(result));
        queryResult.setLimit(query.getMax());
        queryResult.setOffset(query.getOffset());
        queryResult.setPageOffset(query.getPageoffset());
        if (facets != null && !facets.isEmpty())
        {
          QueryFacet licenses = facets.get("license_id");
          for (QueryFacetItem license : licenses.getItems())
          {
            for (Licence lic : listLicenses())
            {
              if (license.getName().equalsIgnoreCase(
                  lic.getName()))
              {
                ((QueryFacetItemImpl) license)
                    .setDisplayName(lic.getTitle());
              }
            }
          }
          queryResult.getFacets().putAll(facets);
        }
      }
      else
      {
        LOG.debug("REST < (http) returns: {} - {}", response
            .getStatusInfo().getStatusCode(), response
                .getStatusInfo().getReasonPhrase());
        queryResult.setSuccess(false);
        queryResult.setErrorMessage(response.getStatusInfo()
            .getReasonPhrase());
      }
    }
    finally
    {
      if (response != null)
      {
        response.close();
      }
    }

    return queryResult;
  }

  private JsonNode queryToJson(Query query)
  {
    ObjectNode node = OM.createObjectNode();

    String searchterm = query.getSearchterm();

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

    switch (query.getMode())
    {
    case EXTENDED:
      if (!query.getTypes().isEmpty())
      {
        StringBuilder types = new StringBuilder("type:(");
        for (MetadataEnumType type : query.getTypes())
        {
          if (!types.toString().endsWith(":("))
          {
            types.append(" OR ");
          }
          types.append(type.toField());
        }
        types.append(")");

        searchterm = (searchterm == null) ? types.toString()
            : searchterm + " " + types.toString();
      }

      if (!query.getCategories().isEmpty())
      {
        StringBuilder categories = new StringBuilder("groups:(");
        for (String category : query.getCategories())
        {
          if (!categories.toString().endsWith(":("))
          {
            categories.append(" OR ");
          }
          categories.append(category);
        }
        categories.append(")");

        searchterm = (searchterm == null) ? categories.toString()
            : searchterm + " " + categories.toString();
      }

      if (!query.getTags().isEmpty())
      {
        StringBuilder tags = new StringBuilder("tags:(");
        for (String tag : query.getTags())
        {
          if (!tags.toString().endsWith(":("))
          {
            tags.append(" OR ");
          }
          tags.append(tag);
        }
        tags.append(")");

        searchterm = (searchterm == null) ? tags.toString()
            : searchterm + " " + tags.toString();
      }

      if (!query.getLicences().isEmpty())
      {
        StringBuilder licences = new StringBuilder("license_id:(");
        for (String licence : query.getLicences())
        {
          if (!licences.toString().endsWith(":("))
          {
            licences.append(" OR ");
          }
          licences.append(licence);
        }
        licences.append(")");

        searchterm = (searchterm == null) ? licences.toString()
            : searchterm + " " + licences.toString();
      }

      if (!query.getFormats().isEmpty())
      {
        StringBuilder formats = new StringBuilder("res_format:(");
        for (String format : query.getFormats())
        {
          if (!formats.toString().endsWith(":("))
          {
            formats.append(" OR ");
          }
          formats.append(format);
        }
        formats.append(")");

        searchterm = (searchterm == null) ? formats.toString()
            : searchterm + " " + formats.toString();
      }

      if (query.getIsOpen() != null)
      {
        String isopen = "isopen:" + query.getIsOpen().toString();
        searchterm = (searchterm == null) ? isopen : searchterm + " "
            + isopen;
      }

      if (query.getCoverageFrom() != null)
      {
        String from = "+temporal_coverage_from:["
            + dateFormat.format(query.getCoverageFrom()) + " TO *]";
        searchterm = (searchterm == null) ? from : searchterm + " "
            + from;
      }

      if (query.getCoverageTo() != null)
      {
        String to = "+temporal_coverage_to:[* TO "
            + dateFormat.format(query.getCoverageTo()) + "]";
        searchterm = (searchterm == null) ? to : searchterm + " " + to;
      }

      break;
    case FILTERED:
    default:
      StringBuilder fq = new StringBuilder("");

      for (MetadataEnumType type : query.getTypes())
      {
        if (type != MetadataEnumType.UNKNOWN)
        {
          fq.append(" type: ");
          fq.append(type.toField());
        }
      }

      for (String category : query.getCategories())
      {
        fq.append(" +groups:");
        fq.append(category);
      }

      for (String tag : query.getTags())
      {
        fq.append(" +tags:\"");
        fq.append(tag);
        fq.append("\"");
      }

      for (String licence : query.getLicences())
      {
        fq.append(" +license_id:");
        fq.append(licence);
      }

      for (String format : query.getFormats())
      {
        fq.append(" +res_format:");
        fq.append(format);
      }

      if (query.getIsOpen() != null)
      {
        fq.append(" +isopen:");
        fq.append(query.getIsOpen().toString());
      }

      if (query.getCoverageFrom() != null)
      {
        fq.append(" +temporal_coverage_from:[");
        fq.append(dateFormat.format(query.getCoverageFrom()));
        fq.append(" TO *]");
      }

      if (query.getCoverageTo() != null)
      {
        fq.append(" +temporal_coverage_to:[* TO ");
        fq.append(dateFormat.format(query.getCoverageTo()));
        fq.append("]");
      }

      fq.append(" +state:active");

      if (!fq.toString().isEmpty())
      {
        node.put("fq", fq.toString());
      }
    }

    if (searchterm != null)
    {
      node.put("q", searchterm);
    }

    StringBuilder sorts = new StringBuilder("");
    for (String sort : query.getSortFields())
    {
      if (!sorts.toString().isEmpty())
      {
        sorts.append(", ");
      }
      sorts.append(sort);
    }
    if (!sorts.toString().isEmpty())
    {
      sorts.append(", score desc");
      node.put("sort", sorts.toString());
    }
    else
    {

      if (defaultSortStr == null)
      {
        defaultSortStr = "score desc";
      }
      sorts.append(defaultSortStr);
      sorts.append(", score desc");
      node.put("sort", sorts.toString());
    }

    int offset = (query.getMax() * query.getPageoffset())
        + query.getOffset();
    node.put("start", offset);
    node.put("rows", query.getMax());

    ArrayNode facets = OM.createArrayNode();
    facets.add("groups");
    facets.add("tags");
    facets.add("type");
    facets.add("res_format");
    facets.add("license_id");
    facets.add("isopen");

    node.set("facet.field", facets);

    return node;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.seitenbau.govdata.odp.registry.ODRClient#createMetadata(de.seitenbau.govdata.odp.registry
   * .model.MetadataEnumType)
   */
  @Override
  public Metadata createMetadata()
  {
    MetadataImpl impl = new MetadataImpl(new MetadataBean(), this);
    return impl;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.seitenbau.govdata.odp.registry.ODRClient#rateMetadata(de.seitenbau.govdata.odp.registry
   * .model.User, java.lang.String, int)
   */
  @Override
  public void rateMetadata(User user, String metadata, int rate) throws OpenDataRegistryException
  {
    String userApikey = getApiKeyFromUser(user);
    if (userApikey != null)
    {
      ObjectNode body = OM.createObjectNode();
      body.put("package", metadata);
      body.put("rating", rate);
      LOG.trace("REST > calling action api 'rating_create' with: {}", body);
      long start = System.currentTimeMillis();
      JsonNode node = action.createMetadataRating(
          userApikey, body);
      LOG.debug("/api/3/action/rating_create: {}ms",
          System.currentTimeMillis() - start);
      LOG.trace("REST < returns: {}", node);
    }
    else
    {
      final String msg = "Rating is not possible, because no apikey for user available!";
      LOG.warn("rateMetadata: " + msg);
      throw new OpenDataRegistryException(msg);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.seitenbau.govdata.odp.registry.ODRClient#showRoles(de.seitenbau.govdata.odp.registry .model.User,
   * java.lang.String)
   */
  @Override
  public List<String> showRoles(User user, String object)
  {
    ObjectNode body = OM.createObjectNode();
    body.put("domain_object", object);
    body.put("user", user.getName());

    List<String> roles = new ArrayList<>();

    LOG.trace("REST > calling action api 'roles_show' with: {}", body);
    long start = System.currentTimeMillis();
    JsonNode node = action.showRoles(body);
    LOG.debug("/api/3/action/roles_show: {}ms", System.currentTimeMillis()
        - start);
    LOG.trace("REST < returns: {}", node);

    if (isSuccess(node))
    {
      JsonNode result = node.get("result");
      JsonNode rolesNode = result.get("roles");
      for (JsonNode entry : rolesNode)
      {
        roles.add(entry.get("role").textValue());
      }
    }
    return roles;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.seitenbau.govdata.odp.registry.ODRClient#listUsers()
   */
  @Override
  public List<String> listUsers()
  {
    ObjectNode body = OM.createObjectNode();
    List<String> users = new ArrayList<>();

    LOG.trace("REST > calling action api 'user_list' with: {}", body);
    long start = System.currentTimeMillis();
    JsonNode node = action.listUsers(body);
    LOG.debug("/api/3/action/user_list: {}ms", System.currentTimeMillis()
        - start);
    LOG.trace("REST < returns: {}", node);

    if (isSuccess(node))
    {
      JsonNode result = node.get("result");
      for (JsonNode entry : result)
      {
        users.add(entry.get("name").textValue());
      }
    }
    return users;
  }

  @Override
  public void updateRoles(User user, String object, List<String> roles)
  {
    ObjectNode body = OM.createObjectNode();
    body.put("domain_object", object);
    body.put("user", user.getName());
    ArrayNode rolesList = OM.createArrayNode();
    for (String role : roles)
    {
      rolesList.add(role);
    }
    body.set("roles", rolesList);

    LOG.trace("REST > calling action api 'user_role_update' with: {}", body);
    long start = System.currentTimeMillis();
    JsonNode node = action.updateRoles(authorizationKey, body);
    LOG.trace("/api/3/action/user_role_update: {}ms",
        System.currentTimeMillis() - start);
    LOG.trace("REST < returns: {}", node);
  }

  private boolean isSuccess(JsonNode result)
  {
    JsonNode success = result.get("success");
    return (success != null && success.isBoolean()) && success.booleanValue();
  }

  private JsonNode getSearchResult(JsonNode node)
  {
    if (isSuccess(node))
    {
      JsonNode result = node.get("result");
      if (result != null && getCount(node) > 0)
      {
        JsonNode results = result.get("results");
        if (results.isArray())
        {
          return results;
        }
      }
    }
    return OM.createArrayNode();
  }

  private int getCount(JsonNode node)
  {
    JsonNode result = node.get("result");
    if (result != null)
    {
      return result.get("count").intValue();
    }
    return 0;
  }

  private JsonNode getResultList(JsonNode node)
  {
    if (isSuccess(node))
    {
      JsonNode result = node.get("result");
      if (result != null && result.isArray())
      {
        return result;
      }
    }
    return OM.createArrayNode();
  }

  private JsonNode getResultObject(JsonNode node)
  {
    if (isSuccess(node))
    {
      JsonNode result = node.get("result");
      if (result != null && result.isObject())
      {
        return result;
      }
    }
    return OM.createObjectNode();
  }

  private JsonNode getAutoCompleteResultList(JsonNode result)
  {
    JsonNode resultSet = result.get("ResultSet");
    if (resultSet != null)
    {
      JsonNode list = resultSet.get("Result");
      if (list != null && list.isArray())
      {
        return list;
      }
    }
    return OM.createArrayNode();
  }

  @Override
  public void persistMetadata(User user, Metadata metadata)
      throws OpenDataRegistryException
  {
    MetadataImpl impl = (MetadataImpl) metadata;

    String auth = getApiKeyFromUser(user);

    if (impl.isNew())
    {
      String munge = mungeTitleToName(impl.getTitle());

      impl.setName(munge);
      JsonNode node = impl.write(false);
      LOG.trace("REST > calling action api 'package_create' with: {}",
          node);
      long start = System.currentTimeMillis();
      JsonNode result = action.createMetadata(auth, node);
      LOG.trace("/api/3/action/package_create: {}ms",
          System.currentTimeMillis() - start);
      LOG.trace("REST < returns: {}", result);
    }
    else
    {
      JsonNode node;
      if (impl.resourcesModified())
      {
        node = impl.write(true);
        LOG.debug(
            "REST > calling action api 'package_update:clearResources:' with: {}",
            node);
        JsonNode result = action.updateMetadata(auth, node);
        LOG.debug("REST call for empty resources < returns: {}", result);
      }
      node = impl.write(false);
      ((ObjectNode) node).remove("metadata_created");
      ((ObjectNode) node).remove("metadata_modified");
      ((ObjectNode) node).remove("isopen");

      ((ObjectNode) node).remove("license_title");
      ((ObjectNode) node).remove("license_url");

      LOG.debug("REST > calling action api 'package_update' with: {}",
          node);
      long start = System.currentTimeMillis();
      JsonNode result = action.updateMetadata(auth, node);
      LOG.trace("/api/3/action/package_update: {}ms",
          System.currentTimeMillis() - start);
      LOG.trace("REST < returns: {}", result);
    }
  }

  @Override
  public Metadata getMetadata(User user, String name)
      throws OpenDataRegistryException
  {
    final String method = "getMetadata() : ";
    LOG.trace(method + "Start");

    ObjectNode body = OM.createObjectNode();
    body.put("id", name);
    String key = getApiKeyFromUser(user);

    LOG.trace("REST > calling action api 'package_show' with: {}", body);
    long start = System.currentTimeMillis();
    JsonNode node;
    try
    {
      node = action.showMetadata(key, body);
    }
    catch (Exception e)
    {
      LOG.error(method + e.getMessage());
      return null;
    }
    if (node == null)
    {
      LOG.info("METADATA not found.");
      return null;

    }
    else
    {
      LOG.debug("/api/3/action/package_show: {}ms",
          System.currentTimeMillis() - start);
      LOG.trace("REST < returns: {}", node);

      JsonNode result = node.get("result");

      LOG.trace(method + "End");
      return generateImpl(result);
    }
  }

  private MetadataImpl generateImpl(JsonNode node)
      throws OpenDataRegistryException
  {

    MetadataImpl impl = null;
    try
    {
      MetadataBean metadata = convert(node, MetadataBean.class);

      switch (MetadataEnumType.fromField(metadata.getType()))
      {
      case DATASET:
      case UNKNOWN:
      default:
        impl = new MetadataImpl(metadata, this);
        break;
      }
    }
    catch (Exception e)
    {
      LOG.error("converting json metadata to bean", e);
      throw new OpenDataRegistryException(e.getMessage());
    }

    return impl;
  }

  private String strip(String value)
  {
    if (value.startsWith("\""))
    {
      value = value.substring(1);
    }
    if (value.endsWith("\""))
    {
      value = value.substring(0, value.length() - 1);
    }
    return value;
  }

  @Override
  public String mungeTitleToName(String title)
  {
    LOG.trace("REST > calling util api 'munge_title_to_name' with: {}",
        title);
    long start = System.currentTimeMillis();
    String result = strip(util.mungeTitleToName(title));
    LOG.debug("/api/2/util/dataset/munge_title_to_name: {}ms",
        System.currentTimeMillis() - start);
    LOG.trace("REST < returns: {}", result);
    return result;
  }

  @Override
  public void loadRating(Metadata metadata)
  {
    LOG.trace("REST > calling model dataset api with: {}",
        metadata.getName());
    long start = System.currentTimeMillis();
    JsonNode node = model.getDataset(authorizationKey, metadata.getName());
    LOG.debug("/api/2/rest/dataset/{}: {}ms", metadata.getName(),
        System.currentTimeMillis() - start);
    LOG.trace("REST < returns: {}", node);

    JsonNode count = node.get("ratings_count");
    if (count != null && count.isNumber())
    {
      ((MetadataImpl) metadata).setRatingCount(count.numberValue().intValue());
    }
    JsonNode average = node.get("ratings_average");
    if (average != null && average.isDouble())
    {
      ((MetadataImpl) metadata).setAverageRating(average.numberValue().floatValue());
    }
  }

  public List<RelationshipBean> listRelationships(String name, String type)
  {
    ObjectNode body = OM.createObjectNode();
    body.put("id", name);
    if (type != null && !type.isEmpty())
    {
      body.put("rel", type);
    }

    List<RelationshipBean> relationships = new ArrayList<>();

    Response response = null;
    try
    {
      LOG.trace(
          "REST > calling action api 'package_relationships_list' with: {}",
          body);
      long start = System.currentTimeMillis();
      response = action.listRelationships(authorizationKey, body);
      LOG.debug("/api/3/action/package_relationships_list: {}ms",
          System.currentTimeMillis() - start);
      if (response.getStatusInfo() == Status.OK)
      {
        JsonNode node = response.readEntity(JsonNode.class);
        LOG.trace("REST < returns: {}", node);

        if (isSuccess(node))
        {
          JsonNode result = getResultList(node);
          for (JsonNode relationship : result)
          {
            RelationshipBean bean = convert(relationship,
                RelationshipBean.class);
            relationships.add(bean);
          }
        }
      }
      else
      {
        LOG.trace("REST < (http) returns: {} - {}", response
            .getStatusInfo().toString(), response
                .getStatusInfo().getReasonPhrase());
      }
    }
    finally
    {
      if (response != null)
      {
        response.close();
      }
    }

    return relationships;
  }

  /**
   * Gibt den Status zurück.
   * 
   * @return the status
   */
  public StatusBean getStatus()
  {
    return status;
  }

  private String getApiKeyFromUser(User user)
  {
    String result = null;
    if (user != null)
    {
      result = ((UserImpl) user).getApikey();
    }
    return result;
  }

  @Override
  public List<Organization> getOrganizationsForUser(User user, String permission)
  {
    List<Organization> organizations = new ArrayList<>();

    String apikey = ((UserImpl) user).getApikey();
    Response response = action.getOrganisationsForUser(apikey, permission);
    if (response.getStatusInfo() == Status.OK)
    {
      JsonNode node = response.readEntity(JsonNode.class);
      LOG.trace("REST < returns: {}", node);

      if (isSuccess(node))
      {
        JsonNode result = getResultList(node);
        for (JsonNode organization : result)
        {
          OrganizationBean bean = convert(organization, OrganizationBean.class);
          organizations.add(new OrganizationImpl(bean));
        }
      }
    }
    else
    {
      LOG.trace("REST < (http) returns: {} - {}",
          response.getStatusInfo().toString(),
          response.getStatusInfo().getReasonPhrase());
    }

    return organizations;
  }
  
  @Override
  public List<Organization> getOrganizations()
  {
    List<Organization> organizations = new ArrayList<>();

    Response response = action.getOrganisations(true); // we need all fields -> true
    if (response.getStatusInfo() == Status.OK)
    {
      JsonNode node = response.readEntity(JsonNode.class);
      LOG.trace("REST < returns: {}", node);

      if (isSuccess(node))
      {
        JsonNode result = getResultList(node);
        for (JsonNode organization : result)
        {
          OrganizationBean bean = convert(organization, OrganizationBean.class);
          organizations.add(new OrganizationImpl(bean));
        }
      }
    }
    else
    {
      LOG.trace("REST < (http) returns: {} - {}",
          response.getStatusInfo().toString(),
          response.getStatusInfo().getReasonPhrase());
    }

    return organizations;
  }

  @Override
  public boolean deleteMetadata(User user, String metadataName)
  {
    ObjectNode deleteParam = OM.createObjectNode();
    deleteParam.set("id", new TextNode(metadataName));
    
    try
    {
      String apikey = ((UserImpl) user).getApikey();
      Response response = action.deleteMetadata(apikey, deleteParam);
      if (response.getStatusInfo() == Status.OK)
      {
        JsonNode node = response.readEntity(JsonNode.class);
        if (isSuccess(node))
        {
          return true;
        }
        else
        {
          LOG.error("Delete failed, non-OK response from CKAN. Metadata: " + metadataName);
        }
      }
      return false;
    }
    catch (Exception e)
    {
      LOG.error("Could not delete metadata: " + metadataName);
      e.printStackTrace();
      return false;
    }
  }

  @Override
  public boolean deleteUser(User user)
  {
    ObjectNode deleteParam = OM.createObjectNode();
    deleteParam.set("id", new TextNode(user.getId()));
    
    try
    {
      Response response = action.deleteUser(authorizationKey, deleteParam);
      if (response.getStatusInfo() == Status.OK)
      {
        JsonNode node = response.readEntity(JsonNode.class);
        if (isSuccess(node))
        {
          return true;
        }
        else
        {
          LOG.error("Delete failed, non-OK response from CKAN. User: " + user.getName());
        }
      }
      return false;
    }
    catch (Exception e)
    {
      LOG.error("Could not delete User: " + user.getName());
      e.printStackTrace();
      return false;
    }
  }

  @Override
  public User renameUser(User user, String newName)
  {
    ObjectNode updateParam = OM.createObjectNode();
    updateParam.set("id", new TextNode(user.getId()));
    updateParam.set("name", new TextNode(newName));
    
    String email = user.getEmail(); // replace email with placeholder, if the existing email is empty.
    updateParam.set("email", new TextNode(StringUtils.isEmpty(email) ? "email@example.com" : email));

    Response response = null;
    try
    {
      response = action.updateUser(authorizationKey, updateParam);
      if (response.getStatus() == Response.Status.OK.getStatusCode())
      {
        JsonNode result = response.readEntity(JsonNode.class);
        JsonNode userNode = result.get("result");
        return new UserImpl(convert(userNode, UserBean.class));
      }
      else
      {
        LOG.error("could not rename user " + user.getName() + ": " + response.getStatus());
        return null;
      }
    }
    finally
    {
      if (response != null)
      {
        response.close();
      }
    }
  }

}
