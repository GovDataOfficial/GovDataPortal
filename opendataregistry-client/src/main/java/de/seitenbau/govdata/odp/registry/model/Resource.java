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

import java.util.Date;
import java.util.List;

/**
 * The Interface Resource.
 *
 * @author sim
 */
public interface Resource
{
  // CHECKSTYLE:OFF
  String getId();

  String getUrl();

  String getFormat();

  /**
   * Helper method to get a abbreviated format string.
   * @return shorter format string.
   */
  String getFormatShort();

  String getDescription();

  String getDescriptionOnlyText();

  List<String> getLanguage();

  String getHash();

  /**
   * Name of the Resource (not OGD-Standard, but CKAN supported).
   */
  String getName();

  String getNameOnlyText();

  Licence getLicense();

  Boolean isOpen();

  String getLicenseAttributionByText();

  String getPlannedAvailability();

  String getAvailability();

  String getDocumentation();

  List<String> getConformsTo();

  String getDownload_url();

  String getStatus();

  String getMimetype();

  String getRights();

  String getHash_algorithm();

  Date getModified();

  Date getIssued();

  List<AccessService> getAccessServices();

}
