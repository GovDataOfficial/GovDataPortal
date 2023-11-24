package de.seitenbau.govdata.odp.registry.model;


/**
 * The Interface User.
 * 
 * @author sim
 */
public interface User
{

    /**
     * Gets the name.
     * 
     * @return the name
     */
    String getName();

    /**
     * Gets the fullname.
     * 
     * @return the fullname
     */
    String getFullname();

    /**
     * Gets the display name.
     * 
     * @return the display name
     */
    String getDisplayName();

    /**
     * Gets the email.
     * 
     * @return the email
     */
    String getEmail();

    /**
     * Gets the ID.
     * 
     * @return the ID
     */
    String getId();

    /**
   * Checks if is creator.
   * 
   * @param metadata the metadata
   * @return true, if is creator
   */
  boolean isCreator(Metadata metadata);

  /**
   * Get the API-token.
   * 
   * @return
   */
  String getApiToken();

}
