package de.seitenbau.govdata.edit.gui.controller;

import java.io.IOException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletSession;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.Cookie;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import com.liferay.mail.kernel.service.MailServiceUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.mail.kernel.model.MailMessage;
import com.liferay.portal.kernel.util.CookieKeys;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.Ticket;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.TicketLocalServiceUtil;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.PortalUtil;

import de.seitenbau.govdata.odp.registry.ODRClient;
import de.seitenbau.govdata.messages.MessageType;
import de.seitenbau.govdata.navigation.LiferayNavigation;
import de.seitenbau.govdata.odr.ODRTools;
import de.seitenbau.govdata.odr.RegistryClient;

@Slf4j
@Controller
@RequestMapping("VIEW")
class DeleteUserController
{
  private static final String EMBEDDING_PAGE_NAME = "konto-loeschen";

  private static final String PARAM_USER_WAS_DELETED = "userWasDeleted";

  private static final String PARAM_TICKET_KEY = "ticketKey";

  private static final String EMAIL_SUBJECT = "od.user.delete.email.subject";

  private static final String EMAIL_BODY = "od.user.delete.email.body";

  private static final int DAYS_TO_EXPIRE = 3;

  private static final String VIEW_NAME = "delete";

  private static final int TICKET_DELETE_USER = 4;

  private static final String MESSAGE = "message";

  private static final String MESSAGE_TYPE = "messageType";

  private static final String ROLE_METADATA_EDITOR = "Datenbereitsteller";

  private static final String ROLE_GUK = "Geschaeftsstelle";

  private static final String EMAIL_GUK_SUBJECT = "od.user.delete.email_guk.subject";

  private static final String EMAIL_GUK_BODY = "od.user.delete.email_guk.body";

  @Inject
  RegistryClient registryClient;

  @Inject
  private LiferayNavigation liferayNavigation;

  @Value("${admin.email.from.name}")
  private String emailName;

  @Value("${admin.email.from.address}")
  private String emailAddress;

  @RenderMapping
  public String show(
      @RequestParam(name = PARAM_TICKET_KEY, defaultValue = "") String ticketKey,
      @RequestParam(name = PARAM_USER_WAS_DELETED, defaultValue = "") String userWasDeleted,
      RenderRequest request,
      RenderResponse response,
      Model model)
  {
    // first step: If we have deleted the user, just show the confirmation without any further
    // checks
    if (StringUtils.isNotEmpty(userWasDeleted))
    {
      // we don't need to check the "userWasDeleted"-value. There will only be "true" or null.
      model.addAttribute(MESSAGE, "od.user.delete.success");
      model.addAttribute(MESSAGE_TYPE, MessageType.SUCCESS.toString());
      return VIEW_NAME;
    }

    // user arriving from email-link. prepare final step.
    if (StringUtils.isNotEmpty(ticketKey))
    {
      // TODO: temporary workaround
      if (ticketKey.contains(","))
      {
        ticketKey = ticketKey.split(",")[0];
      }

      model.addAttribute("actionUrl", response.createActionURL().toString());
      model.addAttribute(PARAM_TICKET_KEY, ticketKey);
      return VIEW_NAME;
    }

    // make sure we have a logged in user
    User user = null;
    try
    {
      user = PortalUtil.getUser(request);
    }
    catch (PortalException | SystemException e)
    {
      // user is null, everything else will fail. Nothing to do.
    }

    if (user == null)
    {
      return failView(model, "od.user.delete.notloggedin");
    }

    // some action has been done, so show the result.
    if (request.getParameter(MESSAGE) != null)
    {
      model.addAttribute(MESSAGE, request.getParameter(MESSAGE));
      model.addAttribute(MESSAGE_TYPE, request.getParameter(MESSAGE_TYPE));

    }
    else
    {
      // if no parameter is set, show confirmation for starting account deletion
      ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
      model.addAttribute("isMetadataEditor", userIsMetadataEditor(user, themeDisplay.getCompanyId()));
      model.addAttribute("actionUrl", response.createActionURL().toString());
    }

    return VIEW_NAME;
  }

  /**
   * Checks if a user is a "Datenbereitsteller"
   * @param liferayUser
   * @return
   */
  private boolean userIsMetadataEditor(User liferayUser, long companyId)
  {
    try
    {
      // get all users belonging to the role
      Role role = RoleLocalServiceUtil.getRole(companyId, ROLE_METADATA_EDITOR);
      long[] userIds = UserLocalServiceUtil.getRoleUserIds(role.getRoleId());

      // check if our user appears in the list
      return ArrayUtils.contains(userIds, liferayUser.getUserId());
    }
    catch (PortalException | SystemException e)
    {
      log.warn("Error while checking if the user has liferay editor role.", e);
      return false;
    }
  }

