package de.seitenbau.govdata.honeypot;

import java.io.IOException;

import javax.portlet.PortletRequest;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.liferay.portal.kernel.captcha.Captcha;
import com.liferay.portal.kernel.captcha.CaptchaException;
import com.liferay.portal.kernel.captcha.CaptchaTextException;
import com.liferay.portal.kernel.util.ParamUtil;

public class Honeypot implements Captcha
{
  private static final String FIELD_NAME = "email";
  private static final String _TAGLIB_PATH = "/html/taglib/ui/captcha/honeypot.jsp";
  
  
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
  public boolean isEnabled(HttpServletRequest request) throws CaptchaException
  {
    // we never expire
    return true;
  }

  @Override
  public boolean isEnabled(PortletRequest portletRequest) throws CaptchaException
  {
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
}
