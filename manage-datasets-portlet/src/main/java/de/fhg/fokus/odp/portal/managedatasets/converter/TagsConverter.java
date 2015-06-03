/**
 * Copyright (c) 2012, 2015 Fraunhofer Institute FOKUS
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

package de.fhg.fokus.odp.portal.managedatasets.converter;

import java.util.ArrayList;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The TagsConverter.
 * 
 * @author msg
 */

@FacesConverter("tagsConverter")
public class TagsConverter implements Converter {
	/** The logger. */
	// private final Logger LOG = LoggerFactory.getLogger(getClass());

	public String munge_tag(String tag) {
		tag = tag.toLowerCase().trim();
		tag = tag.replaceAll("[^a-zA-ZÄÖÜäöü0-9 \\-_\\.]", "");
		// replace 2 or more spaces with single space in string and delete
		// leading spaces only
		tag = tag.replaceAll(" +", " ");
		tag = tag.replaceAll(" ", "-");

		return tag;
	}

	@Override
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2) {
		List<String> tags = new ArrayList<String>();
		if (!arg2.isEmpty()) {
			arg2 = arg2.replaceAll(";", ",");
			for (String tag : arg2.split(",")) {
				tags.add(tag.trim());
			}
		}
		return tags;
	}

	@SuppressWarnings("unchecked")
	@Override
	public String getAsString(FacesContext arg0, UIComponent arg1, Object arg2) {
		List<String> tags = (List<String>) arg2;

		StringBuilder sb = new StringBuilder();
		if (!tags.isEmpty()) {
			for (String tag : tags) {
				tag = munge_tag(tag);
				if (tag != null && !tag.isEmpty())
					sb.append(tag).append(", ");
			}
			if (sb.lastIndexOf(",") != -1) {
				sb.delete(sb.lastIndexOf(","), sb.length() - 1);
			}
		}
		return sb.toString();
	}

}
