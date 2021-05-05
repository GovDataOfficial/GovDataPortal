package de.seitenbau.govdata.odp.registry.ckan.json;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

  @JsonProperty
  private List<ExtraBean> extras;

  /**
   * The extras as list.
   * 
   * @return
   */
  @JsonIgnore
  public List<ExtraBean> getExtras()
  {
    if (extras == null)
    {
      extras = Collections.emptyList();
    }
    return extras;
  }
}
