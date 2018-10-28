package com.user.api_challenge.dummydata.controller;

import com.user.api_challenge.general.responses.GenericApiResponse;
import com.user.api_challenge.general.utils.Utils;
import com.user.api_challenge.user.dao.RoleRepository;
import com.user.api_challenge.user.dao.UserRepository;
import com.user.api_challenge.user.model.Role;
import com.user.api_challenge.user.model.Roles;
import com.user.api_challenge.user.model.User;
import com.user.api_challenge.user.model.UserStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping(value = "/public/testdata")
public class DummyDataController {
  private static final Logger logger = LoggerFactory.getLogger(DummyDataController.class);

  @Value("${nubicall.testdata.api.enabled}")
  private boolean isTestdataEnabled;

  @Value("${nubicall.testdata.password}")
  private String testdataPassword;

  private RoleRepository roleRepository;
  private UserRepository userRepository;
  private BCryptPasswordEncoder bCryptPasswordEncoder;

  @Autowired
  public DummyDataController(
      BCryptPasswordEncoder bCryptPasswordEncoder,
      UserRepository userRepository,
      RoleRepository roleRepository) {
    this.userRepository = userRepository;
    this.roleRepository = roleRepository;
    this.bCryptPasswordEncoder = bCryptPasswordEncoder;
  }

  @PostMapping("/user")
  @Transactional
  public ResponseEntity<GenericApiResponse> generateAll() {
    if (!isTestdataEnabled) {
      logger.debug("####### DummyDataController is disabled");
      GenericApiResponse response =
          new GenericApiResponse(HttpStatus.FORBIDDEN, "Dummydata Controller is disabled.");
      return new ResponseEntity<GenericApiResponse>(
          response, new HttpHeaders(), response.getStatus());
    }

    logger.debug("####### DummyDataController.generateAll() started");
    Optional<User> userResult = this.userRepository.findByEmailIgnoreCase("nacho@nubicall.com");
    if (!userResult.isPresent() && !userResult.equals(Optional.empty())) {
      logger.debug(
          "####### DummyDataController.generateAll() is unable to generate new testdata: Already existing testdata");

      return new ResponseEntity<>(
          new GenericApiResponse(
              HttpStatus.OK, "Skipping creation of testdata: Already existing testdata"),
          HttpStatus.OK);
    }
    try {
      this.generateUserTestData();

    } catch (Exception e) {
      return new ResponseEntity<>(
          new GenericApiResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to initialize testdata"),
          HttpStatus.INTERNAL_SERVER_ERROR);
    }

    return new ResponseEntity<>(
        new GenericApiResponse(HttpStatus.OK, "Dummy Data Created"), HttpStatus.OK);
  }

  private void generateUserTestData() {

    Role role = new Role(Roles.USER.name());
    roleRepository.save(role);
    User testUser =
        new User(
            UserStatus.Active.toString(),
            "nacho",
            "nacho@nubicall.com",
            "Ignacio",
            "Galieri",
            "555-5555");

    List roles = new ArrayList<Role>();
    roles.add(role);

    testUser.setRoles(roles);
    testUser.setPassword(
        bCryptPasswordEncoder.encode(Utils.encodePasswordWithSha256(this.testdataPassword)));
    userRepository.save(testUser);
  }
}
