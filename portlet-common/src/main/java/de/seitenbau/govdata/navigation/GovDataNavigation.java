package de.seitenbau.govdata.navigation;

import java.util.List;

import javax.portlet.PortletRequest;
import javax.portlet.PortletURL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.model.Layout;
import com.liferay.portal.service.LayoutLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.blogs.model.BlogsEntry;
import com.liferay.portlet.blogs.service.BlogsEntryLocalServiceUtil;
import com.liferay.portlet.journal.service.JournalContentSearchLocalServiceUtil;

import de.seitenbau.govdata.constants.DetailsRequestParamNames;
import de.seitenbau.govdata.constants.QueryParamNames;

@Component
public class GovDataNavigation
{
  @Autowired
  private LiferayNavigation liferayNavigation;

  /**
   * Creates a (render-) link for the search result page
   * (partial parameters)
   */
  public PortletURL createLinkForSearchResults(
      String layoutFriendlyUrlName,
      String portletName,
      String q) throws SystemException, PortalException
  {
    return createLinkForSearchResults(layoutFriendlyUrlName, portletName, q, null, null);
  }

  /**
   * Creates a (render-) link for the search result page
   * (partial parameters)
   */
  public PortletURL createLinkForSearchResults(
      String layoutFriendlyUrlName,
      String portletName,
      String q,
      String filter,
      String sort) throws SystemException, PortalException
  {
    return createLinkForSearchResults(layoutFriendlyUrlName, portletName, q, filter, sort, null);
  }

  /**
   * Creates a (render-) link for the search result page
   * (full parameters)
   * 
   * @param layoutFriendlyUrlName Layout (page) friendly URL name of the search result
   * @param portletName Name of the portlet on the target page
   * @param q Search phrase
   * @param filter serialized list of active filters
   * @param sort name of the sorting to use
   * @param bbox boundingbox for geo search
   * @return Link to the search result page
   * 
   * @throws SystemException
   * @throws PortalException
   */
  public PortletURL createLinkForSearchResults(
      String layoutFriendlyUrlName,
      String portletName,
      String q,
      String filter,
      String sort,
      String bbox) throws SystemException, PortalException
  {
    return createLinkForSearchResults(layoutFriendlyUrlName, portletName, q, filter, sort, bbox, null, null);
  }
  
  public PortletURL createLinkForSearchResults(
      String layoutFriendlyUrlName,
      String portletName,
      String q,
      String filter,
      String sort,
      String bbox,
      String from,
      String until) throws SystemException, PortalException
  {
    PortletRequest request = liferayNavigation.getRequestFromContext();
    PortletURL url = liferayNavigation.createLink(request, layoutFriendlyUrlName, portletName);
    PortletUtil.setParameterInPortletUrl(url, QueryParamNames.PARAM_PHRASE, q);
    PortletUtil.setParameterInPortletUrl(url, QueryParamNames.PARAM_FILTER, filter);
    PortletUtil.setParameterInPortletUrl(url, QueryParamNames.PARAM_SORT, sort);
    PortletUtil.setParameterInPortletUrl(url, QueryParamNames.PARAM_BOUNDINGBOX, bbox);
    PortletUtil.setParameterInPortletUrl(url, QueryParamNames.PARAM_FROM, from);
    PortletUtil.setParameterInPortletUrl(url, QueryParamNames.PARAM_UNTIL, until);
    return url;
  }
  
  public PortletURL createLinkForMetadata(
      String portletName,
      String metadataName) throws SystemException, PortalException
  {
    return createLinkForMetadata(portletName, metadataName, "suchen");
  }
  
  public PortletURL createLinkForMetadata(
      String portletName,
      String metadataName,
      String page) throws SystemException, PortalException
  {
    PortletRequest request = liferayNavigation.getRequestFromContext();
    PortletURL url = liferayNavigation.createLink(request, page, portletName);
    PortletUtil.setParameterInPortletUrl(url, DetailsRequestParamNames.PARAM_METADATA, metadataName);
    return url;
  }
  
  public PortletURL createLinkForMetadataEdit(
      String portletName,
      String metadataName) throws SystemException, PortalException
  {
    PortletRequest request = liferayNavigation.getRequestFromContext();
    PortletURL url = liferayNavigation.createLink(request, "bearbeiten", portletName);
    PortletUtil.setParameterInPortletUrl(url, DetailsRequestParamNames.PARAM_METADATA, metadataName);
    return url;
  }

  /**
   * Returns the layout (page) url for an article. If the article is available on more then one layout, the
   * url of the first layout found will be returned
   * @param themeDisplay
   * @param articleId
   * @param groupId
   * @return url of the layout the article is displayed on
   * @throws SystemException
   * @throws PortalException
   */
  public String getArticleLayoutUrl(ThemeDisplay themeDisplay, String articleId, Long groupId)
      throws SystemException,
      PortalException
  {
    List<Long> hitLayoutIds = JournalContentSearchLocalServiceUtil.getLayoutIds(groupId, false, articleId);
    String layoutUrl = "";
    if (hitLayoutIds.size() > 0)
    {
      Long hitLayoutId = hitLayoutIds.get(0);
      Layout hitLayout = LayoutLocalServiceUtil.getLayout(
          groupId, false, hitLayoutId.longValue());
      layoutUrl = PortalUtil.getLayoutURL(hitLayout, themeDisplay);
    }
    return layoutUrl;
  }

  /**
   * Returns the url for a blog entry
   * @param entryClassPK
   * @param portletId
   * @param groupId
   * @return url of the blog entry
   * @throws NumberFormatException
   * @throws PortalException
   * @throws SystemException
   */
  public String getBlogEntryUrl(String entryClassPK, String portletId, Long groupId)
      throws NumberFormatException, PortalException, SystemException
  {
    BlogsEntry blog = BlogsEntryLocalServiceUtil.getBlogsEntry(Long.parseLong(entryClassPK));
    long plid = LayoutLocalServiceUtil.getDefaultPlid(groupId, false, portletId);
    if (plid == 0)
    {
      plid = LayoutLocalServiceUtil.getDefaultPlid(groupId, true, portletId);
    }
    if (plid != 0)
    {
      PortletURL url = liferayNavigation.createLink(liferayNavigation.getRequestFromContext(), "neues", portletId);
      PortletUtil.setParameterInPortletUrl(url, "struts_action", "/blogs/view_entry");
      PortletUtil.setParameterInPortletUrl(url, "urlTitle", blog.getUrlTitle());
      return url.toString();
    }
    return "";
  }

  public void setLiferayNavigation(LiferayNavigation liferayNavigation) {
    this.liferayNavigation = liferayNavigation;
  }
  
  /**
   * Creates the link to the CKAN-Represenation of the given metadataId
   * @param metadataId
   * @return
   */
  public String createCkanUrl(String metadataId) {
    return PropsUtil.get("cKANurlFriendly") + "api/rest/dataset/" + metadataId;
  }
}
