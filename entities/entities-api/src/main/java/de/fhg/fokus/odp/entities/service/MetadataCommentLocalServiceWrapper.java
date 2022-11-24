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

import com.liferay.portal.kernel.service.ServiceWrapper;

/**
 * Provides a wrapper for {@link MetadataCommentLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see MetadataCommentLocalService
 * @generated
 */
public class MetadataCommentLocalServiceWrapper
	implements MetadataCommentLocalService,
			   ServiceWrapper<MetadataCommentLocalService> {

	public MetadataCommentLocalServiceWrapper() {
		this(null);
	}

	public MetadataCommentLocalServiceWrapper(
		MetadataCommentLocalService metadataCommentLocalService) {

		_metadataCommentLocalService = metadataCommentLocalService;
	}

	/**
	 * Adds the metadata comment to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect MetadataCommentLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param metadataComment the metadata comment
	 * @return the metadata comment that was added
	 */
	@Override
	public de.fhg.fokus.odp.entities.model.MetadataComment addMetadataComment(
		de.fhg.fokus.odp.entities.model.MetadataComment metadataComment) {

		return _metadataCommentLocalService.addMetadataComment(metadataComment);
	}

	/**
	 * Creates a new metadata comment with the primary key. Does not add the metadata comment to the database.
	 *
	 * @param _id the primary key for the new metadata comment
	 * @return the new metadata comment
	 */
	@Override
	public de.fhg.fokus.odp.entities.model.MetadataComment
		createMetadataComment(long _id) {

		return _metadataCommentLocalService.createMetadataComment(_id);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _metadataCommentLocalService.createPersistedModel(primaryKeyObj);
	}

	/**
	 * Deletes the metadata comment with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect MetadataCommentLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param _id the primary key of the metadata comment
	 * @return the metadata comment that was removed
	 * @throws PortalException if a metadata comment with the primary key could not be found
	 */
	@Override
	public de.fhg.fokus.odp.entities.model.MetadataComment
			deleteMetadataComment(long _id)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _metadataCommentLocalService.deleteMetadataComment(_id);
	}

	/**
	 * Deletes the metadata comment from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect MetadataCommentLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param metadataComment the metadata comment
	 * @return the metadata comment that was removed
	 */
	@Override
	public de.fhg.fokus.odp.entities.model.MetadataComment
		deleteMetadataComment(
			de.fhg.fokus.odp.entities.model.MetadataComment metadataComment) {

		return _metadataCommentLocalService.deleteMetadataComment(
			metadataComment);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _metadataCommentLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _metadataCommentLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _metadataCommentLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _metadataCommentLocalService.dynamicQuery();
	}

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {

		return _metadataCommentLocalService.dynamicQuery(dynamicQuery);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {

		return _metadataCommentLocalService.dynamicQuery(
			dynamicQuery, start, end);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {

		return _metadataCommentLocalService.dynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	@Override
	public long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {

		return _metadataCommentLocalService.dynamicQueryCount(dynamicQuery);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	@Override
	public long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery,
		com.liferay.portal.kernel.dao.orm.Projection projection) {

		return _metadataCommentLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public de.fhg.fokus.odp.entities.model.MetadataComment fetchMetadataComment(
		long _id) {

		return _metadataCommentLocalService.fetchMetadataComment(_id);
	}

	/**
	 * Find bymetadata name.
	 *
	 * @param metadataName the metadata name
	 * @return the java.util. list
	 */
	@Override
	public java.util.List<de.fhg.fokus.odp.entities.model.MetadataComment>
		findBymetadataName(String metadataName) {

		return _metadataCommentLocalService.findBymetadataName(metadataName);
	}

	/**
	 * Find byuser liferay id.
	 *
	 * @param userLiferayId the user liferay id
	 * @return the list<de.fhg.fokus.odp.entities.model. metadata comment>
	 */
	@Override
	public java.util.List<de.fhg.fokus.odp.entities.model.MetadataComment>
		findByuserLiferayId(long userLiferayId) {

		return _metadataCommentLocalService.findByuserLiferayId(userLiferayId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _metadataCommentLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _metadataCommentLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the metadata comment with the primary key.
	 *
	 * @param _id the primary key of the metadata comment
	 * @return the metadata comment
	 * @throws PortalException if a metadata comment with the primary key could not be found
	 */
	@Override
	public de.fhg.fokus.odp.entities.model.MetadataComment getMetadataComment(
			long _id)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _metadataCommentLocalService.getMetadataComment(_id);
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
	@Override
	public java.util.List<de.fhg.fokus.odp.entities.model.MetadataComment>
		getMetadataComments(int start, int end) {

		return _metadataCommentLocalService.getMetadataComments(start, end);
	}

	/**
	 * Returns the number of metadata comments.
	 *
	 * @return the number of metadata comments
	 */
	@Override
	public int getMetadataCommentsCount() {
		return _metadataCommentLocalService.getMetadataCommentsCount();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _metadataCommentLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _metadataCommentLocalService.getPersistedModel(primaryKeyObj);
	}

	/**
	 * Updates the metadata comment in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect MetadataCommentLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param metadataComment the metadata comment
	 * @return the metadata comment that was updated
	 */
	@Override
	public de.fhg.fokus.odp.entities.model.MetadataComment
		updateMetadataComment(
			de.fhg.fokus.odp.entities.model.MetadataComment metadataComment) {

		return _metadataCommentLocalService.updateMetadataComment(
			metadataComment);
	}

	@Override
	public MetadataCommentLocalService getWrappedService() {
		return _metadataCommentLocalService;
	}

	@Override
	public void setWrappedService(
		MetadataCommentLocalService metadataCommentLocalService) {

		_metadataCommentLocalService = metadataCommentLocalService;
	}

	private MetadataCommentLocalService _metadataCommentLocalService;

}