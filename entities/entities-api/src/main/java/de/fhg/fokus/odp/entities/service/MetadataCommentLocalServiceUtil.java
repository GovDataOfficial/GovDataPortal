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

package de.fhg.fokus.odp.entities.service;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Provides the local service utility for MetadataComment. This utility wraps
 * <code>de.fhg.fokus.odp.entities.service.impl.MetadataCommentLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see MetadataCommentLocalService
 * @generated
 */
public class MetadataCommentLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>de.fhg.fokus.odp.entities.service.impl.MetadataCommentLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the metadata comment to the database. Also notifies the appropriate model listeners.
	 *
	 * @param metadataComment the metadata comment
	 * @return the metadata comment that was added
	 */
	public static de.fhg.fokus.odp.entities.model.MetadataComment
		addMetadataComment(
			de.fhg.fokus.odp.entities.model.MetadataComment metadataComment) {

		return getService().addMetadataComment(metadataComment);
	}

	/**
	 * Creates a new metadata comment with the primary key. Does not add the metadata comment to the database.
	 *
	 * @param _id the primary key for the new metadata comment
	 * @return the new metadata comment
	 */
	public static de.fhg.fokus.odp.entities.model.MetadataComment
		createMetadataComment(long _id) {

		return getService().createMetadataComment(_id);
	}

	/**
	 * @throws PortalException
	 */
	public static com.liferay.portal.kernel.model.PersistedModel
			createPersistedModel(java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().createPersistedModel(primaryKeyObj);
	}

	/**
	 * Deletes the metadata comment with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param _id the primary key of the metadata comment
	 * @return the metadata comment that was removed
	 * @throws PortalException if a metadata comment with the primary key could not be found
	 */
	public static de.fhg.fokus.odp.entities.model.MetadataComment
			deleteMetadataComment(long _id)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().deleteMetadataComment(_id);
	}

	/**
	 * Deletes the metadata comment from the database. Also notifies the appropriate model listeners.
	 *
	 * @param metadataComment the metadata comment
	 * @return the metadata comment that was removed
	 */
	public static de.fhg.fokus.odp.entities.model.MetadataComment
		deleteMetadataComment(
			de.fhg.fokus.odp.entities.model.MetadataComment metadataComment) {

		return getService().deleteMetadataComment(metadataComment);
	}

	/**
	 * @throws PortalException
	 */
	public static com.liferay.portal.kernel.model.PersistedModel
			deletePersistedModel(
				com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().deletePersistedModel(persistedModel);
	}

	public static com.liferay.portal.kernel.dao.orm.DynamicQuery
		dynamicQuery() {

		return getService().dynamicQuery();
	}

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	public static <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {

		return getService().dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>de.fhg.fokus.odp.entities.model.impl.MetadataCommentModelImpl</code>.
	 * </p>
	 *
	 * @param dynamicQuery the dynamic query
	 * @param start the lower bound of the range of model instances
	 * @param end the upper bound of the range of model instances (not inclusive)
	 * @return the range of matching rows
	 */
	public static <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {

		return getService().dynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>de.fhg.fokus.odp.entities.model.impl.MetadataCommentModelImpl</code>.
	 * </p>
	 *
	 * @param dynamicQuery the dynamic query
	 * @param start the lower bound of the range of model instances
	 * @param end the upper bound of the range of model instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching rows
	 */
	public static <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {

		return getService().dynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	public static long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {

		return getService().dynamicQueryCount(dynamicQuery);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	public static long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery,
		com.liferay.portal.kernel.dao.orm.Projection projection) {

		return getService().dynamicQueryCount(dynamicQuery, projection);
	}

	public static de.fhg.fokus.odp.entities.model.MetadataComment
		fetchMetadataComment(long _id) {

		return getService().fetchMetadataComment(_id);
	}

	/**
	 * Find bymetadata name.
	 *
	 * @param metadataName the metadata name
	 * @return the java.util. list
	 */
	public static java.util.List
		<de.fhg.fokus.odp.entities.model.MetadataComment> findBymetadataName(
			String metadataName) {

		return getService().findBymetadataName(metadataName);
	}

	/**
	 * Find byuser liferay id.
	 *
	 * @param userLiferayId the user liferay id
	 * @return the list<de.fhg.fokus.odp.entities.model. metadata comment>
	 */
	public static java.util.List
		<de.fhg.fokus.odp.entities.model.MetadataComment> findByuserLiferayId(
			long userLiferayId) {

		return getService().findByuserLiferayId(userLiferayId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	public static
		com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
			getIndexableActionableDynamicQuery() {

		return getService().getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the metadata comment with the primary key.
	 *
	 * @param _id the primary key of the metadata comment
	 * @return the metadata comment
	 * @throws PortalException if a metadata comment with the primary key could not be found
	 */
	public static de.fhg.fokus.odp.entities.model.MetadataComment
			getMetadataComment(long _id)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().getMetadataComment(_id);
	}

	/**
	 * Returns a range of all the metadata comments.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>de.fhg.fokus.odp.entities.model.impl.MetadataCommentModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of metadata comments
	 * @param end the upper bound of the range of metadata comments (not inclusive)
	 * @return the range of metadata comments
	 */
	public static java.util.List
		<de.fhg.fokus.odp.entities.model.MetadataComment> getMetadataComments(
			int start, int end) {

		return getService().getMetadataComments(start, end);
	}

	/**
	 * Returns the number of metadata comments.
	 *
	 * @return the number of metadata comments
	 */
	public static int getMetadataCommentsCount() {
		return getService().getMetadataCommentsCount();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	public static com.liferay.portal.kernel.model.PersistedModel
			getPersistedModel(java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().getPersistedModel(primaryKeyObj);
	}

	/**
	 * Updates the metadata comment in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * @param metadataComment the metadata comment
	 * @return the metadata comment that was updated
	 */
	public static de.fhg.fokus.odp.entities.model.MetadataComment
		updateMetadataComment(
			de.fhg.fokus.odp.entities.model.MetadataComment metadataComment) {

		return getService().updateMetadataComment(metadataComment);
	}

	public static MetadataCommentLocalService getService() {
		return _serviceTracker.getService();
	}

	private static ServiceTracker
		<MetadataCommentLocalService, MetadataCommentLocalService>
			_serviceTracker;

	static {
		Bundle bundle = FrameworkUtil.getBundle(
			MetadataCommentLocalService.class);

		ServiceTracker<MetadataCommentLocalService, MetadataCommentLocalService>
			serviceTracker =
				new ServiceTracker
					<MetadataCommentLocalService, MetadataCommentLocalService>(
						bundle.getBundleContext(),
						MetadataCommentLocalService.class, null);

		serviceTracker.open();

		_serviceTracker = serviceTracker;
	}

}