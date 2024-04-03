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
   * decodes a JWT token and extracts the user ID and expiration date, then builds a
   * new `AppJwt` object with the extracted values.
   * 
   * @param encodedJwt JSON Web Token (JWT) that needs to be decoded and converted into
   * an instance of `AppJwt`.
   * 
   * @param secret secret key used for verifying the digital signature of the JWT and
   * decoding its claims.
   * 
   * @returns a new `AppJwt` instance containing the decoded user ID and expiration date.
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
   * generates a new JWT based on the given `jwt` and `secret`. It sets the expiration
   * date to the current instant, signs the token with the specified secret using
   * HMAC-SHA-512 algorithm, and returns the encoded token.
   * 
   * @param jwt JSON Web Token to be encoded, which contains information about the user
   * and the expiration date.
   * 
   * @param secret HMAC-SHA-256 secret key used for signing the JWT.
   * 
   * @returns a compact JWT token containing the user ID and expiration date, signed
   * with HMAC-SHA key for the given secret.
   */
  @Override public String encode(AppJwt jwt, String secret) {
    Date expiration = Date.from(jwt.getExpiration().atZone(ZoneId.systemDefault()).toInstant());
    return Jwts.builder()
        .setSubject(jwt.getUserId())
        .setExpiration(expiration)
        .signWith(Keys.hmacShaKeyFor(secret.getBytes()), SignatureAlgorithm.HS512).compact();
  }
}
























