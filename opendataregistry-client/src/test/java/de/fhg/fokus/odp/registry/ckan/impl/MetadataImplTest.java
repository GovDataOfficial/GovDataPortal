package de.fhg.fokus.odp.registry.ckan.impl;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import de.fhg.fokus.odp.registry.ODRClient;
import de.fhg.fokus.odp.registry.ckan.Constants;
import de.fhg.fokus.odp.registry.ckan.json.ExtraBean;
import de.fhg.fokus.odp.registry.ckan.json.MetadataBean;

@RunWith(MockitoJUnitRunner.class)
public class MetadataImplTest
{
  private static final String DATE_WITH_TIMEZONE_VALID = "2.10.2015T3:30:55";

  private static final String DATE_WITH_TIMEZONE_WITH_HOURS_VALID = DATE_WITH_TIMEZONE_VALID + "+02:00";

  private static final String COVERAGEFROM_VALID = "2.10.2015 3:30:55";

  private static final String COVERAGETO_VALID = "12.11.2015 10:20:33";

  private static final String DATE_PATTERN = "dd.MM.yyyy HH:mm:ss";

  private static final String DATE_PATTERN_WITH_TIMEZONE = "dd.MM.yyyy'T'HH:mm:ss";

  private static final String DATE_PATTERN_WITH_TIMEZONE_AND_HOURS = "dd.MM.yyyy'T'HH:mm:ssX";

  @Mock
  private ODRClient odrClient;

  private MetadataImpl target;

  @Test
  public void parsingDate_valid_datetime() throws Exception
  {
    /* prepare */
    Date expectedFrom = parseDate(COVERAGEFROM_VALID);
    Date expectedTo = parseDate(COVERAGETO_VALID);

    /* execute */
    target = new MetadataImpl(createDefaultMetadataBean(), odrClient);

    /* verify */
    Assertions.assertThat(target.getTemporalCoverageFrom()).isEqualToIgnoringMillis(expectedFrom);
    Assertions.assertThat(target.getTemporalCoverageTo()).isEqualToIgnoringMillis(expectedTo);
  }

  @Test
  public void parsingDate_valid_datetime_with_timezone_utc() throws Exception
  {
    /* prepare */
    Date expectedFrom = parseDate(DATE_WITH_TIMEZONE_VALID, DATE_PATTERN_WITH_TIMEZONE);
    Date expectedTo = parseDate(DATE_WITH_TIMEZONE_VALID, DATE_PATTERN_WITH_TIMEZONE);

    /* execute */
    target = new MetadataImpl(createDefaultMetadataBean(
        DATE_WITH_TIMEZONE_VALID, DATE_WITH_TIMEZONE_VALID), odrClient);

    /* verify */
    Assertions.assertThat(target.getTemporalCoverageFrom()).isEqualToIgnoringMillis(expectedFrom);
    Assertions.assertThat(target.getTemporalCoverageTo()).isEqualToIgnoringMillis(expectedTo);
  }

  @Test
  public void parsingDate_valid_datetime_with_timezone_plusTwoHours() throws Exception
  {
    /* prepare */
    Date expectedFrom = parseDate(DATE_WITH_TIMEZONE_WITH_HOURS_VALID, DATE_PATTERN_WITH_TIMEZONE_AND_HOURS);
    Date expectedTo = parseDate(DATE_WITH_TIMEZONE_WITH_HOURS_VALID, DATE_PATTERN_WITH_TIMEZONE_AND_HOURS);

    /* execute */
    target = new MetadataImpl(createDefaultMetadataBean(
        DATE_WITH_TIMEZONE_WITH_HOURS_VALID, DATE_WITH_TIMEZONE_WITH_HOURS_VALID), odrClient);

    /* verify */
    Assertions.assertThat(target.getTemporalCoverageFrom()).isEqualToIgnoringMillis(expectedFrom);
    Assertions.assertThat(target.getTemporalCoverageTo()).isEqualToIgnoringMillis(expectedTo);
  }

  @Test
  public void parsingDate_invalid_datetime() throws Exception
  {
    /* prepare */
    MetadataBean metadataBean = createMetadataBeanWithInvalidTemporalCoverage();

    /* execute */
    target = new MetadataImpl(metadataBean, odrClient);

    /* verify */
    Assertions.assertThat(target.getTemporalCoverageFrom()).isNull();
    Assertions.assertThat(target.getTemporalCoverageTo()).isNull();
  }

  private MetadataBean createDefaultMetadataBean()
  {
    return createDefaultMetadataBean(COVERAGEFROM_VALID, COVERAGETO_VALID);
  }

  private MetadataBean createDefaultMetadataBean(String coverageFrom, String coverageTo)
  {
    MetadataBean result = new MetadataBean();
    result.getExtras().add(createExtraBean(Constants.JSON_FIELD_COVERAGEFROM, coverageFrom));
    result.getExtras().add(createExtraBean(Constants.JSON_FIELD_COVERAGETO, coverageTo));
    return result;
  }

  private MetadataBean createMetadataBeanWithInvalidTemporalCoverage()
  {
    MetadataBean result = new MetadataBean();
    result.getExtras().add(createExtraBean(Constants.JSON_FIELD_COVERAGEFROM, "2015"));
    result.getExtras().add(createExtraBean(Constants.JSON_FIELD_COVERAGETO, "unknown"));
    return result;
  }

  private ExtraBean createExtraBean(String key, String value)
  {
    ExtraBean result = new ExtraBean();
    result.setKey(key);
    result.setValue(value);
    return result;
  }

  private Date parseDate(String dateString, String datePattern) throws ParseException
  {
    DateFormat df = new SimpleDateFormat(datePattern);
    df.setLenient(false);

    return df.parse(dateString);
  }

  private Date parseDate(String dateString) throws ParseException
  {
    return parseDate(dateString, DATE_PATTERN);
  }
}
