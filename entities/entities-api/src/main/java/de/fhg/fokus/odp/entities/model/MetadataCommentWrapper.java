/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package de.fhg.fokus.odp.entities.model;

import com.liferay.portal.kernel.model.ModelWrapper;
import com.liferay.portal.kernel.model.wrapper.BaseModelWrapper;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * This class is a wrapper for {@link MetadataComment}.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see MetadataComment
 * @generated
 */
public class MetadataCommentWrapper
	extends BaseModelWrapper<MetadataComment>
	implements MetadataComment, ModelWrapper<MetadataComment> {

	public MetadataCommentWrapper(MetadataComment metadataComment) {
		super(metadataComment);
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
		String uuid = (String)attributes.get("uuid");

		if (uuid != null) {
			setUuid(uuid);
		}

		Long _id = (Long)attributes.get("_id");

		if (_id != null) {
			set_id(_id);
		}

		Long userLiferayId = (Long)attributes.get("userLiferayId");

		if (userLiferayId != null) {
			setUserLiferayId(userLiferayId);
		}

		String metadataName = (String)attributes.get("metadataName");

		if (metadataName != null) {
			setMetadataName(metadataName);
		}

		String text = (String)attributes.get("text");

		if (text != null) {
			setText(text);
		}

		Date created = (Date)attributes.get("created");

		if (created != null) {
			setCreated(created);
		}
	}

	@Override
	public MetadataComment cloneWithOriginalValues() {
		return wrap(model.cloneWithOriginalValues());
	}

	/**
	 * Returns the _id of this metadata comment.
	 *
	 * @return the _id of this metadata comment
	 */
	@Override
	public long get_id() {
		return model.get_id();
	}

	/**
	 * Returns the created of this metadata comment.
	 *
	 * @return the created of this metadata comment
	 */
	@Override
	public Date getCreated() {
		return model.getCreated();
	}

	/**
	 * Returns the metadata name of this metadata comment.
	 *
	 * @return the metadata name of this metadata comment
	 */
	@Override
	public String getMetadataName() {
		return model.getMetadataName();
	}

	/**
	 * Returns the primary key of this metadata comment.
	 *
	 * @return the primary key of this metadata comment
	 */
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	/**
	 * Returns the text of this metadata comment.
	 *
	 * @return the text of this metadata comment
	 */
	@Override
	public String getText() {
		return model.getText();
	}

	/**
	 * Returns the user liferay ID of this metadata comment.
	 *
	 * @return the user liferay ID of this metadata comment
	 */
	@Override
	public long getUserLiferayId() {
		return model.getUserLiferayId();
	}

	/**
	 * Returns the uuid of this metadata comment.
	 *
	 * @return the uuid of this metadata comment
	 */
	@Override
	public String getUuid() {
		return model.getUuid();
	}

	@Override
	public void persist() {
		model.persist();
	}

	/**
	 * Sets the _id of this metadata comment.
	 *
	 * @param _id the _id of this metadata comment
	 */
	@Override
	public void set_id(long _id) {
		model.set_id(_id);
	}

	/**
	 * Sets the created of this metadata comment.
	 *
	 * @param created the created of this metadata comment
	 */
	@Override
	public void setCreated(Date created) {
		model.setCreated(created);
	}

	/**
	 * Sets the metadata name of this metadata comment.
	 *
	 * @param metadataName the metadata name of this metadata comment
	 */
	@Override
	public void setMetadataName(String metadataName) {
		model.setMetadataName(metadataName);
	}

	/**
	 * Sets the primary key of this metadata comment.
	 *
	 * @param primaryKey the primary key of this metadata comment
	 */
	@Override
	public void setPrimaryKey(long primaryKey) {
		model.setPrimaryKey(primaryKey);
	}

	/**
	 * Sets the text of this metadata comment.
	 *
	 * @param text the text of this metadata comment
	 */
	@Override
	public void setText(String text) {
		model.setText(text);
	}

	/**
	 * Sets the user liferay ID of this metadata comment.
	 *
	 * @param userLiferayId the user liferay ID of this metadata comment
	 */
	@Override
	public void setUserLiferayId(long userLiferayId) {
		model.setUserLiferayId(userLiferayId);
	}

	/**
	 * Sets the uuid of this metadata comment.
	 *
	 * @param uuid the uuid of this metadata comment
	 */
	@Override
	public void setUuid(String uuid) {
		model.setUuid(uuid);
	}

	@Override
	protected MetadataCommentWrapper wrap(MetadataComment metadataComment) {
		return new MetadataCommentWrapper(metadataComment);
	}

}