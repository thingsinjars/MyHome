package com.myhome.controllers.exceptions;

import lombok.extern.slf4j.Slf4j;

/**
 * Extends the AuthenticationException Class and serves as a custom exception for
 * user not found scenarios.
 */
@Slf4j
public class UserNotFoundException extends AuthenticationException {
  public UserNotFoundException(String userEmail) {
    super();
    log.info("User not found - email: " + userEmail);
  }
}
