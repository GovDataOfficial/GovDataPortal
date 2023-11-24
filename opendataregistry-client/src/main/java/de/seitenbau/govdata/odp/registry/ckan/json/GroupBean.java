package de.seitenbau.govdata.odp.registry.ckan.json;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Class for a group in CKAN.
 * 
 * @author sim
 * 
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class GroupBean implements Serializable
{
  // CHECKSTYLE:OFF

    /**
     * 
     */
    private static final long serialVersionUID = -2526804296948451352L;

    @JsonProperty
    private String id;

    @JsonProperty
    private String name;

    @JsonProperty
    private String display_name;

    @JsonProperty
    private String title;

    @JsonProperty
    private String description;

    @JsonProperty
    private String package_count;

    @JsonProperty
    private String approval_status;

    @JsonProperty
    private String state;

    @JsonProperty
    private String image_url;

    @JsonProperty
    private String revision_id;

    @JsonProperty
    private String type;

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
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the count
     */
    public String getPackageCount()
    {
      return package_count;
    }

    /**
     * @param count
     *            the count to set
     */
    public void setPackageCount(String package_count)
    {
      this.package_count = package_count;
    }

    /**
     * @return the displayName
     */
    public String getDisplayName() {
        return display_name;
    }

    /**
     * @param displayName
     *            the displayName to set
     */
    public void setDisplayName(String display_name) {
        this.display_name = display_name;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title
     *            the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description
     *            the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the approval_status
     */
    public String getApprovalStatus() {
        return approval_status;
    }

    /**
     * @param approval_status
     *            the approval_status to set
     */
    public void setApprovalStatus(String approval_status) {
        this.approval_status = approval_status;
    }

    /**
     * @return the state
     */
    public String getState() {
        return state;
    }

    /**
     * @param state
     *            the state to set
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * @return the image_url
     */
    public String getImage_url() {
        return image_url;
    }

    /**
     * @param image_url
     *            the image_url to set
     */
    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    /**
     * @return the revision_id
     */
    public String getRevision_id() {
        return revision_id;
    }

    /**
     * @param revision_id
     *            the revision_id to set
     */
    public void setRevision_id(String revision_id) {
        this.revision_id = revision_id;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type
     *            the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

}
