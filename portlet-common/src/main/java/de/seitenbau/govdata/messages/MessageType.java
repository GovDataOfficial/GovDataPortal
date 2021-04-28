package de.seitenbau.govdata.messages;

/**
 * Enum for Alert-Messages used in thymeleaf-templates
 * @author tscheffler
 */
public enum MessageType {
  ERROR("alert-error"),
  WARNING("alert-warning"),
  SUCCESS("alert-success");
  
  
  private String field;
  
  private MessageType(String field) {
    this.field = field;
  }
  
  @Override
  public String toString()
  {
    return field;
  }
}