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
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.time.ZoneId;
import java.util.Date;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Concrete implementation of {@link AppJwtEncoderDecoder}.
 */
@Component
@Profile("default")
public class SecretJwtEncoderDecoder implements AppJwtEncoderDecoder {

  /**
   * Verifies a JSON Web Token (JWT) using a secret key, extracts user ID and expiration
   * date, and returns an `AppJwt` object with the decoded information.
   *
   * @param encodedJwt base64 encoded JSON Web Token (JWT) to be decoded and its contents
   * extracted.
   *
   * @param secret secret key used for HMAC SHA verification of the JWT signature.
   *
   * @returns an instance of `AppJwt` containing the decoded user ID and expiration date.
   *
   * The returned output is an `AppJwt` object, which has the following attributes:
   * - `userId`: a string representing the user's ID
   * - `expiration`: a `LocalDateTime` object representing the token expiration date
   * and time.
   */
  @Override public AppJwt decode(String encodedJwt, String secret) {
    Claims claims = Jwts.parserBuilder()
        .setSigningKey(Keys.hmacShaKeyFor(secret.getBytes()))
        .build()
        .parseClaimsJws(encodedJwt)
        .getBody();
    String userId = claims.getSubject();
    Date expiration = claims.getExpiration();
    return AppJwt.builder()
        .userId(userId)
        .expiration(expiration.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
        .build();
  }

  /**
   * Generates a JSON Web Token (JWT) that encodes user information and expiration date.
   * It uses the HMAC SHA-512 algorithm for signing with a secret key.
   * The resulting token is returned as a string.
   *
   * @param jwt AppJwt object from which the expiration date is extracted and used to
   * set the expiration of the JWT.
   *
   * @param secret secret key used for HMAC SHA-512 encryption of the JWT.
   *
   * @returns a compact JSON Web Token (JWT) signed with HS512 algorithm.
   */
  @Override public String encode(AppJwt jwt, String secret) {
    Date expiration = Date.from(jwt.getExpiration().atZone(ZoneId.systemDefault()).toInstant());
    return Jwts.builder()
        .setSubject(jwt.getUserId())
        .setExpiration(expiration)
        .signWith(Keys.hmacShaKeyFor(secret.getBytes()), SignatureAlgorithm.HS512).compact();
  }
}
