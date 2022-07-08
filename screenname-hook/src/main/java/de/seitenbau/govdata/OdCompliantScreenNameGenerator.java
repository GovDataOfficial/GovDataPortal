package de.seitenbau.govdata;

import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.ScreenNameGenerator;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.StringUtil;

import com.liferay.portal.kernel.util.Validator;

/**
 * Generates user screen names complying with the CKAN user name scheme. Similar to the
 * DefaultScreenNameGenerator, but excludes all special characters except underscore.
 */
public class OdCompliantScreenNameGenerator implements ScreenNameGenerator
{
  @Override
  public String generate(long companyId, long userId, String emailAddress) throws Exception
  {
    // try to construct a base name
    String screenName = StringUtil.extractFirst(emailAddress, "@");
    // in case the email was null or no @ is contained, fall back to the user ID
    if (screenName == null || screenName.isEmpty())
    {
      screenName = PREFIX + SEPARATOR + userId;
    }

    // replace all unwanted characters
    screenName = StringUtil.toLowerCase(screenName);
    for (char c : screenName.toCharArray())
    {
      if (!isCharAllowed(c))
      {
        screenName = StringUtil.replace(
            screenName, c, SEPARATOR);
      }
    }

    // name needs to start with a letter
    if (!Validator.isChar(screenName.charAt(0)))
    {
      screenName = PREFIX + screenName;
    }

    // obtain reserved screen names from preferences and fallback to properties.
    // This is the same as in the default name generator. Unfortunately, it isn't usable as
    // separate method, so copy it
    String[] reservedScreenNames = PrefsPropsUtil.getStringArray(
        companyId, PropsKeys.ADMIN_RESERVED_SCREEN_NAMES,
        "\n", _ADMIN_RESERVED_SCREEN_NAMES);

    // if name is used, append a number to the name until a free name is found
    int suffix = 1;
    String baseName = screenName;
    while (!isNameValid(companyId, screenName, reservedScreenNames))
    {
      screenName = baseName + SEPARATOR + suffix;
      suffix++;
    }

    return screenName;
  }

  /**
   * Checks if the given name is valid, analogous to the DefaultScreenNameGenerator.
   *
   * @param companyId company ID
   * @param screenName screen name to check
   * @param reservedNames Array of reserved screen names
   * @return True if the name is valid.
   */
  protected boolean isNameValid(long companyId, String screenName, String[] reservedNames)
  {
    for (String name : reservedNames)
    {
      if (StringUtil.equalsIgnoreCase(screenName, name))
      {
        return false;
      }
    }

    User user = UserLocalServiceUtil.fetchUserByScreenName(
        companyId, screenName);

    if (user != null)
    {
      return false;
    }

    Group friendlyURLGroup = GroupLocalServiceUtil.fetchFriendlyURLGroup(
        companyId, "/" + screenName);

    // valid if there also exists no group for the given name
    return friendlyURLGroup == null;
  }

  /**
   * Checks if the given character may be used for the screen name.
   *
   * @param c character to check
   * @return True if the character is allowed
   */
  protected static boolean isCharAllowed(char c)
  {
    return Validator.isChar(c) || Validator.isDigit(c) || c == SEPARATOR;
  }

  // reserved screen names in properties (like in default generator)
  private static final String[] _ADMIN_RESERVED_SCREEN_NAMES =
      StringUtil.splitLines(
          PropsUtil.get(PropsKeys.ADMIN_RESERVED_SCREEN_NAMES));

  private static final char SEPARATOR = '_';

  private static final String PREFIX = "user";
}
