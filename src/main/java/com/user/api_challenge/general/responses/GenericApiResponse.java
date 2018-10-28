package com.user.api_challenge.general.responses;

import org.springframework.http.HttpStatus;

import java.util.Objects;

public class GenericApiResponse {

  private HttpStatus status = null;
  private String message = null;

  public GenericApiResponse(HttpStatus status) {
    this.status = status;
  }

  public GenericApiResponse(HttpStatus status, String message) {
    this.status = status;
    this.message = message;
  }

  public GenericApiResponse status(HttpStatus status) {
    this.status = status;
    return this;
  }

  /**
   * Get status
   *
   * @return status
   */
  public HttpStatus getStatus() {
    return status;
  }

  public void setStatus(HttpStatus status) {
    this.status = status;
  }

  public GenericApiResponse message(String message) {
    this.message = message;
    return this;
  }

  /**
   * Get message
   *
   * @return message
   */
  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    GenericApiResponse genericApiResponse = (GenericApiResponse) o;
    return Objects.equals(this.status, genericApiResponse.status)
        && Objects.equals(this.message, genericApiResponse.message);
  }

  @Override
  public int hashCode() {
    return Objects.hash(status, message);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class GenericApiResponse {\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    message: ").append(toIndentedString(message)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}
