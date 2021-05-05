package de.seitenbau.govdata.portlet.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Represents a option tag with {@link #setValue value} property and {@link #setLabel label}
 * property, used within a select tag.
 * 
 * @author rnoerenberg
 *
 */
@Data
@AllArgsConstructor
public class OptionTag
{
  private String value;

  private String label;
}
