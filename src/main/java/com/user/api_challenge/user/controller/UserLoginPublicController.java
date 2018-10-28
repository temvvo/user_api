package com.user.api_challenge.user.controller;

import com.user.api_challenge.general.responses.GenericApiResponse;
import com.user.api_challenge.general.responses.LoginResponse;
import com.user.api_challenge.security.utils.JwtTokenUtil;
import com.user.api_challenge.user.dao.UserRepository;
import com.user.api_challenge.user.model.Role;
import com.user.api_challenge.user.model.User;
import com.user.api_challenge.user.model.UserLoginRequest;

import com.user.api_challenge.user.model.UserStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
public class UserLoginPublicController {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  @Autowired UserRepository userRepository;
  @Autowired JwtTokenUtil jwtTokenUtil;
  @Autowired private BCryptPasswordEncoder bCryptPasswordEncoder;

  /**
   * Generic login function. This function checks if the user credentials are correct and create a
   * JWT including .
   *
   * @param login
   * @return
   */
  @PostMapping(value = {"/public/user/login"})
  @SuppressWarnings("unchecked")
  public ResponseEntity<GenericApiResponse> login(@Valid @RequestBody UserLoginRequest login) {

    String email = login.getEmail();
    String password = login.getPassword();

    Optional<User> userResult = userRepository.findByEmailIgnoreCase(email);

    // Check if email is correct
    if (!userResult.isPresent() && userResult.equals(Optional.empty())) {
      logger.warn("User login attempt " + email + " failed! User not found!");
      return getUnauthorizedsResponse();
    }
    User user = userResult.get();

    if (!user.getUserStatus().equals(UserStatus.Active.name())) {
      logger.warn(
          "User login attempt "
              + user.getEmail()
              + " failed! Invalid account status: "
              + user.getUserStatus());
      return getUnauthorizedsResponse();
    }

    // Check if password is correct
    if (bCryptPasswordEncoder.matches(password, user.getPassword())) {

      List<String> roles = new ArrayList<>();
      for (Role role : user.getRoles()) {
        roles.add(role.getName()); // add user roles to JWT token
      } // endFor

      logger.info("User login attempt (" + user.getEmail() + ") successful! Roles: " + roles);

      String jwtToken =
          jwtTokenUtil.generateToken(Long.toString(user.getId()), roles, this.getClass());
      Date tokenExpDate = jwtTokenUtil.getExpirationDateFromToken(jwtToken);
      LoginResponse response =
          new LoginResponse(jwtToken, "Success, token expires: " + tokenExpDate);

      return new ResponseEntity<>(response, response.getStatus());
    } else {
      logger.info("User login attempt " + user.getEmail() + " failed! Bad password");
      return getUnauthorizedsResponse();
    }
  }

  private ResponseEntity getUnauthorizedsResponse() {
    return new ResponseEntity<>(
        new GenericApiResponse(HttpStatus.UNAUTHORIZED, "Bad credentials"),
        HttpStatus.UNAUTHORIZED);
  }
}
