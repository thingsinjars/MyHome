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

package com.myhome.controllers;

import com.myhome.api.AmenitiesApi;
import com.myhome.controllers.mapper.AmenityApiMapper;
import com.myhome.domain.Amenity;
import com.myhome.model.AddAmenityRequest;
import com.myhome.model.AddAmenityResponse;
import com.myhome.model.AmenityDto;
import com.myhome.model.GetAmenityDetailsResponse;
import com.myhome.model.UpdateAmenityRequest;
import com.myhome.services.AmenityService;
import java.util.Set;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Is a RESTful API controller that handles requests related to amenities. It provides
 * methods for retrieving all amenities, listing all amenities for a specific community,
 * adding new amenities to a community, deleting an existing amenity, and updating
 * an amenity. The controller uses dependency injection to inject the necessary
 * services (AmenityService and AmenityApiMapper) to perform these operations.
 */
@RestController
@Slf4j
@RequiredArgsConstructor
public class AmenityController implements AmenitiesApi {

  private final AmenityService amenitySDJpaService;
  private final AmenityApiMapper amenityApiMapper;

  /**
   * Retrieves amenity details from a database using a JPA service and maps them to an
   * `AmenityDetailsResponse` object, returning a `ResponseEntity` with a status code
   * indicating the result of the operation.
   * 
   * @param amenityId identifying code of an amenity, which is used to retrieve information
   * about that amenity from the database.
   * 
   * @returns a `ResponseEntity` object containing an `AmenityDetailsResponse` object.
   */
  @Override
  public ResponseEntity<GetAmenityDetailsResponse> getAmenityDetails(
      @PathVariable String amenityId) {
    return amenitySDJpaService.getAmenityDetails(amenityId)
        .map(amenityApiMapper::amenityToAmenityDetailsResponse)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
  }

  /**
   * Retrieves a set of amenities from the database using `JpaService`, and maps them
   * to a set of `GetAmenityDetailsResponse` objects using `Mapper`. It then returns a
   * `ResponseEntity` with the mapped response set.
   * 
   * @param communityId identifier of the community whose amenities are to be listed.
   * 
   * @returns a set of `GetAmenityDetailsResponse` objects containing the details of
   * all amenities for a given community.
   */
  @Override
  public ResponseEntity<Set<GetAmenityDetailsResponse>> listAllAmenities(
      @PathVariable String communityId) {
    Set<Amenity> amenities = amenitySDJpaService.listAllAmenities(communityId);
    Set<GetAmenityDetailsResponse> response =
        amenityApiMapper.amenitiesSetToAmenityDetailsResponseSet(amenities);
    return ResponseEntity.ok(response);
  }

  /**
   * Creates amenities for a given community based on user input and returns an
   * `AddAmenityResponse` object containing the newly created amenities or a `ResponseEntity`
   * indicating that the resource could not be found.
   * 
   * @param communityId ID of the community to which the amenities will be added.
   * 
   * @param request AddAmenityRequest object containing the amenities to be added to
   * the community, which is used by the `amenitySDJpaService` to create the new amenities
   * in the database.
   * 
   * @returns a `ResponseEntity` object representing a successful addition of amenities
   * to a community.
   */
  @Override
  public ResponseEntity<AddAmenityResponse> addAmenityToCommunity(
      @PathVariable String communityId,
      @RequestBody AddAmenityRequest request) {
    return amenitySDJpaService.createAmenities(request.getAmenities(), communityId)
        .map(amenityList -> new AddAmenityResponse().amenities(amenityList))
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  /**
   * Deletes an amenity from the database using the `amenitySDJpaService`. If the amenity
   * is successfully deleted, a `NO_CONTENT` status code is returned; otherwise, a
   * `NOT_FOUND` status code is returned.
   * 
   * @param amenityId ID of the amenity to be deleted.
   * 
   * @returns a `ResponseEntity` object with a status code of either `NO_CONTENT` or
   * `NOT_FOUND`, depending on whether the amenity was successfully deleted or not.
   */
  @Override
  public ResponseEntity deleteAmenity(@PathVariable String amenityId) {
    boolean isAmenityDeleted = amenitySDJpaService.deleteAmenity(amenityId);
    if (isAmenityDeleted) {
      return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    } else {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
  }

  /**
   * Updates an amenity's details based on a request body, and returns a response entity
   * indicating whether the update was successful or not.
   * 
   * @param amenityId ID of the amenity being updated.
   * 
   * @param request UpdateAmenityRequest object that contains the details of the amenity
   * to be updated.
   * 
   * 	- `@Valid`: Indicates that the request body must be validated using the provided
   * bean validation configuration.
   * 	- `@RequestBody`: Represents the request body as a whole, which contains the
   * updates for the amenity.
   * 
   * @returns a response entity with a status code of either `NO_CONTENT` or `NOT_FOUND`,
   * depending on whether the amenity was successfully updated.
   * 
   * 	- `isUpdated`: A boolean value indicating whether the amenity was successfully
   * updated or not.
   * 	- `HttpStatus`: The HTTP status code associated with the response, which can be
   * either `NO_CONTENT` (204) or `NOT_FOUND` (404).
   */
  @Override
  public ResponseEntity<Void> updateAmenity(@PathVariable String amenityId,
      @Valid @RequestBody UpdateAmenityRequest request) {
    AmenityDto amenityDto = amenityApiMapper.updateAmenityRequestToAmenityDto(request);
    amenityDto.setAmenityId(amenityId);
    boolean isUpdated = amenitySDJpaService.updateAmenity(amenityDto);
    if (isUpdated) {
      return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    } else {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
  }
}
