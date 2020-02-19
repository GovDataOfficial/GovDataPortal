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

import com.fasterxml.jackson.databind.JsonNode;

import de.seitenbau.govdata.odp.registry.ckan.json.LicenceBean;
import de.seitenbau.govdata.odp.registry.model.Licence;

/**
 * The Class LicenceImpl.
 *
 * @author sim
 */
public class LicenceImpl implements Licence, Serializable
{
  private static final long serialVersionUID = -6730255931161315851L;

  private final LicenceBean licence;

  private String other;

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
  public String getOther()
  {
    return other;
  }

  @Override
  public boolean isActive() { return licence.getStatus().equals("active"); }

  @Override
  public void setOther(String other)
  {
    this.other = other;
  }

  public static Licence read(JsonNode licence)
  {
    LicenceBean bean = new LicenceBean();

    if (licence != null)
    {
      JsonNode id = licence.get("license_id");
      bean.setId(id != null ? id.textValue() : null);

      // just mirror the id :-(
      bean.setTitle(bean.getId());

      JsonNode url = licence.get("license_url");
      bean.setUrl(url != null ? url.textValue() : null);

      // JsonNode is_free_to_use = licence.get("is_free_to_use");
      // if (is_free_to_use != null) {
      // bean.setIs_okd_compliant(is_free_to_use.getBooleanValue());
      // }
    }

    LicenceImpl impl = new LicenceImpl(bean);
    if (licence != null && licence.get("other") != null)
    {
      impl.setOther(licence.get("other").textValue());
    }

    return impl;
  }

  @Override
  public void setTitle(String title)
  {
    licence.setTitle(title);
  }

  @Override
  public boolean isDomainContent()
  {
    return licence.isDomain_content();
  }

  @Override
  public boolean isDomainData()
  {
    return licence.isDomain_data();
  }

  @Override
  public boolean isDomainSoftware()
  {
    return licence.isDomain_software();
  }

  @Override
  public boolean isOkdCompliant()
  {
    return licence.is_okd_compliant();
  }

  @Override
  public boolean isOsiCompliant()
  {
    return licence.is_osi_compliant();
  }

  @Override
  public boolean isOpen()
  {
    return isOkdCompliant() || isOsiCompliant();
  }

  @Override
  public void setUrl(String url)
  {
    licence.setUrl(url);
  }

}
