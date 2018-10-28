package com.user.api_challenge.user.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

import java.util.List;

@Data
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "user")
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private long id;

  @NotNull(message = "Username is required")
  @NotEmpty(message = "UserName is empty")
  @Column(unique = true)
  private String userName;

  private String firstName;
  private String lastName;

  @NotNull(message = "Email is required")
  @NotEmpty(message = "Email is empty")
  @Email(message = "invalid email")
  @Column(unique = true)
  private String email;

  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  private String password;

  private String phone;
  private String userStatus;

  @JsonIgnore
  @ManyToMany(fetch = FetchType.EAGER)
  @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
  private List<Role> roles;

  public User() {}

  public User(
      String userStatus,
      String userName,
      String email,
      String firstname,
      String lastname,
      String phone) {
    this.setUserStatus(userStatus);
    this.setUserName(userName);
    this.setFirstName(firstname);
    this.setLastName(lastname);
    this.setEmail(email);
    this.setPhone(phone);
  }

  @JsonIgnore
  public String getId2String() {

    return String.valueOf(this.id);
  }

  public void setEmail(String email) {
    if (email != null) {
      this.email = email.toLowerCase();
    }
  }
}
