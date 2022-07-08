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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portletmvc4spring.bind.annotation.RenderMapping;

import de.seitenbau.govdata.developerscorner.util.SparqlEndpoint;
import de.seitenbau.govdata.navigation.PortletUtil;

/**
 * Controller Class for Developers Corner Portlet
 * 
 */
@Controller
@RequestMapping("VIEW")
public class DevelopersCornerController
{
  /** The logger. */
  private static final Logger LOG = LoggerFactory.getLogger(DevelopersCornerController.class);

  private static final String SPARQL_DATASET_TYPE_METADATA = "ds";

  private static final String SPARQL_DATASET_TYPE_MQA = "mqa";

  @Inject
  private MessageSource messageSource;

  private Map<String, String> availableSparqlEndpoints;

  /**
   * Initializes the required resources by the bean.
   */
  @PostConstruct
  public void init()
  {
    availableSparqlEndpoints = new LinkedHashMap<>();
    addSparqlEndpoint(SPARQL_DATASET_TYPE_METADATA, PortletUtil.getLinkToFusekiSparqlEndpoint());
    addSparqlEndpoint(SPARQL_DATASET_TYPE_MQA, PortletUtil.getLinkToFusekiShaclSparqlEndpoint());
  }

  /**
   * Shows the dashboard.
   * 
   * @param request
   * @param response
   * @param model
   * @return
   * @throws PortalException
   * @throws SystemException
   */
  @RenderMapping
  public String showDashboard(
      RenderRequest request,
      RenderResponse response,
      Model model) throws PortalException, SystemException
  {
    final String method = "showDashboard() : ";
    LOG.trace(method + "Start");

    List<SparqlEndpoint> endpoints = new ArrayList<>();
    for (Map.Entry<String, String> entry : availableSparqlEndpoints.entrySet())
    {
      endpoints.add(SparqlEndpoint.builder().type(entry.getKey())
          .name(messageSource.getMessage("od.sparql.query.endpoint.name." + entry.getKey(),
              ArrayUtils.EMPTY_OBJECT_ARRAY, null))
          .url(entry.getValue()).build());
    }
    model.addAttribute("fusekiEndpoints", endpoints);
    model.addAttribute("themeDisplay", request.getAttribute(WebKeys.THEME_DISPLAY));

    LOG.trace(method + "End");
    return "view";
  }

  private void addSparqlEndpoint(String type, String url)
  {
    if (StringUtils.isNotEmpty(type) && StringUtils.isNotEmpty(url))
    {
      availableSparqlEndpoints.put(type, url);
    }
  }

}
