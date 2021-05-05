/**
 * Copyright (c) 2021 SEITENBAU GmbH
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

package de.seitenbau.govdata.metadataquality.controller;

import static de.seitenbau.govdata.metadataquality.common.MetadataQualityConstants.DATA;
import static de.seitenbau.govdata.metadataquality.common.MetadataQualityConstants.LABELS;
import static de.seitenbau.govdata.metadataquality.common.MetadataQualityConstants.PERCENTAGE;
import static de.seitenbau.govdata.metadataquality.common.MetadataQualityConstants.PORTLET_PARAM_FILTER;
import static de.seitenbau.govdata.metadataquality.common.MetadataQualityConstants.QUALITY_FEATURES;
import static de.seitenbau.govdata.metadataquality.common.MetadataQualityConstants.TOPS;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.WebKeys;

import de.seitenbau.govdata.cache.LicenceCache;
import de.seitenbau.govdata.cache.OrganizationCache;
import de.seitenbau.govdata.metadataquality.cache.MetricDataCache;
import de.seitenbau.govdata.metadataquality.util.MetricsParser;
import de.seitenbau.govdata.odp.registry.model.Licence;
import de.seitenbau.govdata.odp.registry.model.Organization;
import de.seitenbau.govdata.search.comparator.FilterViewModelDocCountDescComparator;
import de.seitenbau.govdata.search.gui.model.FilterViewModel;

/**
 * Controller Class for Developers Corner Portlet
 * 
 */
@Controller
@RequestMapping("VIEW")
public class MetadataQualityController implements Serializable
{
  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1L;

  @Inject
  private MetricDataCache metricDataCache;

  @Inject
  private OrganizationCache organizationCache;

  @Inject
  private LicenceCache licenceCache;

  private MetricsParser metricsParser = new MetricsParser();

  private static final String TOP_FORMATS = "top_formats";

  private static final String TOP_LICENSES = "top_licenses";

  private static final String DISCOVERABLE = "discoverable_yes";

  private static final String NOT_DISCOVERABLE = "discoverable_no";

  private static final String ACCESSABLE = "accessibility_yes";

  private static final String NOT_ACCESSABLE = "accessibility_no";

  private static final String USABLE = "usability_yes";

  private static final String NOT_USABLE = "usability_no";

  private static final String ALL_PUBLISHERS = "govdata";

  private static final String COLOR_DARK = "rgb(128, 0, 75)";

  private static final String COLOR_LIGHT = "rgb(230, 203, 218)";


  /**
   * Renders default view.
   * 
   * @param request
   * @param response
   * @param model
   * @return
   * @throws PortalException
   * @throws SystemException
   */
  @RenderMapping
  public String showMetadataQualityDefaultView(
      @RequestParam(value = PORTLET_PARAM_FILTER, required = false) String filterName,
      RenderRequest request,
      RenderResponse response,
      Model model) throws PortalException, SystemException
  {
    // get config
    PortletPreferences portletPreferences = request.getPreferences();
    String selectedConfig =
        GetterUtil.getString(portletPreferences.getValue("selectedPage", QUALITY_FEATURES));

    // generate base url
    PortletURL baseUrl = response.createRenderURL();
    baseUrl.setParameter(PORTLET_PARAM_FILTER, ALL_PUBLISHERS);
    String clearFilterUrl = baseUrl.toString();

    // if no publisher is selected show data from all publishers
    String selectedPortal = filterName != null ? filterName : ALL_PUBLISHERS;

    // get metrics data from cache
    metricsParser.setData(metricDataCache.getRawMetricData());

    List<FilterViewModel> metadataOrganizationList =
        retrieveOrganizationFilterList(filterName, baseUrl, clearFilterUrl);

    String selectedPortalName = getPortalDisplayName(selectedPortal, metadataOrganizationList);

    if (selectedConfig.equals(QUALITY_FEATURES))
    {
      // Auffindbarkeit
      model.addAttribute("discoverable_yes", metricsParser.getValuesForType(DISCOVERABLE));
      model.addAttribute("discoverable_no", metricsParser.getValuesForType(NOT_DISCOVERABLE));

      // Zugänglichkeit
      model.addAttribute("accessibility_yes", metricsParser.getValuesForType(ACCESSABLE));
      model.addAttribute("accessibility_no", metricsParser.getValuesForType(NOT_ACCESSABLE));

      // Wiederverwendbarkeit
      model.addAttribute("usability_yes", metricsParser.getValuesForType(USABLE));
      model.addAttribute("usability_no", metricsParser.getValuesForType(NOT_USABLE));
    }
    else if (selectedConfig.equals(TOPS))
    {
      // Top5 Formats
      model.addAttribute("topFormats", metricsParser.getValuesForType(TOP_FORMATS));

      // Top 5 Licences
      model.addAttribute("topLicenses", createLicenceMapWithTranslatedLabels());
    }

    model.addAttribute("organizations", metadataOrganizationList);
    model.addAttribute("clearFilterUrl", clearFilterUrl);
    model.addAttribute("selectedSourcePortal", selectedPortal);
    model.addAttribute("selectedPortalName", selectedPortalName);
    model.addAttribute("themeDisplay", request.getAttribute(WebKeys.THEME_DISPLAY));

    model.addAttribute("colorDarkHue", COLOR_DARK);
    model.addAttribute("colorLightHue", COLOR_LIGHT);
    model.addAttribute("keyLabels", LABELS);
    model.addAttribute("keyData", DATA);
    model.addAttribute("keyDataPercentage", PERCENTAGE);
    model.addAttribute("viewTop", selectedConfig.equals(TOPS));
    model.addAttribute("viewQualityFeatures", selectedConfig.equals(QUALITY_FEATURES));
    model.addAttribute("allPublishers", ALL_PUBLISHERS);

    return "view";
  }

