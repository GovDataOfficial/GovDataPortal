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
import java.util.Calendar;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.portlet.ActionResponse;
import javax.xml.namespace.QName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liferay.faces.portal.context.LiferayFacesContext;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.Layout;
import com.liferay.portal.theme.ThemeDisplay;

import de.fhg.fokus.odp.registry.model.MetadataEnumType;
import de.fhg.fokus.odp.registry.queries.Query;

/**
 * @author jja
 * @author msg 22.05.2014
 * @see /boxes-portlet/[..]/Tags.search()
 * @see https://extsvnsrv.fokus.fraunhofer.de/cc/elan/OGDD/ticket/37
 *      dataset-portlet: Kategorien und Schlagworte anklickbar
 */
@ManagedBean
@SessionScoped
public class ClickableCategoriesAndTags implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7404342001236008507L;

	private final Logger log = LoggerFactory.getLogger(getClass());

	public void setClickedCategory(String clickedCategory) {
		this.clickedCategory = clickedCategory;
	}

	public void setClickedTag(String clickedTag) {
		this.clickedTag = clickedTag;
	}

	private String clickedCategory = "";
	private String clickedTag = "";

	public String onClick() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		Object responseObject = facesContext.getExternalContext().getResponse();
		if (responseObject != null && responseObject instanceof ActionResponse) {
			ActionResponse actionResponse = (ActionResponse) responseObject;
			Query query = new Query();
			Calendar cal = Calendar.getInstance();
			if (!clickedCategory.isEmpty()) {
				query.getCategories().add(
						clickedCategory + ":#:" + cal.getTimeInMillis());
				clickedCategory = "";
			} else if (!clickedTag.isEmpty()) {
				query.getTags().add(clickedTag + ":#:" + cal.getTimeInMillis());
				clickedTag = "";

			}

			actionResponse.setEvent(new QName(
					"http://fokus.fraunhofer.de/odplatform", "querydatasets"),
					query);

			ThemeDisplay td = LiferayFacesContext.getInstance()
					.getThemeDisplay();

			String location = td.getPortalURL();
			Layout layout = td.getLayout();

			try {
				if (layout.isPublicLayout()) {
					location += LiferayFacesContext.getInstance()
							.getThemeDisplay().getPathFriendlyURLPublic();
				}

				location += layout.hasScopeGroup() ? layout.getScopeGroup()
						.getFriendlyURL() : layout.getGroup().getFriendlyURL();
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

}
