package de.seitenbau.govdata.odp.registry.common;

import java.io.IOException;
import java.util.Date;

import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import de.seitenbau.govdata.odp.registry.ckan.api.CKANClientAction;
import de.seitenbau.govdata.odp.registry.ckan.api.CKANClientModel;
import de.seitenbau.govdata.odp.registry.ckan.api.CKANClientUtil;

/**
 * Bündelt alle Aktionen über die CKAN-API und macht diese verfügbar.
 * 
 * @author rnoerenberg
 *
 */
@Path("/")
public class CkanResource implements CKANClientAction, CKANClientUtil, CKANClientModel
{
  private static final Logger logger = LoggerFactory.getLogger(CkanResource.class);

  private ObjectMapper mapper;

  // Dummy ObjectNode to be returned when no special data is needed.
  private ObjectNode node;

  /**
   * Default-Konstruktor.
   */
  public CkanResource()
  {
    this.mapper = new ObjectMapper();
    this.node = this.mapper.createObjectNode();
    this.node.put("success", "true");
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
    return getJsonNode("license_list_response.json");
  }

  @Override
  public JsonNode showMetadata(String authenticationKey, JsonNode body)
  {
    return getJsonNode("package_show_response.json");
  }

  @Override
  public JsonNode showDcatDataset(String authenticationKey, JsonNode body)
  {
    return getJsonNode("dcat_dataset_show_response.json");
  }

  @Override
  public JsonNode createMetadataRating(String authenticationKey, JsonNode body)
  {
    return getJsonNode("rating_create_response.json");
  }

  @Override
  public Response metadataSearch(JsonNode body)
  {
    JsonNode jsonNode = getJsonNode("package_search_response.json");
    return Response.ok(jsonNode).build();
  }

  @Override
  public Response listGroups(JsonNode body)
  {
    JsonNode jsonNode = getJsonNode("group_list_response.json");
    return Response.ok(jsonNode).build();
  }

  @Override
  public Response showUser(String authenticationKey, JsonNode body)
  {
    JsonNode jsonNode = getJsonNode("user_show_response_admin.json");
    return Response.ok(jsonNode).build();
  }

  @Override
  public JsonNode getDataset(String authenticationKey, String dataset)
  {
    return getJsonNode("dataset_response.json");
  }

  @Override
  public Response listRelationships(String authenticationKey, JsonNode body)
  {
    return Response.ok(this.node).build();
  }

  @Override
  public Response createUser(String authenticationKey, JsonNode body)
  {
    return Response.ok(this.node).build();
  }

  @Override
  public String mungeTitleToName(String title)
  {
    return title;
  }

  @Override
  public JsonNode metadataAutocomplete(JsonNode body)
  {
    return this.node;
  }

  @Override
  public JsonNode getMetadataRating(String authenticationKey, JsonNode body)
  {
    return this.node;
  }

  @Override
  public JsonNode showTag(JsonNode body)
  {
    return this.node;
  }

  @Override
  public JsonNode createMetadata(String authenticationKey, JsonNode body)
  {
    return this.node;
  }

  @Override
  public JsonNode updateMetadata(String authenticationKey, JsonNode body)
  {
    return this.node;
  }

  @Override
  public JsonNode status(JsonNode body)
  {
    return this.node;
  }

  @Override
  public JsonNode listUsers(JsonNode body)
  {
    return this.node;
  }

  @Override
  public JsonNode showGroup(JsonNode body)
  {
    return this.node;
  }

  @Override
  public JsonNode listTags(JsonNode body)
  {
    return this.node;
  }

  @Override
  public JsonNode showRoles(JsonNode body)
  {
    return this.node;
  }

  @Override
  public JsonNode updateRoles(String authenticationKey, JsonNode body)
  {
    return this.node;
  }

  @Override
  public JsonNode getMetadataAutoComplete(String query)
  {
    return this.node;
  }

  @Override
  public JsonNode getTagsAutoComplete(String query)
  {
    return this.node;
  }

  @Override
  public JsonNode getFormatsAutoComplete(String query)
  {
    return this.node;
  }

  @Override
  public JsonNode getGenericModel(String path)
  {
    return this.node;
  }

  @Override
  public JsonNode getRevisions()
  {
    return this.node;
  }

  @Override
  public JsonNode getRevisions(Date since)
  {
    return this.node;
  }

  @Override
  public JsonNode getRevision(String revision)
  {
    return this.node;
  }

  @Override
  public JsonNode getGroups()
  {
    return this.node;
  }

  @Override
  public JsonNode getGroup(String group)
  {
    return this.node;
  }

  @Override
  public JsonNode getDatasets()
  {
    return this.node;
  }

  @Override
  public JsonNode getTags()
  {
    return this.node;
  }

  @Override
  public JsonNode getTag(String name)
  {
    return this.node;
  }

  @Override
  public JsonNode getRating(String name)
  {
    return this.node;
  }

  @Override
  public JsonNode getRatings()
  {
    return this.node;
  }

  @Override
  public Response getOrganisationsForUser(String authenticationKey, String permission)
  {
    return Response.ok(this.node).build();
  }

  @Override
  public Response deleteMetadata(String authenticationKey, JsonNode body)
  {
    return Response.ok(this.node).build();
  }

  @Override
  public Response updateUser(String authenticationKey, JsonNode body)
  {
    return Response.ok(this.node).build();
  }

  @Override
  public Response deleteUser(String authenticationKey, JsonNode body)
  {
    return Response.ok(this.node).build();
  }

  @Override
  public Response getOrganisations(boolean allfields)
  {
    return Response.ok(this.node).build();
  }

}
