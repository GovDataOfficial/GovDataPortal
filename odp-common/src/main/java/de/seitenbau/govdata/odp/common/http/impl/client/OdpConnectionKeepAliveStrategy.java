package de.seitenbau.govdata.odp.common.http.impl.client;

import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HttpResponse;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;

/**
 * An implementation of a strategy deciding duration that a connection can remain idle, but setting
 * the keep alive to a maximum of 10 minutes (600 seconds).
 * 
 */
public class OdpConnectionKeepAliveStrategy implements ConnectionKeepAliveStrategy
{
  /** Instance object. */
  public static final OdpConnectionKeepAliveStrategy INSTANCE = new OdpConnectionKeepAliveStrategy();

  @Override
  public long getKeepAliveDuration(final HttpResponse response, final HttpContext context)
  {
    Args.notNull(response, "HTTP response");
    final HeaderElementIterator it = new BasicHeaderElementIterator(
        response.headerIterator(HTTP.CONN_KEEP_ALIVE));
    while (it.hasNext())
    {
      final HeaderElement he = it.nextElement();
      final String param = he.getName();
      final String value = he.getValue();
      if (value != null && param.equalsIgnoreCase("timeout"))
      {
        try
        {
          return Long.parseLong(value) * 1000;
        }
        catch (final NumberFormatException ignore)
        {
        }
          }
      }
      return 600 * 1000;
  }

}
