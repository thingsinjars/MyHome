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

import com.myhome.controllers.dto.mapper.HouseMemberMapper;
import com.myhome.controllers.mapper.HouseApiMapper;
import com.myhome.domain.CommunityHouse;
import com.myhome.domain.HouseMember;
import com.myhome.model.AddHouseMemberRequest;
import com.myhome.model.AddHouseMemberResponse;
import com.myhome.model.GetHouseDetailsResponse;
import com.myhome.model.GetHouseDetailsResponseCommunityHouse;
import com.myhome.model.HouseMemberDto;
import com.myhome.model.ListHouseMembersResponse;
import com.myhome.services.HouseService;
import helpers.TestUtils;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Contains unit tests for the HouseController class, verifying its behavior under
 * various scenarios.
 */
class HouseControllerTest {

  private final String TEST_HOUSE_ID = "test-house-id";
  private final String TEST_MEMBER_ID = "test-member-id";

  private final int TEST_HOUSES_COUNT = 2;
  private final int TEST_HOUSE_MEMBERS_COUNT = 2;

  @Mock
  private HouseMemberMapper houseMemberMapper;
  @Mock
  private HouseService houseService;
  @Mock
  private HouseApiMapper houseApiMapper;

  @InjectMocks
  private HouseController houseController;

  /**
   * Initializes Mockito annotations, allowing for the use of Mockito's mocking
   * functionality within the test class. This enables the creation of mock objects for
   * dependencies, facilitating unit testing. The `MockitoAnnotations.initMocks(this)`
   * line is the key action performed by the function.
   */
  @BeforeEach
  private void init() {
    MockitoAnnotations.initMocks(this);
  }

  /**
   * Tests the retrieval of all houses from the database by verifying the response
   * status and body from the `houseController` when calling the `listAllHouses` method.
   */
  @Test
  void listAllHouses() {
    // given
    Set<CommunityHouse> testHouses = TestUtils.CommunityHouseHelpers.getTestHouses(TEST_HOUSES_COUNT);
    Set<GetHouseDetailsResponseCommunityHouse> testHousesResponse = testHouses.stream()
        .map(house -> new GetHouseDetailsResponseCommunityHouse().houseId(house.getHouseId()).name(house.getName()))
        .collect(Collectors.toSet());
    GetHouseDetailsResponse expectedResponseBody = new GetHouseDetailsResponse();
    expectedResponseBody.setHouses(testHousesResponse);

    given(houseService.listAllHouses(any()))
        .willReturn(testHouses);
    given(houseApiMapper.communityHouseSetToRestApiResponseCommunityHouseSet(testHouses))
        .willReturn(testHousesResponse);

    // when
    ResponseEntity<GetHouseDetailsResponse> response = houseController.listAllHouses(null);

    // then
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(expectedResponseBody, response.getBody());
  }

  /**
   * Tests the retrieval of house details by ID from a controller, verifying that the
   * correct response is returned and that the service and mapper are called as expected.
   */
  @Test
  void getHouseDetails() {
    // given
    CommunityHouse testCommunityHouse = TestUtils.CommunityHouseHelpers.getTestCommunityHouse(TEST_HOUSE_ID);
    GetHouseDetailsResponseCommunityHouse houseDetailsResponse =
            new GetHouseDetailsResponseCommunityHouse()
                .houseId(testCommunityHouse.getHouseId())
                .name(testCommunityHouse.getName());

    GetHouseDetailsResponse expectedResponseBody = new GetHouseDetailsResponse();
    expectedResponseBody.getHouses().add(houseDetailsResponse);

    given(houseService.getHouseDetailsById(TEST_HOUSE_ID))
        .willReturn(Optional.of(testCommunityHouse));
    given(houseApiMapper.communityHouseToRestApiResponseCommunityHouse(testCommunityHouse))
        .willReturn(houseDetailsResponse);

    // when
    ResponseEntity<GetHouseDetailsResponse> response =
        houseController.getHouseDetails(TEST_HOUSE_ID);

    // then
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(expectedResponseBody, response.getBody());
    verify(houseService).getHouseDetailsById(TEST_HOUSE_ID);
    verify(houseApiMapper).communityHouseToRestApiResponseCommunityHouse(testCommunityHouse);
  }

