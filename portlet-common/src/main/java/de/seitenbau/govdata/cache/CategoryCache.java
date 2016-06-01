package de.seitenbau.govdata.cache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Repository;

import de.fhg.fokus.odp.registry.model.Category;
import de.seitenbau.govdata.comparator.CategoriesTitleComparator;

@Slf4j
@Repository
public class CategoryCache extends BaseRegistryClientCache
{
  private List<Category> categories;

  private Map<String, Category> categoryMap;

  private List<Category> categoriesSortedByTitle;

  public Map<String, Category> getCategoryMap()
  {
    if (categoryMap == null)
    {
      categoryMap = new HashMap<>();
      for (Category cat : getCategories())
      {
        categoryMap.put(cat.getName(), cat);
      }
    }

    return categoryMap;
  }

  public List<Category> getCategoriesSortedByTitle()
  {
    if (categoriesSortedByTitle == null)
    {
      // Avoid ConcurrentModificationException on same list object when modifying list in Java 8
      List<Category> categoriesTemp = getCategories();
      if (categoriesTemp != null)
      {
        categoriesSortedByTitle = new ArrayList<Category>(categoriesTemp);
        Collections.sort(categoriesSortedByTitle, new CategoriesTitleComparator());
      }
    }

    return categoriesSortedByTitle;
  }

  private List<Category> getCategories()
  {
    if (categories == null)
    {
      log.info("Empty categoriy cache, fetching categories from CKAN.");
      categories = new ArrayList<>();
      for (Category category : getRegistryClient().getInstance().listCategories())
      {
        // only load groups, not subgroups and other stuff.
        if ("group".equalsIgnoreCase(category.getType()))
        {
          categories.add(category);
        }
      }
    }
    return categories;
  }
}
