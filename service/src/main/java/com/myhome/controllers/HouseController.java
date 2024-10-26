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
 * Provides RESTful APIs for managing community houses and their members, including
 * listing houses, retrieving house details, adding and deleting members.
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class HouseController implements HousesApi {
  private final HouseMemberMapper houseMemberMapper;
  private final HouseService houseService;
  private final HouseApiMapper houseApiMapper;

  /**
   * Handles a request to retrieve a paginated list of houses. It calls the `houseService`
   * to fetch the details, maps the result to a REST API response format using the
   * `houseApiMapper`, and returns the response with a 200 OK status.
   *
   * @param pageable pagination settings for retrieving a subset of data, allowing the
   * function to handle large data sets efficiently.
   *
   * Destructure:
   * - `pageable` has a `size` property, specifying the number of records to return per
   * page, set to 200 by default.
   *
   * Its main properties include:
   * - `size`: The number of records to return per page.
   * - `pageNumber`: The page number to return.
   *
   * @returns a ResponseEntity containing a GetHouseDetailsResponse object with a set
   * of CommunityHouse details.
   *
   * The output is a `ResponseEntity` containing a `GetHouseDetailsResponse` object,
   * which has a single property: a set of `GetHouseDetailsResponseCommunityHouse` objects.
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
   * Retrieves house details by ID, maps the result to a REST API response, and returns
   * it as a ResponseEntity. If no house is found, it returns a 404 ResponseEntity.
   *
   * @param houseId identifier of the house for which details are being requested.
   *
   * @returns a ResponseEntity containing a GetHouseDetailsResponse with a list of
   * CommunityHouse objects.
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
   * Fetches all members of a house with the specified ID, paginates the result, and
   * returns them in a ListHouseMembersResponse entity wrapped in a ResponseEntity. It
   * handles pagination and returns a 404 response if no members are found.
   *
   * @param houseId identifier of a house for which all members are to be listed.
   *
   * @param pageable pagination settings, allowing for the retrieval of a specified
   * number of items at a time, defaulting to 200 items if not otherwise specified.
   *
   * Destructure:
   * - `pageable` has properties `pageNumber` and `pageSize`.
   *
   * @returns a list of house members in a `ListHouseMembersResponse` object.
   *
   * Contain a ResponseEntity object with a List of HouseMembers.
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
   * Adds members to a house by mapping request data to house members, saving them, and
   * returning a response with the added members.
   *
   * @param houseId identifier of the house to which the members are being added.
   *
   * @param request AddHouseMemberRequest object containing the members to be added to
   * a house, retrieved from the request body or other source.
   *
   * Contain members.
   *
   * @returns a ResponseEntity containing an AddHouseMemberResponse object with added
   * house members.
   *
   * The output is a `ResponseEntity` object with an `AddHouseMemberResponse` body.
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
   * Handles deletion of a member from a house by the house service and returns a 204
   * response if successful or 404 if the member is not found.
   *
   * @param houseId identifier for the house from which a member is to be deleted.
   *
   * @param memberId identifier of the member to be deleted from the specified house.
   *
   * @returns a ResponseEntity indicating either a successful deletion (NO_CONTENT) or
   * a non-existent member (NOT_FOUND).
   *
   * The returned output is a `ResponseEntity` object with a status code of either
   * `NO_CONTENT` or `NOT_FOUND`.
   * It has no response body.
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