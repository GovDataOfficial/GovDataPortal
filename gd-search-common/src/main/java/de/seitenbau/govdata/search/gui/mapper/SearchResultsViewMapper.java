package de.seitenbau.govdata.search.gui.mapper;

import static de.seitenbau.govdata.navigation.GovDataNavigation.FRIENDLY_URL_NAME_SEARCHRESULT_PAGE;
import static de.seitenbau.govdata.navigation.GovDataNavigation.FRIENDLY_URL_NAME_SHOWROOM_PAGE;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.theme.ThemeDisplay;

import de.seitenbau.govdata.cache.CategoryCache;
import de.seitenbau.govdata.cache.LicenceCache;
import de.seitenbau.govdata.clean.StringCleaner;
import de.seitenbau.govdata.common.model.exception.UnknownShowcaseTypeException;
import de.seitenbau.govdata.common.showcase.model.ShowcaseTypeEnum;
import de.seitenbau.govdata.navigation.GovDataNavigation;
import de.seitenbau.govdata.odp.common.filter.SearchConsts;
import de.seitenbau.govdata.odp.registry.model.Category;
import de.seitenbau.govdata.odp.registry.model.Licence;
import de.seitenbau.govdata.odp.registry.model.MetadataEnumType;
import de.seitenbau.govdata.search.gui.model.HitViewModel;
import de.seitenbau.govdata.search.gui.model.LicenseViewModel;
import de.seitenbau.govdata.search.gui.model.ResourceViewModel;
import de.seitenbau.govdata.search.index.model.HitDto;
import de.seitenbau.govdata.search.index.model.ResourceDto;
import lombok.extern.slf4j.Slf4j;

/**
 * Mappt zwischen {@link HitDto} und {@link HitViewModel}.
 * 
 * @author rnoerenberg
 *
 */
@Component
@Slf4j
public class SearchResultsViewMapper
{
  @Inject
  private GovDataNavigation gdNavigation;

  @Inject
  private CategoryCache categoryCache;

  @Inject
  private LicenceCache licenceCache;

  /**
   * Mappt eine Liste von {@link HitDto} zu einer Liste von {@link HitViewModel}.
   * 
   * @param searchHits
   * @param themeDisplay
   * @param currentPage
   * @param preserveContentHTML
   * @return
   * @throws SystemException
   */
  public List<HitViewModel> mapHitDtoListToHitsViewModelList(List<HitDto> searchHits,
      ThemeDisplay themeDisplay, String currentPage, boolean preserveContentHTML) throws SystemException
  {
    final String method = "mapHitDtoListToHitsViewModelList() : ";
    log.trace(method + "Start");

    List<HitViewModel> hitsViewModelList = new ArrayList<HitViewModel>();

    for (HitDto searchHit : searchHits)
    {
      try
      {
        HitViewModel hitViewModel =
            mapHitDtoToHitsViewModel(searchHit, themeDisplay, currentPage, preserveContentHTML);
        hitsViewModelList.add(hitViewModel);
      }
      catch (PortalException e)
      {
        log.warn(method + "Fehler beim Mappen eines Treffers. Details: {}", e.getMessage());
      }
    }

    log.trace(method + "End");
    return hitsViewModelList;
  }

