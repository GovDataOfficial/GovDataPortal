package de.seitenbau.govdata.odp.registry.ckan.json;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
@Data
public class ResourceExportBean implements Serializable
{
    private static final long serialVersionUID = -1474098456725033104L;

    @JsonProperty
    private String id;

    @JsonProperty
    private String name;

    @JsonProperty
    private String resource_type;

    @JsonProperty
    private String description;

    @JsonProperty
    private String format;

    @JsonProperty
    private Date created;

    @JsonProperty
    private String url;

    @JsonProperty
    private String hash;

    @JsonProperty
    private long size;

    @JsonProperty
    private String state;

    /**
     * Will contain the key:value pairs for:
         private String language
         private String license
         private Date issued
         private Date modified;
         private String licenseAttributionByText;
         private String plannedAvailability;
         private String documentation;
         private String conforms_to;
         private String download_url;
         private String status;
         private String mimetype;
         private String rights;
         private String hash_algorithm;
     */
    @JsonProperty
    private Map<String, String> __extras;
}
