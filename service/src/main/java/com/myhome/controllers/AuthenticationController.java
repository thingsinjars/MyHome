package com.myhome.controllers;

import com.myhome.api.AuthenticationApi;
import com.myhome.domain.AuthenticationData;
import com.myhome.model.LoginRequest;
import com.myhome.services.AuthenticationService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

/**
 * Implements an authentication API and handles login requests through its `login()`
 * method. The method takes a valid `LoginRequest` object as input and returns a
 * response entity with the user ID and JWT token in the HTTP headers.
 */
@RequiredArgsConstructor
@RestController
public class AuthenticationController implements AuthenticationApi {

  private final AuthenticationService authenticationService;

  /**
   * Processes a `LoginRequest` and returns an `ResponseEntity` with an `ok` status
   * code, headers created by the `createLoginHeaders` method, and built.
   * 
   * @param loginRequest user authentication request containing the necessary credentials
   * for logging into the application.
   * 
   * @returns a response entity with an OK status and custom headers containing
   * authentication data.
   */
  @Override
  public ResponseEntity<Void> login(@Valid LoginRequest loginRequest) {
    final AuthenticationData authenticationData = authenticationService.login(loginRequest);
    return ResponseEntity.ok()
        .headers(createLoginHeaders(authenticationData))
        .build();
  }

  /**
   * Creates HTTP headers for login purposes, adding a user ID and JWT token obtained
   * from an authentication data object.
   * 
   * @param authenticationData user's login details, providing the user ID and JWT token
   * for authentication purposes.
   * 
   * @returns an HTTP headers object containing the user ID and JWT token of the
   * authenticated user.
   */
  private HttpHeaders createLoginHeaders(AuthenticationData authenticationData) {
    final HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.add("userId", authenticationData.getUserId());
    httpHeaders.add("token", authenticationData.getJwtToken());
    return httpHeaders;
  }
}
