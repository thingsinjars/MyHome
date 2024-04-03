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

@RequiredArgsConstructor
@RestController
public class AuthenticationController implements AuthenticationApi {

  private final AuthenticationService authenticationService;

  /**
   * authenticates a user using the provided `LoginRequest` and returns an `ResponseEntity`
   * with the result of the authentication process.
   * 
   * @param loginRequest authentication request containing user credentials to verify
   * and authenticate the user.
   * 
   * @returns a `ResponseEntity` object with an `OK` status code and custom headers
   * containing authentication data.
   */
  @Override
  public ResponseEntity<Void> login(@Valid LoginRequest loginRequest) {
    final AuthenticationData authenticationData = authenticationService.login(loginRequest);
    return ResponseEntity.ok()
        .headers(createLoginHeaders(authenticationData))
        .build();
  }

  /**
   * creates an HTTP headers object containing user ID and JWT token for successful
   * login authentication.
   * 
   * @param authenticationData user's login credentials, providing the user ID and JWT
   * token needed for authentication.
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
























