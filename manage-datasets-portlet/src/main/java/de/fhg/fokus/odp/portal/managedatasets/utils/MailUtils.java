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

package de.fhg.fokus.odp.portal.managedatasets.utils;

import java.text.MessageFormat;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.portlet.PortletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liferay.faces.portal.context.LiferayFacesContext;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;

import de.fhg.fokus.odp.registry.model.Metadata;

public class MailUtils {

    static private final Logger LOG = LoggerFactory.getLogger(MailUtils.class.getCanonicalName());

    /**
     * Send mail.
     * 
     * @throws MessagingException
     *             the messaging exception
     * @throws SystemException
     * @throws PortalException
     */
    public static void sendMail(Metadata metadata) throws PortalException, SystemException, MessagingException {

        // get some global objects
        LiferayFacesContext lfc = LiferayFacesContext.getInstance();
        ThemeDisplay tD = lfc.getThemeDisplay();
        PortletRequest request = (PortletRequest) lfc.getExternalContext().getRequest();

        // get the email subject and body
        String subject = LanguageUtil.get(request.getLocale(), "od.app.announced.email.subject");
        String body = MessageFormat.format(LanguageUtil.get(request.getLocale(), "od.app.announced.email.body"), tD.getUser().getLogin(),
                metadata.getTitle(), metadata.getName());

        // get the from address
        String fromStr = PortalUtil.getPortalProperties().getProperty("admin.email.from.address");
        if (fromStr == null || fromStr.matches("^\\s*$")) {
            try {
                fromStr = PrefsPropsUtil.getString(tD.getCompanyId(), "admin.email.from.address");
            } catch (SystemException e) {
                LOG.error(e.getMessage());
                return;
            }
        }

        // check if an address can be send out
        if (fromStr == null || fromStr.matches("^\\s*%")) {
            LOG.error("No from email address found");
            return;
        }
        InternetAddress from = new InternetAddress(fromStr);

        // prepare the properties and session
        Properties props = new Properties();

        String smtpHost = PropsUtil.get("mail.session.mail.smtp.host");
        if (smtpHost == null || smtpHost.matches("^\\s*$")) {
            LOG.error("No smtp host specified");
            return;
        }

        props.put("mail.smtp.host", smtpHost);
        String port = PropsUtil.get("mail.session.mail.smtp.port");
        if (port != null && !port.matches("^\\s*$")) {
            props.put("mail.smtp.port", port);
        }

        Session session = Session.getDefaultInstance(props);

        // start with building up the message
        Message msg = new MimeMessage(session);

        // add BCC for observing comments
        msg.addRecipient(Message.RecipientType.TO, new InternetAddress(PropsUtil.get("mail.comment.cc.address")));

        msg.setContent(body, "text/plain");
        msg.setFrom(from);
        msg.setSubject(subject);
        Transport.send(msg);
    }

}
