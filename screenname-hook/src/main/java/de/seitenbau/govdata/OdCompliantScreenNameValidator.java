package de.seitenbau.govdata;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.security.auth.ScreenNameValidator;
import com.liferay.portal.kernel.util.PropsUtil;

/**
 * The Class OdCompliantScreenNameValidator.
 */
public class OdCompliantScreenNameValidator implements ScreenNameValidator
{
  /** The log. */
  private final Logger log = LoggerFactory.getLogger(getClass());

  /**
   * Validate the screen name provided by the user
   * @see com.liferay.portal.security.auth.ScreenNameValidator#validate(long, String)
   */
  @Override
  public boolean validate(long loginId, String screenName)
  {

    String stringPattern = getStringPattern();

    log.debug("Using [" + stringPattern + "] as pattern for new registered user with screen name: "
        + screenName);

    Pattern pattern = Pattern.compile(stringPattern);
    Matcher matcher = pattern.matcher(screenName);

    return matcher.find();
  }

  /**
   * Provides the JavaScript validations for the UI
   */
  @Override
  public String getAUIValidatorJS()
  {

    String jsStringPattern = getStringPattern().replace("\\", "\\\\");

    log.debug("Using [" + jsStringPattern + "] as pattern in Javascript AUI-Validator.");

    return "function(val) {var pattern = new RegExp('" + jsStringPattern
        + "');if (val.match(pattern)) {return true;}return false;}";
  }

  /**
   * This method display the actual error message in the UI
   */
  @Override
  public String getDescription(Locale locale)
  {
    return LanguageUtil.get(locale, "please-enter-a-valid-screen-name");
  }

  /**
   * 
   * @return regex-pattern
   */
  private String getStringPattern()
  {

    String stringPattern = PropsUtil.get("users.screen.name.validator.regexpattern");

    if (StringUtils.isBlank(stringPattern))
    {
      stringPattern = "^[a-zA-Z]+\\w*$";
    }

    return stringPattern;
  }

}
