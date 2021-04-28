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

package de.fhg.fokus.odp.entities.model.impl;

import com.liferay.petra.lang.HashUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.CacheModel;

import de.fhg.fokus.odp.entities.model.MetadataComment;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import java.util.Date;

/**
 * The cache model class for representing MetadataComment in entity cache.
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class MetadataCommentCacheModel
	implements CacheModel<MetadataComment>, Externalizable {

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof MetadataCommentCacheModel)) {
			return false;
		}

		MetadataCommentCacheModel metadataCommentCacheModel =
			(MetadataCommentCacheModel)obj;

		if (_id == metadataCommentCacheModel._id) {
			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		return HashUtil.hash(0, _id);
	}

	@Override
	public String toString() {
		StringBundler sb = new StringBundler(13);

		sb.append("{uuid=");
		sb.append(uuid);
		sb.append(", _id=");
		sb.append(_id);
		sb.append(", userLiferayId=");
		sb.append(userLiferayId);
		sb.append(", metadataName=");
		sb.append(metadataName);
		sb.append(", text=");
		sb.append(text);
		sb.append(", created=");
		sb.append(created);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public MetadataComment toEntityModel() {
		MetadataCommentImpl metadataCommentImpl = new MetadataCommentImpl();

		if (uuid == null) {
			metadataCommentImpl.setUuid("");
		}
		else {
			metadataCommentImpl.setUuid(uuid);
		}

		metadataCommentImpl.set_id(_id);
		metadataCommentImpl.setUserLiferayId(userLiferayId);

		if (metadataName == null) {
			metadataCommentImpl.setMetadataName("");
		}
		else {
			metadataCommentImpl.setMetadataName(metadataName);
		}

		if (text == null) {
			metadataCommentImpl.setText("");
		}
		else {
			metadataCommentImpl.setText(text);
		}

		if (created == Long.MIN_VALUE) {
			metadataCommentImpl.setCreated(null);
		}
		else {
			metadataCommentImpl.setCreated(new Date(created));
		}

		metadataCommentImpl.resetOriginalValues();

		return metadataCommentImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput) throws IOException {
		uuid = objectInput.readUTF();

		_id = objectInput.readLong();

		userLiferayId = objectInput.readLong();
		metadataName = objectInput.readUTF();
		text = objectInput.readUTF();
		created = objectInput.readLong();
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		if (uuid == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(uuid);
		}

		objectOutput.writeLong(_id);

		objectOutput.writeLong(userLiferayId);

		if (metadataName == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(metadataName);
		}

		if (text == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(text);
		}

		objectOutput.writeLong(created);
	}

	public String uuid;
	public long _id;
	public long userLiferayId;
	public String metadataName;
	public String text;
	public long created;

}