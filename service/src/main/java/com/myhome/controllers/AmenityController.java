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
 * TODO
 */
@RestController
@Slf4j
@RequiredArgsConstructor
public class AmenityController implements AmenitiesApi {

  private final AmenityService amenitySDJpaService;
  private final AmenityApiMapper amenityApiMapper;

  /**
   * retrieves amenity details from the database using `amenitySDJpaService`, maps them
   * to `AmenityDetailsResponse` objects using `amenityApiMapper`, and returns a
   * `ResponseEntity` object with a status of `OK` or an alternative status if the
   * amenity ID is not found.
   * 
   * @param amenityId identifier of an amenity that is being requested by the user.
   * 
   * 	- `amenityId`: A string representing the unique identifier for an amenity.
   * 
   * @returns a `ResponseEntity` object containing the details of the specified amenity.
   * 
   * 	- `ResponseEntity<GetAmenityDetailsResponse>`: This is a generic type that
   * represents an entity containing a response to the `getAmenityDetails` request. The
   * `ResponseEntity` class provides a convenient way to handle both successful and
   * failed responses in a single method call.
   * 	- `getAmenityDetails(String amenityId)`: This is the method that is being overridden,
   * which takes an `amenityId` parameter and returns a `ResponseEntity` object containing
   * the details of the requested amenity.
   * 	- `amenitySDJpaService.getAmenityDetails(amenityId)`: This is a call to the
   * `amenitySDJpaService` class's `getAmenityDetails` method, which retrieves the
   * details of the specified amenity from the database.
   * 	- `amenityApiMapper.amenityToAmenityDetailsResponse(Amenity amenity)`: This is a
   * method that maps an `Amenity` object to an `AmenityDetailsResponse` object, which
   * contains additional information about the amenity such as its name, description,
   * and location.
   * 	- `map(ResponseEntity::ok)`: This line of code calls the `map` method on the
   * `ResponseEntity` object, which checks if the response status is 200 (OK) and returns
   * a `ResponseEntity` object with the status set to OK if it is.
   * 	- `orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());`: This line of
   * code provides an alternative way to handle failed responses. If the response status
   * is not 200 (OK), it will create a new `ResponseEntity` object with a status of
   * HTTP_NOT_FOUND and build it using the `build` method.
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
   * retrieves a list of amenities from the database and maps them to a set of
   * `GetAmenityDetailsResponse` objects for further processing.
   * 
   * @param communityId ID of the community whose amenities are to be listed.
   * 
   * 	- `communityId`: A string representing the unique identifier for a community.
   * 	- Length: Exactly 20 characters.
   * 	- Data type: String.
   * 
   * @returns a set of `GetAmenityDetailsResponse` objects containing the details of
   * all amenities associated with the specified community.
   * 
   * 	- `ResponseEntity`: This is an instance of `ResponseEntity`, which is a class
   * that represents a response entity in Spring WebFlux. It contains an `OK` status
   * code and a body containing the list of amenities.
   * 	- `Set<GetAmenityDetailsResponse>`: This is a set of `GetAmenityDetailsResponse`
   * objects, which are the result of mapping the list of amenities returned by the
   * `listAllAmenities` function to the desired response format using the `amenityApiMapper`.
   * Each element in the set contains details about a particular amenity, such as its
   * name, type, and location.
   * 	- `amenitySDJpaService`: This is an instance of `Amenity SD Jpa Service`, which
   * is responsible for accessing and manipulating data related to amenities in the
   * database using JPA (Java Persistence API).
   * 	- `communityId`: This is a string parameter passed to the function, representing
   * the community ID for which the list of amenities is being retrieved.
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
   * adds amenities to a community through the creation of a new amenity list and its
   * association with the specified community ID using the `amenitySDJpaService`.
   * 
   * @param communityId identifier of the community to which the amenities are being added.
   * 
   * 	- `communityId`: A string representing the ID of a community. It is used as a
   * parameter in the function and is also passed to the `amenitySDJpaService` for
   * creating amenities.
   * 
   * @param request AddAmenityRequest object containing the amenities to be added to
   * the community, which is used by the `amenitySDJpaService` to create the new amenities
   * in the database.
   * 
   * 	- `communityId`: A string representing the ID of the community to which the
   * amenities will be added.
   * 	- `request.getAmenities()`: An array of objects containing information about the
   * amenities to be added, including their names and types.
   * 
   * @returns a `ResponseEntity` object representing a successful addition of amenities
   * to a community.
   * 
   * 	- `ResponseEntity<AddAmenityResponse>`: This is the type of the returned entity,
   * which contains an `amenities` field that is a list of amenities.
   * 	- `ok`: This is a property of the returned entity, indicating whether the operation
   * was successful or not. It is set to `true` if the operation succeeded and `false`
   * otherwise.
   * 	- `notFound`: This is another property of the returned entity, indicating whether
   * the community with the given ID was found or not. If the community was not found,
   * this property is set to a `ResponseEntity` object with a status code of 404.
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
   * deletes an amenity from the database based on its ID, returning a response entity
   * indicating whether the operation was successful or not.
   * 
   * @param amenityId ID of the amenity to be deleted.
   * 
   * 	- `amenityId`: A string representing the amenity ID to be deleted.
   * 
   * @returns a `ResponseEntity` object with a status code of either `NO_CONTENT` or
   * `NOT_FOUND`, depending on whether the amenity was successfully deleted or not.
   * 
   * 	- `HttpStatus.NO_CONTENT`: This status code indicates that the request was
   * successful and the amenity was deleted.
   * 	- `HttpStatus.NOT_FOUND`: This status code indicates that the amenity could not
   * be found or was not deleted.
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
   * updates an amenity record using the provided `UpdateAmenityRequest`. If successful,
   * it returns a `ResponseEntity` with a `HttpStatus.NO_CONTENT`. Otherwise, it returns
   * a `ResponseEntity` with a `HttpStatus.NOT_FOUND`.
   * 
   * @param amenityId ID of the amenity being updated.
   * 
   * 	- `amenityId`: This is the primary key for an amenity, representing a unique
   * identifier for the amenity.
   * 
   * @param request UpdateAmenityRequest object containing the details of an amenity
   * to be updated, which is mapped to an AmenityDto object through the amenityApiMapper
   * method before being passed to the amenitySDJpaService for update operation.
   * 
   * 	- `@Valid`: Indicates that the input request body must be valid according to the
   * specified schema.
   * 	- `@RequestBody`: Represents the request body as a single entity, indicating that
   * it should be deserialized and used as the request body.
   * 	- `UpdateAmenityRequest`: This class represents the request body, which contains
   * the details of an amenity to be updated.
   * 
   * @returns a `ResponseEntity` with a status of either `NO_CONTENT` or `NOT_FOUND`,
   * depending on whether the amenity was updated successfully.
   * 
   * 	- The `HttpStatus` field indicates whether the operation was successful (NO_CONTENT)
   * or not (NOT_FOUND).
   * 	- The `ResponseEntity` object itself represents a response message to the client,
   * which includes the status code, headers, and body.
   * 	- The `build()` method is used to create a new ResponseEntity instance with the
   * specified properties.
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
