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
 * represents a data transfer object (DTO) for user data, containing fields for user
 * ID, name, email, password, and community membership, as well as flags for email
 * confirmation and password encryption.
 * Fields:
 * 	- id (Long): represents a unique identifier for a user in the system.
 * 	- userId (String): in the UserDto class represents a unique identifier for a user.
 * 	- name (String): in UserDto represents the user's personal name.
 * 	- email (String): in UserDto is for storing a user's email address.
 * 	- password (String): in the `UserDto` class is used to store an encrypted password
 * for authentication purposes.
 * 	- encryptedPassword (String): in the UserDto class stores an encrypted version
 * of the user's password.
 * 	- communityIds (Set<String>): in the UserDto class represents a set of strings
 * that contain the identifiers of communities to which the user belongs.
 * 	- emailConfirmed (boolean): indicates whether an email address associated with
 * the user has been confirmed.
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
