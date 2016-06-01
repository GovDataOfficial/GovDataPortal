package de.seitenbau.govdata.search.index.listener;


import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;

import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageListener;
import com.liferay.portal.kernel.messaging.MessageListenerException;
import com.liferay.portal.kernel.messaging.proxy.ProxyRequest;
import com.liferay.portal.kernel.search.IndexWriter;

@Slf4j
public class IndexListener implements MessageListener
{
  @Inject
  IndexWriter indexWriter;
  
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
