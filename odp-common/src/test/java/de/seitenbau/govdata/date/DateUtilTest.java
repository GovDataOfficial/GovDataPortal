package de.seitenbau.govdata.date;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.assertj.core.api.Assertions;
import org.junit.Test;

/**
 * Tests für die Klasse {@link DateUtil}.
 * 
 * @author rnoerenberg
 *
 */
public class DateUtilTest
{
  private static final String DATE_MINUS = "2015-11-10";
  private static final String DATE_POINT = "10.11.2015";
  private static final String DATE_PATTERN_MINUS = "yyyy-MM-dd";
  private static final String DATE_PATTERN_POINT = "dd.MM.yyyy";
  private static final String DATE_TIME_PATTERN = "dd.MM.yyyy HH:mm:ss";

  private static final String DATE_TIME_WITH_TIMEZONE_PATTERN = "dd.MM.yyyy'T'HH:mm:ss";

  private static final String DATE_TIME_WITH_TIMEZONE_HOURS_PATTERN = "dd.MM.yyyy'T'HH:mm:ssX";

  private static final String DATE_TIME_ENGLISH_WITH_TIMEZONE_HOURS_PATTERN = "yyyy-MM-dd'T'HH:mm:ssX";

  private static final String DATE_TIME_STRING = "10.11.2015 2:10:55";

  private static final String DATE_TIME_WITH_TIMEZONE_STRING = "10.11.2015T2:10:55";

  private static final String DATE_TIME_WITH_TIMEZONE_HOURS_STRING = "10.11.2015T2:10:55+02:00";

  private static final String DATE_TIME_ENGLISH_WITH_TIMEZONE_HOURS_STRING = "2017-08-16T11:01:06+02:00";

  private static final String DATE_TIME_WITH_TIMEZONE_HOURS_DAYLIGHT_TIME_STRING = "10.07.2015T2:10:55+0200";

  @Test
  public void parseDateString_dateString_null() throws Exception
  {
    /* prepare */

    /* execute */
    Date result = DateUtil.parseDateString(null);

    /* verify */
    Assertions.assertThat(result).isNull();
  }

  @Test
  public void parseDateString_dateString_dateTime() throws Exception
  {
    /* prepare */
    Date expected = parseDate(DATE_TIME_STRING, DATE_TIME_PATTERN);

    /* execute */
    Date result = DateUtil.parseDateString(DATE_TIME_STRING);

    /* verify */
    Assertions.assertThat(result).isEqualTo(expected);
  }

  @Test
  public void parseDateString_dateString_dateTime_withTimezone() throws Exception
  {
    /* prepare */
    Date expected = parseDate(DATE_TIME_WITH_TIMEZONE_STRING, DATE_TIME_WITH_TIMEZONE_PATTERN);

    /* execute */
    Date result = DateUtil.parseDateString(DATE_TIME_WITH_TIMEZONE_STRING);

    /* verify */
    assertDate(expected, result, 2);
  }

  @Test
  public void parseDateString_dateString_dateTime_withTimezoneAndHours() throws Exception
  {
    /* prepare */
    Date expected = parseDate(DATE_TIME_WITH_TIMEZONE_HOURS_STRING, DATE_TIME_WITH_TIMEZONE_HOURS_PATTERN);

    /* execute */
    Date result = DateUtil.parseDateString(DATE_TIME_WITH_TIMEZONE_HOURS_STRING);

    /* verify */
    assertDate(expected, result, 1);
  }

  @Test
  public void parseDateString_dateString_dateTime_english_withTimezoneAndHours() throws Exception
  {
    /* prepare */
    Date expected =
        parseDate(DATE_TIME_ENGLISH_WITH_TIMEZONE_HOURS_STRING, DATE_TIME_ENGLISH_WITH_TIMEZONE_HOURS_PATTERN);

    /* execute */
    Date result = DateUtil.parseDateString(DATE_TIME_ENGLISH_WITH_TIMEZONE_HOURS_STRING);

    /* verify */
    assertDate(expected, result, 11);
  }

  @Test
  public void parseDateString_dateString_dateTime_withTimezoneAndHours_daylightTime() throws Exception
  {
    /* prepare */
    Date expected =
        parseDate(DATE_TIME_WITH_TIMEZONE_HOURS_DAYLIGHT_TIME_STRING, DATE_TIME_WITH_TIMEZONE_HOURS_PATTERN);

    /* execute */
    Date result = DateUtil.parseDateString(DATE_TIME_WITH_TIMEZONE_HOURS_DAYLIGHT_TIME_STRING);

    /* verify */
    assertDate(expected, result, 2);
  }

  @Test
  public void parseDateString_dateString_date_point() throws Exception
  {
    /* prepare */
    Date expected = parseDate(DATE_POINT, DATE_PATTERN_POINT);

    /* execute */
    Date result = DateUtil.parseDateString(DATE_POINT);

    /* verify */
    Assertions.assertThat(result).isEqualTo(expected);
  }

