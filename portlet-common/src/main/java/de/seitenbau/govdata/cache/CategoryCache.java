package de.seitenbau.govdata.cache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Repository;

import de.fhg.fokus.odp.registry.model.Category;
import de.seitenbau.govdata.comparator.CategoriesTitleComparator;

/**
 * Liefert die gecachte Liste der Kategorien (Gruppen in CKAN).
 * 
 * @author rnoerenberg
 */
@Slf4j
@Repository
public class CategoryCache extends BaseRegistryClientCache
{
  private Map<String, Category> categoryMap;

  private List<Category> categoriesSortedByTitle;

  /**
   * Initialisiert die Klasse mit individuellen Parametern.
   */
  @PostConstruct
  public void init()
  {
    setMaxCacheTimeHours(12);
  }

  /**
   * Gibt die Kategorien in einer Map zurück. Der Key der Map ist der Name der Kategorien.
   * 
   * @return die Map mit den Organisationen.
   */
  public Map<String, Category> getCategoryMap()
  {
    final String method = "getCategoryMap() : ";
    log.trace(method + "Start");

    // fill internal cache if not initialized or cache expired
    if (categoryMap == null || isCacheExpired())
    {
      categoryMap = new HashMap<>();

      List<Category> categoriesTemp = getCategories();
      if (CollectionUtils.isNotEmpty(categoriesTemp))
      {
        for (Category cat : categoriesTemp)
        {
          categoryMap.put(cat.getName(), cat);
        }
      }
    }

    log.trace(method + "End");
    return categoryMap;
  }

  /**
   * Gibt die Kategorien in einer sortierten Liste zurück.
   * 
   * @return die sortierte Liste der Kategorien.
   */
  public List<Category> getCategoriesSortedByTitle()
  {
    final String method = "getCategoriesSortedByTitle() : ";
    log.trace(method + "Start");

    // fill internal cache if not initialized or cache expired
    if (categoriesSortedByTitle == null || isCacheExpired())
    {
      List<Category> categoriesTemp = getCategories();
      if (categoriesTemp != null)
      {
        // Avoid ConcurrentModificationException on same list object when modifying list in Java 8
        categoriesSortedByTitle = new ArrayList<Category>(categoriesTemp);
        Collections.sort(categoriesSortedByTitle, new CategoriesTitleComparator());
      }
    }

    log.trace(method + "End");
    return categoriesSortedByTitle;
  }

  private List<Category> getCategories()
  {
    final String method = "getCategories() : ";
    log.trace(method + "Start");

    log.info("{}Empty or expired category cache, fetching categories from CKAN.", method);

    List<Category> categories = new ArrayList<>();
    List<Category> categoriesFromCkan = getRegistryClient().getInstance().listCategories();
    if (CollectionUtils.isNotEmpty(categoriesFromCkan))
    {
      for (Category category : categoriesFromCkan)
      {
        // only load groups, not subgroups and other stuff.
        if (category != null && "group".equalsIgnoreCase(category.getType()))
        {
          categories.add(category);
        }
      }
    }
    cacheUpdated();

    log.trace(method + "End");
    return categories;
  }
}
