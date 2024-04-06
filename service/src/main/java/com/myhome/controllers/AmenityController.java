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
   * retrieves amenity details from the database using the `amenitySDJpaService`, maps
   * them to `AmenityDetailsResponse` objects using `amenityApiMapper`, and returns a
   * `ResponseEntity` with a status of `OK` or an empty response entity if the amenity
   * ID does not exist.
   * 
   * @param amenityId ID of the amenity for which details are requested.
   * 
   * 	- The input `amenityId` is a string variable that represents the unique identifier
   * for an amenity.
   * 	- The `amenityId` can be used to retrieve specific details about the amenity,
   * such as its name, description, and location.
   * 	- The `amenityId` is passed as a path variable in the URL, which allows the
   * function to retrieve the appropriate details based on the input provided.
   * 
   * @returns an `ResponseEntity` object representing a successful response with an
   * `ok` status and the details of the requested amenity.
   * 
   * 	- `ResponseEntity<GetAmenityDetailsResponse>`: This represents an entity that
   * contains an instance of `GetAmenityDetailsResponse`, which is a class with properties
   * representing the details of an amenity.
   * 	- `getAmenityDetails(String amenityId)`: This method takes a string parameter
   * representing the ID of the amenity for which details are being requested.
   * 	- `amenitySDJpaService.getAmenityDetails(amenityId)`: This line calls the
   * `getAmenityDetails` method of the `amenitySDJpaService` class, which returns an
   * instance of `AmenityDetailsResponse`.
   * 	- `map(Function<AmenityDetailsResponse, GetAmenityDetailsResponse> mapper)`: This
   * line applies a mapping function to the output of the `amenitySDJpaService.getAmenityDetails`
   * method, which converts the output into an instance of `GetAmenityDetailsResponse`.
   * 	- `map(ResponseEntity::ok)`: This line maps the output of the mapping function
   * to a `ResponseEntity` object with a status code of `HttpStatus.OK`.
   * 	- `orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build())`: This line provides
   * an alternative response if the `map` method does not produce a valid output. In
   * this case, it returns a `ResponseEntity` object with a status code of `HttpStatus.NOT_FOUND`.
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
   * retrieves a list of amenities from the database using `amenitySDJpaService`, maps
   * them to `GetAmenityDetailsResponse` set using `amenityApiMapper`, and returns a
   * response entity with the mapped set.
   * 
   * @param communityId ID of the community whose amenities are to be listed.
   * 
   * 	- The variable `communityId` represents a `String` data type, which is the primary
   * key for the `Community` entity in the database.
   * 	- It is passed as an HTTP parameter through the `@PathVariable` annotation,
   * indicating that it should be retrieved from the URL path.
   * 	- The `communityId` variable contains the unique identifier of a community, which
   * is used to retrieve the corresponding amenities from the database.
   * 
   * @returns a set of `GetAmenityDetailsResponse` objects containing the details of
   * all amenities for a given community.
   * 
   * 	- `ResponseEntity`: This is the class that represents an HTTP response entity,
   * which contains a result and additional metadata. In this case, the result is a set
   * of `GetAmenityDetailsResponse` objects.
   * 	- `ok`: This is the HTTP status code indicating that the request was successful.
   * 	- `Set<GetAmenityDetailsResponse>`: This is the set of `GetAmenityDetailsResponse`
   * objects that are contained in the response entity. Each object in the set represents
   * a single amenity, with its details such as name, description, and images.
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
   * adds amenities to a community through JPA service, mapping the response to an
   * `AddAmenityResponse` object and returning it as an `ok` ResponseEntity or a
   * `notFound` ResponseEntity if any errors occurred.
   * 
   * @param communityId unique identifier for the community to which the amenities will
   * be added.
   * 
   * 	- `communityId`: This is a String representing the ID of a community in the
   * application. It serves as the primary key for the community object in the database.
   * 
   * The function first calls the `createAmenities` method of the `amenitySDJpaService`
   * class, passing in the amenities and the community ID as parameters. This method
   * creates a list of `Amenity` objects in the database using the provided amenities
   * and community ID.
   * 
   * Next, the function maps the list of `Amenity` objects to an instance of the
   * `AddAmenityResponse` class, which contains a list of `Amenity` objects representing
   * the added amenities. Finally, the function returns an `ResponseEntity` object with
   * a status code of `ok`, indicating that the operation was successful. If any errors
   * occurred during the process, the function will return an `ResponseEntity` object
   * with a status code of `notFound`, indicating that the community ID could not be
   * found in the database.
   * 
   * @param request AddAmenityRequest object containing the amenities to be added to
   * the community, which is used by the `amenitySDJpaService` to create the new amenities
   * in the community.
   * 
   * 	- `communityId`: A string representing the unique identifier of the community to
   * which the amenities will be added.
   * 	- `requestBody`: The request body contains the list of amenities to be added to
   * the community, represented as a JSON object with properties such as name, description,
   * and type.
   * 
   * @returns a `ResponseEntity` object representing the result of adding an amenity
   * to a community.
   * 
   * 	- `ResponseEntity<AddAmenityResponse>`: This represents an entity that contains
   * a response message and an amenities list.
   * 	- `Map<Function, T>`: This is a method that maps a functional interface to its
   * corresponding value. In this case, it maps the `AddAmenityRequest` request body
   * to a `AddAmenityResponse` object.
   * 	- `orElse(T alternative)`: This is a method that returns the result of either the
   * given expression or the provided alternative. In this case, it returns
   * `ResponseEntity.notFound().build()` if the `createAmenities` call fails.
   * 	- `map(Function<T, U> mapper)`: This is another method that maps the output of a
   * function to a new type. In this case, it maps the `AddAmenityResponse` object to
   * an `Ok` response entity.
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
   * deletes an amenity from the system based on the specified `amenityId`. If successful,
   * it returns a response entity with a `NO_CONTENT` status code; otherwise, it returns
   * a response entity with a `NOT_FOUND` status code.
   * 
   * @param amenityId ID of an amenity to be deleted.
   * 
   * 	- `amenitySDJpaService`: This is an instance of `AmenitySDJpaService`, which is
   * a Java class that provides methods for managing amenities.
   * 	- `deleteAmenity(amenityId)`: This method is defined in the `AmenitySDJpaService`
   * class and takes a single parameter, `amenityId`, which is of type `String`. The
   * method deletes an amenity with the specified `amenityId` using JPA (Java Persistence
   * API) functionality.
   * 	- `isAmenityDeleted`: This variable is assigned the value returned by the
   * `deleteAmenity(amenityId)` method, which indicates whether the amenity was
   * successfully deleted or not. If the amenity was successfully deleted, the value
   * of this variable is `true`, otherwise it is `false`.
   * 	- `HttpStatus.NO_CONTENT`: This is an instance of the `HttpStatus` class, which
   * represents a HTTP status code indicating that the request was successful and there
   * is no content to be returned.
   * 	- `HttpStatus.NOT_FOUND`: This is an instance of the `HttpStatus` class, which
   * represents a HTTP status code indicating that the requested resource could not be
   * found.
   * 
   * @returns a HTTP status code indicating whether the amenity was successfully deleted
   * or not.
   * 
   * 	- `HttpStatus`: This is an instance of the `HttpStatus` class, which represents
   * the HTTP status code returned by the function. The value of this field is either
   * `NO_CONTENT` or `NOT_FOUND`.
   * 	- `ResponseEntity`: This is an instance of the `ResponseEntity` class, which
   * represents a response message for the HTTP request. The `HttpStatus` field of this
   * object contains the HTTP status code returned by the function, and the `body` field
   * contains the error message if the amenity could not be deleted.
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
   * updates an amenity using the provided ID and request body data, returning a response
   * indicating the outcome of the update.
   * 
   * @param amenityId ID of the amenity being updated.
   * 
   * 	- `amenityId`: A string representing the ID of the amenity to be updated.
   * 
   * The function processes the input request and updates the amenity in the database
   * using the `amenitySDJpaService`. The update is successful if a `ResponseEntity`
   * with status code `NO_CONTENT` is returned, indicating that the amenity was updated
   * successfully. Otherwise, a `ResponseEntity` with status code `NOT_FOUND` is returned,
   * indicating that the amenity could not be found in the database.
   * 
   * @param request updateAmenityRequest object that contains the details of the amenity
   * to be updated, which is then converted into an AmenityDto object and passed as a
   * parameter to the `amenitySDJpaService.updateAmenity()` method for updating the
   * amenity in the database.
   * 
   * 	- `@Valid`: Indicates that the input `request` must be validated according to the
   * specified validation rules.
   * 	- `@RequestBody`: Marks the `request` parameter as a request body, indicating
   * that its value should be serialized and sent in the HTTP request message.
   * 	- `UpdateAmenityRequest`: Represents the request body data structure containing
   * the fields for updating an amenity.
   * 
   * @returns a `ResponseEntity` object with a status code of either `NO_CONTENT` or
   * `NOT_FOUND`, depending on whether the amenity was updated successfully.
   * 
   * 	- `HttpStatus`: This is an enumeration that represents the HTTP status code of
   * the response. In this case, it can be either `NO_CONTENT` or `NOT_FOUND`.
   * 	- `ResponseEntity`: This is a class that holds the HTTP status code and the body
   * of the response. The body can be either an empty object (`NO_CONTENT`) or a `Void`
   * object (`NOT_FOUND`).
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
