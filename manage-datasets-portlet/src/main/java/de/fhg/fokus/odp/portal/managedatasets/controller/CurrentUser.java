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

package de.fhg.fokus.odp.portal.managedatasets.controller;

import java.io.Serializable;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.portlet.PortletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liferay.portal.kernel.util.PropsUtil;

import de.fhg.fokus.odp.registry.ODRClient;
import de.fhg.fokus.odp.registry.model.User;
import de.fhg.fokus.odp.spi.OpenDataRegistry;

// TODO: Auto-generated Javadoc
/**
 * The Class CurrentUser.
 */
@ManagedBean(name = "currentUser")
@SessionScoped
public class CurrentUser implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The odr client. */
    private ODRClient odrClient;

    /** The logger. */
    private final Logger LOG = LoggerFactory.getLogger(getClass());

    /** The current user. */
    private User currentUser;

    /**
     * Inits the.
     */
    @PostConstruct
    public void init() {

        Properties props = new Properties();
        props.setProperty("ckan.authorization.key", PropsUtil.get("authenticationKey"));
        props.setProperty("ckan.url", PropsUtil.get("cKANurl"));

        LOG.debug("odRClient props:" + props.toString());

        odrClient = OpenDataRegistry.getClient();
        odrClient.init(props);

        PortletRequest request = (PortletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        @SuppressWarnings("unchecked")
        Map<String, String> userInfo = (Map<String, String>) request.getAttribute(PortletRequest.USER_INFO);
        if (userInfo != null) {
            String loginid = userInfo.get("user.login.id");
            if (loginid != null) {
                currentUser = odrClient.findUser(loginid);
            }
        }
    }

    /**
     * Gets the current user.
     * 
     * @return the current user
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Sets the current user.
     * 
     * @param currentUser
     *            the new current user
     */
    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

}
