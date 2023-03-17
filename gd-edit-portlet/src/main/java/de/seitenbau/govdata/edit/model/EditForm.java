package de.seitenbau.govdata.edit.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Pattern.Flag;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.URL;

import de.seitenbau.govdata.constants.CommonConstants;
import de.seitenbau.govdata.edit.gui.common.Constants;
import de.seitenbau.govdata.edit.validator.Categories;
import de.seitenbau.govdata.edit.validator.ContributorId;
import de.seitenbau.govdata.edit.validator.GeoJSONPolygon;
import de.seitenbau.govdata.edit.validator.MapKeys;
import de.seitenbau.govdata.edit.validator.StringDate;
import lombok.Data;

@Data
public class EditForm implements Serializable
{
  private static final long serialVersionUID = 1838562685681593568L;

  private String organizationId;

  @ContributorId(message = "{contributorId}")
  private String contributorId;

  @NotEmpty
  private String name;

  private boolean isPrivate;

  @NotEmpty
  private String title;

  @NotEmpty
  private String notes;

  @Pattern(regexp = "|[ ]*([a-zäöüß0-9\\-_\\. ])+(,[ ]*[a-zäöüß0-9\\-_\\. ]+)*[ ]*", message = "{tags}", flags = {Flag.CASE_INSENSITIVE})
  private String tags;

  @URL
  private String url;

  @URL
  private String qualityProcessURI;

  // Is a list, but we will offer a comma separated string here
  private String policiticalGeocodingURI;

  @URL
  private String policiticalGeocodingLevelURI;

  private String geocodingText;

  private String legalbasisText;

  @GeoJSONPolygon(message="{geoJSONPolygon}")
  private String spatial;

  // should be date, but Spring is amazingly stubborn and will not handle Date correctly
  @StringDate(format=CommonConstants.DATE_PATTERN, regex="\\d{1,2}.\\d{1,2}.\\d{4}", message="{dateFormat}")
  private String temporalCoverageFrom;

  @StringDate(format=CommonConstants.DATE_PATTERN, regex="\\d{1,2}.\\d{1,2}.\\d{4}", message="{dateFormat}")
  private String temporalCoverageUntil;

  // dates
  @StringDate(format=CommonConstants.DATE_PATTERN, regex="\\d{1,2}.\\d{1,2}.\\d{4}", message="{dateFormat}")
  private String datesPublished;

  @StringDate(format=CommonConstants.DATE_PATTERN, regex="\\d{1,2}.\\d{1,2}.\\d{4}", message="{dateFormat}")
  private String datesModified;

  @Categories(message="{categories}")
  private List<String> categories;

  @NotEmpty(message="{resources}")
  @Valid
  private List<Resource> resources;

  @Valid
  @MapKeys(allowedKeys={"maintainer", "publisher", "author", "originator"}, message="{mapKeys}")
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
