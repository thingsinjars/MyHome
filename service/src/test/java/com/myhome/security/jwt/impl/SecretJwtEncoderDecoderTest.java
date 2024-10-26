/*
 * Copyright 2020 Prathab Murugan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.myhome.security.jwt.impl;

import com.myhome.security.jwt.AppJwt;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.WeakKeyException;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Contains unit tests for a SecretJwtEncoderDecoder class, verifying its methods for
 * encoding and decoding JSON Web Tokens (JWTs).
 */
class SecretJwtEncoderDecoderTest {
  private static final String TEST_USER_ID = "test-user-id";

  private static final String EXPIRED_JWT = "eyJhbGciOiJIUzUxMiJ9."
      + "eyJzdWIiOiJ0ZXN0LXVzZXItaWQiLCJleHAiOjE1OTYwOTg4MDF9."
      + "jnvLiLzobwW2XKz0iuNHZu3W_XO3FNDJoDySxQv_9oUsTPGPcy83_9ETMZRsUBLB9YzkZ0ZtSfP05g4RVKuFhg";

  private static final String INVALID_SECRET = "secret";
  private static final String VALID_SECRET = "secretsecretsecretsecretsecretsecretsecretsecret"
      + "secretsecretsecretsecretsecretsecretsecretsecret"
      + "secretsecretsecretsecretsecretsecretsecretsecret"
      + "secretsecretsecretsecretsecretsecretsecretsecret"
      + "secretsecretsecretsecretsecretsecretsecretsecret";

  /**
   * Tests the successful encoding of a JWT with a secret key. It creates a JWT object
   * with a user ID and an expiration time, then uses a secret key to encode it, verifying
   * that the encoded token is not null.
   */
  @Test
  void jwtEncodeSuccess() {
    // given
    SecretJwtEncoderDecoder jwtEncoderDecoder = new SecretJwtEncoderDecoder();

    // when
    AppJwt appJwt = AppJwt.builder().expiration(LocalDateTime.now()).userId(TEST_USER_ID).build();

    // then
    Assertions.assertNotNull(jwtEncoderDecoder.encode(appJwt, VALID_SECRET));
  }

  /**
   * Tests that a `WeakKeyException` is thrown when encoding a JWT with an invalid
   * secret key. It uses the `SecretJwtEncoderDecoder` class to encode the JWT and
   * asserts that an exception is thrown. The test uses a mock `AppJwt` object with a
   * specified expiration time and user ID.
   */
  @Test
  void jwtEncodeFailWithException() {
    // given
    SecretJwtEncoderDecoder jwtEncoderDecoder = new SecretJwtEncoderDecoder();
    AppJwt appJwt = AppJwt.builder().expiration(LocalDateTime.now()).userId(TEST_USER_ID).build();

    // when and then
    Assertions.assertThrows(WeakKeyException.class,
        () -> jwtEncoderDecoder.encode(appJwt, INVALID_SECRET));
  }

  /**
   * Verifies the successful decoding of a JSON Web Token (JWT). It encodes a token
   * with a secret, decodes it with the same secret, and checks for a non-null decoded
   * token with the expected user ID and expiration.
   */
  @Test
  void jwtDecodeSuccess() {
    // given
    SecretJwtEncoderDecoder jwtEncoderDecoder = new SecretJwtEncoderDecoder();
    AppJwt appJwt =
        AppJwt.builder().userId(TEST_USER_ID).expiration(LocalDateTime.now().plusHours(1)).build();
    String encodedJwt = jwtEncoderDecoder.encode(appJwt, VALID_SECRET);

    // when
    AppJwt decodedJwt = jwtEncoderDecoder.decode(encodedJwt, VALID_SECRET);

    // then
    Assertions.assertNotNull(decodedJwt);
    Assertions.assertEquals(decodedJwt.getUserId(), TEST_USER_ID);
    Assertions.assertNotNull(decodedJwt.getExpiration());
  }

  /**
   * Tests the failure of JWT decoding with an expired JWT. It uses the `SecretJwtEncoderDecoder`
   * to attempt to decode an expired JWT using a valid secret, expecting an
   * `ExpiredJwtException` to be thrown.
   */
  @Test
  void jwtDecodeFailWithExpiredJwt() {
    // given
    SecretJwtEncoderDecoder jwtEncoderDecoder = new SecretJwtEncoderDecoder();

    // when and then
    Assertions.assertThrows(ExpiredJwtException.class,
        () -> jwtEncoderDecoder.decode(EXPIRED_JWT, VALID_SECRET));
  }
}