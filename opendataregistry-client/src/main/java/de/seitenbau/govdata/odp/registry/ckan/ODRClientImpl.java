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

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.config.SocketConfig;
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

import de.seitenbau.govdata.clean.StringCleaner;
import de.seitenbau.govdata.odp.common.cache.BaseCache;
import de.seitenbau.govdata.odp.common.http.client.config.OdpRequestConfig;
import de.seitenbau.govdata.odp.common.http.impl.client.OdpConnectionKeepAliveStrategy;
import de.seitenbau.govdata.odp.registry.ODRClient;
import de.seitenbau.govdata.odp.registry.ckan.api.CKANClientAction;
import de.seitenbau.govdata.odp.registry.ckan.cache.TokenCache;
import de.seitenbau.govdata.odp.registry.ckan.impl.CategoryImpl;
import de.seitenbau.govdata.odp.registry.ckan.impl.LicenceImpl;
import de.seitenbau.govdata.odp.registry.ckan.impl.MetadataImpl;
import de.seitenbau.govdata.odp.registry.ckan.impl.OrganizationImpl;
import de.seitenbau.govdata.odp.registry.ckan.impl.TagImpl;
import de.seitenbau.govdata.odp.registry.ckan.impl.UserImpl;
import de.seitenbau.govdata.odp.registry.ckan.json.ApiTokenBean;
import de.seitenbau.govdata.odp.registry.ckan.json.GroupBean;
import de.seitenbau.govdata.odp.registry.ckan.json.LicenceBean;
import de.seitenbau.govdata.odp.registry.ckan.json.MetadataBean;
import de.seitenbau.govdata.odp.registry.ckan.json.OrganizationBean;
import de.seitenbau.govdata.odp.registry.ckan.json.ResourceBean;
import de.seitenbau.govdata.odp.registry.ckan.json.StatusBean;
import de.seitenbau.govdata.odp.registry.ckan.json.TagBean;
import de.seitenbau.govdata.odp.registry.ckan.json.UserBean;
import de.seitenbau.govdata.odp.registry.model.Category;
import de.seitenbau.govdata.odp.registry.model.FormatEnumType;
import de.seitenbau.govdata.odp.registry.model.Licence;
import de.seitenbau.govdata.odp.registry.model.Metadata;
import de.seitenbau.govdata.odp.registry.model.MetadataEnumType;
import de.seitenbau.govdata.odp.registry.model.Organization;
import de.seitenbau.govdata.odp.registry.model.Tag;
import de.seitenbau.govdata.odp.registry.model.User;
import de.seitenbau.govdata.odp.registry.model.exception.OpenDataRegistryException;

/**
 * The Class ODRClientImpl.
 * 
 * @author sim, msg
 * @author rnoerenberg
 */
public class ODRClientImpl implements ODRClient
{

  private static final Logger LOG = LoggerFactory.getLogger(OpenDataRegistryProvider.class);

  private CKANClientAction action;

  private String authorizationToken;

  private static final ObjectMapper OM = new ObjectMapper();

  private static final ObjectNode ALL_FIELDS = OM.createObjectNode();

  private static final String PORTAL_TOKEN_NAME = "portal";

  private LicenceCache licenceCache = new LicenceCache();

  private TokenCache tokenCache = new TokenCache();

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

    authorizationToken = props
        .getProperty(PROPERTY_NAME_CKAN_AUTHORIZATION_KEY);
    String url = props.getProperty(PROPERTY_NAME_CKAN_URL);
    LOG.info("Start initialize CKAN client with URL {} ...", url);
    RegisterBuiltin.register(ResteasyProviderFactory.getInstance());

    CloseableHttpClient httpClient =
        HttpClientBuilder.create().setConnectionManager(new PoolingHttpClientConnectionManager())
            .setKeepAliveStrategy(OdpConnectionKeepAliveStrategy.INSTANCE)
            .setDefaultRequestConfig(OdpRequestConfig.REQUEST_CONFIG)
            .setDefaultSocketConfig(SocketConfig.custom().setSoTimeout(20 * 1000).build())
            .setMaxConnPerRoute(20)
            .setMaxConnTotal(40)
            .build();
    ApacheHttpClient4Engine engine = new ApacheHttpClient4Engine(httpClient);
    ResteasyClient client = new ResteasyClientBuilder().httpEngine(engine).build();
    ResteasyWebTarget target = client.target(url);

