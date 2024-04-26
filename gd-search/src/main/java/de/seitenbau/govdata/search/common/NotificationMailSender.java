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

import com.liferay.mail.kernel.model.MailMessage;
import com.liferay.mail.kernel.service.MailServiceUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;

import de.seitenbau.govdata.data.api.ckan.dto.ContactDto;
import de.seitenbau.govdata.data.api.ckan.dto.MetadataDto;
import de.seitenbau.govdata.navigation.GovDataNavigation;
import de.seitenbau.govdata.odp.registry.model.RoleEnumType;
import lombok.extern.slf4j.Slf4j;

/**
 * Sends notifications to the editors of the site and the metadata maintainer.
 * 
 * @author tscheffler
 *
 */
@Slf4j
@Component
public class NotificationMailSender
{
  private static final String CHEFREDAKTEUR = "Chefredakteur";

  private static final String REDAKTEUR = "Redakteur";

  /**
   * Notification event type.
   */
  public enum EventType
  {
    /** new object */
    NEW("new"),
    /** object changed */
    CHANGED("changed");

    private String key;

    EventType(String key)
    {
      this.key = key;
    }

    public String getKey()
    {
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
   * @param companyId id of the liferay company instance
   * @param comment text of the comment
   * @param type new comment or edited comment?
   * @param user acting user
   * @param metadata affected metadata
   */
  public void notifyCommentEvent(long companyId, String comment, EventType type, User user,
      MetadataDto metadata,
      Locale locale)
  {
    // Set ensures every e-mail-adress only receives one email
    Set<String> recipients = new HashSet<>();

    try
    {
      // get all moderators
      recipients.addAll(getEmailsByRolename(companyId, REDAKTEUR));
      recipients.addAll(getEmailsByRolename(companyId, CHEFREDAKTEUR));

      // get the owner of the dataset
      ContactDto contact =
          metadata.getContacts().stream().filter(con -> con.getRole() == RoleEnumType.MAINTAINER)
              .findFirst().orElse(null);
      if (contact != null && StringUtils.isNotEmpty(contact.getEmail()))
      {
        recipients.add(contact.getEmail());
      }

      String subject =
          MessageFormat.format(LanguageUtil.get(locale, "od.datasets.comment.email.subject." + type.getKey()),
              metadata.getType().getDisplayName());

      InternetAddress from = new InternetAddress(emailName + " <" + emailAddress + ">");

      String datasetUrl =
          gdNavigation.createLinkForMetadata("gdsearchdetails", metadata.getName()).toString();
      String body = MessageFormat.format(
          LanguageUtil.get(locale, "od.datasets.comment.email.template." + type.getKey()), comment, // Comment
          user.getFullName() + " <" + user.getDisplayEmailAddress() + ">", // User
          metadata.getTitle(), // Name of the dataset
          datasetUrl); // Url of the dataset

      for (String recipient : recipients)
      { // make sure your mail server is fast enough ;)
        try
        {
          MailMessage msg = new MailMessage();
          msg.setFrom(from);
          msg.setTo(new InternetAddress(recipient));
          msg.setSubject(subject);
          msg.setBody(body.toString());
          MailServiceUtil.sendEmail(msg);
        }
        catch (AddressException e)
        {
          log.warn("Mail could not be sent to «" + recipient + "», error: " + e.getMessage());
        }
      }

    }
    catch (AddressException | SystemException | PortalException e)
    {
      log.warn("Mail could not be sent, error: " + e.getMessage());
    }
  }

  private List<String> getEmailsByRolename(long companyId, String roleName)
      throws PortalException, SystemException
  {

    List<String> recipients = new ArrayList<>();
    Role role = RoleLocalServiceUtil.getRole(companyId, roleName);
    long[] userIds = UserLocalServiceUtil.getRoleUserIds(role.getRoleId());

    for (long userId : userIds)
    {
      User moderatorUser = UserLocalServiceUtil.getUserById(userId);
      recipients.add(moderatorUser.getEmailAddress());
    }

    return recipients;
  }
}
