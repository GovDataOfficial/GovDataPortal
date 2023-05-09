package de.seitenbau.govdata.odp.registry.date;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import de.seitenbau.govdata.date.DateUtil;

/**
 * Enthält eine Hilfsmethode für die Deserialisierung von Datumsobjekten.
 *
 * @author youalad
 *
 */
public class DateDeserializer extends StdDeserializer<Date>
{
  private static final long serialVersionUID = 7077690438840040324L;

  private static final Logger LOG = LoggerFactory.getLogger(DateDeserializer.class);

  private static final String[] ADDITIONAL_DATE_FORMATS = {"yyyy-MM-dd'T'HH:mm:ss.SSSZ",
      "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", "EEE, dd MMM yyyy HH:mm:ss zzz"};
  
  /**
   * Default constructor.
   */
  public DateDeserializer()
  {
    this(null);
  }

  protected DateDeserializer(Class<?> vc)
  {
    super(vc);
  }

  @Override
  public Date deserialize(JsonParser p, DeserializationContext ctxt)
      throws IOException
  {
    String dateString  = p.getText();
    Date result = DateUtil.parseDateString(dateString);

    if (result == null)
    {
      for (String pattern : ADDITIONAL_DATE_FORMATS)
      {
        result = DateUtil.parseDateString(dateString, pattern);
        if (result != null)
        {
          return result;
        }
      }
      LOG.debug("Unparseable date: '{}'. Supported formats: {}", dateString,
          Arrays.toString(ADDITIONAL_DATE_FORMATS));
    }

    return result;
  }

}
