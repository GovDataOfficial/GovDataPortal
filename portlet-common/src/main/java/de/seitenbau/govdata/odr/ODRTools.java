package de.seitenbau.govdata.odr;

import java.util.ArrayList;
import java.util.List;

import javax.portlet.PortletRequest;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.util.PortalUtil;

import de.fhg.fokus.odp.registry.ODRClient;
import de.fhg.fokus.odp.registry.model.Organization;
import de.fhg.fokus.odp.registry.model.User;
import de.fhg.fokus.odp.registry.model.exception.OpenDataRegistryException;

@Slf4j
public class ODRTools
{
  /**
   * Tries to find the matching ckanUser for the currently logged in Liferay user (if somebody is
   * logged in...). If no user exists, a new one will be created.
   * @param liferayUser
   * @return
   */
  public User findOrCreateCkanUser(String liferayUserScreenname, ODRClient client)
  {
    final String method = "findOrCreateCkanUser() : ";

    User ckanUser = findCkanUser(liferayUserScreenname, client);
    if (ckanUser != null) {
      log.debug(method + "Using CKAN-User: " + ckanUser.getDisplayName());
      return ckanUser;
    } else {
      // no user found, create a new ckan-user!
      ckanUser = createCkanUser(liferayUserScreenname, client);
      if (ckanUser != null) {
        log.debug(method + "CKAN-User was created: " + ckanUser.getDisplayName());
        return ckanUser;
      }
    }
    
    log.warn(method + "Could not create CKAN-User!");
    return ckanUser;
  }
  
  public User findCkanUser(String liferayUserScreenname, ODRClient client) {
    String nameFragment = liferayUserScreenname.toLowerCase();
    return client.findUser(nameFragment);
  }
  
  public User createCkanUser(String liferayUserScreenname, ODRClient client) {
    String nameFragment = liferayUserScreenname.toLowerCase();
    String password = RandomStringUtils.randomAlphanumeric(16);
    return client.createUser(nameFragment, "ckanuser-" + liferayUserScreenname + "@govdata.de", password);
  }
  
  public User getCkanuserFromRequest(PortletRequest request, ODRClient client)
      throws OpenDataRegistryException, PortalException, SystemException
  {
    com.liferay.portal.model.User liferayUser = PortalUtil.getUser(request);
    if (liferayUser != null && liferayUser.getScreenName() != null)
    {
      return new ODRTools().findOrCreateCkanUser(liferayUser.getScreenName(), client);
    }
    else
    {
      throw new OpenDataRegistryException("Could not find ckan user, you are liferay guest.");
    }
  }
  
  public boolean containsOrganization(List<Organization> organizationsForUser, String organizationId)
  {
    for(Organization org : organizationsForUser) {
      if(StringUtils.equals(org.getId(), organizationId)) {
        return true;
      }
    }
    return false;
  }

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
