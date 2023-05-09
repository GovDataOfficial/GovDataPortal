/**
 * Copyright (c) 2012, 2013 Fraunhofer Institute FOKUS | 2017 SEITENBAU GmbH
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

package de.seitenbau.govdata.dataset.details.beans;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;

import de.fhg.fokus.odp.entities.model.MetadataComment;
import de.seitenbau.govdata.clean.StringCleaner;
import de.seitenbau.govdata.constants.FileExtension;
import de.seitenbau.govdata.edit.model.Link;
import de.seitenbau.govdata.navigation.PortletUtil;
import de.seitenbau.govdata.odp.registry.model.AccessService;
import de.seitenbau.govdata.odp.registry.model.Licence;
import de.seitenbau.govdata.odp.registry.model.Metadata;
import de.seitenbau.govdata.odp.registry.model.MetadataListExtraFields;
import de.seitenbau.govdata.odp.registry.model.Resource;
import de.seitenbau.govdata.odp.registry.model.Tag;
import lombok.Data;

/**
 * The Class SelectedMetadata.
 * 
 * @author rnoerenberg
 */
@Data
public class SelectedMetadata implements ISelectedObject
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
  private List<MetadataCommentWrapperExt> comments;

  /**
   * The metadata contact.
   */
  private CurrentMetadataContact contact;

  /**
   * The actual rating.
   */
  private int currentUserRating = 0; // will remain 0... because we can't get the current users
                                     // rating from CKAN API.

  private String keywords = null;

  private List<Link> linksToShowcases = new ArrayList<>();

  private List<String> tagNameList = new ArrayList<>();

  private String ratingActionUrl;

  private String addCommentResourceURL;

  private String editCommentResourceURL;

  private String deleteCommentResourceURL;

  private Set<String> notAvailableResourceLinks;

  private String organizationName;

  /**
   * Gets the title.
   * 
   * @return the title
   */
  public String getTitle()
  {
    String result = null;
    if (metadata != null)
    {
      result = metadata.getTitle();
    }
    return result;
  }

  /**
   * Gibt den titel als Plain-Text zurück. Eventuell enthaltenes Markup wird herausgefiltert.
   * 
   * @return
   */
  @Override
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
    String result = null;
    if (metadata != null)
    {
      result = metadata.getNotes();
    }
    return result;
  }

  /**
   * Gibt die Notes als Plain-Text zurück. Eventuell enthaltenes Markup wird herausgefiltert.
   * 
   * @return
   */
  @Override
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

  /**
   * Gibt die Notes zurück. Nicht erlaubtes Markup wird herausgefiltert.
   * 
   * @return
   */
  public String getNotesValidated()
  {
    String result = "";
    String notes = getNotes();
    if (StringUtils.isNotBlank(notes))
    {
      result = StringCleaner.trimAndFilterString(notes, StringCleaner.WHITELIST_METADATA_NOTES);
    }
    return result;
  }
  
  /**
   * Prüft, ob mindestens ein Blockelement in den Notes enthalten ist.
   * 
   * @return
   */
  public boolean isBlockelementInNotes()
  {
    String[] badtags = {"li", "ol", "p", "ul"};
    
    String notes = getNotes();
    for (String badtag : badtags)
    {
      if (StringUtils.containsIgnoreCase(notes, "<" + badtag))
      {
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
    String result = null;
    if (metadata != null)
    {
      result = metadata.getUrl();
    }
    return result;
  }

  /**
   * Gets the resources.
   * 
   * @return the resources
   */
  public List<Resource> getResources()
  {
    List<Resource> result = new ArrayList<>();
    if (metadata != null)
    {
      result = metadata.getResources();
    }
    return result;
  }

  /**
   * Gets all data services from the resources in one list
   *
   * @return the data services
   */
  public List<AccessService> getDataServices()
  {
    List<AccessService> result = new ArrayList<>();
    for (Resource resource : this.getResources())
    {
      result.addAll(resource.getAccessServices());
    }
    return result;
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
    {
      return resultList;
    }
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
    int result = 0;
    if (metadata != null)
    {
      double averageRating = this.metadata.getAverageRating();
      logger.debug("getRating() -> " + averageRating);
      result = (int) Math.round(averageRating);
    }
    return result;
  }

  /**
   * Gets the rating count.
   * 
   * @return the rating count
   */
  public int getRatingCount()
  {
    int result = 0;
    if (metadata != null)
    {
      result = this.metadata.getRatingCount();
      logger.debug("getRatingCount() -> " + result);
    }
    return result;
  }

  /**
   * Gibt die URL zur CKAN-API zur Anzeige der JSON-Repräsentation der Metadaten zurück.
   * 
   * @return The URL to CKAN API of this metadata
   */
  public String getMetadataCKANUrl()
  {
    String result = "";
    if (metadata != null)
    {
      result =
          PortletUtil.getLinkToDatasetDetailsRawFormatBaseUrl() + metadata.getName() + FileExtension.RDF;
    }
    return result;
  }

  /**
   * Gibt die Schlagwörter als Komma separierte Liste zurück.
   * 
   * @return
   */
  public String getKeywords()
  {
    if (metadata == null)/* msg 15.09.2014 */
    {
      return "";
    }

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

  /**
   * Gibt die Schlagwörter als Liste zurück.
   * 
   * @return
   */
  @Override
  public List<String> getTagNameList()
  {
    if (CollectionUtils.isEmpty(this.tagNameList))
    {
      List<String> result = new ArrayList<String>();
      if (metadata != null)
      {
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
      }
      setTagNameList(result);
    }
    return this.tagNameList;
  }
  
  public List<String> getUsedDatasets()
  {
    return metadata.getExtraList(MetadataListExtraFields.USED_DATASETS);
  }
  
  public List<String> getGeocodingText()
  {
    return metadata.getExtraList(MetadataListExtraFields.GEOCODING_TEXT);
  }

  /**
   * true if the resources have different licenses.
   */
  public boolean hasMultipleLicenses()
  {
    return metadata.getResourcesLicenses().size() > 1;
  }

  /**
   * Returns the first license of this metadatas resources - or null if no license available.
   */
  public Licence getSingleLicense()
  {
    return metadata.getResourcesLicenses().stream().findFirst().orElse(null);
  }

  /**
   * Gibt das Datum der letzten Änderung zurück.
   * 
   * @return das Datum der letzten Änderung.
   */
  public Date getLastModifiedDate()
  {
    Date result = null;
    if (metadata != null)
    {
      result = metadata.getLastModifiedDate();
    }
    return result;
  }
}
