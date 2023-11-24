package de.seitenbau.govdata.edit.gui.common;

/**
 * Enum for Alert-Messages used in thymeleaf-templates
 * @author tscheffler
 */
public enum DisplayMode
{
  /**
   * Display mode confirmationdialog
   */
  CONFIRMATIONDIALOG("confirmationdialog"),
  /**
   * Display mode mailsent
   */
  MAILSENT("mailsent"),
  /**
   * Display mode success
   */
  DELETIONSUCCESS("success"),
  /**
   * Display mode error
   */
  ERROR("error");

  private String field;

  private DisplayMode(String field)
  {
    this.field = field;
  }

  @Override
  public String toString()
  {
    return field;
  }
}