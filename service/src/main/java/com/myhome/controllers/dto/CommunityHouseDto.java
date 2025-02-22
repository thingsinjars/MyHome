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

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a data transfer object for community house information, likely used for
 * data exchange between layers of an application.
 *
 * - houseId (String): is a string representing the identifier of a house.
 *
 * - name (String): Represents the name of a community house.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommunityHouseDto {
  private String houseId;
  private String name;
}
