package de.seitenbau.govdata.edit.validator;

import javax.inject.Inject;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import de.seitenbau.govdata.cache.LicenceCache;
import de.seitenbau.govdata.odp.registry.model.Licence;

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
    Licence licence = licenceCache.getLicenceMap().get(value);
    return licence != null && licence.isActive();
  }

}
