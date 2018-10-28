package com.user.api_challenge.security;

import com.user.api_challenge.security.error.JwtAuthenticationException;
import com.user.api_challenge.security.model.JwtAuthentication;
import com.user.api_challenge.security.model.JwtUnauthenticatedToken;
import com.user.api_challenge.security.utils.JwtTokenUtil;
import io.jsonwebtoken.ExpiredJwtException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class JwtAuthenticationProvider implements AuthenticationProvider {
  private final Log logger = LogFactory.getLog(this.getClass());
  @Autowired private JwtTokenUtil jwtTokenUtil;

  public JwtAuthenticationProvider() {}

  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    try {
      JwtUnauthenticatedToken authToken = (JwtUnauthenticatedToken) authentication;
      String token = (String) authToken.getCredentials();
      Boolean isValid = this.jwtTokenUtil.isSignedAndValid(token);
      if (!isValid) {
        this.logger.info("JWT token is NOT valid: " + token);
        return null;
      } else {
        Long userId = Long.parseLong(this.jwtTokenUtil.getUsernameFromToken(token));
        String issuer = this.jwtTokenUtil.getIssuer(token);
        List<String> jwtRoles = this.jwtTokenUtil.getRolesFromToken(token);
        List<String> jwtPermissions = this.jwtTokenUtil.getPermissionsFromToken(token);
        Collection<GrantedAuthority> authorities = new ArrayList();
        Iterator var10;
        String permission;
        if (jwtRoles != null) {
          var10 = jwtRoles.iterator();

          while (var10.hasNext()) {
            permission = (String) var10.next();
            authorities.add(new SimpleGrantedAuthority("ROLE_" + permission));
          }
        }

        if (jwtPermissions != null) {
          var10 = jwtPermissions.iterator();

          while (var10.hasNext()) {
            permission = (String) var10.next();
            authorities.add(new SimpleGrantedAuthority(permission));
          }
        }

        JwtAuthentication profile = new JwtAuthentication(userId, authorities, token);
        this.logger.debug(
            "JWT token is valid authorities: "
                + Arrays.toString(authorities.toArray())
                + " Issuer: "
                + issuer);
        return profile;
      }
    } catch (ExpiredJwtException e) {
      throw new JwtAuthenticationException("Token expired");
    } catch (Exception e) {
      this.logger.error("Exception: " + e.toString());
      throw new JwtAuthenticationException("Invalid JWT");
    }
  }

  public boolean supports(Class<?> authentication) {
    return JwtUnauthenticatedToken.class.equals(authentication);
  }
}
