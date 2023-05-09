package de.seitenbau.govdata.messages;

public enum MessageKey
{
  /**
   * Error message key.
   */
  ERROR_OCCURRED("od.error.message"),
  /**
   * Comment not existent message key.
   */
  COMMENT_NOT_EXISTENT("od.comment.id_not_existent"),
  /**
   * No permission message key.
   */
  NO_PERMISSION("od.permission.none"),
  /**
   * Comment deleted message key.
   */
  COMMENT_DELETED_SUCCESS("od.comment.delete.success"),
  /**
   * Showcase image saved message key.
   */
  SHOWCASE_SAVE_SUCCESS("od.editform.showcase.save.success"),
  /**
   * Showcase save message key.
   */
  SHOWCASE_SAVE_WARNING("od.editform.showcase.save.warning"),
  /**
   * Showcase error message key.
   */
  SHOWCASE_SAVE_ERROR("od.editform.showcase.save.error"),
  /**
   * Showcase image upload error message key.
   */
  SHOWCASE_IMAGE_UPLOAD_ERROR("od.editform.showcase.image.upload.error");

  private String key;

  private MessageKey(String key)
  {
    this.key = key;
  }

  @Override
  public String toString()
  {
    return key;
  }
}
