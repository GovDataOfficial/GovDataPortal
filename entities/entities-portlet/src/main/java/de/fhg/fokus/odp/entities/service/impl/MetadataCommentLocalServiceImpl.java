/**
 * Copyright (c) 2012, 2013 Fraunhofer Institute FOKUS
 *
 * This file is part of Open Data Platform.
 *
 * Open Data Platform is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * Open Data Plaform is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.

 * You should have received a copy of the GNU Affero General Public License
 * along with Open Data Platform.  If not, see <http://www.gnu.org/licenses/agpl-3.0>.
 */

package de.fhg.fokus.odp.entities.service.impl;

import de.fhg.fokus.odp.entities.service.base.MetadataCommentLocalServiceBaseImpl;
import de.fhg.fokus.odp.entities.service.persistence.MetadataCommentUtil;

/**
 * The implementation of the metadata comment local service.
 * 
 * <p>
 * All custom service methods should be put in this class. Whenever methods are added, rerun ServiceBuilder to copy their definitions into the
 * {@link de.fhg.fokus.odp.entities.service.MetadataCommentLocalService} interface.
 * 
 * <p>
 * This is a local service. Methods of this service will not have security checks based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 * </p>
 * 
 * @author Brian Wing Shun Chan
 * @see de.fhg.fokus.odp.entities.service.base.MetadataCommentLocalServiceBaseImpl
 * @see de.fhg.fokus.odp.entities.service.MetadataCommentLocalServiceUtil
 */
public class MetadataCommentLocalServiceImpl extends MetadataCommentLocalServiceBaseImpl {
    /*
     * NOTE FOR DEVELOPERS:
     * 
     * Never reference this interface directly. Always use {@link de.fhg.fokus.odp.entities.service.MetadataCommentLocalServiceUtil} to access the metadata
     * comment local service.
     */

    /**
     * Find byuser liferay id.
     * 
     * @param userLiferayId
     *            the user liferay id
     * @return the list<de.fhg.fokus.odp.entities.model. metadata comment>
     * @throws SystemException
     *             the system exception
     */
    @Override
    public java.util.List<de.fhg.fokus.odp.entities.model.MetadataComment> findByuserLiferayId(long userLiferayId)
            throws com.liferay.portal.kernel.exception.SystemException {
        return MetadataCommentUtil.findByuserLiferayId(userLiferayId);
    }

    /**
     * Find bymetadata name.
     * 
     * @param metadataName
     *            the metadata name
     * @return the java.util. list
     * @throws SystemException
     *             the system exception
     */
    @Override
    public java.util.List<de.fhg.fokus.odp.entities.model.MetadataComment> findBymetadataName(java.lang.String metadataName)
            throws com.liferay.portal.kernel.exception.SystemException {
        return MetadataCommentUtil.findBymetadataName(metadataName);
    }

}