  @SuppressWarnings("unchecked")
  Map<String, Map<String, List<?>>> createLicenceMapWithTranslatedLabels()
  {
    Map<String, Licence> licenceMap = licenceCache.getLicenceMap();
    Map<String, Map<String, List<?>>> resultMap = new HashMap<>();
    Map<String, Map<String, List<?>>> topLicences = metricsParser.getValuesForType(TOP_LICENSES);
    for (Map.Entry<String, Map<String, List<?>>> publisherEntry : topLicences.entrySet())
    {

      Map<String, List<?>> entryResult = new HashMap<>();
      for (Map.Entry<String, List<?>> dataEntry : publisherEntry.getValue().entrySet())
      {
        if (dataEntry.getKey().equals(LABELS))
        {
          List<String> licenceLabelList = new ArrayList<>();
          List<String> licenceIdList = (List<String>) dataEntry.getValue();
          for (String licenceId : licenceIdList)
          {
            // Replace licence id with title as display name
            if (licenceMap.containsKey(licenceId))
            {
              Licence licence = licenceMap.get(licenceId);
              if (Objects.nonNull(licence))
              {
                licenceLabelList.add(licence.getTitle());
              }
            }
            else
            {
              // Licence not found in LicenceCache - use ID instead
              licenceLabelList.add(licenceId);
            }
          }
          entryResult.put(LABELS, licenceLabelList);
        }
        else
        {
          entryResult.put(dataEntry.getKey(), dataEntry.getValue());
        }
      }
      resultMap.put(publisherEntry.getKey(), entryResult);
    }
    return resultMap;
  }

  List<FilterViewModel> retrieveOrganizationFilterList(String filterName, PortletURL baseUrl,
      String clearFilterUrl)
  {
    // get all publishers that contain data
    Map<String, Long> availablePublishers = metricsParser.getAvailablePublishers();

    // get organizations for filter
    List<Organization> organizationList = organizationCache.getOrganizationsSorted();
    List<FilterViewModel> metadataOrganizationList = new ArrayList<>();
    for (Organization orga : organizationList)
    {
      if (!availablePublishers.containsKey(orga.getId()) || orga.getName().equalsIgnoreCase(ALL_PUBLISHERS))
      {
        continue; // filter govdata and publishers without data
      }

      Boolean isActive = false;
      if (filterName != null && orga.getId().equals(filterName))
      {
        isActive = true;
      }

      String actionUrl = clearFilterUrl;
      if (!isActive)
      {
        baseUrl.setParameter(PORTLET_PARAM_FILTER, orga.getId());
        actionUrl = baseUrl.toString();
      }
      Long totalCount = availablePublishers.get(orga.getId());
      FilterViewModel organizationModel = new FilterViewModel(totalCount, orga.getId());
      organizationModel.setDisplayName(orga.getDisplayName());
      organizationModel.setActionURL(actionUrl);
      organizationModel.setActive(isActive);
      metadataOrganizationList.add(organizationModel);
    }
    // Sort list by doc_count descending
    List<FilterViewModel> metadataOrganizationListSorted = metadataOrganizationList
        .stream()
        .sorted(new FilterViewModelDocCountDescComparator())
        .collect(Collectors.toList());
    return metadataOrganizationListSorted;
  }

  String getPortalDisplayName(String portalId, List<FilterViewModel> organizationList)
  {
    if (portalId.equals(ALL_PUBLISHERS))
    {
      return "GovData";
    }
    for (FilterViewModel orga : organizationList)
    {
      if (orga.getName().equals(portalId))
      {
        return orga.getDisplayName();
      }
    }
    return null;
  }

}
