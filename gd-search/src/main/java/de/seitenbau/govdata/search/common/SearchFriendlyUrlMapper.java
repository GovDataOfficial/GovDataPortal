package de.seitenbau.govdata.search.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.portlet.DefaultFriendlyURLMapper;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringEncoder;
import com.liferay.portal.kernel.util.URLStringEncoder;
import com.liferay.portal.kernel.util.Validator;

public class SearchFriendlyUrlMapper extends DefaultFriendlyURLMapper
{
  final String[] validParameters = {"q", "f", "s", "boundingbox", "start", "end"};
  final Pattern findParameterRegex = Pattern.compile("/([^/]*)/([^/]*)");
  final String renderParameterPrefix = "p_r_p_x_http://portlet.govdata.dev.seitenbau.net_";
  
  private static final StringEncoder _urlEncoder = new URLStringEncoder();

  @Override
  public String buildPath(LiferayPortletURL liferayPortletURL)
  {

    Map<String, String> routeParameters = new HashMap<String, String>();

    buildRouteParameters(liferayPortletURL, routeParameters);

    StringBuilder sb = new StringBuilder();
    for (String param : validParameters)
    {
      if (routeParameters.containsKey(param))
      {
        if(StringUtils.isNotEmpty(routeParameters.get(param)) || param == "q") {
          String paramVal = _urlEncoder.encode(routeParameters.get(param));
          sb.append(StringPool.SLASH).append(param).append(StringPool.SLASH).append(paramVal);
        }
        
        // remove parameter to signal Liferay "we handled it"
        routeParameters.remove(param);
      }
      // filter namespaced params
      liferayPortletURL.addParameterIncludedInPath(renderParameterPrefix + param);
    }

    String friendlyURLPath = sb.toString();
    
    addParametersIncludedInPath(liferayPortletURL, routeParameters);

    friendlyURLPath = StringPool.SLASH.concat(getMapping()).concat(friendlyURLPath);

    return friendlyURLPath;
  }

  @Override
  public void populateParams(
      String friendlyURLPath, Map<String, String[]> parameterMap,
      Map<String, Object> requestContext)
  {

    friendlyURLPath = friendlyURLPath.substring(getMapping().length() + 1);

    if (friendlyURLPath.endsWith(StringPool.SLASH))
    {
      friendlyURLPath = friendlyURLPath.substring(
          0, friendlyURLPath.length() - 1);
    }

    Map<String, String> routeParameters = new HashMap<String, String>();
    ArrayList<String> remainingParameters = new ArrayList<String>(Arrays.asList(validParameters));
    
    // extract existing valid parameters
    Matcher matcher = findParameterRegex.matcher(friendlyURLPath);
    while(matcher.find()) {
      String name = matcher.group(1).toLowerCase();
      String value = matcher.group(2);
      
      if(ArrayUtils.contains(validParameters, name)) {
        String valueDecoded = _urlEncoder.decode(value);
        routeParameters.put(name,  valueDecoded);
        remainingParameters.remove(name);
      }
    }
    
    // fill remaining valid parameters with null
    for(String param : remainingParameters) {
      routeParameters.put(param, "");
    }
    
    String portletId = getPortletId();

    if (Validator.isNull(portletId))
    {
      return;
    }

    String namespace = PortalUtil.getPortletNamespace(portletId);

    addParameter(namespace, parameterMap, "p_p_id", portletId);

    populateParams(parameterMap, namespace, routeParameters);
  }
}
