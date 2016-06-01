package de.seitenbau.govdata.search.gui.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.apache.commons.collections.CollectionUtils;
import org.elasticsearch.common.lang3.StringUtils;

import de.fhg.fokus.odp.registry.model.Category;
import de.seitenbau.govdata.filter.SearchConsts;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HitViewModel
{
  private String id;
  
  private String type;
  
  private String title;
  
  private String source;
  
  private String content;
  
  private Date lastModified;
  
  private Date temporalCoverageFrom;
  private Date temporalCoverageTo;

  private String linkToDetailPage;
  
  private Boolean isOpen;
  
  private String contact;
  
  private String contactEmail;

  private String ownerOrg;

  private List<ResourceViewModel> resources;
  
  private String linkToCKan;
  
  private List<Category> categories;

  public boolean isCkanMetadata()
  {
    return StringUtils.equals(source, SearchConsts.SOURCE_CKAN);
  }

  public List<String> getDifferentFormatTypes()
  {
    List<String> result = new ArrayList<String>();

    if (CollectionUtils.isNotEmpty(resources))
    {
      for (ResourceViewModel res : resources)
      {
        String format = res.getFormat();
        if (format != null && !result.contains(format))
        {
          result.add(format);
        }
      }
    }

    return result;
  }
}
