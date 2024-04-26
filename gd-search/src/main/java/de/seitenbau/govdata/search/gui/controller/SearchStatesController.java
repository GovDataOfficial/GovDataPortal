package de.seitenbau.govdata.search.gui.controller;

import static de.seitenbau.govdata.navigation.GovDataNavigation.FRIENDLY_URL_NAME_SEARCHRESULT_PAGE;
import static de.seitenbau.govdata.navigation.GovDataNavigation.PORTLET_NAME_SEARCHRESULT;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portletmvc4spring.bind.annotation.RenderMapping;

import de.seitenbau.govdata.data.api.GovdataResource;
import de.seitenbau.govdata.data.api.dto.DisplayStateDto;
import de.seitenbau.govdata.navigation.GovDataNavigation;
import de.seitenbau.govdata.odp.common.filter.FilterPathUtils;
import de.seitenbau.govdata.odp.common.filter.SearchConsts;
import de.seitenbau.govdata.odp.common.util.GovDataCollectionUtils;
import de.seitenbau.govdata.search.util.states.StateViewModel;
import lombok.extern.slf4j.Slf4j;

/**
 * Controller Class for State-Map.
 * @author sgebhart
 *
 */
@Slf4j
@RequestMapping("VIEW")
public class SearchStatesController extends AbstractBaseController
{
  @Inject
  private GovdataResource govdataResource;

  @Inject
  private GovDataNavigation govDataNavigation;

  /**
   * Display state-search-map.
   * @param request
   * @param response
   * @param model
   * @return
   */
  @RenderMapping
  public String showMap(
      RenderRequest request,
      RenderResponse response,
      Model model)
  {
    final String method = "showMap() : ";
    log.trace(method + "Start");

    // read portlet config
    PortletPreferences portletPreferences = request.getPreferences();
    String blockedStatesString = GetterUtil.getString(portletPreferences.getValue("blockedStates", ""));

    // get state-list
    List<StateViewModel> stateList = getStates(blockedStatesString);


    model.addAttribute("stateList", stateList);

    log.trace(method + "End");
    return "searchstates";
  }

  private List<StateViewModel> getStates(String blockedStatesString)
  {
    List<DisplayStateDto> states = govdataResource.getDisplayStateList(blockedStatesString);

    List<StateViewModel> result = states.stream().map(this::toModel).filter(m -> Objects.nonNull(m))
    .collect(Collectors.toList());

    // Do not cache link URLs, because the links could be corrupted while startup.
    GovDataCollectionUtils.collectionToStream(result).filter(m -> Objects.nonNull(m))
        .forEach(s -> s.setUrl(createLinkUrl(s.getStateKey())));

    return result;
  }

  private String createLinkUrl(String stateKey)
  {
    final String method = "createLinkUrl() : ";
    log.trace(method + "Start");

    try
    {
      String filterParam =
          FilterPathUtils.serializeFilter(SearchConsts.FACET_STATE, stateKey);
      PortletURL redirectUrl = govDataNavigation
          .createLinkForSearchResults(FRIENDLY_URL_NAME_SEARCHRESULT_PAGE, PORTLET_NAME_SEARCHRESULT, "",
              filterParam, "");
      return redirectUrl.toString();
    }
    catch (PortalException | IllegalArgumentException e)
    {
      log.warn("{}Error while creating URL for state key {}: {}", method, stateKey,
          e.getMessage());
    }

    log.trace(method + "End");
    return null;
  }

  /**
   * Mappt ein {@link DisplayStateDto} zu einem {@link StateViewModel}.
   *
   * @param hit
   * @return
   */
  private StateViewModel toModel(DisplayStateDto displayStateDto)
  {
    if (displayStateDto == null)
    {
      return null;
    }
    StateViewModel stateViewModel = new StateViewModel();
    stateViewModel.setId(displayStateDto.getId());
    stateViewModel.setDisplayName(displayStateDto.getDisplayName());
    stateViewModel.setName(displayStateDto.getName());
    stateViewModel.setStateKey(displayStateDto.getStateKey());
    stateViewModel.setBlocked(displayStateDto.isBlocked());
    stateViewModel.setCssClass(displayStateDto.getCssClass());
    stateViewModel.setResultCount(displayStateDto.getResultCount());

    return stateViewModel;

  }
}