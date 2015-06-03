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

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.portlet.PortletRequest;

import org.apache.commons.validator.routines.UrlValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liferay.faces.portal.context.LiferayFacesContext;
import com.liferay.portal.kernel.language.LanguageUtil;

@FacesValidator(value = "urlValidator")
public class URLValidator implements Validator {
	   private static final Logger log = LoggerFactory
   			.getLogger(URLValidator.class);
    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
    	
		String[] schemes = { "http", "https", "ftp", "ftps" };
		UrlValidator urlValidator = new UrlValidator(schemes);
		// UrlValidator urlValidator = new UrlValidator();
		
        if (!urlValidator.isValid((String) value) || ((String) value).isEmpty()) {
            LiferayFacesContext lfc = LiferayFacesContext.getInstance();
            PortletRequest request = (PortletRequest) lfc.getExternalContext().getRequest();

            FacesMessage msg = new FacesMessage(LanguageUtil.get(request.getLocale(), "od.url.invalid.error"), LanguageUtil.get(
                    request.getLocale(), "od.url.invalid.error"));
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);           
            throw new ValidatorException(msg);
        }

    }

}
