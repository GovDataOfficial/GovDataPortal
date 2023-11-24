package de.seitenbau.govdata.odr;

import java.util.Properties;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import de.seitenbau.govdata.odp.registry.ODRClient;
import de.seitenbau.govdata.odp.registry.ckan.Constants;
import de.seitenbau.govdata.odp.spi.OpenDataRegistry;
import lombok.extern.slf4j.Slf4j;

/**
 * Ein Client für den Zugriff auf CKAN über die CKAN-API.
 * 
 * @author rnoerenberg
 * 
 */
@Slf4j
@Repository
public class RegistryClient
{
  private ODRClient client;

  @Value("${authenticationKey}")
  private String authKey;

  @Value("${cKANurl}")
  private String ckanUrl;

  /**
   * Initialize the client.
   */
  @PostConstruct
  public void init()
  {
    client = OpenDataRegistry.getClient();
    Properties props = new Properties();
    props.setProperty(Constants.PROPERTY_NAME_CKAN_AUTHORIZATION_KEY, authKey);
    props.setProperty(Constants.PROPERTY_NAME_CKAN_URL, ckanUrl);
    try
    {
      client.init(props);
    }
    catch (Exception e)
    {
      log.error("An error occurred while initializing client!", e);
    }
  }

  /**
   * Getter for client instance.
   * @return
   */
  public ODRClient getInstance()
  {
    return client;
  }
}
