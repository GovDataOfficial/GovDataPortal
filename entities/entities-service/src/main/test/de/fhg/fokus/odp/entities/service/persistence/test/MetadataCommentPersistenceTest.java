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

package de.fhg.fokus.odp.entities.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.ProjectionFactoryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.util.IntegerWrapper;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PersistenceTestRule;
import com.liferay.portal.test.rule.TransactionalTestRule;

import de.fhg.fokus.odp.entities.exception.NoSuchMetadataCommentException;
import de.fhg.fokus.odp.entities.model.MetadataComment;
import de.fhg.fokus.odp.entities.service.MetadataCommentLocalServiceUtil;
import de.fhg.fokus.odp.entities.service.persistence.MetadataCommentPersistence;
import de.fhg.fokus.odp.entities.service.persistence.MetadataCommentUtil;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @generated
 */
@RunWith(Arquillian.class)
public class MetadataCommentPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "de.fhg.fokus.odp.entities.service"));

	@Before
	public void setUp() {
		_persistence = MetadataCommentUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<MetadataComment> iterator = _metadataComments.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		MetadataComment metadataComment = _persistence.create(pk);

		Assert.assertNotNull(metadataComment);

		Assert.assertEquals(metadataComment.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		MetadataComment newMetadataComment = addMetadataComment();

		_persistence.remove(newMetadataComment);

		MetadataComment existingMetadataComment =
			_persistence.fetchByPrimaryKey(newMetadataComment.getPrimaryKey());

		Assert.assertNull(existingMetadataComment);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addMetadataComment();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		MetadataComment newMetadataComment = _persistence.create(pk);

		newMetadataComment.setUuid(RandomTestUtil.randomString());

		newMetadataComment.setUserLiferayId(RandomTestUtil.nextLong());

		newMetadataComment.setMetadataName(RandomTestUtil.randomString());

		newMetadataComment.setText(RandomTestUtil.randomString());

		newMetadataComment.setCreated(RandomTestUtil.nextDate());

		_metadataComments.add(_persistence.update(newMetadataComment));

		MetadataComment existingMetadataComment = _persistence.findByPrimaryKey(
			newMetadataComment.getPrimaryKey());

		Assert.assertEquals(
			existingMetadataComment.getUuid(), newMetadataComment.getUuid());
		Assert.assertEquals(
			existingMetadataComment.get_id(), newMetadataComment.get_id());
		Assert.assertEquals(
			existingMetadataComment.getUserLiferayId(),
			newMetadataComment.getUserLiferayId());
		Assert.assertEquals(
			existingMetadataComment.getMetadataName(),
			newMetadataComment.getMetadataName());
		Assert.assertEquals(
			existingMetadataComment.getText(), newMetadataComment.getText());
		Assert.assertEquals(
			Time.getShortTimestamp(existingMetadataComment.getCreated()),
			Time.getShortTimestamp(newMetadataComment.getCreated()));
	}

	@Test
	public void testCountByUuid() throws Exception {
		_persistence.countByUuid("");

		_persistence.countByUuid("null");

		_persistence.countByUuid((String)null);
	}

	@Test
	public void testCountByuserLiferayId() throws Exception {
		_persistence.countByuserLiferayId(RandomTestUtil.nextLong());

		_persistence.countByuserLiferayId(0L);
	}

	@Test
	public void testCountBymetadataName() throws Exception {
		_persistence.countBymetadataName("");

		_persistence.countBymetadataName("null");

		_persistence.countBymetadataName((String)null);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		MetadataComment newMetadataComment = addMetadataComment();

		MetadataComment existingMetadataComment = _persistence.findByPrimaryKey(
			newMetadataComment.getPrimaryKey());

		Assert.assertEquals(existingMetadataComment, newMetadataComment);
	}

	@Test(expected = NoSuchMetadataCommentException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<MetadataComment> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"entities_MetadataComment", "uuid", true, "_id", true,
			"userLiferayId", true, "metadataName", true, "text", true,
			"created", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		MetadataComment newMetadataComment = addMetadataComment();

		MetadataComment existingMetadataComment =
			_persistence.fetchByPrimaryKey(newMetadataComment.getPrimaryKey());

		Assert.assertEquals(existingMetadataComment, newMetadataComment);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		MetadataComment missingMetadataComment = _persistence.fetchByPrimaryKey(
			pk);

		Assert.assertNull(missingMetadataComment);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		MetadataComment newMetadataComment1 = addMetadataComment();
		MetadataComment newMetadataComment2 = addMetadataComment();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newMetadataComment1.getPrimaryKey());
		primaryKeys.add(newMetadataComment2.getPrimaryKey());

		Map<Serializable, MetadataComment> metadataComments =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, metadataComments.size());
		Assert.assertEquals(
			newMetadataComment1,
			metadataComments.get(newMetadataComment1.getPrimaryKey()));
		Assert.assertEquals(
			newMetadataComment2,
			metadataComments.get(newMetadataComment2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, MetadataComment> metadataComments =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(metadataComments.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		MetadataComment newMetadataComment = addMetadataComment();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newMetadataComment.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, MetadataComment> metadataComments =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, metadataComments.size());
		Assert.assertEquals(
			newMetadataComment,
			metadataComments.get(newMetadataComment.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, MetadataComment> metadataComments =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(metadataComments.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		MetadataComment newMetadataComment = addMetadataComment();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newMetadataComment.getPrimaryKey());

		Map<Serializable, MetadataComment> metadataComments =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, metadataComments.size());
		Assert.assertEquals(
			newMetadataComment,
			metadataComments.get(newMetadataComment.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			MetadataCommentLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod<MetadataComment>() {

				@Override
				public void performAction(MetadataComment metadataComment) {
					Assert.assertNotNull(metadataComment);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		MetadataComment newMetadataComment = addMetadataComment();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			MetadataComment.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq("_id", newMetadataComment.get_id()));

		List<MetadataComment> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(1, result.size());

		MetadataComment existingMetadataComment = result.get(0);

		Assert.assertEquals(existingMetadataComment, newMetadataComment);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			MetadataComment.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq("_id", RandomTestUtil.nextLong()));

		List<MetadataComment> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		MetadataComment newMetadataComment = addMetadataComment();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			MetadataComment.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(ProjectionFactoryUtil.property("_id"));

		Object new_id = newMetadataComment.get_id();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in("_id", new Object[] {new_id}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existing_id = result.get(0);

		Assert.assertEquals(existing_id, new_id);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			MetadataComment.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(ProjectionFactoryUtil.property("_id"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"_id", new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	protected MetadataComment addMetadataComment() throws Exception {
		long pk = RandomTestUtil.nextLong();

		MetadataComment metadataComment = _persistence.create(pk);

		metadataComment.setUuid(RandomTestUtil.randomString());

		metadataComment.setUserLiferayId(RandomTestUtil.nextLong());

		metadataComment.setMetadataName(RandomTestUtil.randomString());

		metadataComment.setText(RandomTestUtil.randomString());

		metadataComment.setCreated(RandomTestUtil.nextDate());

		_metadataComments.add(_persistence.update(metadataComment));

		return metadataComment;
	}

	private List<MetadataComment> _metadataComments =
		new ArrayList<MetadataComment>();
	private MetadataCommentPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}