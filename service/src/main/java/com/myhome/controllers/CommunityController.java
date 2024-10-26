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
 * REST Controller which provides endpoints for managing community
 */
@RequiredArgsConstructor
@RestController
@Slf4j
public class CommunityController implements CommunitiesApi {
  private final CommunityService communityService;
  private final CommunityApiMapper communityApiMapper;

  /**
   * Handles a community creation request, maps the request to a CommunityDto object,
   * creates a community using the community service, maps the created community to a
   * CreateCommunityResponse object, and returns the community creation response with
   * a 201 status code.
   *
   * @param request data sent in the request body, which is validated and then used to
   * create a new community.
   *
   * Extracted properties of `request` include name and description.
   *
   * @returns a `CreateCommunityResponse` object with HTTP status code 201 (Created).
   *
   * The output is a `ResponseEntity` object with a `CreateCommunityResponse` body, and
   * a HTTP status of 201 (Created).
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
   * Receives a pageable request to list all community details, retrieves the details
   * from the `communityService`, maps the community details to a REST API response,
   * and returns the response as a HTTP OK status.
   *
   * @param pageable pagination settings for the list of communities, allowing for page
   * size control and limiting the number of results.
   *
   * Destructure `pageable` to its main properties:
   * - `pageNumber`: represents the current page number
   * - `pageSize`: represents the number of items per page
   *
   * @returns a collection of community details in a JSON format.
   *
   * The output is a ResponseEntity, a Java class representing an HTTP response.
   * The body of the response is an instance of GetCommunityDetailsResponse, a class
   * containing a set of GetCommunityDetailsResponseCommunity objects.
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
   * Fetches community details by ID, maps the response to a REST API format, and returns
   * it as a `GetCommunityDetailsResponse` entity in a `ResponseEntity`. If no community
   * is found, it returns a `404 Not Found` response.
   *
   * @param communityId identifier of the community for which details are requested.
   *
   * @returns a `ResponseEntity` containing a list of community details in the
   * `GetCommunityDetailsResponse` format.
   *
   * The returned output is a `ResponseEntity` object, specifically a `GetCommunityDetailsResponse`
   * instance wrapped in a `ResponseEntity`.
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
   * Retrieves a list of community administrators for a specified community ID, processes
   * the result, and returns it as a REST API response.
   *
   * @param communityId identifier of a community for which all admins are to be retrieved
   * and listed.
   *
   * @param pageable pagination settings for the response, allowing for the retrieval
   * of a specified number of records at a time.
   *
   * Destructure:
   * - `pageable` is of type `Pageable`, which is a Spring Data interface.
   * - It has the following main properties:
   *   - `pageNumber`: the current page number
   *   - `pageSize`: the number of items on each page
   *
   * @returns a ResponseEntity containing a List of community admins in a specific
   * response format.
   *
   * The output is a ResponseEntity, primarily containing a List of CommunityAdmins in
   * a set format.
   * It also contains HTTP status codes, specifically 200 for successful responses or
   * 404 for not found responses.
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
   * Handles a request to retrieve a list of houses for a specified community ID.
   * It maps the result from the service layer to a REST API response.
   * If the community ID is not found, it returns a 404 Not Found response.
   *
   * @param communityId identifier of the community for which all houses are to be listed.
   *
   * @param pageable pagination settings for the returned list of houses.
   *
   * Extract its properties.
   *
   * pageable is an object with the following properties:
   * - Page: the current page being requested
   * - PageSize: the number of items to return per page
   * - Sort: the sorting criteria for the returned items
   * - PageNumber: the page number being requested
   * - Offset: the index of the first item to return
   *
   * @returns a ResponseEntity containing a GetHouseDetailsResponse with a set of
   * community houses.
   *
   * Contain a `ResponseEntity` object.
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
   * Adds admins to a community. It takes a community ID and a list of admin users,
   * adds them to the community, and returns a response containing the updated list of
   * admins if successful, or a 404 status code if the community is not found.
   *
   * @param communityId identifier of the community to which admin users are being added.
   *
   * @param request data for adding admins to a community, containing a list of admins
   * to be added.
   *
   * Contain admins which is a list of User objects
   *
   * @returns a ResponseEntity containing an AddCommunityAdminResponse object or a
   * NOT_FOUND status.
   *
   * The returned output is of type `ResponseEntity<AddCommunityAdminResponse>`. It
   * contains a `status` attribute, which is an HTTP status code, and a `body` attribute,
   * which is an instance of `AddCommunityAdminResponse`.
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
   * Adds community houses to a specified community, receiving a request with a community
   * ID and a set of house names, and returns a response with the added house IDs if successful.
   *
   * @param communityId identifier for a community to which new houses are being added.
   *
   * @param request AddCommunityHouseRequest object containing the houses to be added
   * to the community.
   *
   * Extract Set<CommunityHouseName> houseNames from the request object.
   *
   * @returns a ResponseEntity containing a list of house IDs in case of a successful
   * request.
   *
   * The output is a `ResponseEntity` object containing an `AddCommunityHouseResponse`
   * object in its body.
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
   * Deletes a house from a community based on the community and house IDs provided.
   * It checks if the community exists and if the house can be removed from it before
   * returning a successful response.
   *
   * @param communityId identifier of the community from which a house is to be deleted.
   *
   * @param houseId identifier of the house to be removed from the specified community.
   *
   * @returns a `ResponseEntity` containing either a 204 No Content status or a 404 Not
   * Found status.
   *
   * The output is a `ResponseEntity` object with a `Void` body.
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
   * Removes an admin from a community and returns a response based on the outcome. It
   * calls the `communityService.removeAdminFromCommunity` method and returns a 204
   * (NO_CONTENT) response if the admin is successfully removed, or a 404 (NOT_FOUND)
   * response if not.
   *
   * @param communityId identifier for the community from which an admin is to be removed.
   *
   * @param adminId identifier of the admin to be removed from the specified community.
   *
   * @returns either a `NO_CONTENT` response (200) if the admin is removed or a `NOT_FOUND`
   * response (404) if the admin is not found.
   *
   * The returned output is a `ResponseEntity` object of type `Void`. It has a status
   * code, which can be either `NO_CONTENT` or `NOT_FOUND`.
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
   * Handles a request to delete a community by its ID. It logs the request, calls the
   * `communityService` to delete the community, and returns a `NO_CONTENT` response
   * if successful or a `NOT_FOUND` response if the community does not exist.
   *
   * @param communityId identifier for the community to be deleted.
   *
   * @returns either a 204 (NO_CONTENT) response indicating successful deletion or a
   * 404 (NOT_FOUND) response indicating the community was not found.
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
