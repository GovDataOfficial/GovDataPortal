package de.fhg.fokus.odp.entities.service.persistence;

import com.liferay.portal.kernel.dao.orm.BaseActionableDynamicQuery;
import com.liferay.portal.kernel.exception.SystemException;

import de.fhg.fokus.odp.entities.model.MetadataComment;
import de.fhg.fokus.odp.entities.service.MetadataCommentLocalServiceUtil;

/**
 * @author Brian Wing Shun Chan
 * @generated
 */
public abstract class MetadataCommentActionableDynamicQuery
    extends BaseActionableDynamicQuery {
    public MetadataCommentActionableDynamicQuery() throws SystemException {
        setBaseLocalService(MetadataCommentLocalServiceUtil.getService());
        setClass(MetadataComment.class);

        setClassLoader(de.fhg.fokus.odp.entities.service.ClpSerializer.class.getClassLoader());

        setPrimaryKeyPropertyName("_id");
    }
}
