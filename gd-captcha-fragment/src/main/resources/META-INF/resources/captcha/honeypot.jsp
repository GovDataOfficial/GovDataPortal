
<%@ include file="/captcha/init.jsp" %>

<c:if test="<%= captchaEnabled %>">
  <aui:input label="" name="email" tabindex="-1" type="text" cssClass="offscreen" aria-role="presentation" />
</c:if>
