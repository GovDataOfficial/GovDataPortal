package de.seitenbau.govdata.edit.model;

import static de.seitenbau.govdata.edit.constants.EditCommonConstants.DEFAULT_COLUMN_SIZE_STRING;

import javax.validation.constraints.Size;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.URL;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Link implements ISimpleDbRelationLink
{
  private static final long serialVersionUID = -2948302022811715695L;

  private Long id;

  @URL
  @Size(max = DEFAULT_COLUMN_SIZE_STRING, message = "{javax.validation.constraints.Size.max.message}")
  private String url;

  @Size(max = DEFAULT_COLUMN_SIZE_STRING, message = "{javax.validation.constraints.Size.max.message}")
  private String name;

  /**
   * Checks if the class {@link Link} is empty.
   * 
   * @return True if there is minimum one value in the property {@link #url} or {@link #name}
   *         available, otherwise false.
   */
  @Override
  public boolean hasValues()
  {
    return StringUtils.isNotBlank(getName()) || StringUtils.isNotBlank(getUrl());
  }
}
