package com.myhome.controllers;

import com.myhome.domain.AuthenticationData;
import com.myhome.model.LoginRequest;
import com.myhome.services.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

public class AuthenticationControllerTest {

  private static final String TEST_ID = "1";
  private static final String TEST_EMAIL = "email@mail.com";
  private static final String TEST_PASSWORD = "password";
  private static final String TOKEN = "token";

  @Mock
  private AuthenticationService authenticationService;
  @InjectMocks
  private AuthenticationController authenticationController;

  /**
   * initializes mock objects using MockitoAnnotations.
   */
  @BeforeEach
  private void init() {
    MockitoAnnotations.initMocks(this);
  }

  /**
   * tests the login functionality of the `AuthenticationController`. It provides a
   * default `LoginRequest`, `AuthenticationData`, and `HttpHeaders`, and asserts that
   * the `ResponseEntity` status code is `OK`, the header size is 2, and the headers
   * match the expected values. Additionally, it verifies that the `authenticationService.login()`
   * method was called with the provided `LoginRequest`.
   */
  @Test
  void loginSuccess() {
    // given
    LoginRequest loginRequest = getDefaultLoginRequest();
    AuthenticationData authenticationData = getDefaultAuthenticationData();
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.add("userId", authenticationData.getUserId());
    httpHeaders.add("token", authenticationData.getJwtToken());
    given(authenticationService.login(loginRequest))
        .willReturn(authenticationData);

    // when
    ResponseEntity<Void> response = authenticationController.login(loginRequest);

    // then
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(response.getHeaders().size(), 2);
    assertEquals(response.getHeaders(), httpHeaders);
    verify(authenticationService).login(loginRequest);
  }

  /**
   * creates a default login request with an email address of TEST_EMAIL and a password
   * of TEST_PASSWORD.
   * 
   * @returns a `LoginRequest` object containing the email address "TEST_EMAIL" and
   * password "TEST_PASSWORD".
   */
  private LoginRequest getDefaultLoginRequest() {
    return new LoginRequest().email(TEST_EMAIL).password(TEST_PASSWORD);
  }

  /**
   * creates a new `AuthenticationData` instance with token `TOKEN` and test ID `TEST_ID`.
   * 
   * @returns an `AuthenticationData` object containing the token and test ID.
   */
  private AuthenticationData getDefaultAuthenticationData() {
    return new AuthenticationData(TOKEN, TEST_ID);
  }
}
























