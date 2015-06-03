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

/**
 *
 */
package de.fhg.fokus.odp.portal.datasets;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.portlet.ActionResponse;
import javax.portlet.PortletRequest;
import javax.xml.namespace.QName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liferay.counter.service.CounterLocalServiceUtil;
import com.liferay.faces.portal.context.LiferayFacesContext;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.model.Layout;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;

import de.fhg.fokus.odp.entities.model.MetadataComment;
import de.fhg.fokus.odp.entities.service.MetadataCommentLocalServiceUtil;
import de.fhg.fokus.odp.registry.model.Application;
import de.fhg.fokus.odp.registry.model.Contact;
import de.fhg.fokus.odp.registry.model.Metadata;
import de.fhg.fokus.odp.registry.model.Resource;
import de.fhg.fokus.odp.registry.model.Tag;
import de.fhg.fokus.odp.registry.model.exception.OpenDataRegistryException;

// TODO: Auto-generated Javadoc
/**
 * The Class SelectedMetadata.
 * 
 * @author sim, msg
 */
@ManagedBean
@ViewScoped
public class SelectedMetadata implements Serializable {

	/**
	 * The Constant serialVersionUID.
	 */
	private static final long serialVersionUID = -4683938383470850824L;
	/**
	 * The metadata.
	 */
	private Metadata metadata;
	/**
	 * The registry client.
	 */
	@ManagedProperty("#{registryClient}")
	private RegistryClient registryClient;
	/**
	 * The current user.
	 */
	@ManagedProperty("#{currentUser}")
	private CurrentUser currentUser;
	@ManagedProperty("#{feedback}")
	private Feedback feedback;
	/**
	 * The comments.
	 */
	private List<MetadataComment> comments;
	/**
	 * The new comment.
	 */
	private String newComment;

	private CurrentMetadataContact contact;
	/**
	 * The logger.
	 */
	private final Logger logger = LoggerFactory.getLogger(getClass());
	/**
	 * The rss url.
	 */
	private String rssUrl = "none";
	/**
	 * The actual rating.
	 */
	private int actualRating = 0;

	private String keywords = "";

	/**
	 * gets the rssUrl.
	 * 
	 * @return te rssUrl
	 */
	public String getRssUrl() {
		if (metadata == null)/* msg 15.09.2014 */
			return "";
		ThemeDisplay themeDisplay = LiferayFacesContext.getInstance()
				.getThemeDisplay();
		rssUrl = themeDisplay.getURLPortal()
				+ "/rss-servlet/webresources/rssservice?";
		rssUrl += "name=" + metadata.getName();
		return rssUrl;
	}

	/**
	 * Sets the rss url.
	 * 
	 * @param rssUrl
	 *            the rssUrl
	 */
	public void setRssUrl(String rssUrl) {
		this.rssUrl = rssUrl;
	}

