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
   * retrieves amenity details for a given ID and maps the result to an `AmenityDetailsResponse`
   * object using a mapper, returning an `ResponseEntity` with a status code of 200 if
   * the amenity is found, or a status code of 404 if it's not.
   * 
   * @param amenityId ID of the amenity to retrieve details for.
   * 
   * 	- `amenityId`: A String representing the unique identifier for an amenity.
   * 
   * @returns an `OkResponseEntity` containing the details of the amenity with the
   * specified ID.
   * 
   * 	- ` ResponseEntity<GetAmenityDetailsResponse>` is a utility class provided by
   * Spring that represents a response entity with an optional body. In this case, the
   * body is of type `GetAmenityDetailsResponse`.
   * 	- `getAmenityDetails` method returns a `Flux` object representing the result of
   * the database query. The `Flux` object emits objects of type `AmenityDetailsResponse`.
   * 	- The `map` methods are used to transform the `Flux` object into a `Optional`
   * object, which represents the presence or absence of a response entity. If the
   * response is present, the `Optional` object is `ofType` `ResponseEntity`, and its
   * `getBody` method returns the actual response entity. Otherwise, the `Optional`
   * object is `empty`, indicating that no response was found.
   * 	- The `orElse` method is used to provide an alternative response if the `Optional`
   * object is empty. In this case, the alternative response is a `ResponseEntity` with
   * a status code of `NOT_FOUND`.
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
   * them to `GetAmenityDetailsResponse` objects through `amenityApiMapper`, and returns
   * an `Ok` response entity with the mapped amenity details.
   * 
   * @param communityId community for which the list of amenities is being retrieved.
   * 
   * 	- `communityId`: A string representing the community ID used to retrieve amenities.
   * 	- Type: `String`
   * 	- Description: The unique identifier for a community.
   * 
   * @returns a set of `GetAmenityDetailsResponse` objects containing the details of
   * all amenities for a given community.
   * 
   * 	- `ResponseEntity`: This is the type of the returned response, which indicates
   * that it is an entity containing a set of `GetAmenityDetailsResponse` objects.
   * 	- `ok`: This is the status code of the response, indicating that it was successful.
   * 	- `Set<GetAmenityDetailsResponse>`: This is the set of `GetAmenityDetailsResponse`
   * objects contained in the response entity.
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
   * creates amenities for a given community using the `amenitySDJpaService`, maps the
   * created amenities to a `AddAmenityResponse`, and returns a `ResponseEntity` with
   * a status of `ok` or `notFound` depending on the outcome.
   * 
   * @param communityId ID of the community to which the amenities will be added.
   * 
   * 	- `communityId`: A string representing the unique identifier of a community.
   * 	- `@PathVariable`: An annotation used to inject the community ID from the URL
   * path into the function.
   * 
   * @param request AddAmenityRequest object containing the amenities to be added to
   * the community, which is used by the method to create the new amenities in the database.
   * 
   * 	- `request.getAmenities()`: A list of amenity objects containing information about
   * the amenities to be added to the community. Each amenity object has attributes
   * such as `name`, `type`, and `description`.
   * 
   * @returns a `ResponseEntity` object containing an `AddAmenityResponse` object with
   * the created amenities.
   * 
   * 	- `ResponseEntity`: This is a class that represents a response entity in Spring
   * WebFlux. It contains an `OK` status code and a `body` property that holds the
   * actual response data.
   * 	- `ok`: This is a boolean value that indicates whether the operation was successful
   * or not. If the operation was successful, this field will be set to `true`, otherwise
   * it will be set to `false`.
   * 	- `amenities`: This is a list of amenity objects that were created by the
   * `createAmenities` method. Each amenity object contains information about the
   * amenity, such as its name, description, and category.
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
   * deletes an amenity from the database using the `amenitySDJpaService`. If the amenity
   * is successfully deleted, a `NO_CONTENT` status code is returned. Otherwise, a
   * `NOT_FOUND` status code is returned.
   * 
   * @param amenityId ID of an amenity to be deleted.
   * 
   * 	- The method takes a String parameter called `amenityId`, which is used to identify
   * a specific amenity in the system.
   * 	- The parameter `amenityId` has a length of 20 or more characters, indicating
   * that it is a unique identifier for each amenity in the system.
   * 
   * @returns a `ResponseEntity` object with a status code of either `NO_CONTENT` or
   * `NOT_FOUND`, depending on whether the amenity was successfully deleted or not.
   * 
   * 	- `HttpStatus.NO_CONTENT`: This status code indicates that the amenity was
   * successfully deleted.
   * 	- `HttpStatus.NOT_FOUND`: This status code indicates that the amenity could not
   * be found, which means it may have been deleted previously or it does not exist in
   * the database.
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
   * updates an amenity using the provided `UpdateAmenityRequest`. If the update is
   * successful, it returns a `ResponseEntity` with a status code of `NO_CONTENT`. If
   * the update fails, it returns a `ResponseEntity` with a status code of `NOT_FOUND`.
   * 
   * @param amenityId ID of the amenity being updated.
   * 
   * 	- `amenityId`: This is the unique identifier for an amenity in the system, used
   * for updating the amenity record.
   * 
   * @param request UpdateAmenityRequest object that contains the details of the amenity
   * to be updated.
   * 
   * 	- `@Valid` indicates that the `request` object is validated by the framework
   * before being processed.
   * 	- `@RequestBody` specifies that the `request` object is passed as a request body
   * in the HTTP request.
   * 	- `UpdateAmenityRequest` is the class that contains the properties of the request.
   * 
   * @returns a `ResponseEntity` object with a status code of either `NO_CONTENT` or
   * `NOT_FOUND`, depending on whether the amenity was updated successfully.
   * 
   * 	- `HttpStatus`: This is an enumeration that indicates the HTTP status code of the
   * response. In this case, it can be either `NO_CONTENT` or `NOT_FOUND`.
   * 	- `ResponseEntity`: This is a class that represents a response entity, which
   * contains the status code and other metadata about the response.
   * 	- `Void`: This is a type parameter that indicates that the response entity does
   * not contain any data.
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
