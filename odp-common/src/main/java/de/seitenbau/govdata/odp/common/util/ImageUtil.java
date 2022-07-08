package de.seitenbau.govdata.odp.common.util;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Objects;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * Utility class for operations with images.
 *
 */
@Slf4j
public final class ImageUtil
{
  private ImageUtil()
  {
    // private
  }

  /** Base64 PNG prefix. */
  public static final String BASE_64_STRING_PREFIX = "data:image/png;base64,";

  /**
   * Converts the given image base64 encoded string into an image byte array.
   * 
   * @param base64 the base64 encoded string
   * @return the byte array of the given image base64 encoded string, or null if the input is null
   *         or the base64 encoded string is not an image
   */
  public static byte[] convertBase64StringToByteArray(String base64)
  {
    byte[] result = null;
    if (StringUtils.isNotBlank(base64))
    {
      result = Base64.getDecoder().decode(StringUtils.substringAfter(base64, ","));
    }
    return result;
  }

  /**
   * Converts the given image byte array into an image base64 encoded string.
   * 
   * @param toConvert the byte array
   * @return the image base64 encoded string of the given byte array, or null if the input is null
   *         or the byte array is not an image
   */
  public static String convertByteArrayToBase64String(byte[] toConvert)
  {
    String result = null;
    if (ArrayUtils.isNotEmpty(toConvert))
    {
      result = BASE_64_STRING_PREFIX + Base64.getEncoder().encodeToString(toConvert);
    }
    return result;
  }

  /**
   * Converts the given image byte array into an image base64 encoded string.
   * 
   * @param toConvert the byte array
   * @return the base64 encoded string of the smaller image, or null if the input is null or the
   *         byte array is not an image
   */
  public static String convertByteArrayToBase64StringThumbnail(byte[] toConvert)
  {
    return convertByteArrayToBase64String(getThumbnail(toConvert));
  }

  /**
   * Converts the given image byte array into an image base64 encoded string with the given size.
   * 
   * @param toConvert the byte array
   * @param targetWidth target width of the thumbnail
   * @param targetHeight target height of the thumbnail
   * @return the base64 encoded string of the smaller image, or null if the input is null or the
   *         byte array is not an image
   */
  public static String convertByteArrayToBase64StringThumbnail(byte[] toConvert, int targetWidth,
      int targetHeight)
  {
    return convertByteArrayToBase64String(getThumbnail(toConvert, targetWidth, targetHeight));
  }

  /**
   * Converts the given image byte array into an (probably) smaller image.
   * 
   * @param toConvert the byte array
   * @return the byte array of the smaller image, or null if the input is null or the byte array is
   *         not an image
   */
  public static byte[] getThumbnail(byte[] toConvert)
  {
    return getThumbnail(toConvert, 200, 200);
  }

  /**
   * Converts the given image byte array into an image width the given dimension.
   * 
   * @param toConvert the byte array
   * @param targetWidth the target width of the thumbail
   * @param targetHeight the target height of the thumbnail
   * @return the byte array of the thumbnail image, or null if the input is null or the byte array
   *         is not an image
   */
  public static byte[] getThumbnail(byte[] toConvert, int targetWidth, int targetHeight)
  {
    byte[] result = null;
    if (ArrayUtils.isNotEmpty(toConvert))
    {
      try (ByteArrayInputStream inputStream = new ByteArrayInputStream(toConvert))
      {
        BufferedImage image = ImageIO.read(inputStream);
        if (Objects.nonNull(image))
        {
          Image awtImage = image.getScaledInstance(targetWidth, targetHeight, java.awt.Image.SCALE_DEFAULT);
          BufferedImage outputImage =
              new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
          outputImage.getGraphics().drawImage(awtImage, 0, 0, null);
          ByteArrayOutputStream output = new ByteArrayOutputStream();
          ImageIO.write(outputImage, "png", output);
          output.flush();
          output.close();
          result = output.toByteArray();
        }
      }
      catch (IOException e)
      {
        log.warn("Error while reading or writing image from/to byte array. Details: {}", e.getMessage());
      }
    }
    return result;
  }

  /**
   * Checks if the given filename is a valid filename, e.g. contains no path information.
   * 
   * @param filename the filename to check
   * @return True if the filename is valid, false otherwise
   */
  public static boolean isValidFilename(String filename)
  {
    return StringUtils.isNotEmpty(filename) && Pattern.matches("^[A-z0-9]+\\.[A-z]+$", filename);
  }

}
