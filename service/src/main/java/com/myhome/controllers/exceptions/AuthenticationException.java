package com.myhome.controllers.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Extends the RuntimeException and is used to handle authentication-related errors
 * with a status code of HTTP UNAUTHORIZED.
 *
 * - ERROR_MESSAGE (String): represents a string value indicating the error message
 * for an authentication failure.
 */
@Slf4j
@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class AuthenticationException extends RuntimeException {
  private static final String ERROR_MESSAGE = "Credentials are incorrect or user does not exists";
  public AuthenticationException() {
    super(ERROR_MESSAGE);
  }
}
