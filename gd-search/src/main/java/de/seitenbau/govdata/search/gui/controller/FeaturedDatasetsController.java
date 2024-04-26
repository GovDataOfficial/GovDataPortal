package de.seitenbau.govdata.search.gui.controller;

import static de.seitenbau.govdata.navigation.GovDataNavigation.FRIENDLY_URL_NAME_DATASET_PAGE;
import static de.seitenbau.govdata.navigation.GovDataNavigation.PORTLET_NAME_SEARCHRESULT;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portletmvc4spring.bind.annotation.RenderMapping;

import de.seitenbau.govdata.navigation.GovDataNavigation;
import de.seitenbau.govdata.odp.common.filter.SearchConsts;
import de.seitenbau.govdata.search.api.SearchResource;
import de.seitenbau.govdata.search.api.model.search.dto.SearchResultContainer;
import de.seitenbau.govdata.search.gui.mapper.SearchResultsViewMapper;
import de.seitenbau.govdata.search.gui.model.HitViewModel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("VIEW")
public class FeaturedDatasetsController extends AbstractBaseController
{
  private static final String MODEL_SHOW_DATAET_KEY = "showDatasetButton";

  private static final String MODEL_HITS_KEY = "hits";

  private static final String MODEL_DATASET_COUNT_KEY = "count";

  private static final String MODEL_LINK_DATASETS_KEY = "linkToDatasets";

  private static final String DATASET_PAGE = "/daten";

  private static final String[] SEARCH_INDEXES = {"govdata-ckan-de"};

  private static final Integer NUMBER_OF_FALLBACK_DATASETS = 3;

  @Inject
  private SearchResource searchResource;

  @Inject
  private GovDataNavigation gdNavigation;

  @Inject
  private SearchResultsViewMapper searchResultsMapper;

  /**
   * Display the selected featured datasets.
   * @param request
   * @param response
   * @param model
   * @return
   * @throws SystemException
   * @throws PortalException
   */
  @RenderMapping
  public String showFeaturedDatasets(
      RenderRequest request,
      RenderResponse response,
      Model model) throws SystemException, PortalException
  {
    final String method = "showFeaturedDatasets() : ";

    ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
    PortletPreferences portletPreferences = request.getPreferences();

    // read values from portlet configuration
    String selectedFeaturedDatasetId1 = GetterUtil.getString(portletPreferences.getValue("datasetId1", ""));
    String selectedFeaturedDatasetId2 = GetterUtil.getString(portletPreferences.getValue("datasetId2", ""));
    String selectedFeaturedDatasetId3 = GetterUtil.getString(portletPreferences.getValue("datasetId3", ""));
    boolean showDatasetButton = GetterUtil.getBoolean(
        portletPreferences.getValue("showDatasetButton", StringPool.TRUE));

    // prepare dataset ids
    List<String> selectedIdList = new ArrayList<>();
    if (!selectedFeaturedDatasetId1.isEmpty())
    {
      selectedIdList.add(selectedFeaturedDatasetId1);
    }
    if (!selectedFeaturedDatasetId2.isEmpty())
    {
      selectedIdList.add(selectedFeaturedDatasetId2);
    }
    if (!selectedFeaturedDatasetId3.isEmpty())
    {
      selectedIdList.add(selectedFeaturedDatasetId3);
    }
    log.debug(method + "Got " + selectedIdList.size() + " dataset-names.");

    List<HitViewModel> hitViewModelList = new ArrayList<>();
    if (selectedIdList.size() > 0)
    {
      // elastic search for datasets
      SearchResultContainer result =
          searchResource.getSingleSearch(selectedIdList, Arrays.asList(SEARCH_INDEXES));

      // map results
      List<HitViewModel> hitViewModelListResult = searchResultsMapper.mapHitDtoListToHitsViewModelList(
          result.getHits(), themeDisplay, DATASET_PAGE, false);

      // order results
      for (String id : selectedIdList)
      {
        HitViewModel hitViewModel =
            hitViewModelListResult.stream().filter(hit -> id.equals(hit.getName())).findFirst().orElse(null);
        if (hitViewModel != null)
        {
          hitViewModelList.add(hitViewModel);
        }
        else
        {
          log.debug(method + "Dataset " + id + " was not found in elastic search!");
        }
      }
    }
    log.debug(method + "Found " + hitViewModelList.size() + "/" + selectedIdList.size() + " datasets.");

    if (hitViewModelList.size() == 0)
    {
      // no datasets found. Use fallback: Load latest 3 datasets
      log.debug(method + "Use fallback: Load the three latest datasets");

      // search in index
      SearchResultContainer result =
          searchResource.getSearchLatest(NUMBER_OF_FALLBACK_DATASETS, SearchConsts.TYPE_DATASET);

      // map result
      hitViewModelList = searchResultsMapper.mapHitDtoListToHitsViewModelList(
          result.getHits(), themeDisplay, DATASET_PAGE, false);
      log.debug(method + "Found " + hitViewModelList.size() + " Datasets to display.");
    }

    PortletURL redirectUrl = gdNavigation.createLinkForSearchResults(FRIENDLY_URL_NAME_DATASET_PAGE,
        PORTLET_NAME_SEARCHRESULT, "");

    model.addAttribute(MODEL_KEY_THEME_DISPLAY, themeDisplay);
    model.addAttribute(MODEL_HITS_KEY, hitViewModelList);
    model.addAttribute(MODEL_DATASET_COUNT_KEY, hitViewModelList.size());
    model.addAttribute(MODEL_SHOW_DATAET_KEY, showDatasetButton);
    model.addAttribute(MODEL_LINK_DATASETS_KEY, redirectUrl.toString());

    return "featureddatasets";
  }

}
