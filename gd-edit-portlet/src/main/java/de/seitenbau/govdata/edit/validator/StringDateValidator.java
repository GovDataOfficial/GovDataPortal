package de.seitenbau.govdata.edit.validator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang3.StringUtils;

public class StringDateValidator implements ConstraintValidator<StringDate, String>
{
  SimpleDateFormat dateFormat;
  Pattern regexFormat;

  @Override
  public void initialize(StringDate constraintAnnotation)
  {
    dateFormat = new SimpleDateFormat(constraintAnnotation.format());
    dateFormat.setLenient(false);
    
    regexFormat = Pattern.compile(constraintAnnotation.regex());
  }

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context)
  {
    // null and empty string is valid
    if(StringUtils.isEmpty(value)) {
      return true;
    }
    
    // check if the correct string format is given
    Matcher matcher = regexFormat.matcher(value);
    if(!matcher.matches()) {
      return false; // early exit, the date format does not match.
    }
    
    // check if the string validates as correct date
    try
    {
      return (dateFormat.parse(value) != null);
    }
    catch (ParseException e)
    {
      return false;
    }
  }

}
