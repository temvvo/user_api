package com.user.api_challenge.general.exceptions;


import lombok.Setter;

public class UserExistsException extends Exception {

  public enum Conflict {
    EMAIL_EXISTS,
    USERNAME_EXISTS
  }
  @Setter
  private Conflict conflict;

  public UserExistsException(Conflict conflict) {
    super("User exists");
    this.setConflict(conflict);
  }
    }