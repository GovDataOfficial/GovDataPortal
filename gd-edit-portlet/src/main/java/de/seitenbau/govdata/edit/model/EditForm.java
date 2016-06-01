package de.seitenbau.govdata.edit.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Pattern.Flag;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.URL;

import de.seitenbau.govdata.edit.gui.common.Constants;
import de.seitenbau.govdata.edit.validator.Categories;
import de.seitenbau.govdata.edit.validator.GeoJSONPolygon;
import de.seitenbau.govdata.edit.validator.LicenceId;
import de.seitenbau.govdata.edit.validator.MapKeys;
import de.seitenbau.govdata.edit.validator.RequiredRole;
import de.seitenbau.govdata.edit.validator.StringDate;

import lombok.Data;

@Data
public class EditForm implements Serializable
{
  private static final long serialVersionUID = 1838562685681593568L;
  
  private String organizationId;
  
  @NotEmpty
  private String name;
  
  private boolean isPrivate;
  
  @NotEmpty
  private String title;
    
  private String maintainer;
  
  @Email
  private String maintainerEmail;
  
  @NotEmpty
  private String notes;
  
  @Pattern(regexp="|[ ]*([a-zäöüß0-9\\-_\\. ])+(,[ ]*[a-zäöüß0-9\\-_\\. ]+)*[ ]*", message="{tags}", flags={Flag.CASE_INSENSITIVE})
  private String tags;
  
  @URL
  private String url;
  
  @Pattern(regexp="datensatz|dokument|app", message="{typ}")
  private String typ;
  
  @NotEmpty
  @LicenceId(message="{licenceId}")
  private String licenseId;
  
  @GeoJSONPolygon(message="{geoJSONPolygon}")
  private String spatial;
  
  // should be date, but Spring is amazingly stubborn and will not handle Date correctly
  @StringDate(format="dd.MM.yyyy", regex="\\d{1,2}.\\d{1,2}.\\d{4}", message="{dateFormat}")
  private String temporalCoverageFrom;
  
  @StringDate(format="dd.MM.yyyy", regex="\\d{1,2}.\\d{1,2}.\\d{4}", message="{dateFormat}")
  private String temporalCoverageUntil;
  
  // dates
  @NotEmpty
  @StringDate(format="dd.MM.yyyy", regex="\\d{1,2}.\\d{1,2}.\\d{4}", message="{dateFormat}")
  private String datesCreated;
  
  @NotEmpty
  @StringDate(format="dd.MM.yyyy", regex="\\d{1,2}.\\d{1,2}.\\d{4}", message="{dateFormat}")
  private String datesPublished;
  
  @NotEmpty
  @StringDate(format="dd.MM.yyyy", regex="\\d{1,2}.\\d{1,2}.\\d{4}", message="{dateFormat}")
  private String datesModified;
  
  @NotEmpty(message="{categories.notempty}")
  @Categories(message="{categories}")
  private List<String> categories;
  
  @NotEmpty(message="{resources}")
  @Valid
  private List<Resource> resources;
  
  @Valid
  @MapKeys(allowedKeys={"autor", "vertrieb", "ansprechpartner"}, message="{mapKeys}")
  @RequiredRole(name="autor", message="{requiredRole}")
  private Map<String, Contact> contacts;

  /**
   * Checks if the edit form contains a new dataset.
   * 
   * @return true, if the form contains a new dataset, otherwise false.
   */
  public boolean isNewDataset()
  {
    return StringUtils.equals(getName(), Constants.NAME_NEW_DATASET);
  }
}
