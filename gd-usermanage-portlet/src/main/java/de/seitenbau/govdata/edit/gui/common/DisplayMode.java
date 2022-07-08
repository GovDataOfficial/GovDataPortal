package de.seitenbau.govdata.edit.gui.common;

/**
 * Enum for Alert-Messages used in thymeleaf-templates
 * @author tscheffler
 */
public enum DisplayMode
{
  CONFIRMATIONDIALOG("confirmationdialog"),
  MAILSENT("mailsent"),
  DELETIONSUCCESS("success"),
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