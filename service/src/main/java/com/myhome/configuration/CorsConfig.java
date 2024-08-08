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
 * Configures cross-origin resource sharing (CORS) settings for an application. It
 * allows specific origins to make requests to the server and specifies which headers
 * and methods are allowed. The configuration is applied to all URLs ("/**") of the
 * application.
 */
@Configuration
public class CorsConfig {

  @Value("${server.cors.allowedOrigins}")
  private String[] allowedOrigins;

  /**
   * Configures CORS (Cross-Origin Resource Sharing) for a web application, allowing
   * requests from any origin, with any HTTP method and header, and exposing specific
   * headers ("token", "userId"). The configuration enables credential storage for
   * authenticated requests.
   *
   * @returns a configuration for CORS (Cross-Origin Resource Sharing) mapping.
   *
   * The returned object is an instance of `WebMvcConfigurer`. The `addCorsMappings`
   * method specifies the mapping for CORS configuration.
   */
  @Bean
  public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurer() {
      /**
       * Enables cross-origin resource sharing (CORS) for all endpoints. It specifies allowed
       * origins, methods, and headers for incoming requests, as well as exposes specific
       * headers and allows credentials to be sent.
       *
       * @param registry CorsRegistry that is used to configure CORS (Cross-Origin Resource
       * Sharing) settings for the application.
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
