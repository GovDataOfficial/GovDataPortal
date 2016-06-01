package de.seitenbau.govdata.search.gui.mapper;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.theme.ThemeDisplay;

import de.fhg.fokus.odp.registry.model.Category;
import de.fhg.fokus.odp.registry.model.MetadataEnumType;
import de.seitenbau.govdata.cache.CategoryCache;
import de.seitenbau.govdata.clean.StringCleaner;
import de.seitenbau.govdata.filter.SearchConsts;
import de.seitenbau.govdata.navigation.GovDataNavigation;
import de.seitenbau.govdata.search.gui.model.HitViewModel;
import de.seitenbau.govdata.search.gui.model.ResourceViewModel;
import de.seitenbau.govdata.search.index.model.HitDto;
import de.seitenbau.govdata.search.index.model.ResourceDto;

@Component
@Slf4j
public class SearchResultsViewMapper
{
  @Inject
  private GovDataNavigation gdNavigation;

  @Inject
  private CategoryCache categoryCache;

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
        HitViewModel hitViewModel = mapHitDtoToHitsViewModel(searchHit, themeDisplay, currentPage, preserveContentHTML);
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

  public HitViewModel mapHitDtoToHitsViewModel(HitDto searchHit, ThemeDisplay themeDisplay, String currentPage, boolean preserveContentHTML)
      throws SystemException, PortalException
  {
    String source = SearchConsts.SOURCE_CKAN;
    String url = "";
    String ckanLink = null;
    String type = searchHit.getType();
    if (SearchConsts.TYPE_ARTICLE.equals(type))
    {
      url = gdNavigation.getArticleLayoutUrl(
          themeDisplay, searchHit.getArticleId(), searchHit.getGroupId());
      source = SearchConsts.SOURCE_PORTAL;
    }
    else if (SearchConsts.TYPE_BLOG.equals(type))
    {
      url = gdNavigation.getBlogEntryUrl(
          searchHit.getEntryClassPK(), searchHit.getPortletId(), searchHit.getGroupId());
      source = SearchConsts.SOURCE_PORTAL;
    }
    else
    {
      String name = searchHit.getName();
      if(name.isEmpty())
      {
        name = searchHit.getId();
      }
      type = mapTypeFromCkan(searchHit.getType());
      
      if(StringUtils.isEmpty(currentPage)) {
        currentPage = "suchen";
      }
      
      url = gdNavigation.createLinkForMetadata("gdsearchdetails", name, currentPage).toString();
      
      // add link to ckan resource
      ckanLink = gdNavigation.createCkanUrl(name);
    }

    HitViewModel hitViewModel = HitViewModel.builder()
        .source(source)
        .id(searchHit.getId())
        .title(StringCleaner.trimAndFilterString(searchHit.getTitle()))
        .content((preserveContentHTML ? searchHit.getContent().trim() : StringCleaner.trimAndFilterString(searchHit.getContent())))
        .type(type)
        .lastModified(searchHit.getLastModified())
        .temporalCoverageFrom(searchHit.getTemporalCoverageFrom())
        .temporalCoverageTo(searchHit.getTemporalCoverageTo())
        .linkToDetailPage(url)
        .linkToCKan(ckanLink)
        .contact(searchHit.getContact())
        .contactEmail(searchHit.getContactEmail())
        .isOpen(searchHit.getIsOpen())
        .categories(mapCategories(searchHit.getGroups()))
        .ownerOrg(searchHit.getOwnerOrg())
        .resources(mapResources(searchHit.getResources()))
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
            .build());
      }
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
