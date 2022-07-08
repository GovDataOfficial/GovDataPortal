package de.seitenbau.govdata.edit.validator;

import java.util.Map;
import java.util.Map.Entry;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang3.StringUtils;

import de.seitenbau.govdata.edit.model.Contact;

public class RequiredRoleValidator implements ConstraintValidator<RequiredRole, Map<String, Contact>>
{
  private String name;

  @Override
  public void initialize(RequiredRole constraintAnnotation)
  {
    name = constraintAnnotation.name();
  }

  @Override
  public boolean isValid(Map<String, Contact> value, ConstraintValidatorContext context)
  {
    // if we have no map, but we need a map to have a required role
    if (value == null)
    {
      return false;
    }
    
    // test if role is present and has a non-empty string as Name
    for (Entry<String, Contact> entry : value.entrySet())
    {
      if (StringUtils.equals(entry.getKey(), name) && StringUtils.isNotEmpty(entry.getValue().getName()))
      {
        return true;
      }
    }
    return false;
  }

}
