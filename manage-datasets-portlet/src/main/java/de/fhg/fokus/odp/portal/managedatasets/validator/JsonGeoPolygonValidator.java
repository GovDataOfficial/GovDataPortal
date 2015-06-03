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


package de.fhg.fokus.odp.portal.managedatasets.validator;

import java.io.IOException;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.portlet.PortletRequest;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;
import com.liferay.faces.portal.context.LiferayFacesContext;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.util.PropsUtil;

/**
 * The Validator for JsonGeo Polygon.
 * 
 * @author msg
 */

@FacesValidator(value = "jsonGeoPolygonValidator")
public class JsonGeoPolygonValidator implements Validator {

	private static final String PROP_SPATIAL_VALIDATE = "od.spatial.validate";

	private boolean isValidJSON(final String json) {
		boolean valid = false;
		try {
			final JsonParser parser = new ObjectMapper().getJsonFactory()
					.createJsonParser(json);
			while (parser.nextToken() != null) {
			}
			valid = true;
		} catch (JsonParseException jpe) {
			jpe.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return valid;
	}

	private void throwPolygonError(PortletRequest request, String type) {
		FacesMessage msg = new FacesMessage(LanguageUtil.get(
				request.getLocale(), "od.spatial" + type + "invalid.error"),
				LanguageUtil.get(request.getLocale(), "od.spatial" + type
						+ "invalid.error"));
		msg.setSeverity(FacesMessage.SEVERITY_ERROR);
		throw new ValidatorException(msg);

	}

	@Override
	public void validate(FacesContext context, UIComponent component,
			Object value) throws ValidatorException {

		if (value != null && !((String) value).isEmpty()) {
			LiferayFacesContext lfc = LiferayFacesContext.getInstance();
			PortletRequest request = (PortletRequest) lfc.getExternalContext()
					.getRequest();

			String spatialValidate = PropsUtil.get(PROP_SPATIAL_VALIDATE);
			// default behavior is 2 validate
			boolean bSpatialValidate = true;

			// log.info("validateBoolean.parseBoolean(spatialValidate) ="
			// + Boolean.parseBoolean(spatialValidate));

			if (spatialValidate != null) {
				bSpatialValidate = Boolean.parseBoolean(spatialValidate);
			} else {
				// default behavior is 2 validate
				bSpatialValidate = true;
			}

			if (!bSpatialValidate)
				return;

			if (!isValidJSON((String) value))
				throwPolygonError(request, ".");

			ObjectMapper mapper = new ObjectMapper();
			JsonNode spatialNode = null;
			try {
				spatialNode = mapper.readTree((String) value);
			} catch (JsonProcessingException e1) {
				throwPolygonError(request, ".");
			} catch (IOException e1) {
				throwPolygonError(request, ".");
			}

			String type;
			try {
				type = spatialNode.get("type").getTextValue();
				if (type == null || type.isEmpty()) {
					throwPolygonError(request, ".type.");
				}
			} catch (Exception e) {
				throwPolygonError(request, ".type.");
			}

			JsonNode coordinatesNode = null;
			try {
				coordinatesNode = spatialNode.get("coordinates");
				if (coordinatesNode == null || !coordinatesNode.isArray()
						|| coordinatesNode.size() == 0) {
					throwPolygonError(request, ".coordinates.");
				}
			} catch (Exception e) {
				throwPolygonError(request, ".coordinates.");
			}

			if (coordinatesNode.isArray() && coordinatesNode.size() > 0) {
				for (int i = 0; i < coordinatesNode.size(); i++) {
					int co = 0;
					double x1 = 0.0, y1 = 0.0, x4 = 0.0, y4 = 0.0;
					JsonNode inner = null;
					try {
						inner = coordinatesNode.get(i);
					} catch (Exception e) {
						throwPolygonError(request, ".");
					}
					if (!inner.isArray() || inner.size() < 3) {
						throwPolygonError(request, ".polygon.");
					}
					for (JsonNode coordinate : inner) {
						JsonNode xNode = null;
						JsonNode yNode = null;
						if (!coordinate.isArray() || coordinate.size() < 2) {
							throwPolygonError(request, ".coordinate.");
						}
						if (coordinate.isArray()) {
							try {
								xNode = coordinate.get(0);
								yNode = coordinate.get(1);
							} catch (Exception e) {
								throwPolygonError(request, ".");
							}
						}
						if (co == 0) {
							x1 = xNode.getDoubleValue();
							y1 = yNode.getDoubleValue();
						}
						if (co == 4) {
							x4 = xNode.getDoubleValue();
							y4 = yNode.getDoubleValue();
						}
						if (co == 4 && (x1 != x4 || y1 != y4)) {
							throwPolygonError(request, ".firstlast.");
						}
						co++;
					}
				}
			}
		}
	}
}