  /**
   * Tests the behavior of a controller when a house with a specified ID does not exist
   * in the system. It verifies that a 404 status code is returned and that no response
   * body is present.
   */
  @Test
  void getHouseDetailsNotExists() {
    // given
    CommunityHouse testCommunityHouse = TestUtils.CommunityHouseHelpers.getTestCommunityHouse(TEST_HOUSE_ID);

    given(houseService.getHouseDetailsById(TEST_HOUSE_ID))
        .willReturn(Optional.empty());

    // when
    ResponseEntity<GetHouseDetailsResponse> response =
        houseController.getHouseDetails(TEST_HOUSE_ID);

    // then
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertNull(response.getBody());
    verify(houseService).getHouseDetailsById(TEST_HOUSE_ID);
    verify(houseApiMapper, never()).communityHouseToRestApiResponseCommunityHouse(
        testCommunityHouse);
  }

  /**
   * Tests the retrieval of a house's members by testing the `houseController` method.
   * It verifies that the method returns a successful response with the expected list
   * of house members.
   */
  @Test
  void listAllMembersOfHouse() {
    // given
    Set<HouseMember> testHouseMembers = TestUtils.HouseMemberHelpers.getTestHouseMembers(TEST_HOUSE_MEMBERS_COUNT);
    Set<com.myhome.model.HouseMember> testHouseMemberDetails = testHouseMembers.stream()
        .map(member -> new com.myhome.model.HouseMember()
            .memberId(member.getMemberId())
            .name(member.getName()))
        .collect(Collectors.toSet());

    ListHouseMembersResponse expectedResponseBody =
        new ListHouseMembersResponse().members(testHouseMemberDetails);

    given(houseService.getHouseMembersById(TEST_HOUSE_ID, null))
        .willReturn(Optional.of(new ArrayList<>(testHouseMembers)));
    given(houseMemberMapper.houseMemberSetToRestApiResponseHouseMemberSet(
        new HashSet<>(testHouseMembers)))
        .willReturn(testHouseMemberDetails);

    // when
    ResponseEntity<ListHouseMembersResponse> response =
        houseController.listAllMembersOfHouse(TEST_HOUSE_ID, null);

    // then
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(expectedResponseBody, response.getBody());
    verify(houseService).getHouseMembersById(TEST_HOUSE_ID, null);
    verify(houseMemberMapper).houseMemberSetToRestApiResponseHouseMemberSet(
        new HashSet<>(testHouseMembers));
  }

  /**
   * Tests the behavior of the `listAllMembersOfHouse` controller method when a house
   * with the specified ID does not exist. It verifies that a 404 status code is returned
   * and no response body is present.
   */
  @Test
  void listAllMembersOfHouseNotExists() {
    // given
    given(houseService.getHouseMembersById(TEST_HOUSE_ID, null))
        .willReturn(Optional.empty());

    // when
    ResponseEntity<ListHouseMembersResponse> response =
        houseController.listAllMembersOfHouse(TEST_HOUSE_ID, null);

    // then
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertNull(response.getBody());
    verify(houseService).getHouseMembersById(TEST_HOUSE_ID, null);
    verify(houseMemberMapper, never()).houseMemberSetToRestApiResponseHouseMemberSet(anySet());
  }

