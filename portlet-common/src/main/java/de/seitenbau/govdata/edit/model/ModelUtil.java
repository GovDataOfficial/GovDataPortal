package de.seitenbau.govdata.edit.model;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import de.seitenbau.govdata.edit.constants.EditCommonConstants;
import de.seitenbau.govdata.odp.common.util.ImageUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * Utility class for updating the model.
 * 
 * @author rnoerenberg
 */
@Slf4j
public final class ModelUtil
{
  private ModelUtil()
  {
    // private
  }
  /**
   * Merges values from the database and values from the HTML form together. Keeps objects from DB
   * if the content is equal.
   * 
   * @param selectedValues values from the HTML form
   * @param objectsInDb values from the DB
   * @param clazz the target class for creating new objects
   * @return the merged list
   * @throws InstantiationException
   * @throws IllegalAccessException
   */
  public static <T extends ISimpleDbRelationValue> List<T> mergeObjectsFromDb(List<String> selectedValues,
      List<T> objectsInDb, Class<T> clazz)
      throws InstantiationException, IllegalAccessException
  {
    List<T> result = new ArrayList<>();
    if (selectedValues != null)
    {
      for (String s : selectedValues)
      {
        boolean exists = false;
        if (objectsInDb != null)
        {
          for (T c : objectsInDb)
          {
            if (s.equals(c.getName()))
            {
              exists = true;
              result.add(c);
              break;
            }
          }
        }
        if (!exists)
        {
          T toAdd = clazz.newInstance();
          toAdd.setName(s);
          result.add(toAdd);
        }
      }
    }
    return result;
  }

  /**
   * Merges values from the database and values from the HTML form together. Keeps objects from DB
   * if the content is equal.
   * 
   * @param selectedValues values from the HTML form
   * @param objectsInDb values from the DB
   * @return the merged list
   */
  public static <T extends ISimpleDbRelationLink> List<T> mergeLinkObjectsFromDb(
      List<T> selectedValues, List<T> objectsInDb)
  {
    List<T> selectedAppLinks = new ArrayList<>();
    if (selectedValues != null)
    {
      for (T link : selectedValues)
      {
        if (Objects.nonNull(link))
        {
          if (link.hasValues())
          {
            // Map to existent object, equals if both, name and url are equal
            boolean exists = false;
            for (T linkInDb : objectsInDb)
            {
              if (linkInDb.getName().equals(link.getName()) && linkInDb.getUrl().equals(link.getUrl()))
              {
                exists = true;
                selectedAppLinks.add(linkInDb);
                break;
              }
            }
            if (!exists)
            {
              selectedAppLinks.add(link);
            }
          }
        }
      }
    }
    return selectedAppLinks;
  }

  /**
   * Merges values from the database and values from the HTML form together. Keeps objects from DB
   * if the content is equal.
   * 
   * @param selectedValues values from the HTML form
   * @param objectsInDb values from the DB
   * @return the merged list
   */
  public static List<Type> mergeAdditionalTypesFromDb(List<String> selectedValues, List<Type> objectsInDb,
      Type currentPrimaryType)
  {
    List<Type> selectedAdditionalTypes = new ArrayList<>();
    if (selectedValues != null)
    {
      for (String s : selectedValues)
      {
        if (s.equals(currentPrimaryType.getName()))
        {
          continue; // ignore Type if its already the primary Type
        }
        boolean exists = false;
        if (objectsInDb != null)
        {
          for (Type t : objectsInDb)
          {
            if (s.equals(t.getName()))
            {
              exists = true;
              selectedAdditionalTypes.add(t);
              break; // Type already exists
            }
          }
        }
        if (!exists)
        {
          selectedAdditionalTypes.add(new Type(null, s)); // Type is missing, create it
        }
      }
    }
    return selectedAdditionalTypes;
  }

  /**
   * Merges values from the database and values from the HTML form together. Keeps objects from DB
   * if the content is equal.
   * 
   * @param selectedValues values from the HTML form
   * @param objectsInDb values from the DB
   * @return the merged list
   */
  public static List<Image> mergeImagesFromDb(List<Image> selectedValues, List<Image> objectsInDb)
  {
    List<Image> selectedImages = new ArrayList<>();
    if (selectedValues != null)
    {
      int i = 1;
      for (Image image : selectedValues)
      {
        Image tmp = null;
        for (Image candidate : objectsInDb)
        {
          if (Objects.nonNull(image.getId()) && Objects.equals(image.getId(), candidate.getId()))
          {
            tmp = candidate;
            break;
          }
        }
        // Image is new or was updated
        if (ImageUtil.isValidFilename(image.getTmpFileName()))
        {
          Path path = EditCommonConstants.PATH_TEMP_DIR_EDIT_PORTLET.resolve(image.getTmpFileName());
          byte[] toAdd = readImageFromFilesystem(path);
          if (ArrayUtils.isNotEmpty(toAdd))
          {
            if (Objects.nonNull(tmp))
            {
              tmp.setImage(toAdd);
              tmp.setImageOrderId(i++);
              tmp.setSourceName(image.getSourceName());
              selectedImages.add(tmp);
            }
            else
            {
              image.setImage(toAdd);
              image.setImageOrderId(i++);
              selectedImages.add(image);
            }
          }
        }
        // Image is still the same
        else if (StringUtils.isNotEmpty(image.getThumbnailImage()))
        {
          if (Objects.nonNull(tmp))
          {
            tmp.setImageOrderId(i++);
            tmp.setSourceName(image.getSourceName());
            selectedImages.add(tmp);
          }
          else
          {
            log.warn("Does not found existent image to ID {}! Ignoring image.", image.getId());
          }
        }
      }
    }
    return selectedImages;
  }

  private static byte[] readImageFromFilesystem(Path path)
  {
    try (ByteArrayOutputStream out = new ByteArrayOutputStream())
    {
      Files.copy(path, out);
      Files.deleteIfExists(path);
      return out.toByteArray();
    }
    catch (IOException e)
    {
      log.warn("Could not read image file {} from filesystem! Details: {}", path.toString(), e.getMessage());
    }
    return null;
  }
}
