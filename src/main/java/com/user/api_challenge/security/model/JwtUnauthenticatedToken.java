package com.user.api_challenge.security.model;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class JwtUnauthenticatedToken implements Authentication {
  private final String username;
  private final String token;

  public JwtUnauthenticatedToken(String username, String token) {
    this.username = username;
    this.token = token;
  }

  public Collection<? extends GrantedAuthority> getAuthorities() {
    return null;
  }

  public Object getCredentials() {
    return this.token;
  }

  public Object getDetails() {
    return null;
  }

  public Object getPrincipal() {
    return this.username;
  }

  public boolean isAuthenticated() {
    return false;
  }

  public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {}

  public String getName() {
    return this.username;
  }
}
