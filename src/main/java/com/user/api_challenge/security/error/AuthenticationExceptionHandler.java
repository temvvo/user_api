package com.user.api_challenge.security.error;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;

@Component
public class AuthenticationExceptionHandler implements AuthenticationEntryPoint, Serializable {
  private final Log logger = LogFactory.getLog(this.getClass());

  public AuthenticationExceptionHandler() {}

  public void commence(
      HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException authException)
      throws IOException {
    this.logger.warn("invalid authentication: " + authException.getLocalizedMessage());
    response.sendError(401, authException.getMessage());
  }
}
