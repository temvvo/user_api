package com.user.api_challenge.user.model;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
public class UserLoginRequest {

  @NotEmpty String email;

  @NotEmpty String password;

  public UserLoginRequest() {}

  public UserLoginRequest(String email, String password) {
    this.email = email;
    this.password = password;
  }

  public void setEmail(String email) {
    this.email = email.toLowerCase();
  }
}
