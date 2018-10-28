package com.user.api_challenge.general.responses;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public class LoginResponse extends GenericApiResponse {

  @Getter private String token = null;

  public LoginResponse(String token) {
    super(HttpStatus.OK);
    this.token = token;
  }

  public LoginResponse(String token, String msg) {
    super(HttpStatus.OK, msg);
    this.token = token;
  }
}
