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

package com.myhome.domain;

import lombok.*;

import java.time.LocalDateTime;
import javax.persistence.*;

/**
 * Represents a booking item for an amenity in a domain model, extending a BaseEntity
 * class.
 *
 * - amenityBookingItemId (String): is a unique string identifier for an AmenityBookingItem.
 *
 * - amenity (Amenity): represents a Many-To-One relationship with an Amenity entity.
 *
 * - bookingStartDate (LocalDateTime): represents the start date of a booking.
 *
 * - bookingEndDate (LocalDateTime): represents the end date of a booking.
 *
 * - bookingUser (User): represents the user who made the booking.
 */
@Entity
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false, of = {"amenityBookingItemId"})
@Getter
@Setter
@With
@NamedEntityGraphs({
    @NamedEntityGraph(
        name = "AmenityBookingItem.amenity",
        attributeNodes = {
            @NamedAttributeNode("amenity"),
        }),
    @NamedEntityGraph(
        name = "AmenityBookingItem.bookingUser",
        attributeNodes = {
            @NamedAttributeNode("bookingUser"),
        })
})

public class AmenityBookingItem extends BaseEntity {

  @Column(nullable = false, unique = true)
  private String amenityBookingItemId;
  @ManyToOne(fetch = FetchType.LAZY)
  private Amenity amenity;
  @Column(nullable = false)
  private LocalDateTime bookingStartDate;
  @Column
  private LocalDateTime bookingEndDate;
  @ManyToOne(fetch = FetchType.LAZY)
  private User bookingUser;
}
