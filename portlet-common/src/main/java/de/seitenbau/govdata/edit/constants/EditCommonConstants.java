package de.seitenbau.govdata.edit.constants;

import java.nio.file.Path;
import java.nio.file.Paths;

public final class EditCommonConstants
{
  private EditCommonConstants()
  {
    // private
  }

  /** Path to the temporary saved preview images. */
  public static final Path PATH_TEMP_DIR_EDIT_PORTLET =
      Paths.get(System.getProperty("java.io.tmpdir"), "edit-portlet-images");

  /** Default size in the database for columns of type text. */
  public static final int DEFAULT_COLUMN_SIZE_STRING = 255;

}
