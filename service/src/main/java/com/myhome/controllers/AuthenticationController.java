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
 * Implements the AuthenticationApi interface and creates HTTP headers for logging
 * in users based on provided authentication data. The class has one method,
 * createLoginHeaders, which takes an AuthenticationData object as input and returns
 * a set of HTTP headers containing the user ID and JWT token for authentication purposes.
 */
@RequiredArgsConstructor
@RestController
public class AuthenticationController implements AuthenticationApi {

  private final AuthenticationService authenticationService;

  /**
   * Creates an HTTP headers object containing user ID and JWT token for authentication
   * purposes.
   * 
   * @param authenticationData user's login information, providing the user ID and JWT
   * token required for authentication.
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
