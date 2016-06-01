package de.fhg.fokus.odp.entities.model.impl;

import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.model.CacheModel;

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
 * @see MetadataComment
 * @generated
 */
public class MetadataCommentCacheModel implements CacheModel<MetadataComment>,
    Externalizable {
    public String uuid;
    public long _id;
    public long userLiferayId;
    public String metadataName;
    public String text;
    public long created;

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
            metadataCommentImpl.setUuid(StringPool.BLANK);
        } else {
            metadataCommentImpl.setUuid(uuid);
        }

        metadataCommentImpl.set_id(_id);
        metadataCommentImpl.setUserLiferayId(userLiferayId);

        if (metadataName == null) {
            metadataCommentImpl.setMetadataName(StringPool.BLANK);
        } else {
            metadataCommentImpl.setMetadataName(metadataName);
        }

        if (text == null) {
            metadataCommentImpl.setText(StringPool.BLANK);
        } else {
            metadataCommentImpl.setText(text);
        }

        if (created == Long.MIN_VALUE) {
            metadataCommentImpl.setCreated(null);
        } else {
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
    public void writeExternal(ObjectOutput objectOutput)
        throws IOException {
        if (uuid == null) {
            objectOutput.writeUTF(StringPool.BLANK);
        } else {
            objectOutput.writeUTF(uuid);
        }

        objectOutput.writeLong(_id);
        objectOutput.writeLong(userLiferayId);

        if (metadataName == null) {
            objectOutput.writeUTF(StringPool.BLANK);
        } else {
            objectOutput.writeUTF(metadataName);
        }

        if (text == null) {
            objectOutput.writeUTF(StringPool.BLANK);
        } else {
            objectOutput.writeUTF(text);
        }

        objectOutput.writeLong(created);
    }
}
