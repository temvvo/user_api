package com.user.api_challenge.general.exceptions;

public class UnprocessableEntityException extends Exception {
  public UnprocessableEntityException(String message) {
    super(message);
  }

  public UnprocessableEntityException() {
    super("Unprocessable entity");
  }
}
