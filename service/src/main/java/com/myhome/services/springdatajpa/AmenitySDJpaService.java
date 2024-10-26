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

package com.myhome.services.springdatajpa;

import com.myhome.controllers.mapper.AmenityApiMapper;
import com.myhome.domain.Amenity;
import com.myhome.domain.Community;
import com.myhome.model.AmenityDto;
import com.myhome.repositories.AmenityRepository;
import com.myhome.repositories.CommunityRepository;
import com.myhome.services.AmenityService;
import com.myhome.services.CommunityService;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Provides data access and manipulation operations for amenities.
 */
@Service
@RequiredArgsConstructor
public class AmenitySDJpaService implements AmenityService {

  private final AmenityRepository amenityRepository;
  private final CommunityRepository communityRepository;
  private final CommunityService communityService;
  private final AmenityApiMapper amenityApiMapper;

  /**
   * Creates a list of amenities for a community, persists them to the database, and
   * returns the created amenities as a list of Dtos. It requires a community ID and a
   * set of amenity Dtos.
   *
   * @param amenities set of amenity objects to be created and associated with a community.
   *
   * Contain a set of `AmenityDto` objects.
   *
   * @param communityId identifier used to retrieve community details from the
   * `communityService` and associate amenities with the corresponding community.
   *
   * @returns an Optional containing a list of created AmenitiesDto objects.
   *
   * The returned output is an `Optional` containing a `List` of `AmenityDto` objects.
   */
  @Override
  public Optional<List<AmenityDto>> createAmenities(Set<AmenityDto> amenities, String communityId) {
    final Optional<Community> community = communityService.getCommunityDetailsById(communityId);
    if (!community.isPresent()) {
      return Optional.empty();
    }
    final List<Amenity> amenitiesWithCommunity = amenities.stream()
        .map(amenityApiMapper::amenityDtoToAmenity)
        .map(amenity -> {
          amenity.setCommunity(community.get());
          return amenity;
        })
        .collect(Collectors.toList());
    final List<AmenityDto> createdAmenities =
        amenityRepository.saveAll(amenitiesWithCommunity).stream()
            .map(amenityApiMapper::amenityToAmenityDto)
            .collect(Collectors.toList());
    return Optional.of(createdAmenities);
  }

  /**
   * Retrieves the details of an amenity based on a given `amenityId` and returns the
   * result as an `Optional` object. The actual retrieval is delegated to the
   * `amenityRepository`. The function is marked as `@Override`, indicating it overrides
   * a method in a superclass.
   *
   * @param amenityId identifier of the amenity for which details are to be retrieved.
   *
   * @returns an Optional instance containing an Amenity object if found, or an empty
   * Optional otherwise.
   */
  @Override
  public Optional<Amenity> getAmenityDetails(String amenityId) {
    return amenityRepository.findByAmenityId(amenityId);
  }

  /**
   * Deletes an amenity from the database by its ID and removes it from the associated
   * community's amenities list. It returns true if the deletion is successful, or false
   * otherwise. The deletion operation is performed on the database using the `amenityRepository`.
   *
   * @param amenityId identifier of the amenity to be deleted from the database.
   *
   * @returns either `true` if the deletion is successful or `false` otherwise.
   */
  @Override
  public boolean deleteAmenity(String amenityId) {
    return amenityRepository.findByAmenityIdWithCommunity(amenityId)
        .map(amenity -> {
          Community community = amenity.getCommunity();
          community.getAmenities().remove(amenity);
          amenityRepository.delete(amenity);
          return true;
        })
        .orElse(false);
  }

  /**
   * Returns a set of amenities associated with a community identified by the given
   * community ID. If no community is found, an empty set is returned. The amenities
   * are retrieved from a repository using a method that fetches communities with their
   * associated amenities.
   *
   * @param communityId identifier for a specific community, used to retrieve its
   * associated amenities.
   *
   * @returns a set of amenities associated with a community, or an empty set if none
   * exist.
   */
  @Override
  public Set<Amenity> listAllAmenities(String communityId) {
    return communityRepository.findByCommunityIdWithAmenities(communityId)
        .map(Community::getAmenities)
        .orElse(new HashSet<>());
  }

  /**
   * Updates an existing amenity in the database with new information from the provided
   * `AmenityDto` object and saves the changes. It retrieves the community associated
   * with the amenity and the community's amenities before updating the amenity.
   *
   * @param updatedAmenity amenity details to be updated, containing the community ID,
   * amenity ID, name, price, and description.
   *
   * Destructure `updatedAmenity` to include `name`, `price`, `description`, and
   * `communityId`, and `amenityId`.
   *
   * @returns a boolean value indicating whether the amenity was successfully updated.
   */
  @Override
  public boolean updateAmenity(AmenityDto updatedAmenity) {
    String amenityId = updatedAmenity.getAmenityId();
    return amenityRepository.findByAmenityId(amenityId)
        .map(amenity -> communityRepository.findByCommunityId(updatedAmenity.getCommunityId())
            .map(community -> {
              Amenity updated = new Amenity();
              updated.setName(updatedAmenity.getName());
              updated.setPrice(updatedAmenity.getPrice());
              updated.setId(amenity.getId());
              updated.setAmenityId(amenityId);
              updated.setDescription(updatedAmenity.getDescription());
              return updated;
            })
            .orElse(null))
        .map(amenityRepository::save).isPresent();
  }
}
