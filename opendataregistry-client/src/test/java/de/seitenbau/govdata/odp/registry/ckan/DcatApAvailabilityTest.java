package de.seitenbau.govdata.odp.registry.ckan;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DcatApAvailabilityTest {
  
  @Test
  public void getFromUriDcatAp_null() throws Exception
  {
    /* prepare */
    String value = null;
    boolean catched = false;

    try
    {
    /* execute */
      DcatApAvailability.getFromUriDcatAp(value);
    }
    catch (IllegalArgumentException ex)
    {
    /* verify */
      Assertions.assertThat(ex.getMessage()).contains("URI " + value);
      catched = true;
    }
    Assertions.assertThat(catched).as("Expected " + IllegalArgumentException.class.getName()).isTrue();
  }

  @Test
  public void getFromUriDcatAp_blank() throws Exception
  {
    /* prepare */
    String value = " ";
    boolean catched = false;

    try
    {
    /* execute */
      DcatApAvailability.getFromUriDcatAp(value);
    }
    catch (IllegalArgumentException ex)
    {
    /* verify */
      Assertions.assertThat(ex.getMessage()).contains("URI " + value);
      catched = true;
    }
    Assertions.assertThat(catched).as("Expected " + IllegalArgumentException.class.getName()).isTrue();
  }

  @Test
  public void getFromUriDcatAp_europa() throws Exception
  {
    /* prepare */
    String value = "http://publications.europa.eu/resource/authority/planned-availability/STABLE";
    boolean catched = false;

    try
    {
    /* execute */
      DcatApAvailability.getFromUriDcatAp(value);
    }
    catch (IllegalArgumentException ex)
    {
    /* verify */
      Assertions.assertThat(ex.getMessage()).contains("URI " + value);
      catched = true;
    }
    Assertions.assertThat(catched).as("Expected " + IllegalArgumentException.class.getName()).isTrue();
  }

  @Test
  public void getFromUriDcatAp() throws Exception
  {
    /* prepare */
    String value = "http://dcat-ap.de/def/plannedAvailability/stable";

    /* execute */
    DcatApAvailability result = DcatApAvailability.getFromUriDcatAp(value);

    /* verify */
    Assertions.assertThat(result).isEqualTo(DcatApAvailability.STABLE);
  }

  @Test
  public void getFromAvailability_null() throws Exception
  {
    /* prepare */
    String value = null;

    /* execute */
    Optional<DcatApAvailability> result = DcatApAvailability.getFromAvailability(value);

    /* verify */
    Assertions.assertThat(result.isPresent()).isFalse();
  }

  @Test
  public void getFromAvailability_blank() throws Exception
  {
    /* prepare */
    String value = " ";

    /* execute */
    Optional<DcatApAvailability> result = DcatApAvailability.getFromAvailability(value);

    /* verify */
    Assertions.assertThat(result.isPresent()).isFalse();
  }

  @Test
  public void getFromAvailability_europa() throws Exception
  {
    /* prepare */
    String value = "http://publications.europa.eu/resource/authority/planned-availability/STABLE";

    /* execute */
    Optional<DcatApAvailability> result = DcatApAvailability.getFromAvailability(value);

    /* verify */
    Assertions.assertThat(result.isPresent()).isTrue();
    Assertions.assertThat(result.get()).isEqualTo(DcatApAvailability.STABLE);
  }

  @Test
  public void getFromAvailability_dcatapde() throws Exception
  {
    /* prepare */
    String value = "http://dcat-ap.de/def/plannedAvailability/stable";

    /* execute */
    Optional<DcatApAvailability> result = DcatApAvailability.getFromAvailability(value);

    /* verify */
    Assertions.assertThat(result.isPresent()).isTrue();
    Assertions.assertThat(result.get()).isEqualTo(DcatApAvailability.STABLE);
  }

}
