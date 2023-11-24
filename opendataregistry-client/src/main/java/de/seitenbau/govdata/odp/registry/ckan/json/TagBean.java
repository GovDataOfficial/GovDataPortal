package de.seitenbau.govdata.odp.registry.ckan.json;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Stores tags of a metadata entry.
 *
 * @author sim
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Data
@NoArgsConstructor
public class TagBean implements Serializable
{
  private static final long serialVersionUID = -3809890981773970748L;

  /**
   * Convenience constructor to create a new tag from user interface.
   * @param name name of the tag (and display_name, since they are the same for us)
   */
  public TagBean(String name)
  {
    this.name = name;
    this.display_name = name;
  }

  @JsonProperty
  private String id;

  @JsonProperty
  private String name;

  @JsonProperty
  private String display_name;
}
