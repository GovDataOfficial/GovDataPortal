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

package de.fhg.fokus.odp.portal.datasets;

import java.io.IOException;
import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.portlet.ActionResponse;
import javax.xml.namespace.QName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liferay.faces.portal.context.LiferayFacesContext;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.model.Layout;

import de.fhg.fokus.odp.registry.queries.Query;

/**
 * @author jja
 * @see /boxes-portlet/[..]/Tags.search()
 * @see https://extsvnsrv.fokus.fraunhofer.de/cc/elan/OGDD/ticket/37
 *      dataset-portlet: Kategorien und Schlagworte anklickbar
 */
@ManagedBean
@ViewScoped
public class ResetSearch implements Serializable {

	private static final long serialVersionUID = 740434211123608507L;
	private final Logger log = LoggerFactory.getLogger(getClass());

	public String onClick() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		Object responseObject = facesContext.getExternalContext().getResponse();
		if (responseObject != null && responseObject instanceof ActionResponse) {
			ActionResponse actionResponse = (ActionResponse) responseObject;
			Query query = new Query();

			actionResponse.removePublicRenderParameter("searchterm");
			actionResponse.removePublicRenderParameter("searchcategory");

			actionResponse.setEvent(new QName(
					"http://fokus.fraunhofer.de/odplatform", "querydatasets"),
					query);

			String location = LiferayFacesContext.getInstance()
					.getThemeDisplay().getPortalURL();
			Layout layout = LiferayFacesContext.getInstance().getThemeDisplay()
					.getLayout();

			try {
				if (layout.isPublicLayout()) {
					location += LiferayFacesContext.getInstance()
							.getThemeDisplay().getPathFriendlyURLPublic();
				}
				location += layout.hasScopeGroup() ? layout.getScopeGroup()
						.getFriendlyURL() : layout.getGroup().getFriendlyURL();
				String currPage = layout.getFriendlyURL(); // currPage may be
															// /suchen /daten,
															// etc.
				location += currPage;
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

}
