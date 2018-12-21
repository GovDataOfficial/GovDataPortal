package de.seitenbau.govdata.dcatde;

/**
 * Utilities for displaying DCAT-AP.de values.
 */
public abstract class ViewUtil
{
  private static final String[] FORMAT_PREFIX_URLS = {
      "http://www.iana.org/assignments/media-types/",
      "https://www.iana.org/assignments/media-types/",
      "http://publications.europa.eu/resource/authority/file-type/",
      "https://publications.europa.eu/resource/authority/file-type/",
      "http://publications.europa.eu/mdr/resource/authority/file-type/",
      "https://publications.europa.eu/mdr/resource/authority/file-type/"
  };

  /**
   * Removes the URI part of data types (media-type, file-type, ...).
   *
   * @param longFormat
   *  Original data parameter, i.e. full URL
   * @return
   *  Shortened value (without prefix)
   */
  public static String getShortenedFormatRef(final String longFormat)
  {
    if (longFormat != null)
    {
      for (String prefix : FORMAT_PREFIX_URLS)
      {
        if (longFormat.startsWith(prefix))
        {
          // return the part after the prefix
          return longFormat.substring(prefix.length());
        }
      }
    }

    return longFormat;
  }
}
