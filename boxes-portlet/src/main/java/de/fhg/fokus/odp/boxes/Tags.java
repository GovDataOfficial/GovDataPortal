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

package de.fhg.fokus.odp.boxes;

// imports
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.portlet.ActionResponse;
import javax.xml.namespace.QName;

import com.liferay.faces.portal.context.LiferayFacesContext;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.model.Layout;

import de.fhg.fokus.odp.registry.model.Tag;
import de.fhg.fokus.odp.registry.queries.Query;

/**
 * The class constitutes a bean that serves as a source for the most popular tags on the start page boxes.
 * 
 * @author Nikolay Tcholtchev, Fraunhofer FOKUS
 * 
 */
@ManagedBean
@RequestScoped
public class Tags {

    /** The maximum number of most popular tags to show. */
    private static final int maximumNumberOfTags = 4;

    /** The array with the most popular tags. */
    private List<Tag> tags;

    /** The selected tag for the redirection. */
    private String selectedTag;

    @ManagedProperty("#{registryClient}")
    private RegistryClient registryClient;

    /**
     * An init method for the bean.
     */
    @PostConstruct
    public void init() {

        // get the list of tags
        tags = new ArrayList<Tag>();
        for (Tag tag : registryClient.getMostPopularTags(maximumNumberOfTags)) {
            tags.add(tag);
        }
    }

    /**
     * Gets the selected tag.
     * 
     * @return the selected tag.
     */
    public String getSelectedTag() {
        return selectedTag;
    }

    /**
     * Sets the selected tag.
     * 
     * @param selectedTag
     *            the selected tag to set.
     */
    public void setSelectedTag(String selectedTag) {
        this.selectedTag = selectedTag;
    }

    /**
     * Gets the tags.
     * 
     * @return the tags
     */
    public List<Tag> getTags() {
        return tags;
    }

    /**
     * Sets the tags.
     * 
     * @param tags
     *            the new tags
     */
    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    /**
     * The search redirection.
     * 
     * @return an empty string.
     */
    public String search() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        Object responseObject = facesContext.getExternalContext().getResponse();

        if (responseObject != null && responseObject instanceof ActionResponse) {
            ActionResponse actionResponse = (ActionResponse) responseObject;

            Query query = new Query();
            query.getTags().add(selectedTag);
            actionResponse.setEvent(new QName("http://fokus.fraunhofer.de/odplatform", "querydatasets"), query);

            String location = LiferayFacesContext.getInstance().getThemeDisplay().getPortalURL();
            Layout layout = LiferayFacesContext.getInstance().getThemeDisplay().getLayout();

            try {
                if (layout.isPublicLayout()) {
                    location += LiferayFacesContext.getInstance().getThemeDisplay().getPathFriendlyURLPublic();
                }

                location += layout.hasScopeGroup() ? layout.getScopeGroup().getFriendlyURL() : layout.getGroup().getFriendlyURL();
                location += "/suchen";
            } catch (PortalException e) {
                e.printStackTrace();
            } catch (SystemException e) {
                e.printStackTrace();
            }

            try {
                facesContext.getExternalContext().redirect(location);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    /**
     * @param registryClient
     *            the registryClient to set
     */
    public void setRegistryClient(RegistryClient registryClient) {
        this.registryClient = registryClient;
    }

}
