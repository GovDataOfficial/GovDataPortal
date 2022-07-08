package de.seitenbau.govdata.search.index.listener;


import javax.inject.Inject;

import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageListener;
import com.liferay.portal.kernel.messaging.MessageListenerException;
import com.liferay.portal.kernel.messaging.proxy.ProxyRequest;
import com.liferay.portal.kernel.search.IndexWriter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class IndexListener implements MessageListener
{
  @Inject
  private IndexWriter indexWriter;
  
  @Override
  public void receive(Message message) throws MessageListenerException
  {
    log.debug("IndexListener.recieve: " + message.getDestinationName());
    ProxyRequest proxyRequest = (ProxyRequest) message.getPayload();
    
    try
    {
      proxyRequest.execute(indexWriter);
    }
    catch (Exception e)
    {
      log.error("Exceution of proxy request failed: " + e.getMessage());
      throw new MessageListenerException(e);
    }
    
    return;
  }

}
