package de.seitenbau.govdata.honeypot;

import java.io.IOException;
import java.util.Map;

import javax.portlet.PortletRequest;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;

import com.liferay.captcha.configuration.CaptchaConfiguration;
import com.liferay.captcha.simplecaptcha.SimpleCaptchaImpl;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.captcha.Captcha;
import com.liferay.portal.kernel.captcha.CaptchaException;
import com.liferay.portal.kernel.captcha.CaptchaTextException;
import com.liferay.portal.kernel.util.ParamUtil;

@Component(
    configurationPid = "com.liferay.captcha.configuration.CaptchaConfiguration",
    immediate = true,
    property = "captcha.engine.impl=de.seitenbau.govdata.honeypot.Honeypot",
    service = Captcha.class
)
public class Honeypot extends SimpleCaptchaImpl
{
  private static final String FIELD_NAME = "email";

  private static final String _TAGLIB_PATH = "/captcha/honeypot.jsp";
  
  @Override
  public void check(HttpServletRequest request) throws CaptchaException
  {
    validate(ParamUtil.getString(request, FIELD_NAME));
  }


  @Override
  public void check(PortletRequest request) throws CaptchaException
  {
    validate(ParamUtil.getString(request, FIELD_NAME));
  }
  
  private void validate(String parameter) throws CaptchaException
  {
    if(StringUtils.isNotEmpty(parameter)) {
      throw new CaptchaTextException("Honeypot activated.");
    }
  }

  @Override
  public String getTaglibPath()
  {
    return _TAGLIB_PATH;
  }

  @Override
  public boolean isEnabled(HttpServletRequest httpServletRequest) {
    // we never expire
    return true;
  }

  @Override
  public boolean isEnabled(PortletRequest portletRequest) {
    // we never expire
    return true;
  }

  @Override
  public void serveImage(HttpServletRequest request, HttpServletResponse response) throws IOException
  {
    // we serve no image
  }

  @Override
  public void serveImage(ResourceRequest resourceRequest, ResourceResponse resourceResponse)
      throws IOException
  {
    // we serve no image
  }
  
  @Override
  @Activate
  @Modified
  protected void activate(Map<String, Object> properties) {
    setCaptchaConfiguration(ConfigurableUtil.createConfigurable(CaptchaConfiguration.class, properties));
  }
}
