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
 * Is a Spring REST controller that implements the AuthenticationApi interface. It
 * provides a login method that takes a valid LoginRequest object as input and returns
 * a ResponseEntity with the user ID and JWT token in the HTTP headers. The class
 * also includes a private method for creating the HTTP headers based on the returned
 * AuthenticationData object.
 */
@RequiredArgsConstructor
@RestController
public class AuthenticationController implements AuthenticationApi {

  private final AuthenticationService authenticationService;

  /**
   * Authenticates a user by calling the `loginService` and returning an `ResponseEntity`
   * with the authentication data.
   * 
   * @param loginRequest user's login details, containing the username and password for
   * authentication by the `authenticationService`.
   * 
   * @returns a `ResponseEntity` object with an `OK` status and headers containing
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
   * Creates HTTP headers with user ID and JWT token for login purposes based on input
   * `AuthenticationData`.
   * 
   * @param authenticationData user's login information, providing the user ID and JWT
   * token for authentication purposes.
   * 
   * @returns a set of HTTP headers containing the user ID and JWT token for authentication
   * purposes.
   */
  private HttpHeaders createLoginHeaders(AuthenticationData authenticationData) {
    final HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.add("userId", authenticationData.getUserId());
    httpHeaders.add("token", authenticationData.getJwtToken());
    return httpHeaders;
  }
}
