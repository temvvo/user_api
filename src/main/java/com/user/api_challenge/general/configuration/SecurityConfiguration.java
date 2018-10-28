package com.user.api_challenge.general.configuration;

import com.user.api_challenge.security.JwtAuthenticationTokenFilter;
import com.user.api_challenge.security.JwtUserDetailsAuthenticationProvider;
import com.user.api_challenge.security.error.AuthenticationExceptionHandler;
import com.user.api_challenge.user.dao.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

  // parses JWT tokens out of requests headers
  @Autowired public JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;
  @Autowired UserRepository userBaseRepository;
  // receives JWT and verifies that JWT is valid and grands authentication
  @Autowired private JwtUserDetailsAuthenticationProvider jwtAuthenticationProvider;
  // handles unauthorized requests (without JWT) to secured resources
  @Autowired private AuthenticationExceptionHandler unauthorizedHandler;

  @Override
  protected void configure(HttpSecurity httpSecurity) throws Exception {

    httpSecurity
        // enable cors
        .cors()
        .and()
        .csrf()
        .disable()
        // capture unauthorized requests to secured endpoints
        .exceptionHandling()
        .authenticationEntryPoint(unauthorizedHandler)
        .and()
        // don't create session
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        .authorizeRequests()
        .antMatchers("/public/**")
        .permitAll()
        .antMatchers("/web/**")
        .permitAll()
        .anyRequest()
        .authenticated()
        .and()
        .addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);
  }

  @Override
  public void configure(AuthenticationManagerBuilder auth) {
    auth.authenticationProvider(jwtAuthenticationProvider);
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {

    CorsConfiguration configuration = new CorsConfiguration();
    configuration.addAllowedOrigin("*");
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "DELETE", "PUT", "OPTIONS"));
    configuration.addAllowedHeader("*");

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }

  @Bean
  public BCryptPasswordEncoder bCryptPasswordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
