package com.user.api_challenge.user.controller;

import com.user.api_challenge.general.configuration.SecurityConfiguration;
import com.user.api_challenge.mockfactory.WithJwtMockUser;
import com.user.api_challenge.security.JwtAuthenticationProvider;
import com.user.api_challenge.security.JwtUserDetailsAuthenticationProvider;
import com.user.api_challenge.security.error.AuthenticationExceptionHandler;
import com.user.api_challenge.security.service.JwtUserDetailsService;
import com.user.api_challenge.security.utils.JwtTokenUtil;
import com.user.api_challenge.user.dao.RoleRepository;
import com.user.api_challenge.user.dao.UserRepository;
import com.user.api_challenge.user.model.Role;
import com.user.api_challenge.user.model.Roles;
import com.user.api_challenge.user.model.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@Import({
  SecurityConfiguration.class,
  JwtTokenUtil.class,
  JwtAuthenticationProvider.class,
  AuthenticationExceptionHandler.class,
  JwtAuthenticationProvider.class,
  JwtUserDetailsAuthenticationProvider.class,
  JwtUserDetailsAuthenticationProvider.class,
  JwtUserDetailsService.class
})
// include security config
@WebMvcTest(value = UserController.class)
public class UserControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private UserRepository userRepository;

  @MockBean private RoleRepository roleRepository;

  private User USER_MOCKED;

  @Before
  public void setUp() {

    // ####################################################################
    // petowner
    Role role = new Role(Roles.USER.name());
    List roles = new ArrayList();
    roles.add(role);

    USER_MOCKED =
        new User("Active", "cj123", "c.j.williams@gmail.com", "C.J.", "Williams", "555-555");
    USER_MOCKED.setRoles(roles);
    USER_MOCKED.setPassword(UUID.randomUUID().toString());

    String updateRequest =
        "{"
            + "   \"username\":\"Awesome\", "
            + "   \"firstName\": \"Awesome\", "
            + "   \"lastName\": \"Testuser\", "
            + "   \"email\": \"awesome.testuser@gmail.com\", "
            + "   \"password\": \"123\", "
            + "   \"userStatus\": \"Active\", "
            + "   \"phone\": \"555-222\""
            + "}";
  }

  @Test
  @WithJwtMockUser(
      username = "1",
      roles = {"USER"})
  public void getUser_NotFoundTest() throws Exception {
    Mockito.when(this.userRepository.findById(0L)).thenReturn(null);
    RequestBuilder request =
        MockMvcRequestBuilders.get("/users/testuser").accept(MediaType.APPLICATION_JSON);

    mockMvc.perform(request).andExpect(status().isNotFound());
  }

  @Test
  @WithJwtMockUser(
      username = "1",
      roles = {"USER"})
  public void createUser_UserAlreadyExists() throws Exception {

    Mockito.when(this.userRepository.findById(0L)).thenReturn(Optional.empty());
    Mockito.when(this.userRepository.findUserByUserName("1")).thenReturn(USER_MOCKED);

    String postRequest =
        "{"
            + "   \"username\": \"Awesome\", "
            + "   \"firstName\": \"Awesome\", "
            + "   \"lastName\": \"Testuser\", "
            + "   \"email\": \"awesome.testuser@gmail.com\", "
            + "   \"password\": \"123\", "
            + "   \"phone\": \"1988-0329\","
            + "   \"userStatus\": \"1234\""
            + "}";

    RequestBuilder request =
        MockMvcRequestBuilders.post("/users")
            .accept(MediaType.APPLICATION_JSON)
            .content(postRequest)
            .contentType(MediaType.APPLICATION_JSON);

    mockMvc.perform(request).andExpect(status().isBadRequest());
  }

  @Test
  @WithJwtMockUser(
      username = "1",
      roles = {"USER"})
  public void createUser_NoEmail() throws Exception {

    String postRequest =
        "{"
            + "   \"username\": \"Awesome\", "
            + "   \"firstName\": \"Awesome\", "
            + "   \"lastName\": \"Testuser\", "
            + "   \"password\": \"123\", "
            + "   \"phone\": \"1988-0329\","
            + "   \"userStatus\": \"1234\""
            + "}";

    RequestBuilder request =
        MockMvcRequestBuilders.post("/users")
            .accept(MediaType.APPLICATION_JSON)
            .content(postRequest)
            .contentType(MediaType.APPLICATION_JSON);

    mockMvc.perform(request).andExpect(status().isBadRequest());
  }

  @Test
  @WithJwtMockUser(
      username = "1",
      roles = {"USER"})
  public void createUser_TokenUserNotFound() throws Exception {

    String postRequest =
        "{"
            + "   \"username\": \"Awesome\", "
            + "   \"firstName\": \"Awesome\", "
            + "   \"lastName\": \"Testuser\", "
            + "   \"email\": \"awesome.testuser@gmail.com\", "
            + "   \"password\": \"123\", "
            + "   \"phone\": \"1988-0329\","
            + "   \"userStatus\": \"1234\""
            + "}";

    Mockito.when(this.userRepository.findById(0L)).thenReturn(null);

    RequestBuilder request =
        MockMvcRequestBuilders.post("/users")
            .accept(MediaType.APPLICATION_JSON)
            .content(postRequest)
            .contentType(MediaType.APPLICATION_JSON);

    mockMvc.perform(request).andExpect(status().isBadRequest());
  }

  @Test
  @WithJwtMockUser(
      username = "1",
      roles = {"USER"})
  public void getUserByUsername_NotFound() throws Exception {

    Mockito.when(this.userRepository.findUserByUserName(Mockito.anyString())).thenReturn(null);

    RequestBuilder request = MockMvcRequestBuilders.get("/users/" + USER_MOCKED.getUserName());

    mockMvc.perform(request).andExpect(status().isNotFound());
  }

  @Test
  @WithJwtMockUser(
          username = "1",
          roles = {"USER"})
  public void getUserByUsername_Ok() throws Exception {

    Mockito.when(this.userRepository.findUserByUserName(Mockito.anyString())).thenReturn(USER_MOCKED);

    RequestBuilder request = MockMvcRequestBuilders.get("/users/" + USER_MOCKED.getUserName());

    mockMvc.perform(request).andExpect(status().isOk());
  }


  @Test
  @WithJwtMockUser(
          username = "1",
          roles = {"USER"})
  public void updateUser_NotFound() throws Exception {

    String putRequest =
            "{"
                    + "   \"username\": \"Awesome\", "
                    + "   \"firstName\": \"Awesome\", "
                    + "   \"lastName\": \"Testuser\", "
                    + "   \"email\": \"awesome.testuser@gmail.com\", "
                    + "   \"password\": \"123\", "
                    + "   \"phone\": \"1988-0329\","
                    + "   \"userStatus\": \"1234\""
                    + "}";

    Mockito.when(this.userRepository.findUserByUserName(Mockito.anyString())).thenReturn(null);

    RequestBuilder request = MockMvcRequestBuilders.put("/users/" + USER_MOCKED.getUserName())
            .accept(MediaType.APPLICATION_JSON)
            .content(putRequest)
            .contentType(MediaType.APPLICATION_JSON);

    mockMvc.perform(request).andExpect(status().isNotFound());
  }

  @Test
  @WithJwtMockUser(
          username = "1",
          roles = {"USER"})
  public void updateUser_Ok() throws Exception {

    String putRequest =
            "{"
                    + "   \"username\": \"Awesome\", "
                    + "   \"firstName\": \"Awesome\", "
                    + "   \"lastName\": \"Testuser\", "
                    + "   \"email\": \"awesome.testuser@gmail.com\", "
                    + "   \"password\": \"123\", "
                    + "   \"phone\": \"1988-0329\","
                    + "   \"userStatus\": \"1234\""
                    + "}";

    Mockito.when(this.userRepository.findUserByUserName(Mockito.anyString())).thenReturn(USER_MOCKED);
    Mockito.when(this.userRepository.save(USER_MOCKED)).thenReturn(USER_MOCKED);

    RequestBuilder request = MockMvcRequestBuilders.put("/users/" + USER_MOCKED.getUserName())
            .accept(MediaType.APPLICATION_JSON)
            .content(putRequest)
            .contentType(MediaType.APPLICATION_JSON);

    mockMvc.perform(request).andExpect(status().isOk());
  }

  @Test
  @WithJwtMockUser(
          username = "1",
          roles = {"USER"})
  public void deleteUser_NotFound() throws Exception {

    Mockito.when(this.userRepository.findUserByUserName(Mockito.anyString())).thenReturn(null);

    RequestBuilder request = MockMvcRequestBuilders.delete("/users/" + USER_MOCKED.getUserName());

    mockMvc.perform(request).andExpect(status().isNotFound());
  }

  @Test
  @WithJwtMockUser(
          username = "1",
          roles = {"USER"})
  public void deleteUser_Ok() throws Exception {

    Mockito.when(this.userRepository.findUserByUserName(Mockito.anyString())).thenReturn(USER_MOCKED);
    Mockito.when(this.userRepository.save(USER_MOCKED)).thenReturn(Mockito.any());

    RequestBuilder request = MockMvcRequestBuilders.delete("/users/" + USER_MOCKED.getUserName());

    mockMvc.perform(request).andExpect(status().isOk());
  }
}
