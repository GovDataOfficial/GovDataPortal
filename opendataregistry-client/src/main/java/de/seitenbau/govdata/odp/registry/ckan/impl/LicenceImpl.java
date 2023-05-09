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

/**
 *
 */
package de.seitenbau.govdata.odp.registry.ckan.impl;

import java.io.Serializable;
import java.util.Objects;

import de.seitenbau.govdata.odp.registry.ckan.json.LicenceBean;
import de.seitenbau.govdata.odp.registry.model.Licence;
import de.seitenbau.govdata.odp.registry.model.LicenceConformance;

/**
 * The Class LicenceImpl.
 *
 * @author sim
 */
public class LicenceImpl implements Licence, Serializable
{
  private static final long serialVersionUID = -6730255931161315851L;

  private final LicenceBean licence;

  /**
   * Constructor with license bean.
   * 
   * @param licence the licence bean,
   */
  public LicenceImpl(LicenceBean licence)
  {
    this.licence = licence;
  }

  @Override
  public String getTitle()
  {
    return licence.getTitle();
  }

  @Override
  public String getName()
  {
    return licence.getId();
  }

  @Override
  public String getUrl()
  {
    return licence.getUrl();
  }

  @Override
  public String getOdConformance()
  {
    return licence.getOd_conformance();
  }

  @Override
  public String getOsdConformance()
  {
    return licence.getOsd_conformance();
  }

  @Override
  public boolean isActive()
  {
    return Objects.equals(licence.getStatus(), "active");
  }

  @Override
  public boolean isOpen()
  {
    return (getOdConformance() != null && getOdConformance().equals(LicenceConformance.APPROVED.getValue()))
        || (getOsdConformance() != null
            && getOsdConformance().equals(LicenceConformance.APPROVED.getValue()));
  }

}
