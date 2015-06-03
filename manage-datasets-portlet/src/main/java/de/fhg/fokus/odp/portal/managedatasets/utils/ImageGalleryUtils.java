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

package de.fhg.fokus.odp.portal.managedatasets.utils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liferay.faces.bridge.model.UploadedFile;
import com.liferay.faces.portal.context.LiferayFacesContext;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portlet.documentlibrary.model.DLFolder;
import com.liferay.portlet.documentlibrary.service.DLAppLocalServiceUtil;
import com.liferay.portlet.documentlibrary.service.DLFolderLocalServiceUtil;

// TODO: Auto-generated Javadoc
/**
 * The Class ImageGalleryUtils.
 */
public class ImageGalleryUtils {

    /** The logger. */
    private static final Logger LOG = LoggerFactory.getLogger(ImageGalleryUtils.class);

    /**
     * Upload image.
     * 
     * @param image
     *            the image
     * @return the uri
     * @throws SystemException
     *             the system exception
     * @throws URISyntaxException
     *             the uRI syntax exception
     * @throws IOException
     * @throws PortalException
     */
    public static URI uploadImage(UploadedFile image) throws SystemException, URISyntaxException, IOException, PortalException,
            NullPointerException {

        LiferayFacesContext lfc = LiferayFacesContext.getInstance();
        ThemeDisplay td = lfc.getThemeDisplay();

        ServiceContext serviceContext = new ServiceContext();
        serviceContext.setScopeGroupId(td.getScopeGroupId());

        long imageFolderId = Long.parseLong(PropsUtil.get("image.folder.id"));
        DLFolder folder;

        try {
            /* Check if folder for image exists */
            folder = DLFolderLocalServiceUtil.getDLFolder(imageFolderId);
            LOG.debug("Folder for app images exists.");
        } catch (PortalException e) {
            /* If not -> create */
            folder = DLFolderLocalServiceUtil.createDLFolder(imageFolderId);
            folder.setName("App-Images");
            DLFolderLocalServiceUtil.addDLFolder(folder);
            LOG.debug("Created folder for app images.");
        }

        String uuidExt = null;
        FileEntry file = null;

        uuidExt = td.getUser().getScreenName() + "_" + UUID.randomUUID().toString() + "_" + new Date().getTime() + "_";

        file = DLAppLocalServiceUtil.addFileEntry(td.getUserId(), td.getScopeGroupId(), folder.getFolderId(), uuidExt + image.getName(),
                MimeTypesUtil.getContentType(image.getName()), uuidExt + image.getName(), uuidExt + image.getName(), "new",
                image.getBytes(), serviceContext);

        return new URI(td.getPortalURL() + "/c/document_library/get_file?uuid=" + file.getUuid() + "&groupId=" + td.getScopeGroupId());
    }
}
