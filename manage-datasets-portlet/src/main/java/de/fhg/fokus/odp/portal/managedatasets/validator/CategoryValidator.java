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

package de.fhg.fokus.odp.portal.managedatasets.validator;

import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.portlet.PortletRequest;

import org.primefaces.context.RequestContext;
import org.primefaces.event.CloseEvent;
import org.primefaces.event.ToggleEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liferay.faces.portal.context.LiferayFacesContext;
import com.liferay.portal.kernel.language.LanguageUtil;

@FacesValidator(value = "categoryValidator")
public class CategoryValidator implements Validator {

	private static final Logger log = LoggerFactory
			.getLogger(CategoryValidator.class);

	@Override
	public void validate(FacesContext context, UIComponent component,
			Object value) throws ValidatorException {

		LiferayFacesContext lfc = LiferayFacesContext.getInstance();
		PortletRequest request = (PortletRequest) lfc.getExternalContext()
				.getRequest();

		/*
		 * @SuppressWarnings("rawtypes") List selectedVals = (List) value; int
		 * numberOfCaterories = 0; if (selectedVals != null) {
		 * numberOfCaterories = selectedVals.size(); }
		 * 
		 * if (numberOfCaterories > 2) { log.info("numberOfCaterories > 5 == "
		 * +numberOfCaterories ); FacesMessage message = new
		 * FacesMessage(FacesMessage.SEVERITY_WARN, "What we do in life",
		 * "Echoes in eternity.");
		 * 
		 * RequestContext.getCurrentInstance().showMessageInDialog(message);
		 * 
		 * 
		 * String message = LanguageUtil.get(request.getLocale(),
		 * "od.category.reference.toomuch.warn"); FacesMessage msg = new
		 * FacesMessage(message, message);
		 * 
		 * msg.setSeverity(FacesMessage.SEVERITY_ERROR);
		 * 
		 * 
		 * throw new ValidatorException(msg); }
		 */

	}
}
