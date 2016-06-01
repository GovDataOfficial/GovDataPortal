package de.fhg.fokus.odp.entities.model;

import com.liferay.portal.kernel.bean.AutoEscapeBeanHandler;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.model.BaseModel;
import com.liferay.portal.model.impl.BaseModelImpl;

import de.fhg.fokus.odp.entities.service.ClpSerializer;
import de.fhg.fokus.odp.entities.service.MetadataCommentLocalServiceUtil;

import java.io.Serializable;

import java.lang.reflect.Method;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class MetadataCommentClp extends BaseModelImpl<MetadataComment>
    implements MetadataComment {
    private String _uuid;
    private long __id;
    private long _userLiferayId;
    private String _metadataName;
    private String _text;
    private Date _created;
    private BaseModel<?> _metadataCommentRemoteModel;
    private Class<?> _clpSerializerClass = de.fhg.fokus.odp.entities.service.ClpSerializer.class;

    public MetadataCommentClp() {
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
    public long getPrimaryKey() {
        return __id;
    }

    @Override
    public void setPrimaryKey(long primaryKey) {
        set_id(primaryKey);
    }

    @Override
    public Serializable getPrimaryKeyObj() {
        return __id;
    }

    @Override
    public void setPrimaryKeyObj(Serializable primaryKeyObj) {
        setPrimaryKey(((Long) primaryKeyObj).longValue());
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

    @Override
    public String getUuid() {
        return _uuid;
    }

    @Override
    public void setUuid(String uuid) {
        _uuid = uuid;

        if (_metadataCommentRemoteModel != null) {
            try {
                Class<?> clazz = _metadataCommentRemoteModel.getClass();

                Method method = clazz.getMethod("setUuid", String.class);

                method.invoke(_metadataCommentRemoteModel, uuid);
            } catch (Exception e) {
                throw new UnsupportedOperationException(e);
            }
        }
    }

    @Override
    public long get_id() {
        return __id;
    }

    @Override
    public void set_id(long _id) {
        __id = _id;

        if (_metadataCommentRemoteModel != null) {
            try {
                Class<?> clazz = _metadataCommentRemoteModel.getClass();

                Method method = clazz.getMethod("set_id", long.class);

                method.invoke(_metadataCommentRemoteModel, _id);
            } catch (Exception e) {
                throw new UnsupportedOperationException(e);
            }
        }
    }

    @Override
    public long getUserLiferayId() {
        return _userLiferayId;
    }

    @Override
    public void setUserLiferayId(long userLiferayId) {
        _userLiferayId = userLiferayId;

        if (_metadataCommentRemoteModel != null) {
            try {
                Class<?> clazz = _metadataCommentRemoteModel.getClass();

                Method method = clazz.getMethod("setUserLiferayId", long.class);

                method.invoke(_metadataCommentRemoteModel, userLiferayId);
            } catch (Exception e) {
                throw new UnsupportedOperationException(e);
            }
        }
    }

    @Override
    public String getMetadataName() {
        return _metadataName;
    }

    @Override
    public void setMetadataName(String metadataName) {
        _metadataName = metadataName;

        if (_metadataCommentRemoteModel != null) {
            try {
                Class<?> clazz = _metadataCommentRemoteModel.getClass();

                Method method = clazz.getMethod("setMetadataName", String.class);

                method.invoke(_metadataCommentRemoteModel, metadataName);
            } catch (Exception e) {
                throw new UnsupportedOperationException(e);
            }
        }
    }

    @Override
    public String getText() {
        return _text;
    }

    @Override
    public void setText(String text) {
        _text = text;

        if (_metadataCommentRemoteModel != null) {
            try {
                Class<?> clazz = _metadataCommentRemoteModel.getClass();

                Method method = clazz.getMethod("setText", String.class);

                method.invoke(_metadataCommentRemoteModel, text);
            } catch (Exception e) {
                throw new UnsupportedOperationException(e);
            }
        }
    }

    @Override
    public Date getCreated() {
        return _created;
    }

    @Override
    public void setCreated(Date created) {
        _created = created;

        if (_metadataCommentRemoteModel != null) {
            try {
                Class<?> clazz = _metadataCommentRemoteModel.getClass();

                Method method = clazz.getMethod("setCreated", Date.class);

                method.invoke(_metadataCommentRemoteModel, created);
            } catch (Exception e) {
                throw new UnsupportedOperationException(e);
            }
        }
    }

    public BaseModel<?> getMetadataCommentRemoteModel() {
        return _metadataCommentRemoteModel;
    }

    public void setMetadataCommentRemoteModel(
        BaseModel<?> metadataCommentRemoteModel) {
        _metadataCommentRemoteModel = metadataCommentRemoteModel;
    }

    public Object invokeOnRemoteModel(String methodName,
        Class<?>[] parameterTypes, Object[] parameterValues)
        throws Exception {
        Object[] remoteParameterValues = new Object[parameterValues.length];

        for (int i = 0; i < parameterValues.length; i++) {
            if (parameterValues[i] != null) {
                remoteParameterValues[i] = ClpSerializer.translateInput(parameterValues[i]);
            }
        }

        Class<?> remoteModelClass = _metadataCommentRemoteModel.getClass();

        ClassLoader remoteModelClassLoader = remoteModelClass.getClassLoader();

        Class<?>[] remoteParameterTypes = new Class[parameterTypes.length];

        for (int i = 0; i < parameterTypes.length; i++) {
            if (parameterTypes[i].isPrimitive()) {
                remoteParameterTypes[i] = parameterTypes[i];
            } else {
                String parameterTypeName = parameterTypes[i].getName();

                remoteParameterTypes[i] = remoteModelClassLoader.loadClass(parameterTypeName);
            }
        }

        Method method = remoteModelClass.getMethod(methodName,
                remoteParameterTypes);

        Object returnValue = method.invoke(_metadataCommentRemoteModel,
                remoteParameterValues);

        if (returnValue != null) {
            returnValue = ClpSerializer.translateOutput(returnValue);
        }

        return returnValue;
    }

    @Override
    public void persist() throws SystemException {
        if (this.isNew()) {
            MetadataCommentLocalServiceUtil.addMetadataComment(this);
        } else {
            MetadataCommentLocalServiceUtil.updateMetadataComment(this);
        }
    }

    @Override
    public MetadataComment toEscapedModel() {
        return (MetadataComment) ProxyUtil.newProxyInstance(MetadataComment.class.getClassLoader(),
            new Class[] { MetadataComment.class },
            new AutoEscapeBeanHandler(this));
    }

    @Override
    public Object clone() {
        MetadataCommentClp clone = new MetadataCommentClp();

        clone.setUuid(getUuid());
        clone.set_id(get_id());
        clone.setUserLiferayId(getUserLiferayId());
        clone.setMetadataName(getMetadataName());
        clone.setText(getText());
        clone.setCreated(getCreated());

        return clone;
    }

    @Override
    public int compareTo(MetadataComment metadataComment) {
        int value = 0;

        value = DateUtil.compareTo(getCreated(), metadataComment.getCreated());

        if (value != 0) {
            return value;
        }

        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof MetadataCommentClp)) {
            return false;
        }

        MetadataCommentClp metadataComment = (MetadataCommentClp) obj;

        long primaryKey = metadataComment.getPrimaryKey();

        if (getPrimaryKey() == primaryKey) {
            return true;
        } else {
            return false;
        }
    }

    public Class<?> getClpSerializerClass() {
        return _clpSerializerClass;
    }

    @Override
    public int hashCode() {
        return (int) getPrimaryKey();
    }

    @Override
    public String toString() {
        StringBundler sb = new StringBundler(13);

        sb.append("{uuid=");
        sb.append(getUuid());
        sb.append(", _id=");
        sb.append(get_id());
        sb.append(", userLiferayId=");
        sb.append(getUserLiferayId());
        sb.append(", metadataName=");
        sb.append(getMetadataName());
        sb.append(", text=");
        sb.append(getText());
        sb.append(", created=");
        sb.append(getCreated());
        sb.append("}");

        return sb.toString();
    }

    @Override
    public String toXmlString() {
        StringBundler sb = new StringBundler(22);

        sb.append("<model><model-name>");
        sb.append("de.fhg.fokus.odp.entities.model.MetadataComment");
        sb.append("</model-name>");

        sb.append(
            "<column><column-name>uuid</column-name><column-value><![CDATA[");
        sb.append(getUuid());
        sb.append("]]></column-value></column>");
        sb.append(
            "<column><column-name>_id</column-name><column-value><![CDATA[");
        sb.append(get_id());
        sb.append("]]></column-value></column>");
        sb.append(
            "<column><column-name>userLiferayId</column-name><column-value><![CDATA[");
        sb.append(getUserLiferayId());
        sb.append("]]></column-value></column>");
        sb.append(
            "<column><column-name>metadataName</column-name><column-value><![CDATA[");
        sb.append(getMetadataName());
        sb.append("]]></column-value></column>");
        sb.append(
            "<column><column-name>text</column-name><column-value><![CDATA[");
        sb.append(getText());
        sb.append("]]></column-value></column>");
        sb.append(
            "<column><column-name>created</column-name><column-value><![CDATA[");
        sb.append(getCreated());
        sb.append("]]></column-value></column>");

        sb.append("</model>");

        return sb.toString();
    }
}
