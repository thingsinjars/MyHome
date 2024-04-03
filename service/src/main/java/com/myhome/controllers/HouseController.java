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

@RestController
@RequiredArgsConstructor
@Slf4j
public class HouseController implements HousesApi {
  private final HouseMemberMapper houseMemberMapper;
  private final HouseService houseService;
  private final HouseApiMapper houseApiMapper;

  /**
   * receives a pageable request, lists all houses from the service, and returns them
   * in a REST API response.
   * 
   * @param pageable page request parameters, such as the page number, size, and sort
   * order, which are used to filter and retrieve a subset of the houses from the database.
   * 
   * @returns a `GetHouseDetailsResponse` object containing a set of `CommunityHouse`
   * objects converted from the service's response.
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
   * retrieves house details for a given ID, maps them to a REST API response, and
   * returns it as an `ResponseEntity`.
   * 
   * @param houseId unique identifier of the house for which the user is requesting details.
   * 
   * @returns a `ResponseEntity` object representing the result of the API call, which
   * contains a list of houses with their details.
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
   * retrieves the members of a house given its ID and pagination information, maps
   * them to a `RestApiResponse`, and returns a `ResponseEntity` with the list of members.
   * 
   * @param houseId identifier of the house for which the members are to be listed.
   * 
   * @param pageable pagination configuration for the list of members, allowing for
   * efficient retrieval of a specific page of members with a given number of elements.
   * 
   * @returns a `ListHouseMembersResponse` object containing the list of members of the
   * specified house.
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
   * receives a request to add members to a house, maps the request to a Set of
   * HouseMembers, adds the members to the house using the `addHouseMembers` method of
   * the `houseService`, and returns the updated membership set in the response.
   * 
   * @param houseId identifier of the house to which the members will be added.
   * 
   * @param request AddHouseMemberRequest object containing the member details to be
   * added to the house, which is passed through to the service layer for processing
   * and storage in the database.
   * 
   * @returns a `ResponseEntity` object with a `HTTP Status Code` of either `CREATED`
   * or `NOT_FOUND`, depending on whether the members were successfully added or not.
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
   * deletes a member from a house based on the provided house ID and member ID. If the
   * member is successfully deleted, a `NO_CONTENT` status code is returned. Otherwise,
   * a `NOT_FOUND` status code is returned.
   * 
   * @param houseId identifier of the house to which the member belongs, which is used
   * as the basis for deleting the member from that house.
   * 
   * @param memberId ID of the member to be deleted from the specified house.
   * 
   * @returns a `ResponseEntity` with a status code of either `NO_CONTENT` or `NOT_FOUND`,
   * depending on whether the member was successfully deleted.
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























