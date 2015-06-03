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

package de.fhg.fokus.odp.rssservlet.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LiferayPropsUtil {
    /** Local constant for the relative path to the Liferay properties file. */
    private static final String RELATIVE_PATH = ".." + File.separator + ".." + File.separator + ".." + File.separator;
    // TODO: make the above constant configurable over a properties file

    /** The filename of the properties file. */
    private static final String CONF_PROPERTIES_FILENAME = "portal-ext.properties";
    // TODO: make the above constant configurable over a properties file

    /** The logger for the class. */
    private static final Logger LOG = LoggerFactory.getLogger(LiferayPropsUtil.class);

    public static String getValueFromKey(String key, ServletContext context) {
        LOG.debug("Properties file for configuring the RSS servlet ...");

        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream(context.getRealPath("/") + RELATIVE_PATH + CONF_PROPERTIES_FILENAME));
        } catch (FileNotFoundException e) {
            LOG.debug(e.getMessage());
            return null;
        } catch (IOException e) {
            LOG.debug(e.getMessage());
            return null;
        }

        return prop.getProperty(key);
    }
}
