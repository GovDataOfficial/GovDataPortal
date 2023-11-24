package de.seitenbau.govdata.odp.registry.ckan.json;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

/**
 * The Class LicenceBean.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Data
public class LicenceBean implements Serializable
{
  private static final long serialVersionUID = 7316836055751291814L;

  @JsonProperty
  private String id;

  @JsonProperty
  private String title;

  @JsonProperty
  private String url;

  @JsonProperty
  private String status;

  @JsonProperty
  private String od_conformance;

  @JsonProperty
  private String osd_conformance;
}
