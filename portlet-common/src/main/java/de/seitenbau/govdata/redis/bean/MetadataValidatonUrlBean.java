package de.seitenbau.govdata.redis.bean;

import java.io.Serializable;

import lombok.Data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Eine Klasse, die einen URL-Eintrag für die Linkchecker-Report eines Metadatensatzes in der
 * Redis-Datenbank repräsentiert.
 * 
 * @author rnoerenberg
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MetadataValidatonUrlBean implements Serializable
{
  private static final long serialVersionUID = -4008607650136079391L;

  @JsonProperty
  private String status;

  @JsonProperty
  private String date;
  
  @JsonProperty
  private Integer strikes;
}
