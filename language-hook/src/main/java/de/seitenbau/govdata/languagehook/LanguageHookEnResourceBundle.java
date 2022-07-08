package de.seitenbau.govdata.languagehook;

import com.liferay.portal.kernel.language.UTF8Control;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;

import java.util.Enumeration;
import java.util.ResourceBundle;

import javax.portlet.Portlet;

import org.osgi.service.component.annotations.Component;

@Component(property = {"language.id=en_US"}, service = ResourceBundle.class)
public class LanguageHookEnResourceBundle extends ResourceBundle
{

  @Override
  protected Object handleGetObject(String key)
  {
    return _resourceBundle.getObject(key);
  }

  @Override
  public Enumeration<String> getKeys()
  {
    return _resourceBundle.getKeys();
  }

  private final ResourceBundle _resourceBundle = ResourceBundle.getBundle(
      "content.Language_en_US", UTF8Control.INSTANCE);

}