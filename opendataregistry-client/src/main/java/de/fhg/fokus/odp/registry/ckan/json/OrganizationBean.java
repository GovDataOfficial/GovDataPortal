package de.fhg.fokus.odp.registry.ckan.json;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * Just implemented the values we needed.
 * Currently this is only read, but not written in any way.
 * @author tscheffler
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrganizationBean implements Serializable
{
  private static final long serialVersionUID = 4429215101938936132L;

  @JsonProperty
  private String id;
  
  @JsonProperty
  private String name;
  
  @JsonProperty
  private String display_name;
  
  @JsonProperty
  private String title;
}
