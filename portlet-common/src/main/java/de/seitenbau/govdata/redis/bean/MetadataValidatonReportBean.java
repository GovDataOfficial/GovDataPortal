package de.seitenbau.govdata.redis.bean;

import java.io.Serializable;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * Eine Klasse, die einen Eintrag für die Reports eines Metadatensatzes in der Redis-Datenbank
 * repräsentiert.
 * 
 * @author rnoerenberg
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MetadataValidatonReportBean implements Serializable
{
  private static final long serialVersionUID = 2295475658233636789L;

  @JsonProperty
  private String maintainer;

  @JsonProperty
  private String maintainer_email;
  
  @JsonProperty
  private String name;

  @JsonProperty
  private Map<String, MetadataValidatonUrlBean> urls;
  
  @JsonProperty
  private String metadata_original_portal;

  @JsonProperty
  private String id;
}
