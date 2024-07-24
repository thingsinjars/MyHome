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

import com.myhome.api.CommunitiesApi;
import com.myhome.controllers.dto.CommunityDto;
import com.myhome.controllers.mapper.CommunityApiMapper;
import com.myhome.domain.Community;
import com.myhome.domain.CommunityHouse;
import com.myhome.domain.User;
import com.myhome.model.AddCommunityAdminRequest;
import com.myhome.model.AddCommunityAdminResponse;
import com.myhome.model.AddCommunityHouseRequest;
import com.myhome.model.AddCommunityHouseResponse;
import com.myhome.model.CommunityHouseName;
import com.myhome.model.CreateCommunityRequest;
import com.myhome.model.CreateCommunityResponse;
import com.myhome.model.GetCommunityDetailsResponse;
import com.myhome.model.GetCommunityDetailsResponseCommunity;
import com.myhome.model.GetHouseDetailsResponse;
import com.myhome.model.ListCommunityAdminsResponse;
import com.myhome.services.CommunityService;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Provides REST endpoints for managing communities, including creating, listing,
 * getting details of communities, adding and removing community admins and houses,
 * and deleting communities. It uses the CommunityService and CommunityApiMapper to
 * perform these operations. The class implements the CommunitiesApi interface and
 * handles HTTP requests and responses accordingly.
 */
@RequiredArgsConstructor
@RestController
@Slf4j
public class CommunityController implements CommunitiesApi {
  private final CommunityService communityService;
  private final CommunityApiMapper communityApiMapper;

