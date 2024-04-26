package de.seitenbau.govdata.edit.mapper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import de.seitenbau.govdata.common.json.DateUtil;
import de.seitenbau.govdata.constants.CommonConstants;
import de.seitenbau.govdata.db.api.model.Showcase;
import de.seitenbau.govdata.db.api.model.ShowcaseCategory;
import de.seitenbau.govdata.db.api.model.ShowcaseContact;
import de.seitenbau.govdata.db.api.model.ShowcaseImage;
import de.seitenbau.govdata.db.api.model.ShowcaseKeyword;
import de.seitenbau.govdata.db.api.model.ShowcaseLink;
import de.seitenbau.govdata.db.api.model.ShowcasePlatform;
import de.seitenbau.govdata.db.api.model.ShowcaseType;
import de.seitenbau.govdata.db.api.model.ShowcaseUsedDataset;
import de.seitenbau.govdata.edit.model.Category;
import de.seitenbau.govdata.edit.model.Contact;
import de.seitenbau.govdata.edit.model.ContactAddress;
import de.seitenbau.govdata.edit.model.Image;
import de.seitenbau.govdata.edit.model.Keyword;
import de.seitenbau.govdata.edit.model.Link;
import de.seitenbau.govdata.edit.model.Platform;
import de.seitenbau.govdata.edit.model.ShowcaseViewModel;
import de.seitenbau.govdata.edit.model.Type;
import lombok.extern.slf4j.Slf4j;

/**
 * Mapping between {@link Showcase} and {@link ShowcaseViewModel}.
 *
 */
@Slf4j
public final class ShowcaseMapper
{
  private ShowcaseMapper()
  {
    // private
  }

