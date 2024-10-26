package com.myhome.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Represents authentication data with a predefined structure for storing and accessing
 * JWT token and user ID information.
 *
 * - jwtToken (String): represents a JSON Web Token.
 *
 * - userId (String): represents a unique identifier for a user.
 */
@Getter
@RequiredArgsConstructor
public class AuthenticationData {
  private final String jwtToken;
  private final String userId;
}
