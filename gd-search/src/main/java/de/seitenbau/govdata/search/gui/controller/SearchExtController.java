package de.seitenbau.govdata.search.gui.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.util.KeyValuePair;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.theme.ThemeDisplay;

import de.seitenbau.govdata.dcatde.ViewUtil;
import de.seitenbau.govdata.odp.common.filter.SearchConsts;
import de.seitenbau.govdata.odp.registry.model.Category;
import de.seitenbau.govdata.odp.registry.model.Licence;
import de.seitenbau.govdata.odp.registry.model.Organization;
import de.seitenbau.govdata.cache.CategoryCache;
import de.seitenbau.govdata.cache.LicenceCache;
import de.seitenbau.govdata.cache.OrganizationCache;
import de.seitenbau.govdata.constants.QueryParamNames;
import de.seitenbau.govdata.search.cache.ResourceFormatCache;
import de.seitenbau.govdata.search.common.searchresult.ParameterProcessing;
import de.seitenbau.govdata.search.common.searchresult.PreparedParameters;
import de.seitenbau.govdata.search.common.searchresult.UrlBuilder;
import de.seitenbau.govdata.search.gui.model.SearchExtViewModel;

@RequestMapping("VIEW")
public class SearchExtController extends AbstractBaseController
{
  @Inject
  private LicenceCache licenceCache;
  
  @Inject
  private OrganizationCache organizationCache;
  
  @Inject
  private CategoryCache categoryCache;
  
  @Inject
  private ResourceFormatCache resourceFormatCache;

  @RenderMapping
  public String showExtendedSearch(
      RenderRequest request,
      RenderResponse response,
      Model model) throws JsonProcessingException
  {
    PortletURL actionUrl = response.createActionURL();
    
    // get currentpage for type filter
    ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
    Locale locale = themeDisplay.getLocale();
    String currentPage = themeDisplay.getLayout().getFriendlyURL();
    
    // preprocessing for parameters
    PreparedParameters preparm =
        ParameterProcessing.prepareParameters(request.getParameterMap(), currentPage);
    
    // list of fields we want to use for the form (filled by js)
    String[] hiddenFields = {
        QueryParamNames.PARAM_PHRASE,
        QueryParamNames.PARAM_FILTER,
        QueryParamNames.PARAM_START,
        QueryParamNames.PARAM_END
    };
    
    // fields we want to just pass through (with "hiddenFields" being exactly the fields we want to exclude)
    UrlBuilder urlBuilder = new UrlBuilder(preparm);
    List<KeyValuePair> passthroughParams = urlBuilder.parametersAsList(hiddenFields);
    
    SearchExtViewModel viewModel = SearchExtViewModel.builder()
        .preparedParameters(preparm)
        .categoryList(prepareCategoryList())
        .licenceList(prepareLicenceList())
        .organizationList(prepareOrganizationList())
        .typeList(prepareTypeList(locale))
        .formatList(prepareFormatList())
        .opennessList(prepareOpennessList(locale))
        .passthroughParams(passthroughParams)
        .hiddenFields(hiddenFields)
        .actionUrl(actionUrl.toString())
        .build();
    
    model.addAttribute("searchExt", viewModel);
    return "searchext";
  }
  
  private List<Map<String, String>> prepareCategoryList()
  {
    List<Map<String, String>> options = new ArrayList<>();
    for(Category cat : categoryCache.getCategoriesSortedByTitle())
    {
      HashMap<String, String> hashMap = new HashMap<>();
      hashMap.put("key", cat.getName());
      hashMap.put("label", cat.getDisplayName());
      options.add(hashMap);
    }
    return options;
  }
  
  private List<Map<String, String>> prepareLicenceList()
  {
    List<Map<String, String>> options = new ArrayList<>();
    for (Licence licence : licenceCache.getActiveLicenceListSortedByTitle())
    {
      HashMap<String, String> hashMap = new HashMap<>();
      hashMap.put("key", licence.getName());
      hashMap.put("label", licence.getTitle());
      options.add(hashMap);
    }
    return options;
  }
  
  private List<Map<String, String>> prepareOrganizationList()
  {
    List<Map<String, String>> options = new ArrayList<>();
    for(Organization item : organizationCache.getOrganizationsSorted())
    {
      HashMap<String, String> hashMap = new HashMap<>();
      hashMap.put("key", item.getId());
      hashMap.put("label", item.getTitle());
      options.add(hashMap);
    }
    return options;
  }

  
  private List<Map<String, String>> prepareFormatList()
  {
    List<String> formats = resourceFormatCache.getFormatsSorted();
    
    List<Map<String, String>> options = new ArrayList<>();
    for(String format : formats)
    {
      HashMap<String, String> hashMap = new HashMap<>();
      hashMap.put("key", format);
      hashMap.put("label", ViewUtil.getShortenedFormatRef(format));
      options.add(hashMap);
    }
    return options;
  }
  
  private List<Map<String, String>> prepareTypeList(Locale locale)
  {
    return translateList(locale, "od.gdsearch.searchresult.filter.label.", new String[] {
        SearchConsts.TYPE_DATASET,
        SearchConsts.TYPE_ARTICLE,
        SearchConsts.TYPE_BLOG
    });
  }
  
  private List<Map<String, String>> prepareOpennessList(Locale locale)
  {
    return translateList(locale, "od.usage_agreement.", new String[] {
        SearchConsts.FACET_HAS_OPEN,
        SearchConsts.FACET_HAS_CLOSED,
    });
  }

  /**
   * Formats a List of translation-Keys into a List ready for display in extended Search Formular
   * @param locale
   * @param translationPrefix
   * @param values
   * @return
   */
  private List<Map<String, String>> translateList(Locale locale, String translationPrefix, String[] values)
  {
    List<Map<String, String>> options = new ArrayList<>();
    
    for(String value : values)
    {
      String label = LanguageUtil.get(locale, translationPrefix + value, value);
      HashMap<String, String> hashMap = new HashMap<>();
      hashMap.put("key", value);
      hashMap.put("label", label);
      options.add(hashMap);
    }
    return options;
  }
}
