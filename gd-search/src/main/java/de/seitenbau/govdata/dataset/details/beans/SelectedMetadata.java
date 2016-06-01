/**
 * Copyright (c) 2012, 2013 Fraunhofer Institute FOKUS
 *
 * This file is part of Open Data Platform.
 *
 * Open Data Platform is free software: you can redistribute it and/or modify it under the terms of
 * the GNU Affero General Public License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 * 
 * Open Data Plaform is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with Open Data
 * Platform. If not, see <http://www.gnu.org/licenses/agpl-3.0>.
 */

/**
 *
 */
package de.seitenbau.govdata.dataset.details.beans;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import lombok.Data;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.service.UserLocalServiceUtil;

import de.fhg.fokus.odp.entities.model.MetadataComment;
import de.fhg.fokus.odp.registry.model.Application;
import de.fhg.fokus.odp.registry.model.Metadata;
import de.fhg.fokus.odp.registry.model.Resource;
import de.fhg.fokus.odp.registry.model.Tag;
import de.seitenbau.govdata.clean.StringCleaner;

/**
 * The Class SelectedMetadata.
 * 
 * @author rnoerenberg
 */
@Data
public class SelectedMetadata
{
  /**
   * The logger.
   */
  private final Logger logger = LoggerFactory.getLogger(getClass());

  private String actionUrl;

  /**
   * The metadata.
   */
  private Metadata metadata;

  /**
   * The current user.
   */
  private CurrentUser currentUser;

  /**
   * The comments.
   */
  private List<MetadataCommentWrapper> comments;

  /**
   * The metadata contact.
   */
  private CurrentMetadataContact contact;

  /**
   * The actual rating.
   */
  private int currentUserRating = 0; // will remain 0... because we can't get the current users rating from CKAN API.

  private String keywords = null;

  private List<String> tagNameList = new ArrayList<String>();

  private String ratingActionUrl;

  private String addCommentResourceURL;

  private String editCommentResourceURL;

  private String deleteCommentResourceURL;

  private Set<String> notAvailableResourceLinks;

  /**
   * Gets the title.
   * 
   * @return the title
   */
  public String getTitle()
  {
    return metadata == null ? null : metadata.getTitle();
  }

  public String getTitleOnlyText()
  {
    String result = "";
    String title = getTitle();
    if (StringUtils.isNotBlank(title))
    {
      result = StringCleaner.trimAndFilterString(title);
    }
    return result;
  }

  /**
   * Gets the notes.
   * 
   * @return the notes
   */
  public String getNotes()
  {
    return metadata == null ? null : metadata.getNotes();
  }

  public String getNotesOnlyText()
  {
    String result = "";
    String notes = getNotes();
    if (StringUtils.isNotBlank(notes))
    {
      result = StringCleaner.trimAndFilterString(notes);
    }
    return result;
  }

  public String getNotesValidated()
  {
    String result = "";
    String notes = getNotes();
    if (StringUtils.isNotBlank(notes))
    {
      result = StringCleaner.trimAndFilterString(notes, StringCleaner.metadataNotes);
    }
    return result;
  }
  
  public boolean isBlockelementInNotes() {
    String[] badtags = {"li", "ol", "p", "ul"};
    
    String notes = getNotes();
    for(String badtag : badtags) {
      if(StringUtils.containsIgnoreCase(notes, "<" + badtag)) {
        return true;
      }
    }
    
    return false;
  }

  /**
   * Gets the url.
   * 
   * @return the url
   */
  public String getUrl()
  {
    return metadata == null ? null : metadata.getUrl();
  }

  /**
   * Gets the resources.
   * 
   * @return the resources
   */
  public List<Resource> getResources()
  {
    return metadata == null ? null : metadata.getResources();
  }

  /**
   * Gets the comment author.
   * 
   * @param comment the comment
   * @return the comment author
   * @throws PortalException the portal exception
   * @throws SystemException the system exception
   */
  public String getCommentAuthor(MetadataComment comment)
      throws PortalException, SystemException
  {
    return UserLocalServiceUtil.getUser(comment.getUserLiferayId())
        .getScreenName();
  }

  /**
   * Gets the rating. Number of 1s represents the average rating.
   * 
   * @return the rating
   */
  public List<Integer> getRatingAsList()
  {
    List<Integer> resultList = new ArrayList<Integer>();
    if (metadata == null)/* msg 16.09.2014 */
      return resultList;
    double averageRating = this.metadata.getAverageRating();
    logger.debug("getRating() -> " + averageRating);
    int ratingListCount = (int) averageRating;

    for (int i = 0; i < ratingListCount; i++)
    {
      resultList.add(1);
    }

    for (int i = ratingListCount; i < 5; i++)
    {
      resultList.add(0);
    }
    return resultList;
  }

  /**
   * Gets the rating.
   * 
   * @return the rating
   */
  public int getRating()
  {
    double averageRating = this.metadata.getAverageRating();
    logger.debug("getRating() -> " + averageRating);
    return (int) Math.round(averageRating);
  }

  /**
   * Gets the rating count.
   * 
   * @return the rating count
   */
  public int getRatingCount()
  {
    int ratingCount = this.metadata.getRatingCount();
    logger.debug("getRatingCount() -> " + ratingCount);
    return ratingCount;
  }

  public Application asApplication()
  {
    return (Application) metadata;
  }

  /**
   * 
   * @return The URL to CKAN API of this metadata
   */
  public String getMetadataCKANUrl()
  {
    // TODO : Info / Zusammenbauen an zentraler Stelle ablegen. Konstante, etc.
    return PropsUtil.get("cKANurlFriendly") + "api/rest/dataset/"
        + metadata.getName();
  }

  public String getKeywords()
  {
    if (metadata == null)/* msg 15.09.2014 */
      return "";

    if (this.keywords == null)
    {
      List<String> result = new ArrayList<String>();
      List<Tag> tags = metadata.getTags();
      for (Iterator<Tag> iterator = tags.iterator(); iterator.hasNext();)
      {
        Tag tag = iterator.next();
        result.add(tag.getName());
      }
      setKeywords(StringUtils.join(result, ","));
    }

    return this.keywords;
  }

  public List<String> getTagNameList()
  {
    if (CollectionUtils.isEmpty(this.tagNameList))
    {
      List<String> result = new ArrayList<String>();
      List<Tag> tagList = metadata.getTags();
      if (CollectionUtils.isNotEmpty(tagList))
      {
        for (Tag tag : tagList)
        {
          String tagName = tag.getName();
          if (StringUtils.isNotEmpty(tagName))
          {
            result.add(StringCleaner.trimAndFilterString(tagName));
          }
        }
      }
      setTagNameList(result);
    }
    return this.tagNameList;
  }
}
