package com.user.api_challenge.mockfactory;

import com.user.api_challenge.security.model.JwtAuthentication;
import com.user.api_challenge.security.model.JwtUserDetails;
import com.user.api_challenge.user.model.Roles;
import com.user.api_challenge.user.model.User;
import org.mockito.Mockito;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class WithJwtMockUserSecurityContextFactory implements WithSecurityContextFactory<WithJwtMockUser> {
    public static final long MOCK_USER_ID = 123L;

    public SecurityContext createSecurityContext(WithJwtMockUser withUser) {

        String username = StringUtils.hasLength(withUser.username()) ? withUser.username() : withUser.value();

        List<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();
        for (String authority : withUser.authorities()) {
            grantedAuthorities.add(new SimpleGrantedAuthority(authority));
        }

        if (grantedAuthorities.isEmpty()) {
            for (String role : withUser.roles()) {
                if (role.startsWith("ROLE_")) {
                    throw new IllegalArgumentException("roles cannot start with ROLE_ Got "
                            + role);
                }
                grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + role));
            }
        } else if (withUser.roles().length == 1 && "SYSTEM".equals(withUser.roles()[0])) {
            // generating system token
            grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_SYSTEM"));

        } else if (!(withUser.roles().length == 1 && "USER".equals(withUser.roles()[0]))) {
            throw new IllegalStateException("You cannot define roles attribute " + Arrays.asList(withUser.roles()) + " with authorities attribute " + Arrays.asList(withUser.authorities()));
        }

        JwtAuthentication authentication = new JwtAuthentication(Long.parseLong(username), grantedAuthorities, withUser.token());

        User mockUser = null;

if (authentication.hasRole(Roles.USER.name())) {
            mockUser = Mockito.mock(User.class);


        }

        if (mockUser != null) {
            Mockito.when(mockUser.getId()).thenReturn(Long.valueOf(username));
            JwtUserDetails petProPrincipal = new JwtUserDetails(mockUser, grantedAuthorities);
            authentication.setDetails(petProPrincipal);
        }

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        return context;
    }
}