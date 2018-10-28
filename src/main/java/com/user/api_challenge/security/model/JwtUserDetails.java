package com.user.api_challenge.security.model;

import com.user.api_challenge.user.model.User;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Iterator;

@Data
public class JwtUserDetails implements UserDetails {
  User user;
  private Boolean accountNonExpired = false;
  private Boolean accountNonLocked = false;
  private Boolean credentialsNonExpired = false;
  private Boolean enabled = false;
  Collection<GrantedAuthority> authorities;

  public JwtUserDetails() {}

  public JwtUserDetails(User user, Collection<GrantedAuthority> authorities) {
    this.user = user;
    this.authorities = authorities;
  }

  public JwtUserDetails(
      User user,
      Collection<GrantedAuthority> authorities,
      Boolean accountNonExpired,
      Boolean accountNonLocked,
      Boolean credentialsNonExpired,
      Boolean enabled) {
    this.user = user;
    this.accountNonExpired = accountNonExpired;
    this.accountNonLocked = accountNonLocked;
    this.credentialsNonExpired = credentialsNonExpired;
    this.enabled = enabled;
    this.authorities = authorities;
  }

  public Collection<? extends GrantedAuthority> getAuthorities() {
    return this.authorities;
  }

  public String getPassword() {
    return this.getUser().getPassword();
  }

  public String getUsername() {
    return Long.toString(this.getUser().getId());
  }

  public boolean isAccountNonExpired() {
    return this.accountNonExpired;
  }

  public boolean isAccountNonLocked() {
    return this.accountNonLocked;
  }

  public boolean isCredentialsNonExpired() {
    return this.credentialsNonExpired;
  }

  public boolean isEnabled() {
    return this.enabled;
  }

  public boolean hasRole(String role) {
    if (this.getAuthorities() == null) return false;

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

  public boolean hasAuthority(String authority) {
    if (this.getAuthorities() == null) return false;

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
