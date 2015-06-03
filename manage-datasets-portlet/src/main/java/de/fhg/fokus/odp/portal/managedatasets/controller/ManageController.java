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

package de.fhg.fokus.odp.portal.managedatasets.controller;

import java.io.IOException;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.validator.ValidatorException;
import javax.mail.MessagingException;
import javax.portlet.PortletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liferay.faces.bridge.component.HtmlInputFile;
import com.liferay.faces.portal.context.LiferayFacesContext;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.model.Layout;
import com.liferay.portal.model.Role;
import com.liferay.portal.service.RoleLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;

import de.fhg.fokus.odp.portal.managedatasets.utils.ImageGalleryUtils;
import de.fhg.fokus.odp.portal.managedatasets.utils.MailUtils;
import de.fhg.fokus.odp.registry.ODRClient;
import de.fhg.fokus.odp.registry.ckan.impl.LicenceImpl;
import de.fhg.fokus.odp.registry.ckan.json.LicenceBean;
import de.fhg.fokus.odp.registry.model.Application;
import de.fhg.fokus.odp.registry.model.Category;
import de.fhg.fokus.odp.registry.model.Contact;
import de.fhg.fokus.odp.registry.model.GeoGranularityEnumType;
import de.fhg.fokus.odp.registry.model.Licence;
import de.fhg.fokus.odp.registry.model.Metadata;
import de.fhg.fokus.odp.registry.model.MetadataEnumType;
import de.fhg.fokus.odp.registry.model.Resource;
import de.fhg.fokus.odp.registry.model.RoleEnumType;
import de.fhg.fokus.odp.registry.model.SectorEnumType;
import de.fhg.fokus.odp.registry.model.Tag;
import de.fhg.fokus.odp.registry.model.TemporalGranularityEnumType;
import de.fhg.fokus.odp.registry.model.exception.OpenDataRegistryException;
import de.fhg.fokus.odp.registry.model.exception.UnknownRoleException;
import de.fhg.fokus.odp.spi.OpenDataRegistry;

// TODO: Auto-generated Javadoc
/**
 * creation and editing for metadata of datasets, documents and apps. The
 * controller class of view.xhtlm. Used to control all actions regarding the
 * 
 * @author Benjamin Dittwald, Fraunhofer FOKUS
 * @author Majid Salehi, Fraunhofer FOKUS
 * 
 */

