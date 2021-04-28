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

package de.fhg.fokus.odp.entities.service.impl;

import com.liferay.portal.aop.AopService;

import de.fhg.fokus.odp.entities.service.base.MetadataCommentLocalServiceBaseImpl;

import org.osgi.service.component.annotations.Component;

/**
 * The implementation of the metadata comment local service.
 *
 * <p>
 * All custom service methods should be put in this class. Whenever methods are added, rerun ServiceBuilder to copy their definitions into the <code>de.fhg.fokus.odp.entities.service.MetadataCommentLocalService</code> interface.
 *
 * <p>
 * This is a local service. Methods of this service will not have security checks based on the propagated JAAS credentials because this service can only be accessed from within the same VM.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see MetadataCommentLocalServiceBaseImpl
 */
@Component(
	property = "model.class.name=de.fhg.fokus.odp.entities.model.MetadataComment",
	service = AopService.class
)
public class MetadataCommentLocalServiceImpl
	extends MetadataCommentLocalServiceBaseImpl {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never reference this class directly. Use <code>de.fhg.fokus.odp.entities.service.MetadataCommentLocalService</code> via injection or a <code>org.osgi.util.tracker.ServiceTracker</code> or use <code>de.fhg.fokus.odp.entities.service.MetadataCommentLocalServiceUtil</code>.
	 */

  /**
   * Find byuser liferay id.
   * 
   * @param userLiferayId the user liferay id
   * @return the list<de.fhg.fokus.odp.entities.model. metadata comment>
   */
  public java.util.List<de.fhg.fokus.odp.entities.model.MetadataComment> findByuserLiferayId(
      long userLiferayId)
  {
    return metadataCommentPersistence.findByuserLiferayId(userLiferayId);
  }

  /**
   * Find bymetadata name.
   * 
   * @param metadataName the metadata name
   * @return the java.util. list
   */
  public java.util.List<de.fhg.fokus.odp.entities.model.MetadataComment> findBymetadataName(
      java.lang.String metadataName)
  {
    return metadataCommentPersistence.findBymetadataName(metadataName);
  }
}