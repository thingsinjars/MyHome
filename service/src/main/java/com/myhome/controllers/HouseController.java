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

import com.myhome.api.HousesApi;
import com.myhome.controllers.dto.mapper.HouseMemberMapper;
import com.myhome.controllers.mapper.HouseApiMapper;
import com.myhome.domain.CommunityHouse;
import com.myhome.domain.HouseMember;
import com.myhome.model.AddHouseMemberRequest;
import com.myhome.model.AddHouseMemberResponse;
import com.myhome.model.GetHouseDetailsResponse;
import com.myhome.model.GetHouseDetailsResponseCommunityHouse;
import com.myhome.model.ListHouseMembersResponse;
import com.myhome.services.HouseService;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * Provides RESTful API endpoints for managing houses and their members. It handles
 * requests to list all houses, get house details, list house members, add house
 * members, and delete house members. The controller delegates tasks to the HouseService
 * class for data retrieval and manipulation.
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class HouseController implements HousesApi {
  private final HouseMemberMapper houseMemberMapper;
  private final HouseService houseService;
  private final HouseApiMapper houseApiMapper;

  /**
   * Retrieves a list of houses, maps them to a REST API response format, and returns
   * the result as a HTTP OK response. It uses pagination with a default page size of
   * 200.
   * 
   * @param pageable pagination settings for retrieving houses, allowing to control the
   * number of items returned per page and the current page number.
   * 
   * Destructured into Pageable parameters: page, size, and sort.
   * 
   * @returns a ResponseEntity containing a list of GetHouseDetailsResponseCommunityHouse
   * objects.
   */
  @Override
  public ResponseEntity<GetHouseDetailsResponse> listAllHouses(
      @PageableDefault(size = 200) Pageable pageable) {
    log.trace("Received request to list all houses");

    Set<CommunityHouse> houseDetails =
        houseService.listAllHouses(pageable);
    Set<GetHouseDetailsResponseCommunityHouse> getHouseDetailsResponseSet =
        houseApiMapper.communityHouseSetToRestApiResponseCommunityHouseSet(houseDetails);

    GetHouseDetailsResponse response = new GetHouseDetailsResponse();

    response.setHouses(getHouseDetailsResponseSet);

    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  /**
   * Retrieves house details by ID, maps them to a REST API response object, and returns
   * a ResponseEntity with a GetHouseDetailsResponse containing the mapped houses. If
   * no matching record is found, it returns a ResponseEntity with a 404 status code.
   * 
   * @param houseId identifier of the house whose details are to be retrieved and
   * processed by the function.
   * 
   * @returns a ResponseEntity containing GetHouseDetailsResponse with houses.
   */
  @Override
  public ResponseEntity<GetHouseDetailsResponse> getHouseDetails(String houseId) {
    log.trace("Received request to get details of a house with id[{}]", houseId);
    return houseService.getHouseDetailsById(houseId)
        .map(houseApiMapper::communityHouseToRestApiResponseCommunityHouse)
        .map(Collections::singleton)
        .map(getHouseDetailsResponseCommunityHouses -> new GetHouseDetailsResponse().houses(getHouseDetailsResponseCommunityHouses))
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  /**
   * Retrieves all members of a house with a specified ID, applies pagination using a
   * pageable object, maps the result to a response API, and returns it as a ResponseEntity
   * containing a ListHouseMembersResponse or a not-found error.
   * 
   * @param houseId ID of the house for which all members are to be listed, as indicated
   * by the log message and the subsequent method call to `houseService.getHouseMembersById`.
   * 
   * @param pageable page information for pagination, allowing for retrieval of a
   * specific subset of members from the house with a specified size.
   * 
   * Pageable has default size set to 200 and no offset specified.
   * 
   * @returns a ResponseEntity of ListHouseMembersResponse.
   * 
   * The ResponseEntity is of type ListHouseMembersResponse with members containing a
   * set of HouseMemberSet converted from house member objects using houseMemberMapper.
   */
  @Override
  public ResponseEntity<ListHouseMembersResponse> listAllMembersOfHouse(
      String houseId,
      @PageableDefault(size = 200) Pageable pageable) {
    log.trace("Received request to list all members of the house with id[{}]", houseId);

    return houseService.getHouseMembersById(houseId, pageable)
        .map(HashSet::new)
        .map(houseMemberMapper::houseMemberSetToRestApiResponseHouseMemberSet)
        .map(houseMembers -> new ListHouseMembersResponse().members(houseMembers))
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  /**
   * Handles requests to add members to a house, converting provided data transfer
   * objects (DTOs) into domain objects and storing them in the database. It returns a
   * response indicating success or failure based on whether the operation was successful.
   * 
   * @param houseId ID of the house to which members are being added, and it is used
   * as a path variable.
   * 
   * @param request AddHouseMemberRequest object that contains a set of house member
   * DTOs to be added to the specified house with id.
   * 
   * Destructured request:
   * - `houseId`: String
   * - `members`: Set of HouseMember DTOs
   * 
   * @returns a response entity containing an add house member response.
   * 
   * The response is of type `ResponseEntity` and contains an instance of
   * `AddHouseMemberResponse`. This response includes a set of `HouseMember` objects,
   * represented as `members`, in its attributes.
   */
  @Override
  public ResponseEntity<AddHouseMemberResponse> addHouseMembers(
      @PathVariable String houseId, @Valid AddHouseMemberRequest request) {

    log.trace("Received request to add member to the house with id[{}]", houseId);
    Set<HouseMember> members =
        houseMemberMapper.houseMemberDtoSetToHouseMemberSet(request.getMembers());
    Set<HouseMember> savedHouseMembers = houseService.addHouseMembers(houseId, members);

    if (savedHouseMembers.size() == 0 && request.getMembers().size() != 0) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    } else {
      AddHouseMemberResponse response = new AddHouseMemberResponse();
      response.setMembers(
          houseMemberMapper.houseMemberSetToRestApiResponseAddHouseMemberSet(savedHouseMembers));
      return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
  }

  /**
   * Handles a request to delete a member from a house with specified IDs. It logs the
   * request and calls the `houseService.deleteMemberFromHouse` method to perform the
   * deletion. If successful, it returns a response indicating no content; otherwise,
   * it returns a not found status.
   * 
   * @param houseId identifying code of a house to which a member is being deleted from.
   * 
   * @param memberId unique identifier of the member to be deleted from the house with
   * the specified `houseId`.
   * 
   * @returns a `ResponseEntity` with either HTTP status NO CONTENT (204) or NOT FOUND
   * (404).
   * 
   * The output is a ResponseEntity object with Void as its payload. It has a status
   * code either HttpStatus.NO_CONTENT or HttpStatus.NOT_FOUND depending on whether the
   * member was successfully deleted or not.
   */
  @Override
  public ResponseEntity<Void> deleteHouseMember(String houseId, String memberId) {
    log.trace("Received request to delete a member from house with house id[{}] and member id[{}]",
        houseId, memberId);
    boolean isMemberDeleted = houseService.deleteMemberFromHouse(houseId, memberId);
    if (isMemberDeleted) {
      return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    } else {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
  }
}