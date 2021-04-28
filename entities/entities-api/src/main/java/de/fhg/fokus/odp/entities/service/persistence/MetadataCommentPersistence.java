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

import com.liferay.portal.kernel.service.persistence.BasePersistence;

import de.fhg.fokus.odp.entities.exception.NoSuchMetadataCommentException;
import de.fhg.fokus.odp.entities.model.MetadataComment;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the metadata comment service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see MetadataCommentUtil
 * @generated
 */
@ProviderType
public interface MetadataCommentPersistence
	extends BasePersistence<MetadataComment> {

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
	 */
	public java.util.List<MetadataComment> findByUuid(String uuid);

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
	public java.util.List<MetadataComment> findByUuid(
		String uuid, int start, int end);

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
	public java.util.List<MetadataComment> findByUuid(
		String uuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<MetadataComment>
			orderByComparator);

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
	public java.util.List<MetadataComment> findByUuid(
		String uuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<MetadataComment>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first metadata comment in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching metadata comment
	 * @throws NoSuchMetadataCommentException if a matching metadata comment could not be found
	 */
	public MetadataComment findByUuid_First(
			String uuid,
			com.liferay.portal.kernel.util.OrderByComparator<MetadataComment>
				orderByComparator)
		throws NoSuchMetadataCommentException;

	/**
	 * Returns the first metadata comment in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching metadata comment, or <code>null</code> if a matching metadata comment could not be found
	 */
	public MetadataComment fetchByUuid_First(
		String uuid,
		com.liferay.portal.kernel.util.OrderByComparator<MetadataComment>
			orderByComparator);

	/**
	 * Returns the last metadata comment in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching metadata comment
	 * @throws NoSuchMetadataCommentException if a matching metadata comment could not be found
	 */
	public MetadataComment findByUuid_Last(
			String uuid,
			com.liferay.portal.kernel.util.OrderByComparator<MetadataComment>
				orderByComparator)
		throws NoSuchMetadataCommentException;

	/**
	 * Returns the last metadata comment in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching metadata comment, or <code>null</code> if a matching metadata comment could not be found
	 */
	public MetadataComment fetchByUuid_Last(
		String uuid,
		com.liferay.portal.kernel.util.OrderByComparator<MetadataComment>
			orderByComparator);

	/**
	 * Returns the metadata comments before and after the current metadata comment in the ordered set where uuid = &#63;.
	 *
	 * @param _id the primary key of the current metadata comment
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next metadata comment
	 * @throws NoSuchMetadataCommentException if a metadata comment with the primary key could not be found
	 */
	public MetadataComment[] findByUuid_PrevAndNext(
			long _id, String uuid,
			com.liferay.portal.kernel.util.OrderByComparator<MetadataComment>
				orderByComparator)
		throws NoSuchMetadataCommentException;

	/**
	 * Removes all the metadata comments where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public void removeByUuid(String uuid);

	/**
	 * Returns the number of metadata comments where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching metadata comments
	 */
	public int countByUuid(String uuid);

	/**
	 * Returns all the metadata comments where userLiferayId = &#63;.
	 *
	 * @param userLiferayId the user liferay ID
	 * @return the matching metadata comments
	 */
	public java.util.List<MetadataComment> findByuserLiferayId(
		long userLiferayId);

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
	public java.util.List<MetadataComment> findByuserLiferayId(
		long userLiferayId, int start, int end);

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
	public java.util.List<MetadataComment> findByuserLiferayId(
		long userLiferayId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<MetadataComment>
			orderByComparator);

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
	public java.util.List<MetadataComment> findByuserLiferayId(
		long userLiferayId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<MetadataComment>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first metadata comment in the ordered set where userLiferayId = &#63;.
	 *
	 * @param userLiferayId the user liferay ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching metadata comment
	 * @throws NoSuchMetadataCommentException if a matching metadata comment could not be found
	 */
	public MetadataComment findByuserLiferayId_First(
			long userLiferayId,
			com.liferay.portal.kernel.util.OrderByComparator<MetadataComment>
				orderByComparator)
		throws NoSuchMetadataCommentException;

	/**
	 * Returns the first metadata comment in the ordered set where userLiferayId = &#63;.
	 *
	 * @param userLiferayId the user liferay ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching metadata comment, or <code>null</code> if a matching metadata comment could not be found
	 */
	public MetadataComment fetchByuserLiferayId_First(
		long userLiferayId,
		com.liferay.portal.kernel.util.OrderByComparator<MetadataComment>
			orderByComparator);

	/**
	 * Returns the last metadata comment in the ordered set where userLiferayId = &#63;.
	 *
	 * @param userLiferayId the user liferay ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching metadata comment
	 * @throws NoSuchMetadataCommentException if a matching metadata comment could not be found
	 */
	public MetadataComment findByuserLiferayId_Last(
			long userLiferayId,
			com.liferay.portal.kernel.util.OrderByComparator<MetadataComment>
				orderByComparator)
		throws NoSuchMetadataCommentException;

	/**
	 * Returns the last metadata comment in the ordered set where userLiferayId = &#63;.
	 *
	 * @param userLiferayId the user liferay ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching metadata comment, or <code>null</code> if a matching metadata comment could not be found
	 */
	public MetadataComment fetchByuserLiferayId_Last(
		long userLiferayId,
		com.liferay.portal.kernel.util.OrderByComparator<MetadataComment>
			orderByComparator);

	/**
	 * Returns the metadata comments before and after the current metadata comment in the ordered set where userLiferayId = &#63;.
	 *
	 * @param _id the primary key of the current metadata comment
	 * @param userLiferayId the user liferay ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next metadata comment
	 * @throws NoSuchMetadataCommentException if a metadata comment with the primary key could not be found
	 */
	public MetadataComment[] findByuserLiferayId_PrevAndNext(
			long _id, long userLiferayId,
			com.liferay.portal.kernel.util.OrderByComparator<MetadataComment>
				orderByComparator)
		throws NoSuchMetadataCommentException;

	/**
	 * Removes all the metadata comments where userLiferayId = &#63; from the database.
	 *
	 * @param userLiferayId the user liferay ID
	 */
	public void removeByuserLiferayId(long userLiferayId);

	/**
	 * Returns the number of metadata comments where userLiferayId = &#63;.
	 *
	 * @param userLiferayId the user liferay ID
	 * @return the number of matching metadata comments
	 */
	public int countByuserLiferayId(long userLiferayId);

	/**
	 * Returns all the metadata comments where metadataName = &#63;.
	 *
	 * @param metadataName the metadata name
	 * @return the matching metadata comments
	 */
	public java.util.List<MetadataComment> findBymetadataName(
		String metadataName);

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
	public java.util.List<MetadataComment> findBymetadataName(
		String metadataName, int start, int end);

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
	public java.util.List<MetadataComment> findBymetadataName(
		String metadataName, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<MetadataComment>
			orderByComparator);

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
	public java.util.List<MetadataComment> findBymetadataName(
		String metadataName, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<MetadataComment>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first metadata comment in the ordered set where metadataName = &#63;.
	 *
	 * @param metadataName the metadata name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching metadata comment
	 * @throws NoSuchMetadataCommentException if a matching metadata comment could not be found
	 */
	public MetadataComment findBymetadataName_First(
			String metadataName,
			com.liferay.portal.kernel.util.OrderByComparator<MetadataComment>
				orderByComparator)
		throws NoSuchMetadataCommentException;

	/**
	 * Returns the first metadata comment in the ordered set where metadataName = &#63;.
	 *
	 * @param metadataName the metadata name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching metadata comment, or <code>null</code> if a matching metadata comment could not be found
	 */
	public MetadataComment fetchBymetadataName_First(
		String metadataName,
		com.liferay.portal.kernel.util.OrderByComparator<MetadataComment>
			orderByComparator);

	/**
	 * Returns the last metadata comment in the ordered set where metadataName = &#63;.
	 *
	 * @param metadataName the metadata name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching metadata comment
	 * @throws NoSuchMetadataCommentException if a matching metadata comment could not be found
	 */
	public MetadataComment findBymetadataName_Last(
			String metadataName,
			com.liferay.portal.kernel.util.OrderByComparator<MetadataComment>
				orderByComparator)
		throws NoSuchMetadataCommentException;

	/**
	 * Returns the last metadata comment in the ordered set where metadataName = &#63;.
	 *
	 * @param metadataName the metadata name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching metadata comment, or <code>null</code> if a matching metadata comment could not be found
	 */
	public MetadataComment fetchBymetadataName_Last(
		String metadataName,
		com.liferay.portal.kernel.util.OrderByComparator<MetadataComment>
			orderByComparator);

	/**
	 * Returns the metadata comments before and after the current metadata comment in the ordered set where metadataName = &#63;.
	 *
	 * @param _id the primary key of the current metadata comment
	 * @param metadataName the metadata name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next metadata comment
	 * @throws NoSuchMetadataCommentException if a metadata comment with the primary key could not be found
	 */
	public MetadataComment[] findBymetadataName_PrevAndNext(
			long _id, String metadataName,
			com.liferay.portal.kernel.util.OrderByComparator<MetadataComment>
				orderByComparator)
		throws NoSuchMetadataCommentException;

	/**
	 * Removes all the metadata comments where metadataName = &#63; from the database.
	 *
	 * @param metadataName the metadata name
	 */
	public void removeBymetadataName(String metadataName);

	/**
	 * Returns the number of metadata comments where metadataName = &#63;.
	 *
	 * @param metadataName the metadata name
	 * @return the number of matching metadata comments
	 */
	public int countBymetadataName(String metadataName);

	/**
	 * Caches the metadata comment in the entity cache if it is enabled.
	 *
	 * @param metadataComment the metadata comment
	 */
	public void cacheResult(MetadataComment metadataComment);

	/**
	 * Caches the metadata comments in the entity cache if it is enabled.
	 *
	 * @param metadataComments the metadata comments
	 */
	public void cacheResult(java.util.List<MetadataComment> metadataComments);

	/**
	 * Creates a new metadata comment with the primary key. Does not add the metadata comment to the database.
	 *
	 * @param _id the primary key for the new metadata comment
	 * @return the new metadata comment
	 */
	public MetadataComment create(long _id);

	/**
	 * Removes the metadata comment with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param _id the primary key of the metadata comment
	 * @return the metadata comment that was removed
	 * @throws NoSuchMetadataCommentException if a metadata comment with the primary key could not be found
	 */
	public MetadataComment remove(long _id)
		throws NoSuchMetadataCommentException;

	public MetadataComment updateImpl(MetadataComment metadataComment);

	/**
	 * Returns the metadata comment with the primary key or throws a <code>NoSuchMetadataCommentException</code> if it could not be found.
	 *
	 * @param _id the primary key of the metadata comment
	 * @return the metadata comment
	 * @throws NoSuchMetadataCommentException if a metadata comment with the primary key could not be found
	 */
	public MetadataComment findByPrimaryKey(long _id)
		throws NoSuchMetadataCommentException;

	/**
	 * Returns the metadata comment with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param _id the primary key of the metadata comment
	 * @return the metadata comment, or <code>null</code> if a metadata comment with the primary key could not be found
	 */
	public MetadataComment fetchByPrimaryKey(long _id);

	/**
	 * Returns all the metadata comments.
	 *
	 * @return the metadata comments
	 */
	public java.util.List<MetadataComment> findAll();

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
	public java.util.List<MetadataComment> findAll(int start, int end);

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
	public java.util.List<MetadataComment> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<MetadataComment>
			orderByComparator);

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
	public java.util.List<MetadataComment> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<MetadataComment>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the metadata comments from the database.
	 */
	public void removeAll();

	/**
	 * Returns the number of metadata comments.
	 *
	 * @return the number of metadata comments
	 */
	public int countAll();

}