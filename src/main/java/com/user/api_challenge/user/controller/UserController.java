package com.user.api_challenge.user.controller;

import com.user.api_challenge.general.exceptions.UnprocessableEntityException;
import com.user.api_challenge.general.exceptions.UserExistsException;
import com.user.api_challenge.general.responses.GenericApiResponse;
import com.user.api_challenge.general.responses.UuidGenericResponse;
import com.user.api_challenge.security.model.JwtAuthentication;
import com.user.api_challenge.user.dao.RoleRepository;
import com.user.api_challenge.user.dao.UserRepository;
import com.user.api_challenge.user.model.Roles;
import com.user.api_challenge.user.model.User;

import com.user.api_challenge.user.model.UserStatus;
import com.user.api_challenge.user.requests.CreateUserRequest;
import com.user.api_challenge.user.responses.UserResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "/users")
public class UserController {

  private static final Logger logger = LoggerFactory.getLogger(UserController.class);

  private UserRepository userRepository;
  private RoleRepository roleRepository;

  @Autowired
  public UserController(UserRepository userRepository, RoleRepository roleRepository) {

    this.userRepository = userRepository;
    this.roleRepository = roleRepository;
  }

  @PostMapping("")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<GenericApiResponse> createUser(
      @Validated @RequestBody CreateUserRequest userRequest,
      Errors validationError,
      Principal principal)
      throws UnprocessableEntityException {

    logger.info("POST /users  called");

    if (validationError.hasFieldErrors()) {
      String msg =
          (validationError.getFieldError().getDefaultMessage() != null)
              ? validationError.getFieldError().getDefaultMessage()
              : validationError.getFieldError().getField() + " is wrong or missing";
      return new ResponseEntity<>(
          new GenericApiResponse(HttpStatus.BAD_REQUEST, msg), HttpStatus.BAD_REQUEST);
    }
    // Verify if user from token exists in the DB
    User user = userRepository.findUserById(Long.parseLong(principal.getName()));
    if (user == null) {
      logger.warn("Unable to find user: " + principal.getName());
      return new ResponseEntity<>(
          new GenericApiResponse(HttpStatus.BAD_REQUEST, "Internal error while creating User"),
          HttpStatus.BAD_REQUEST);
    }

    try {

      doCreatePetOwner(userRequest);
      return new ResponseEntity<>(HttpStatus.CREATED);

    } catch (UserExistsException e) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
  }

  @Transactional(rollbackFor = Exception.class)
  public void doCreatePetOwner(CreateUserRequest userRequest)
      throws UserExistsException, UnprocessableEntityException {

    logger.info("Creating new user: " + userRequest.getEmail());


    User userByEmail = userRepository.findUserByEmail(userRequest.getEmail());
    User userByUsername = userRepository.findUserByUserName(userRequest.getUsername());
    if (userByEmail == null) {
      logger.warn("User with email: " + userRequest.getEmail() + " already exists.");
      throw new UserExistsException(UserExistsException.Conflict.EMAIL_EXISTS);
    }

    if (userByUsername == null) {
      logger.warn("User with username: " + userRequest.getUsername() + " already exists.");
      throw new UserExistsException(UserExistsException.Conflict.USERNAME_EXISTS);
    }

    User newUser =
        new User(
            userRequest.getUserStatus(),
            userRequest.getUsername(),
            userRequest.getEmail(),
            userRequest.getFirstName(),
            userRequest.getLastName(),
            userRequest.getPhone());

    if (newUser.getUserStatus() == null
        || !newUser.getUserStatus().equals(UserStatus.Active.name())
        || !newUser.getUserStatus().equals(UserStatus.Blocked.name())) {
      // Default user status
      newUser.setUserStatus(UserStatus.Active.name());
    }

    // TODO: Password should be encrypted in FE
    if (userRequest.getPassword() == null || userRequest.getPassword().isEmpty()) {
      // define initial unknown password for user
      newUser.setPassword(UUID.randomUUID().toString());
    } else {
      newUser.setPassword(userRequest.getPassword());
    }

    // add user roles
    List roles = new ArrayList();
    roles.add(roleRepository.findRoleByName(Roles.USER.name()));
    newUser.setRoles(roles);
    userRepository.save(newUser);
  }

  @GetMapping("/{username}")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<GenericApiResponse> getUserByUsername(
      @PathVariable String username, JwtAuthentication authentication) {

    logger.info("GET /users/" + username + " called");

    //TODO: Verify if user from token exists in the DB

    User user = this.userRepository.findUserByUserName(username);

    // Errorhandling if username does not exists
    if (user == null) {
      return new ResponseEntity<>(
          new UuidGenericResponse(
              HttpStatus.NOT_FOUND,
              "no user with username: " + username + " found",
              UUID.randomUUID().toString()),
          HttpStatus.NOT_FOUND);
    }

    return new ResponseEntity<>(new UserResponse(user), HttpStatus.OK);
  }

  @PutMapping("/{username}")
  @Transactional
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<GenericApiResponse> updateUser(
      @PathVariable String username,
      @RequestBody User userUpdated,
      JwtAuthentication authentication) {


    //TODO: Verify if user from token exists in the DB

    // check if user exists
    User userToChange = userRepository.findUserByUserName(username);
    if (userToChange == null) {
      return new ResponseEntity<>(
          new UuidGenericResponse(
              HttpStatus.NOT_FOUND,
              "no user with username: " + username + " found",
              UUID.randomUUID().toString()),
          HttpStatus.NOT_FOUND);
    }

    // update the user
    // set possibly changed values to database object, everything else should be kept untouched
    userToChange.setFirstName(userUpdated.getFirstName());
    userToChange.setLastName(userUpdated.getLastName());
    userToChange.setPhone(userUpdated.getPhone());
    userToChange.setUserStatus(userUpdated.getUserStatus());
    this.userRepository.save(userToChange);
    return new ResponseEntity<>(new UserResponse(userToChange), HttpStatus.OK);
  }

  @DeleteMapping("/{username}")
  @Transactional
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<GenericApiResponse> deleteUser(
      @PathVariable String username, JwtAuthentication authentication) throws Exception {

    logger.info("DELETE /users/" + username + " called");
    //TODO: Verify if user from token exists in the DB

    // check if user exists in DB
    User userToDelete = userRepository.findUserByUserName(username);
    if (userToDelete == null) {
      return new ResponseEntity<>(
          new UuidGenericResponse(
              HttpStatus.NOT_FOUND,
              "no user with username: " + username + " found",
              UUID.randomUUID().toString()),
          HttpStatus.NOT_FOUND);
    }
    userRepository.delete(userToDelete);

    return new ResponseEntity<>(
        new UuidGenericResponse(
            HttpStatus.OK, "User username: " + username + " deleted", UUID.randomUUID().toString()),
        HttpStatus.OK);
  }
}
