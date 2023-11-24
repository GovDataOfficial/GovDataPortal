package de.seitenbau.govdata.odp.registry.ckan.json;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Class for a user in CKAN.
 * 
 * @author sim
 * 
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserBean implements Serializable
{
  // CHECKSTYLE:OFF

    /**
     * 
     */
    private static final long serialVersionUID = -6600071594997257635L;

    @JsonProperty
    private String id;

    @JsonProperty
    private String name;

    @JsonProperty
    private String fullname;

    @JsonProperty
    private String display_name;

    @JsonProperty
    private String about;

    @JsonProperty
    private Date created;

    @JsonProperty
    private String reset_key;

    @JsonProperty
    private String email;

    @JsonProperty
    private String email_hash;

    // @JsonIgnore
    // private List<MetadataBean> datasets;

    @JsonProperty
    private long number_of_edits;

    @JsonProperty
    private List<String> activity;

    @JsonProperty
    private long number_administered_packages;

    @JsonProperty
    private String openid;

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
     * @return the fullname
     */
    public String getFullname() {
        return fullname;
    }

    /**
     * @param fullname
     *            the fullname to set
     */
    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    /**
     * @return the display_name
     */
    public String getDisplay_name() {
        return display_name;
    }

    /**
     * @param display_name
     *            the display_name to set
     */
    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    /**
     * @return the about
     */
    public String getAbout() {
        return about;
    }

    /**
     * @param about
     *            the about to set
     */
    public void setAbout(String about) {
        this.about = about;
    }

    /**
     * @return the created
     */
    public Date getCreated() {
        return created == null ? null : (Date) created.clone();
    }

    /**
     * @param created
     *            the created to set
     */
    public void setCreated(Date created) {
        this.created = created == null ? null : (Date) created.clone();
    }

    /**
     * @return the reset_key
     */
    public String getReset_key() {
        return reset_key;
    }

    /**
     * @param reset_key
     *            the reset_key to set
     */
    public void setReset_key(String reset_key) {
        this.reset_key = reset_key;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email
     *            the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return the email_hash
     */
    public String getEmail_hash() {
        return email_hash;
    }

    /**
     * @param email_hash
     *            the email_hash to set
     */
    public void setEmail_hash(String email_hash) {
        this.email_hash = email_hash;
    }

    /**
     * @return the number_of_edits
     */
    public long getNumber_of_edits() {
        return number_of_edits;
    }

    /**
     * @param number_of_edits
     *            the number_of_edits to set
     */
    public void setNumber_of_edits(long number_of_edits) {
        this.number_of_edits = number_of_edits;
    }

    /**
     * @return the activity
     */
    public List<String> getActivity() {
        return activity;
    }

    /**
     * @param activity
     *            the activity to set
     */
    public void setActivity(List<String> activity) {
        this.activity = activity;
    }

    /**
     * @return the number_administered_packages
     */
    public long getNumber_administered_packages() {
        return number_administered_packages;
    }

    /**
     * @param number_administered_packages
     *            the number_administered_packages to set
     */
    public void setNumber_administered_packages(long number_administered_packages) {
        this.number_administered_packages = number_administered_packages;
    }

    /**
     * @return the openid
     */
    public String getOpenid() {
        return openid;
    }

    /**
     * @param openid
     *            the openid to set
     */
    public void setOpenid(String openid) {
        this.openid = openid;
    }

}
