package com.user.api_challenge.security.model;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Iterator;

public class JwtAuthentication extends AbstractAuthenticationToken {
  private Long userId;
  private String jwtToken;
  private UserDetails details;

  public JwtAuthentication(Long userId, Collection<GrantedAuthority> authorities, String jwtToken) {
    super(authorities);
    this.jwtToken = jwtToken;
    this.userId = userId;
  }

  public Object getCredentials() {
    return this.jwtToken;
  }

  public Object getPrincipal() {
    return this.details != null ? this.details : this.userId;
  }

  public boolean isAuthenticated() {
    return true;
  }

  public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {}

  public boolean hasRole(String role) {
    if (this.getAuthorities() == null) {
      return false;
    } else {
      Iterator var2 = this.getAuthorities().iterator();

      GrantedAuthority grantedAuthority;
      do {
        if (!var2.hasNext()) {
          return false;
        }

        grantedAuthority = (GrantedAuthority) var2.next();
      } while (!grantedAuthority.getAuthority().equals("ROLE_" + role));

      return true;
    }
  }

  public boolean hasAuthority(String authority) {
    if (this.getAuthorities() == null) {
      return false;
    } else {
      Iterator var2 = this.getAuthorities().iterator();

      GrantedAuthority grantedAuthority;
      do {
        if (!var2.hasNext()) {
          return false;
        }

        grantedAuthority = (GrantedAuthority) var2.next();
      } while (!grantedAuthority.getAuthority().equals(authority));

      return true;
    }
  }

  public Long getUserId() {
    return this.userId;
  }

  public String getJwtToken() {
    return this.jwtToken;
  }

  public UserDetails getDetails() {
    return this.details;
  }

  public void setDetails(UserDetails details) {
    this.details = details;
  }
}
