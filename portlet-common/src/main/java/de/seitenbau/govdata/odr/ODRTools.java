package de.seitenbau.govdata.odr;

import java.util.ArrayList;
import java.util.List;

import javax.portlet.PortletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.PortalUtil;

import de.seitenbau.govdata.odp.registry.ODRClient;
import de.seitenbau.govdata.odp.registry.model.Organization;
import de.seitenbau.govdata.odp.registry.model.User;
import de.seitenbau.govdata.odp.registry.model.exception.OpenDataRegistryException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ODRTools
{
  /**
   * Tries to find the matching ckanUser for the currently logged in Liferay user (if somebody is
   * logged in...). If no user exists, a new one will be created.
   * @param liferayUserScreenname liferay screen name
   * @param client the client for interacting with the CKAN instance
   * @return
   */
  public User findOrCreateCkanUser(String liferayUserScreenname, ODRClient client)
  {
    final String method = "findOrCreateCkanUser() : ";

    User ckanUser = findCkanUser(liferayUserScreenname, client);
    if (ckanUser != null)
    {
      log.debug(method + "Using CKAN-User: " + ckanUser.getDisplayName());
      return ckanUser;
    }
    else
    {
      // no user found, create a new ckan-user!
      ckanUser = createCkanUser(liferayUserScreenname, client);
      if (ckanUser != null)
      {
        log.debug(method + "CKAN-User was created: " + ckanUser.getDisplayName());
        return ckanUser;
      }
    }
    
    log.warn(method + "Could not create CKAN-User!");
    return ckanUser;
  }
  
  /**
   * Searches for the responding CKAN user to the given liferay screen name.
   * 
   * @param liferayUserScreenname liferay screen name
   * @param client the client for interacting with the CKAN instance
   * @return
   */
  public User findCkanUser(String liferayUserScreenname, ODRClient client)
  {
    String nameFragment = liferayUserScreenname.toLowerCase();
    return client.findUser(nameFragment);
  }
  
  /**
   * Creates a new user in CKAN for the given liferay screen name.
   * 
   * @param liferayUserScreenname liferay screen name
   * @param client the client for interacting with the CKAN instance
   * @return
   */
  public User createCkanUser(String liferayUserScreenname, ODRClient client)
  {
    String nameFragment = liferayUserScreenname.toLowerCase();
    String password = RandomStringUtils.randomAlphanumeric(16);
    return client.createUser(nameFragment, "ckanuser-" + liferayUserScreenname + "@govdata.de", password);
  }
  
  /**
   * Reads the liferay user from the request and gets or creates a CKAN user for the liferay user.
   * 
   * @param request
   * @param client
   * @return
   * @throws OpenDataRegistryException
   * @throws PortalException
   * @throws SystemException
   */
  public User getCkanuserFromRequest(PortletRequest request, ODRClient client)
      throws OpenDataRegistryException, PortalException, SystemException
  {
    com.liferay.portal.kernel.model.User liferayUser = PortalUtil.getUser(request);
    if (liferayUser != null && liferayUser.getScreenName() != null)
    {
      return findOrCreateCkanUser(liferayUser.getScreenName(), client);
    }
    else
    {
      throw new OpenDataRegistryException("Could not find ckan user, you are liferay guest.");
    }
  }
  
  /**
   * Checks if the organization elements contain a organization with the given organization ID.
   * 
   * @param organizationsForUser
   * @param organizationId
   * @return
   */
  public boolean containsOrganization(List<Organization> organizationsForUser, String organizationId)
  {
    for (Organization org : organizationsForUser)
    {
      if (StringUtils.equals(org.getId(), organizationId))
      {
        return true;
      }
    }
    return false;
  }

  /**
   * Extracts the IDs from the given organization elements.
   * 
   * @param organizationList list with organization objects.
   * @return
   */
  public List<String> extractIDsFromOrganizations(List<Organization> organizationList)
  {
    List<String> result = new ArrayList<String>();
    if (CollectionUtils.isNotEmpty(organizationList))
    {
      for (Organization org : organizationList)
      {
        if (org != null && org.getId() != null)
        {
          result.add(org.getId());
        }
      }
    }

    return result;
  }

}
