package de.seitenbau.govdata.search.gui.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import de.seitenbau.govdata.common.model.exception.UnknownShowcasePlatformException;
import de.seitenbau.govdata.common.showcase.model.ShowcasePlatformEnum;
import de.seitenbau.govdata.common.showcase.model.ShowcaseTypeEnum;
import de.seitenbau.govdata.dcatde.ViewUtil;
import de.seitenbau.govdata.odp.common.filter.SearchConsts;
import de.seitenbau.govdata.odp.registry.model.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HitViewModel
{
  private String id;

  private String type;

  private String title;

  private String name;

  private String source;

  private String content;

  private Date lastModified;

  private Date temporalCoverageFrom;
  private Date temporalCoverageTo;

  private String linkToDetailPage;

  private Boolean hasOpen;

  private Boolean hasClosed;

  private String contact;

  private String contactEmail;

  private String ownerOrg;

  private List<ResourceViewModel> resources;

  private String linkToCKan;

  private List<Category> categories;

  // Showcase fields
  private String primaryShowcaseType;

  private List<String> platforms;

  private String displayImage;

  private Date releaseDate;

  private List<String> usedDatasets;

  private List<ShowcaseTypeEnum> allShowcaseTypes;

  private Boolean hasHvd;

  public boolean isCkanMetadata()
  {
    return StringUtils.equals(source, SearchConsts.SOURCE_CKAN);
  }

  /**
   * Gets the platforms with the display names as a comma separated list. Unknown platforms are
   * ignored.
   * 
   * @return the platforms as comma separated list
   */
  public String getPlatformsAsString()
  {
    StringJoiner stringJoiner = new StringJoiner(", ");
    if (CollectionUtils.isNotEmpty(platforms))
    {
      for (String s : platforms)
      {
        try
        {
          stringJoiner.add(ShowcasePlatformEnum.fromField(s).getDisplayName());
        }
        catch (UnknownShowcasePlatformException e)
        {
          // pass
        }
      }
    }
    return stringJoiner.toString();
  }

  /**
   * Gets the distinct list of the formats within the resources.
   * 
   * @return the distinct list of the resources formats
   */
  public List<String> getDifferentFormatTypes()
  {
    List<String> result = new ArrayList<String>();

    if (CollectionUtils.isNotEmpty(resources))
    {
      for (ResourceViewModel res : resources)
      {
        String format = ViewUtil.getShortenedFormatRef(res.getFormat());
        if (format != null && !result.contains(format))
        {
          result.add(format);
        }
      }
    }

    return result;
  }

  public Set<LicenseViewModel> getResourcesLicenses()
  {
    return getResources().stream()
        .filter(resource -> resource.getLicense() != null)
        .map(ResourceViewModel::getLicense)
        .collect(Collectors.toSet());
  }
}
