package de.seitenbau.govdata.edit.model;

import static de.seitenbau.govdata.edit.constants.EditCommonConstants.DEFAULT_COLUMN_SIZE_STRING;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Pattern.Flag;
import javax.validation.constraints.Size;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.URL;

import de.seitenbau.govdata.clean.StringCleaner;
import de.seitenbau.govdata.constants.CommonConstants;
import de.seitenbau.govdata.edit.constants.EditCommonConstants;
import de.seitenbau.govdata.edit.validator.Categories;
import de.seitenbau.govdata.edit.validator.GeoJSONPolygon;
import de.seitenbau.govdata.edit.validator.StringDate;
import de.seitenbau.govdata.odp.common.util.ImageUtil;
import lombok.Data;

@Data
public class ShowcaseViewModel implements Serializable
{
  private static final long serialVersionUID = 3494116990255710338L;

  private static int MAX_COUNT_IMAGES = 4;

  private Long id;

  @NotEmpty
  @Size(max = DEFAULT_COLUMN_SIZE_STRING, message = "{javax.validation.constraints.Size.max.message}")
  private String title;

  private boolean isPrivate = true;

  @NotEmpty
  private String notes;

  private Type primaryShowcaseType;

  @NotEmpty
  @Size(max = DEFAULT_COLUMN_SIZE_STRING, message = "{javax.validation.constraints.Size.max.message}")
  private String selectedPrimaryShowcaseType;

  private List<Type> additionalShowcaseTypes;

  private List<String> selectedAdditionalShowcaseTypes;

  @Valid
  private List<Image> images;

  @Valid
  private List<Link> linksToShowcase;

  @Valid
  private List<Link> usedDatasets;

  private List<Platform> platforms;

  private List<String> selectedPlatforms;

  @Valid
  private Contact contact;

  @URL
  @Size(max = DEFAULT_COLUMN_SIZE_STRING, message = "{javax.validation.constraints.Size.max.message}")
  private String linkToSourcesUrl;

  @Size(max = DEFAULT_COLUMN_SIZE_STRING, message = "{javax.validation.constraints.Size.max.message}")
  private String linkToSourcesName;

  private List<Category> categories;

  @Categories(message = "{selectedCategories}")
  private List<String> selectedCategories;

  private List<Keyword> keywords;

  @Pattern(regexp = "|[ ]*([a-zäöüß0-9\\-_\\. ])+(,[ ]*[a-zäöüß0-9\\-_\\. ]+)*[ ]*",
      message = "{tags}", flags = {Flag.CASE_INSENSITIVE})
  private String tags;

  @Size(max = DEFAULT_COLUMN_SIZE_STRING, message = "{javax.validation.constraints.Size.max.message}")
  private String url;

  @GeoJSONPolygon(message = "{geoJSONPolygon}")
  private String spatial;

  @StringDate(format=CommonConstants.DATE_PATTERN, regex="\\d{1,2}.\\d{1,2}.\\d{4}", message = "{dateFormat}")
  private String manualShowcaseCreatedDate;

  @StringDate(format=CommonConstants.DATE_PATTERN, regex="\\d{1,2}.\\d{1,2}.\\d{4}", message = "{dateFormat}")
  private String manualShowcaseModifiedDate;

  @Size(max = DEFAULT_COLUMN_SIZE_STRING, message = "{javax.validation.constraints.Size.max.message}")
  private String usecasePublisher;

  @URL
  @Size(max = DEFAULT_COLUMN_SIZE_STRING, message = "{javax.validation.constraints.Size.max.message}")
  private String usecaseSourceUrl;

  private String creatorUserId;

  @StringDate(format=CommonConstants.DATE_PATTERN, regex="\\d{1,2}.\\d{1,2}.\\d{4}", message = "{dateFormat}")
  private String modifyDate;

  @StringDate(format=CommonConstants.DATE_PATTERN, regex="\\d{1,2}.\\d{1,2}.\\d{4}", message = "{dateFormat}")
  private String createDate;

  private boolean inEditing;

  public boolean isNewShowcase()
  {
    return Objects.isNull(this.id);
  }

  /**
   * Prepare data for mapping to entity.
   * 
   * Parse values from selected fields and map them to existing entity-Ids
   * @throws IllegalAccessException
   * @throws InstantiationException
   * @throws SecurityException
   * @throws NoSuchMethodException
   * @throws InvocationTargetException
   * @throws IllegalArgumentException
   */
  public void updateModel() throws InstantiationException, IllegalAccessException, IllegalArgumentException,
      InvocationTargetException, NoSuchMethodException, SecurityException
  {
    updateModel(this);
  }

