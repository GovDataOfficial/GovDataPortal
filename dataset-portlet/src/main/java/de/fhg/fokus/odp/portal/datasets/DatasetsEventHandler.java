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

/**
 * 
 */
package de.fhg.fokus.odp.portal.datasets;

import java.io.Serializable;

import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.faces.context.FacesContext;
import javax.portlet.Event;
import javax.portlet.faces.BridgeEventHandler;
import javax.portlet.faces.event.EventNavigationResult;

import com.liferay.faces.portal.context.LiferayFacesContext;
import com.liferay.portal.theme.ThemeDisplay;

import de.fhg.fokus.odp.registry.queries.Query;

// TODO: Auto-generated Javadoc
/**
 * The Class DatasetsEventHandler.
 * 
 * @author sim
 */
public class DatasetsEventHandler implements BridgeEventHandler, Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.portlet.faces.BridgeEventHandler#handleEvent(javax.faces.context
	 * .FacesContext, javax.portlet.Event)
	 */
	@Override
	public EventNavigationResult handleEvent(FacesContext facesContext,
			Event event) {
		EventNavigationResult eventNavigationResult = null;

		String eventQName = event.getQName().toString();
		if (eventQName
				.equals("{http://fokus.fraunhofer.de/odplatform}querydatasets")) {
			Query query = (Query) event.getValue();
			if (query != null) {
				eventNavigationResult = new EventNavigationResult();
				eventNavigationResult.setOutcome("whatever");

				ThemeDisplay themeDisplay = LiferayFacesContext.getInstance()
						.getThemeDisplay();
				String currentPage = themeDisplay.getLayout().getFriendlyURL();
				if (currentPage.equals("/home")) {
					currentPage = "/suchen";
				}
				QueryManager queryManager = getQueryManagerBean(facesContext);
				if (queryManager != null) {
					// queryManager.addQuery(currentPage, query);
					queryManager.setQuery(query);
				}
			}
		} else if (eventQName
				.equals("{http://fokus.fraunhofer.de/odplatform}metadataAppCreated")) {
			Feedback feedback = getFeedbackBean(facesContext);
			if (feedback != null) {
				feedback.setMetadataAppCreated(true);
			}
		}

		return eventNavigationResult;
	}

	/**
	 * Gets the query manager bean.
	 * 
	 * @param facesContext
	 *            the faces context
	 * @return the query manager bean
	 */
	protected QueryManager getQueryManagerBean(FacesContext facesContext) {
		String elExpression = "#{queryManager}";
		ELContext elContext = facesContext.getELContext();
		ValueExpression valueExpression = facesContext
				.getApplication()
				.getExpressionFactory()
				.createValueExpression(elContext, elExpression,
						QueryManager.class);

		return (QueryManager) valueExpression.getValue(elContext);
	}

	/**
	 * Gets the feedback bean.
	 * 
	 * @param facesContext
	 *            the faces context
	 * @return the feedback bean
	 */
	protected Feedback getFeedbackBean(FacesContext facesContext) {
		String elExpression = "#{feedback}";
		ELContext elContext = facesContext.getELContext();
		ValueExpression valueExpression = facesContext.getApplication()
				.getExpressionFactory()
				.createValueExpression(elContext, elExpression, Feedback.class);

		return (Feedback) valueExpression.getValue(elContext);
	}

}
