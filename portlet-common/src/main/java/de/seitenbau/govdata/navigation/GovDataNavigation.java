package de.seitenbau.govdata.navigation;

import java.util.Objects;

import javax.portlet.PortletRequest;
import javax.portlet.PortletURL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.service.BlogsEntryLocalServiceUtil;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalServiceUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.PortalUtil;

import de.seitenbau.govdata.constants.DetailsRequestParamNames;
import de.seitenbau.govdata.constants.QueryParamNames;

@Component
public class GovDataNavigation
{
  /** Friendly URL name of the default search result page. */
  public static final String FRIENDLY_URL_NAME_SEARCHRESULT_PAGE = "suchen";

  /** Friendly URL name of the dataset page. */
  public static final String FRIENDLY_URL_NAME_DATASET_PAGE = "daten";

  /** Friendly URL name of the showroom page. */
  public static final String FRIENDLY_URL_NAME_SHOWROOM_PAGE = "showroom";

  /** Portlet name of the search result portlet. */
  public static final String PORTLET_NAME_SEARCHRESULT = "gdsearchresult";

  private static final String BLOG_PORTLET_NAME = "com_liferay_blogs_web_portlet_BlogsPortlet";

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

  /**
   * Generate a link for search results.
   * @param layoutFriendlyUrlName
   * @param portletName
   * @param q
   * @param filter
   * @param sort
   * @param bbox
   * @param from
   * @param until
   * @return
   * @throws SystemException
   * @throws PortalException
   */
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
    PortletUtil.setParameterInPortletUrl(url, QueryParamNames.PARAM_START, from);
    PortletUtil.setParameterInPortletUrl(url, QueryParamNames.PARAM_END, until);
    return url;
  }

  /**
   * Generate a link for dataset.
   * @param portletName
   * @param metadataName
   * @return
   * @throws SystemException
   * @throws PortalException
   */
  public PortletURL createLinkForMetadata(
      String portletName,
      String metadataName) throws SystemException, PortalException
  {
    return createLinkForMetadata(portletName, metadataName, FRIENDLY_URL_NAME_SEARCHRESULT_PAGE);
  }

  /**
   * Generate a link for dataset.
   * @param portletName
   * @param metadataName
   * @param page
   * @return
   * @throws SystemException
   * @throws PortalException
   */
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

  /**
   * Generate a link tothe edit dataset form.
   * @param portletName
   * @param metadataName
   * @return
   * @throws SystemException
   * @throws PortalException
   */
  public PortletURL createLinkForMetadataEdit(
      String portletName,
      String metadataName) throws SystemException, PortalException
  {
    PortletRequest request = liferayNavigation.getRequestFromContext();
    PortletURL url = liferayNavigation.createLink(request, "bearbeiten", portletName, false);
    if (Objects.nonNull(url))
    {
      PortletUtil.setParameterInPortletUrl(url, DetailsRequestParamNames.PARAM_METADATA, metadataName);
    }
    return url;
  }

  /**
   * Generate a link to the edit showcase form.
   * @param portletName
   * @param showcaseName
   * @return
   * @throws SystemException
   * @throws PortalException
   */
  public PortletURL createLinkForShowcaseEdit(
      String portletName,
      String showcaseName) throws SystemException, PortalException
  {
    PortletRequest request = liferayNavigation.getRequestFromContext();
    PortletURL url = liferayNavigation.createLink(request, "showcase-bearbeiten", portletName);
    PortletUtil.setParameterInPortletUrl(url, DetailsRequestParamNames.PARAM_METADATA, showcaseName);
    return url;
  }

  /**
   * Returns the page url for an article.
   * @param articleId
   * @param groupId
   * @return url of the article
   * @throws PortalException
   */
  public String getJournalArticleUrl(ThemeDisplay themeDisplay, String articleId, Long groupId)
      throws PortalException
  {
    PortletRequest request = liferayNavigation.getRequestFromContext();
    AssetRendererFactory<JournalArticle> assetRendererFactory =
        AssetRendererFactoryRegistryUtil.getAssetRendererFactoryByClass(JournalArticle.class);
    JournalArticle article = JournalArticleLocalServiceUtil.getArticle(groupId, articleId);
    AssetRenderer<JournalArticle> assetRenderer =
        assetRendererFactory.getAssetRenderer(article.getResourcePrimKey());
    String noSuchEntryRedirect = StringPool.BLANK;
    AssetEntry assetEntry = assetRendererFactory.getAssetEntry(
        JournalArticle.class.getName(), article.getResourcePrimKey());
    if (assetEntry != null)
    {
      noSuchEntryRedirect = PortalUtil.getPortalURL(request);
    }

    try
    {
      return assetRenderer.getURLViewInContext(themeDisplay, noSuchEntryRedirect);
    }
    catch (Exception e)
    {
      throw new PortalException(e);
    }
  }

  /**
   * Returns the url for a blog entry
   * @param entryClassPK
   * @param groupId
   * @return url of the blog entry
   * @throws NumberFormatException
   * @throws PortalException
   * @throws SystemException
   */
  public String getBlogEntryUrl(String entryClassPK, Long groupId)
      throws NumberFormatException, PortalException, SystemException
  {
    BlogsEntry blog = BlogsEntryLocalServiceUtil.getBlogsEntry(Long.parseLong(entryClassPK));
    // Check if the blog portlet is available on any site by retrieving plid.
    long plid = LayoutLocalServiceUtil.getDefaultPlid(groupId, false, BLOG_PORTLET_NAME);
    if (plid == 0)
    {
      plid = LayoutLocalServiceUtil.getDefaultPlid(groupId, true, BLOG_PORTLET_NAME);
    }
    if (plid != 0)
    {
      PortletURL url =
          liferayNavigation.createLink(liferayNavigation.getRequestFromContext(), "neues", BLOG_PORTLET_NAME);
      PortletUtil.setParameterInPortletUrl(url, "mvcRenderCommandName", "/blogs/view_entry");
      PortletUtil.setParameterInPortletUrl(url, "urlTitle", blog.getUrlTitle());
      return url.toString();
    }
    return "";
  }

  @Autowired
  public void setLiferayNavigation(LiferayNavigation liferayNavigation)
  {
    this.liferayNavigation = liferayNavigation;
  }
  
  /**
   * Creates the link to the CKAN-Representation of the given metadataId
   * @param metadataId die Metadata ID/Name.
   * @return the URL.
   */
  public String createCkanUrl(String metadataId)
  {
    return PortletUtil.getLinkToDatasetDetailsRawFormatBaseUrl() + metadataId;
  }
}
