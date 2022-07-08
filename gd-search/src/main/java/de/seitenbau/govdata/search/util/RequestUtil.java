package de.seitenbau.govdata.search.util;

import java.util.Arrays;

import javax.portlet.RenderRequest;

import org.apache.commons.lang3.StringUtils;

import de.seitenbau.govdata.navigation.PortletUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class RequestUtil
{
  private RequestUtil()
  {
    // static helper class
  }

  /**
   * Return false if expectedFriendlyUrlMapping does not equal the friendlyUrlMapping of the
   * request.
   * @param request
   * @param expectedFriendlyUrlMapping
   * @return
   */
  public static boolean isResponsible(RenderRequest request, String expectedFriendlyUrlMapping)
  {
    final String method = "isResponsible() : ";
    log.trace(method + "Start");

    String friendlyUrlMapping = PortletUtil.extractFriendlyUrlMappingFromRequestUrl(request);
    log.debug(method + "friendlyUrlMapping: {}", friendlyUrlMapping);
    if (StringUtils.isNotEmpty(friendlyUrlMapping)
        && !expectedFriendlyUrlMapping.equals(friendlyUrlMapping))
    {
      return false;
    }

    log.trace(method + "End");
    return true;
  }

  /**
   * Return true if the friendlyUrlMapping of the request is in blockedFriendlyUrls.
   * @param request
   * @param blockedFriendlyUrls
   * @return
   */
  public static boolean isBlockedUrlMapping(RenderRequest request, String[] blockedFriendlyUrls)
  {
    final String method = "isBlockedUrlMapping() : ";
    log.trace(method + "Start");
    
    String friendlyUrlMapping = PortletUtil.extractFriendlyUrlMappingFromRequestUrl(request);
    if (StringUtils.isNotEmpty(friendlyUrlMapping)
        && Arrays.asList(blockedFriendlyUrls).contains(friendlyUrlMapping))
    {
      log.trace(method + "End True");
      return true;
    }
    log.trace(method + "End False");
    return false;
  }
}
