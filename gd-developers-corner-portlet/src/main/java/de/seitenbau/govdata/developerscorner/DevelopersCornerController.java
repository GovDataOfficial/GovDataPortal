package de.seitenbau.govdata.developerscorner;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
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
              ArrayUtils.EMPTY_OBJECT_ARRAY, Locale.getDefault()))
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
