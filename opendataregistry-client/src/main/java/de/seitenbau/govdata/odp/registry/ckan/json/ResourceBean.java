package de.seitenbau.govdata.odp.registry.ckan.json;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import de.seitenbau.govdata.odp.registry.date.DateDeserializer;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
@Data
public class ResourceBean implements Serializable
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
    private String language;

    @JsonProperty
    @JsonDeserialize(using = DateDeserializer.class)
    private Date created;

    @JsonProperty
    @JsonDeserialize(using = DateDeserializer.class)
    private Date last_modified;

    @JsonProperty
    private String url;

    @JsonProperty
    private String hash;

    @JsonProperty
    private long size;

    @JsonProperty
    private String state;
    
    @JsonProperty
    private String license;
    
    @JsonProperty
    @JsonDeserialize(using = DateDeserializer.class)
    private Date issued;
    
    @JsonProperty
    @JsonDeserialize(using = DateDeserializer.class)
    private Date modified;
    
    @JsonProperty
    private String licenseAttributionByText;
    
    @JsonProperty
    private String plannedAvailability;
    
    @JsonProperty
    private String availability;

    @JsonProperty
    private String documentation;
    
    @JsonProperty
    private String conforms_to;
    
    @JsonProperty
    private String download_url;
    
    @JsonProperty
    private String status;
    
    @JsonProperty
    private String mimetype;
    
    @JsonProperty
    private String rights;
    
    @JsonProperty
    private String hash_algorithm;

    @JsonProperty(value = "access_services")
    private String accessServices;

    @JsonProperty(value = "applicable_legislation")
    private String applicableLegislation;
}
