<%@ include file="/html/taglib/ui/captcha/init.jsp" %>

<%
boolean captchaEnabled = false;

try {
  if (portletRequest != null) {
    captchaEnabled = CaptchaUtil.isEnabled(portletRequest);
  }
  else {
    captchaEnabled = CaptchaUtil.isEnabled(request);
  }
}
catch (CaptchaMaxChallengesException cmce) {
  captchaEnabled = true;
}
%>

<c:if test="<%= captchaEnabled %>">
  <aui:input label="" name="email" type="text" cssClass="offscreen" aria-role="presentation" />
</c:if>

<aui:script use="aui-base">
  A.one('#<portlet:namespace />email').attr('tabindex', -1);
</aui:script>