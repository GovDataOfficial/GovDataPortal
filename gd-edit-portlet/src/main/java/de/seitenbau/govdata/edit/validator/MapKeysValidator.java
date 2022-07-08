package de.seitenbau.govdata.edit.validator;

import java.util.Map;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang3.ArrayUtils;

public class MapKeysValidator implements ConstraintValidator<MapKeys, Map<String, ?>>
{
  
  
  private String[] allowedKeys;

  @Override
  public void initialize(MapKeys constraintAnnotation)
  {
    allowedKeys = constraintAnnotation.allowedKeys();
  }

  @Override
  public boolean isValid(Map<String, ?> value, ConstraintValidatorContext context)
  {
    // if we have no map, that's okay.
    if (value == null)
    {
      return true;
    }
    
    // test each key if it is allowed
    for (String key : value.keySet())
    {
      if (!ArrayUtils.contains(allowedKeys, key))
      {
        return false;
      }
    }
    return true;
  }

}
