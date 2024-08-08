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

package com.myhome.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Enables Cross-Origin Resource Sharing (CORS) configuration for a web application.
 * It allows specifying allowed origins and exposed headers through properties files.
 * The configuration provides a set of default values for CORS settings, such as
 * allowing all methods, headers, and credentials.
 */
@Configuration
public class CorsConfig {

  @Value("${server.cors.allowedOrigins}")
  private String[] allowedOrigins;

  /**
   * Configures CORS (Cross-Origin Resource Sharing) for a Spring Boot application,
   * allowing requests from any origin to access resources at any path (`/**`). It
   * allows all HTTP methods and headers, and exposes specific headers (`token`, `userId`)
   * with credentials enabled.
   *
   * @returns a configuration for CORS (Cross-Origin Resource Sharing).
   */
  @Bean
  public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurer() {
      /**
       * Configures CORS (Cross-Origin Resource Sharing) for a web application by defining
       * allowed origins, methods, headers, and exposed headers. It sets up cross-origin
       * requests from any origin to any method with any header, exposing specific headers
       * and allowing credentials.
       *
       * @param registry configuration object used to register new Cors mappings with the
       * Spring MVC framework.
       */
      @Override
      public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            .allowedOrigins(allowedOrigins)
            .allowedMethods("*")
            .allowedHeaders("*")
            .exposedHeaders("token", "userId")
            .allowCredentials(true);
      }
    };
  }
}
