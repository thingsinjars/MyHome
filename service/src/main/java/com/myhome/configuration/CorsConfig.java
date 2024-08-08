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
 * Enables cross-origin resource sharing (CORS) for an application. It specifies
 * allowed origins, methods, headers, and credentials for incoming requests.
 */
@Configuration
public class CorsConfig {

  @Value("${server.cors.allowedOrigins}")
  private String[] allowedOrigins;

  /**
   * Defines a Spring WebMvcConfigurer that enables cross-origin resource sharing (CORS)
   * for all endpoints ("/**") by allowing specific origins, methods, and headers. It
   * also exposes certain headers ("token", "userId") and allows credentials to be
   * included in requests.
   *
   * @returns a configuration for enabling CORS.
   *
   * It is an instance of `WebMvcConfigurer`, and provides mapping configurations for
   * cross-origin requests. The `addCorsMappings` method specifies that all URLs (/***)
   * are allowed to be accessed from any origin, using any HTTP method with any headers,
   * exposing specific headers (token and userId), and allowing credentials.
   */
  @Bean
  public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurer() {
      /**
       * Configures CORS (Cross-Origin Resource Sharing) for a web application. It allows
       * requests from all origins, methods, and headers, exposes specific headers, and
       * enables credentials for cross-origin requests. The configuration is applied to all
       * resources using the `/**` pattern.
       *
       * @param registry configuration registry for CORS (Cross-Origin Resource Sharing)
       * settings, allowing the specification of mapping rules and configurations for
       * cross-origin requests.
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