    action = target.proxy(CKANClientAction.class);

    ALL_FIELDS.put(JSON_FIELD_ALL_FIELDS, true);

    getStatus();
    LOG.info("CKAN client sucessfully initialized.");
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
    return licenceCache.getLicenses();
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
    String method = "findUser() : ";
    ObjectNode userName = OM.createObjectNode();
    userName.put("id", name);

    UserImpl impl = null;

    Response response = null;
    try
    {
      LOG.trace("{}REST > calling action api 'user_show' with: {}", method, userName);
      long start = System.currentTimeMillis();
      response = action.showUser(authorizationToken, userName);
      LOG.debug("/api/3/action/user_show: {}ms",
          System.currentTimeMillis() - start);

      if (response.getStatusInfo() == Status.OK)
      {
        JsonNode node = response.readEntity(JsonNode.class);
        LOG.trace("REST < returns: {}", node);

        JsonNode userNode = node.get("result");

        UserBean userBean = convert(userNode, UserBean.class);
        impl = new UserImpl(userBean);
        if (userBean == null)
        {
          LOG.warn("{}Could not fetch user object!", method);
        }
        else
        {
          // find a token for the user
          String userToken = getOrCreateTokenForUser(impl.getId());
          LOG.debug("Use token: {}", userToken);
          if (userToken == null)
          {
            LOG.warn("{}Could not find or create a token for the user!", method);
          }
          impl.setApiToken(userToken);
        }
      }
      else if (response.getStatusInfo() == Status.NOT_FOUND)
      {
        LOG.trace("{}Could not find user named {} in CKAN.", method, name);
      }
      else
      {
        LOG.warn("{}REST < (http) returns: {} - {}", method,
            response.getStatusInfo().getFamily().name(),
            response.getStatusInfo().getReasonPhrase());
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
    String method = "createUser() : ";
    ObjectNode user = OM.createObjectNode();
    user.put("name", name);
    user.put("email", email);
    user.put("password", password);
    UserImpl impl = null;

    Response response = null;
    try
    {
      LOG.trace("{}REST > calling action api 'user_create' with: {}", method, user);
      long start = System.currentTimeMillis();
      response = action.createUser(authorizationToken, user);
      LOG.debug("/api/3/action/user_create: {}ms",
          System.currentTimeMillis() - start);
      if (response.getStatusInfo() == Status.OK)
      {
        JsonNode result = response.readEntity(JsonNode.class);
        LOG.trace("{}REST < returns: {}", method, response);

        JsonNode userNode = result.get("result");
        impl = new UserImpl(convert(userNode, UserBean.class));
        impl.setApiToken(getOrCreateTokenForUser(impl.getId()));
      }
      else
      {
        LOG.warn("{}REST < (http) returns: {} - {}", method,
            response.getStatusInfo().getFamily().name(),
            response.getStatusInfo().getReasonPhrase());
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
   * @see de.seitenbau.govdata.odp.registry.ODRClient#createApiTokenForUser(java.lang.String,
   * java.lang.String)
   */
  @Override
  public String createApiTokenForUser(String userName, String tokenName)
  {
    ObjectNode params = OM.createObjectNode();
    params.put("name", tokenName);
    params.put("user", userName);

    String token = null;
    Response response = null;
    try
    {
      LOG.trace("REST > calling action api 'api_token_create' with: {}", params);
      long start = System.currentTimeMillis();
      response = action.createApiToken(authorizationToken, params);
      LOG.debug("/api/3/action/api_token_create: {}ms", System.currentTimeMillis() - start);
      if (response.getStatusInfo() == Status.OK)
      {
        JsonNode node = response.readEntity(JsonNode.class);
        LOG.trace("REST < returns: {}", node);

        JsonNode tokenNode = node.get("result");
        token = tokenNode.get("token").asText();
      }
    }
    catch (Exception e)
    {
      LOG.warn("Error while creating a token: {}", e.getMessage());
    }
    finally
    {
      if (response != null)
      {
        response.close();
      }
    }
    return token;
  }

  /*
   * (non-Javadoc)
   *
   * @see de.seitenbau.govdata.odp.registry.ODRClient#revokeApiTokenById(java.lang.String)
   */
  @Override
  public void revokeApiTokenById(String tokenId)
  {
    ObjectNode params = OM.createObjectNode();
    params.put("jti", tokenId);

    Response response = null;
    try
    {
      LOG.trace("REST > calling action api 'api_token_revoke' with: {}", params);
      long start = System.currentTimeMillis();
      response = action.revokeApiToken(authorizationToken, params);
      LOG.debug("/api/3/action/api_token_revoke: {}ms", System.currentTimeMillis() - start);
      if (response.getStatusInfo() != Response.Status.OK)
      {
        LOG.warn("Revoking token {} failed!", tokenId);
      }
    }
    finally
    {
      if (response != null)
      {
        response.close();
      }
    }
    return;
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
    String auth = getApiTokenFromUser(user);
    if (auth != null)
    {
      ObjectNode body = OM.createObjectNode();
      body.put("package", metadata);
      body.put("rating", rate);
      LOG.trace("REST > calling action api 'rating_create' with: {}", body);
      long start = System.currentTimeMillis();
      JsonNode node = action.createMetadataRating(
          auth, body);
      LOG.debug("/api/3/action/rating_create: {}ms",
          System.currentTimeMillis() - start);
      LOG.trace("REST < returns: {}", node);
    }
    else
    {
      final String msg = "Rating is not possible, because no api-token for user available!";
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
    JsonNode node = action.updateRoles(authorizationToken, body);
    LOG.trace("/api/3/action/user_role_update: {}ms",
        System.currentTimeMillis() - start);
    LOG.trace("REST < returns: {}", node);
  }

  private boolean isSuccess(JsonNode result)
  {
    JsonNode success = result.get("success");
    return (success != null && success.isBoolean()) && success.booleanValue();
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

  @Override
  public boolean persistMetadata(User user, Metadata metadata)
      throws OpenDataRegistryException
  {
    MetadataImpl impl = (MetadataImpl) metadata;

    String auth = getApiTokenFromUser(user);
    
    JsonNode node;
    JsonNode result;

    if (impl.isNew())
    {
      String munge = StringCleaner.mungeTitleToName(impl.getTitle());

      impl.setName(munge);
      node = impl.write(false);
      LOG.trace("REST > calling action api 'package_create' with: {}",
          node);
      long start = System.currentTimeMillis();
      result = action.createMetadata(auth, node);
      LOG.trace("/api/3/action/package_create: {}ms",
          System.currentTimeMillis() - start);
      LOG.trace("REST < returns: {}", result);
    }
    else
    {
      if (impl.resourcesModified())
      {
        node = impl.write(true);
        LOG.debug(
            "REST > calling action api 'package_update:clearResources:' with: {}",
            node);
        result = action.updateMetadata(auth, node);
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
      result = action.updateMetadata(auth, node);
      LOG.trace("/api/3/action/package_update: {}ms",
          System.currentTimeMillis() - start);
      LOG.trace("REST < returns: {}", result);
    }
    return isSuccess(result);
  }

  @Override
  public Metadata getMetadata(User user, String name)
      throws OpenDataRegistryException
  {
    final String method = "getMetadata() : ";
    LOG.trace(method + "Start");

    ObjectNode body = OM.createObjectNode();
    body.put("id", name);
    String auth = getApiTokenFromUser(user);

    LOG.trace("REST > calling action api 'package_show' with: {}", body);
    long start = System.currentTimeMillis();
    JsonNode node;
    try
    {
      node = action.showMetadata(auth, body);
    }
    catch (ClientErrorException e)
    {
      LOG.info(method + e.getMessage());
      return null;
    }
    catch (Exception e)
    {
      LOG.error(method + "Unexpected error. Details: " + e.getMessage());
      return null;
    }
    if (node == null)
    {
      LOG.info(method + "Node is null. METADATA not found.");
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

  @Override
  public JsonNode getDcatDataset(User user, String identifier, FormatEnumType format, String[] profileList)
  {
    final String method = "getDcatDataset() : ";
    LOG.trace(method + "Start");

    ObjectNode body = OM.createObjectNode();
    body.put("id", identifier);
    ArrayNode profiles = OM.createArrayNode();
    for (String pro : profileList)
    {
      profiles.add(pro);
    }
    body.set("profiles", profiles);
    body.put("format", format.getKey());
    String auth = getApiTokenFromUser(user);

    LOG.trace("REST > calling action api 'dcat_dataset_show' with: {}", body);
    long start = System.currentTimeMillis();
    JsonNode node;
    try
    {
      node = action.showDcatDataset(auth, body);

    }
    catch (ClientErrorException e)
    {
      LOG.info(method + e.getMessage());
      return null;
    }
    catch (Exception e)
    {
      LOG.error(method + "Unexpected error. Details: " + e.getMessage());
      return null;
    }
    LOG.debug("/api/3/action/dcat_dataset_show: {}ms",
        System.currentTimeMillis() - start);
    LOG.trace(method + "End");
    return node;
  }

  /**
   * Parses the given input as JSON-LD schema.org graph and replaces the dataset and catalog URL with
   * the given ones.
   *
   * @param input JSON-LD schema.org dataset as string
   * @param datasetUrl Desired schema:url value for the dataset
   * @param catalogUrl Desired schema:url value for the catalog
   * @return The modified dataset as String, or the unmodified input if processing fails
   */
  private String replaceJsonLdUrls(String input, String datasetUrl, String catalogUrl)
  {
    try
    {
      JsonNode parsedResult = OM.readTree(input);
      JsonNode schemaGraph = parsedResult.path("@graph");

      JsonNode datasetNode = null;
      JsonNode catalogNode = null;
      // find Dataset item
      for (JsonNode item : schemaGraph)
      {
        if (isJsonNodeOfType(item, "schema:Dataset"))
        {
          datasetNode = item;
        }
        else if (isJsonNodeOfType(item, "schema:DataCatalog"))
        {
          catalogNode = item;
        }
      }
      // replace dataset URL
      if (datasetNode != null)
      {
        ((ObjectNode) datasetNode).put("schema:url", datasetUrl);
      }
      // replace catalog URL
      if (catalogNode != null)
      {
        ((ObjectNode) catalogNode).put("schema:url", catalogUrl);
      }
      // generate String value again
      return parsedResult.toString();
    }
    catch (IOException e)
    {
      LOG.error("IO Exception on JSON-LD input, not replacing CKAN Dataset URL", e);
      return input;
    }
  }

  private boolean isJsonNodeOfType(JsonNode item, String typeValue)
  {
    return item.path("@type").isTextual() && item.path("@type").textValue().equals(typeValue);
  }

  @Override
  public String getJsonLdMetadata(User user, String name, String datasetSchemaUrl, String catalogSchemaUrl)
  {
    final String method = "getJsonLdMetadata() : ";
    LOG.trace(method + "Start");
    String[] profiles = {"schemaorg"};
    JsonNode node = getDcatDataset(user, name, FormatEnumType.JSONLD, profiles);

    if (node == null)
    {
      LOG.info(method + "Node is null. METADATA not found.");
      return null;
    }
    else
    {
      LOG.trace("REST < returns: {}", node);

      JsonNode result = node.path("result");
      // Replace the schema:url of the dataset element such that dataset search redirects to the
      // frontend instead of CKAN.
      // Do the same for the catalog, which should point to the dataset results page.
      // result contains a string representation, which is to be parsed during replacement.
      String output = replaceJsonLdUrls(result.asText(), datasetSchemaUrl, catalogSchemaUrl);
      LOG.trace(method + "End");
      return output;
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
        // Map
        List<ResourceBean> resources = new ArrayList<>(metadata.getResources());
        List<ResourceBean> mapped = resources
                              .stream()
                              .map(res -> mapAvailability(res))
                              .collect(Collectors.toList());

        metadata.setResources(mapped);
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

  private ResourceBean mapAvailability(ResourceBean resource)
  {
    if (resource.getAvailability() == null && resource.getPlannedAvailability() != null)
    {
      try
      {
        DcatApAvailability candidate = DcatApAvailability.getFromUriDcatAp(resource.getPlannedAvailability());
        resource.setAvailability(candidate.getUri());
      }
      catch (IllegalArgumentException ex)
      {
        // do nothing
      }

      if (resource.getAvailability() != null)
      {
        resource.setPlannedAvailability(null);
      }
    }

    return resource;
  }

  /**
   * Gibt den Status zurück.
   * 
   * @return the status
   */
  public StatusBean getStatus()
  {
    StatusBean result = null;
    LOG.trace("REST > calling action api 'status' with nothing");
    long start = System.currentTimeMillis();
    JsonNode node = action.status(OM.createObjectNode());
    LOG.debug("/api/3/action/status_show: {}ms", System.currentTimeMillis()
        - start);
    LOG.trace("REST < returns: {}", node);
    if (isSuccess(node))
    {
      result = convert(getResultObject(node), StatusBean.class);
    }
    return result;
  }

  private String getApiTokenFromUser(User user)
  {
    String result = null;
    if (user != null)
    {
      result = ((UserImpl) user).getApiToken();
    }
    return result;
  }

  @Override
  public List<Organization> getOrganizationsForUser(User user, String permission)
  {
    List<Organization> organizations = new ArrayList<>();

    // Get all organizations with additional informations, e.g. extras, and use these
    List<Organization> allOrganizations = getOrganizations();

    String auth = getApiTokenFromUser(user);

    Response response = null;
    try
    {
      response = action.getOrganisationsForUser(auth, permission);
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
            for (Organization org : allOrganizations)
            {
              if (bean.getName().equals(org.getName()))
              {
                organizations.add(org);
                break;
              }
            }

          }
        }
      }
      else
      {
        LOG.trace("REST < (http) returns: {} - {}",
            response.getStatusInfo().toString(),
            response.getStatusInfo().getReasonPhrase());
      }
    }
    finally
    {
      if (response != null)
      {
        response.close();
      }
    }

    return organizations;
  }

  @Override
  public List<Organization> getOrganizations()
  {
    List<Organization> organizations = new ArrayList<>();

    Response response = null;
    try
    {
      response = action.getOrganisations(true, true); // we need all fields and extras -> true, true
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
    }
    finally
    {
      if (response != null)
      {
        response.close();
      }
    }

    return organizations;
  }

  @Override
  public boolean deleteMetadata(User user, String metadataName)
  {
    ObjectNode deleteParam = OM.createObjectNode();
    deleteParam.set("id", new TextNode(metadataName));

    Response response = null;
    try
    {
      String auth = getApiTokenFromUser(user);
      response = action.deleteMetadata(auth, deleteParam);
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
      LOG.error("Could not delete metadata: " + metadataName, e);
      return false;
    }
    finally
    {
      if (response != null)
      {
        response.close();
      }
    }
  }

  @Override
  public boolean deleteUser(User user)
  {
    ObjectNode deleteParam = OM.createObjectNode();
    deleteParam.set("id", new TextNode(user.getId()));

    Response response = null;
    try
    {
      response = action.deleteUser(authorizationToken, deleteParam);
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
      LOG.error("Could not delete User: " + user.getName(), e);
      return false;
    }
    finally
    {
      if (response != null)
      {
        response.close();
      }
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

    UserImpl impl = null;
    Response response = null;
    try
    {
      response = action.updateUser(authorizationToken, updateParam);
      if (response.getStatusInfo() == Status.OK)
      {
        JsonNode result = response.readEntity(JsonNode.class);
        JsonNode userNode = result.get("result");
        impl = new UserImpl(convert(userNode, UserBean.class));
        impl.setApiToken(getOrCreateTokenForUser(impl.getId()));
      }
      else
      {
        LOG.error("could not rename user " + user.getName() + ": " + response.getStatus());
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

  /**
   * Read the token for a given user. If no valid token is available create a new one.
   *
   * @param userId the id of the user
   * @return the value of the token
   */
  private String getOrCreateTokenForUser(String userId)
  {
    String apiToken = tokenCache.getTokenForUser(userId);
    if (apiToken != null)
    {
      // check if the token is still valid for CKAN requests
      if (!checkIfTokenIsValid(apiToken, userId))
      {
        apiToken = null;
      }
    }
    if (apiToken == null)
    {
      // clean up expired tokens
      List<ApiTokenBean> ckanApiTokenList = getTokenListForUser(userId);
      for (ApiTokenBean ckanToken : ckanApiTokenList)
      {
        if (ckanToken.getName().equals(PORTAL_TOKEN_NAME))
        {
          revokeApiTokenById(ckanToken.getId());
        }
      }
      // create new token
      apiToken = createApiTokenForUser(userId, PORTAL_TOKEN_NAME);
      if (apiToken == null)
      {
        LOG.warn("Could not generate a token for user {}", userId);
      }
      else
      {
        // update token in Cache
        LOG.info("Created a new token for user {}", userId);
        tokenCache.updateCache(apiToken, userId);
      }
    }
    return apiToken;
  }

  /**
   * Check if a given token is still valid for CKAN-API requests Make a request and check if the
   * token was accepted.
   * 
   * @param token the token to check
   * @param userId the id of the user
   * @return true if the token is valid, else false.
   */
  private boolean checkIfTokenIsValid(String token, String userId)
  {
    ObjectNode params = OM.createObjectNode();
    params.put("user_id", userId);

    Response response = null;
    boolean tokenValid = false;
    try
    {
      LOG.trace("REST > calling action api 'api_token_list' with params {}", params);
      long start = System.currentTimeMillis();
      response = action.getTokenList(token, userId);
      LOG.debug("/api/3/action/api_token_list: {}ms", System.currentTimeMillis() - start);
      if (response.getStatusInfo() == Status.OK)
      {
        tokenValid = true;
      }
    }
    finally
    {
      if (response != null)
      {
        response.close();
      }
    }
    return tokenValid;
  }

  /**
   * Get all available tokens from the CKAN API.
   * 
   * @param userId the id of the user
   * @return
   */
  private List<ApiTokenBean> getTokenListForUser(String userId)
  {
    ObjectNode params = OM.createObjectNode();
    params.put("user_id", userId);

    Response response = null;
    List<ApiTokenBean> apiTokens = new ArrayList<>();
    try
    {
      LOG.trace("REST > calling action api 'api_token_list' with params {}", params);
      long start = System.currentTimeMillis();
      response = action.getTokenList(authorizationToken, userId);
      LOG.debug("/api/3/action/api_token_list: {}ms", System.currentTimeMillis() - start);
      if (response.getStatusInfo() == Status.OK)
      {
        JsonNode node = response.readEntity(JsonNode.class);
        JsonNode result = getResultList(node);
        for (JsonNode token : result)
        {
          ApiTokenBean bean = convert(token, ApiTokenBean.class);
          apiTokens.add(bean);
        }
      }
    }
    finally
    {
      if (response != null)
      {
        response.close();
      }
    }
    return apiTokens;
  }

  private class LicenceCache extends BaseCache
  {
    private List<Licence> licences = null;

    public LicenceCache()
    {
      setMaxCacheTimeAmount(12);
    }

    public List<Licence> getLicenses()
    {
      // Use the cache if possible.
      if (licences == null || isCacheExpired())
      {
        licences = new ArrayList<>();

        LOG.trace("REST > calling action api 'licence_list' with nothing");
        long start = System.currentTimeMillis();
        JsonNode result = action.listLicences(OM.createObjectNode());
        LOG.debug("/api/3/action/licence_list: {}ms", System.currentTimeMillis() - start);
        LOG.trace("REST < returns: {}", result);

        JsonNode list = getResultList(result);
        for (JsonNode entry : list)
        {
          licences.add(new LicenceImpl(convert(entry, LicenceBean.class)));
        }
        cacheUpdated();
      }
      return licences;
    }
  }
}
