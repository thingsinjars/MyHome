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
   * retrieves the details of an amenity using a JPA service and maps it to a
   * `GetAmenityDetailsResponse` object.
   * 
   * @param amenityId ID of the amenity for which details are to be retrieved.
   * 
   * 	- ` amenitySDJpaService`: This is an instance of `AmenitySDJpaService`, which is
   * a Java class that provides methods for interacting with the amenities database.
   * 	- `map(amenityApiMapper::amenityToAmenityDetailsResponse)`: This line uses the
   * `map` method to apply a transformation to the result of the `getAmenityDetails`
   * call. The transformation is performed by an instance of `AmenityApiMapper`, which
   * is responsible for mapping the raw data from the database to the desired response
   * format.
   * 	- `map(ResponseEntity::ok)`: This line uses the `map` method again, this time to
   * check if the result of the previous transformation is a `ResponseEntity` with a
   * status code of `HttpStatus.OK`. If it is, the method returns the `ResponseEntity`
   * directly. Otherwise, it creates a new `ResponseEntity` with a status code of `HttpStatus.NOT_FOUND`.
   * 	- `orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());`: This line
   * provides an alternative to the previous `map` method. If the result of the previous
   * transformation is not a `ResponseEntity` with a status code of `HttpStatus.OK`,
   * this line creates a new `ResponseEntity` with a status code of `HttpStatus.NOT_FOUND`
   * and returns it directly.
   * 
   * @returns a `ResponseEntity` object containing an `AmenityDetailsResponse` object.
   * 
   * 	- `ResponseEntity<GetAmenityDetailsResponse>` is an entity that contains a
   * `getAmenityDetailsResponse` field, which is a response to the API request.
   * 	- `getAmenityDetailsResponse` is a class that contains fields for `amenityId`,
   * `name`, `description`, `icon`, and `location`. These fields represent the details
   * of the amenity retrieved from the database.
   * 	- The `map` method is used to transform the `List<Amenity>` returned by the
   * `getAmenityDetails` function into a `ResponseEntity` with an `ok` status code.
   * 	- The `orElse` method is used as a fallback to return a `ResponseEntity` with a
   * `NOT_FOUND` status code if the `map` method does not produce a valid response.
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
   * returns a set of `GetAmenityDetailsResponse` objects containing details of all
   * amenities associated with a given community ID, retrieved from the database using
   * `amenitySDJpaService`, and then mapped to the response set using `amenityApiMapper`.
   * 
   * @param communityId ID of the community whose amenities are to be listed.
   * 
   * 	- `String communityId`: This is the path variable that represents the ID of a
   * community. It is an essential parameter for retrieving amenities in this function.
   * 
   * @returns a set of `GetAmenityDetailsResponse` objects containing information about
   * the amenities.
   * 
   * 	- `ResponseEntity`: This is the generic type of the response entity, which is an
   * object that represents a successful HTTP response with a status code and a body.
   * 	- `Set<GetAmenityDetailsResponse>`: This is the set of `AmenityDetailsResponse`
   * objects that contain the details of each amenity in the community.
   * 	- `amenities`: This is the list of `Amenity` objects that are retrieved from the
   * database using the `listAllAmenities` function.
   * 	- `amenitySDJpaService`: This is the Java Persistence API (JPA) service that is
   * used to retrieve the list of amenities from the database.
   * 	- `GetAmenityDetailsResponse`: This is the type of the objects in the `response`
   * set, which contain the details of each amenity in the community.
   * 
   * The `listAllAmenities` function returns a response entity with a status code of
   * `200 OK` and a body that contains a set of `GetAmenityDetailsResponse` objects,
   * each containing the details of a single amenity in the community.
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
   * adds amenities to a community based on a request body containing the amenities and
   * the community ID. It returns an HTTP ResponseEntity with OK status code if the
   * amenities are added successfully, otherwise it returns NOT FOUND status code.
   * 
   * @param communityId ID of the community to which the amenities will be added.
   * 
   * 	- `communityId`: This is a String variable that represents the identifier of a
   * community. It is passed as a path variable in the HTTP request.
   * 	- `@PathVariable`: This is an annotation that indicates that the value of the
   * `communityId` variable is provided by the URL path and should be extracted from it.
   * 
   * @param request AddAmenityRequest object that contains the amenities to be added
   * to the community, which is passed from the client to the server for processing.
   * 
   * 	- `request.getAmenities()`: This is an array of `AddAmenityRequest.Amenity`
   * objects, representing the amenities to be added to the community. Each `Amenity`
   * object has the following properties:
   * 	+ `name`: The name of the amenity.
   * 	+ `description`: A brief description of the amenity.
   * 	+ `type`: The type of amenity (e.g., "park", "library", etc.).
   * 	- `communityId`: The ID of the community to which the amenities will be added.
   * 
   * @returns a `ResponseEntity` object with a status of `ok` and a body containing the
   * list of added amenities.
   * 
   * 	- `ResponseEntity<AddAmenityResponse>`: This is an instance of the `ResponseEntity`
   * class, which contains a `body` field that represents the result of the API call.
   * In this case, the body is an instance of `AddAmenityResponse`.
   * 	- `AddAmenityResponse`: This class represents the response of the API call,
   * including the list of created amenities. It has a single field called `amenities`,
   * which is a list of `Amenity` objects representing the created amenities.
   * 	- `ok`: This is a Boolean value indicating whether the API call was successful
   * or not. If the call was successful, this field will be set to `true`, otherwise
   * it will be set to `false`.
   * 	- `notFound`: This is an instance of the `ResponseEntity` class, which indicates
   * that the requested community could not be found. It has a single field called
   * `body`, which is an instance of the `ErrorResponse` class representing the error
   * message.
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
   * deletes an amenity from the database using the `amenitySDJpaService`. If successful,
   * it returns a `ResponseEntity` with a status code of `NO_CONTENT`. Otherwise, it
   * returns a `ResponseEntity` with a status code of `NOT_FOUND`.
   * 
   * @param amenityId ID of an amenity to be deleted.
   * 
   * 	- `amenityId`: A String that represents the unique identifier for an amenity to
   * be deleted from the database.
   * 
   * @returns a `ResponseEntity` object with a status code of either `NO_CONTENT` or
   * `NOT_FOUND`, depending on whether the amenity was successfully deleted or not.
   * 
   * 	- `HttpStatus.NO_CONTENT`: This status code indicates that the request was
   * successful and resulted in no content being sent back to the client. It is a common
   * response when the requested resource is successfully deleted.
   * 	- `HttpStatus.NOT_FOUND`: This status code indicates that the requested amenity
   * could not be found. It may be because the amenity ID provided does not match any
   * existing amenity, or there may be some other error in the request.
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
   * updates an amenity in the database based on a request received from the client.
   * It first converts the request to an `AmenityDto` object, then updates the amenity
   * using the `amenitySDJpaService`, and finally returns a response indicating whether
   * the update was successful or not.
   * 
   * @param amenityId ID of the amenity being updated.
   * 
   * 	- `amenityId`: This is a String variable representing the amenity ID being updated.
   * 
   * @param request UpdateAmenityRequest object containing the details of an amenity
   * to be updated, which is converted into an AmenityDto object by the amenityApiMapper
   * before being passed to the amenitySDJpaService for update.
   * 
   * 	- `@Valid`: This annotation indicates that the request body must be validated
   * according to the schema defined in the Java code.
   * 	- `@RequestBody`: This annotation specifies that the request body is a JSON object
   * that contains the request data.
   * 	- `UpdateAmenityRequest` is the class that defines the structure of the request
   * data, which includes properties such as `amenityId`, `name`, and `description`.
   * 
   * @returns a `ResponseEntity` with a status code of either `NO_CONTENT` or `NOT_FOUND`,
   * depending on whether the amenity was updated successfully.
   * 
   * 	- `HttpStatus`: This is an instance of the `HttpStatus` class, which represents
   * the HTTP status code returned by the method. In this case, it can be either
   * `NO_CONTENT` or `NOT_FOUND`.
   * 	- `ResponseEntity`: This is an instance of the `ResponseEntity` class, which
   * represents a response entity with a status code and a body. The body can be either
   * an empty object (`{}`) for `NO_CONTENT` or a `Void` object for `NOT_FOUND`.
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
