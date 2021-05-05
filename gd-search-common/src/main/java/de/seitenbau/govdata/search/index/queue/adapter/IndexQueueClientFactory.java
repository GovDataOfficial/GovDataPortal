package de.seitenbau.govdata.search.index.queue.adapter;

import org.springframework.beans.factory.annotation.Value;

import de.seitenbau.serviceportal.common.client.AbstractClientFactory;

/**
 * Factory für Clients, die auf die Index-Queue zugreifen wollen.
 */
public class IndexQueueClientFactory extends AbstractClientFactory
{
  
  @Value("${service.indexqueue.url:${service.url}}")
  private String baseUrl;
  
  /**
   * Erzeugt einen neuen threadsafe Client für die IndexQueueAdapterServiceRESTResource
   *
   * @return Client für die IndexQueueAdapterServiceRESTResource
   */
  public IndexQueueAdapterServiceRESTResource createSuchindexQueueClient()
  {
    return createClient(baseUrl, IndexQueueAdapterServiceRESTResource.class);
  }

  public void setBaseUrl(String baseUrl)
  {
    this.baseUrl = baseUrl;
  }

}
