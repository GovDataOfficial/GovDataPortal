package de.seitenbau.govdata.edit.model;

import java.io.Serializable;

import javax.validation.constraints.NotEmpty;

import org.hibernate.validator.constraints.URL;

import de.seitenbau.govdata.constants.CommonConstants;
import de.seitenbau.govdata.edit.validator.LicenceId;
import de.seitenbau.govdata.edit.validator.StringDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Resource implements Serializable
{
  private static final long serialVersionUID = -516410984434917647L;

  private String id;

  @URL
  @NotEmpty
  private String url;
  
  private String format;
  
  private String description;
  
  private String name;
  
  private String language;
  
  @NotEmpty
  @LicenceId(message="{licenceId}")
  private String licenseId;

  private String licenseAttributionByText;

  @StringDate(format = CommonConstants.DATE_PATTERN, regex = "\\d{1,2}.\\d{1,2}.\\d{4}",
      message = "{dateFormat}")
  private String issued;

  @StringDate(format=CommonConstants.DATE_PATTERN, regex="\\d{1,2}.\\d{1,2}.\\d{4}", message="{dateFormat}")
  private String modified;

  @URL
  private String plannedAvailability;

  @URL
  private String availability;
}
