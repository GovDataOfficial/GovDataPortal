package de.seitenbau.govdata.odp.registry.ckan.impl;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

import de.seitenbau.govdata.odp.registry.ckan.json.UserBean;
import de.seitenbau.govdata.odp.registry.model.Metadata;
import de.seitenbau.govdata.odp.registry.model.User;

/**
 * Implementation for the interface {@link User}.
 * 
 */
public class UserImpl implements User, Serializable
{

  private static final long serialVersionUID = -9199121089537222860L;

  private UserBean user;

  private String apiToken;

  /**
   * Constructor with user bean.
   * 
   * @param user
   */
  public UserImpl(UserBean user)
  {
    this.user = user;
  }

  @Override
  public String getName()
  {
    return user.getName();
  }

  @Override
  public String getFullname()
  {
    return user.getFullname();
  }

  @Override
  public String getDisplayName()
  {
    return user.getDisplay_name();
  }

  @Override
  public String getEmail()
  {
    return user.getEmail();
  }

  @Override
  public boolean isCreator(Metadata metadata)
  {
    return StringUtils.equals(this.user.getId(), metadata.getCreatorUserId());
  }
  
  @Override
  public String toString()
  {
    return user.getId() + ": " + user.getName() + " / " + user.getDisplay_name() + " / " + getApiToken();
  }

  @Override
  public String getId()
  {
    return user.getId();
  }

  @Override
  public String getApiToken()
  {
    return this.apiToken;
  }

  /**
   * Set an API-token.
   * 
   * @param token
   */
  public void setApiToken(String token)
  {
    this.apiToken = token;
  }

}
