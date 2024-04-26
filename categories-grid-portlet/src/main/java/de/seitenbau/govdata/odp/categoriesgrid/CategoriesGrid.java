package de.seitenbau.govdata.odp.categoriesgrid;

import static de.seitenbau.govdata.navigation.GovDataNavigation.FRIENDLY_URL_NAME_SEARCHRESULT_PAGE;
import static de.seitenbau.govdata.navigation.GovDataNavigation.PORTLET_NAME_SEARCHRESULT;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portletmvc4spring.bind.annotation.RenderMapping;

import de.seitenbau.govdata.data.api.GovdataResource;
import de.seitenbau.govdata.data.api.dto.CategoryDto;
import de.seitenbau.govdata.navigation.GovDataNavigation;
import de.seitenbau.govdata.odp.categoriesgrid.model.CategoryViewModel;
import de.seitenbau.govdata.odp.common.filter.FilterPathUtils;
import de.seitenbau.govdata.odp.common.filter.SearchConsts;

/**
 * The class constitutes a bean that serves as a source for the categories on the categories-grid
 * portlet.
 * 
 * @author rnoerenberg, SEITENBAU GmbH
 * @author Benjamin Dittwald, Fraunhofer FOKUS
 */
@Controller(value = "categoriesGridController")
@RequestMapping("VIEW")
public class CategoriesGrid
{
  /** The logger. */
  private static final Logger LOG = LoggerFactory.getLogger(CategoriesGrid.class);

  @Autowired
  private GovDataNavigation govDataNavigation;

  @Inject
  private GovdataResource govdataResource;

  /**
   * Erstellt die Objekte für die Anzeige.
   * 
   * @param request der Request.
   * @param response die Response.
   * @param model das Model für die Anzeige.
   * @return der View-Name.
   * @throws PortalException
   * @throws SystemException
   */
  @RenderMapping
  public String showSearchResults(
      RenderRequest request,
      RenderResponse response,
      Model model) throws PortalException, SystemException
  {
    final String method = "showSearchResults() : ";
    LOG.trace(method + "Start");

    List<CategoryDto> categories = getCategories();
    List<CategoryViewModel> categoryViewModels = mapToCategoryViewModels(categories);

    model.addAttribute("categories", categoryViewModels);
    LOG.trace(method + "End");
    return "grid";
  }

  /**
   * Gets the categories.
   * 
   * @return the categories
   */
  private List<CategoryDto> getCategories()
  {
    final String method = "getCategories() : ";
    LOG.trace(method + "Start");

    List<CategoryDto> categories = govdataResource.getCategoriesSortedByTitle();

    LOG.trace(method + "End");
    return categories;
    }

  /**
   * Map to CategoryViewModels and add actionURLs
   * @param categories list of ODP-categories
   * @param renderUrl
   * @return list of categoryViewModels
   * @throws PortalException
   * @throws SystemException
   */
  private List<CategoryViewModel> mapToCategoryViewModels(List<CategoryDto> categories) throws SystemException
  {
    final String method = "mapToCategoryViewModels() : ";
    LOG.trace(method + "Start");

    List<CategoryViewModel> result = new ArrayList<>();

    String redirectUrlString = null;
    for (CategoryDto category : categories)
    {
      try
      {
        // set current category as filter
        String filterParam =
            FilterPathUtils.serializeFilter(SearchConsts.FACET_GROUPS, category.getName());
        PortletURL redirectUrl = govDataNavigation
            .createLinkForSearchResults(FRIENDLY_URL_NAME_SEARCHRESULT_PAGE, PORTLET_NAME_SEARCHRESULT, "",
                filterParam, "", "");
        redirectUrlString = redirectUrl.toString();
      }
      catch (PortalException | IllegalArgumentException e)
      {
        LOG.warn(method + "Fehler beim Erstellen der Filter-URL für die Kategorien. Fehler: {}"
            + e.getMessage());
      }

      result.add(CategoryViewModel.builder()
          .name(category.getName())
          .title(category.getTitle())
          .count(category.getCount())
          .actionURL(redirectUrlString).build());
    }

    LOG.trace(method + "End");
    return result;

  }

}
