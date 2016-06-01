package de.fhg.fokus.odp.entities.service;

import com.liferay.portal.kernel.bean.PortletBeanLocatorUtil;
import com.liferay.portal.kernel.util.ReferenceRegistry;
import com.liferay.portal.service.InvokableLocalService;

/**
 * Provides the local service utility for MetadataComment. This utility wraps
 * {@link de.fhg.fokus.odp.entities.service.impl.MetadataCommentLocalServiceImpl} and is the
 * primary access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see MetadataCommentLocalService
 * @see de.fhg.fokus.odp.entities.service.base.MetadataCommentLocalServiceBaseImpl
 * @see de.fhg.fokus.odp.entities.service.impl.MetadataCommentLocalServiceImpl
 * @generated
 */
public class MetadataCommentLocalServiceUtil {
    private static MetadataCommentLocalService _service;

    /*
     * NOTE FOR DEVELOPERS:
     *
     * Never modify this class directly. Add custom service methods to {@link de.fhg.fokus.odp.entities.service.impl.MetadataCommentLocalServiceImpl} and rerun ServiceBuilder to regenerate this class.
     */

    /**
    * Adds the metadata comment to the database. Also notifies the appropriate model listeners.
    *
    * @param metadataComment the metadata comment
    * @return the metadata comment that was added
    * @throws SystemException if a system exception occurred
    */
    public static de.fhg.fokus.odp.entities.model.MetadataComment addMetadataComment(
        de.fhg.fokus.odp.entities.model.MetadataComment metadataComment)
        throws com.liferay.portal.kernel.exception.SystemException {
        return getService().addMetadataComment(metadataComment);
    }

    /**
    * Creates a new metadata comment with the primary key. Does not add the metadata comment to the database.
    *
    * @param _id the primary key for the new metadata comment
    * @return the new metadata comment
    */
    public static de.fhg.fokus.odp.entities.model.MetadataComment createMetadataComment(
        long _id) {
        return getService().createMetadataComment(_id);
    }

    /**
    * Deletes the metadata comment with the primary key from the database. Also notifies the appropriate model listeners.
    *
    * @param _id the primary key of the metadata comment
    * @return the metadata comment that was removed
    * @throws PortalException if a metadata comment with the primary key could not be found
    * @throws SystemException if a system exception occurred
    */
    public static de.fhg.fokus.odp.entities.model.MetadataComment deleteMetadataComment(
        long _id)
        throws com.liferay.portal.kernel.exception.PortalException,
            com.liferay.portal.kernel.exception.SystemException {
        return getService().deleteMetadataComment(_id);
    }

    /**
    * Deletes the metadata comment from the database. Also notifies the appropriate model listeners.
    *
    * @param metadataComment the metadata comment
    * @return the metadata comment that was removed
    * @throws SystemException if a system exception occurred
    */
    public static de.fhg.fokus.odp.entities.model.MetadataComment deleteMetadataComment(
        de.fhg.fokus.odp.entities.model.MetadataComment metadataComment)
        throws com.liferay.portal.kernel.exception.SystemException {
        return getService().deleteMetadataComment(metadataComment);
    }

