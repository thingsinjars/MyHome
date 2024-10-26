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

import com.myhome.security.filters.CommunityAuthorizationFilter;
import com.myhome.security.jwt.AppJwtEncoderDecoder;
import com.myhome.services.CommunityService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.servlet.Filter;

/**
 * Configure Spring Security settings to enable CORS, disable CSRF and frame options,
 * and implement JWT-based authentication with Community authorization filter.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurity extends WebSecurityConfigurerAdapter {
  private final Environment environment;
  private final UserDetailsService userDetailsService;
  private final CommunityService communityService;
  private final PasswordEncoder passwordEncoder;
  private final AppJwtEncoderDecoder appJwtEncoderDecoder;

  /**
   * Disables CSRF and frame options, enables stateless sessions, and adds filters for
   * community and authorization. It also defines authorization rules for specific URLs
   * and methods, allowing public access while requiring authentication for all other
   * requests.
   *
   * @param http configuration of the HTTP security, which is used to configure and
   * customize the security settings for an application.
   *
   * Disable cross-origin resource sharing, enable CSRF protection, and configure frame
   * options are disabled.
   */
  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.cors().and().csrf().disable();
    http.headers().frameOptions().disable();
    http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    http.addFilterAfter(getCommunityFilter(), MyHomeAuthorizationFilter.class);

    http.authorizeRequests()
        .antMatchers(environment.getProperty("api.public.h2console.url.path"))
        .permitAll()
        .antMatchers(environment.getProperty("api.public.actuator.url.path"))
        .permitAll()
        .antMatchers(HttpMethod.POST, environment.getProperty("api.public.registration.url.path"))
        .permitAll()
        .antMatchers(HttpMethod.POST, environment.getProperty("api.public.login.url.path"))
        .permitAll()
        .antMatchers(HttpMethod.OPTIONS, environment.getProperty("api.public.cors.url.path"))
        .permitAll()
        .antMatchers(HttpMethod.GET, environment.getProperty("api.public.confirm-email.url.path"))
        .permitAll()
        .antMatchers(HttpMethod.GET, environment.getProperty("api.public.resend-confirmation-email.url.path"))
        .permitAll()
        .antMatchers(HttpMethod.POST, environment.getProperty("api.public.confirm-email.url.path"))
        .permitAll()
        .antMatchers("/swagger/**")
        .permitAll()
        .anyRequest()
        .authenticated()
        .and()
        .addFilter(new MyHomeAuthorizationFilter(authenticationManager(), environment,
            appJwtEncoderDecoder))
        .addFilterAfter(getCommunityFilter(), MyHomeAuthorizationFilter.class);
  }

  /**
   * Returns an instance of `CommunityAuthorizationFilter` configured with the provided
   * `authenticationManager` and `communityService`. The filter is used to authorize
   * community access. It throws an `Exception` if configuration is invalid.
   *
   * @returns an instance of `CommunityAuthorizationFilter`.
   */
  private Filter getCommunityFilter() throws Exception {
    return new CommunityAuthorizationFilter(authenticationManager(), communityService);
  }

  /**
   * Configures the authentication manager by setting up a user details service and
   * password encoder.
   * It links the user details service with the password encoder for secure password handling.
   * This configuration is typically used in a Spring Security application.
   *
   * @param auth AuthenticationManagerBuilder instance used to configure the authentication
   * mechanism.
   */
  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
  }
}
