package de.fhg.fokus.odp.entities.service.persistence;

import com.liferay.portal.service.persistence.BasePersistence;

import de.fhg.fokus.odp.entities.model.MetadataComment;

/**
 * The persistence interface for the metadata comment service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see MetadataCommentPersistenceImpl
 * @see MetadataCommentUtil
 * @generated
 */
public interface MetadataCommentPersistence extends BasePersistence<MetadataComment> {
    /*
     * NOTE FOR DEVELOPERS:
     *
     * Never modify or reference this interface directly. Always use {@link MetadataCommentUtil} to access the metadata comment persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
     */

    /**
    * Returns all the metadata comments where uuid = &#63;.
    *
    * @param uuid the uuid
    * @return the matching metadata comments
    * @throws SystemException if a system exception occurred
    */
    public java.util.List<de.fhg.fokus.odp.entities.model.MetadataComment> findByUuid(
        java.lang.String uuid)
        throws com.liferay.portal.kernel.exception.SystemException;

    /**
    * Returns a range of all the metadata comments where uuid = &#63;.
    *
    * <p>
    * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link de.fhg.fokus.odp.entities.model.impl.MetadataCommentModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
    * </p>
    *
    * @param uuid the uuid
    * @param start the lower bound of the range of metadata comments
    * @param end the upper bound of the range of metadata comments (not inclusive)
    * @return the range of matching metadata comments
    * @throws SystemException if a system exception occurred
    */
    public java.util.List<de.fhg.fokus.odp.entities.model.MetadataComment> findByUuid(
        java.lang.String uuid, int start, int end)
        throws com.liferay.portal.kernel.exception.SystemException;