  /**
   * Creates a new community based on a given request, maps it to a `CommunityDto`, and
   * then returns a response indicating the successful creation of the community.
   * 
   * @param request `CreateCommunityRequest` object that is validated and used to create
   * a new community by mapping it to a `CommunityDto` and then creating a new community
   * using the `communityService`.
   * 
   * Creates community request is valid request body and contains CreateCommunityRequest.
   * 
   * This class has properties such as name, description, and visibility.
   * 
   * @returns a response entity with a create community response.
   */
  @Override
  public ResponseEntity<CreateCommunityResponse> createCommunity(@Valid @RequestBody
      CreateCommunityRequest request) {
    log.trace("Received create community request");
    CommunityDto requestCommunityDto =
        communityApiMapper.createCommunityRequestToCommunityDto(request);
    Community createdCommunity = communityService.createCommunity(requestCommunityDto);
    CreateCommunityResponse createdCommunityResponse =
        communityApiMapper.communityToCreateCommunityResponse(createdCommunity);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdCommunityResponse);
  }

  /**
   * Retrieves a list of community details from the database, maps them to a REST API
   * response format, and returns a HTTP OK response with the resulting data. It uses
   * pagination and logging.
   * 
   * @param pageable criteria for retrieving a limited number of items from a larger
   * collection, with the default size being set to 200.
   * 
   * Sort - It defines sort criteria and direction for the query result set.
   * Offset - It specifies the offset of the first record from the beginning of the
   * query result set.
   * PageSize - It specifies the maximum number of records returned in the response.
   * 
   * @returns a ResponseEntity with a list of community details.
   * 
   * The output is an instance of `ResponseEntity`, which contains a status and a body.
   * The status is set to HTTP 200 (OK). The body is an object of type `GetCommunityDetailsResponse`.
   */
  @Override
  public ResponseEntity<GetCommunityDetailsResponse> listAllCommunity(
      @PageableDefault(size = 200) Pageable pageable) {
    log.trace("Received request to list all community");

    Set<Community> communityDetails = communityService.listAll(pageable);
    Set<GetCommunityDetailsResponseCommunity> communityDetailsResponse =
        communityApiMapper.communitySetToRestApiResponseCommunitySet(communityDetails);

    GetCommunityDetailsResponse response = new GetCommunityDetailsResponse();
    response.getCommunities().addAll(communityDetailsResponse);

    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  /**
   * Retrieves community details by ID, maps them to a REST API response object, and
   * returns it as a JSON response entity. If no matching community is found, it returns
   * a "Not Found" response entity with an empty body.
   * 
   * @param communityId identifier of the community whose details are to be retrieved
   * and processed by the function.
   * 
   * @returns a `ResponseEntity` containing a `GetCommunityDetailsResponse`.
   * 
   * Returns a ResponseEntity containing GetCommunityDetailsResponse object with
   * communities attribute. The communities attribute is a set of community objects.
   */
  @Override
  public ResponseEntity<GetCommunityDetailsResponse> listCommunityDetails(
      @PathVariable String communityId) {
    log.trace("Received request to get details about community with id[{}]", communityId);

    return communityService.getCommunityDetailsById(communityId)
        .map(communityApiMapper::communityToRestApiResponseCommunity)
        .map(Arrays::asList)
        .map(HashSet::new)
        .map(communities -> new GetCommunityDetailsResponse().communities(communities))
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  /**
   * Receives a request to list all admins for a community with a specified ID, applies
   * pagination with a default size of 200, and returns a response containing the list
   * of admins if found or a not-found error message if not.
   * 
   * @param communityId identifier of the community for which all admins are to be
   * listed and is used as an argument when calling the `findCommunityAdminsById` method
   * of the `communityService`.
   * 
   * @param pageable pagination parameters for retrieving community admins, controlling
   * the size and page number of the result set.
   * 
   * Retrieve is false by default, and the maximum size for the result is 200.
   * 
   * @returns a `ResponseEntity` containing a list of community admins.
   * 
   * Returns a ResponseEntity containing a ListCommunityAdminsResponse. The
   * ListCommunityAdminsResponse includes an admins attribute with a set of community
   * admin objects.
   */
  @Override
  public ResponseEntity<ListCommunityAdminsResponse> listCommunityAdmins(
      @PathVariable String communityId,
      @PageableDefault(size = 200) Pageable pageable) {
    log.trace("Received request to list all admins of community with id[{}]", communityId);

    return communityService.findCommunityAdminsById(communityId, pageable)
        .map(HashSet::new)
        .map(communityApiMapper::communityAdminSetToRestApiResponseCommunityAdminSet)
        .map(admins -> new ListCommunityAdminsResponse().admins(admins))
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  /**
   * Retrieves a list of houses for a given community ID, applies pagination with a
   * default size of 200, and returns a response containing the house details in JSON
   * format or a 404 error if no houses are found.
   * 
   * @param communityId identifier of the community for which all houses are to be
   * retrieved and returned in response.
   * 
   * @param pageable pagination criteria for retrieving houses of a community, allowing
   * for the specification of the page size and other related parameters.
   * 
   * Sort - It specifies the sorting criteria for the result set. It can be null if no
   * sorting is required.
   * Size - It determines the number of records that should be fetched at a time. In
   * this case, it's 200.
   * 
   * @returns a `ResponseEntity` containing a list of community houses.
   * 
   * Returns a ResponseEntity containing GetHouseDetailsResponse object with houses
   * attribute populated from communityService's findCommunityHousesById method result.
   * The response can be OK or NOT_FOUND status.
   */
  @Override
  public ResponseEntity<GetHouseDetailsResponse> listCommunityHouses(
      @PathVariable String communityId,
      @PageableDefault(size = 200) Pageable pageable) {
    log.trace("Received request to list all houses of community with id[{}]", communityId);

    return communityService.findCommunityHousesById(communityId, pageable)
        .map(HashSet::new)
        .map(communityApiMapper::communityHouseSetToRestApiResponseCommunityHouseSet)
        .map(houses -> new GetHouseDetailsResponse().houses(houses))
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  /**
   * Adds one or more admins to a community with a given ID, validates the request and
   * logs the operation. It returns a response indicating whether the operation was
   * successful or not, along with the updated list of admins for the community.
   * 
   * @param communityId identifier of the community to which admins are being added,
   * and is used to retrieve the corresponding community object from the service layer.
   * 
   * @param request AddCommunityAdminRequest object containing the list of admins to
   * be added to the community with the specified communityId.
   * 
   * The `AddCommunityAdminRequest` object contains an array of administrators to be
   * added to a community with a specific ID. The main property is `admins`, which
   * represents a collection of users.
   * 
   * @returns a ResponseEntity containing an AddCommunityAdminResponse object.
   * 
   * The output is a `ResponseEntity` object that contains an `AddCommunityAdminResponse`
   * object as its body. This response contains a set of administrators' user IDs.
   */
  @Override
  public ResponseEntity<AddCommunityAdminResponse> addCommunityAdmins(
      @PathVariable String communityId, @Valid @RequestBody
      AddCommunityAdminRequest request) {
    log.trace("Received request to add admin to community with id[{}]", communityId);
    Optional<Community> communityOptional =
        communityService.addAdminsToCommunity(communityId, request.getAdmins());
    return communityOptional.map(community -> {
      Set<String> adminsSet = community.getAdmins()
          .stream()
          .map(User::getUserId)
          .collect(Collectors.toSet());
      AddCommunityAdminResponse response = new AddCommunityAdminResponse().admins(adminsSet);
      return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
  }

  /**
   * Adds houses to a community with a given ID, processing a request containing a list
   * of house names. It calls an external API to convert the house names into objects
   * and then adds them to the community using a service.
   * 
   * @param communityId ID of the community to which houses are being added.
   * 
   * @param request `AddCommunityHouseRequest` object that contains a set of house names
   * to be added to a community with a specified ID.
   * 
   * The `request` object has a single property `houses`, which is a set of
   * `CommunityHouseName` objects.
   * 
   * @returns a `ResponseEntity` containing an `AddCommunityHouseResponse`.
   * 
   * The response is an instance of ResponseEntity type with status code and body. The
   * status code can be either HTTP 201 (Created) or HTTP 400 (Bad Request). The body
   * of the response contains an object of AddCommunityHouseResponse type which has a
   * set of house IDs as its attribute.
   */
  @Override
  public ResponseEntity<AddCommunityHouseResponse> addCommunityHouses(
      @PathVariable String communityId, @Valid @RequestBody
      AddCommunityHouseRequest request) {
    log.trace("Received request to add house to community with id[{}]", communityId);
    Set<CommunityHouseName> houseNames = request.getHouses();
    Set<CommunityHouse> communityHouses =
        communityApiMapper.communityHouseNamesSetToCommunityHouseSet(houseNames);
    Set<String> houseIds = communityService.addHousesToCommunity(communityId, communityHouses);
    if (houseIds.size() != 0 && houseNames.size() != 0) {
      AddCommunityHouseResponse response = new AddCommunityHouseResponse();
      response.setHouses(houseIds);
      return ResponseEntity.status(HttpStatus.CREATED).body(response);
    } else {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
  }

  /**
   * Deletes a house from a community given their IDs, logs the request, and returns a
   * response indicating success or failure. If the house is found, it is removed from
   * the community; otherwise, a "not found" response is returned.
   * 
   * @param communityId identifier of the community from which to delete a house, used
   * as an argument for retrieving community details and then removing the specified house.
   * 
   * @param houseId ID of the house to be removed from the community identified by the
   * `communityId`.
   * 
   * @returns a ResponseEntity with no content or a not found response.
   * 
   * The output is either a `ResponseEntity` with no content or a response entity
   * indicating that the requested resource was not found.
   */
  @Override
  public ResponseEntity<Void> removeCommunityHouse(
      @PathVariable String communityId, @PathVariable String houseId
  ) {
    log.trace(
        "Received request to delete house with id[{}] from community with id[{}]",
        houseId, communityId);

    Optional<Community> communityOptional = communityService.getCommunityDetailsById(communityId);

    return communityOptional.filter(
        community -> communityService.removeHouseFromCommunityByHouseId(community, houseId))
        .map(removed -> ResponseEntity.noContent().<Void>build())
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  /**
   * Removes an admin from a community and returns a response indicating success or
   * failure. It calls the `communityService.removeAdminFromCommunity` method to perform
   * the removal, then returns a ResponseEntity with a status code of NO_CONTENT if
   * successful or NOT_FOUND if not found.
   * 
   * @param communityId identifier of the community from which an admin is to be removed.
   * 
   * @param adminId identifier of the admin to be removed from the community specified
   * by the `communityId`.
   * 
   * @returns a HTTP response with status NO CONTENT or NOT FOUND.
   * 
   * Returns a ResponseEntity of type Void indicating the status of the operation. If
   * the admin is removed successfully, the response has a status of HttpStatus.NO_CONTENT
   * (204). Otherwise, it returns a response with a status of HttpStatus.NOT_FOUND (404).
   */
  @Override
  public ResponseEntity<Void> removeAdminFromCommunity(
      @PathVariable String communityId, @PathVariable String adminId) {
    log.trace(
        "Received request to delete an admin from community with community id[{}] and admin id[{}]",
        communityId, adminId);
    boolean adminRemoved = communityService.removeAdminFromCommunity(communityId, adminId);
    if (adminRemoved) {
      return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    } else {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
  }

  /**
   * Handles a request to delete a community with a specified ID. It checks if the
   * deletion is successful by calling the `communityService`, and returns a response
   * status indicating whether the deletion was successful or not, using HTTP status codes.
   * 
   * @param communityId identifier of the community to be deleted, which is used by the
   * `communityService` to perform the deletion operation.
   * 
   * @returns a HTTP response with a status code.
   */
  @Override
  public ResponseEntity<Void> deleteCommunity(@PathVariable String communityId) {
    log.trace("Received delete community request");
    boolean isDeleted = communityService.deleteCommunity(communityId);
    if (isDeleted) {
      return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    } else {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
  }
}