@ManagedBean(name = "manageController")
@ViewScoped
public class ManageController implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * The count of the configuration property for the category.max.choose.warn.
	 */
	private static final String PROP_CATEGORY_MAX_CHOOSE_WARN = "category.max.choose.warn";

	/** The odr client. */
	private ODRClient odrClient;

	/** The current user. */
	@ManagedProperty("#{currentUser}")
	private CurrentUser currentUser;

	/** The metadata controller. */
	@ManagedProperty("#{metadataController}")
	private MetadataController metadataController;

	/** The logger. */
	private final Logger LOG = LoggerFactory.getLogger(getClass());

	/** The licences. */
	private List<Licence> licences;

	/** The categories. */
	private List<Category> categories;

	/** The sectors. */
	private List<SectorEnumType> sectors;

	/** The geo granularities. */
	private List<GeoGranularityEnumType> geoGranularities;

	/** The temporal granularity enum types. */
	private List<TemporalGranularityEnumType> temporalGranularityEnumTypes;

	/** The selected categories. */
	private List<String> selectedCategories;
	/** The selected many categories. */
	private List<String> selectedManyCategories;

	/** The selected tags. */
	private List<String> selectedTags;

	/** The caption. */
	private String caption;

	/** The selected licence. */
	private String selectedLicence;

	/** The unknown licence. */
	private boolean unknownLicence = false;

	/** The unknown licence text. */
	private String unknownLicenceText;

	/** The unknown licence url. */
	private String unknownLicenceUrl;

	/** The metadata type. */
	private String metadataType;

	/** The edit mode. */
	private boolean editMode;

	/** The redakteur. */
	private boolean redakteur;

	/** The used dataset uris. */
	private List<String> usedDatasetUris;

	/** The current used dataset uri. */
	private String currentUsedDatasetUri;

	/** The author. */
	private Contact author;

	/** The maintainer. */
	private Contact maintainer;

	/** The distributor. */
	private Contact distributor;

	/** The date pattern. */
	public final static String DATE_PATTERN = "dd.MM.yyyy";

	/** The app image. */
	private transient HtmlInputFile appImage;

	/** The submitquestion. */
	private String submitquestion;

	// private List<String> resource2DeleteList;
	// private List<Resource> allResources;

	/**
	 * Inits the.
	 * 
	 */
	@PostConstruct
	private void init() {
		LiferayFacesContext lfc = LiferayFacesContext.getInstance();
		PortletRequest request = (PortletRequest) lfc.getExternalContext()
				.getRequest();
		ThemeDisplay td = lfc.getThemeDisplay();

		Properties props = new Properties();
		if (metadataController.getMetadata().getType()
				.equals(MetadataEnumType.APPLICATION)) {
			props.setProperty("ckan.authorization.key",
					PropsUtil.get("authenticationKeyApps"));
		} else {
			props.setProperty("ckan.authorization.key",
					PropsUtil.get("authenticationKey"));
		}
		props.setProperty("ckan.url", PropsUtil.get("cKANurl"));

		LOG.debug("odRClient props:" + props.toString());

		odrClient = OpenDataRegistry.getClient();
		odrClient.init(props);

		redakteur = false;
		try {
			for (Role role : RoleLocalServiceUtil.getUserRoles(td.getUserId())) {
				if (role.getName().equals("Redakteur")
						&& RoleLocalServiceUtil.hasUserRole(role.getRoleId(),
								td.getUserId())) {
					redakteur = true;
				}
			}
		} catch (SystemException e) {
			LOG.error(e.getMessage(), e);
		}

		selectedTags = new ArrayList<String>();
		selectedCategories = new ArrayList<String>();
		selectedManyCategories = new ArrayList<String>();
		licences = new ArrayList<Licence>();
		// resource2DeleteList = new ArrayList<String>();

		/*
		 * Sets caption according to create/edit metadata for
		 * dataset/app/document
		 */
		if (metadataController.getMetadata().getTitle() == null
				|| metadataController.getMetadata().getTitle().isEmpty()) {
			/*
			 * Create
			 */

			author = metadataController.getMetadata().newContact(
					RoleEnumType.AUTHOR);
			maintainer = metadataController.getMetadata().newContact(
					RoleEnumType.MAINTAINER);
			distributor = metadataController.getMetadata().newContact(
					RoleEnumType.DISTRIBUTOR);

			editMode = false;

			LicenceBean lb = new LicenceBean();
			Licence emptyLicence = new LicenceImpl(lb);
			metadataController.getMetadata().setLicence(emptyLicence);
			emptyLicence.setTitle(LanguageUtil.get(request.getLocale(),
					"od.licence.select"));

			licences.add(emptyLicence);

			if (metadataController.getMetadata().getType()
					.equals(MetadataEnumType.DATASET)
					|| metadataController.getMetadata().getType()
							.equals(MetadataEnumType.UNKNOWN)) {
				metadataController.getMetadata().newResource();
				caption = LanguageUtil.get(request.getLocale(),
						"od.create.metadata.data");
				submitquestion = LanguageUtil.get(request.getLocale(),
						"od.create.metadata.data.submit");
				metadataType = "dataset";
			} else if (metadataController.getMetadata().getType()
					.equals(MetadataEnumType.APPLICATION)) {
				caption = LanguageUtil.get(request.getLocale(),
						"od.create.metadata.app");
				submitquestion = LanguageUtil.get(request.getLocale(),
						"od.create.metadata.app.submit");
				metadataType = "app";
				usedDatasetUris = new ArrayList<String>();
			} else if (metadataController.getMetadata().getType()
					.equals(MetadataEnumType.DOCUMENT)) {
				metadataController.getMetadata().newResource();
				caption = LanguageUtil.get(request.getLocale(),
						"od.create.metadata.document");
				submitquestion = LanguageUtil.get(request.getLocale(),
						"od.create.metadata.document.submit");
				metadataType = "document";
			}
		} else {
			/*
			 * Edit
			 */

			try {
				// Handle contacts
				for (Contact contact : metadataController.getMetadata()
						.getContacts()) {
					if (contact.getRole().equals(RoleEnumType.AUTHOR)) {
						author = contact;
					} else if (contact.getRole()
							.equals(RoleEnumType.MAINTAINER)) {
						maintainer = contact;
					} else if (contact.getRole().equals(
							RoleEnumType.DISTRIBUTOR)) {
						distributor = contact;
					} else if (contact.getRole().equals(RoleEnumType.PUBLISHER)) {
						LOG.info("Handle metadata contact PUBLISHER ["
								+ contact.getName() + "] now as AUTHOR");
						/* msg 10.10.2014 begin */
						author = contact;
						/* msg 10.10.2014 end */
					}
				}
			} catch (UnknownRoleException une) {
				LOG.error("UnknownRoleException -> Contact role:", une.getMessage());
			} finally {
				metadataController.getMetadata().getContacts().clear();
			}

			if (author == null) {
				author = metadataController.getMetadata().newContact(
						RoleEnumType.AUTHOR);
			}

			if (maintainer == null) {
				maintainer = metadataController.getMetadata().newContact(
						RoleEnumType.MAINTAINER);
			}

			if (distributor == null) {
				distributor = metadataController.getMetadata().newContact(
						RoleEnumType.DISTRIBUTOR);
			}

			editMode = true;
			selectedLicence = metadataController.getMetadata().getLicence()
					.getName();
			unknownLicenceText = metadataController.getMetadata().getLicence()
					.getTitle();
			unknownLicenceUrl = metadataController.getMetadata().getLicence()
					.getUrl();
			for (Tag t : metadataController.getMetadata().getTags()) {
				selectedTags.add(t.getName());
			}
			for (Category c : metadataController.getMetadata().getCategories()) {
				selectedCategories.add(c.getName());
				selectedManyCategories.add(c.getName());
			}

			submitquestion = LanguageUtil.get(request.getLocale(),
					"od.edit.any.data.save.changes");

			if (metadataController.getMetadata().getType()
					.equals(MetadataEnumType.DATASET)
					|| metadataController.getMetadata().getType()
							.equals(MetadataEnumType.UNKNOWN)) {
				caption = LanguageUtil.get(request.getLocale(),
						"od.edit.metadata.data");

				metadataType = "dataset";
			} else if (metadataController.getMetadata().getType()
					.equals(MetadataEnumType.APPLICATION)) {
				caption = LanguageUtil.get(request.getLocale(),
						"od.edit.metadata.app");

				metadataType = "app";
				usedDatasetUris = ((Application) metadataController
						.getMetadata()).getUsedDatasets();
				if (((Application) metadataController.getMetadata())
						.getUsedDatasets().size() < 1) {
				}
			} else if (metadataController.getMetadata().getType()
					.equals(MetadataEnumType.DOCUMENT)) {
				caption = LanguageUtil.get(request.getLocale(),
						"od.edit.metadata.document");

				metadataType = "document";
			}

			unknownLicence = unknownLicence(metadataController.getMetadata()
					.getLicence().getName());
		}

		/*
		 * Fill licences accordingly to the metadata type: dataset, app,
		 * document
		 */

		List<Licence> tempDatenlizenzDeutschlandLicences = new ArrayList<Licence>();
		List<Licence> tempEINGESCHRAENKTLicences = new ArrayList<Licence>();

		if (metadataController.getMetadata().getType()
				.equals(MetadataEnumType.DATASET)
				|| metadataController.getMetadata().getType()
						.equals(MetadataEnumType.UNKNOWN)) {
			for (Licence licence : odrClient.listLicenses()) {
				if (licence.isDomainData()) {
					add2licences(request, tempDatenlizenzDeutschlandLicences,
							tempEINGESCHRAENKTLicences, licence);
				}
			}
		} else if (metadataController.getMetadata().getType()
				.equals(MetadataEnumType.APPLICATION)) {
			for (Licence licence : odrClient.listLicenses()) {
				if (licence.isDomainSoftware()) {
					add2licences(request, tempDatenlizenzDeutschlandLicences,
							tempEINGESCHRAENKTLicences, licence);
				}
			}
		} else if (metadataController.getMetadata().getType()
				.equals(MetadataEnumType.DOCUMENT)) {
			for (Licence licence : odrClient.listLicenses()) {
				if (licence.isDomainContent()) {
					add2licences(request, tempDatenlizenzDeutschlandLicences,
							tempEINGESCHRAENKTLicences, licence);
				}
			}
		}

		Collections.sort(tempDatenlizenzDeutschlandLicences,
				new Comparator<Licence>() {
					public int compare(Licence l1, Licence l2) {
						return -(l2.getTitle().compareTo(l1.getTitle()));
					}
				});

		Collections.sort(tempEINGESCHRAENKTLicences, new Comparator<Licence>() {
			public int compare(Licence l1, Licence l2) {
				return -(l2.getTitle().compareTo(l1.getTitle()));
			}
		});
		Collections.sort(licences, new Comparator<Licence>() {
			public int compare(Licence l1, Licence l2) {
				return -(l2.getTitle().compareTo(l1.getTitle()));
			}
		});

		// Damit alle DatenlizenzDeutschland immer oben in der liste stehen
		if (!licences
				.get(0)
				.getTitle()
				.equals(LanguageUtil.get(request.getLocale(),
						"od.licence.select"))) {
			licences.addAll(0, tempDatenlizenzDeutschlandLicences);
		} else
			licences.addAll(1, tempDatenlizenzDeutschlandLicences);
		// Damit alle EINGESCHRAENKT immer ganz unten in der liste stehen
		licences.addAll(tempEINGESCHRAENKTLicences);

		categories = new ArrayList<Category>();
		List<Category> cats = odrClient.listCategories();
		for (Category c : cats) {
			if ("group".equals(c.getType())) {
				categories.add(c);
			}
		}

		sectors = new ArrayList<SectorEnumType>();
		for (SectorEnumType sector : SectorEnumType.values()) {
			sectors.add(sector);
		}

		geoGranularities = new ArrayList<GeoGranularityEnumType>();
		for (GeoGranularityEnumType geoType : GeoGranularityEnumType.values()) {
			geoGranularities.add(geoType);
		}

		temporalGranularityEnumTypes = new ArrayList<TemporalGranularityEnumType>();
		for (TemporalGranularityEnumType t : TemporalGranularityEnumType
				.values()) {
			temporalGranularityEnumTypes.add(t);
		}
	}

	private void add2licences(PortletRequest request,
			List<Licence> tempDatenlizenzDeutschlandLicences,
			List<Licence> tempEINGESCHRAENKTLicences, Licence licence) {
		if (!licence.isOpen()
				&& !licence.getTitle().equals(
						LanguageUtil.get(request.getLocale(),
								"od.licence.select"))) {
			licence.setTitle(LanguageUtil.get(request.getLocale(),
					"od.restricted").toUpperCase(request.getLocale())
					+ " - " + licence.getTitle());
			tempEINGESCHRAENKTLicences.add(licence);
		} else if (licence.getTitle()
				.matches("(.*)Datenlizenz Deutschland(.*)")) {
			tempDatenlizenzDeutschlandLicences.add(licence);
		} else
			licences.add(licence);
	}

	/**
	 * check if the titel already exist in ckan.
	 * 
	 */
	public void titelValidator(FacesContext context, UIComponent component,
			Object value) throws ValidatorException {
		if (editMode) {
			return;
		}
		String titel = value.toString();
		Properties props = new Properties();
		props.setProperty("ckan.authorization.key",
				PropsUtil.get("authenticationKey"));
		props.setProperty("ckan.url", PropsUtil.get("cKANurl"));
		ODRClient oDRClient = OpenDataRegistry.getClient();
		oDRClient.init(props);
		boolean nameExist = false;
		try {
			String mungeTitle = oDRClient.mungeTitleToName(titel);
			Metadata metadata = oDRClient.getMetadata(
					currentUser.getCurrentUser(), mungeTitle);
			if (metadata != null) {
				LOG.debug("titelValidator:Metadata Titel existiert:" + titel);
				nameExist = true;
			} else {
				LOG.debug("titelValidator:Metadata Titel existiert NICHT:"
						+ titel);
				nameExist = false;
			}
		} catch (Exception e) {
			LOG.debug("titelValidator:Metadata Titel existiert NICHT:" + titel);
			nameExist = false;
		}

		if (nameExist) {
			// FacesMessage message = new
			// FacesMessage(FacesMessage.SEVERITY_ERROR,"Invalid titel","Titel existiert schon:"
			// + titel);
			// context.addMessage(component.getClientId(), message);

			throw new ValidatorException(new FacesMessage(
					FacesMessage.SEVERITY_ERROR, component.getClientId(),
					"Titel existiert schon:" + titel));
		}
	}

	public void changeSelectedCategories(AjaxBehaviorEvent vcEvent) {
		selectedManyCategories = selectedCategories;

		// get some global objects
		LiferayFacesContext lfc = LiferayFacesContext.getInstance();
		// ThemeDisplay tD = lfc.getThemeDisplay();
		PortletRequest request = (PortletRequest) lfc.getExternalContext()
				.getRequest();

		int maxCats;
		String maxCatsStr = PropsUtil.get(PROP_CATEGORY_MAX_CHOOSE_WARN);
		try {
			maxCats = Integer.parseInt(maxCatsStr);
		} catch (NumberFormatException e) {
			maxCats = 3;
		}
		// log.info("changeSelectedCategories:maxCats==" + maxCats);
		String details = LanguageUtil.get(request.getLocale(),
				"od.category.reference.toomuch.warn.details");
		String summary = LanguageUtil.get(request.getLocale(),
				"od.category.reference.toomuch.warn.summary");
		if (selectedManyCategories.size() > maxCats) {
			FacesContext.getCurrentInstance().addMessage(
					"categorymessageskey",
					new FacesMessage(FacesMessage.SEVERITY_WARN, summary,
							details));
		} else {
			Iterator<FacesMessage> msgIterator = FacesContext
					.getCurrentInstance().getMessages();
			while (msgIterator.hasNext()) {
				msgIterator.next();
				msgIterator.remove();
			}
		}

	} // end of changeSelectedCategories

	/**
	 * Submit.
	 * 
	 * @return the string
	 */
	@SuppressWarnings("deprecation")
	public String submit() {

		for (Licence l : licences) {
			if (selectedLicence.equals(l.getName())) {
				if (unknownLicence) {
					l.setTitle(unknownLicenceText);
					l.setUrl(unknownLicenceUrl);
					l.setOther(unknownLicenceText);
				}
				metadataController.getMetadata().setLicence(l);
				break;
			}
		}

		metadataController.getMetadata().getCategories().clear();

		for (Category c : categories) {
			for (String cName : selectedManyCategories) {
				if (cName.equals(c.getName())) {
					metadataController.getMetadata().getCategories().add(c);
				}
			}
		}

		metadataController.getMetadata().getTags().clear();
		for (String t : selectedTags) {
			Tag tag = metadataController.getMetadata().newTag(t);
			metadataController.getMetadata().getTags().add(tag);
		}

		metadataController.getMetadata().getContacts().clear();
		metadataController.getMetadata().getContacts().add(author);
		metadataController.getMetadata().getContacts().add(maintainer);
		metadataController.getMetadata().getContacts().add(distributor);
		Contact publisher = author;
		metadataController.getMetadata().getContacts().add(publisher);

		LiferayFacesContext lfc = LiferayFacesContext.getInstance();
		ThemeDisplay themeDisplay = lfc.getThemeDisplay();

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

			switch (metadataController.getMetadata().getType()) {
			case APPLICATION:
				location += "/apps";

				try {
					metadataController
							.getMetadata()
							.getImages()
							.add(ImageGalleryUtils.uploadImage(
									appImage.getUploadedFile()).toString());
				} catch (URISyntaxException e) {
					// TODO: better errorhandling...
					LOG.error("App image URI. ", e.getMessage());
				} catch (IOException e) {
					// TODO: better errorhandling...
					LOG.error("Error on storing image: {}",
							appImage.getUploadedFile(), e.getMessage());
				} catch (NullPointerException e) {
					// TODO: better errorhandling...
					LOG.error("Error on storing image: {}",
							"[no image selected]", e.getMessage());
				}

				((Application) metadataController.getMetadata())
						.getUsedDatasets().clear();

				/* Check and sort out empty URIs */
				for (String uri : usedDatasetUris) {
					if (!uri.isEmpty()) {
						((Application) metadataController.getMetadata())
								.getUsedDatasets().add(uri);
					}
				}

				break;
			case DOCUMENT:
				location += "/dokumente";
				break;
			case UNKNOWN:
				location += "/suchen";
				break;
			default:
				location += "/daten";
			}

			if (editMode
					|| !metadataController.getMetadata().getType()
							.equals(MetadataEnumType.APPLICATION)) {
				// title can be changed correct location is to get the name of
				// Metadata @msg
				String name = metadataController.getMetadata().getName();
				if (name == null)
					name = metadataController.getMetadata().getTitle();
				location += "/-/details/" + odrClient.mungeTitleToName(name);
			}

		} catch (PortalException e) {
			LOG.error(e.getMessage());
			LOG.debug("Error on metadata create / edit submit.",
					e.getStackTrace());
		} catch (SystemException e) {
			LOG.error(e.getMessage());
			LOG.debug("Error on metadata create / edit submit.",
					e.getStackTrace());
		}

		try {
			try {
				odrClient.persistMetadata(currentUser.getCurrentUser(),
						metadataController.getMetadata());
			} catch (OpenDataRegistryException e) {
				// TODO: better errorhandling...
				LOG.info(
						"OpenDataRegistryException:Error on creating metadata: {}",
						metadataController.getMetadata().getName(),
						e.getMessage());
			} catch (Exception e) {
				// TODO: better errorhandling...
				LOG.info("Exception on creating metadata: {}",
						metadataController.getMetadata().getName(),
						e.getMessage());
				e.printStackTrace();
			} finally {
				// wird in jedem Fall ausgeführt
				metadataController.getMetadata().setResoucesModified(false);
			}

			if (!editMode
					&& metadataController.getMetadata().getType()
							.equals(MetadataEnumType.APPLICATION)) {

				Properties props = new Properties();
				props.setProperty("ckan.authorization.key",
						PropsUtil.get("authenticationKey"));
				props.setProperty("ckan.url", PropsUtil.get("cKANurl"));
				ODRClient appOdrClient = OpenDataRegistry.getClient();
				appOdrClient.init(props);
				Metadata newApp;
				try {
					newApp = appOdrClient.getMetadata(currentUser
							.getCurrentUser(), metadataController.getMetadata()
							.getName());

					newApp.setState("deleted");
					odrClient.persistMetadata(currentUser.getCurrentUser(),
							newApp);
					MailUtils.sendMail(newApp);
				} catch (OpenDataRegistryException e) {
					// TODO: better errorhandling...
					LOG.error(
							"Error on updating state of metadata to deleted: {}",
							metadataController.getMetadata().getName(),
							e.getMessage());
				} catch (PortalException e) {
					LOG.error("Error creating app: {}\n{}", metadataController
							.getMetadata().getName(), e.getMessage());
				} catch (SystemException e) {
					LOG.error("Error creating app: {}\n{}", metadataController
							.getMetadata().getName(), e.getMessage());
				} catch (MessagingException e) {
					LOG.error("Error creating app: {}\n{}", metadataController
							.getMetadata().getName(), e.getMessage());
				}
			}

			FacesContext.getCurrentInstance().getExternalContext()
					.redirect(location);
		} catch (IOException e) {
			LOG.error(e.getMessage());
			LOG.debug("Error ob metadata create / edit submit",
					e.getStackTrace());
		}

		return null;
	}

	/**
	 * Unknown licence.
	 * 
	 * @param name
	 *            the name
	 * @return true, if successful
	 */
	private boolean unknownLicence(String name) {
		boolean result;

		if (name.equals("other-open") || name.equals("other-closed")
				|| name.equals("app_commercial") || name.equals("app_freeware")
				|| name.equals("app_opensource")) {
			result = true;
		} else {
			result = false;
		}

		return result;
	}

	/**
	 * Terms of use value change listener.
	 * 
	 * @param e
	 *            the e
	 */
	public void termsOfUseValueChangeListener(ValueChangeEvent e) {

		String newLicence = (String) e.getNewValue();

		unknownLicence = unknownLicence(newLicence);
	}

	/**
	 * An autocomplete method for the resources file formats. Uses the
	 * OD-Registrie's autoSuggest method.
	 * 
	 * @param fragment
	 *            The fragment which should be completed
	 * @return A List of autocomplete suggestions regarding the given fragment.
	 */
	public List<String> formatSuggest(String fragment) {
		List<String> results = new ArrayList<String>();

		List<String> suggestions = odrClient.autoSuggestFormats(fragment);
		for (String suggestion : suggestions) {
			results.add(suggestion);
		}

		return results;
	}

	/**
	 * An autocomplete method for the app used datasets. Uses the OD-Registrie's
	 * autoSuggest method.
	 * 
	 * @param fragment
	 *            the fragment
	 * @return the list
	 */
	public List<String> metadataDatasetSuggest(String fragment) {
		List<String> results = new ArrayList<String>();

		List<String> suggestions = odrClient.autoSuggestMetadata(fragment);
		for (String suggestion : suggestions) {
			results.add(suggestion);
		}

		return results;
	}

	/**
	 * Adds the dataset uri.
	 * 
	 * @return the string
	 */
	public String addDatasetURI() {
		usedDatasetUris.add(currentUsedDatasetUri);
		currentUsedDatasetUri = "";
		return null;
	}

	/**
	 * Removes the used dataset.
	 * 
	 * @param uri
	 *            the uri
	 * @return the string
	 */
	public String removeUsedDataset(String uri) {
		usedDatasetUris.remove(uri);
		return null;
	}

	/**
	 * Adds a new empty resource to the resource list. This can be used to
	 * create a new resource for the metadataController.getMetadata().
	 * 
	 * @return null
	 */
	public String addResource() {
		metadataController.getMetadata().newResource();
		return null;
	}

	/**
	 * Deletes a specific resource from the resource list.
	 * 
	 * @param r
	 *            The resource which has to be deleted
	 * @return null
	 */
	public String removeResource(Resource r) {
		// metadataController.getMetadata().getResources().remove(r);
		// msg 14.04.2014
		// use removeResource to remove resources
		metadataController.getMetadata().removeResource(r);
		return null;
	}

	/**
	 * Gets the categories.
	 * 
	 * @return the categories
	 */
	public List<Category> getCategories() {
		return categories;
	}

	/**
	 * Sets the categories.
	 * 
	 * @param categories
	 *            the new categories
	 */
	public void setCategories(List<Category> categories) {
		this.categories = categories;
	}

	/**
	 * Gets the licenses.
	 * 
	 * @return the licenses
	 */
	public List<Licence> getLicences() {
		return licences;
	}

	/**
	 * Sets the licenses.
	 * 
	 * @param licences
	 *            the new licences
	 */
	public void setLicences(List<Licence> licences) {
		this.licences = licences;
	}

	/**
	 * Gets the caption.
	 * 
	 * @return the caption
	 */
	public String getCaption() {
		return caption;
	}

	/**
	 * Sets the caption.
	 * 
	 * @param caption
	 *            the new caption
	 */
	public void setCaption(String caption) {
		this.caption = caption;
	}

	/**
	 * Checks if is unknown licence.
	 * 
	 * @return true, if is unknown licence
	 */
	public boolean isUnknownLicence() {
		return unknownLicence;
	}

	/**
	 * Sets the unknown licence.
	 * 
	 * @param unknownLicence
	 *            the new unknown licence
	 */
	public void setUnknownLicence(boolean unknownLicence) {
		this.unknownLicence = unknownLicence;
	}

	/**
	 * Gets the geo granularities.
	 * 
	 * @return the geo granularities
	 */
	public List<GeoGranularityEnumType> getGeoGranularities() {
		return geoGranularities;
	}

	/**
	 * Sets the geo granularities.
	 * 
	 * @param geoGranularities
	 *            the new geo granularities
	 */
	public void setGeoGranularities(
			List<GeoGranularityEnumType> geoGranularities) {
		this.geoGranularities = geoGranularities;
	}

	/**
	 * Gets the selected licence.
	 * 
	 * @return the selected licence
	 */
	public String getSelectedLicence() {
		return selectedLicence;
	}

	/**
	 * Sets the selected licence.
	 * 
	 * @param selectedLicence
	 *            the new selected licence
	 */
	public void setSelectedLicence(String selectedLicence) {
		this.selectedLicence = selectedLicence;
	}

	/**
	 * Gets the selected categories.
	 * 
	 * @return the selected categories
	 */
	public List<String> getSelectedCategories() {
		return selectedCategories;
	}

	/**
	 * Sets the selected categories.
	 * 
	 * @param selectedCategories
	 *            the new selected categories
	 */
	public void setSelectedCategories(List<String> selectedCategories) {
		this.selectedCategories = selectedCategories;
	}

	/**
	 * Gets the selected tags.
	 * 
	 * @return the selected tags
	 */
	public List<String> getSelectedTags() {
		return selectedTags;
	}

	/**
	 * Sets the selected tags.
	 * 
	 * @param selectedTags
	 *            the new selected tags
	 */
	public void setSelectedTags(List<String> selectedTags) {
		this.selectedTags = selectedTags;
	}

	/**
	 * Gets the hidden.
	 * 
	 * @return the hidden
	 */
	public boolean getHidden() {
		return "deleted".equals(metadataController.getMetadata().getState());
	}

	/**
	 * Sets the hidden.
	 * 
	 * @param hidden
	 *            the new hidden
	 */
	public void setHidden(boolean hidden) {
		metadataController.getMetadata()
				.setState(hidden ? "deleted" : "active");
	}

	/**
	 * Gets the metadata type.
	 * 
	 * @return the metadata type
	 */
	public String getMetadataType() {
		return metadataType;
	}

	/**
	 * Sets the metadata type.
	 * 
	 * @param metadataType
	 *            the new metadata type
	 */
	public void setMetadataType(String metadataType) {
		this.metadataType = metadataType;
	}

	/**
	 * Gets the temporal granularity enum types.
	 * 
	 * @return the temporal granularity enum types
	 */
	public List<TemporalGranularityEnumType> getTemporalGranularityEnumTypes() {
		return temporalGranularityEnumTypes;
	}

	/**
	 * Sets the temporal granularity enum types.
	 * 
	 * @param temporalGranularityEnumTypes
	 *            the new temporal granularity enum types
	 */
	public void setTemporalGranularityEnumTypes(
			List<TemporalGranularityEnumType> temporalGranularityEnumTypes) {
		this.temporalGranularityEnumTypes = temporalGranularityEnumTypes;
	}

	/**
	 * Gets the unknown licence text.
	 * 
	 * @return the unknown licence text
	 */
	public String getUnknownLicenceText() {
		return unknownLicenceText;
	}

	/**
	 * Sets the unknown licence text.
	 * 
	 * @param unknownLicenceText
	 *            the new unknown licence text
	 */
	public void setUnknownLicenceText(String unknownLicenceText) {
		this.unknownLicenceText = unknownLicenceText;
	}

	/**
	 * Checks if is edits the mode.
	 * 
	 * @return true, if is edits the mode
	 */
	public boolean isEditMode() {
		return editMode;
	}

	/**
	 * Sets the edits the mode.
	 * 
	 * @param editMode
	 *            the new edits the mode
	 */
	public void setEditMode(boolean editMode) {
		this.editMode = editMode;
	}

	/**
	 * Checks if is redakteur.
	 * 
	 * @return true, if is redakteur
	 */
	public boolean isRedakteur() {
		return redakteur;
	}

	/**
	 * Sets the redakteur.
	 * 
	 * @param redakteur
	 *            the new redakteur
	 */
	public void setRedakteur(boolean redakteur) {
		this.redakteur = redakteur;
	}

	/**
	 * Gets the sectors.
	 * 
	 * @return the sectors
	 */
	public List<SectorEnumType> getSectors() {
		return sectors;
	}

	/**
	 * Sets the sectors.
	 * 
	 * @param sectors
	 *            the new sectors
	 */
	public void setSectors(List<SectorEnumType> sectors) {
		this.sectors = sectors;
	}

	/**
	 * Gets the used dataset uris.
	 * 
	 * @return the used dataset uris
	 */
	public List<String> getUsedDatasetUris() {
		return usedDatasetUris;
	}

	/**
	 * Sets the used dataset uris.
	 * 
	 * @param usedDatasetUris
	 *            the new used dataset uris
	 */
	public void setUsedDatasetUris(List<String> usedDatasetUris) {
		this.usedDatasetUris = usedDatasetUris;
	}

	/**
	 * Gets the current used dataset uri.
	 * 
	 * @return the current used dataset uri
	 */
	public String getCurrentUsedDatasetUri() {
		return currentUsedDatasetUri;
	}

	/**
	 * Sets the current used dataset uri.
	 * 
	 * @param currentUsedDatasetUri
	 *            the new current used dataset uri
	 */
	public void setCurrentUsedDatasetUri(String currentUsedDatasetUri) {
		this.currentUsedDatasetUri = currentUsedDatasetUri;
	}

	/**
	 * Gets the author.
	 * 
	 * @return the author
	 */
	public Contact getAuthor() {
		return author;
	}

	/**
	 * Sets the author.
	 * 
	 * @param author
	 *            the new author
	 */
	public void setAuthor(Contact author) {
		this.author = author;
	}

	/**
	 * Gets the maintainer.
	 * 
	 * @return the maintainer
	 */
	public Contact getMaintainer() {
		return maintainer;
	}

	/**
	 * Sets the maintainer.
	 * 
	 * @param maintainer
	 *            the new maintainer
	 */
	public void setMaintainer(Contact maintainer) {
		this.maintainer = maintainer;
	}

	/**
	 * Gets the distributor.
	 * 
	 * @return the distributor
	 */
	public Contact getDistributor() {
		return distributor;
	}

	/**
	 * Sets the distributor.
	 * 
	 * @param distributor
	 *            the new distributor
	 */
	public void setDistributor(Contact distributor) {
		this.distributor = distributor;
	}

	/**
	 * Gets the date pattern.
	 * 
	 * @return the date pattern
	 */
	public String getDATE_PATTERN() {
		return DATE_PATTERN;
	}

	/**
	 * Sets the current user.
	 * 
	 * @param currentUser
	 *            the new current user
	 */
	public void setCurrentUser(CurrentUser currentUser) {
		this.currentUser = currentUser;
	}

	/**
	 * Sets the metadata controller.
	 * 
	 * @param metadataController
	 *            the new metadata controller
	 */
	public void setMetadataController(MetadataController metadataController) {
		this.metadataController = metadataController;
	}

	/**
	 * Gets the app image.
	 * 
	 * @return the app image
	 */
	public HtmlInputFile getAppImage() {
		return appImage;
	}

	/**
	 * Sets the app image.
	 * 
	 * @param appImage
	 *            the new app image
	 */
	public void setAppImage(HtmlInputFile appImage) {
		this.appImage = appImage;
	}

	/**
	 * Gets the submitquestion.
	 * 
	 * @return the submitquestion
	 */
	public String getSubmitquestion() {
		return submitquestion;
	}

	/**
	 * Sets the submitquestion.
	 * 
	 * @param submitquestion
	 *            the new submitquestion
	 */
	public void setSubmitquestion(String submitquestion) {
		this.submitquestion = submitquestion;
	}

	/**
	 * Gets the unknown licence url.
	 * 
	 * @return the unknown licence url
	 */
	public String getUnknownLicenceUrl() {
		return unknownLicenceUrl;
	}

	/**
	 * Sets the unknown licence url.
	 * 
	 * @param unknownLicenceUrl
	 *            the new unknown licence url
	 */
	public void setUnknownLicenceUrl(String unknownLicenceUrl) {
		this.unknownLicenceUrl = unknownLicenceUrl;
	}

}