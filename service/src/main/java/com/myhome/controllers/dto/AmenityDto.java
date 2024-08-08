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
 * Represents a data transfer object for storing and transferring amenity-related
 * data with its corresponding attributes and annotations for easier serialization
 * and deserialization.
 *
 * - id (Long): represents a unique identifier for an object of type AmenityDto.
 *
 * - amenityId (String): represents a unique identifier for an amenity in this class.
 *
 * - name (String): in AmenityDto is a string attribute representing an amenity's name.
 *
 * - description (String): is of type String in the AmenityDto class.
 *
 * - price (BigDecimal): represents a numeric value with decimal places, specifically
 * of type BigDecimal, which stores a monetary amount.
 *
 * - communityId (String): represents a string value in an AmenityDto object.
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
