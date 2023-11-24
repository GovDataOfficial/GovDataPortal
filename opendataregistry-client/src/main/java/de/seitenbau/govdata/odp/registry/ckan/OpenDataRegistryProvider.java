package de.seitenbau.govdata.odp.registry.ckan;

import de.seitenbau.govdata.odp.registry.ODRClient;
import de.seitenbau.govdata.odp.spi.OpenDataRegistry;

/**
 * The Class OpenDataRegistryProvider.
 * 
 * @author sim
 */
public class OpenDataRegistryProvider extends OpenDataRegistry
{

    /*
     * (non-Javadoc)
     * 
     * @see de.seitenbau.govdata.odp.spi.OpenDataRegistry#getName()
     */
    @Override
    public String getName()
    {
      return Constants.OPEN_DATA_PROVIDER_NAME;
    }

    @Override
    public ODRClient createClient()
    {
        return new ODRClientImpl();
    }

}
