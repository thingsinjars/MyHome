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
import com.myhome.security.jwt.AppJwtEncoderDecoder;
import java.time.LocalDateTime;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Concrete implementation of {@link AppJwtEncoderDecoder}. Use this only in testing.
 */
@Profile("test")
@Component
public class NoSecretJwtEncoderDecoder implements AppJwtEncoderDecoder {
  private static final String SEPARATOR = "\\+";

  /**
   * Parses a JWT string into an AppJwt object, extracting the user ID and expiration
   * date from the input string, then builds an AppJwt object with these values.
   *
   * @param encodedJwt encoded JWT string to be decoded and parsed into an `AppJwt` object.
   *
   * @param secret secret key used for signature verification, although it is not
   * utilized in the provided code snippet.
   *
   * @returns an AppJwt object containing userId and expiration.
   */
  @Override public AppJwt decode(String encodedJwt, String secret) {
    String[] strings = encodedJwt.split(SEPARATOR);
    return AppJwt.builder().userId(strings[0]).expiration(LocalDateTime.parse(strings[1])).build();
  }

  /**
   * Combines user ID and expiration information from an AppJwt object with a separator,
   * and returns the resulting string. The string is likely used for authentication or
   * authorization purposes.
   *
   * @param jwt an object containing the user ID and expiration details to be encoded.
   *
   * @param secret secret key used for JWT encoding, but it is not used in the provided
   * function.
   *
   * @returns a string concatenating the user ID and expiration with a separator.
   */
  @Override public String encode(AppJwt jwt, String secret) {
    return jwt.getUserId() + SEPARATOR + jwt.getExpiration();
  }
}