  /**
   * Mappt ein {@link HitDto} zu einem {@link HitViewModel}.
   * 
   * @param searchHit
   * @param themeDisplay
   * @param currentPage
   * @param preserveContentHTML
   * @return
   * @throws SystemException
   * @throws PortalException
   */
  public HitViewModel mapHitDtoToHitsViewModel(HitDto searchHit, ThemeDisplay themeDisplay,
      String currentPage, boolean preserveContentHTML)
      throws SystemException, PortalException
  {
    String source = SearchConsts.SOURCE_CKAN;
    String url = "";
    String ckanLink = null;
    String type = searchHit.getType();
    if (SearchConsts.TYPE_ARTICLE.equals(type))
    {
      url = gdNavigation.getJournalArticleUrl(themeDisplay, searchHit.getArticleId(), searchHit.getGroupId());
      source = SearchConsts.SOURCE_PORTAL;
    }
    else if (SearchConsts.TYPE_BLOG.equals(type))
    {
      url = gdNavigation.getBlogEntryUrl(searchHit.getEntryClassPK(), searchHit.getGroupId());
      source = SearchConsts.SOURCE_PORTAL;
    }
    else if (SearchConsts.TYPE_SHOWCASE.equals(type))
    {
      if (StringUtils.isEmpty(currentPage))
      {
        currentPage = FRIENDLY_URL_NAME_SHOWROOM_PAGE;
      }

      url = gdNavigation.createLinkForMetadata("gdsearchdetails", searchHit.getId(), currentPage).toString();
      source = SearchConsts.SOURCE_SHOWCASE;
    }
    else
    {
      String name = searchHit.getName();
      if (StringUtils.isEmpty(name))
      {
        name = searchHit.getId();
      }
      type = mapTypeFromCkan(searchHit.getType());

      if (StringUtils.isEmpty(currentPage))
      {
        currentPage = FRIENDLY_URL_NAME_SEARCHRESULT_PAGE;
      }

      url = gdNavigation.createLinkForMetadata("gdsearchdetails", name, currentPage).toString();

      // add link to ckan resource
      ckanLink = gdNavigation.createCkanUrl(name);
    }

    String content;
    if (preserveContentHTML)
    {
      content = searchHit.getContent().trim();
    }
    else
    {
      content = StringCleaner.trimAndFilterString(searchHit.getContent());
    }
    HitViewModel hitViewModel =
        HitViewModel
            .builder()
            .source(source)
            .id(searchHit.getId())
            .title(StringCleaner.trimAndFilterString(searchHit.getTitle()))
            .name(searchHit.getName())
            .content(content)
            .type(type)
            .lastModified(searchHit.getLastModified())
            .temporalCoverageFrom(searchHit.getTemporalCoverageFrom())
            .temporalCoverageTo(searchHit.getTemporalCoverageTo())
            .linkToDetailPage(url)
            .linkToCKan(ckanLink)
            .contact(searchHit.getContact())
            .contactEmail(searchHit.getContactEmail())
            .hasOpen(searchHit.getHasOpen())
            .hasClosed(searchHit.getHasClosed())
            .categories(mapCategories(searchHit.getGroups()))
            .ownerOrg(searchHit.getOwnerOrg())
            .resources(mapResources(searchHit.getResources()))
            .primaryShowcaseType(searchHit.getPrimaryShowcaseType())
            .displayImage(searchHit.getDisplayImage())
            .platforms(searchHit.getPlatforms())
            .usedDatasets(searchHit.getUsedDatasets())
            .releaseDate(searchHit.getReleaseDate())
            .allShowcaseTypes(mapShowcaseTypes(searchHit.getAllShowcaseTypes()))
            .build();

    return hitViewModel;
  }

  private List<ResourceViewModel> mapResources(List<ResourceDto> resources)
  {
    List<ResourceViewModel> result = new ArrayList<ResourceViewModel>();
    if (CollectionUtils.isNotEmpty(resources))
    {
      for (ResourceDto resourceDto : resources)
      {
        result.add(ResourceViewModel.builder()
            .id(resourceDto.getId())
            .hash(resourceDto.getHash())
            .format(resourceDto.getFormat())
            .description(resourceDto.getDescription())
            .language(resourceDto.getLanguage())
            .type(resourceDto.getType())
            .url(resourceDto.getUrl())
            .license(mapLicense(resourceDto.getLicense()))
            .build());
      }
    }
    return result;
  }

  private LicenseViewModel mapLicense(String licenseId)
  {
    LicenseViewModel result = null;
    Licence licence = licenceCache.getLicenceMap().get(licenseId);
    if (Objects.nonNull(licence))
    {
      result =
          new LicenseViewModel(licence.getId(), licence.getUrl(), licence.getTitle(), licence.isOpen());
    }
    return result;
  }

  private List<Category> mapCategories(List<String> groups)
  {
    List<Category> result = new ArrayList<Category>();
    if (CollectionUtils.isNotEmpty(groups))
    {
      List<Category> categoryList = categoryCache.getCategoriesSortedByTitle();
      for (Category cat : categoryList)
      {
        if (groups.contains(cat.getName()))
        {
          result.add(cat);
        }
      }
    }
    return result;
  }

  private List<ShowcaseTypeEnum> mapShowcaseTypes(List<String> types)
  {
    List<ShowcaseTypeEnum> result = new ArrayList<>();
    if (CollectionUtils.isNotEmpty(types))
    {
      for (String s : types)
      {
        try
        {
          result.add(ShowcaseTypeEnum.fromField(s));
        }
        catch (UnknownShowcaseTypeException e)
        {
          log.warn("mapShowcaseTypes() : Unexpected showcase-type: " + s);
        }
      }
    }
    return result;
  }

  private String mapTypeFromCkan(String type)
  {
    String result = type;
    MetadataEnumType enumType = MetadataEnumType.fromField(type);
    if (MetadataEnumType.UNKNOWN.equals(enumType))
    {
      result = MetadataEnumType.DATASET.toField();
    }
    return result;
  }
}
