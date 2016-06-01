/**
 * Copyright (c) 2012, 2013 Fraunhofer Institute FOKUS
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

/**
 * 
 */
package de.seitenbau.govdata.odr;

import java.util.Properties;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import de.fhg.fokus.odp.registry.ODRClient;
import de.fhg.fokus.odp.registry.ckan.Constants;
import de.fhg.fokus.odp.spi.OpenDataRegistry;

/**
 * Ein Client für den Zugriff auf CKAN über die CKAN-API.
 * 
 * @author rnoerenberg
 * 
 */
@Repository
public class RegistryClient
{
  private ODRClient client;

  @Value("${authenticationKey}")
  private String authKey;

  @Value("${cKANurl}")
  private String ckanUrl;

  @PostConstruct
  public void init()
  {
    client = OpenDataRegistry.getClient();
    Properties props = new Properties();
    props.setProperty(Constants.PROPERTY_NAME_CKAN_AUTHORIZATION_KEY, authKey);
    props.setProperty(Constants.PROPERTY_NAME_CKAN_URL, ckanUrl);
    client.init(props);
  }

  public ODRClient getInstance()
  {
    return client;
  }
}
