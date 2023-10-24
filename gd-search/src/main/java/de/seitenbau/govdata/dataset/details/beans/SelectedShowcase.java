package de.seitenbau.govdata.dataset.details.beans;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.seitenbau.govdata.clean.StringCleaner;
import de.seitenbau.govdata.common.model.exception.UnknownShowcasePlatformException;
import de.seitenbau.govdata.common.model.exception.UnknownShowcaseTypeException;
import de.seitenbau.govdata.common.showcase.model.ShowcasePlatformEnum;
import de.seitenbau.govdata.common.showcase.model.ShowcaseTypeEnum;
import de.seitenbau.govdata.edit.model.Image;
import de.seitenbau.govdata.edit.model.Keyword;
import de.seitenbau.govdata.edit.model.Platform;
import de.seitenbau.govdata.edit.model.ShowcaseViewModel;
import de.seitenbau.govdata.edit.model.Type;
import de.seitenbau.govdata.odp.registry.model.Category;
import de.seitenbau.govdata.permission.PermissionUtil;
import lombok.Data;

/**
 * The Class SelectedShowcase.
 * 
 * @author sgebhart
 */
@Data
public class SelectedShowcase implements ISelectedObject
{
  /**
   * The logger.
   */
  private final Logger logger = LoggerFactory.getLogger(getClass());

  private String actionUrl;

  /**
   * The metadata.
   */
  private ShowcaseViewModel showcase;

  /**
   * The current user.
   */
  private CurrentUser currentUser;

  private List<String> showcasePlatformsDisplayNames;

  private Map<String, Category> categoryMap;

  /**
   * Constructor
   * @param showcase
   * @param categoryMap
   */
  public SelectedShowcase(ShowcaseViewModel showcase, CurrentUser currentUser,
      Map<String, Category> categoryMap)
  {
    this.showcase = showcase;
    this.currentUser = currentUser;
    this.categoryMap = categoryMap;
    init();
  }

  private void init()
  {
    showcase.sortImages();
    initShowcasePlatformsDisplayNames();
    initImages();
  }

  /**
   * Returns true if the user is allowed to edit Showcases
   * @return
   */
  public boolean userHasEditPermission()
  {
    return PermissionUtil.hasEditShowcasePermission(currentUser.getLiferayUser());
  }