  /**
   * Notifies the GuK-Stelle about the deletion of this Datenbereitsteller-User.
   * @param deletedUser
   * @throws SystemException
   * @throws PortalException
   */
  private void notifyMetadataEditorWasDeleted(User deletedUser, ThemeDisplay themeDisplay)
      throws PortalException, SystemException
  {
    // get all users / emails belonging to users of this role
    List<String> recipients = new ArrayList<>();
    Role role = RoleLocalServiceUtil.getRole(themeDisplay.getCompanyId(), ROLE_GUK);
    long[] userIds = UserLocalServiceUtil.getRoleUserIds(role.getRoleId());

    for (long userId : userIds)
    {
      User moderatorUser = UserLocalServiceUtil.getUserById(userId);
      recipients.add(moderatorUser.getEmailAddress());
    }

    // send mail
    String body = MessageFormat.format(
        LanguageUtil.get(themeDisplay.getLocale(), EMAIL_GUK_BODY),
        deletedUser.getFullName(),
        deletedUser.getEmailAddress());

    for (String recipient : recipients)
    { // make sure your mail server is fast enough ;)
      try
      {
        MailMessage msg = new MailMessage();
        msg.setFrom(getEmailSenderAddress());
        msg.setTo(new InternetAddress(recipient));
        msg.setSubject(LanguageUtil.get(themeDisplay.getLocale(), EMAIL_GUK_SUBJECT));
        msg.setBody(body.toString());
        MailServiceUtil.sendEmail(msg);
      }
      catch (AddressException e)
      {
        log.warn("Error while sending notify email.", e);
        throw new PortalException("Email could not be used!");
      }
    }
  }

  private void deleteAccount(User ticketUser, ActionRequest request, ActionResponse response)
      throws PortalException, SystemException
  {
    ODRClient client = registryClient.getInstance();

    // send notification mail for GuK - when the user is da MetadataEditor
    ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
    if (userIsMetadataEditor(ticketUser, themeDisplay.getCompanyId()))
    {
      try
      {
        notifyMetadataEditorWasDeleted(ticketUser, themeDisplay);
      }
      catch (Exception e)
      {
        log.error("Error while sending notification email.", e);
      }
    }

    // delete ckan user
    de.seitenbau.govdata.odp.registry.model.User ckanUser =
        new ODRTools().findCkanUser(ticketUser.getScreenName(), client);
    if (ckanUser != null)
    {
      // rename the ckan user prior to deletion (so the original name can be used again)
      // make it unlikely to already exist
      /*
       * TODO: Temporary disabled, see GOVDATA-2585 String newName = ckanUser.getName() + "-del-" +
       * RandomStringUtils.randomAlphanumeric(6).toLowerCase();
       * if(registryClient.getInstance().renameUser(ckanUser, newName) == null) { throw new
       * PortalException("could not rename user!"); }
       */

      if (!registryClient.getInstance().deleteUser(ckanUser))
      {
        throw new PortalException("could not delete user " + ticketUser.getScreenName().toLowerCase() + "!");
      }
      ;
    }
    else
    {
      log.info("Skipping CKAN-User, no user " + ticketUser.getScreenName().toLowerCase() + " was found.");
    }

    // we must first logout the user, so existing references are removed
    logoutLiferayAccount(request, response);

    // now we can proceed.
    UserLocalServiceUtil.deleteUser(ticketUser);
  }

  private void logoutLiferayAccount(ActionRequest request, ActionResponse response)
  {
    try
    {
      PortletSession session = request.getPortletSession();

      Cookie companyIdCookie = expireCookie(CookieKeys.COMPANY_ID);
      Cookie idCookie = expireCookie(CookieKeys.ID);
      Cookie passwordCookie = expireCookie(CookieKeys.PASSWORD);
      Cookie loginCookie = expireCookie(CookieKeys.LOGIN);
      Cookie rememberMeCookie = expireCookie(CookieKeys.REMEMBER_ME);

      response.addProperty(loginCookie);
      response.addProperty(companyIdCookie);
      response.addProperty(idCookie);
      response.addProperty(passwordCookie);
      response.addProperty(rememberMeCookie);

      // try invalidating the session the good way...
      try
      {
        session.invalidate();
      }
      catch (Exception e)
      {
        log.warn("Error while invalidating session.", e);
      }

      // destroy the jsessionid manually
      response.addProperty(expireCookie(CookieKeys.JSESSIONID));
    }
    catch (Exception e)
    {
      log.warn("Error while trying to logout the liferay user.", e);
    }
  }

