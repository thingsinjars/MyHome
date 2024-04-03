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

@RestController
@Slf4j
@RequiredArgsConstructor
public class AmenityController implements AmenitiesApi {

  private final AmenityService amenitySDJpaService;
  private final AmenityApiMapper amenityApiMapper;

  /**
   * retrieves amenity details for a given ID from a Java Persistence API (JPA) service
   * and maps the result to an `AmenityDetailsResponse` object using a mapper. It returns
   * a `ResponseEntity` object with a status code of `OK` or an error message if the
   * amenity is not found.
   * 
   * @param amenityId ID of the amenity to retrieve details for.
   * 
   * @returns an `ResponseEntity` object representing a successful response with the
   * details of the requested amenity.
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
   * retrieves a set of amenities from the database using the `amenitySDJpaService`,
   * and maps them to a set of `GetAmenityDetailsResponse` objects using the
   * `amenityApiMapper`. It then returns a response entity with the mapped `Set<GetAmenityDetailsResponse>`.
   * 
   * @param communityId ID of the community for which the user wants to list all amenities.
   * 
   * @returns a set of `GetAmenityDetailsResponse` objects containing information about
   * the listed amenities.
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
   * creates amenities for a given community by calling the `createAmenities` method
   * of the `amenitySDJpaService`, maps the result to an `AddAmenityResponse`, and
   * returns it as an `ok` ResponseEntity or an error ResponseEntity if any.
   * 
   * @param communityId ID of the community to which the amenities will be added.
   * 
   * @param request AddAmenityRequest object containing the amenities to be added to
   * the community, which is used by the method to create the new amenities in the database.
   * 
   * @returns a `ResponseEntity` object of type `AddAmenityResponse`, containing the
   * list of newly created amenities.
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
   * deletes an amenity based on its ID, returning a HTTP status code indicating the
   * result of the operation.
   * 
   * @param amenityId identity of an amenity that is to be deleted.
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
   * updates an amenity using the provided ID and update request, returning a HTTP
   * NO_CONTENT status code if successful or NOT_FOUND otherwise.
   * 
   * @param amenityId unique identifier of the amenity being updated.
   * 
   * @param request UpdateAmenityRequest object containing the details of the amenity
   * to be updated, which is used by the `amenityApiMapper` to map the request to an
   * AmenityDto object before updating the amenity in the database.
   * 
   * @returns a response entity with a status code of either NO_CONTENT or NOT_FOUND,
   * indicating whether the update was successful or not.
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
























