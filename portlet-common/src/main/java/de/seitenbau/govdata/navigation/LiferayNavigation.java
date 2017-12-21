package de.seitenbau.govdata.navigation;

import java.util.List;
import java.util.Objects;

import javax.faces.context.FacesContext;
import javax.portlet.PortletRequest;
import javax.portlet.PortletURL;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.portlet.context.PortletRequestAttributes;

import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.model.Layout;
import com.liferay.portal.model.LayoutModel;
import com.liferay.portal.model.LayoutTypePortlet;
import com.liferay.portal.service.LayoutLocalServiceUtil;
import com.liferay.portal.util.LayoutTypePortletFactoryUtil;
import com.liferay.portlet.PortletURLFactoryUtil;

@Slf4j
@Component
public class LiferayNavigation
{
  /**
   * Determines the  layout by the friendly URL name.
   * @param friendlyUrlName name of the friendly URL
   * @return Layout - <code>null</code> if not available.
   * 
   * @throws SystemException
   */
  public Layout getLayout(String friendlyUrlName) throws SystemException
  {
    if (StringUtils.startsWith(friendlyUrlName, "/"))
    {
      friendlyUrlName = StringUtils.substring(friendlyUrlName, 1);
    }
    
    // http://solvedstack.com/questions/programmatically-get-the-url-of-a-page-in-liferay
    Layout targetLayout = null;
    
    // list all available layouts (== pages)
    List<Layout> layouts = LayoutLocalServiceUtil.getLayouts(QueryUtil.ALL_POS, QueryUtil.ALL_POS);
    
    for (Layout candidate : layouts)
    {
      String friendlyURL = candidate.getFriendlyURL();
      if (("/"+friendlyUrlName).equals(friendlyURL))
      {
        targetLayout = candidate;
        break;
      }
    }
    return targetLayout;
  }
  
  /**
   * Determines the portlet ID by name and Layout.
   * 
   * @param portletName name of the portlet <code>portlet.xml</code>.
   * @param layout Layout to match
   * @return ID of the portlet - <code>null</code> if not available.
   * @throws PortalException
   * @throws SystemException
   */
  public String getPortletOnLayout(String portletName, Layout layout) throws PortalException, SystemException
  {
    LayoutTypePortlet layoutTypePortlet = 
        LayoutTypePortletFactoryUtil
          .create(
              LayoutLocalServiceUtil.getFriendlyURLLayout(
                  layout.getGroupId(), 
                  false, 
                  layout.getFriendlyURL()));
    List<String> portletIdList = layoutTypePortlet.getPortletIds();
    String pid = null;
    for (String candidate : portletIdList)
    {
      if (StringUtils.startsWith(candidate, portletName))
      {
        pid = candidate;
        break;
      }
    }
    if (pid == null)
    {
      log.warn("Portlet ID '{}' for Layout '{}' does not exist. Available portlets: {}",
          portletName,
          layout.getFriendlyURL(),
          portletIdList);
    }
    return pid;
  }
  
  /**
   * Creates a link o a portlet by ID and Layout. 
   * Target is the <b>RENDER</b> phase. 
   * 
   * @param layout
   * @param portletId
   * @return
   */
  public PortletURL createLink(LayoutModel layout, String portletId)
  {
    PortletRequest requestFromContext = getRequestFromContext();
    
    return createLink(requestFromContext, layout, portletId);
  }
  
  /**
   * Creates a link to a portlet by ID and Layout. Target is the <b>RENDER</b> phase.
   * 
   * @param request
   * @param layout
   * @param portletId
   * @return
   */
  protected PortletURL createLink(PortletRequest request, LayoutModel layout, String portletId)
  {
    PortletURL redirectURL = PortletURLFactoryUtil.create(
        request,
        portletId,
        layout.getPlid(), 
        PortletRequest.RENDER_PHASE);
    
    return redirectURL;
  }
  
  protected PortletURL createLink(
      String layoutFriendlyUrlName, String portletName) throws SystemException, PortalException
  {
    PortletRequest requestFromContext = getRequestFromContext();
    return createLink(requestFromContext, layoutFriendlyUrlName, portletName);
  }
  
  public PortletURL createLink(
      PortletRequest request, String layoutFriendlyUrlName, String portletName) throws SystemException,
      PortalException
  {
    PortletURL url = null;
    Layout targetLayout = getLayout(layoutFriendlyUrlName);
    if (targetLayout != null)
    {
      String pid = getPortletOnLayout(portletName, targetLayout);
      url = createLink(request, targetLayout, pid);
    }
    else
    {
      log.warn("Could not found layout for friendlyUrlName: {}", layoutFriendlyUrlName);
      throw new IllegalArgumentException("layout is null!");
    }
    
    return url;
  }

  /**
   * Retrieves the current request from the spring context
   * @return the current request
   */
  public PortletRequest getRequestFromContext()
  {
    PortletRequest requestFromContext;
    RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
    if (Objects.nonNull(attributes))
    {
      PortletRequestAttributes pAttributes = (PortletRequestAttributes) attributes;
      requestFromContext = pAttributes.getRequest();
    }
    else
    {
      requestFromContext =
          (PortletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
    }
    return requestFromContext;
  }
}
