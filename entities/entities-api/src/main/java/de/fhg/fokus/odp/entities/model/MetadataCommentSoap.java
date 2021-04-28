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

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This class is used by SOAP remote services.
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class MetadataCommentSoap implements Serializable {

	public static MetadataCommentSoap toSoapModel(MetadataComment model) {
		MetadataCommentSoap soapModel = new MetadataCommentSoap();

		soapModel.setUuid(model.getUuid());
		soapModel.set_id(model.get_id());
		soapModel.setUserLiferayId(model.getUserLiferayId());
		soapModel.setMetadataName(model.getMetadataName());
		soapModel.setText(model.getText());
		soapModel.setCreated(model.getCreated());

		return soapModel;
	}

	public static MetadataCommentSoap[] toSoapModels(MetadataComment[] models) {
		MetadataCommentSoap[] soapModels =
			new MetadataCommentSoap[models.length];

		for (int i = 0; i < models.length; i++) {
			soapModels[i] = toSoapModel(models[i]);
		}

		return soapModels;
	}

	public static MetadataCommentSoap[][] toSoapModels(
		MetadataComment[][] models) {

		MetadataCommentSoap[][] soapModels = null;

		if (models.length > 0) {
			soapModels =
				new MetadataCommentSoap[models.length][models[0].length];
		}
		else {
			soapModels = new MetadataCommentSoap[0][0];
		}

		for (int i = 0; i < models.length; i++) {
			soapModels[i] = toSoapModels(models[i]);
		}

		return soapModels;
	}

	public static MetadataCommentSoap[] toSoapModels(
		List<MetadataComment> models) {

		List<MetadataCommentSoap> soapModels =
			new ArrayList<MetadataCommentSoap>(models.size());

		for (MetadataComment model : models) {
			soapModels.add(toSoapModel(model));
		}

		return soapModels.toArray(new MetadataCommentSoap[soapModels.size()]);
	}

	public MetadataCommentSoap() {
	}

	public long getPrimaryKey() {
		return __id;
	}

	public void setPrimaryKey(long pk) {
		set_id(pk);
	}

	public String getUuid() {
		return _uuid;
	}

	public void setUuid(String uuid) {
		_uuid = uuid;
	}

	public long get_id() {
		return __id;
	}

	public void set_id(long _id) {
		__id = _id;
	}

	public long getUserLiferayId() {
		return _userLiferayId;
	}

	public void setUserLiferayId(long userLiferayId) {
		_userLiferayId = userLiferayId;
	}

	public String getMetadataName() {
		return _metadataName;
	}

	public void setMetadataName(String metadataName) {
		_metadataName = metadataName;
	}

	public String getText() {
		return _text;
	}

	public void setText(String text) {
		_text = text;
	}

	public Date getCreated() {
		return _created;
	}

	public void setCreated(Date created) {
		_created = created;
	}

	private String _uuid;
	private long __id;
	private long _userLiferayId;
	private String _metadataName;
	private String _text;
	private Date _created;

}