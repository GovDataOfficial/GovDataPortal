package de.seitenbau.govdata.search.common;

import java.util.Map;

import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.util.PortalUtil;

public class AtomFeedUrlMapper extends SearchFriendlyUrlMapper
{
  @Override
  public void populateParams(String friendlyURLPath, Map<String, String[]> parameterMap,
      Map<String, Object> requestContext)
  {
    super.populateParams(friendlyURLPath, parameterMap, requestContext);
  
    
    String portletId = getPortletId();

    if (Validator.isNull(portletId))
    {
      return;
    }
   
    
    String namespace = PortalUtil.getPortletNamespace(portletId);
    
    addParameter(namespace, parameterMap, "p_p_resource_id", "atomfeed");
    addParameter(namespace, parameterMap, "p_p_lifecycle", 2);
    addParameter(namespace, parameterMap, "p_p_state", "normal");
    addParameter(namespace, parameterMap, "p_p_mode", "view");
    addParameter(namespace, parameterMap, "p_p_cacheability", "cacheLevelPage");
    addParameter(namespace, parameterMap, "p_p_col_id", "column-1");
    addParameter(namespace, parameterMap, "p_p_col_count", 1);
  }
}
