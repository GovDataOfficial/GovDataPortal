/**
 * Copyright (c) 2012, 2013 Fraunhofer Institute FOKUS | 2017 SEITENBAU GmbH
 *
 * This file is part of Open Data Platform.
 *
 * Open Data Platform is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * Open Data Plaform is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.

 * You should have received a copy of the GNU Affero General Public License
 * along with Open Data Platform.  If not, see <http://www.gnu.org/licenses/agpl-3.0>.
 */

package de.seitenbau.govdata.developerscorner;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;

import de.seitenbau.govdata.navigation.PortletUtil;

/**
 * Controller Class for Developers Corner Portlet
 * 
 */
@Controller
@RequestMapping("VIEW")
public class DevelopersCornerController implements Serializable
{
  /** The logger. */
  private static final Logger LOG = LoggerFactory.getLogger(DevelopersCornerController.class);

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1L;


  @PostConstruct
  public void init()
  {

  }

  @PreDestroy
  public void shutdown()
  {
    
  }

  @RenderMapping
  public String showSearchResults(
      RenderRequest request,
      RenderResponse response,
      Model model) throws PortalException, SystemException
  {
    String url = PortletUtil.getLinkToFusekiSparqlEndpoint();
    model.addAttribute("fusekiQueryUrl", url);
    return "view";
  }

}
