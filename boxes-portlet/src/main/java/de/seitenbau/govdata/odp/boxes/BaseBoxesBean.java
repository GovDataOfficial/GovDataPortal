package de.seitenbau.govdata.odp.boxes;

import javax.faces.context.FacesContext;

import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;

public abstract class BaseBoxesBean<T>
{

  /**
   * Gets the ThemeDisplay.
   * 
   * @return
   */
  public ThemeDisplay getThemeDisplay()
  {
    ThemeDisplay themeDisplay = (ThemeDisplay) FacesContext.getCurrentInstance().getExternalContext()
        .getRequestMap().get(WebKeys.THEME_DISPLAY);
    return themeDisplay;
  }

}
