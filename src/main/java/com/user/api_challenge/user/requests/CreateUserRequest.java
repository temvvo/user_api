package com.user.api_challenge.user.requests;

import lombok.Data;
import javax.validation.constraints.NotEmpty;

@Data
public class CreateUserRequest {

  @NotEmpty(message = "username is missing")
  private String username;

  private String firstName;

  private String lastName;

  @NotEmpty(message = "email is missing")
  private String email;

  private String password;

  private String phone;

  private String userStatus;

  public void setEmail(String email) {
    this.email = email.toLowerCase();
  }
}
