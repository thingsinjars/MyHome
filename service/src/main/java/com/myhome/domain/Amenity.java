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

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
import javax.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.With;

/**
 * Represents an entity in a database that stores information about a specific amenity,
 * likely associated with a community or house.
 *
 * - amenityId (String): is a unique string identifier for an Amenity entity.
 *
 * - name (String): is a non-nullable string field.
 *
 * - description (String): stores the description of an amenity.
 *
 * - price (BigDecimal): is a non-nullable BigDecimal column.
 *
 * - community (Community): represents a many-to-one relationship with a Community entity.
 *
 * - communityHouse (CommunityHouse): represents a reference to a CommunityHouse entity.
 *
 * - bookingItems (Set<AmenityBookingItem>): represents a collection of AmenityBookingItem
 * objects.
 */
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@With
@NamedEntityGraphs({
    @NamedEntityGraph(
        name = "Amenity.community",
        attributeNodes = {
            @NamedAttributeNode("community"),
        }
    ),
    @NamedEntityGraph(
        name = "Amenity.bookingItems",
        attributeNodes = {
            @NamedAttributeNode("bookingItems"),
        }
    )
})

public class Amenity extends BaseEntity {
  @Column(nullable = false, unique = true)
  private String amenityId;
  @Column(nullable = false)
  private String name;
  @Column(nullable = false)
  private String description;
  @Column(nullable = false)
  private BigDecimal price;
  @ManyToOne(fetch = FetchType.LAZY)
  private Community community;
  @ManyToOne
  private CommunityHouse communityHouse;
  @ToString.Exclude
  @OneToMany(fetch = FetchType.LAZY, mappedBy = "amenity")
  private Set<AmenityBookingItem> bookingItems = new HashSet<>();
}
