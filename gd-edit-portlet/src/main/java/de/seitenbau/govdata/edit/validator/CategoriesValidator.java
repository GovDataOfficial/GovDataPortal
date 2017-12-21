package de.seitenbau.govdata.edit.validator;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import de.seitenbau.govdata.odp.registry.model.Category;
import de.seitenbau.govdata.cache.CategoryCache;

public class CategoriesValidator implements ConstraintValidator<Categories, List<String>>
{
  @Inject
  private CategoryCache categoryCache;
  
  Map<String, Category> categoryMap;

  @Override
  public void initialize(Categories constraintAnnotation)
  {
    categoryMap = categoryCache.getCategoryMap();
  }

  /**
   * To be valid, every category must be one of the known categories.
   */
  @Override
  public boolean isValid(List<String> value, ConstraintValidatorContext context)
  {
    if(value != null) {
      for(String cat : value) {
        if(!categoryMap.containsKey(cat)) {
          return false;
        }
      }
    }
    
    return true;
  }

}
