package de.seitenbau.govdata.edit.model;

import static de.seitenbau.govdata.edit.constants.EditCommonConstants.DEFAULT_COLUMN_SIZE_STRING;

import java.io.Serializable;

import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * An address-container for a contact.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContactAddress implements Serializable
{
  private static final long serialVersionUID = 8205379687645694254L;

  @Size(max = DEFAULT_COLUMN_SIZE_STRING, message = "{javax.validation.constraints.Size.max.message}")
  private String addressee;

  @Size(max = DEFAULT_COLUMN_SIZE_STRING, message = "{javax.validation.constraints.Size.max.message}")
  private String details;

  @Size(max = DEFAULT_COLUMN_SIZE_STRING, message = "{javax.validation.constraints.Size.max.message}")
  private String street;

  @Size(max = DEFAULT_COLUMN_SIZE_STRING, message = "{javax.validation.constraints.Size.max.message}")
  private String city;

  @Size(max = DEFAULT_COLUMN_SIZE_STRING, message = "{javax.validation.constraints.Size.max.message}")
  private String zip;

  @Size(max = DEFAULT_COLUMN_SIZE_STRING, message = "{javax.validation.constraints.Size.max.message}")
  private String country;
}
