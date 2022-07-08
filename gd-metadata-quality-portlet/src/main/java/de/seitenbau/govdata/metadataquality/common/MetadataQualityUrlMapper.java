package de.seitenbau.govdata.metadataquality.common;

import java.util.HashMap;
import java.util.Map;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.portlet.DefaultFriendlyURLMapper;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.util.Validator;


public class MetadataQualityUrlMapper extends DefaultFriendlyURLMapper
{
  private static final String RENDER_PARAMETER_PREFIX = "p_r_p_x_http://portlet.govdata.dev.seitenbau.net_";

  @Override
  public String buildPath(LiferayPortletURL liferayPortletURL)
  {
    Map<String, String> routeParameters = new HashMap<>();

    buildRouteParameters(liferayPortletURL, routeParameters);

    String friendlyURLPath = router.parametersToUrl(routeParameters);

    if (Validator.isNull(friendlyURLPath))
    {
      return null;
    }

    // filter namespaced param(s)
    liferayPortletURL.addParameterIncludedInPath(
        RENDER_PARAMETER_PREFIX + MetadataQualityConstants.PORTLET_PARAM_FILTER);

    addParametersIncludedInPath(liferayPortletURL, routeParameters);

    return StringPool.SLASH.concat(
        getMapping()).concat(
            friendlyURLPath);
  }
}

