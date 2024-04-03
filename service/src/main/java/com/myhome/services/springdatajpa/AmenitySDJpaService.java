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

@Service
@RequiredArgsConstructor
public class AmenitySDJpaService implements AmenityService {

  private final AmenityRepository amenityRepository;
  private final CommunityRepository communityRepository;
  private final CommunityService communityService;
  private final AmenityApiMapper amenityApiMapper;

  /**
   * takes a set of amenities and a community ID, maps them to `Amenity` objects, saves
   * them to the database, and returns a list of the created amenities as `AmenityDto`.
   * 
   * @param amenities set of `AmenityDto` objects to be created or updated in the database.
   * 
   * @param communityId community ID for which the amenities are being created, and is
   * used to retrieve the details of the community from the service.
   * 
   * @returns a list of `AmenityDto` objects representing the newly created amenities.
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
   * retrieves the details of an amenity based on its ID.
   * 
   * @param amenityId identifier of the amenity to retrieve details for.
   * 
   * @returns an Optional object containing the details of the specified amenity if
   * found, otherwise empty.
   */
  @Override
  public Optional<Amenity> getAmenityDetails(String amenityId) {
    return amenityRepository.findByAmenityId(amenityId);
  }

  /**
   * deletes an amenity from the database by finding it using its ID, removing it from
   * the community's amenities list, and deleting it from the repository.
   * 
   * @param amenityId ID of an amenity that needs to be deleted.
   * 
   * @returns a boolean value indicating whether the amenity was successfully deleted.
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
   * retrieves a set of amenities associated with a community by using the
   * `communityRepository.findByCommunityIdWithAmenities()` method and maps the community's
   * amenities to a set using the `map()` method. If no amenities are found, it returns
   * an empty set.
   * 
   * @param communityId identifier of the community whose amenities should be listed.
   * 
   * @returns a set of amenities associated with a given community.
   */
  @Override
  public Set<Amenity> listAllAmenities(String communityId) {
    return communityRepository.findByCommunityIdWithAmenities(communityId)
        .map(Community::getAmenities)
        .orElse(new HashSet<>());
  }

  /**
   * updates an amenity in the database by retrieving the existing amenity with the
   * matching ID, updating its name, price, and other properties, and then saving the
   * updated amenity to the database.
   * 
   * @param updatedAmenity updated amenity data to be saved in the repository, including
   * its name, price, id, amenity id, and description.
   * 
   * @returns a boolean value indicating whether the amenity was updated successfully.
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
