  @Test
  public void parseDateString_dateString_date_minus() throws Exception
  {
    /* prepare */
    Date expected = parseDate(DATE_MINUS, DATE_PATTERN_MINUS);

    /* execute */
    Date result = DateUtil.parseDateString(DATE_MINUS);

    /* verify */
    Assertions.assertThat(result).isEqualTo(expected);
  }

  @Test
  public void parseDateString_dateString_noMatchingPattern() throws Exception
  {
    /* prepare */

    /* execute */
    Date result = DateUtil.parseDateString("10-11-2015");

    /* verify */
    Assertions.assertThat(result).isNull();
  }

  @Test
  public void parseDateString_dateStringWithPattern_both_null() throws Exception
  {
    /* prepare */

    /* execute */
    Date result = DateUtil.parseDateString(null, null);

    /* verify */
    Assertions.assertThat(result).isNull();
  }

  @Test
  public void parseDateString_dateStringWithPattern_pattern_null() throws Exception
  {
    /* prepare */

    /* execute */
    Date result = DateUtil.parseDateString(DATE_TIME_STRING, null);

    /* verify */
    Assertions.assertThat(result).isNull();
  }

  @Test
  public void parseDateString_dateStringWithPattern_dateString_null() throws Exception
  {
    /* prepare */

    /* execute */
    Date result = DateUtil.parseDateString(null, DATE_TIME_PATTERN);

    /* verify */
    Assertions.assertThat(result).isNull();
  }

  @Test
  public void parseDateString_dateStringWithPattern_dateTime() throws Exception
  {
    /* prepare */
    Date expected = parseDate(DATE_TIME_STRING, DATE_TIME_PATTERN);

    /* execute */
    Date result = DateUtil.parseDateString(DATE_TIME_STRING, DATE_TIME_PATTERN);

    /* verify */
    Assertions.assertThat(result).isEqualTo(expected);
  }

  @Test
  public void parseDateString_dateStringWithPattern_date() throws Exception
  {
    /* prepare */
    Date expected = parseDate(DATE_TIME_STRING, DATE_PATTERN_POINT);

    /* execute */
    Date result = DateUtil.parseDateString(DATE_TIME_STRING, DATE_PATTERN_POINT);

    /* verify */
    Assertions.assertThat(result).isEqualTo(expected);
  }

  @Test
  public void parseDateString_dateStringWithPattern_date_notParseable() throws Exception
  {
    /* prepare */

    /* execute */
    Date result = DateUtil.parseDateString(DATE_POINT, DATE_TIME_PATTERN);

    /* verify */
    Assertions.assertThat(result).isNull();
  }

  @Test
  public void formatDate_dateStringWithPattern_date() throws Exception
  {
    /* prepare */
    Date input = new GregorianCalendar(2015, Calendar.NOVEMBER, 10).getTime();

    /* execute */
    String result = DateUtil.formatDate(input, DATE_PATTERN_POINT);

    /* verify */
    Assertions.assertThat(result).isEqualTo(DATE_POINT);
  }

  @Test
  public void formatDate_dateStringUTC_datetime_winter() throws Exception
  {
    /* prepare */
    Date input = new GregorianCalendar(2015, Calendar.NOVEMBER, 10, 12, 15, 34).getTime();

    /* execute */
    String result = DateUtil.formatDateUTC(input);

    /* verify */
    Assertions.assertThat(result).isEqualTo("2015-11-10T11:15:34");
  }

  @Test
  public void formatDate_dateStringUTC_datetime_summer() throws Exception
  {
    /* prepare */
    Date input = new GregorianCalendar(2015, Calendar.MAY, 10, 12, 15, 34).getTime();

    /* execute */
    String result = DateUtil.formatDateUTC(input);

    /* verify */
    Assertions.assertThat(result).isEqualTo("2015-05-10T10:15:34");
  }

  private Date parseDate(String dateString, String pattern) throws ParseException
  {
    DateFormat df = new SimpleDateFormat(pattern);
    df.setLenient(false);

    return df.parse(dateString);
  }

  private void assertDate(Date expected, Date result, int hourExpected)
  {
    Assertions.assertThat(result).isEqualTo(expected);
    Calendar cal = new GregorianCalendar();
    cal.setLenient(false);
    cal.setTime(result);
    Assertions.assertThat(cal.getTimeZone()).isEqualToComparingFieldByField(
        TimeZone.getTimeZone("Europe/Berlin"));
    Assertions.assertThat(cal.get(Calendar.HOUR_OF_DAY)).isEqualTo(hourExpected);
    Assertions.assertThat(cal.getTime()).isEqualTo(expected);
  }
}
