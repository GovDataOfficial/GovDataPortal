package de.fhg.fokus.odp.registry.common;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import javax.ws.rs.core.Application;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.junit.Before;

import com.sun.jersey.spi.container.servlet.ServletContainer;

import de.fhg.fokus.odp.registry.ODRClient;
import de.fhg.fokus.odp.registry.ckan.ODRClientImpl;

public class TestBase
{
  /** The Constant PROPERTY_NAME_CKAN_URL. */
  static final String PROPERTY_NAME_CKAN_URL = "ckan.url";

  /** The Constant PROPERTY_NAME_CKAN_AUTHORIZATION_KEY. */
  static final String PROPERTY_NAME_CKAN_AUTHORIZATION_KEY = "ckan.authorization.key";

  static final String PROP_NAME_DEFAULT_SORT_METADATA = "sorting.default.metadata";

  private static final int MOCK_PORT = 6666;

  private static final String MOCK_HTTP_BASE_URL = "http://localhost:" + MOCK_PORT;

  private static String CKAN_PATH = "/ckan/";

  private static String CKAN_URL = MOCK_HTTP_BASE_URL + CKAN_PATH;

  private Server httpServer;

  protected ODRClient odrClient;
  
  @Before
  public void setupTarget()
  {
    odrClient = new ODRClientImpl();
  }

  protected void setupConfigurationAndHttpServer() throws Exception
  {
    // Http server
    httpServer = new Server(MOCK_PORT);

    // Servlet container for CkanResource implementation
    ServletContainer ckanServerServlet = new ServletContainer(
        new Application()
        {
          @Override
          public Set<Class<?>> getClasses()
          {
            HashSet<Class<?>> resources = new HashSet<Class<?>>();

            // CkanResource implements CKANClientAction
            resources.add(CkanResource.class);
            return resources;
          }
        });

    ServletContextHandler context 
        = new ServletContextHandler(ServletContextHandler.SESSIONS);

    // Initialize http server
    context.setContextPath("/");
    context.addServlet(new ServletHolder(ckanServerServlet), CKAN_PATH + "*");
    httpServer.setHandler(context);
    httpServer.start();

    // Endpoint neu setzen
    Properties props = new Properties();
    props.setProperty(PROPERTY_NAME_CKAN_AUTHORIZATION_KEY, "secret");
    props.setProperty(PROP_NAME_DEFAULT_SORT_METADATA, "sort desc");
    props.setProperty(PROPERTY_NAME_CKAN_URL, CKAN_URL);
    odrClient.init(props);
  }

  protected void stopHttpServer() throws Exception
  {
    if (httpServer != null)
    {
      httpServer.stop();
      httpServer = null;
    }
  }
}
