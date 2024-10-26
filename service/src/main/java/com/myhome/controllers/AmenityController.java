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
 * Handles CRUD operations for amenities, providing API endpoints for retrieving,
 * creating, updating, and deleting amenities. It integrates with the AmenityService
 * and AmenityApiMapper to perform these operations.
 */
@RestController
@Slf4j
@RequiredArgsConstructor
public class AmenityController implements AmenitiesApi {

  private final AmenityService amenitySDJpaService;
  private final AmenityApiMapper amenityApiMapper;

  /**
   * Returns a `ResponseEntity` containing the details of an amenity based on the
   * provided `amenityId`. If the amenity is found, it is mapped to a `GetAmenityDetailsResponse`
   * object; otherwise, a `NOT_FOUND` status is returned.
   *
   * @param amenityId identifier for the amenity details to be retrieved.
   *
   * @returns an `AmenityDetailsResponse` object wrapped in a `ResponseEntity` with a
   * 200 status code if found, otherwise a 404 status code.
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
   * Retrieves a set of amenities associated with a specified community, maps them to
   * a set of GetAmenityDetailsResponse objects, and returns a successful HTTP response
   * containing the mapped amenities.
   *
   * @param communityId identifier for a community, used to filter the list of amenities.
   *
   * @returns a ResponseEntity containing a Set of GetAmenityDetailsResponse objects.
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
   * Adds amenities to a community by calling the `createAmenities` method of the
   * `amenitySDJpaService` and returns a `ResponseEntity` containing the added amenities
   * or a 404 response if the operation fails.
   *
   * @param communityId identifier of the community to which an amenity is being added.
   *
   * @param request AddAmenityRequest object containing the amenities to be added to
   * the community, retrieved from the request body.
   *
   * @returns a ResponseEntity containing either an AddAmenityResponse with amenities
   * or a 404 Not Found response.
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
   * Deletes an amenity based on the provided `amenityId`. If the deletion is successful,
   * it returns a `NO_CONTENT` response (204). If the amenity is not found, it returns
   * a `NOT_FOUND` response (404).
   *
   * @param amenityId identifier of the amenity to be deleted.
   *
   * @returns either a 204 (NO_CONTENT) response if the amenity is deleted or a 404
   * (NOT_FOUND) response if it is not.
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
   * Updates an amenity in the database based on the provided `amenityId` and
   * `UpdateAmenityRequest` object, and returns a 204 response if the update is successful,
   * or a 404 response if the amenity is not found.
   *
   * @param amenityId identifier of the amenity to be updated in the database.
   *
   * @param request UpdateAmenityRequest object, which is mapped to an AmenityDto object
   * using the amenityApiMapper.
   *
   * Extract the properties of the `request` object, which is of type `UpdateAmenityRequest`.
   * It likely contains properties such as name, description, and possibly other amenity
   * details.
   *
   * @returns either a 204 No Content response or a 404 Not Found response.
   *
   * The returned output is a `ResponseEntity` object. It contains a status indicating
   * whether the update was successful or not, represented by HTTP status codes.
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
