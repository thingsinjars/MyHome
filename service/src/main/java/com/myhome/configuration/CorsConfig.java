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
 * Is a configuration class that enables Cross-Origin Resource Sharing (CORS) for a
 * Spring-based web application. It configures CORS settings for all mappings to allow
 * cross-origin requests from specific origins, methods, and headers. The class
 * provides a way to expose certain headers in the response.
 */
@Configuration
public class CorsConfig {

  @Value("${server.cors.allowedOrigins}")
  private String[] allowedOrigins;

  /**
   * Configures CORS (Cross-Origin Resource Sharing) for a web application. It enables
   * CORS on all resources by mapping all URLs (`/**`) and specifying allowed origins,
   * methods, headers, and exposed headers, allowing cross-origin requests with credentials
   * enabled.
   * 
   * @returns a configuration for cross-origin resource sharing.
   * 
   * Configure cors mappings for all URLs ("/**") with allowed origins, methods, and headers.
   */
  @Bean
  public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurer() {
      /**
       * Enables CORS (Cross-Origin Resource Sharing) for all resources by mapping "*" to
       * "/**". It allows requests from specified origins, uses all HTTP methods, and accepts
       * all headers. Additionally, it exposes specific headers and allows credentials.
       * 
       * @param registry registry of CORS mappings, which is used to add new mappings for
       * specifying allowed origins, methods, headers, and credentials for cross-origin
       * resource sharing (CORS).
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
