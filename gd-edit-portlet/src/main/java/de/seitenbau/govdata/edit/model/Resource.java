package de.seitenbau.govdata.edit.model;

import de.seitenbau.govdata.edit.validator.LicenceId;
import de.seitenbau.govdata.edit.validator.StringDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.URL;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Resource implements Serializable
{
  private static final long serialVersionUID = -516410984434917647L;
  
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

  @StringDate(format="dd.MM.yyyy", regex="\\d{1,2}.\\d{1,2}.\\d{4}", message="{dateFormat}")
  private String modified;

  @URL
  private String plannedAvailability;
}
