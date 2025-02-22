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

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

/**
 * Represents a data transfer object for an amenity with properties such as id, name,
 * and price.
 *
 * - id (Long): is a Long representing an identifier.
 *
 * - amenityId (String): is a String.
 *
 * - name (String): represents the name of an amenity.
 *
 * - description (String): represents a text description of the amenity.
 *
 * - price (BigDecimal): is a field of type BigDecimal.
 *
 * - communityId (String): stores a community identifier.
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@With
@Data
public class AmenityDto {
  private Long id;
  private String amenityId;
  private String name;
  private String description;
  private BigDecimal price;
  private String communityId;
}
