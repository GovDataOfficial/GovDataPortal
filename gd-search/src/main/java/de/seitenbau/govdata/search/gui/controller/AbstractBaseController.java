package de.seitenbau.govdata.search.gui.controller;

import static de.seitenbau.govdata.navigation.GovDataNavigation.FRIENDLY_URL_NAME_SEARCHRESULT_PAGE;

import java.io.IOException;
import java.util.Arrays;

import javax.inject.Inject;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletURL;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;

import de.seitenbau.govdata.constants.DetailsRequestParamNames;
import de.seitenbau.govdata.navigation.GovDataNavigation;
import de.seitenbau.govdata.search.common.searchresult.ParameterProcessing;
import de.seitenbau.govdata.search.common.searchresult.PreparedParameters;
import de.seitenbau.govdata.search.common.searchresult.UrlBuilder;

@RequestMapping("VIEW")
public abstract class AbstractBaseController
{
  /** Model key for the Theme Display object. */
  public static final String MODEL_KEY_THEME_DISPLAY = "themeDisplay";

  @Inject
  private GovDataNavigation navigationHelper;

  @Inject
  private ParameterProcessing parameterProcessing;
  
  @Value("${elasticsearch.search.paths.typefiltered}")
  private String[] typeFilteredPaths;

  /**
   * Commonly used Requesthandler for handling form-data submitted by different (but structural equal) forms
   * @param request
   * @param response
   * @param model
   * @throws SystemException
   * @throws PortalException
   * @throws IOException
   */
  @RequestMapping
  public void searchAction(
      ActionRequest request,
      ActionResponse response,
      Model model) throws SystemException, PortalException, IOException
  {
    // get the page we will end on
    ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
    String currentPage = themeDisplay.getLayout().getFriendlyURL().substring(1);
    String target = FRIENDLY_URL_NAME_SEARCHRESULT_PAGE; // default target
    if (Arrays.asList(typeFilteredPaths).contains(currentPage))
    {
      target = currentPage;
    }
    
    // process parameters and create URL
    PreparedParameters prepareParameters =
        parameterProcessing.prepareParameters(request.getParameterMap(), target);
    UrlBuilder urlbuilder = new UrlBuilder(prepareParameters);
    PortletURL redirectUrl = urlbuilder.createRedirectToResultsUrl(navigationHelper, target);
    
    // remove unnecessary parameter which will lead to problems if set
    response.removePublicRenderParameter(DetailsRequestParamNames.PARAM_METADATA);
    
    // send the Redirect :)
    response.sendRedirect(redirectUrl.toString());
  }
}
