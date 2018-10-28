package com.user.api_challenge.security;

import com.user.api_challenge.security.model.JwtAuthentication;
import com.user.api_challenge.security.service.JwtUserDetailsService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class JwtUserDetailsAuthenticationProvider extends JwtAuthenticationProvider {
  private final Log logger = LogFactory.getLog(this.getClass());
  @Autowired JwtUserDetailsService userDetailsService;

  public JwtUserDetailsAuthenticationProvider() {}

  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    JwtAuthentication jwtAuthentication = (JwtAuthentication) super.authenticate(authentication);
    if (jwtAuthentication.hasRole("SYSTEM")) {
      this.logger.debug("User details auth ignored because of SYSTEM JWT");
      return jwtAuthentication;
    } else {
      if (jwtAuthentication != null && jwtAuthentication.isAuthenticated()) {
        this.logger.debug("Fetching user details for user: " + jwtAuthentication.getName());
        UserDetails details =
            this.userDetailsService.loadUserByUsername(jwtAuthentication.getName());
        jwtAuthentication.setDetails(details);
      }

      return jwtAuthentication;
    }
  }
}
