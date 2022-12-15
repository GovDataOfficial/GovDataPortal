package de.seitenbau.govdata.odp.registry.ckan.json;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
@Data
public class AccessServiceBean implements Serializable
{
  private static final long serialVersionUID = -1853401003777466533L;

  @JsonProperty
  private String availability;

  @JsonProperty
  private String title;

  @JsonProperty(value = "endpoint_description")
  private String endpointDescription;

  @JsonProperty
  private String license;

  @JsonProperty(value = "access_rights")
  private String accessRights;

  @JsonProperty
  private String description;

  @JsonProperty(value = "endpoint_url")
  private List<String> endpointUrls;

  @JsonProperty(value = "serves_dataset")
  private List<String> servesDataset;

  @JsonProperty
  private String licenseAttributionByText;
}
