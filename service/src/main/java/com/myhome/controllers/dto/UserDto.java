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

package com.myhome.controllers.dto;

import java.util.Set;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a data transfer object for user information, encapsulating relevant
 * attributes and properties in a structured format.
 *
 * - id (Long): is a Long representing a unique identifier.
 *
 * - userId (String): stores a unique identifier for a user.
 *
 * - name (String): Stores a user's name.
 *
 * - email (String): stores a user's email address.
 *
 * - password (String): stores a user password.
 *
 * - encryptedPassword (String): stores an encrypted version of the password.
 *
 * - communityIds (Set<String>): contains a set of community IDs.
 *
 * - emailConfirmed (boolean): indicates whether the user's email address has been confirmed.
 */
@Builder
@Getter
@Setter
public class UserDto {
  private Long id;
  private String userId;
  private String name;
  private String email;
  private String password;
  private String encryptedPassword;
  private Set<String> communityIds;
  private boolean emailConfirmed;
}
