package de.seitenbau.govdata.odp.registry.ckan;

import java.util.Arrays;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

public enum DcatApAvailability
{
  /**
   * STABLE
   */
  STABLE("http://publications.europa.eu/resource/authority/planned-availability/STABLE",
      "http://dcat-ap.de/def/plannedAvailability/stable"),

  /**
   * AVAILABLE
   */
  AVAILABLE("http://publications.europa.eu/resource/authority/planned-availability/AVAILABLE",
      "http://dcat-ap.de/def/plannedAvailability/available"),

  /**
   * TEMPORARY
   */
  TEMPORARY("http://publications.europa.eu/resource/authority/planned-availability/TEMPORARY",
      "http://dcat-ap.de/def/plannedAvailability/temporary"),

  /**
   * EXPERIMENTAL
   */
  EXPERIMENTAL("http://publications.europa.eu/resource/authority/planned-availability/EXPERIMENTAL",
      "http://dcat-ap.de/def/plannedAvailability/experimental"),

  /**
   * OP_DATPRO
   */
  OP_DATPRO("http://publications.europa.eu/resource/authority/planned-availability/OP_DATPRO", "");

  private String uri;

  private String uriDcatApDe;

  private DcatApAvailability(String uri, String uriDcatApDe)
  {
    this.uri = uri;
    this.uriDcatApDe = uriDcatApDe;
  }

  public String getUri()
  {
    return uri;
  }

  public String getUriDcatApDe()
  {
    return uriDcatApDe;
  }

  /**
   * Searches for a enum to the given DCAT-AP.de URI.
   * 
   * @param uriDcatApDe DCAT-AP.de URI for planned_availability.
   * @return the enum corresponds to the given URI.
   * @throws IllegalArgumentException If no enum could be found.
   */
  public static DcatApAvailability getFromUriDcatAp(String uriDcatApDe)
  {
    for (DcatApAvailability value : values())
    {
      if (StringUtils.equalsIgnoreCase(value.getUriDcatApDe(), uriDcatApDe))
      {
        return value;
      }
    }
    throw new IllegalArgumentException("Could not find matching enum for DCAT-AP.de URI " + uriDcatApDe);
  }

  /**
   * Searches for an enum with an uri matching the given availability string.
   *
   * @param availability string.
   * @return the enum corresponds to the given URI.
   */
  public static Optional<DcatApAvailability> getFromAvailability(String availability)
  {
    return Arrays.stream(DcatApAvailability.values())
        .filter(a -> StringUtils.equalsIgnoreCase(a.getUri(), availability)
            || StringUtils.equalsIgnoreCase(a.getUriDcatApDe(), availability))
        .findFirst();
  }
}
