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
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;
import java.util.TimeZone;

import org.apache.commons.lang3.StringUtils;
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
  private static final Logger LOG = LoggerFactory.getLogger(DateUtil.class);

  private static final String[] DEFAULT_DATE_FORMATS = {"yyyy-MM-dd'T'HH:mm:ssX", "yyyy-MM-dd'T'HH:mm:ssz",
      "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd HH:mm:ssX", "yyyy-MM-dd HH:mm:ssz", "yyyy-MM-dd HH:mm:ss X",
      "yyyy-MM-dd HH:mm:ss z", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd", "dd.MM.yyyy'T'HH:mm:ssX",
      "dd.MM.yyyy'T'HH:mm:ssz", "dd.MM.yyyy'T'HH:mm:ss", "dd.MM.yyyy HH:mm:ss", "dd.MM.yyyy"};

  /**
   * Versucht aus dem übergebenen String ein Datumsobjekt zu erzeugen.
   * 
   * @param dateString ein String, der eventuell ein Datum repräsentiert.
   * @param pattern das Datums-Pattern, anhand dem versucht wird ein Datumsobjekt aus dem String zu
   *        erzeugen.
   * @return das Datumsobjekt.
   */
  public static Date parseDateString(String dateString, String pattern)
  {
    final String method = "parseDateString(String, String) : ";
    LOG.trace(method + "Start");

    Date result = null;
    if (dateString != null && pattern != null)
    {
      try
      {
        result = parseDateStringInternal(dateString, pattern);
      }
      catch (ParseException e)
      {
        LOG.debug(method + e.getMessage());
      }
    }

    LOG.trace(method + "End");
    return result;
  }

  /**
   * Versucht aus dem übergebenen String ein Datumsobjekt zu erzeugen.
   * 
   * @param dateString ein String, der eventuell ein Datum repräsentiert.
   * @return das Datumsobjekt.
   */
  public static Date parseDateString(String dateString)
  {
    final String method = "parseDateString(String) : ";
    LOG.trace(method + "Start");

    Date result = null;
    if (StringUtils.isNotBlank(dateString))
    {
      for (String pattern : DEFAULT_DATE_FORMATS)
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
        LOG.debug(method + "Could not parse string '{}' as date! Supported formats: {}", dateString,
            Arrays.toString(DEFAULT_DATE_FORMATS));
      }
    }

    LOG.trace(method + "End");
    return result;
  }

  /**
   * Versucht anhand des übergebenen Patterns das übergebene Datumsobjekt als einen String zu
   * formatieren.
   * 
   * @param date das zu formatierende Datum.
   * @param pattern das gewünschte Pattern.
   * @return die Repräsentation als String oder null, falls das übergebene Datum null ist oder das
   *         Datum nicht formatiert werden konnte.
   */
  public static String formatDate(Date date, String pattern)
  {
    final String method = "formatDate() : ";
    LOG.trace(method + "Start");

    String result = null;
    if (date != null && pattern != null)
    {
      try
      {
        result = formatDateInternal(date, pattern);
      }
      catch (ParseException e)
      {
        LOG.debug(method + e.getMessage());
      }
    }

    LOG.trace(method + "End");
    return result;
  }

  /**
   * Versucht das übergebene Datumsobjekt als einen String in UTC-Zeit zu formatieren.
   * 
   * @param date das zu formatierende Datum.
   * @return die Repräsentation als String oder null, falls das übergebene Datum null ist oder das
   *         Datum nicht formatiert werden konnte.
   */
  public static String formatDateUTC(Date date)
  {
    final String method = "formatDate() : ";
    LOG.trace(method + "Start");

    String result = null;
    if (date != null)
    {
      try
      {
        result = formatDateInternal(date, "yyyy-MM-dd'T'HH:mm:ss", TimeZone.getTimeZone("UTC"));
      }
      catch (ParseException e)
      {
        LOG.debug(method + e.getMessage());
      }
    }

    LOG.trace(method + "End");
    return result;
  }

  private static Date parseDateStringInternal(String dateString, String pattern) throws ParseException
  {
    final String method = "parseDateString() : ";
    LOG.trace(method + "Start");

    DateFormat df = new SimpleDateFormat(pattern);
    df.setLenient(false);
    Date result = df.parse(dateString);

    LOG.trace(method + "End");
    return result;
  }

  private static String formatDateInternal(Date date, String pattern) throws ParseException
  {
    return formatDateInternal(date, pattern, null);
  }

  private static String formatDateInternal(Date date, String pattern, TimeZone timeZone) throws ParseException
  {
    final String method = "formatDateInternal() : ";
    LOG.trace(method + "Start");

    DateFormat df = new SimpleDateFormat(pattern);
    df.setLenient(false);
    if (Objects.nonNull(timeZone))
    {
      df.setTimeZone(timeZone);
    }
    String result = df.format(date);

    LOG.trace(method + "End");
    return result;
  }

  public static Date getCopyOfDate(Date date)
  {
    if (date == null)
    {
      return null;
    }

    return new Date(date.getTime());
  }

}
