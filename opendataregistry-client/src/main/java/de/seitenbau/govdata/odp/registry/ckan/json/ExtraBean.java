package de.seitenbau.govdata.odp.registry.ckan.json;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.TextNode;

/**
 * Class for an extra in a CKAN dataset.
 * 
 * @author sim
 * 
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExtraBean implements Serializable
{
  // CHECKSTYLE:OFF
    private static final long serialVersionUID = -617566629321775708L;

    @JsonProperty
    private String id;

    @JsonProperty
    private String key;

    @JsonProperty
    private String value;

    @JsonProperty
    private String state;

    @JsonProperty
    private String package_id;

    @JsonProperty
    private String revision_id;

    @JsonProperty
    private Date revision_timestamp;

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id
     *            the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the key
     */
    public String getKey() {
        return key;
    }

    /**
     * @param key
     *            the key to set
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * @return the value
     */
    public JsonNode getValue() {
        // we just have a bunch of characters... let's make a string.
        return new TextNode(value);
    }

    /**
     * @param value
     *            the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }

}