  /**
   * Gibt den Titel als Plain-Text zurück. Eventuell enthaltenes Markup wird herausgefiltert.
   * 
   * @return
   */
  @Override
  public String getTitleOnlyText()
  {
    String result = "";
    String title = showcase.getTitle();
    if (StringUtils.isNotBlank(title))
    {
      result = StringCleaner.trimAndFilterString(title);
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
    String notes = showcase.getNotes();
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
    String notes = showcase.getNotes();
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
    
    String notes = showcase.getNotes();
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
   * Looks up the display name for the primary and the additional showcase types and returns it as
   * comma separated string.
   * @return
   */
  public String getShowcaseTypeDisplayName()
  {
    List<String> types = new ArrayList<>();
    if (Objects.nonNull(showcase.getPrimaryShowcaseType()))
    {
      try
      {
        types.add(ShowcaseTypeEnum.fromField(showcase.getPrimaryShowcaseType().getName()).getDisplayName());
      }
      catch (UnknownShowcaseTypeException e)
      {
        types.add(showcase.getPrimaryShowcaseType().getName());
        logger.warn("Unexcepted ShowcaseTypeEnum {}: {}", showcase.getPrimaryShowcaseType().getName(),
            e.getMessage());
      }
    }
    if (Objects.nonNull(showcase.getAdditionalShowcaseTypes()))
    {
      for (Type t : showcase.getAdditionalShowcaseTypes())
      {
        try
        {
          types.add(ShowcaseTypeEnum.fromField(t.getName()).getDisplayName());
        }
        catch (UnknownShowcaseTypeException e)
        {
          types.add(t.getName());
          logger.warn("Unexcepted ShowcaseTypeEnum {}: {}", t.getName(), e.getMessage());
        }
      }
    }
    return String.join(", ", types);
  }


  /**
   * Looks up the display names for the platforms type and initializes them.
   */
  private void initShowcasePlatformsDisplayNames()
  {
    List<String> platformDisplayNames = new ArrayList<>();

    if (CollectionUtils.isNotEmpty(showcase.getPlatforms()))
    {
      for (Platform p : showcase.getPlatforms())
      {
        String platformDisplayName = p.getName();
        try
        {
          platformDisplayName = ShowcasePlatformEnum.fromField(p.getName()).getDisplayName();
        }
        catch (UnknownShowcasePlatformException e)
        {
          logger.warn("Unexcepted ShowcasePlatformEnum {}: {}", p.getName(), e.getMessage());
        }
        platformDisplayNames.add(platformDisplayName);
      }
    }

    setShowcasePlatformsDisplayNames(platformDisplayNames);
  }

  /**
   * Initializes a list of all images as base64 Strings.
   */
  private void initImages()
  {
    if (showcase.getImages() != null)
    {
      for (Image i : showcase.getImages())
      {
        if (Objects.nonNull(i))
        {
          i.updateThumbnailImage(250, 250);
        }
      }
    }
  }

  /**
   * Checks if there are links to showcases available to display.
   * 
   * @return True if there are links to showcases to display, otherwise false.
   */
  public boolean hasLinksToShowcase()
  {
    return showcase.getLinksToShowcase() != null && !showcase.getLinksToShowcase().isEmpty();
  }

  /**
   * Checks if there are links to datasets available to display.
   * 
   * @return True if there are links to datasets to display, otherwise false.
   */
  public boolean hasLinksToDatasets()
  {
    return showcase.getUsedDatasets() != null && !showcase.getUsedDatasets().isEmpty();
  }

  /**
   * Checks if a link to the sources is available to display.
   * 
   * @return True if there is a link to the sources to display, otherwise false.
   */
  public boolean hasLinkToSourcesUrl()
  {
    return StringUtils.isNotEmpty(showcase.getLinkToSourcesName())
        || StringUtils.isNotEmpty(showcase.getLinkToSourcesUrl());
  }

  /**
   * Checks if a link to the use-case is available to display.
   * 
   * @return True if there is a link to the use-case to display, otherwise false.
   */
  public boolean hasLinkToUseCase()
  {
    return StringUtils.isNotEmpty(showcase.getUsecasePublisher())
        || StringUtils.isNotEmpty(showcase.getUsecaseSourceUrl());
  }

  /**
   * Checks if the current user has the permission to view the showcase.
   * 
   * @return True if the current user has the permission to view the showcase, otherwise false.
   */
  public boolean isShowcaseVisibleForUser()
  {
    if (showcase != null && (!showcase.isPrivate() || userHasEditPermission()))
    {
      return true;
    }
    return false;
  }

  /**
   * Gibt die Kategorien als Liste zurück.
   * 
   * @return
   */
  public List<Category> getCategories()
  {
    List<Category> result = new ArrayList<>();
    if (CollectionUtils.isNotEmpty(showcase.getCategories()))
    {
      for (de.seitenbau.govdata.edit.model.Category cat : showcase.getCategories())
      {
        if (Objects.nonNull(categoryMap))
        {
          Category category = categoryMap.get(cat.getName());
          if (Objects.nonNull(category))
          {
            result.add(category);
          }
        }
      }
    }
    return result;
  }

  /**
   * Gibt die Schlagwörter als Liste zurück.
   * 
   * @return
   */
  @Override
  public List<String> getTagNameList()
  {
    List<String> result = new ArrayList<>();
    if (CollectionUtils.isNotEmpty(showcase.getKeywords()))
    {
      for (Keyword keyword : showcase.getKeywords())
      {
        String tagName = keyword.getName();
        if (StringUtils.isNotEmpty(tagName))
        {
          result.add(StringCleaner.trimAndFilterString(tagName));
        }
      }
    }
    return result;
  }
}
