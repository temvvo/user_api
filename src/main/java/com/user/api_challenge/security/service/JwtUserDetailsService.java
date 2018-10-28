package com.user.api_challenge.security.service;

import com.user.api_challenge.security.model.JwtUserDetails;
import com.user.api_challenge.user.dao.UserRepository;
import com.user.api_challenge.user.model.Role;
import com.user.api_challenge.user.model.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;

@Service
public class JwtUserDetailsService implements UserDetailsService {
  private final Log logger = LogFactory.getLog(this.getClass());
  @Autowired private UserRepository userRepository;

  public JwtUserDetailsService() {}

  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Optional<User> userResult = this.userRepository.findById(Long.parseLong(username));
    if (!userResult.isPresent()) {
      this.logger.warn("Unable to find user in DB: " + username);
      throw new UsernameNotFoundException(username);
    }
    User user = userResult.get();
    Collection<GrantedAuthority> authorities = new ArrayList();
    Iterator iter = user.getRoles().iterator();

    while (iter.hasNext()) {
      Role role = (Role) iter.next();
      authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));
    }

    return new JwtUserDetails(user, authorities, true, true, true, true);
  }
}
