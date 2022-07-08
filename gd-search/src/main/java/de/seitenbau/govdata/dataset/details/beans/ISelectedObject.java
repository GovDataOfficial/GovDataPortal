package de.seitenbau.govdata.dataset.details.beans;

import java.util.List;

/**
 * Indicates the minimum common methods for objects to display.
 * 
 * @author rnoerenberg
 *
 */
public interface ISelectedObject
{
  /**
   * Returns the title without any markup.
   * 
   * @return the title without any markup
   */
  String getTitleOnlyText();

  /**
   * Returns the notes without any markup.
   * 
   * @return the notes without any markup
   */
  String getNotesOnlyText();

  /**
   * Returns the list of the tags, cleaned from any markup.
   * 
   * @return the list of the tags, cleaned from any markup
   */
  List<String> getTagNameList();
}
