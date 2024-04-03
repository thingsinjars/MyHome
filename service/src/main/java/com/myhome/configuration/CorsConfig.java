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

@Configuration
public class CorsConfig {

  @Value("${server.cors.allowedOrigins}")
  private String[] allowedOrigins;

  /**
   * configure CORS settings for a web application by adding mapping to allow cross-origin
   * resource sharing.
   * 
   * @returns a WebMvcConfigurer object that adds CORS mapping for all URLs and allows
   * origins, methods, headers, and credentials.
   */
  @Bean
  public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurer() {
      /**
       * adds CORS mappings to a registry, allowing cross-origin resource sharing by
       * specifying allowed origins, methods, headers, and credentials.
       * 
       * @param registry Cors registry that can be modified to configure cross-origin
       * resource sharing (CORS) settings for the given function.
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
























