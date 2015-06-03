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

import com.liferay.faces.portal.context.LiferayFacesContext;
import com.liferay.portal.kernel.language.LanguageUtil;

@FacesValidator(value = "licenceValidator")
public class LicenceValidator implements Validator {

    @Override
    public void validate(FacesContext arg0, UIComponent arg1, Object arg2) throws ValidatorException {

        String licence = (String) arg2;

        if (licence.contains("de.fhg.fokus.odp.registry.ckan.impl.LicenceImpl")) {
            LiferayFacesContext lfc = LiferayFacesContext.getInstance();
            PortletRequest request = (PortletRequest) lfc.getExternalContext().getRequest();

            FacesMessage msg = new FacesMessage(LanguageUtil.get(request.getLocale(), "od.validation_required"), LanguageUtil.get(
                    request.getLocale(), "od.validation_required"));
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(msg);
        }

    }

}
