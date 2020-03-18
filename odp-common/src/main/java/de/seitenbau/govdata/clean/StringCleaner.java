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

package de.seitenbau.govdata.clean;

import java.text.Normalizer;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Cleaner;
import org.jsoup.safety.Whitelist;

/**
 * Enthält Methoden zum Filtern von Strings.
 * 
 * @author rnoerenberg
 *
 */
public abstract class StringCleaner
{
  /**
   * Eine Whitelist von HTML-Elementen und -Attributen für die Metadaten-Beschreibung.
   */
  public static final Whitelist WHITELIST_METADATA_NOTES = new Whitelist()
      .addTags("a", "li", "ol", "p", "ul")
      .addAttributes("a", "href")
      .addProtocols("a", "href", "ftp", "http", "https", "mailto")
      .addEnforcedAttribute("a", "rel", "nofollow")
      .addEnforcedAttribute("a", "target", "_blank");

  public final static int CKAN_PACKAGE_NAME_MAX_LENGTH = 100;

  public final static int CKAN_PACKAGE_NAME_MIN_LENGTH = 2;

  /**
   * Entfernt aus dem übergebenen String jeglichen Markup, trimmt den String und gibt diesen zurück.
   * 
   * @param value der zu säubernde String.
   * @return der gesäuberte String oder null, falls der übergebene String null ist.
   */
  public static String trimAndFilterString(String value)
  {
    String result = null;
    if (value != null)
    {
      result = Jsoup.parse(value).text();
    }
    return result;
  }

  /**
   * Wendet auf den übergebenen String die übergebene Whitelist an und gibt diesen zurück. Aus dem
   * String wird jeglicher Markup entfernt, ausgenommen der in der Whitelist definierte Markup.
   * 
   * @param value der zu säubernde String.
   * @param whitelist die Whitlelist an erlaubtem Markup.
   * @return der gesäuberte String oder der String selbst, falls die Whitelist null ist, oder null,
   *         falls der übergebene String null ist.
   */
  public static String trimAndFilterString(String value, Whitelist whitelist)
  {
    String result = value;
    if (value != null && whitelist != null)
    {
      Cleaner cleaner = new Cleaner(whitelist);
      Document dirty = Jsoup.parse(value);
      result = cleaner.clean(dirty).body().html();
    }
    return result;
  }

  /**
   * Erzeugt basierend auf dem übergebenen String einen gültigen Package-Namen für CKAN. Es wird die
   * Methode "munge_title_to_name" in https://github.com/ckan/ckan/blob/master/ckan/lib/munge.py
   * nachimplementiert, da diese über die neue und einzige Action-API nicht mehr exponiert wird.
   * 
   * @param title der Titel, aus dem ein gültiger Package-Name für CKAN erzeugt wird.
   * @return ein gültiger Package-Name, basierend auf dem übergebenen String.
   */
  public static String mungeTitleToName(String title)
  {
    String munge = Normalizer.normalize(title, Normalizer.Form.NFD);
    // convert spaces and separators
    munge = munge.replaceAll("[ .:/]", "-");
    // take out not-allowed characters
    munge = munge.replaceAll("[^a-zA-Z0-9-_]", "").toLowerCase();
    // remove doubles
    munge = munge.replaceAll("-+", "-");
    // remove leading or trailing hyphens
    munge = StringUtils.strip(munge, "-");
    // fit length
    if (StringUtils.length(munge) > CKAN_PACKAGE_NAME_MAX_LENGTH)
    {
      munge = StringUtils.substring(munge, 0, CKAN_PACKAGE_NAME_MAX_LENGTH);
    }
    else if (StringUtils.length(munge) < CKAN_PACKAGE_NAME_MIN_LENGTH)
    {
      munge = StringUtils.rightPad(munge, CKAN_PACKAGE_NAME_MIN_LENGTH, "_");
    }
    return munge;
  }
}
