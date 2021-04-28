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

package de.fhg.fokus.odp.entities.service.base;

import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.jdbc.SqlUpdate;
import com.liferay.portal.kernel.dao.jdbc.SqlUpdateFactoryUtil;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DefaultActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.Projection;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.framework.service.IdentifiableOSGiService;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.service.BaseLocalServiceImpl;
import com.liferay.portal.kernel.service.PersistedModelLocalService;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.PortalUtil;

import de.fhg.fokus.odp.entities.model.MetadataComment;
import de.fhg.fokus.odp.entities.service.MetadataCommentLocalService;
import de.fhg.fokus.odp.entities.service.persistence.MetadataCommentPersistence;

import java.io.Serializable;

import java.util.List;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Reference;

/**
 * Provides the base implementation for the metadata comment local service.
 *
 * <p>
 * This implementation exists only as a container for the default service methods generated by ServiceBuilder. All custom service methods should be put in {@link de.fhg.fokus.odp.entities.service.impl.MetadataCommentLocalServiceImpl}.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see de.fhg.fokus.odp.entities.service.impl.MetadataCommentLocalServiceImpl
 * @generated
 */
public abstract class MetadataCommentLocalServiceBaseImpl
	extends BaseLocalServiceImpl
	implements AopService, IdentifiableOSGiService,
			   MetadataCommentLocalService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Use <code>MetadataCommentLocalService</code> via injection or a <code>org.osgi.util.tracker.ServiceTracker</code> or use <code>de.fhg.fokus.odp.entities.service.MetadataCommentLocalServiceUtil</code>.
	 */

	/**
	 * Adds the metadata comment to the database. Also notifies the appropriate model listeners.
	 *
	 * @param metadataComment the metadata comment
	 * @return the metadata comment that was added
	 */
	@Indexable(type = IndexableType.REINDEX)
	@Override
	public MetadataComment addMetadataComment(MetadataComment metadataComment) {
		metadataComment.setNew(true);

		return metadataCommentPersistence.update(metadataComment);
	}

	/**
	 * Creates a new metadata comment with the primary key. Does not add the metadata comment to the database.
	 *
	 * @param _id the primary key for the new metadata comment
	 * @return the new metadata comment
	 */
	@Override
	@Transactional(enabled = false)
	public MetadataComment createMetadataComment(long _id) {
		return metadataCommentPersistence.create(_id);
	}

	/**
	 * Deletes the metadata comment with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param _id the primary key of the metadata comment
	 * @return the metadata comment that was removed
	 * @throws PortalException if a metadata comment with the primary key could not be found
	 */
	@Indexable(type = IndexableType.DELETE)
	@Override
	public MetadataComment deleteMetadataComment(long _id)
		throws PortalException {

		return metadataCommentPersistence.remove(_id);
	}

	/**
	 * Deletes the metadata comment from the database. Also notifies the appropriate model listeners.
	 *
	 * @param metadataComment the metadata comment
	 * @return the metadata comment that was removed
	 */
	@Indexable(type = IndexableType.DELETE)
	@Override
	public MetadataComment deleteMetadataComment(
		MetadataComment metadataComment) {

		return metadataCommentPersistence.remove(metadataComment);
	}

	@Override
	public DynamicQuery dynamicQuery() {
		Class<?> clazz = getClass();

		return DynamicQueryFactoryUtil.forClass(
			MetadataComment.class, clazz.getClassLoader());
	}

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	@Override
	public <T> List<T> dynamicQuery(DynamicQuery dynamicQuery) {
		return metadataCommentPersistence.findWithDynamicQuery(dynamicQuery);
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
	public <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return metadataCommentPersistence.findWithDynamicQuery(
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
	public <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<T> orderByComparator) {

		return metadataCommentPersistence.findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	@Override
	public long dynamicQueryCount(DynamicQuery dynamicQuery) {
		return metadataCommentPersistence.countWithDynamicQuery(dynamicQuery);
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
		DynamicQuery dynamicQuery, Projection projection) {

		return metadataCommentPersistence.countWithDynamicQuery(
			dynamicQuery, projection);
	}

	@Override
	public MetadataComment fetchMetadataComment(long _id) {
		return metadataCommentPersistence.fetchByPrimaryKey(_id);
	}

	/**
	 * Returns the metadata comment with the primary key.
	 *
	 * @param _id the primary key of the metadata comment
	 * @return the metadata comment
	 * @throws PortalException if a metadata comment with the primary key could not be found
	 */
	@Override
	public MetadataComment getMetadataComment(long _id) throws PortalException {
		return metadataCommentPersistence.findByPrimaryKey(_id);
	}

	@Override
	public ActionableDynamicQuery getActionableDynamicQuery() {
		ActionableDynamicQuery actionableDynamicQuery =
			new DefaultActionableDynamicQuery();

		actionableDynamicQuery.setBaseLocalService(metadataCommentLocalService);
		actionableDynamicQuery.setClassLoader(getClassLoader());
		actionableDynamicQuery.setModelClass(MetadataComment.class);

		actionableDynamicQuery.setPrimaryKeyPropertyName("_id");

		return actionableDynamicQuery;
	}

	@Override
	public IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		IndexableActionableDynamicQuery indexableActionableDynamicQuery =
			new IndexableActionableDynamicQuery();

		indexableActionableDynamicQuery.setBaseLocalService(
			metadataCommentLocalService);
		indexableActionableDynamicQuery.setClassLoader(getClassLoader());
		indexableActionableDynamicQuery.setModelClass(MetadataComment.class);

		indexableActionableDynamicQuery.setPrimaryKeyPropertyName("_id");

		return indexableActionableDynamicQuery;
	}

	protected void initActionableDynamicQuery(
		ActionableDynamicQuery actionableDynamicQuery) {

		actionableDynamicQuery.setBaseLocalService(metadataCommentLocalService);
		actionableDynamicQuery.setClassLoader(getClassLoader());
		actionableDynamicQuery.setModelClass(MetadataComment.class);

		actionableDynamicQuery.setPrimaryKeyPropertyName("_id");
	}

	/**
	 * @throws PortalException
	 */
	public PersistedModel createPersistedModel(Serializable primaryKeyObj)
		throws PortalException {

		return metadataCommentPersistence.create(
			((Long)primaryKeyObj).longValue());
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public PersistedModel deletePersistedModel(PersistedModel persistedModel)
		throws PortalException {

		return metadataCommentLocalService.deleteMetadataComment(
			(MetadataComment)persistedModel);
	}

	public BasePersistence<MetadataComment> getBasePersistence() {
		return metadataCommentPersistence;
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public PersistedModel getPersistedModel(Serializable primaryKeyObj)
		throws PortalException {

		return metadataCommentPersistence.findByPrimaryKey(primaryKeyObj);
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
	public List<MetadataComment> getMetadataComments(int start, int end) {
		return metadataCommentPersistence.findAll(start, end);
	}

	/**
	 * Returns the number of metadata comments.
	 *
	 * @return the number of metadata comments
	 */
	@Override
	public int getMetadataCommentsCount() {
		return metadataCommentPersistence.countAll();
	}

	/**
	 * Updates the metadata comment in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * @param metadataComment the metadata comment
	 * @return the metadata comment that was updated
	 */
	@Indexable(type = IndexableType.REINDEX)
	@Override
	public MetadataComment updateMetadataComment(
		MetadataComment metadataComment) {

		return metadataCommentPersistence.update(metadataComment);
	}

	@Override
	public Class<?>[] getAopInterfaces() {
		return new Class<?>[] {
			MetadataCommentLocalService.class, IdentifiableOSGiService.class,
			PersistedModelLocalService.class
		};
	}

	@Override
	public void setAopProxy(Object aopProxy) {
		metadataCommentLocalService = (MetadataCommentLocalService)aopProxy;
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return MetadataCommentLocalService.class.getName();
	}

	protected Class<?> getModelClass() {
		return MetadataComment.class;
	}

	protected String getModelClassName() {
		return MetadataComment.class.getName();
	}

	/**
	 * Performs a SQL query.
	 *
	 * @param sql the sql query
	 */
	protected void runSQL(String sql) {
		try {
			DataSource dataSource = metadataCommentPersistence.getDataSource();

			DB db = DBManagerUtil.getDB();

			sql = db.buildSQL(sql);
			sql = PortalUtil.transformSQL(sql);

			SqlUpdate sqlUpdate = SqlUpdateFactoryUtil.getSqlUpdate(
				dataSource, sql);

			sqlUpdate.update();
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
	}

	protected MetadataCommentLocalService metadataCommentLocalService;

	@Reference
	protected MetadataCommentPersistence metadataCommentPersistence;

	@Reference
	protected com.liferay.counter.kernel.service.CounterLocalService
		counterLocalService;

	@Reference
	protected com.liferay.portal.kernel.service.ClassNameLocalService
		classNameLocalService;

	@Reference
	protected com.liferay.portal.kernel.service.ResourceLocalService
		resourceLocalService;

	@Reference
	protected com.liferay.portal.kernel.service.UserLocalService
		userLocalService;

}