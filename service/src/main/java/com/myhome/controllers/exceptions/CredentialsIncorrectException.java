package com.myhome.controllers.exceptions;

import lombok.extern.slf4j.Slf4j;

/**
 * Represents an exception for incorrect user credentials, logging the affected user
 * ID when instantiated.
 */
@Slf4j
public class CredentialsIncorrectException extends AuthenticationException {
  public CredentialsIncorrectException(String userId) {
    super();
    log.info("Credentials are incorrect for userId: " + userId);
  }
}
