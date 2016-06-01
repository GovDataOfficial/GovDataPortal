package de.fhg.fokus.odp.entities.service.messaging;

import com.liferay.portal.kernel.messaging.BaseMessageListener;
import com.liferay.portal.kernel.messaging.Message;

import de.fhg.fokus.odp.entities.service.ClpSerializer;
import de.fhg.fokus.odp.entities.service.MetadataCommentLocalServiceUtil;


public class ClpMessageListener extends BaseMessageListener {
    public static String getServletContextName() {
        return ClpSerializer.getServletContextName();
    }

    @Override
    protected void doReceive(Message message) throws Exception {
        String command = message.getString("command");
        String servletContextName = message.getString("servletContextName");

        if (command.equals("undeploy") &&
                servletContextName.equals(getServletContextName())) {
            MetadataCommentLocalServiceUtil.clearService();
        }
    }
}