	/**
	 * Inits the.
	 * 
	 */
	@PostConstruct
	public void init() {

		if (metadata == null) {
			String name = FacesContext.getCurrentInstance()
					.getExternalContext().getRequestParameterMap()
					.get("metadata");
			if (name != null) {				
				try {
					metadata = registryClient.getInstance().getMetadata(null,
							name);
					// registryClient.getInstance().loadRating(metadata);
					this.contact = new CurrentMetadataContact(metadata);
				} catch (OpenDataRegistryException e) {
					logger.error("Metadata detail page", e.getMessage());
				}
			}
		}

		if (metadata != null) {			
			LiferayFacesContext.getInstance().getThemeDisplay().getLayout()
					.setTitle(metadata.getTitle());
			try {
				comments = MetadataCommentLocalServiceUtil
						.findBymetadataName(metadata.getName());
			} catch (SystemException e) {
				logger.error(
						"Loading comments of metadata " + metadata.getName(), e);
			}
		} else {
			logger.info("selectedmetadata not found!");
			String name = FacesContext.getCurrentInstance()
					.getExternalContext().getRequestParameterMap()
					.get("metadata");
			// get some global objects
			LiferayFacesContext lfc = LiferayFacesContext.getInstance();
			// ThemeDisplay tD = lfc.getThemeDisplay();
			PortletRequest request = (PortletRequest) lfc.getExternalContext()
					.getRequest();
			String details = LanguageUtil.get(request.getLocale(),
					"od.dataset.not.exsist.error.details");
			String summary = LanguageUtil.get(request.getLocale(),
					"od.dataset.not.exsist.error.summary");
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, summary + ":"
							+ name, details));
		}
	}

	/**
	 * Send mail.
	 * 
	 * @throws MessagingException
	 *             the messaging exception
	 * @throws SystemException
	 * @throws PortalException
	 */
	private void sendMail() throws MessagingException, PortalException,
			SystemException {

		// get some global objects
		LiferayFacesContext lfc = LiferayFacesContext.getInstance();
		ThemeDisplay tD = lfc.getThemeDisplay();
		PortletRequest request = (PortletRequest) lfc.getExternalContext()
				.getRequest();

		String metadataUrl = LiferayFacesContext.getInstance()
				.getThemeDisplay().getLayout().getFriendlyURL();
		metadataUrl += "/-/details/" + metadata.getName();

		// get the email subject and body
		String subject = LanguageUtil.get(request.getLocale(),
				"od.datasets.comment.email.subject")
				+ " "
				+ metadata.getType().getDisplayName();
		String body = LanguageUtil.get(request.getLocale(),
				"od.datasets.comment.email.body.comment") + "\t" + newComment;

		body += "\n\n"
				+ LanguageUtil.get(request.getLocale(),
						"od.datasets.comment.email.body.metadata") + "\t"
				+ metadata.getTitle() + "\n\t" + metadata.getName() + "\n\t"
				+ portalUrl(metadataUrl);

		body += "\n\n"
				+ LanguageUtil.get(request.getLocale(),
						"od.datasets.comment.email.body.user") + "\t"
				+ UserLocalServiceUtil.getUser(tD.getUserId()).getLogin();

		// get the from address
		String fromStr = PortalUtil.getPortalProperties().getProperty(
				"admin.email.from.address");
		if (fromStr == null || fromStr.matches("^\\s*$")) {
			try {
				fromStr = PrefsPropsUtil.getString(tD.getCompanyId(),
						"admin.email.from.address");
			} catch (SystemException e) {
				logger.error("sendMail:SystemException:" + e.getMessage());
				return;
			}
		}

		// check if an address can be send out
		if (fromStr == null || fromStr.matches("^\\s*%")) {
			logger.error("No from email address found");
			return;
		}
		InternetAddress from = new InternetAddress(fromStr);

		// prepare the properties and session
		Properties props = new Properties();

		String smtpHost = PropsUtil.get("mail.session.mail.smtp.host");
		if (smtpHost == null || smtpHost.matches("^\\s*$")) {
			logger.error("No smtp host specified");
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

		// get all the email addresses
		List<Contact> cLs = metadata.getContacts();
		for (int i = 0; i < cLs.size(); i++) {
			try {
				logger.debug(cLs.get(i).getEmail());
				msg.addRecipient(Message.RecipientType.TO, new InternetAddress(
						cLs.get(i).getEmail()));
			} catch (AddressException e) {
				logger.warn(e.getMessage() + ": " + cLs.get(i).getEmail());
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}

		// add BCC for observing comments
		msg.addRecipient(Message.RecipientType.BCC, new InternetAddress(
				PropsUtil.get("mail.comment.cc.address")));

		msg.setContent(body, "text/plain");
		msg.setFrom(from);
		msg.setSubject(subject);
		Transport.send(msg);
	}

	/**
	 * Submit comment.
	 * 
	 * @return the string
	 * @throws MessagingException
	 *             the messaging exception
	 */
	public void addComment(ActionEvent event) throws MessagingException {

		LiferayFacesContext lfc = LiferayFacesContext.getInstance();
		ThemeDisplay tD = lfc.getThemeDisplay();

		if (!newComment.isEmpty()) {

			try {
				MetadataComment comment = MetadataCommentLocalServiceUtil
						.createMetadataComment(CounterLocalServiceUtil
								.increment(MetadataComment.class.getName()));

				comment.setText(newComment);
				comment.setUserLiferayId(tD.getUserId());
				comment.setMetadataName(metadata.getName());
				comment.setCreated(new Date());

				MetadataCommentLocalServiceUtil.addMetadataComment(comment);

				comments = MetadataCommentLocalServiceUtil
						.findBymetadataName(metadata.getName());
				feedback.setMetadataCommentCreated(true);
				sendMail();

			} catch (SystemException ex) {
				logger.error(ex.getMessage());
			} catch (PortalException ex) {
				logger.error(ex.getMessage());
			}

			newComment = "";
		}

	}

	/**
	 * Gets the comment author.
	 * 
	 * @param comment
	 *            the comment
	 * @return the comment author
	 * @throws PortalException
	 *             the portal exception
	 * @throws SystemException
	 *             the system exception
	 */
	public String getCommentAuthor(MetadataComment comment)
			throws PortalException, SystemException {
		return UserLocalServiceUtil.getUser(comment.getUserLiferayId())
				.getScreenName();
	}

	/**
	 * Removes the comment.
	 * 
	 * @param comment
	 *            the comment
	 * @return the string
	 * @throws PortalException
	 *             the portal exception
	 * @throws SystemException
	 *             the system exception
	 */
	public String removeComment(MetadataComment comment)
			throws PortalException, SystemException {
		MetadataCommentLocalServiceUtil.deleteMetadataComment(comment.get_id());
		return show();
	}

	/**
	 * Gets the metadata.
	 * 
	 * @return the metadata
	 */
	public Metadata getMetadata() {
		return metadata;
	}

	/**
	 * Sets the metadata.
	 * 
	 * @param metadata
	 *            the metadata
	 */
	public void setMetadata(Metadata metadata) {
		this.metadata = metadata;
	}

	/**
	 * Gets the title.
	 * 
	 * @return the title
	 */
	public String getTitle() {
		return metadata == null ? null : metadata.getTitle();
	}

	/**
	 * Gets the notes.
	 * 
	 * @return the notes
	 */
	public String getNotes() {
		return metadata == null ? null : metadata.getNotes();
	}

	/**
	 * Gets the url.
	 * 
	 * @return the url
	 */
	public String getUrl() {
		return metadata == null ? null : metadata.getUrl();
	}

	/**
	 * Gets the resources.
	 * 
	 * @return the resources
	 */
	public List<Resource> getResources() {
		return metadata == null ? null : metadata.getResources();
	}

	/**
	 * Sets the registry client.
	 * 
	 * @param registryClient
	 *            the registryClient to set
	 */
	public void setRegistryClient(RegistryClient registryClient) {
		this.registryClient = registryClient;
	}

	/**
	 * Sets the current user.
	 * 
	 * @param currentUser
	 *            the current user
	 */
	public void setCurrentUser(CurrentUser currentUser) {
		this.currentUser = currentUser;
	}

	/**
	 * Gets the comments.
	 * 
	 * @return the comments
	 */
	public List<MetadataComment> getComments() {
		return comments;
	}

	/**
	 * Sets the comments.
	 * 
	 * @param comments
	 *            the comments
	 */
	public void setComments(List<MetadataComment> comments) {
		this.comments = comments;
	}

	/**
	 * Show.
	 * 
	 * @return the string
	 */
	public String show() {
		String location = LiferayFacesContext.getInstance().getThemeDisplay()
				.getLayout().getFriendlyURL();
		location += "/-/details/" + metadata.getName();
		link(location);
		return "";
	}

	/**
	 * Edits the.
	 * 
	 * @return the string
	 */
	public String edit() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		Object responseObject = facesContext.getExternalContext().getResponse();
		if (responseObject != null && responseObject instanceof ActionResponse) {
			ActionResponse actionResponse = (ActionResponse) responseObject;
			actionResponse.setEvent(new QName(
					"http://fokus.fraunhofer.de/odplatform", "metadata"),
					metadata);
		}

		link("/bearbeiten");
		return "";
	}

	private String portalUrl(String page) {
		ThemeDisplay themeDisplay = LiferayFacesContext.getInstance()
				.getThemeDisplay();
		String location = themeDisplay.getPortalURL();

		Layout layout = themeDisplay.getLayout();
		try {
			if (layout.isPublicLayout()) {
				location += themeDisplay.getPathFriendlyURLPublic();
			}
			if (layout.hasScopeGroup()) {
				location += layout.getScopeGroup().getFriendlyURL();
			} else {
				location += layout.getGroup().getFriendlyURL();
			}

			location += page;
		} catch (PortalException e) {
			e.printStackTrace();
		} catch (SystemException e) {
			e.printStackTrace();
		}

		return location;
	}

	/**
	 * Link.
	 * 
	 * @param page
	 *            the page
	 */
	private void link(String page) {

		String location = portalUrl(page);

		try {
			FacesContext.getCurrentInstance().getExternalContext()
					.redirect(location);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gets the new comment.
	 * 
	 * @return the new comment
	 */
	public String getNewComment() {
		return newComment;
	}

	/**
	 * Sets the new comment.
	 * 
	 * @param newComment
	 *            the new comment
	 */
	public void setNewComment(String newComment) {
		this.newComment = newComment;
	}

	/**
	 * Gets the rating. Number of 1s represents the average rating.
	 * 
	 * @return the rating
	 */
	public List<Integer> getRatingAsList() {
		// registryClient.getInstance().loadRating(this.metadata);
		List<Integer> resultList = new ArrayList<Integer>();
		if (metadata == null)/* msg 16.09.2014 */
			return resultList;
		double averageRating = this.metadata.getAverageRating();
		logger.debug("getRating() -> " + averageRating);
		int ratingListCount = (int) averageRating;

		for (int i = 0; i < ratingListCount; i++) {
			resultList.add(1);
		}

		for (int i = ratingListCount; i < 5; i++) {
			resultList.add(0);
		}
		return resultList;
	}

	/**
	 * Gets the rating.
	 * 
	 * @return the rating
	 */
	public int getRating() {
		// registryClient.getInstance().loadRating(this.metadata);
		double averageRating = this.metadata.getAverageRating();
		logger.debug("getRating() -> " + averageRating);
		return (int) averageRating;
	}

	/**
	 * Gets the rating count.
	 * 
	 * @return the rating count
	 */
	public int getRatingCount() {
		// registryClient.getInstance().loadRating(this.metadata);
		int ratingCount = this.metadata.getRatingCount();
		logger.debug("getRatingCount() -> " + ratingCount);
		return ratingCount;
	}

	/**
	 * Rate.
	 * 
	 * @see http 
	 *      ://docs.ckan.org/en/latest/apiv3.html?highlight=rating#ckan.logic
	 *      .action.create.rating_create
	 */
	public void rate() {
		logger.debug("setRating() -> " + actualRating + " - "
				+ currentUser.getUser().getDisplayName() + " - "
				+ metadata.getName());
		registryClient.getInstance().rateMetadata(currentUser.getUser(),
				metadata.getName(), actualRating);
	}

	/**
	 * Gets the actual rating.
	 * 
	 * @return the actualRating
	 */
	public int getActualRating() {
		return actualRating;
	}

	/**
	 * Sets the actual rating.
	 * 
	 * @param actualRating
	 *            the actualRating to set
	 */
	public void setActualRating(int actualRating) {
		this.actualRating = actualRating;
	}

	public Application asApplication() {
		return (Application) metadata;
	}

	/**
	 * @return the feedback
	 */
	public Feedback getFeedback() {
		return feedback;
	}

	/**
	 * @param feedback
	 *            the feedback to set
	 */
	public void setFeedback(Feedback feedback) {
		this.feedback = feedback;
	}

	/**
	 * 
	 * @return The URL to CKAN API of this metadata
	 */
	public String getMetadataCKANUrl() {
		return PropsUtil.get("cKANurlFriendly") + "api/rest/dataset/"
				+ metadata.getName();
	}

	public String getKeywords() {
		if (metadata == null)/* msg 15.09.2014 */
			return "";
		String keywordsString = "";

		List<Tag> tags = metadata.getTags();
		for (Iterator<Tag> iterator = tags.iterator(); iterator.hasNext();) {
			Tag tag = (Tag) iterator.next();
			keywordsString = keywordsString + tag.getName();
			keywordsString = keywordsString + ", ";
		}
		setKeywords(keywordsString);

		return this.keywords;

	}

	public CurrentMetadataContact getContact() {
		return contact;
	}

	public void setContact(CurrentMetadataContact contact) {
		this.contact = contact;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}
}
