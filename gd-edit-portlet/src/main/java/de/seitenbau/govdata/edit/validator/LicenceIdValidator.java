package de.seitenbau.govdata.edit.validator;

import javax.inject.Inject;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import de.seitenbau.govdata.cache.LicenceCache;

public class LicenceIdValidator implements ConstraintValidator<LicenceId, String>
{
  @Inject
  private LicenceCache licenceCache;

  @Override
  public void initialize(LicenceId constraintAnnotation)
  {
  }

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context)
  {
    return licenceCache.getLicenceMap().containsKey(value);
  }

}