    /**
    * Returns an ordered range of all the metadata comments where uuid = &#63;.
    *
    * <p>
    * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link de.fhg.fokus.odp.entities.model.impl.MetadataCommentModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
    * </p>
    *
    * @param uuid the uuid
    * @param start the lower bound of the range of metadata comments
    * @param end the upper bound of the range of metadata comments (not inclusive)
    * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
    * @return the ordered range of matching metadata comments
    * @throws SystemException if a system exception occurred
    */
    public java.util.List<de.fhg.fokus.odp.entities.model.MetadataComment> findByUuid(
        java.lang.String uuid, int start, int end,
        com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
        throws com.liferay.portal.kernel.exception.SystemException;

    /**
    * Returns the first metadata comment in the ordered set where uuid = &#63;.
    *
    * @param uuid the uuid
    * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
    * @return the first matching metadata comment
    * @throws de.fhg.fokus.odp.entities.NoSuchMetadataCommentException if a matching metadata comment could not be found
    * @throws SystemException if a system exception occurred
    */
    public de.fhg.fokus.odp.entities.model.MetadataComment findByUuid_First(
        java.lang.String uuid,
        com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
        throws com.liferay.portal.kernel.exception.SystemException,
            de.fhg.fokus.odp.entities.NoSuchMetadataCommentException;

    /**
    * Returns the first metadata comment in the ordered set where uuid = &#63;.
    *
    * @param uuid the uuid
    * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
    * @return the first matching metadata comment, or <code>null</code> if a matching metadata comment could not be found
    * @throws SystemException if a system exception occurred
    */
    public de.fhg.fokus.odp.entities.model.MetadataComment fetchByUuid_First(
        java.lang.String uuid,
        com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
        throws com.liferay.portal.kernel.exception.SystemException;

    /**
    * Returns the last metadata comment in the ordered set where uuid = &#63;.
    *
    * @param uuid the uuid
    * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
    * @return the last matching metadata comment
    * @throws de.fhg.fokus.odp.entities.NoSuchMetadataCommentException if a matching metadata comment could not be found
    * @throws SystemException if a system exception occurred
    */
    public de.fhg.fokus.odp.entities.model.MetadataComment findByUuid_Last(
        java.lang.String uuid,
        com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
        throws com.liferay.portal.kernel.exception.SystemException,
            de.fhg.fokus.odp.entities.NoSuchMetadataCommentException;

    /**
    * Returns the last metadata comment in the ordered set where uuid = &#63;.
    *
    * @param uuid the uuid
    * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
    * @return the last matching metadata comment, or <code>null</code> if a matching metadata comment could not be found
    * @throws SystemException if a system exception occurred
    */
    public de.fhg.fokus.odp.entities.model.MetadataComment fetchByUuid_Last(
        java.lang.String uuid,
        com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
        throws com.liferay.portal.kernel.exception.SystemException;

    /**
    * Returns the metadata comments before and after the current metadata comment in the ordered set where uuid = &#63;.
    *
    * @param _id the primary key of the current metadata comment
    * @param uuid the uuid
    * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
    * @return the previous, current, and next metadata comment
    * @throws de.fhg.fokus.odp.entities.NoSuchMetadataCommentException if a metadata comment with the primary key could not be found
    * @throws SystemException if a system exception occurred
    */
    public de.fhg.fokus.odp.entities.model.MetadataComment[] findByUuid_PrevAndNext(
        long _id, java.lang.String uuid,
        com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
        throws com.liferay.portal.kernel.exception.SystemException,
            de.fhg.fokus.odp.entities.NoSuchMetadataCommentException;

    /**
    * Removes all the metadata comments where uuid = &#63; from the database.
    *
    * @param uuid the uuid
    * @throws SystemException if a system exception occurred
    */
    public void removeByUuid(java.lang.String uuid)
        throws com.liferay.portal.kernel.exception.SystemException;

    /**
    * Returns the number of metadata comments where uuid = &#63;.
    *
    * @param uuid the uuid
    * @return the number of matching metadata comments
    * @throws SystemException if a system exception occurred
    */
    public int countByUuid(java.lang.String uuid)
        throws com.liferay.portal.kernel.exception.SystemException;

    /**
    * Returns all the metadata comments where userLiferayId = &#63;.
    *
    * @param userLiferayId the user liferay ID
    * @return the matching metadata comments
    * @throws SystemException if a system exception occurred
    */
    public java.util.List<de.fhg.fokus.odp.entities.model.MetadataComment> findByuserLiferayId(
        long userLiferayId)
        throws com.liferay.portal.kernel.exception.SystemException;

    /**
    * Returns a range of all the metadata comments where userLiferayId = &#63;.
    *
    * <p>
    * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link de.fhg.fokus.odp.entities.model.impl.MetadataCommentModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
    * </p>
    *
    * @param userLiferayId the user liferay ID
    * @param start the lower bound of the range of metadata comments
    * @param end the upper bound of the range of metadata comments (not inclusive)
    * @return the range of matching metadata comments
    * @throws SystemException if a system exception occurred
    */
    public java.util.List<de.fhg.fokus.odp.entities.model.MetadataComment> findByuserLiferayId(
        long userLiferayId, int start, int end)
        throws com.liferay.portal.kernel.exception.SystemException;

    /**
    * Returns an ordered range of all the metadata comments where userLiferayId = &#63;.
    *
    * <p>
    * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link de.fhg.fokus.odp.entities.model.impl.MetadataCommentModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
    * </p>
    *
    * @param userLiferayId the user liferay ID
    * @param start the lower bound of the range of metadata comments
    * @param end the upper bound of the range of metadata comments (not inclusive)
    * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
    * @return the ordered range of matching metadata comments
    * @throws SystemException if a system exception occurred
    */
    public java.util.List<de.fhg.fokus.odp.entities.model.MetadataComment> findByuserLiferayId(
        long userLiferayId, int start, int end,
        com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
        throws com.liferay.portal.kernel.exception.SystemException;

    /**
    * Returns the first metadata comment in the ordered set where userLiferayId = &#63;.
    *
    * @param userLiferayId the user liferay ID
    * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
    * @return the first matching metadata comment
    * @throws de.fhg.fokus.odp.entities.NoSuchMetadataCommentException if a matching metadata comment could not be found
    * @throws SystemException if a system exception occurred
    */
    public de.fhg.fokus.odp.entities.model.MetadataComment findByuserLiferayId_First(
        long userLiferayId,
        com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
        throws com.liferay.portal.kernel.exception.SystemException,
            de.fhg.fokus.odp.entities.NoSuchMetadataCommentException;

    /**
    * Returns the first metadata comment in the ordered set where userLiferayId = &#63;.
    *
    * @param userLiferayId the user liferay ID
    * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
    * @return the first matching metadata comment, or <code>null</code> if a matching metadata comment could not be found
    * @throws SystemException if a system exception occurred
    */
    public de.fhg.fokus.odp.entities.model.MetadataComment fetchByuserLiferayId_First(
        long userLiferayId,
        com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
        throws com.liferay.portal.kernel.exception.SystemException;

    /**
    * Returns the last metadata comment in the ordered set where userLiferayId = &#63;.
    *
    * @param userLiferayId the user liferay ID
    * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
    * @return the last matching metadata comment
    * @throws de.fhg.fokus.odp.entities.NoSuchMetadataCommentException if a matching metadata comment could not be found
    * @throws SystemException if a system exception occurred
    */
    public de.fhg.fokus.odp.entities.model.MetadataComment findByuserLiferayId_Last(
        long userLiferayId,
        com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
        throws com.liferay.portal.kernel.exception.SystemException,
            de.fhg.fokus.odp.entities.NoSuchMetadataCommentException;

    /**
    * Returns the last metadata comment in the ordered set where userLiferayId = &#63;.
    *
    * @param userLiferayId the user liferay ID
    * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
    * @return the last matching metadata comment, or <code>null</code> if a matching metadata comment could not be found
    * @throws SystemException if a system exception occurred
    */
    public de.fhg.fokus.odp.entities.model.MetadataComment fetchByuserLiferayId_Last(
        long userLiferayId,
        com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
        throws com.liferay.portal.kernel.exception.SystemException;

    /**
    * Returns the metadata comments before and after the current metadata comment in the ordered set where userLiferayId = &#63;.
    *
    * @param _id the primary key of the current metadata comment
    * @param userLiferayId the user liferay ID
    * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
    * @return the previous, current, and next metadata comment
    * @throws de.fhg.fokus.odp.entities.NoSuchMetadataCommentException if a metadata comment with the primary key could not be found
    * @throws SystemException if a system exception occurred
    */
    public de.fhg.fokus.odp.entities.model.MetadataComment[] findByuserLiferayId_PrevAndNext(
        long _id, long userLiferayId,
        com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
        throws com.liferay.portal.kernel.exception.SystemException,
            de.fhg.fokus.odp.entities.NoSuchMetadataCommentException;

    /**
    * Removes all the metadata comments where userLiferayId = &#63; from the database.
    *
    * @param userLiferayId the user liferay ID
    * @throws SystemException if a system exception occurred
    */
    public void removeByuserLiferayId(long userLiferayId)
        throws com.liferay.portal.kernel.exception.SystemException;

    /**
    * Returns the number of metadata comments where userLiferayId = &#63;.
    *
    * @param userLiferayId the user liferay ID
    * @return the number of matching metadata comments
    * @throws SystemException if a system exception occurred
    */
    public int countByuserLiferayId(long userLiferayId)
        throws com.liferay.portal.kernel.exception.SystemException;

    /**
    * Returns all the metadata comments where metadataName = &#63;.
    *
    * @param metadataName the metadata name
    * @return the matching metadata comments
    * @throws SystemException if a system exception occurred
    */
    public java.util.List<de.fhg.fokus.odp.entities.model.MetadataComment> findBymetadataName(
        java.lang.String metadataName)
        throws com.liferay.portal.kernel.exception.SystemException;

    /**
    * Returns a range of all the metadata comments where metadataName = &#63;.
    *
    * <p>
    * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link de.fhg.fokus.odp.entities.model.impl.MetadataCommentModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
    * </p>
    *
    * @param metadataName the metadata name
    * @param start the lower bound of the range of metadata comments
    * @param end the upper bound of the range of metadata comments (not inclusive)
    * @return the range of matching metadata comments
    * @throws SystemException if a system exception occurred
    */
    public java.util.List<de.fhg.fokus.odp.entities.model.MetadataComment> findBymetadataName(
        java.lang.String metadataName, int start, int end)
        throws com.liferay.portal.kernel.exception.SystemException;

    /**
    * Returns an ordered range of all the metadata comments where metadataName = &#63;.
    *
    * <p>
    * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link de.fhg.fokus.odp.entities.model.impl.MetadataCommentModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
    * </p>
    *
    * @param metadataName the metadata name
    * @param start the lower bound of the range of metadata comments
    * @param end the upper bound of the range of metadata comments (not inclusive)
    * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
    * @return the ordered range of matching metadata comments
    * @throws SystemException if a system exception occurred
    */
    public java.util.List<de.fhg.fokus.odp.entities.model.MetadataComment> findBymetadataName(
        java.lang.String metadataName, int start, int end,
        com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
        throws com.liferay.portal.kernel.exception.SystemException;

    /**
    * Returns the first metadata comment in the ordered set where metadataName = &#63;.
    *
    * @param metadataName the metadata name
    * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
    * @return the first matching metadata comment
    * @throws de.fhg.fokus.odp.entities.NoSuchMetadataCommentException if a matching metadata comment could not be found
    * @throws SystemException if a system exception occurred
    */
    public de.fhg.fokus.odp.entities.model.MetadataComment findBymetadataName_First(
        java.lang.String metadataName,
        com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
        throws com.liferay.portal.kernel.exception.SystemException,
            de.fhg.fokus.odp.entities.NoSuchMetadataCommentException;

    /**
    * Returns the first metadata comment in the ordered set where metadataName = &#63;.
    *
    * @param metadataName the metadata name
    * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
    * @return the first matching metadata comment, or <code>null</code> if a matching metadata comment could not be found
    * @throws SystemException if a system exception occurred
    */
    public de.fhg.fokus.odp.entities.model.MetadataComment fetchBymetadataName_First(
        java.lang.String metadataName,
        com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
        throws com.liferay.portal.kernel.exception.SystemException;

    /**
    * Returns the last metadata comment in the ordered set where metadataName = &#63;.
    *
    * @param metadataName the metadata name
    * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
    * @return the last matching metadata comment
    * @throws de.fhg.fokus.odp.entities.NoSuchMetadataCommentException if a matching metadata comment could not be found
    * @throws SystemException if a system exception occurred
    */
    public de.fhg.fokus.odp.entities.model.MetadataComment findBymetadataName_Last(
        java.lang.String metadataName,
        com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
        throws com.liferay.portal.kernel.exception.SystemException,
            de.fhg.fokus.odp.entities.NoSuchMetadataCommentException;

    /**
    * Returns the last metadata comment in the ordered set where metadataName = &#63;.
    *
    * @param metadataName the metadata name
    * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
    * @return the last matching metadata comment, or <code>null</code> if a matching metadata comment could not be found
    * @throws SystemException if a system exception occurred
    */
    public de.fhg.fokus.odp.entities.model.MetadataComment fetchBymetadataName_Last(
        java.lang.String metadataName,
        com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
        throws com.liferay.portal.kernel.exception.SystemException;

    /**
    * Returns the metadata comments before and after the current metadata comment in the ordered set where metadataName = &#63;.
    *
    * @param _id the primary key of the current metadata comment
    * @param metadataName the metadata name
    * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
    * @return the previous, current, and next metadata comment
    * @throws de.fhg.fokus.odp.entities.NoSuchMetadataCommentException if a metadata comment with the primary key could not be found
    * @throws SystemException if a system exception occurred
    */
    public de.fhg.fokus.odp.entities.model.MetadataComment[] findBymetadataName_PrevAndNext(
        long _id, java.lang.String metadataName,
        com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
        throws com.liferay.portal.kernel.exception.SystemException,
            de.fhg.fokus.odp.entities.NoSuchMetadataCommentException;

    /**
    * Removes all the metadata comments where metadataName = &#63; from the database.
    *
    * @param metadataName the metadata name
    * @throws SystemException if a system exception occurred
    */
    public void removeBymetadataName(java.lang.String metadataName)
        throws com.liferay.portal.kernel.exception.SystemException;

    /**
    * Returns the number of metadata comments where metadataName = &#63;.
    *
    * @param metadataName the metadata name
    * @return the number of matching metadata comments
    * @throws SystemException if a system exception occurred
    */
    public int countBymetadataName(java.lang.String metadataName)
        throws com.liferay.portal.kernel.exception.SystemException;

    /**
    * Caches the metadata comment in the entity cache if it is enabled.
    *
    * @param metadataComment the metadata comment
    */
    public void cacheResult(
        de.fhg.fokus.odp.entities.model.MetadataComment metadataComment);

    /**
    * Caches the metadata comments in the entity cache if it is enabled.
    *
    * @param metadataComments the metadata comments
    */
    public void cacheResult(
        java.util.List<de.fhg.fokus.odp.entities.model.MetadataComment> metadataComments);

    /**
    * Creates a new metadata comment with the primary key. Does not add the metadata comment to the database.
    *
    * @param _id the primary key for the new metadata comment
    * @return the new metadata comment
    */
    public de.fhg.fokus.odp.entities.model.MetadataComment create(long _id);

    /**
    * Removes the metadata comment with the primary key from the database. Also notifies the appropriate model listeners.
    *
    * @param _id the primary key of the metadata comment
    * @return the metadata comment that was removed
    * @throws de.fhg.fokus.odp.entities.NoSuchMetadataCommentException if a metadata comment with the primary key could not be found
    * @throws SystemException if a system exception occurred
    */
    public de.fhg.fokus.odp.entities.model.MetadataComment remove(long _id)
        throws com.liferay.portal.kernel.exception.SystemException,
            de.fhg.fokus.odp.entities.NoSuchMetadataCommentException;

    public de.fhg.fokus.odp.entities.model.MetadataComment updateImpl(
        de.fhg.fokus.odp.entities.model.MetadataComment metadataComment)
        throws com.liferay.portal.kernel.exception.SystemException;

    /**
    * Returns the metadata comment with the primary key or throws a {@link de.fhg.fokus.odp.entities.NoSuchMetadataCommentException} if it could not be found.
    *
    * @param _id the primary key of the metadata comment
    * @return the metadata comment
    * @throws de.fhg.fokus.odp.entities.NoSuchMetadataCommentException if a metadata comment with the primary key could not be found
    * @throws SystemException if a system exception occurred
    */
    public de.fhg.fokus.odp.entities.model.MetadataComment findByPrimaryKey(
        long _id)
        throws com.liferay.portal.kernel.exception.SystemException,
            de.fhg.fokus.odp.entities.NoSuchMetadataCommentException;

    /**
    * Returns the metadata comment with the primary key or returns <code>null</code> if it could not be found.
    *
    * @param _id the primary key of the metadata comment
    * @return the metadata comment, or <code>null</code> if a metadata comment with the primary key could not be found
    * @throws SystemException if a system exception occurred
    */
    public de.fhg.fokus.odp.entities.model.MetadataComment fetchByPrimaryKey(
        long _id) throws com.liferay.portal.kernel.exception.SystemException;

    /**
    * Returns all the metadata comments.
    *
    * @return the metadata comments
    * @throws SystemException if a system exception occurred
    */
    public java.util.List<de.fhg.fokus.odp.entities.model.MetadataComment> findAll()
        throws com.liferay.portal.kernel.exception.SystemException;

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
    public java.util.List<de.fhg.fokus.odp.entities.model.MetadataComment> findAll(
        int start, int end)
        throws com.liferay.portal.kernel.exception.SystemException;

    /**
    * Returns an ordered range of all the metadata comments.
    *
    * <p>
    * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link de.fhg.fokus.odp.entities.model.impl.MetadataCommentModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
    * </p>
    *
    * @param start the lower bound of the range of metadata comments
    * @param end the upper bound of the range of metadata comments (not inclusive)
    * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
    * @return the ordered range of metadata comments
    * @throws SystemException if a system exception occurred
    */
    public java.util.List<de.fhg.fokus.odp.entities.model.MetadataComment> findAll(
        int start, int end,
        com.liferay.portal.kernel.util.OrderByComparator orderByComparator)
        throws com.liferay.portal.kernel.exception.SystemException;

    /**
    * Removes all the metadata comments from the database.
    *
    * @throws SystemException if a system exception occurred
    */
    public void removeAll()
        throws com.liferay.portal.kernel.exception.SystemException;

    /**
    * Returns the number of metadata comments.
    *
    * @return the number of metadata comments
    * @throws SystemException if a system exception occurred
    */
    public int countAll()
        throws com.liferay.portal.kernel.exception.SystemException;
}
