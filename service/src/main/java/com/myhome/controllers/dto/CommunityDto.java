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
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a data transfer object for a community, encapsulating its attributes
 * and relationships with other entities.
 *
 * - id (Long): is a unique identifier.
 *
 * - communityId (String): represents a string identifier for a community.
 *
 * - name (String): represents the name of a community.
 *
 * - district (String): represents a string value.
 *
 * - admins (Set<UserDto>): stores a collection of UserDto objects.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CommunityDto {
  private Long id;
  private String communityId;
  private String name;
  private String district;
  private Set<UserDto> admins;
}
