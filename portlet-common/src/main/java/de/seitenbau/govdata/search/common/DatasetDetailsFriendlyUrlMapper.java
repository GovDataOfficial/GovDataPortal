package de.seitenbau.govdata.search.common;

import java.util.HashMap;
import java.util.Map;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.portlet.DefaultFriendlyURLMapper;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.util.Validator;


public class DatasetDetailsFriendlyUrlMapper extends DefaultFriendlyURLMapper
{
  final String renderParameterPrefix = "p_r_p_x_http://portlet.govdata.dev.seitenbau.net_";

  @Override
  public String buildPath(LiferayPortletURL liferayPortletURL)
  {
    Map<String, String> routeParameters = new HashMap<>();

    buildRouteParameters(liferayPortletURL, routeParameters);

    String friendlyURLPath = router.parametersToUrl(routeParameters);

    if (Validator.isNull(friendlyURLPath)) {
      return null;
    }

    // filter namespaced param(s)
    liferayPortletURL.addParameterIncludedInPath(renderParameterPrefix + "metadata");
    
    addParametersIncludedInPath(liferayPortletURL, routeParameters);

    return StringPool.SLASH.concat(
      getMapping()
    ).concat(
      friendlyURLPath
    );
  }
}
