package de.seitenbau.govdata.edit.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;

public class ContributorIdValidator implements ConstraintValidator<ContributorId, String>
{
  private boolean hideContributorId;

  @Override
  public void initialize(ContributorId constraintAnnotation)
  {
  }

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context)
  {
    // Always return true if contributorID is not beeing used
    if (hideContributorId || StringUtils.isNotEmpty(value))
    {
      return true;
    }
    return false;
  }

  @Value("${editform.disable.contributor.id.field:}")
  public void setHideContributorId(String hideContributorId)
  {
    this.hideContributorId = Boolean.valueOf(StringUtils.trim(hideContributorId));
  }

}
