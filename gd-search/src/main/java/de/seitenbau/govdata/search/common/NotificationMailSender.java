package de.seitenbau.govdata.search.common;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.inject.Inject;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.liferay.mail.service.MailServiceUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.mail.MailMessage;
import com.liferay.portal.model.Role;
import com.liferay.portal.model.User;
import com.liferay.portal.service.RoleLocalServiceUtil;
import com.liferay.portal.service.UserLocalServiceUtil;

import de.fhg.fokus.odp.registry.model.Contact;
import de.fhg.fokus.odp.registry.model.Metadata;
import de.fhg.fokus.odp.registry.model.RoleEnumType;
import de.seitenbau.govdata.navigation.GovDataNavigation;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class NotificationMailSender {
  private static final String CHEFREDAKTEUR = "Chefredakteur";
  private static final String REDAKTEUR = "Redakteur";

  public enum EventType {
    NEW("new"), CHANGED("changed");

    private String key;

    EventType(String key) {
      this.key = key;
    }

    public String getKey() {
      return key;
    }
  }

  @Inject
  private GovDataNavigation gdNavigation;

  @Value("${admin.email.from.name}")
  private String emailName;

  @Value("${admin.email.from.address}")
  private String emailAddress;

  /**
   * Send a Notification-Email to moderators and owner of the dataset.
   * 
   * @param companyId
   *          id of the liferay company instance
   * @param comment
   *          text of the comment
   * @param type
   *          new comment or edited comment?
   * @param user
   *          acting user
   * @param metadataName
   *          id of the dataset
   */
  public void notifyCommentEvent(long companyId, String comment, EventType type, User user, Metadata metadata,
      Locale locale) {
    Set<String> recipients = new HashSet<>(); // Set ensures every e-mail-adress only receives one email

    try {
      // get all moderators
      recipients.addAll(getEmailsByRolename(companyId, REDAKTEUR));
      recipients.addAll(getEmailsByRolename(companyId, CHEFREDAKTEUR));
      
      // get the owner of the dataset
      Contact contact = metadata.getContact(RoleEnumType.MAINTAINER);
      if (contact != null && StringUtils.isNotEmpty(contact.getEmail())) {
        recipients.add(contact.getEmail());
      }
      
      String subject =
          MessageFormat.format(LanguageUtil.get(locale, "od.datasets.comment.email.subject." + type.getKey()),
              metadata.getType().getDisplayName());

      InternetAddress from = new InternetAddress(emailName + " <" + emailAddress + ">");

      String datasetUrl = gdNavigation.createLinkForMetadata("gdsearchdetails", metadata.getName()).toString();
      String body = MessageFormat.format(
          LanguageUtil.get(locale, "od.datasets.comment.email.template." + type.getKey()), comment, // Comment
          user.getFullName() + " <" + user.getDisplayEmailAddress() + ">", // User
          metadata.getTitle(), // Name of the dataset
          datasetUrl); // Url of the dataset

      // prepare BCC list (alternative solution)
//      ArrayList<InternetAddress> bcc = new ArrayList<>();
//      for (String recipient : recipients) {
//         bcc.add(new InternetAddress(recipient));
//      }
//      
//      MailMessage msg = new MailMessage();
//      msg.setFrom(from);
//      msg.setBCC(bcc.toArray(new InternetAddress[bcc.size()]));
//      msg.setSubject(subject);
//      msg.setBody(body.toString());
//      MailServiceUtil.sendEmail(msg);

      for (String recipient : recipients) { // make sure your mail server is fast enough ;)
        try {
          MailMessage msg = new MailMessage();
          msg.setFrom(from);
          msg.setTo(new InternetAddress(recipient));
          msg.setSubject(subject);
          msg.setBody(body.toString());
          MailServiceUtil.sendEmail(msg);
        } catch (AddressException e) {
          log.warn("Mail could not be sent to «" + recipient + "», error: " + e.getMessage());
        }
      }

    } catch (AddressException | SystemException | PortalException e) {
      log.warn("Mail could not be sent, error: " + e.getMessage());
    }
  }

  private List<String> getEmailsByRolename(long companyId, String roleName)
      throws PortalException, SystemException {
    
    List<String> recipients = new ArrayList<>();
    Role role = RoleLocalServiceUtil.getRole(companyId, roleName);
    long[] userIds = UserLocalServiceUtil.getRoleUserIds(role.getRoleId());

    for (long userId : userIds) {
      User moderatorUser = UserLocalServiceUtil.getUserById(userId);
      recipients.add(moderatorUser.getEmailAddress());
    }

    return recipients;
  }
}
