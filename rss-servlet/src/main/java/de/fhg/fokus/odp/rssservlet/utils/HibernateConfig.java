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

import java.util.Properties;

import javax.servlet.ServletContext;

import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fhg.fokus.odp.rssservlet.CommentaryEntity;

/**
 * The HibernateConfiguration. Use enum for singleton pattern.
 */
public enum HibernateConfig {

    /** The instance. */
    INSTANCE;

    /** The logger for the class. */
    private final Logger LOG = LoggerFactory.getLogger(getClass());

    /**
     * Gets the connection.
     * 
     * @param contextPath
     *            the context path
     * @return the connection
     */
    public Configuration getConnection(ServletContext context) {

        Properties props = new Properties();
        props.setProperty("hibernate.connection.driver_class", LiferayPropsUtil.getValueFromKey("jdbc.default.driverClassName", context));
        props.setProperty("hibernate.connection.url", LiferayPropsUtil.getValueFromKey("jdbc.default.url", context));
        props.setProperty("hibernate.connection.username", LiferayPropsUtil.getValueFromKey("jdbc.default.username", context));
        props.setProperty("hibernate.connection.password", LiferayPropsUtil.getValueFromKey("jdbc.default.password", context));
        props.setProperty("hibernate.connection.pool_size", "20");

        props.setProperty("hibernate.c3p0.min_size", "5");
        props.setProperty("hibernate.c3p0.max_size", "20");
        props.setProperty("hibernate.c3p0.timeout", "300");
        props.setProperty("hibernate.c3p0.max_statements", "50");

        LOG.debug("Using the following props for hibernate: {}", props);

        Configuration conf = new Configuration();
        conf.setProperties(props);
        conf.addAnnotatedClass(CommentaryEntity.class);
        return conf;
    }
}
