package de.seitenbau.govdata.edit.model;

import static de.seitenbau.govdata.edit.constants.EditCommonConstants.DEFAULT_COLUMN_SIZE_STRING;

import java.io.Serializable;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.URL;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Contact implements Serializable
{
  private static final long serialVersionUID = -1253636402396326173L;

  private Long id;

  @Size(max = DEFAULT_COLUMN_SIZE_STRING, message = "{javax.validation.constraints.Size.max.message}")
  private String name;
  
  @URL
  @Size(max = DEFAULT_COLUMN_SIZE_STRING, message = "{javax.validation.constraints.Size.max.message}")
  private String url;
  
  @Email
  @Size(max = DEFAULT_COLUMN_SIZE_STRING, message = "{javax.validation.constraints.Size.max.message}")
  private String email;
  
  @Valid
  private ContactAddress address;
  
  public boolean shouldStoreContact()
  {
    if (StringUtils.isBlank(name) && StringUtils.isBlank(email) && StringUtils.isBlank(url))
    {
      return false;
    }
    return true;
  }

}
