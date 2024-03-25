package de.seitenbau.govdata.odp.registry.ckan;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The Enum HVDCategory.
 *
 * @author youalad
 */
public enum HVDCategory
{
  /**
   * Meteorological
   */
  MET("http://data.europa.eu/bna/c_164e0bf5"),

  /**
   * Companies and company ownership
   */
  CCO("http://data.europa.eu/bna/c_a9135398"),

  /**
   * Geospatial
   */
  GEO("http://data.europa.eu/bna/c_ac64a52d"),

  /**
   * Mobility
   */
  MOB("http://data.europa.eu/bna/c_b79e35eb"),

  /**
   * Earth observation and environment
   */
  EOE("http://data.europa.eu/bna/c_dd313021"),

  /**
   * Statistics
   */
  STA("http://data.europa.eu/bna/c_e1da4e07");

  private String uri;

  public String getUri()
  {
    return uri;
  }

  private HVDCategory(String uri)
  {
    this.uri = uri;
  }

  /**
   * Get {@link HVDCategory} from uri.
   * 
   * @param uri of the HVD category
   * @return the corresponding {@link HVDCategory} to the <b>uri</b>
   */
  public static HVDCategory fromUri(String uri)
  {
    for (HVDCategory platform : HVDCategory.values())
    {
      if (platform.getUri().equals(uri))
      {
        return platform;
      }
    }

    throw new IllegalArgumentException("Could not find matching enum for HVD category URI " + uri);
  }

  /**
   * Get {@link List<HVDCategory>} from a list of uris.
   *
   * @param uris list of the HVD category
   * @return the corresponding {@link List<HVDCategory>} to the <b>uris</b>
   */
  public static List<HVDCategory> fromUris(List<String> uris)
  {
    if (uris != null)
    {
      return uris.stream().map(hvdCat -> HVDCategory.fromUri(hvdCat)).collect(Collectors.toList());
    }
    return new ArrayList<>();
  }
}
