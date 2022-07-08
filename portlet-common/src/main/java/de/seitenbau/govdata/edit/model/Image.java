package de.seitenbau.govdata.edit.model;

import java.io.Serializable;

import javax.validation.constraints.Size;

import org.apache.commons.lang3.StringUtils;

import de.seitenbau.govdata.odp.common.util.ImageUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Image implements Serializable
{
  private static final long serialVersionUID = 4795897243302233367L;

  private Long id;

  private int imageOrderId;

  private byte[] image;

  private String thumbnailImage;

  private String tempImageResourceUrl;

  private String tmpFileName;

  @Size(max = 1024, message = "{javax.validation.constraints.Size.max.message}")
  private String sourceName;

  /**
   * Converts the image byte array into an image bease64 encoded string and set it to the property
   * {@link #thumbnailImage}.
   */
  public void updateThumbnailImage()
  {
    setThumbnailImage(ImageUtil.convertByteArrayToBase64StringThumbnail(getImage()));
  }

  /**
   * Converts the image byte array into an image bease64 encoded string and set it to the property
   * {@link #thumbnailImage}.
   */
  public void updateThumbnailImage(int width, int height)
  {
    setThumbnailImage(ImageUtil.convertByteArrayToBase64StringThumbnail(getImage(), width, height));
  }

  /**
   * Checks if the {@link Image} has any image to display.
   * 
   * @return true if there is an image to display, otherwise false
   */
  public boolean isImageToDisplay()
  {
    return StringUtils.isNotEmpty(getThumbnailImage()) || StringUtils.isNotEmpty(getTmpFileName());
  }

  /**
   * Checks if the {@link Image} has only a already saved image to display.
   * 
   * @return true if there is only a already saved image to display, otherwise false
   */
  public boolean isOnlySavedImageToDisplay()
  {
    return StringUtils.isNotEmpty(getThumbnailImage()) && StringUtils.isEmpty(getTmpFileName());
  }
}
