package de.seitenbau.govdata.languagehook;

import com.liferay.portal.kernel.language.UTF8Control;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;

import java.util.Enumeration;
import java.util.ResourceBundle;

import javax.portlet.Portlet;

import org.osgi.service.component.annotations.Component;


@Component(
    property = { "language.id=de_DE" }, 
    service = ResourceBundle.class
)
public class LanguageHookDeResourceBundle extends ResourceBundle {

    @Override
    protected Object handleGetObject(String key) {
        return _resourceBundle.getObject(key);
    }

    @Override
    public Enumeration<String> getKeys() {
        return _resourceBundle.getKeys();
    }

    private final ResourceBundle _resourceBundle = ResourceBundle.getBundle(
        "content.Language_de_DE", UTF8Control.INSTANCE);

}