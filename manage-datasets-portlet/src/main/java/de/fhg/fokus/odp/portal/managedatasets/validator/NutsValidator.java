/**
 * Copyright (c) 2012, 2014 Fraunhofer Institute FOKUS
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
 * The Validator for JsonGeo Polygon.
 * 
 * @author msg
 */
package de.fhg.fokus.odp.portal.managedatasets.validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.portlet.PortletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liferay.faces.portal.context.LiferayFacesContext;
import com.liferay.portal.kernel.language.LanguageUtil;

@FacesValidator(value = "nutsValidator")
public class NutsValidator implements Validator {

	private static final Logger log = LoggerFactory
			.getLogger(NutsValidator.class);

	@Override
	public void validate(FacesContext context, UIComponent component,
			Object value) throws ValidatorException {

		if (value != null && !((String) value).isEmpty()
				&& !((String) value).matches("^[A-Z]{2}[0-9A-Z]{0,3}$")) {
			LiferayFacesContext lfc = LiferayFacesContext.getInstance();
			PortletRequest request = (PortletRequest) lfc.getExternalContext()
					.getRequest();

			FacesMessage msg = new FacesMessage(LanguageUtil.get(
					request.getLocale(), "od.nuts.invalid.error"),
					LanguageUtil.get(request.getLocale(),
							"od.nuts.invalid.error"));
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);
			throw new ValidatorException(msg);

		}
	}

}