  /**
   * Maps {@link Showcase} into a {@link ShowcaseViewModel}.
   * 
   * @param showcase the {@link Showcase} to map.
   * @return the mapped {@link ShowcaseViewModel}.
   */
  public static ShowcaseViewModel mapShowcaseEntityToViewModel(Showcase showcase)
  {
    if (Objects.isNull(showcase))
    {
      return null;
    }
    ShowcaseViewModel svm = new ShowcaseViewModel();

    svm.setId(showcase.getId());

    svm.setTitle(showcase.getTitle());

    svm.setNotes(showcase.getNotes());

    svm.setPrivate(showcase.isHidden());

    // showcase type
    Type primaryShowcase = null;
    List<Type> additionalShowcaseTypes = new ArrayList<>();
    List<ShowcaseType> showcaseTypeList = new ArrayList<>(showcase.getShowcaseTypes());
    for (ShowcaseType showcaseType : showcaseTypeList)
    {
      if (showcaseType.isPrimaryShowcase())
      {
        if (primaryShowcase != null)
        {
          log.info("The showcase with ID '{}' has more than one primary showcase type in the database. "
              + "Overriding value '{}' with '{}'", showcase.getId(), primaryShowcase, showcaseType.getName());
        }
        primaryShowcase = new Type(showcaseType.getId(), showcaseType.getName());
      }
      else
      {
        additionalShowcaseTypes.add(new Type(showcaseType.getId(), showcaseType.getName()));
      }
    }
    svm.setPrimaryShowcaseType(primaryShowcase);
    svm.setAdditionalShowcaseTypes(additionalShowcaseTypes);

    // showcase images
    List<Image> imgList = new ArrayList<>();
    for (ShowcaseImage showcaseImg : showcase.getImages())
    {
      Image img = new Image();
      img.setId(showcaseImg.getId());
      img.setImage(showcaseImg.getImage());
      img.setImageOrderId(showcaseImg.getImageOrderId());
      img.setSourceName(showcaseImg.getUrl());
      imgList.add(img);
    }
    svm.setImages(imgList);

    // links
    List<Link> linksToShowcase = new ArrayList<>();
    for (ShowcaseLink scl : showcase.getLinksToShowcase())
    {
      linksToShowcase.add(new Link(scl.getId(), scl.getUrl(), scl.getName()));
    }
    svm.setLinksToShowcase(linksToShowcase);

    List<Link> usedDatasets = new ArrayList<>();
    for (ShowcaseUsedDataset scud : showcase.getUsedDatasets())
    {
      usedDatasets.add(new Link(scud.getId(), scud.getUrl(), scud.getName()));
    }
    svm.setUsedDatasets(usedDatasets);

    // platforms
    List<Platform> platforms = new ArrayList<>();
    for (ShowcasePlatform scp : showcase.getPlatforms())
    {
      platforms.add(new Platform(scp.getId(), scp.getName()));
    }
    svm.setPlatforms(platforms);

    // contact
    if (Objects.nonNull(showcase.getContact()))
    {
      ShowcaseContact showcaseContact = showcase.getContact();
      ContactAddress address = new ContactAddress(showcaseContact.getAddressReceiver(),
          showcaseContact.getAddressExtras(), showcaseContact.getAddressStreet(),
          showcaseContact.getAddressCity(), showcaseContact.getAddressPostalCode(),
          showcaseContact.getAddressCountry());
      svm.setContact(new Contact(showcaseContact.getId(), showcaseContact.getName(),
          showcaseContact.getWebsite(), showcaseContact.getEmail(), address));
    }

    svm.setLinkToSourcesUrl(showcase.getLinkToSourcesUrl());
    svm.setLinkToSourcesName(showcase.getLinkToSourcesName());

    // categories
    List<Category> categories = new ArrayList<>();
    for (ShowcaseCategory scc : showcase.getCategories())
    {
      categories.add(new Category(scc.getId(), scc.getName()));
    }
    svm.setCategories(categories);

    // keywords
    List<Keyword> keywords = new ArrayList<>();
    for (ShowcaseKeyword sck : showcase.getKeywords())
    {
      keywords.add(new Keyword(sck.getId(), sck.getName()));
    }
    svm.setKeywords(keywords);

    svm.setUrl(showcase.getWebsite());

    svm.setManualShowcaseCreatedDate(
        DateUtil.formatDate(showcase.getManualShowcaseCreatedDate(), CommonConstants.DATE_PATTERN));

    svm.setManualShowcaseModifiedDate(
        DateUtil.formatDate(showcase.getManualShowcaseModifiedDate(), CommonConstants.DATE_PATTERN));

    svm.setUsecasePublisher(showcase.getUsecasePublisher());
    svm.setUsecaseSourceUrl(showcase.getUsecaseSourceUrl());

    svm.setCreatorUserId(showcase.getCreatorUserId());

    svm.setSpatial(showcase.getSpatial());

    svm.setCreateDate(DateUtil.formatDate(showcase.getCreateDate(), CommonConstants.DATE_PATTERN));

    svm.setModifyDate(DateUtil.formatDate(showcase.getModifyDate(), CommonConstants.DATE_PATTERN));

    return svm;
  }

