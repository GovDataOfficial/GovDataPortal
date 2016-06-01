/**
 * Copyright (c) 2015 SEITENBAU GmbH
 *
 * This file is part of Open Data Platform.
 *
 * Open Data Platform is free software: you can redistribute it and/or modify it under the terms of
 * the GNU Affero General Public License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 * 
 * Open Data Plaform is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with Open Data
 * Platform. If not, see <http://www.gnu.org/licenses/agpl-3.0>.
 */

package de.seitenbau.govdata.date;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Enthält Hilfsmethoden für die Verarbeitung von Datumsobjekten.
 * 
 * @author rnoerenberg
 *
 */
public abstract class DateUtil
{
  private static final Logger log = LoggerFactory.getLogger(DateUtil.class);

  private static final String[] defaultDateFormats = {"yyyy-MM-dd'T'HH:mm:ssX", "yyyy-MM-dd'T'HH:mm:ssz",
      "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd HH:mm:ssX", "yyyy-MM-dd HH:mm:ssz", "yyyy-MM-dd HH:mm:ss X",
      "yyyy-MM-dd HH:mm:ss z", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd", "dd.MM.yyyy'T'HH:mm:ssX",
      "dd.MM.yyyy'T'HH:mm:ssz", "dd.MM.yyyy'T'HH:mm:ss", "dd.MM.yyyy HH:mm:ss", "dd.MM.yyyy"};

  public static Date parseDateString(String dateString, String pattern)
  {
    final String method = "parseDateString(String, String) : ";
    log.trace(method + "Start");

    Date result = null;
    if (dateString != null && pattern != null)
    {
      try
      {
        result = parseDateStringInternal(dateString, pattern);
      }
      catch (ParseException e)
      {
        log.debug(method + e.getMessage());
      }
    }

    log.trace(method + "End");
    return result;
  }

  public static Date parseDateString(String dateString)
  {
    final String method = "parseDateString(String) : ";
    log.trace(method + "Start");

    Date result = null;
    if (dateString != null)
    {
      for (String pattern : defaultDateFormats)
      {
        try
        {
          result = parseDateStringInternal(dateString, pattern);
          break;
        }
        catch (ParseException e)
        {
          continue;
        }
      }
      if (result == null)
      {
        log.debug(method + "Could not parse string '{}' as date!", dateString);
      }
    }

    log.trace(method + "End");
    return result;
  }

  private static Date parseDateStringInternal(String dateString, String pattern) throws ParseException
  {
    final String method = "parseDateString() : ";
    log.trace(method + "Start");

    DateFormat df = new SimpleDateFormat(pattern);
    df.setLenient(false);
    Date result = df.parse(dateString);

    log.trace(method + "End");
    return result;
  }
}
