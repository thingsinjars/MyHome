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
 * Is a Spring Boot REST controller that implements the AuthenticationApi interface.
 * It creates HTTP headers for logging in users and returns them in response entities.
 */
@RequiredArgsConstructor
@RestController
public class AuthenticationController implements AuthenticationApi {

  private final AuthenticationService authenticationService;

  /**
   * Creates HTTP headers with user ID and JWT token for login purposes based on provided
   * `AuthenticationData`.
   * 
   * @param authenticationData user details for creating HTTP headers.
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
