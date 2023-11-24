package de.seitenbau.govdata.odp.registry.ckan;

/**
 * The Class Constants.
 * 
 * @author sim, msg
 */
public final class Constants
{
  private Constants()
  {
    // private
  }

  /** The Constant PROPERTIES_FILENAME. */
  public static final String PROPERTIES_FILENAME = "ckan.properties";

  /** The Constant PROPERTY_NAME_CKAN_URL. */
  public static final String PROPERTY_NAME_CKAN_URL = "ckan.url";

  /** The Constant PROPERTY_NAME_CKAN_AUTHORIZATION_KEY. */
  public static final String PROPERTY_NAME_CKAN_AUTHORIZATION_KEY = "ckan.authorization.key";

  /** The Constant OPEN_DATA_PROVIDER_NAME. */
  public static final String OPEN_DATA_PROVIDER_NAME = "CKAN";

  /** The Constant JSON_FIELD_EXTRAS. */
  public static final String JSON_FIELD_EXTRAS = "extras";

  /** The Constant JSON_FIELD_CONTACTS. */
  public static final String JSON_FIELD_CONTACTS = "contacts";

  /** The Constant JSON_FIELD_RESOURCES. */
  public static final String JSON_FIELD_RESOURCES = "resources";

  /** The Constant JSON_FIELD_TAGS. */
  public static final String JSON_FIELD_TAGS = "tags";

  /** The Constant JSON_FIELD_CATEGORIES. */
  public static final String JSON_FIELD_CATEGORIES = "groups";

  /** The Constant JSON_FIELD_LICENCE. */
  public static final String JSON_FIELD_LICENCE = "terms_of_use";

  /** The Constant JSON_FIELD_COVERAGEFROM. */
  public static final String JSON_FIELD_COVERAGEFROM = "temporal_start";

  /** The Constant JSON_FIELD_COVERAGETO. */
  public static final String JSON_FIELD_COVERAGETO = "temporal_end";

  /** The Constant JSON_FIELD_TEMPORALGRANULARITY. */
  public static final String JSON_FIELD_TEMPORALGRANULARITY = "temporal_granularity";

  /** The Constant JSON_FIELD_TEMPORALGRANULARITY_FACTOR. */
  public static final String JSON_FIELD_TEMPORALGRANULARITY_FACTOR = "temporal_granularity_factor";

  /** The Constant JSON_FIELD_ALL_FIELDS. */
  public static final String JSON_FIELD_ALL_FIELDS = "all_fields";

  /** The Constant JSON_DATETIME_PATTERN. */
  public static final String JSON_DATETIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";

  /** The Constant JSON_FIELD_PUBLISHED. */
  public static final String JSON_FIELD_PUBLISHED = "issued";

  /** The Constant JSON_FIELD_MODIFIED. */
  public static final String JSON_FIELD_MODIFIED = "modified";
}