  /**
   * Created a new cookie with 0 lifetime, so existing cookies in the browser are expired and
   * deleted.
   * @param name Name of the cookie (key)
   * @param domain Domain which the cookie will be valid
   * @return A new cookie that will trigger expiration of the stored cookie in browser
   */
  private Cookie expireCookie(String name)
  {
    Cookie cookie = new Cookie(name, StringPool.BLANK);
    cookie.setMaxAge(0);
    cookie.setPath(StringPool.SLASH);
    return cookie;
  }

  private String failView(Model model, String reason)
  {
    model.addAttribute(MESSAGE, reason);
    model.addAttribute(MESSAGE_TYPE, MessageType.ERROR.toString());
    return VIEW_NAME;
  }

  @RequestMapping(params = {PARAM_TICKET_KEY})
  public void deleteAction(
      @RequestParam(name = PARAM_TICKET_KEY) String ticketKey,
      ActionRequest request,
      ActionResponse response)
  {
    try
    {
      Ticket ticket = TicketLocalServiceUtil.getTicket(ticketKey);

      if (ticket.isExpired() || ticket.getType() != TICKET_DELETE_USER)
      {
        response.setRenderParameter(MESSAGE, "od.user.delete.ticketexpired");
        response.setRenderParameter(MESSAGE_TYPE, MessageType.ERROR.toString());
        return;
      }

      User ticketUser = UserLocalServiceUtil.fetchUserById(ticket.getClassPK());
      deleteAccount(ticketUser, request, response);

      TicketLocalServiceUtil.deleteTicket(ticket);
      PortletURL redirect = liferayNavigation.createLink(request, EMBEDDING_PAGE_NAME, "gdusermanageportlet");
      redirect.setParameter(PARAM_USER_WAS_DELETED, "true");
      response.sendRedirect(redirect.toString());
    }
    catch (PortalException | SystemException | IOException e)
    {
      log.warn("Error while deleting user.", e);
      response.setRenderParameter(MESSAGE, "od.user.delete.failed");
      response.setRenderParameter(MESSAGE_TYPE, MessageType.ERROR.toString());
    }
  }

  @RequestMapping(params = {"deletionconfirmed"})
  public void sendMailAction(
      @RequestParam(name = "deletionconfirmed") String verificationKey,
      ActionRequest request,
      ActionResponse response)
  {
    ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
    try
    {
      ServiceContext serviceContext = new ServiceContext();
      User user = PortalUtil.getUser(request);

      if (user == null)
      {
        throw new PortalException("not logged in");
      }

      // *** create a ticket so we can verify the email-roundtrip later

      // make the ticket expire in 3 days.
      Date expirationDate = DateUtils.addDays(new Date(), DAYS_TO_EXPIRE);

      Ticket ticket = TicketLocalServiceUtil.addTicket(
          themeDisplay.getCompanyId(),
          User.class.getName(), user.getUserId(),
          TICKET_DELETE_USER, null, expirationDate, serviceContext);

      // *** send the link via email
      String recipient = user.getEmailAddress();

      PortletURL verificationLink =
          liferayNavigation.createLink(request, EMBEDDING_PAGE_NAME, "gdusermanageportlet");
      verificationLink.setParameter(PARAM_TICKET_KEY, ticket.getKey());

      // prepare body
      SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
      String body = MessageFormat.format(
          LanguageUtil.get(themeDisplay.getLocale(), EMAIL_BODY),
          user.getFullName(),
          verificationLink.toString(),
          dateFormat.format(expirationDate));

      // send mail
      MailMessage msg = new MailMessage();
      msg.setFrom(getEmailSenderAddress());
      msg.setTo(new InternetAddress(recipient));
      msg.setSubject(LanguageUtil.get(themeDisplay.getLocale(), EMAIL_SUBJECT));
      msg.setBody(body);
      MailServiceUtil.sendEmail(msg);
    }
    catch (SystemException | PortalException | AddressException e)
    {
      log.warn("Error while sending email with delete verification link.", e);

      response.setRenderParameter(MESSAGE, "od.user.delete.email.sendfailed");
      response.setRenderParameter(MESSAGE_TYPE, MessageType.ERROR.toString());
      return;
    }

    response.setRenderParameter(MESSAGE, "od.user.delete.email.sendsuccess");
    response.setRenderParameter(MESSAGE_TYPE, MessageType.SUCCESS.toString());
  }

  private InternetAddress getEmailSenderAddress() throws AddressException
  {
    InternetAddress from = new InternetAddress(emailName + " <" + emailAddress + ">");
    return from;
  }
}
