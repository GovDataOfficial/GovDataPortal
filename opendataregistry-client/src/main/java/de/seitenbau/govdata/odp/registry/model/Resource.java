/**
 * Copyright (c) 2012, 2013 Fraunhofer Institute FOKUS
 * <p>
 * This file is part of Open Data Platform.
 * <p>
 * Open Data Platform is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * Open Data Plaform is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Affero General Public License
 * along with Open Data Platform.  If not, see <http://www.gnu.org/licenses/agpl-3.0>.
 */

package de.seitenbau.govdata.odp.registry.model;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.Date;
import java.util.List;

/**
 * The Interface Resource.
 *
 * @author sim
 */
public interface Resource
{

  String getId();

  String getUrl();

  void setUrl(String url);

  String getFormat();

  void setFormat(String format);

  /**
   * Helper method to get a abbreviated format string.
   * @return shorter format string.
   */
  String getFormatShort();

  String getDescription();

  void setDescription(String description);

  List<String> getLanguage();

  void setLanguage(List<String> list);

  String getHash();

  void setHash(String hash);

  /**
   * Name of the Resource (not OGD-Standard, but CKAN supported).
   */
  String getName();

  void setName(String name);

  Licence getLicense();

  void setLicense(String license);

  Boolean isOpen();

  String getLicenseAttributionByText();

  void setLicenseAttributionByText(String text);

  String getPlannedAvailability();

  void setPlannedAvailability(String text);

  String getDocumentation();

  List<String> getConformsTo();

  void SetDocumentation(String text);

  void SetDocumentation(List<String> list) throws JsonProcessingException;

  String getDownload_url();

  void setDownload_url(String text);

  String getStatus();

  void setStatus(String text);

  String getMimetype();

  void setMimetype(String text);

  String getRights();

  void setRights(String text);

  String getHash_algorithm();

  void setHash_algorithm(String text);

  Date getLast_modified();

  void setLast_modified(Date date);
}
