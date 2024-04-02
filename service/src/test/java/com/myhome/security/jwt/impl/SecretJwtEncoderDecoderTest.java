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
   * tests whether a JWT encoder-decoder can successfully encode an AppJwt object using
   * a valid secret key.
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
   * tests the behavior of the `SecretJwtEncoderDecoder` class when an invalid secret
   * key is provided during encoding.
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
   * tests the successful decoding of a JWT token using a provided encoder and secret.
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
   * tests whether an exception is thrown when an expired JWT is passed to the `decode()`
   * method of a `SecretJwtEncoderDecoder` instance.
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





















