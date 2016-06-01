package de.fhg.fokus.odp.entities.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.model.ModelWrapper;

/**
 * <p>
 * This class is a wrapper for {@link MetadataComment}.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see MetadataComment
 * @generated
 */
public class MetadataCommentWrapper implements MetadataComment,
    ModelWrapper<MetadataComment> {
    private MetadataComment _metadataComment;

    public MetadataCommentWrapper(MetadataComment metadataComment) {
        _metadataComment = metadataComment;
    }

    @Override
    public Class<?> getModelClass() {
        return MetadataComment.class;
    }

    @Override
    public String getModelClassName() {
        return MetadataComment.class.getName();
    }

    @Override
    public Map<String, Object> getModelAttributes() {
        Map<String, Object> attributes = new HashMap<String, Object>();

        attributes.put("uuid", getUuid());
        attributes.put("_id", get_id());
        attributes.put("userLiferayId", getUserLiferayId());
        attributes.put("metadataName", getMetadataName());
        attributes.put("text", getText());
        attributes.put("created", getCreated());

        return attributes;
    }

    @Override
    public void setModelAttributes(Map<String, Object> attributes) {
        String uuid = (String) attributes.get("uuid");

        if (uuid != null) {
            setUuid(uuid);
        }

        Long _id = (Long) attributes.get("_id");

        if (_id != null) {
            set_id(_id);
        }

        Long userLiferayId = (Long) attributes.get("userLiferayId");

        if (userLiferayId != null) {
            setUserLiferayId(userLiferayId);
        }

        String metadataName = (String) attributes.get("metadataName");

        if (metadataName != null) {
            setMetadataName(metadataName);
        }

        String text = (String) attributes.get("text");

        if (text != null) {
            setText(text);
        }

        Date created = (Date) attributes.get("created");

        if (created != null) {
            setCreated(created);
        }
    }

    /**
    * Returns the primary key of this metadata comment.
    *
    * @return the primary key of this metadata comment
    */
    @Override
    public long getPrimaryKey() {
        return _metadataComment.getPrimaryKey();
    }

    /**
    * Sets the primary key of this metadata comment.
    *
    * @param primaryKey the primary key of this metadata comment
    */
    @Override
    public void setPrimaryKey(long primaryKey) {
        _metadataComment.setPrimaryKey(primaryKey);
    }

    /**
    * Returns the uuid of this metadata comment.
    *
    * @return the uuid of this metadata comment
    */
    @Override
    public java.lang.String getUuid() {
        return _metadataComment.getUuid();
    }

    /**
    * Sets the uuid of this metadata comment.
    *
    * @param uuid the uuid of this metadata comment
    */
    @Override
    public void setUuid(java.lang.String uuid) {
        _metadataComment.setUuid(uuid);
    }

    /**
    * Returns the _id of this metadata comment.
    *
    * @return the _id of this metadata comment
    */
    @Override
    public long get_id() {
        return _metadataComment.get_id();
    }

    /**
    * Sets the _id of this metadata comment.
    *
    * @param _id the _id of this metadata comment
    */
    @Override
    public void set_id(long _id) {
        _metadataComment.set_id(_id);
    }

    /**
    * Returns the user liferay ID of this metadata comment.
    *
    * @return the user liferay ID of this metadata comment
    */
    @Override
    public long getUserLiferayId() {
        return _metadataComment.getUserLiferayId();
    }

    /**
    * Sets the user liferay ID of this metadata comment.
    *
    * @param userLiferayId the user liferay ID of this metadata comment
    */
    @Override
    public void setUserLiferayId(long userLiferayId) {
        _metadataComment.setUserLiferayId(userLiferayId);
    }

    /**
    * Returns the metadata name of this metadata comment.
    *
    * @return the metadata name of this metadata comment
    */
    @Override
    public java.lang.String getMetadataName() {
        return _metadataComment.getMetadataName();
    }

    /**
    * Sets the metadata name of this metadata comment.
    *
    * @param metadataName the metadata name of this metadata comment
    */
    @Override
    public void setMetadataName(java.lang.String metadataName) {
        _metadataComment.setMetadataName(metadataName);
    }

    /**
    * Returns the text of this metadata comment.
    *
    * @return the text of this metadata comment
    */
    @Override
    public java.lang.String getText() {
        return _metadataComment.getText();
    }

    /**
    * Sets the text of this metadata comment.
    *
    * @param text the text of this metadata comment
    */
    @Override
    public void setText(java.lang.String text) {
        _metadataComment.setText(text);
    }

    /**
    * Returns the created of this metadata comment.
    *
    * @return the created of this metadata comment
    */
    @Override
    public java.util.Date getCreated() {
        return _metadataComment.getCreated();
    }

    /**
    * Sets the created of this metadata comment.
    *
    * @param created the created of this metadata comment
    */
    @Override
    public void setCreated(java.util.Date created) {
        _metadataComment.setCreated(created);
    }

    @Override
    public boolean isNew() {
        return _metadataComment.isNew();
    }

    @Override
    public void setNew(boolean n) {
        _metadataComment.setNew(n);
    }

    @Override
    public boolean isCachedModel() {
        return _metadataComment.isCachedModel();
    }

    @Override
    public void setCachedModel(boolean cachedModel) {
        _metadataComment.setCachedModel(cachedModel);
    }

    @Override
    public boolean isEscapedModel() {
        return _metadataComment.isEscapedModel();
    }

    @Override
    public java.io.Serializable getPrimaryKeyObj() {
        return _metadataComment.getPrimaryKeyObj();
    }

    @Override
    public void setPrimaryKeyObj(java.io.Serializable primaryKeyObj) {
        _metadataComment.setPrimaryKeyObj(primaryKeyObj);
    }

    @Override
    public com.liferay.portlet.expando.model.ExpandoBridge getExpandoBridge() {
        return _metadataComment.getExpandoBridge();
    }

    @Override
    public void setExpandoBridgeAttributes(
        com.liferay.portal.model.BaseModel<?> baseModel) {
        _metadataComment.setExpandoBridgeAttributes(baseModel);
    }

    @Override
    public void setExpandoBridgeAttributes(
        com.liferay.portlet.expando.model.ExpandoBridge expandoBridge) {
        _metadataComment.setExpandoBridgeAttributes(expandoBridge);
    }

    @Override
    public void setExpandoBridgeAttributes(
        com.liferay.portal.service.ServiceContext serviceContext) {
        _metadataComment.setExpandoBridgeAttributes(serviceContext);
    }

    @Override
    public java.lang.Object clone() {
        return new MetadataCommentWrapper((MetadataComment) _metadataComment.clone());
    }

    @Override
    public int compareTo(
        de.fhg.fokus.odp.entities.model.MetadataComment metadataComment) {
        return _metadataComment.compareTo(metadataComment);
    }

    @Override
    public int hashCode() {
        return _metadataComment.hashCode();
    }

    @Override
    public com.liferay.portal.model.CacheModel<de.fhg.fokus.odp.entities.model.MetadataComment> toCacheModel() {
        return _metadataComment.toCacheModel();
    }

    @Override
    public de.fhg.fokus.odp.entities.model.MetadataComment toEscapedModel() {
        return new MetadataCommentWrapper(_metadataComment.toEscapedModel());
    }

    @Override
    public de.fhg.fokus.odp.entities.model.MetadataComment toUnescapedModel() {
        return new MetadataCommentWrapper(_metadataComment.toUnescapedModel());
    }

    @Override
    public java.lang.String toString() {
        return _metadataComment.toString();
    }

    @Override
    public java.lang.String toXmlString() {
        return _metadataComment.toXmlString();
    }

    @Override
    public void persist()
        throws com.liferay.portal.kernel.exception.SystemException {
        _metadataComment.persist();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof MetadataCommentWrapper)) {
            return false;
        }

        MetadataCommentWrapper metadataCommentWrapper = (MetadataCommentWrapper) obj;

        if (Validator.equals(_metadataComment,
                    metadataCommentWrapper._metadataComment)) {
            return true;
        }

        return false;
    }

    /**
     * @deprecated As of 6.1.0, replaced by {@link #getWrappedModel}
     */
    public MetadataComment getWrappedMetadataComment() {
        return _metadataComment;
    }

    @Override
    public MetadataComment getWrappedModel() {
        return _metadataComment;
    }

    @Override
    public void resetOriginalValues() {
        _metadataComment.resetOriginalValues();
    }
}
