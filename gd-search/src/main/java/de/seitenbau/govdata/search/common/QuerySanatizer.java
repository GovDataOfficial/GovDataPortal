package de.seitenbau.govdata.search.common;

import java.text.ParseException;
import java.util.Date;

import org.jsoup.Jsoup;

import de.seitenbau.govdata.date.DateUtil;

public class QuerySanatizer {
  private static final String BLACKLISTED_QUERY_CHARS = "[/]";

  public static String sanatizeQuery(String q) {
    q = Jsoup.parse(q).text(); // remove html
    q = q.replaceAll(BLACKLISTED_QUERY_CHARS, " ");
    
    return q;
  }
  
  public static Date getDateFromString(String dateStr) throws ParseException {
    Date result = DateUtil.parseDateString(dateStr, "dd.MM.yyyy");
    if(result == null) {
      throw new ParseException("Not a valid Date-String", 0);
    }
    return result;
  }
}
