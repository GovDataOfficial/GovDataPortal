package de.seitenbau.govdata.search.gui.controller;

import java.util.List;

import javax.inject.Inject;
import javax.portlet.PortletPreferences;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portletmvc4spring.bind.annotation.RenderMapping;

import de.seitenbau.govdata.search.geostate.display.cache.DisplayStateCache;
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
  private DisplayStateCache displayStateCache;

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
    List<StateViewModel> stateList = displayStateCache.getStateList(blockedStatesString);
    model.addAttribute("stateList", stateList);

    log.trace(method + "End");
    return "searchstates";
  }
}