package de.seitenbau.govdata.odp.registry;

import java.util.List;
import java.util.Properties;

import com.fasterxml.jackson.databind.JsonNode;

import de.seitenbau.govdata.odp.registry.model.Category;
import de.seitenbau.govdata.odp.registry.model.FormatEnumType;
import de.seitenbau.govdata.odp.registry.model.Licence;
import de.seitenbau.govdata.odp.registry.model.Metadata;
import de.seitenbau.govdata.odp.registry.model.Organization;
import de.seitenbau.govdata.odp.registry.model.Tag;
import de.seitenbau.govdata.odp.registry.model.User;
import de.seitenbau.govdata.odp.registry.model.exception.OpenDataRegistryException;

/**
 * The Interface ODRClient.
 * 
 * @author sim
 * @author rnoerenberg
 */
public interface ODRClient
{

  /**
   * Inits the client implementation.
   * 
   * @throws OpenDataRegistryException If the implementation could not be initialized properly.
   */
  void init() throws OpenDataRegistryException;

  /**
   * Inits the client implementation with a set of properties. In case of the standard CKAN
   * implementation the following properties can be set:
   * 
   * <pre>
   *   ckan.url
   *   ckan.authorization.key
   * </pre>
   * 
   * @param props the list of properties.
   */
  void init(Properties props);

  /**
   * Status. General status as String. The format and the contained information depend on the
   * implementation. In case of the default CKAN implementation this is a JSON Object with standard
   * CKAn Server information.
   * 
   * @return the status string
   */
  String status();

  /**
   * List categories.
   * 
   * @return the list of categories
   */
  List<Category> listCategories();

  /**
   * List licenses.
   * 
   * @return the list of licenses
   */
  List<Licence> listLicenses();

  /**
   * List tags.
   * 
   * @return the list
   */
  List<Tag> listTags();

  /**
   * Gets the tag.
   * 
   * @param name the name
   * @return the tag
   */
  Tag getTag(String name);

  /**
   * Gets the metadata.
   * 
   * @param user the user
   * @param name the name
   * @return the metadata
   * @throws OpenDataRegistryException the open data registry exception
   */
  Metadata getMetadata(User user, String name) throws OpenDataRegistryException;

  /**
   * Get a single dataset
   * @param user
   * @param identifier
   * @param format
   * @param profileList
   * @return
   */
  JsonNode getDcatDataset(User user, String identifier, FormatEnumType format, String[] profileList);

  /**
   * Gets the JSON-LD schema.org representation for the metadata.
   *
   * @param user the user to authenticate with
   * @param name dataset name
   * @param datasetSchemaUrl The URL to the frontend page which is to be set in schema:url of the dataset
   *                   element
   * @param catalogSchemaUrl The URL to the frontend page which is to be set in schema:url of the catalog
   *                   element
   * @return JSON-LD representation as string
   * @throws OpenDataRegistryException
   */
  String getJsonLdMetadata(User user, String name, String datasetSchemaUrl, String catalogSchemaUrl);

  /**
   * Find user.
   * 
   * @param name the name
   * @return the user
   */
  User findUser(String name);

  /**
   * Creates the user.
   * 
   * @param name the name
   * @param email the email
   * @param password the password
   * @return the user
   */
  User createUser(String name, String email, String password);

  /**
   * Create a CKAN API token for a user.
   *
   * @param userName name of the user
   * @param tokenName name of the token
   * @return the token as String
   */
  String createApiTokenForUser(String userName, String tokenName);

  /**
   * Delete the given token from CKAN.
   * 
   * @param tokenId the tokenId of the token
   * @return
   */
  void revokeApiTokenById(String tokenId);

  /**
   * Show roles.
   * 
   * @param user the user
   * @param metadata the metadata
   * @return the list
   */
  List<String> showRoles(User user, String metadata);

  /**
   * Update roles.
   * 
   * @param user
   * @param object
   * @param roles
   */
  void updateRoles(User user, String object, List<String> roles);

  /**
   * Rate metadata.
   * 
   * @param user the user
   * @param metadata the metadata
   * @param rate the rate
   * @throws OpenDataRegistryException if the user object contains no api-token.
   */
  void rateMetadata(User user, String metadata, int rate) throws OpenDataRegistryException;

  /**
   * Creates a new metadata object.
   * 
   * @return the metadata
   */
  Metadata createMetadata();

  /**
   * Persist metadata.
   * 
   * @param user the user
   * @param metadata the metadata
   * @throws OpenDataRegistryException the open data registry exception
   */
  boolean persistMetadata(User user, Metadata metadata) throws OpenDataRegistryException;

  /**
   * List users.
   * 
   * @return
   */
  List<String> listUsers();
  
  /**
   * Lists all Organisations for the current user in which he has given permission.
   * @param permission
   * @return List of Organisations
   */
  List<Organization> getOrganizationsForUser(User user, String permission);
  
  /**
   * Lists all Organisations known to the CKAN Server.
   * @return List of Organisations
   */
  List<Organization> getOrganizations();
  
  /**
   * Deletes Metadata
   * @param user ckan-user who should be allowed to delete the dataset
   * @param metadataName name of the dataset to delete
   * @return true if success, false if not
   */
  boolean deleteMetadata(User user, String metadataName);
  
  /**
   * Deletes a user.
   * @param user
   * @return
   */
  boolean deleteUser(User user);
  
  /**
   * renames an existing user.
   * Notice: If the existing user has an empty email, a placeholder will be inserted.
   * @param user
   * @param newName
   * @return
   */
  User renameUser(User user, String newName);
}
