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

package de.fhg.fokus.odp.portal.managedatasets.eventhandler;

import javax.faces.context.FacesContext;
import javax.portlet.Event;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.portlet.faces.BridgeEventHandler;
import javax.portlet.faces.event.EventNavigationResult;

import de.fhg.fokus.odp.registry.model.Metadata;

public class ManageEventHandler implements BridgeEventHandler {

    @SuppressWarnings({ "static-access" })
    @Override
    public EventNavigationResult handleEvent(FacesContext facesContext, Event event) {

        String eventQName = event.getQName().toString();
        if (eventQName.equals("{http://fokus.fraunhofer.de/odplatform}metadata")) {
            Metadata eData = (Metadata) event.getValue();
            if (eData != null) {
                PortletRequest request = (PortletRequest) facesContext.getCurrentInstance().getExternalContext().getRequest();
                PortletSession session = request.getPortletSession();
                session.setAttribute("metadata", eData);
            }
        }
        return null;
    }
}