  /**
   * Maps {@link ShowcaseViewModel} into a {@link Showcase}.
   * 
   * @param svm the {@link ShowcaseViewModel} to map.
   * @return the mapped {@link Showcase}.
   */
  public static Showcase mapShowcaseViewModelToEntity(ShowcaseViewModel svm)
  {
    if (Objects.isNull(svm))
    {
      return null;
    }
    Showcase showcase = new Showcase();

    showcase.setId(svm.getId());

    showcase.setTitle(svm.getTitle());

    showcase.setNotes(svm.getNotes());

    showcase.setHidden(svm.isPrivate());

    // type
    Set<ShowcaseType> showcaseTypes = new HashSet<>();
    showcaseTypes.add(
        new ShowcaseType(svm.getPrimaryShowcaseType().getId(), svm.getPrimaryShowcaseType().getName(),
            true));
    if (Objects.nonNull(svm.getAdditionalShowcaseTypes()))
    {
      for (Type additionalType : svm.getAdditionalShowcaseTypes())
      {
        showcaseTypes.add(new ShowcaseType(additionalType.getId(), additionalType.getName(), false));
      }
    }
    showcase.setShowcaseTypes(showcaseTypes);

    // image
    if (Objects.nonNull(svm.getImages()))
    {
      Set<ShowcaseImage> showcaseImages = new HashSet<>();
      for (Image img : svm.getImages())
      {
        if (img.getImage() != null)
        {
          showcaseImages.add(
              new ShowcaseImage(img.getId(), img.getImageOrderId(), img.getImage(), img.getSourceName()));
        }
      }
      showcase.setImages(showcaseImages);
    }

    // links
    if (Objects.nonNull(svm.getLinksToShowcase()))
    {
      Set<ShowcaseLink> showcaseLinks = new HashSet<>();
      for (Link link : svm.getLinksToShowcase())
      {
        showcaseLinks.add(new ShowcaseLink(link.getId(), link.getName(), link.getUrl()));
      }
      showcase.setLinksToShowcase(showcaseLinks);
    }

    // datatset links
    if (Objects.nonNull(svm.getUsedDatasets()))
    {
      Set<ShowcaseUsedDataset> showcaseDatasets = new HashSet<>();
      for (Link link : svm.getUsedDatasets())
      {
        showcaseDatasets.add(new ShowcaseUsedDataset(link.getId(), link.getName(), link.getUrl()));
      }
      showcase.setUsedDatasets(showcaseDatasets);
    }

    // platforms
    if (Objects.nonNull(svm.getPlatforms()))
    {
      Set<ShowcasePlatform> showcasePlatforms = new HashSet<>();
      for (Platform plat : svm.getPlatforms())
      {
        showcasePlatforms.add(new ShowcasePlatform(plat.getId(), plat.getName()));
      }
      showcase.setPlatforms(showcasePlatforms);
    }

    // contact
    if (Objects.nonNull(svm.getContact()))
    {
      Contact contact = svm.getContact();
      ContactAddress contactAddress = contact.getAddress();
      ShowcaseContact showcaseContact = new ShowcaseContact(contact.getId(), contact.getName(),
          contact.getEmail(), contact.getUrl(), contactAddress.getAddressee(), contactAddress.getDetails(),
          contactAddress.getStreet(), contactAddress.getCity(), contactAddress.getZip(),
          contactAddress.getCountry());
      showcase.setContact(showcaseContact);
    }

    showcase.setLinkToSourcesUrl(svm.getLinkToSourcesUrl());
    showcase.setLinkToSourcesName(svm.getLinkToSourcesName());

    // categories
    if (Objects.nonNull(svm.getCategories()))
    {
      Set<ShowcaseCategory> showcaseCategories = new HashSet<>();
      for (Category cat : svm.getCategories())
      {
        showcaseCategories.add(new ShowcaseCategory(cat.getId(), cat.getName()));
      }
      showcase.setCategories(showcaseCategories);
    }

    // keywords
    if (Objects.nonNull(svm.getKeywords()))
    {
      Set<ShowcaseKeyword> showcaseKeywords = new HashSet<>();
      for (Keyword key : svm.getKeywords())
      {
        showcaseKeywords.add(new ShowcaseKeyword(key.getId(), key.getName()));
      }
      showcase.setKeywords(showcaseKeywords);
    }

    showcase.setWebsite(svm.getUrl());

    showcase.setManualShowcaseCreatedDate(DateUtil.parseDateString(svm.getManualShowcaseCreatedDate()));

    showcase.setManualShowcaseModifiedDate(DateUtil.parseDateString(svm.getManualShowcaseModifiedDate()));

    showcase.setUsecasePublisher(svm.getUsecasePublisher());
    showcase.setUsecaseSourceUrl(svm.getUsecaseSourceUrl());

    showcase.setCreatorUserId(svm.getCreatorUserId());

    showcase.setSpatial(svm.getSpatial());

    showcase.setCreateDate(DateUtil.parseDateString(svm.getCreateDate()));

    showcase.setModifyDate(DateUtil.parseDateString(svm.getModifyDate()));

    return showcase;
  }
}
