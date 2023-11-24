package de.seitenbau.govdata.odp.registry.ckan.api;

import static de.seitenbau.govdata.odp.registry.ckan.Constants.PROPERTIES_FILENAME;
import static de.seitenbau.govdata.odp.registry.ckan.Constants.PROPERTY_NAME_CKAN_AUTHORIZATION_KEY;
import static de.seitenbau.govdata.odp.registry.ckan.Constants.PROPERTY_NAME_CKAN_URL;

import java.io.IOException;
import java.util.Properties;

import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Ignore("Implement HTTP-Server-Mock")
public class CKANClientActionTest
{

  private final Logger log = LoggerFactory.getLogger(getClass());

  private CKANClientAction ckanClientAction;

  private Properties properties = new Properties();

  @Before
  public void beforeTest()
  {
    RegisterBuiltin.register(ResteasyProviderFactory.getInstance());
    ResteasyClient client = new ResteasyClientBuilder().build();
    try
    {
      properties
          .load(Thread.currentThread().getContextClassLoader().getResourceAsStream(PROPERTIES_FILENAME));
      ResteasyWebTarget target = client.target((String) properties.get(PROPERTY_NAME_CKAN_URL));
      ckanClientAction = target.proxy(CKANClientAction.class);
    }
    catch (IOException e)
    {
      log.error("loading properties file", e);
      ResteasyWebTarget target = client.target("http://localhost:5000");
      ckanClientAction = target.proxy(CKANClientAction.class);
    }
  }

  @Test
  public void showUser()
  {
    ObjectMapper m = new ObjectMapper();
    ObjectNode body = m.createObjectNode();

    Response response =
        ckanClientAction.showUser(properties.getProperty(PROPERTY_NAME_CKAN_AUTHORIZATION_KEY), body);
    if (response.getStatus() == Response.Status.OK.getStatusCode())
    {
      log.info(response.getEntity().toString());
    }
  }

  @Test
  public void showRoles()
  {
    ObjectMapper m = new ObjectMapper();
    ObjectNode body = m.createObjectNode();
    body.put("domain_object", "system");
    body.put("user", "datenbereitstellera");

    JsonNode node = ckanClientAction.showRoles(body);

    log.info("roles: {}", node.toString());
  }
}