    public static com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
        return getService().dynamicQuery();
    }

    /**
    * Performs a dynamic query on the database and returns the matching rows.
    *
    * @param dynamicQuery the dynamic query
    * @return the matching rows
    * @throws SystemException if a system exception occurred
    */
    @SuppressWarnings("rawtypes")
    public static java.util.List dynamicQuery(
        com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery)
        throws com.liferay.portal.kernel.exception.SystemException {
        return getService().dynamicQuery(dynamicQuery);
    }

    /**
    * Performs a dynamic query on the database and returns a range of the matching rows.
    *
    * <p>
    * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link de.fhg.fokus.odp.entities.model.impl.MetadataCommentModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
    * </p>
    *
    * @param dynamicQuery the dynamic query
    * @param start the lower bound of the range of model instances
    * @param end the upper bound of the range of model instances (not inclusive)
    * @return the range of matching rows
    * @throws SystemException if a system exception occurred
    */
    @SuppressWarnings("rawtypes")
    public static java.util.List dynamicQuery(
        com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
        int end) throws com.liferay.portal.kernel.exception.SystemException {
        return getService().dynamicQuery(dynamicQuery, start, end);
    }

    /**
    * Performs a dynamic query on the database and returns an ordered range of the matching rows.
    *
    * <p>
    * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link de.fhg.fokus.odp.entities.model.impl.MetadataCommentModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
    * </p>
    *
    * @param dynamicQuery the dynamic query
    * @param start the lower bound of the range of model instances
    * @param end the upper bound of the range of model instances (not inclusive)
    * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
    * @return the ordered range of matching rows
    * @throws SystemException if a system exception occurred
    */
    @SuppressWarnings("rawtypes")
    public static java.util.List dynamicQuery(
        com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
        int end,
        com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
        throws com.liferay.portal.kernel.exception.SystemException {
        return getService()
                   .dynamicQuery(dynamicQuery, start, end, orderByComparator);
    }

    /**
    * Returns the number of rows that match the dynamic query.
    *
    * @param dynamicQuery the dynamic query
    * @return the number of rows that match the dynamic query
    * @throws SystemException if a system exception occurred
    */
    public static long dynamicQueryCount(
        com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery)
        throws com.liferay.portal.kernel.exception.SystemException {
        return getService().dynamicQueryCount(dynamicQuery);
    }

    /**
    * Returns the number of rows that match the dynamic query.
    *
    * @param dynamicQuery the dynamic query
    * @param projection the projection to apply to the query
    * @return the number of rows that match the dynamic query
    * @throws SystemException if a system exception occurred
    */
    public static long dynamicQueryCount(
        com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery,
        com.liferay.portal.kernel.dao.orm.Projection projection)
        throws com.liferay.portal.kernel.exception.SystemException {
        return getService().dynamicQueryCount(dynamicQuery, projection);
    }

    public static de.fhg.fokus.odp.entities.model.MetadataComment fetchMetadataComment(
        long _id) throws com.liferay.portal.kernel.exception.SystemException {
        return getService().fetchMetadataComment(_id);
    }

    /**
    * Returns the metadata comment with the primary key.
    *
    * @param _id the primary key of the metadata comment
    * @return the metadata comment
    * @throws PortalException if a metadata comment with the primary key could not be found
    * @throws SystemException if a system exception occurred
    */
    public static de.fhg.fokus.odp.entities.model.MetadataComment getMetadataComment(
        long _id)
        throws com.liferay.portal.kernel.exception.PortalException,
            com.liferay.portal.kernel.exception.SystemException {
        return getService().getMetadataComment(_id);
    }

    public static com.liferay.portal.model.PersistedModel getPersistedModel(
        java.io.Serializable primaryKeyObj)
        throws com.liferay.portal.kernel.exception.PortalException,
            com.liferay.portal.kernel.exception.SystemException {
        return getService().getPersistedModel(primaryKeyObj);
    }

    /**
    * Returns a range of all the metadata comments.
    *
    * <p>
    * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link de.fhg.fokus.odp.entities.model.impl.MetadataCommentModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
    * </p>
    *
    * @param start the lower bound of the range of metadata comments
    * @param end the upper bound of the range of metadata comments (not inclusive)
    * @return the range of metadata comments
    * @throws SystemException if a system exception occurred
    */
    public static java.util.List<de.fhg.fokus.odp.entities.model.MetadataComment> getMetadataComments(
        int start, int end)
        throws com.liferay.portal.kernel.exception.SystemException {
        return getService().getMetadataComments(start, end);
    }

    /**
    * Returns the number of metadata comments.
    *
    * @return the number of metadata comments
    * @throws SystemException if a system exception occurred
    */
    public static int getMetadataCommentsCount()
        throws com.liferay.portal.kernel.exception.SystemException {
        return getService().getMetadataCommentsCount();
    }

    /**
    * Updates the metadata comment in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
    *
    * @param metadataComment the metadata comment
    * @return the metadata comment that was updated
    * @throws SystemException if a system exception occurred
    */
    public static de.fhg.fokus.odp.entities.model.MetadataComment updateMetadataComment(
        de.fhg.fokus.odp.entities.model.MetadataComment metadataComment)
        throws com.liferay.portal.kernel.exception.SystemException {
        return getService().updateMetadataComment(metadataComment);
    }

    /**
    * Returns the Spring bean ID for this bean.
    *
    * @return the Spring bean ID for this bean
    */
    public static java.lang.String getBeanIdentifier() {
        return getService().getBeanIdentifier();
    }

    /**
    * Sets the Spring bean ID for this bean.
    *
    * @param beanIdentifier the Spring bean ID for this bean
    */
    public static void setBeanIdentifier(java.lang.String beanIdentifier) {
        getService().setBeanIdentifier(beanIdentifier);
    }

    public static java.lang.Object invokeMethod(java.lang.String name,
        java.lang.String[] parameterTypes, java.lang.Object[] arguments)
        throws java.lang.Throwable {
        return getService().invokeMethod(name, parameterTypes, arguments);
    }

    /**
    * Find byuser liferay id.
    *
    * @param userLiferayId
    the user liferay id
    * @return the list<de.fhg.fokus.odp.entities.model. metadata comment>
    * @throws SystemException
    the system exception
    */
    public static java.util.List<de.fhg.fokus.odp.entities.model.MetadataComment> findByuserLiferayId(
        long userLiferayId)
        throws com.liferay.portal.kernel.exception.SystemException {
        return getService().findByuserLiferayId(userLiferayId);
    }

    /**
    * Find bymetadata name.
    *
    * @param metadataName
    the metadata name
    * @return the java.util. list
    * @throws SystemException
    the system exception
    */
    public static java.util.List<de.fhg.fokus.odp.entities.model.MetadataComment> findBymetadataName(
        java.lang.String metadataName)
        throws com.liferay.portal.kernel.exception.SystemException {
        return getService().findBymetadataName(metadataName);
    }

    public static void clearService() {
        _service = null;
    }

    public static MetadataCommentLocalService getService() {
        if (_service == null) {
            InvokableLocalService invokableLocalService = (InvokableLocalService) PortletBeanLocatorUtil.locate(ClpSerializer.getServletContextName(),
                    MetadataCommentLocalService.class.getName());

            if (invokableLocalService instanceof MetadataCommentLocalService) {
                _service = (MetadataCommentLocalService) invokableLocalService;
            } else {
                _service = new MetadataCommentLocalServiceClp(invokableLocalService);
            }

            ReferenceRegistry.registerReference(MetadataCommentLocalServiceUtil.class,
                "_service");
        }

        return _service;
    }

    /**
     * @deprecated As of 6.2.0
     */
    public void setService(MetadataCommentLocalService service) {
    }
}
