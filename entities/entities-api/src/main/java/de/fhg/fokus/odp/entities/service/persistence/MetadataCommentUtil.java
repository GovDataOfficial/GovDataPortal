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

package de.fhg.fokus.odp.entities.service.persistence;

import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import de.fhg.fokus.odp.entities.model.MetadataComment;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;

/**
 * The persistence utility for the metadata comment service. This utility wraps <code>de.fhg.fokus.odp.entities.service.persistence.impl.MetadataCommentPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see MetadataCommentPersistence
 * @generated
 */
public class MetadataCommentUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#clearCache()
	 */
	public static void clearCache() {
		getPersistence().clearCache();
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#clearCache(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void clearCache(MetadataComment metadataComment) {
		getPersistence().clearCache(metadataComment);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#countWithDynamicQuery(DynamicQuery)
	 */
	public static long countWithDynamicQuery(DynamicQuery dynamicQuery) {
		return getPersistence().countWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#fetchByPrimaryKeys(Set)
	 */
	public static Map<Serializable, MetadataComment> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<MetadataComment> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<MetadataComment> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<MetadataComment> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<MetadataComment> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static MetadataComment update(MetadataComment metadataComment) {
		return getPersistence().update(metadataComment);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static MetadataComment update(
		MetadataComment metadataComment, ServiceContext serviceContext) {

		return getPersistence().update(metadataComment, serviceContext);
	}

	/**
	 * Returns all the metadata comments where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching metadata comments
	 */
	public static List<MetadataComment> findByUuid(String uuid) {
		return getPersistence().findByUuid(uuid);
	}

	/**
	 * Returns a range of all the metadata comments where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MetadataCommentModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of metadata comments
	 * @param end the upper bound of the range of metadata comments (not inclusive)
	 * @return the range of matching metadata comments
	 */
	public static List<MetadataComment> findByUuid(
		String uuid, int start, int end) {

		return getPersistence().findByUuid(uuid, start, end);
	}

	/**
	 * Returns an ordered range of all the metadata comments where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MetadataCommentModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of metadata comments
	 * @param end the upper bound of the range of metadata comments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching metadata comments
	 */
	public static List<MetadataComment> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<MetadataComment> orderByComparator) {

		return getPersistence().findByUuid(uuid, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the metadata comments where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MetadataCommentModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of metadata comments
	 * @param end the upper bound of the range of metadata comments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching metadata comments
	 */
	public static List<MetadataComment> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<MetadataComment> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid(
			uuid, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first metadata comment in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching metadata comment
	 * @throws NoSuchMetadataCommentException if a matching metadata comment could not be found
	 */
	public static MetadataComment findByUuid_First(
			String uuid, OrderByComparator<MetadataComment> orderByComparator)
		throws de.fhg.fokus.odp.entities.exception.
			NoSuchMetadataCommentException {

		return getPersistence().findByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Returns the first metadata comment in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching metadata comment, or <code>null</code> if a matching metadata comment could not be found
	 */
	public static MetadataComment fetchByUuid_First(
		String uuid, OrderByComparator<MetadataComment> orderByComparator) {

		return getPersistence().fetchByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Returns the last metadata comment in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching metadata comment
	 * @throws NoSuchMetadataCommentException if a matching metadata comment could not be found
	 */
	public static MetadataComment findByUuid_Last(
			String uuid, OrderByComparator<MetadataComment> orderByComparator)
		throws de.fhg.fokus.odp.entities.exception.
			NoSuchMetadataCommentException {

		return getPersistence().findByUuid_Last(uuid, orderByComparator);
	}

	/**
	 * Returns the last metadata comment in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching metadata comment, or <code>null</code> if a matching metadata comment could not be found
	 */
	public static MetadataComment fetchByUuid_Last(
		String uuid, OrderByComparator<MetadataComment> orderByComparator) {

		return getPersistence().fetchByUuid_Last(uuid, orderByComparator);
	}

	/**
	 * Returns the metadata comments before and after the current metadata comment in the ordered set where uuid = &#63;.
	 *
	 * @param _id the primary key of the current metadata comment
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next metadata comment
	 * @throws NoSuchMetadataCommentException if a metadata comment with the primary key could not be found
	 */
	public static MetadataComment[] findByUuid_PrevAndNext(
			long _id, String uuid,
			OrderByComparator<MetadataComment> orderByComparator)
		throws de.fhg.fokus.odp.entities.exception.
			NoSuchMetadataCommentException {

		return getPersistence().findByUuid_PrevAndNext(
			_id, uuid, orderByComparator);
	}

	/**
	 * Removes all the metadata comments where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public static void removeByUuid(String uuid) {
		getPersistence().removeByUuid(uuid);
	}

	/**
	 * Returns the number of metadata comments where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching metadata comments
	 */
	public static int countByUuid(String uuid) {
		return getPersistence().countByUuid(uuid);
	}

	/**
	 * Returns all the metadata comments where userLiferayId = &#63;.
	 *
	 * @param userLiferayId the user liferay ID
	 * @return the matching metadata comments
	 */
	public static List<MetadataComment> findByuserLiferayId(
		long userLiferayId) {

		return getPersistence().findByuserLiferayId(userLiferayId);
	}

	/**
	 * Returns a range of all the metadata comments where userLiferayId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MetadataCommentModelImpl</code>.
	 * </p>
	 *
	 * @param userLiferayId the user liferay ID
	 * @param start the lower bound of the range of metadata comments
	 * @param end the upper bound of the range of metadata comments (not inclusive)
	 * @return the range of matching metadata comments
	 */
	public static List<MetadataComment> findByuserLiferayId(
		long userLiferayId, int start, int end) {

		return getPersistence().findByuserLiferayId(userLiferayId, start, end);
	}

	/**
	 * Returns an ordered range of all the metadata comments where userLiferayId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MetadataCommentModelImpl</code>.
	 * </p>
	 *
	 * @param userLiferayId the user liferay ID
	 * @param start the lower bound of the range of metadata comments
	 * @param end the upper bound of the range of metadata comments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching metadata comments
	 */
	public static List<MetadataComment> findByuserLiferayId(
		long userLiferayId, int start, int end,
		OrderByComparator<MetadataComment> orderByComparator) {

		return getPersistence().findByuserLiferayId(
			userLiferayId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the metadata comments where userLiferayId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MetadataCommentModelImpl</code>.
	 * </p>
	 *
	 * @param userLiferayId the user liferay ID
	 * @param start the lower bound of the range of metadata comments
	 * @param end the upper bound of the range of metadata comments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching metadata comments
	 */
	public static List<MetadataComment> findByuserLiferayId(
		long userLiferayId, int start, int end,
		OrderByComparator<MetadataComment> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByuserLiferayId(
			userLiferayId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first metadata comment in the ordered set where userLiferayId = &#63;.
	 *
	 * @param userLiferayId the user liferay ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching metadata comment
	 * @throws NoSuchMetadataCommentException if a matching metadata comment could not be found
	 */
	public static MetadataComment findByuserLiferayId_First(
			long userLiferayId,
			OrderByComparator<MetadataComment> orderByComparator)
		throws de.fhg.fokus.odp.entities.exception.
			NoSuchMetadataCommentException {

		return getPersistence().findByuserLiferayId_First(
			userLiferayId, orderByComparator);
	}

	/**
	 * Returns the first metadata comment in the ordered set where userLiferayId = &#63;.
	 *
	 * @param userLiferayId the user liferay ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching metadata comment, or <code>null</code> if a matching metadata comment could not be found
	 */
	public static MetadataComment fetchByuserLiferayId_First(
		long userLiferayId,
		OrderByComparator<MetadataComment> orderByComparator) {

		return getPersistence().fetchByuserLiferayId_First(
			userLiferayId, orderByComparator);
	}

	/**
	 * Returns the last metadata comment in the ordered set where userLiferayId = &#63;.
	 *
	 * @param userLiferayId the user liferay ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching metadata comment
	 * @throws NoSuchMetadataCommentException if a matching metadata comment could not be found
	 */
	public static MetadataComment findByuserLiferayId_Last(
			long userLiferayId,
			OrderByComparator<MetadataComment> orderByComparator)
		throws de.fhg.fokus.odp.entities.exception.
			NoSuchMetadataCommentException {

		return getPersistence().findByuserLiferayId_Last(
			userLiferayId, orderByComparator);
	}

	/**
	 * Returns the last metadata comment in the ordered set where userLiferayId = &#63;.
	 *
	 * @param userLiferayId the user liferay ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching metadata comment, or <code>null</code> if a matching metadata comment could not be found
	 */
	public static MetadataComment fetchByuserLiferayId_Last(
		long userLiferayId,
		OrderByComparator<MetadataComment> orderByComparator) {

		return getPersistence().fetchByuserLiferayId_Last(
			userLiferayId, orderByComparator);
	}

	/**
	 * Returns the metadata comments before and after the current metadata comment in the ordered set where userLiferayId = &#63;.
	 *
	 * @param _id the primary key of the current metadata comment
	 * @param userLiferayId the user liferay ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next metadata comment
	 * @throws NoSuchMetadataCommentException if a metadata comment with the primary key could not be found
	 */
	public static MetadataComment[] findByuserLiferayId_PrevAndNext(
			long _id, long userLiferayId,
			OrderByComparator<MetadataComment> orderByComparator)
		throws de.fhg.fokus.odp.entities.exception.
			NoSuchMetadataCommentException {

		return getPersistence().findByuserLiferayId_PrevAndNext(
			_id, userLiferayId, orderByComparator);
	}

	/**
	 * Removes all the metadata comments where userLiferayId = &#63; from the database.
	 *
	 * @param userLiferayId the user liferay ID
	 */
	public static void removeByuserLiferayId(long userLiferayId) {
		getPersistence().removeByuserLiferayId(userLiferayId);
	}

	/**
	 * Returns the number of metadata comments where userLiferayId = &#63;.
	 *
	 * @param userLiferayId the user liferay ID
	 * @return the number of matching metadata comments
	 */
	public static int countByuserLiferayId(long userLiferayId) {
		return getPersistence().countByuserLiferayId(userLiferayId);
	}

	/**
	 * Returns all the metadata comments where metadataName = &#63;.
	 *
	 * @param metadataName the metadata name
	 * @return the matching metadata comments
	 */
	public static List<MetadataComment> findBymetadataName(
		String metadataName) {

		return getPersistence().findBymetadataName(metadataName);
	}

	/**
	 * Returns a range of all the metadata comments where metadataName = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MetadataCommentModelImpl</code>.
	 * </p>
	 *
	 * @param metadataName the metadata name
	 * @param start the lower bound of the range of metadata comments
	 * @param end the upper bound of the range of metadata comments (not inclusive)
	 * @return the range of matching metadata comments
	 */
	public static List<MetadataComment> findBymetadataName(
		String metadataName, int start, int end) {

		return getPersistence().findBymetadataName(metadataName, start, end);
	}

	/**
	 * Returns an ordered range of all the metadata comments where metadataName = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MetadataCommentModelImpl</code>.
	 * </p>
	 *
	 * @param metadataName the metadata name
	 * @param start the lower bound of the range of metadata comments
	 * @param end the upper bound of the range of metadata comments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching metadata comments
	 */
	public static List<MetadataComment> findBymetadataName(
		String metadataName, int start, int end,
		OrderByComparator<MetadataComment> orderByComparator) {

		return getPersistence().findBymetadataName(
			metadataName, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the metadata comments where metadataName = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MetadataCommentModelImpl</code>.
	 * </p>
	 *
	 * @param metadataName the metadata name
	 * @param start the lower bound of the range of metadata comments
	 * @param end the upper bound of the range of metadata comments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching metadata comments
	 */
	public static List<MetadataComment> findBymetadataName(
		String metadataName, int start, int end,
		OrderByComparator<MetadataComment> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findBymetadataName(
			metadataName, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first metadata comment in the ordered set where metadataName = &#63;.
	 *
	 * @param metadataName the metadata name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching metadata comment
	 * @throws NoSuchMetadataCommentException if a matching metadata comment could not be found
	 */
	public static MetadataComment findBymetadataName_First(
			String metadataName,
			OrderByComparator<MetadataComment> orderByComparator)
		throws de.fhg.fokus.odp.entities.exception.
			NoSuchMetadataCommentException {

		return getPersistence().findBymetadataName_First(
			metadataName, orderByComparator);
	}

	/**
	 * Returns the first metadata comment in the ordered set where metadataName = &#63;.
	 *
	 * @param metadataName the metadata name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching metadata comment, or <code>null</code> if a matching metadata comment could not be found
	 */
	public static MetadataComment fetchBymetadataName_First(
		String metadataName,
		OrderByComparator<MetadataComment> orderByComparator) {

		return getPersistence().fetchBymetadataName_First(
			metadataName, orderByComparator);
	}

	/**
	 * Returns the last metadata comment in the ordered set where metadataName = &#63;.
	 *
	 * @param metadataName the metadata name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching metadata comment
	 * @throws NoSuchMetadataCommentException if a matching metadata comment could not be found
	 */
	public static MetadataComment findBymetadataName_Last(
			String metadataName,
			OrderByComparator<MetadataComment> orderByComparator)
		throws de.fhg.fokus.odp.entities.exception.
			NoSuchMetadataCommentException {

		return getPersistence().findBymetadataName_Last(
			metadataName, orderByComparator);
	}

	/**
	 * Returns the last metadata comment in the ordered set where metadataName = &#63;.
	 *
	 * @param metadataName the metadata name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching metadata comment, or <code>null</code> if a matching metadata comment could not be found
	 */
	public static MetadataComment fetchBymetadataName_Last(
		String metadataName,
		OrderByComparator<MetadataComment> orderByComparator) {

		return getPersistence().fetchBymetadataName_Last(
			metadataName, orderByComparator);
	}

	/**
	 * Returns the metadata comments before and after the current metadata comment in the ordered set where metadataName = &#63;.
	 *
	 * @param _id the primary key of the current metadata comment
	 * @param metadataName the metadata name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next metadata comment
	 * @throws NoSuchMetadataCommentException if a metadata comment with the primary key could not be found
	 */
	public static MetadataComment[] findBymetadataName_PrevAndNext(
			long _id, String metadataName,
			OrderByComparator<MetadataComment> orderByComparator)
		throws de.fhg.fokus.odp.entities.exception.
			NoSuchMetadataCommentException {

		return getPersistence().findBymetadataName_PrevAndNext(
			_id, metadataName, orderByComparator);
	}

	/**
	 * Removes all the metadata comments where metadataName = &#63; from the database.
	 *
	 * @param metadataName the metadata name
	 */
	public static void removeBymetadataName(String metadataName) {
		getPersistence().removeBymetadataName(metadataName);
	}

	/**
	 * Returns the number of metadata comments where metadataName = &#63;.
	 *
	 * @param metadataName the metadata name
	 * @return the number of matching metadata comments
	 */
	public static int countBymetadataName(String metadataName) {
		return getPersistence().countBymetadataName(metadataName);
	}

	/**
	 * Caches the metadata comment in the entity cache if it is enabled.
	 *
	 * @param metadataComment the metadata comment
	 */
	public static void cacheResult(MetadataComment metadataComment) {
		getPersistence().cacheResult(metadataComment);
	}

	/**
	 * Caches the metadata comments in the entity cache if it is enabled.
	 *
	 * @param metadataComments the metadata comments
	 */
	public static void cacheResult(List<MetadataComment> metadataComments) {
		getPersistence().cacheResult(metadataComments);
	}

	/**
	 * Creates a new metadata comment with the primary key. Does not add the metadata comment to the database.
	 *
	 * @param _id the primary key for the new metadata comment
	 * @return the new metadata comment
	 */
	public static MetadataComment create(long _id) {
		return getPersistence().create(_id);
	}

	/**
	 * Removes the metadata comment with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param _id the primary key of the metadata comment
	 * @return the metadata comment that was removed
	 * @throws NoSuchMetadataCommentException if a metadata comment with the primary key could not be found
	 */
	public static MetadataComment remove(long _id)
		throws de.fhg.fokus.odp.entities.exception.
			NoSuchMetadataCommentException {

		return getPersistence().remove(_id);
	}

	public static MetadataComment updateImpl(MetadataComment metadataComment) {
		return getPersistence().updateImpl(metadataComment);
	}

	/**
	 * Returns the metadata comment with the primary key or throws a <code>NoSuchMetadataCommentException</code> if it could not be found.
	 *
	 * @param _id the primary key of the metadata comment
	 * @return the metadata comment
	 * @throws NoSuchMetadataCommentException if a metadata comment with the primary key could not be found
	 */
	public static MetadataComment findByPrimaryKey(long _id)
		throws de.fhg.fokus.odp.entities.exception.
			NoSuchMetadataCommentException {

		return getPersistence().findByPrimaryKey(_id);
	}

	/**
	 * Returns the metadata comment with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param _id the primary key of the metadata comment
	 * @return the metadata comment, or <code>null</code> if a metadata comment with the primary key could not be found
	 */
	public static MetadataComment fetchByPrimaryKey(long _id) {
		return getPersistence().fetchByPrimaryKey(_id);
	}

	/**
	 * Returns all the metadata comments.
	 *
	 * @return the metadata comments
	 */
	public static List<MetadataComment> findAll() {
		return getPersistence().findAll();
	}

	/**
	 * Returns a range of all the metadata comments.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MetadataCommentModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of metadata comments
	 * @param end the upper bound of the range of metadata comments (not inclusive)
	 * @return the range of metadata comments
	 */
	public static List<MetadataComment> findAll(int start, int end) {
		return getPersistence().findAll(start, end);
	}

	/**
	 * Returns an ordered range of all the metadata comments.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MetadataCommentModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of metadata comments
	 * @param end the upper bound of the range of metadata comments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of metadata comments
	 */
	public static List<MetadataComment> findAll(
		int start, int end,
		OrderByComparator<MetadataComment> orderByComparator) {

		return getPersistence().findAll(start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the metadata comments.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MetadataCommentModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of metadata comments
	 * @param end the upper bound of the range of metadata comments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of metadata comments
	 */
	public static List<MetadataComment> findAll(
		int start, int end,
		OrderByComparator<MetadataComment> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findAll(
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the metadata comments from the database.
	 */
	public static void removeAll() {
		getPersistence().removeAll();
	}

	/**
	 * Returns the number of metadata comments.
	 *
	 * @return the number of metadata comments
	 */
	public static int countAll() {
		return getPersistence().countAll();
	}

	public static MetadataCommentPersistence getPersistence() {
		return _serviceTracker.getService();
	}

	private static ServiceTracker
		<MetadataCommentPersistence, MetadataCommentPersistence>
			_serviceTracker;

	static {
		Bundle bundle = FrameworkUtil.getBundle(
			MetadataCommentPersistence.class);

		ServiceTracker<MetadataCommentPersistence, MetadataCommentPersistence>
			serviceTracker =
				new ServiceTracker
					<MetadataCommentPersistence, MetadataCommentPersistence>(
						bundle.getBundleContext(),
						MetadataCommentPersistence.class, null);

		serviceTracker.open();

		_serviceTracker = serviceTracker;
	}

}