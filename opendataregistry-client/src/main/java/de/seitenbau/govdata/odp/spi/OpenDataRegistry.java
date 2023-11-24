package de.seitenbau.govdata.odp.spi;

import java.util.ServiceLoader;

import de.seitenbau.govdata.odp.registry.ODRClient;
import de.seitenbau.govdata.odp.registry.ckan.Constants;

/**
 * The Class OpenDataRegistry is the factory for the Open Data Registry provider interface.
 * 
 * <p>
 * Usage: <blockquote>
 * 
 * <pre>
 * ODRClient client = OpenDataRegistry.getClient();
 * client.init();
 * </pre>
 * 
 * </blockquote> or: <blockquote>
 * 
 * <pre>
 * ODRClient client = OpenDataRegistry.getClient(&quot;CKAN&quot;);
 * client.init();
 * </pre>
 * 
 * </blockquote>
 * 
 * @see ODRClient
 */
public abstract class OpenDataRegistry
{

    /** The odr loader. */
    private static ServiceLoader<OpenDataRegistry> odrLoader = ServiceLoader.load(OpenDataRegistry.class);

    /**
     * Gets the provider implementation for a given name of the open data registry client interface.
     * 
     * @param name
     *            the name of the provider implementation
     * @return the client interface
     */
    public static synchronized ODRClient getClient(String name)
    {
        if (name != null)
        {
            for (OpenDataRegistry op : odrLoader)
            {
                if (name.equals(op.getName()))
                {
                    return op.createClient();
                }
            }
        }
        return null;
    }

    /**
     * Gets the default provider implementation of the open data registry client interface, which is 'CKAN'.
     * 
     * @return the client interface
     */
    public static synchronized ODRClient getClient()
    {
        ODRClient client = getClient(Constants.OPEN_DATA_PROVIDER_NAME);
        if (client == null)
        {
            for (OpenDataRegistry op : odrLoader)
            {
                client = op.createClient();
                // if there a several implementations available, check, if this
                // is a suitable one
            }
        }
        return client;
    }

    /**
     * Gets the name of the current open data registry provider implementation. Default provider is 'CKAN'.
     * 
     * @return the name of the current provider
     */
    public abstract String getName();

    /**
     * Creates the client.
     * 
     * @return the oDR client
     */
    public abstract ODRClient createClient();

}