  /**
   * Adds new members to a house by sending an `AddHouseMemberRequest` to the
   * `houseController`, which then calls the `houseService` to add the members and
   * returns an `AddHouseMemberResponse` with the added members.
   */
  @Test
  void addHouseMembers() {
    // given
    Set<HouseMember> testMembers = TestUtils.HouseMemberHelpers.getTestHouseMembers(TEST_HOUSE_MEMBERS_COUNT);
    Set<HouseMemberDto> testMembersDto = testMembers.stream()
        .map(member -> new HouseMemberDto()
            .memberId(member.getMemberId())
            .name(member.getName()))
        .collect(Collectors.toSet());

    AddHouseMemberRequest request = new AddHouseMemberRequest().members(testMembersDto);

    Set<com.myhome.model.HouseMember> addedMembers = testMembers.stream()
        .map(member -> new com.myhome.model.HouseMember()
            .memberId(member.getMemberId())
            .name(member.getName()))
        .collect(Collectors.toSet());

    AddHouseMemberResponse expectedResponseBody = new AddHouseMemberResponse();
    expectedResponseBody.setMembers(addedMembers);

    given(houseMemberMapper.houseMemberDtoSetToHouseMemberSet(testMembersDto))
        .willReturn(testMembers);
    given(houseService.addHouseMembers(TEST_HOUSE_ID, testMembers)).
        willReturn(testMembers);
    given(houseMemberMapper.houseMemberSetToRestApiResponseAddHouseMemberSet(testMembers))
        .willReturn(addedMembers);

    // when
    ResponseEntity<AddHouseMemberResponse> response =
        houseController.addHouseMembers(TEST_HOUSE_ID, request);

    // then
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals(expectedResponseBody, response.getBody());
    verify(houseMemberMapper).houseMemberDtoSetToHouseMemberSet(testMembersDto);
    verify(houseService).addHouseMembers(TEST_HOUSE_ID, testMembers);
    verify(houseMemberMapper).houseMemberSetToRestApiResponseAddHouseMemberSet(testMembers);
  }

  /**
   * Tests the scenario where no house members are added to a house. It verifies that
   * a NOT_FOUND response is returned and that the house service is called with the
   * test members.
   */
  @Test
  void addHouseMembersNoMembersAdded() {
    // given
    Set<HouseMember> testMembers = TestUtils.HouseMemberHelpers.getTestHouseMembers(TEST_HOUSE_MEMBERS_COUNT);
    Set<HouseMemberDto> testMembersDto = testMembers.stream()
        .map(member -> new HouseMemberDto()
            .memberId(member.getMemberId())
            .name(member.getName())
        )
        .collect(Collectors.toSet());

    AddHouseMemberRequest request = new AddHouseMemberRequest().members(testMembersDto);

    Set<com.myhome.model.HouseMember> addedMembers = testMembers.stream()
        .map(member -> new com.myhome.model.HouseMember()
            .memberId(member.getMemberId())
            .name(member.getName()))
        .collect(Collectors.toSet());

    AddHouseMemberResponse expectedResponseBody = new AddHouseMemberResponse();
    expectedResponseBody.setMembers(addedMembers);

    given(houseMemberMapper.houseMemberDtoSetToHouseMemberSet(testMembersDto))
        .willReturn(testMembers);
    given(houseService.addHouseMembers(TEST_HOUSE_ID, testMembers)).
        willReturn(new HashSet<>());

    // when
    ResponseEntity<AddHouseMemberResponse> response =
        houseController.addHouseMembers(TEST_HOUSE_ID, request);

    // then
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertNull(response.getBody());
    verify(houseMemberMapper).houseMemberDtoSetToHouseMemberSet(testMembersDto);
    verify(houseService).addHouseMembers(TEST_HOUSE_ID, testMembers);
    verify(houseMemberMapper, never()).houseMemberSetToRestApiResponseAddHouseMemberSet(
        testMembers);
  }

  /**
   * Tests the deletion of a house member by calling the `deleteHouseMember` controller
   * method with given test IDs, verifying a successful response with a 204 status code
   * and no response body.
   */
  @Test
  void deleteHouseMemberSuccess() {
    // given
    given(houseService.deleteMemberFromHouse(TEST_HOUSE_ID, TEST_MEMBER_ID))
        .willReturn(true);
    // when
    ResponseEntity<Void> response =
        houseController.deleteHouseMember(TEST_HOUSE_ID, TEST_MEMBER_ID);

    // then
    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    assertNull(response.getBody());
  }

  /**
   * Tests the deletion of a house member when the deletion fails, verifying that a 404
   * status code and no response body are returned.
   */
  @Test
  void deleteHouseMemberFailure() {
    // given
    given(houseService.deleteMemberFromHouse(TEST_HOUSE_ID, TEST_MEMBER_ID))
        .willReturn(false);

    // when
    ResponseEntity<Void> response =
        houseController.deleteHouseMember(TEST_HOUSE_ID, TEST_MEMBER_ID);

    // then
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertNull(response.getBody());
  }

}