package de.seitenbau.govdata.odp.registry.ckan.json;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
@Data
public class ApiTokenBean implements Serializable
{

  private static final long serialVersionUID = 1853231537966976976L;

  @JsonProperty
  private String id;

  @JsonProperty
  private String name;

  @JsonProperty(value = "created_at")
  private String createdAt;

  @JsonProperty(value = "last_access")
  private String lastAccess;
}