  /**
   * Prepare data for mapping to entity.
   * 
   * Parse values from selected fields and map them to existing entity-Ids
   * @throws IllegalAccessException
   * @throws InstantiationException
   * @throws SecurityException
   * @throws NoSuchMethodException
   * @throws InvocationTargetException
   * @throws IllegalArgumentException
   */
  public void updateModel(ShowcaseViewModel modelToUpdate)
      throws InstantiationException, IllegalAccessException, IllegalArgumentException,
      InvocationTargetException, NoSuchMethodException, SecurityException
  {
    setInEditing(false);

    cleanNotes();

    // Primary Showcase Type
    Type currentPrimaryType = modelToUpdate.getPrimaryShowcaseType();
    if (getSelectedPrimaryShowcaseType() != null)
    {
      if (currentPrimaryType == null)
      {
        currentPrimaryType = new Type(null, getSelectedPrimaryShowcaseType());
      }
      else if (!getSelectedPrimaryShowcaseType().equals(currentPrimaryType.getName()))
      {
        currentPrimaryType.setName(getSelectedPrimaryShowcaseType());
        for (Type additionalType : modelToUpdate.getAdditionalShowcaseTypes())
        {
          if (getSelectedPrimaryShowcaseType().equals(additionalType.getName()))
          {
            currentPrimaryType = additionalType;
            break;
          }
        }
      }
    }
    setPrimaryShowcaseType(currentPrimaryType);

    // Additional Showcase Types
    List<Type> selectedAdditionalTypes =
        ModelUtil.mergeAdditionalTypesFromDb(getSelectedAdditionalShowcaseTypes(),
            modelToUpdate.getAdditionalShowcaseTypes(), currentPrimaryType);
    setAdditionalShowcaseTypes(selectedAdditionalTypes);

    // Categories
    setCategories(
        ModelUtil.mergeObjectsFromDb(getSelectedCategories(), modelToUpdate.getCategories(), Category.class));

    // Platforms
    setPlatforms(
        ModelUtil.mergeObjectsFromDb(getSelectedPlatforms(), modelToUpdate.getPlatforms(), Platform.class));

    // Keywords
    List<Keyword> selectedKeywords = new ArrayList<>();
    String[] tagList = StringUtils.stripAll(StringUtils.split(this.tags, ","));
    if (tagList != null)
    {
      selectedKeywords = ModelUtil.mergeObjectsFromDb(Arrays.stream(tagList).collect(Collectors.toList()),
          modelToUpdate.getKeywords(), Keyword.class);
    }
    setKeywords(selectedKeywords);

    // Links to apps
    List<Link> selectedAppLinks =
        ModelUtil.mergeLinkObjectsFromDb(getLinksToShowcase(), modelToUpdate.getLinksToShowcase());
    setLinksToShowcase(selectedAppLinks);

    // Links to used datasets
    List<Link> selectedUsedDatasetLinks =
        ModelUtil.mergeLinkObjectsFromDb(getUsedDatasets(), modelToUpdate.getUsedDatasets());
    setUsedDatasets(selectedUsedDatasetLinks);

    // contact
    if (this.contact != null && !contact.shouldStoreContact())
    {
      this.contact = null;
    }

    // images
    List<Image> selectedImages = ModelUtil.mergeImagesFromDb(getImages(), modelToUpdate.getImages());
    setImages(selectedImages);
  }

  /**
   * Prepare data for displaying values in form
   * 
   * Write values into selected-fields
   */
  public void initModel()
  {
    setInEditing(true);

    prepareNotes();

    // Primary Type
    if (primaryShowcaseType != null)
    {
      selectedPrimaryShowcaseType = primaryShowcaseType.getName();
    }

    // Additional Types
    List<String> addShowcaseTypes = new ArrayList<String>();
    if (additionalShowcaseTypes != null)
    {
      for (Type a : additionalShowcaseTypes)
      {
        addShowcaseTypes.add(a.getName());
      }
    }
    setSelectedAdditionalShowcaseTypes(addShowcaseTypes);

    // Categories
    List<String> categoryList = new ArrayList<String>();
    if (categories != null)
    {
      for (Category c : categories)
      {
        categoryList.add(c.getName());
      }
    }
    setSelectedCategories(categoryList);

    // Platforms
    List<String> platformList = new ArrayList<String>();
    if (platforms != null)
    {
      for (Platform p : platforms)
      {
        platformList.add(p.getName());
      }
    }
    setSelectedPlatforms(platformList);

    // Tags|Keywords
    String selectedTags = "";
    if (keywords != null)
    {
      selectedTags =
          StringUtils.join(keywords.stream().map(k -> k.getName()).collect(Collectors.toList()), ",");
    }
    setTags(selectedTags);

    // Images
    if (images != null)
    {
      List<Image> selectedImages = new ArrayList<>();
      for (Image i : images)
      {
        if (ArrayUtils.isNotEmpty(i.getImage()))
        {
          i.updateThumbnailImage();
        }
        selectedImages.add(i);
      }
      setImages(selectedImages);
    }
    fillImages();
    sortImages();

    // contact
    if (Objects.isNull(contact))
    {
      setEmptyContact();
    }

    // Links to apps
    addEmptyLinkToShowcase();

    // Links to used datasets
    addEmptyLinkToUsedDataset();
  }

