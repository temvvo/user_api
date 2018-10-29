package com.user.api_challenge.general.exceptions;

import lombok.Data;

@Data
public class UserExistsException extends Exception {

  public enum Conflict {
    EMAIL_EXISTS,
    USERNAME_EXISTS
  }

  private Conflict conflict;

  public UserExistsException(Conflict conflict) {
    super("User exists");
    this.setConflict(conflict);
  }
    }