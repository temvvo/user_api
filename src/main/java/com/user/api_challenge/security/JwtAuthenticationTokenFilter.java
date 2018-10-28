package com.user.api_challenge.security;

import com.user.api_challenge.security.model.JwtUnauthenticatedToken;
import com.user.api_challenge.security.utils.JwtTokenUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {
  private final Log logger = LogFactory.getLog(this.getClass());
  @Autowired private JwtTokenUtil jwtTokenUtil;

  @Value("${JWT_AUTH_HEADER:Authorization}")
  private String tokenHeader;

  public JwtAuthenticationTokenFilter() {}

  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws ServletException, IOException {
    String requestHeader = request.getHeader(this.tokenHeader);
    String username = null;
    String authToken = null;
    if (requestHeader != null && requestHeader.startsWith("Bearer ")) {
      authToken = requestHeader.substring("Bearer ".length());

      try {
        username = this.jwtTokenUtil.getUsernameFromToken(authToken);
      } catch (MalformedJwtException var8) {
        this.logger.error("JWT is malformatted " + var8.getMessage());
      } catch (IllegalArgumentException var9) {
        this.logger.error(
            "an error occurred during getting username from token " + var9.getMessage());
      } catch (SignatureException var10) {
        this.logger.warn("JWT SignatureException:" + var10.getMessage());
      } catch (ExpiredJwtException var11) {
        this.logger.warn("JWT ExpiredJwtException:" + var11.getMessage());
        response.setHeader("jwt_exception", "token expired");
        response.setHeader("jwt_code", "440");
      } catch (JwtException var12) {
        this.logger.warn("JWT exception", var12);
      }
    } else {
      this.logger.debug("Couldn't find JWT bearer string");
    }

    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
      this.logger.debug("Found valid JWT token in header, passing token to security context.");
      Authentication jwtAuthToken = new JwtUnauthenticatedToken(username, authToken);
      SecurityContextHolder.getContext().setAuthentication(jwtAuthToken);
    } else if (username == null) {
      this.logger.debug(
          "Found JWT bearer string in header, but without 'sub' / username. Will Ignore this token! ");
    }

    chain.doFilter(request, response);
  }
}