  /**
   * Initializes all required fields for a new {@link ShowcaseViewModel}.
   * 
   */
  public void initNewModel()
  {
    setInEditing(true);
    setEmptyContact();

    addEmptyLinkToShowcase();
    addEmptyLinkToUsedDataset();

    // Images
    fillImages();
  }

  /**
   * Adds a new empty {@link Link} object to the {@link ShowcaseViewModel#linksToShowcase}.
   * 
   */
  public void addEmptyLinkToShowcase()
  {
    Link toAdd = new Link(null, "", "");
    if (Objects.nonNull(getLinksToShowcase()))
    {
      getLinksToShowcase().add(toAdd);
    }
    else
    {
      List<Link> newListToAdd = new ArrayList<>();
      newListToAdd.add(toAdd);
      setLinksToShowcase(newListToAdd);
    }
  }

  /**
   * Adds a new empty {@link Link} object to the {@link ShowcaseViewModel#usedDatasets}.
   * 
   */
  public void addEmptyLinkToUsedDataset()
  {
    Link toAdd = new Link(null, "", "");
    if (Objects.nonNull(getUsedDatasets()))
    {
      getUsedDatasets().add(toAdd);
    }
    else
    {
      List<Link> newListToAdd = new ArrayList<>();
      newListToAdd.add(toAdd);
      setUsedDatasets(newListToAdd);
    }
  }

  /**
   * Removes the image with the given index from the showcase.
   * 
   * @param index index of the image to remove
   * @throws IOException if an error occurs on deleting temp file from the file system
   */
  public void removeImage(int index) throws IOException
  {
    if (CollectionUtils.size(getImages()) > index)
    {
      List<Image> cleaned = IntStream.range(0, getImages().size())
          .filter(i -> i != index)
          .mapToObj(i -> getImages().get(i))
          .collect(Collectors.toList());
      Image toRemove = getImages().get(index);
      setImages(cleaned);
      fillImages();
      if (ImageUtil.isValidFilename(toRemove.getTmpFileName()))
      {
        Files.deleteIfExists(
            EditCommonConstants.PATH_TEMP_DIR_EDIT_PORTLET.resolve(toRemove.getTmpFileName()));
      }
    }
  }

  private void cleanNotes()
  {
    String notesWithBrTags = RegExUtils.replaceAll(getNotes(), "\\R", "<br>");
    String cleaned =
        StringCleaner.trimAndFilterString(notesWithBrTags, StringCleaner.WHITELIST_METADATA_NOTES);
    setNotes(cleaned);
  }

  private void prepareNotes()
  {
    String notesWithNewLines = RegExUtils.replaceAll(getNotes(), "<br[\\h/]*>", "\n");
    setNotes(notesWithNewLines);
  }

  private void setEmptyContact()
  {
    setContact(new Contact(null, "", "", "", new ContactAddress()));
  }

  private void fillImages()
  {
    if (Objects.isNull(images))
    {
      images = new ArrayList<>();
    }
    int toFillCount = MAX_COUNT_IMAGES - images.size();
    for (int i = toFillCount; i > 0; i--)
    {
      Image toAdd = new Image();
      toAdd.setImageOrderId(MAX_COUNT_IMAGES - (i - 1));
      toAdd.setSourceName("");
      images.add(toAdd);
    }
  }

  /**
   * Sorts the images by their {@link Image#getImageOrderId()} ascending.
   */
  public void sortImages()
  {
    if (CollectionUtils.isNotEmpty(images))
    {
      Collections.sort(images, Comparator.comparingInt(Image::getImageOrderId));
    }
  }
}
