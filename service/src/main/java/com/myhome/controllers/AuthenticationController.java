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
 * TODO
 */
@RequiredArgsConstructor
@RestController
public class AuthenticationController implements AuthenticationApi {

  private final AuthenticationService authenticationService;

  /**
   * authenticates a user using the `AuthenticationService`, creates headers with login
   * information, and returns a `ResponseEntity` object with an OK status and built headers.
   * 
   * @param loginRequest login request, which includes the user credentials and other
   * relevant information for authentication.
   * 
   * 	- `LoginRequest`: An object that contains login credentials for authentication purposes.
   * 	- `authenticationService`: A service responsible for authenticating user requests.
   * 
   * @returns a `ResponseEntity` object with an `OK` status and headers generated based
   * on the `AuthenticationData`.
   * 
   * 	- The `ResponseEntity` is an instance of `ResponseEntity` class, which contains
   * the headers and body of the response.
   * 	- The `headers` field is an instance of `HttpHeaders` class, which contains
   * metadata about the response such as status code, caching, and content type.
   * 	- The `build()` method is used to create a new `ResponseEntity` instance with the
   * provided headers.
   * 	- The authenticationData field contains the result of the login operation, including
   * the authenticated user's details.
   */
  @Override
  public ResponseEntity<Void> login(@Valid LoginRequest loginRequest) {
    final AuthenticationData authenticationData = authenticationService.login(loginRequest);
    return ResponseEntity.ok()
        .headers(createLoginHeaders(authenticationData))
        .build();
  }

  /**
   * creates an HTTP headers object containing user ID and JWT token for login
   * authentication based on provided AuthenticationData.
   * 
   * @param authenticationData user's credentials, providing the `User ID` and `JWT
   * Token` necessary for authentication.
   * 
   * 	- `userId`: an integer representing the user ID associated with the JWT token.
   * 	- `token`: a string denoting the JWT token issued by the authentication service.
   * 
   * @returns an HTTP header object containing the user ID and JWT token of the
   * authenticated user.
   * 
   * 	- `httpHeaders`: This is an instance of `HttpHeaders`, which represents the HTTP
   * headers for the login request.
   * 	- `userId`: The value of this property is a String representing the user ID of
   * the authenticated user.
   * 	- `token`: The value of this property is a String representing the JWT token
   * obtained through authentication.
   */
  private HttpHeaders createLoginHeaders(AuthenticationData authenticationData) {
    final HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.add("userId", authenticationData.getUserId());
    httpHeaders.add("token", authenticationData.getJwtToken());
    return httpHeaders;
  }
}
