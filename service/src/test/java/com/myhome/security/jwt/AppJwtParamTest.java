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

package com.myhome.security.jwt;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

/**
 * Contains a unit test method testParamCreationBuilder that creates an AppJwt instance
 * using a builder pattern and prints it to the console.
 */
class AppJwtParamTest {

  /**
   * Tests the creation of an `AppJwt` object using a builder pattern. It constructs
   * an `AppJwt` instance with a user ID and an expiration time set to the current date
   * and time. The resulting object is then printed to the console.
   */
  @Test
  void testParamCreationBuilder() {
    AppJwt param = AppJwt.builder().userId("test-user-id").expiration(LocalDateTime.now()).build();
    System.out.println(param);
  }
}