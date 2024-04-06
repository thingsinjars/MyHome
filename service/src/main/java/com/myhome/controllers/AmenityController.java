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
   * retrieves amenity details from the database through the `amenitySDJpaService` and
   * maps them to `AmenityDetailsResponse` using `amenityApiMapper`. It returns a
   * `ResponseEntity` with status code `OK` or an empty response entity if the amenity
   * ID does not exist.
   * 
   * @param amenityId ID of the amenity being requested, which is used to retrieve the
   * corresponding details from the database.
   * 
   * 	- `amenityId`: A String variable representing the amenity ID to be queried.
   * 
   * The `amenitySDJpaService.getAmenityDetails(amenityId)` method is called with the
   * `amenityId` parameter, which is used to retrieve the details of the specified
   * amenity from the database. The `map()` methods are then applied to transform the
   * retrieved data into a `GetAmenityDetailsResponse` object using the `amenityApiMapper`.
   * If the amenity is found in the database, the `map()` method returns an instance
   * of `ResponseEntity` with a status code of `OK` (200). Otherwise, it returns an
   * instance of `ResponseEntity` with a status code of `NOT_FOUND` (404).
   * 
   * @returns a `ResponseEntity` object representing the amenity details response.
   * 
   * 	- `ResponseEntity<GetAmenityDetailsResponse>`: This is the generic type of the
   * output, indicating that it is an entity with a response body of type `GetAmenityDetailsResponse`.
   * 	- `getAmenityDetails(String amenityId)`: This is the method signature, which takes
   * an `amenityId` parameter and returns an `ResponseEntity` object.
   * 	- `.map(amenitySDJpaService::getAmenityDetails)`: This line calls the
   * `getAmenityDetails()` method of the `amenitySDJpaService` object, passing in the
   * `amenityId` parameter as a argument. This method is used to retrieve the details
   * of the amenity with the given `id`.
   * 	- `.map(amenityApiMapper::amenityToAmenityDetailsResponse)`: This line calls the
   * `amenityToAmenityDetailsResponse()` method of the `amenityApiMapper` object, passing
   * in the result of the previous call as a parameter. This method is used to map the
   * retrieved amenity details to a response body of type `GetAmenityDetailsResponse`.
   * 	- `.map(ResponseEntity::ok)`: This line calls the `ok()` method of the `ResponseEntity`
   * object, which returns a new `ResponseEntity` object with a status code of 200 (OK).
   * 	- `orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build())`: This line provides
   * an alternative to the previous mapping, which is to return a `ResponseEntity`
   * object with a status code of 404 (NOT FOUND) if the amenity with the given `id`
   * cannot be found.
   * 
   * In summary, the output of the `getAmenityDetails` function is a `ResponseEntity`
   * object that contains the details of the amenity with the given `id`, and has a
   * status code of 200 (OK) if the amenity exists, or a status code of 404 (NOT FOUND)
   * if the amenity cannot be found.
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
   * retrieves a set of amenities from the database using `amenitySDJpaService`, maps
   * them to `GetAmenityDetailsResponse` objects using `amenityApiMapper`, and returns
   * an `Ok` response entity with the transformed set of `GetAmenityDetailsResponse` objects.
   * 
   * @param communityId unique identifier for a community, which is used to retrieve
   * the amenities associated with that community from the database.
   * 
   * 	- `communityId`: This is a string representing the unique identifier for a
   * community. It is passed as a path variable in the HTTP request.
   * 
   * @returns a set of `GetAmenityDetailsResponse` objects containing the amenities for
   * a given community ID.
   * 
   * 	- `ResponseEntity`: This is the class that represents a successful response to a
   * HTTP request, with an `OK` status code.
   * 	- `Set<GetAmenityDetailsResponse>`: This set contains the list of amenities in
   * detail, which were obtained from the database using the `listAllAmenities` method.
   * 	- `amenitySDJpaService`: This is a Java Persistence API (JPA) service that provides
   * methods for interacting with the amenities data stored in the database.
   * 	- `amenityApiMapper`: This is an API mapper class that maps the amenities retrieved
   * from the database to the `GetAmenityDetailsResponse` set.
   * 
   * In summary, the `listAllAmenities` function returns a successful response with a
   * list of `GetAmenityDetailsResponse` objects containing detailed information about
   * each amenity in the community.
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
   * adds amenities to a community using the `amenitySDJpaService`. It creates an
   * `AddAmenityResponse` object with the added amenities and returns it as a
   * `ResponseEntity` with a status of `ok` or `notFound` depending on the result of
   * the operation.
   * 
   * @param communityId ID of the community to which the amenities will be added.
   * 
   * 	- `communityId`: This is a String variable that represents the unique identifier
   * of a community. It is used to identify the community in which amenities will be added.
   * 	- `@PathVariable`: This annotation indicates that the value of `communityId` is
   * passed as a path variable from the URL.
   * 
   * @param request AddAmenityRequest object containing the amenities to be added to
   * the community, which is used by the `amenitySDJpaService` to create the new amenities
   * in the database.
   * 
   * 	- `communityId`: a string representing the ID of the community to which amenities
   * will be added.
   * 	- `request.getAmenities()`: an array of objects containing the amenities to be
   * added to the community. Each object in the array has properties such as `name`,
   * `description`, and `type`.
   * 
   * @returns a `ResponseEntity` object with a status of `ok` and an `AddAmenityResponse`
   * object containing the created amenities.
   * 
   * 	- `ResponseEntity<AddAmenityResponse>`: This is an entity that contains a response
   * object of type `AddAmenityResponse`.
   * 	- `AddAmenityResponse`: This class represents the response to the add amenities
   * request. It has a single attribute `amenities`, which is a list of amenities added
   * to the community.
   * 	- `map(Function<AddAmenityResponse, ResponseEntity<AddAmenityResponse>>
   * mappingFunction)`: This line uses the `mappingFunction` to map the response object
   * to an instance of `ResponseEntity`. The `mappingFunction` takes the `AddAmenityResponse`
   * object as input and returns a `ResponseEntity` object with a status code of `ok`
   * or `notFound`, depending on whether the amenities were added successfully or not.
   * 	- `orElse(Function<Throwable, ResponseEntity<AddAmenityResponse>> fallbackMappingFunction)`:
   * This line provides a fallback mapping function in case the original mapping function
   * fails. The `fallbackMappingFunction` takes a `Throwable` object as input and returns
   * a `ResponseEntity` object with a status code of `internalServerError`.
   * 
   * Overall, the output of the `addAmenityToCommunity` function is a response entity
   * that contains the result of adding amenities to a community. The response entity
   * has a single attribute, `amenities`, which is a list of added amenities.
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
   * deletes an amenity from the database based on its ID, returning a HTTP status code
   * indicating the result of the operation.
   * 
   * @param amenityId id of the amenity to be deleted.
   * 
   * 	- `String amenityId`: This represents the unique identifier for an amenity in the
   * system. It is a required parameter passed through `@PathVariable`.
   * 
   * @returns a `ResponseEntity` object with a status code of either `NO_CONTENT` or
   * `NOT_FOUND`, depending on whether the amenity was successfully deleted or not.
   * 
   * 	- `HttpStatus.NO_CONTENT`: This status code indicates that the requested resource
   * has been deleted successfully.
   * 	- `HttpStatus.NOT_FOUND`: This status code indicates that the requested amenity
   * could not be found in the database, possibly due to deletion errors or other issues.
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
   * updates an amenity by using the `amenityApiMapper` to convert the request body
   * into a `AmenityDto` object, then passing it to the `amenitySDJpaService` to update
   * the amenity. If the update is successful, a `NO_CONTENT` status code is returned,
   * otherwise a `NOT_FOUND` status code is returned.
   * 
   * @param amenityId ID of the amenity to be updated in the system.
   * 
   * 	- `amenityId`: The id of the amenity being updated.
   * 
   * The function updates the amenity using the `amenitySDJpaService`, and based on the
   * update result, returns a response entity with a HTTP status code of NO_CONTENT or
   * NOT_FOUND.
   * 
   * @param request UpdateAmenityRequest object containing the details of the amenity
   * to be updated, which is used by the method to update the corresponding amenity in
   * the database.
   * 
   * 	- `@Valid`: This annotation is used to indicate that the request body must be
   * valid according to the specified schema.
   * 	- `@RequestBody`: This annotation indicates that the request body should be
   * deserialized and processed as a request entity.
   * 	- `UpdateAmenityRequest`: This class represents the request body, which contains
   * the details of the amenity to be updated.
   * 	- `amenityId`: The ID of the amenity to be updated.
   * 	- `request`: The complete request body containing all the necessary attributes
   * for updating an amenity.
   * 
   * @returns a `ResponseEntity` with a status code of either `NO_CONTENT` or `NOT_FOUND`,
   * depending on whether the amenity was updated successfully.
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
