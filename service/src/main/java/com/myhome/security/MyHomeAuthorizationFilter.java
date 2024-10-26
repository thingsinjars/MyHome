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

package com.myhome.security;

import com.myhome.security.jwt.AppJwt;
import com.myhome.security.jwt.AppJwtEncoderDecoder;
import java.io.IOException;
import java.util.Collections;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

/**
 * Extends the BasicAuthenticationFilter class to implement custom authentication logic.
 */
public class MyHomeAuthorizationFilter extends BasicAuthenticationFilter {

  private final Environment environment;
  private final AppJwtEncoderDecoder appJwtEncoderDecoder;

  public MyHomeAuthorizationFilter(
      AuthenticationManager authenticationManager,
      Environment environment,
      AppJwtEncoderDecoder appJwtEncoderDecoder) {
    super(authenticationManager);
    this.environment = environment;
    this.appJwtEncoderDecoder = appJwtEncoderDecoder;
  }

  /**
   * Verifies the presence of an authorization token in the HTTP request headers and
   * authenticates the user if the token is valid. If authentication is successful, it
   * sets the authentication context. Otherwise, it allows the request to proceed.
   *
   * @param request HTTP request being filtered, providing access to request headers
   * and other attributes.
   *
   * Passed as an argument to the function, the `HttpServletRequest` object contains
   * main properties such as method, path, headers, and parameters.
   * It holds information about the HTTP request, including the request method, URI,
   * and headers.
   * The `request` object also contains a collection of parameters.
   *
   * @param response HTTP response that the filter will write to.
   *
   * Contain a `setStatus` method to set the HTTP status code,
   * a `setHeader` method to set HTTP headers,
   * an `addHeader` method to add HTTP headers.
   *
   * @param chain sequence of filters that will be executed after the current filter,
   * allowing the request to proceed through the filter chain.
   *
   * Destructure is not applied here as `chain` is a single object.
   * It is an instance of `FilterChain` class, which implements `Filter` interface.
   */
  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain chain) throws IOException, ServletException {
    String authHeaderName = environment.getProperty("authorization.token.header.name");
    String authHeaderPrefix = environment.getProperty("authorization.token.header.prefix");

    String authHeader = request.getHeader(authHeaderName);
    if (authHeader == null || !authHeader.startsWith(authHeaderPrefix)) {
      chain.doFilter(request, response);
      return;
    }

    UsernamePasswordAuthenticationToken authentication = getAuthentication(request);
    SecurityContextHolder.getContext().setAuthentication(authentication);
    chain.doFilter(request, response);
  }

  /**
   * Extracts authentication information from an HTTP request header, decodes a JWT
   * token, and returns a `UsernamePasswordAuthenticationToken` if the token is valid
   * and contains a user ID.
   *
   * @param request HTTP request object from which the authentication token is extracted.
   *
   * Get the HTTP headers of the request, specifically the 'Authorization' header, and
   * access the environment variables.
   *
   * @returns a `UsernamePasswordAuthenticationToken` object or `null`.
   *
   * The returned `UsernamePasswordAuthenticationToken` object has three attributes:
   * - `principal`: a `String` representing the user ID.
   * - `credentials`: a `null` value, as credentials are not required for authentication.
   * - `authorities`: an empty `List` of authorities.
   */
  private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
    String authHeader =
        request.getHeader(environment.getProperty("authorization.token.header.name"));
    if (authHeader == null) {
      return null;
    }

    String token =
        authHeader.replace(environment.getProperty("authorization.token.header.prefix"), "");
    AppJwt jwt = appJwtEncoderDecoder.decode(token, environment.getProperty("token.secret"));

    if (jwt.getUserId() == null) {
      return null;
    }
    return new UsernamePasswordAuthenticationToken(jwt.getUserId(), null, Collections.emptyList());
  }
}
