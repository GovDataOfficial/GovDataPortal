package de.seitenbau.govdata.odp.registry.common;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import de.seitenbau.govdata.odp.registry.ckan.api.CKANClientAction;

/**
 * Bündelt alle Aktionen über die CKAN-API und macht diese verfügbar.
 * 
 * @author rnoerenberg
 *
 */
@Path("/")
public class CkanResource implements CKANClientAction
{
  private static final Logger logger = LoggerFactory.getLogger(CkanResource.class);

  private ObjectMapper mapper;

  // Dummy ObjectNode to be returned when no special data is needed.
  private ObjectNode node;

  private static Map<String, JsonNode> apiMethodsCalled = new HashMap<>();

  /**
   * Default-Konstruktor.
   */
  public CkanResource()
  {
    this.mapper = new ObjectMapper();
    this.node = this.mapper.createObjectNode();
    this.node.put("success", true);
  }

  // Reads JSON response from file (fileName) and creates a JsonNode to be used as a response.
  private static JsonNode getJsonNode(final String fileName)
  {
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode jsonNode = null;

    try
    {
      jsonNode = objectMapper.readTree(CkanResource.class.getResourceAsStream(fileName));
    }
    catch (IOException e)
    {
      logger.error("Reading of response file failed!", e.getMessage(), e);
    }

    return jsonNode;
  }

  @Override
  public JsonNode listLicences(JsonNode body)
  {
    apiMethodsCalled.put("listLicences", body);
    return getJsonNode("license_list_response.json");
  }

  @Override
  public JsonNode showMetadata(String authenticationKey, JsonNode body)
  {
    apiMethodsCalled.put("showMetadata", body);
    return getJsonNode("package_show_response.json");
  }

  @Override
  public JsonNode showDcatDataset(String authenticationKey, JsonNode body)
  {
    JsonNode profiles = body.get("profiles");
    String profile = null;
    if (profiles.has(0))
    {
      profile = profiles.get(0).asText();
    }
    apiMethodsCalled.put("showDcatDataset", body);
    if ("schemaorg".equals(profile))
    {
      return getJsonNode("dcat_dataset_show_response_jsonld.json");
    }
    else
    {
      return getJsonNode("dcat_dataset_show_response_xml.json");
    }
  }

  @Override
  public JsonNode createMetadataRating(String authenticationKey, JsonNode body)
  {
    apiMethodsCalled.put("createMetadataRating", body);
    return getJsonNode("rating_create_response.json");
  }

  @Override
  public Response listGroups(JsonNode body)
  {
    JsonNode jsonNode = getJsonNode("group_list_response.json");
    apiMethodsCalled.put("listGroups", body);
    return Response.ok(jsonNode).build();
  }

  @Override
  public Response showUser(String authenticationKey, JsonNode body)
  {
    JsonNode jsonNode = getJsonNode("user_show_response_admin.json");
    apiMethodsCalled.put("showUser", body);
    return Response.ok(jsonNode).build();
  }

  @Override
  public Response createUser(String authenticationKey, JsonNode body)
  {
    JsonNode jsonNode = getJsonNode("user_create_response.json");
    apiMethodsCalled.put("createUser", body);
    return Response.ok(jsonNode).build();
  }

  @Override
  public JsonNode getMetadataRating(String authenticationKey, JsonNode body)
  {
    apiMethodsCalled.put("getMetadataRating", body);
    return this.node;
  }

  @Override
  public JsonNode showTag(JsonNode body)
  {
    apiMethodsCalled.put("showTag", body);
    return this.node;
  }

  @Override
  public JsonNode createMetadata(String authenticationKey, JsonNode body)
  {
    apiMethodsCalled.put("createMetadata", body);
    return this.node;
  }

  @Override
  public JsonNode updateMetadata(String authenticationKey, JsonNode body)
  {
    apiMethodsCalled.put("updateMetadata", body);
    return this.node;
  }

  @Override
  public JsonNode status(JsonNode body)
  {
    apiMethodsCalled.put("status", body);
    return this.node;
  }

  @Override
  public JsonNode listUsers(JsonNode body)
  {
    apiMethodsCalled.put("listUsers", body);
    return this.node;
  }

  @Override
  public JsonNode showGroup(JsonNode body)
  {
    apiMethodsCalled.put("showGroup", body);
    return this.node;
  }

  @Override
  public JsonNode listTags(JsonNode body)
  {
    apiMethodsCalled.put("listTags", body);
    return this.node;
  }

  @Override
  public JsonNode showRoles(JsonNode body)
  {
    apiMethodsCalled.put("showRoles", body);
    return this.node;
  }

  @Override
  public JsonNode updateRoles(String authenticationKey, JsonNode body)
  {
    apiMethodsCalled.put("updateRoles", body);
    return this.node;
  }

  @Override
  public Response getOrganisationsForUser(String authenticationKey, String permission)
  {
    apiMethodsCalled.put("getOrganisationsForUser", null);
    return Response.ok(this.node).build();
  }

  @Override
  public Response deleteMetadata(String authenticationKey, JsonNode body)
  {
    apiMethodsCalled.put("deleteMetadata", body);
    return Response.ok(this.node).build();
  }

  @Override
  public Response updateUser(String authenticationKey, JsonNode body)
  {
    JsonNode jsonNode = getJsonNode("update_user_response.json");
    apiMethodsCalled.put("updateUser", body);
    return Response.ok(jsonNode).build();
  }

  @Override
  public Response deleteUser(String authenticationKey, JsonNode body)
  {
    apiMethodsCalled.put("deleteUser", body);
    return Response.ok(this.node).build();
  }

  @Override
  public Response getOrganisations(boolean allfields, boolean includeextras)
  {
    apiMethodsCalled.put("getOrganisations", null);
    return Response.ok(this.node).build();
  }

  @Override
  public Response createApiToken(String authenticationKey, JsonNode body)
  {
    JsonNode jsonNode = getJsonNode("api_token_create_response.json");
    apiMethodsCalled.put("createApiToken", body);
    return Response.ok(jsonNode).build();
  }

  @Override
  public Response revokeApiToken(String authenticationKey, JsonNode body)
  {
    apiMethodsCalled.put("revokeApiToken", body);
    return Response.ok(this.node).build();
  }

  @Override
  public Response getTokenList(String authenticationKey, String userId)
  {
    apiMethodsCalled.put("getTokenList", null);
    return Response.ok(this.node).build();
  }

  public static Map<String, JsonNode> getApiMethodsCalled()
  {
    return apiMethodsCalled;
  }

  public static void clearApiMethodsCalled()
  {
    apiMethodsCalled.clear();
  }

}
