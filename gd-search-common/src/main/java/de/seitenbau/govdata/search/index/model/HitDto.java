package de.seitenbau.govdata.search.index.model;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Enthält die Daten zu einem Treffer aus dem Suchindex.
 * 
 * @author rnoerenberg
 *
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HitDto
{
  private String id;
  
  private String name;
  
  private String type;

  private String title;
  
  private String content;
  
  private String created;
  
  private Date lastModified;
  
  private Date temporalCoverageFrom;
  private Date temporalCoverageTo;
  
  private String contact;
  
  private String contactEmail;
  
  private List<String> groups;
  
  private String articleId;
  
  private String entryClassPK;
  
  private Long groupId;
  
  private Boolean hasOpen;

  private Boolean hasClosed;

  private String ownerOrg;

  private List<ResourceDto> resources;

  // Showcase fields
  private String primaryShowcaseType;

  private List<String> platforms;

  private List<String> usedDatasets;

  private String displayImage;

  private Date releaseDate;

  private List<String> allShowcaseTypes;
}
