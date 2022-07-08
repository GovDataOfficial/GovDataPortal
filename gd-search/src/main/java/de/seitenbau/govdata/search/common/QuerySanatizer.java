package de.seitenbau.govdata.search.common;

import java.text.ParseException;
import java.util.Date;
import java.util.Objects;

import de.seitenbau.govdata.clean.StringCleaner;
import de.seitenbau.govdata.date.DateUtil;

/**
 * Stellt Methoden bereits, um die Parameter in der Suchanfrage zu säubern.
 * 
 * @author tscheffler
 *
 */
public abstract class QuerySanatizer
{
  private static final String BLACKLISTED_QUERY_CHARS = "[/]";

  /**
   * Entfernt aus dem übergebenen String HTML-Markup und nicht erlaubte Zeichen heraus und gibt
   * diesen String zurück.
   * 
   * @param q der zu säubernde String.
   * @return der gesäuberte String oder null, falls der Parameter <b>q</b> null ist.
   */
  public static String sanatizeQuery(String q)
  {
    if (Objects.nonNull(q))
    {
      q = StringCleaner.trimAndFilterString(q); // remove html
      q = q.replaceAll(BLACKLISTED_QUERY_CHARS, " ");
    }

    return q;
  }

  /**
   * Versucht aus dem übergebenen String ein Datumsobjekt zu erzeugen.
   * 
   * @param dateStr ein String, der eventuell ein Datum repräsentiert.
   * @return das Datumsobjekt.
   * @throws ParseException falls der String nicht zu einem Datum geparst werden konnte.
   */
  public static Date getDateFromString(String dateStr) throws ParseException
  {
    Date result = DateUtil.parseDateString(dateStr, "dd.MM.yyyy");
    if (result == null)
    {
      throw new ParseException("Not a valid Date-String", 0);
    }
    return result;
  }
}
