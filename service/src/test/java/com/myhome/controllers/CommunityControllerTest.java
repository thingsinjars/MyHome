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

import com.myhome.controllers.dto.CommunityDto;
import com.myhome.controllers.dto.UserDto;
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
import com.myhome.model.GetHouseDetailsResponseCommunityHouse;
import com.myhome.model.ListCommunityAdminsResponse;
import com.myhome.model.ListCommunityAdminsResponseCommunityAdmin;
import com.myhome.services.CommunityService;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

/**
 * Provides
 * ````
 * Create a
 */
class CommunityControllerTest {
  private static final String COMMUNITY_ADMIN_ID = "1";
  private static final String COMMUNITY_ADMIN_NAME = "Test Name";
  private static final String COMMUNITY_ADMIN_EMAIL = "testadmin@myhome.com";
  private static final String COMMUNITY_ADMIN_PASSWORD = "testpassword@myhome.com";
  private static final String COMMUNITY_HOUSE_ID = "2";
  private static final String COMMUNITY_HOUSE_NAME = "Test House";
  private static final String COMMUNITY_NAME = "Test Community";
  private static final String COMMUNITY_ID = "3";
  private static final String COMMUNITY_DISTRICT = "Wonderland";

  @Mock
  private CommunityService communityService;

  @Mock
  private CommunityApiMapper communityApiMapper;

  @InjectMocks
  private CommunityController communityController;

  /**
   * Is called before each test, initializing Mockito annotations to mock objects.
   */
  @BeforeEach
  private void init() {
    MockitoAnnotations.initMocks(this);
  }

  /**
   * Creates a test CommunityDto object with a predefined community ID, name, district,
   * and a set of community admins. The community admin is a UserDto object with a
   * predefined user ID, name, email, password, and a set of community IDs.
   *
   * @returns a `CommunityDto` object with a set of admins containing a single `UserDto`.
   *
   * The returned output is a `CommunityDto` object with the following properties:
   * - `communityId`: a unique identifier for the community, set to `COMMUNITY_ID`.
   * - `name`: the name of the community, set to `COMMUNITY_NAME`.
   * - `district`: the district of the community, set to `COMMUNITY_DISTRICT`.
   * - `admins`: a set of community administrators, containing a single `UserDto` with
   * `userId` `COMMUNITY_ADMIN_ID`.
   */
  private CommunityDto createTestCommunityDto() {
    Set<UserDto> communityAdminDtos = new HashSet<>();
    UserDto userDto = UserDto.builder()
        .userId(COMMUNITY_ADMIN_ID)
        .name(COMMUNITY_ADMIN_NAME)
        .email(COMMUNITY_ADMIN_NAME)
        .password(COMMUNITY_ADMIN_PASSWORD)
        .communityIds(new HashSet<>(singletonList(COMMUNITY_ID)))
        .build();

    communityAdminDtos.add(userDto);
    CommunityDto communityDto = new CommunityDto();
    communityDto.setCommunityId(COMMUNITY_ID);
    communityDto.setName(COMMUNITY_NAME);
    communityDto.setDistrict(COMMUNITY_DISTRICT);
    communityDto.setAdmins(communityAdminDtos);

    return communityDto;
  }

  /**
   * Creates a new instance of `CommunityHouse` with the specified `community`, name,
   * ID, empty sets of members and visitors. The function returns the created
   * `CommunityHouse` object.
   *
   * @param community parent community to which the created `CommunityHouse` belongs.
   *
   * @returns a new `CommunityHouse` object with specified properties and empty set collections.
   */
  private CommunityHouse createTestCommunityHouse(Community community) {
    return new CommunityHouse(community, COMMUNITY_HOUSE_NAME, COMMUNITY_HOUSE_ID, new HashSet<>(),
        new HashSet<>());
  }

  /**
   * Creates a test community with specified attributes, adds an administrator user to
   * the community, adds a test community house to the community, and links the community
   * to the administrator.
   *
   * @returns a fully initialized Community object with associated User and House entities.
   *
   * The returned output is a Community object with the following attributes:
   * a unique ID, a name, a district, a set of houses, a set of users (admins), a set
   * of users (residents), and a set of amenities.
   */
  private Community createTestCommunity() {
    Community community =
        new Community(new HashSet<>(), new HashSet<>(), COMMUNITY_NAME, COMMUNITY_ID,
            COMMUNITY_DISTRICT, new HashSet<>());
    User admin = new User(COMMUNITY_ADMIN_NAME, COMMUNITY_ADMIN_ID, COMMUNITY_ADMIN_EMAIL, true,
        COMMUNITY_ADMIN_PASSWORD, new HashSet<>(), null);
    community.getAdmins().add(admin);
    community.getHouses().add(createTestCommunityHouse(community));
    admin.getCommunities().add(community);

    return community;
  }

  /**
   * Tests the creation of a community by verifying a successful HTTP response and the
   * correct mapping of data between different layers. It mocks the community service
   * and mapper to isolate the community controller's functionality.
   */
  @Test
  void shouldCreateCommunitySuccessfully() {
    // given
    CreateCommunityRequest request =
        new CreateCommunityRequest()
            .name(COMMUNITY_NAME)
            .district(COMMUNITY_DISTRICT);
    CommunityDto communityDto = createTestCommunityDto();
    CreateCommunityResponse response =
        new CreateCommunityResponse()
            .communityId(COMMUNITY_ID);
    Community community = createTestCommunity();

    given(communityApiMapper.createCommunityRequestToCommunityDto(request))
        .willReturn(communityDto);
    given(communityService.createCommunity(communityDto))
        .willReturn(community);
    given(communityApiMapper.communityToCreateCommunityResponse(community))
        .willReturn(response);

    // when
    ResponseEntity<CreateCommunityResponse> responseEntity =
        communityController.createCommunity(request);

    // then
    assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
    assertEquals(response, responseEntity.getBody());
    verify(communityApiMapper).createCommunityRequestToCommunityDto(request);
    verify(communityApiMapper).communityToCreateCommunityResponse(community);
    verify(communityService).createCommunity(communityDto);
  }

  /**
   * Tests the listing of all communities by verifying that the community controller
   * returns a successful response with the expected community details when the community
   * service and mapper functions are called correctly.
   */
  @Test
  void shouldListAllCommunitiesSuccessfully() {
    // given
    Set<Community> communities = new HashSet<>();
    Community community = createTestCommunity();
    communities.add(community);

    Set<GetCommunityDetailsResponseCommunity> communityDetailsResponse
        = new HashSet<>();
    communityDetailsResponse.add(
        new GetCommunityDetailsResponseCommunity()
            .communityId(COMMUNITY_ID)
            .name(COMMUNITY_NAME)
            .district(COMMUNITY_DISTRICT)
    );

    GetCommunityDetailsResponse response = new GetCommunityDetailsResponse();
    response.getCommunities().addAll(communityDetailsResponse);

    Pageable pageable = PageRequest.of(0, 1);
    given(communityService.listAll(pageable))
        .willReturn(communities);
    given(communityApiMapper.communitySetToRestApiResponseCommunitySet(communities))
        .willReturn(communityDetailsResponse);

    // when
    ResponseEntity<GetCommunityDetailsResponse> responseEntity =
        communityController.listAllCommunity(pageable);

    // then
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(response, responseEntity.getBody());
    verify(communityApiMapper).communitySetToRestApiResponseCommunitySet(communities);
    verify(communityService).listAll(pageable);
  }

  /**
   * Tests the retrieval of community details by ID. It simulates a successful response
   * from the community service and API mapper, then verifies that the response entity
   * status and body match the expected values.
   */
  @Test
  void shouldGetCommunityDetailsSuccessfully() {
    // given
    Optional<Community> communityOptional = Optional.of(createTestCommunity());
    Community community = communityOptional.get();
    GetCommunityDetailsResponseCommunity communityDetails =
        new GetCommunityDetailsResponseCommunity()
            .communityId(COMMUNITY_ID)
            .name(COMMUNITY_NAME)
            .district(COMMUNITY_DISTRICT);

    Set<GetCommunityDetailsResponseCommunity> communityDetailsResponse
        = new HashSet<>();
    communityDetailsResponse.add(communityDetails);

    GetCommunityDetailsResponse response =
        new GetCommunityDetailsResponse().communities(communityDetailsResponse);

    given(communityService.getCommunityDetailsById(COMMUNITY_ID))
        .willReturn(communityOptional);
    given(communityApiMapper.communityToRestApiResponseCommunity(community))
        .willReturn(communityDetails);

    // when
    ResponseEntity<GetCommunityDetailsResponse> responseEntity =
        communityController.listCommunityDetails(COMMUNITY_ID);

    // then
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(response, responseEntity.getBody());
    verify(communityService).getCommunityDetailsById(COMMUNITY_ID);
    verify(communityApiMapper).communityToRestApiResponseCommunity(community);
  }

  /**
   * Tests the functionality of listing community details when the community with the
   * specified ID is not found. It verifies that a 404 Not Found response is returned
   * with an empty body and that the community service is called to retrieve the community
   * details.
   */
  @Test
  void shouldGetNotFoundListCommunityDetailsSuccess() {
    // given
    given(communityService.getCommunityDetailsById(COMMUNITY_ID))
        .willReturn(Optional.empty());

    // when
    ResponseEntity<GetCommunityDetailsResponse> responseEntity =
        communityController.listCommunityDetails(COMMUNITY_ID);

    // then
    assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    assertNull(responseEntity.getBody());
    verify(communityService).getCommunityDetailsById(COMMUNITY_ID);
    verifyNoInteractions(communityApiMapper);
  }

  /**
   * Tests the functionality of listing community admins by verifying the successful
   * response from the `communityController` when calling the `listCommunityAdmins` method.
   * It checks for a valid HTTP status code and the correctness of the response body.
   */
  @Test
  void shouldListCommunityAdminsSuccess() {
    // given
    Community community = createTestCommunity();
    List<User> admins = new ArrayList<>(community.getAdmins());
    Optional<List<User>> communityAdminsOptional = Optional.of(admins);

    Pageable pageable = PageRequest.of(0, 1);

    given(communityService.findCommunityAdminsById(COMMUNITY_ID, pageable))
        .willReturn(communityAdminsOptional);

    Set<User> adminsSet = new HashSet<>(admins);

    Set<ListCommunityAdminsResponseCommunityAdmin> listAdminsResponses = new HashSet<>();
    listAdminsResponses.add(
        new ListCommunityAdminsResponseCommunityAdmin()
            .adminId(COMMUNITY_ADMIN_ID)
    );

    given(communityApiMapper.communityAdminSetToRestApiResponseCommunityAdminSet(adminsSet))
        .willReturn(listAdminsResponses);

    ListCommunityAdminsResponse response =
        new ListCommunityAdminsResponse().admins(listAdminsResponses);

    // when
    ResponseEntity<ListCommunityAdminsResponse> responseEntity =
        communityController.listCommunityAdmins(COMMUNITY_ID, pageable);

    // then
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(response, responseEntity.getBody());
    verify(communityApiMapper).communityAdminSetToRestApiResponseCommunityAdminSet(adminsSet);
    verify(communityService).findCommunityAdminsById(COMMUNITY_ID, pageable);
  }

  /**
   * Tests the functionality of listing community admins when no admins are found. It
   * verifies that a 404 status code is returned and no admin details are present in
   * the response.
   */
  @Test
  void shouldReturnNoAdminDetailsNotFoundSuccess() {
    // given
    Pageable pageable = PageRequest.of(0, 1);

    given(communityService.findCommunityAdminsById(COMMUNITY_ID, pageable))
        .willReturn(Optional.empty());

    // when
    ResponseEntity<ListCommunityAdminsResponse> responseEntity =
        communityController.listCommunityAdmins(COMMUNITY_ID, pageable);

    // then
    assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    assertNull(responseEntity.getBody());
    verify(communityService).findCommunityAdminsById(COMMUNITY_ID, pageable);
    verifyNoInteractions(communityApiMapper);
  }

  /**
   * Tests the addition of community administrators. It creates a community and an add
   * request, adds existing community administrators to the request, and then simulates
   * a successful addition of administrators to the community via the community service.
   */
  @Test
  void shouldAddCommunityAdminSuccess() {
    // given
    AddCommunityAdminRequest addRequest = new AddCommunityAdminRequest();
    Community community = createTestCommunity();
    Set<User> communityAdmins = community.getAdmins();
    for (User admin : communityAdmins) {
      addRequest.getAdmins().add(admin.getUserId());
    }

    Set<String> adminIds = addRequest.getAdmins();
    AddCommunityAdminResponse response = new AddCommunityAdminResponse().admins(adminIds);

    given(communityService.addAdminsToCommunity(COMMUNITY_ID, adminIds))
        .willReturn(Optional.of(community));

    // when
    ResponseEntity<AddCommunityAdminResponse> responseEntity =
        communityController.addCommunityAdmins(COMMUNITY_ID, addRequest);

    // then
    assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
    assertEquals(response, responseEntity.getBody());
    verify(communityService).addAdminsToCommunity(COMMUNITY_ID, adminIds);
  }

  /**
   * Tests a scenario where an attempt is made to add admin users to a community that
   * does not exist. It verifies that the community controller returns a 404 status
   * code and an empty response body in this case.
   */
  @Test
  void shouldNotAddAdminToCommunityNotFoundSuccessfully() {
    // given
    AddCommunityAdminRequest addRequest = new AddCommunityAdminRequest();
    Community community = createTestCommunity();
    Set<User> communityAdmins = community.getAdmins();
    for (User admin : communityAdmins) {
      addRequest.getAdmins().add(admin.getUserId());
    }

    Set<String> adminIds = addRequest.getAdmins();

    given(communityService.addAdminsToCommunity(COMMUNITY_ID, adminIds))
        .willReturn(Optional.empty());

    // when
    ResponseEntity<AddCommunityAdminResponse> responseEntity =
        communityController.addCommunityAdmins(COMMUNITY_ID, addRequest);

    // then
    assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    assertNull(responseEntity.getBody());
    verify(communityService).addAdminsToCommunity(COMMUNITY_ID, adminIds);
  }

  /**
   * Tests the successful listing of community houses by verifying the response status
   * code and body, as well as the calls to the `communityService` and `communityApiMapper`
   * methods.
   */
  @Test
  void shouldListCommunityHousesSuccess() {
    Community community = createTestCommunity();
    List<CommunityHouse> houses = new ArrayList<>(community.getHouses());
    Set<CommunityHouse> housesSet = new HashSet<>(houses);
    Set<GetHouseDetailsResponseCommunityHouse> getHouseDetailsSet = new HashSet<>();
    getHouseDetailsSet.add(new GetHouseDetailsResponseCommunityHouse()
        .houseId(COMMUNITY_HOUSE_ID)
        .name(COMMUNITY_NAME)
    );

    GetHouseDetailsResponse response = new GetHouseDetailsResponse().houses(getHouseDetailsSet);
    Pageable pageable = PageRequest.of(0, 1);

    given(communityService.findCommunityHousesById(COMMUNITY_ID, pageable))
        .willReturn(Optional.of(houses));
    given(communityApiMapper.communityHouseSetToRestApiResponseCommunityHouseSet(housesSet))
        .willReturn(getHouseDetailsSet);

    // when
    ResponseEntity<GetHouseDetailsResponse> responseEntity =
        communityController.listCommunityHouses(COMMUNITY_ID, pageable);

    //then
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(response, responseEntity.getBody());
    verify(communityService).findCommunityHousesById(COMMUNITY_ID, pageable);
    verify(communityApiMapper).communityHouseSetToRestApiResponseCommunityHouseSet(housesSet);
  }

  /**
   * Tests the scenario where a community with the given ID does not exist, and the
   * controller returns a 404 status code with no response body. The service is called
   * to find community houses, and the mapper is not interacted with.
   */
  @Test
  void testListCommunityHousesCommunityNotExistSuccess() {
    // given
    Pageable pageable = PageRequest.of(0, 1);
    given(communityService.findCommunityHousesById(COMMUNITY_ID, pageable))
        .willReturn(Optional.empty());

    // when
    ResponseEntity<GetHouseDetailsResponse> responseEntity =
        communityController.listCommunityHouses(COMMUNITY_ID, pageable);

    // then
    assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    assertNull(responseEntity.getBody());
    verify(communityService).findCommunityHousesById(COMMUNITY_ID, pageable);
    verifyNoInteractions(communityApiMapper);
  }

  /**
   * Tests the successful addition of community houses to a community. It creates a
   * request to add houses, mocks API responses, and verifies that the houses are added
   * correctly, returning a 201 status code and the expected response.
   */
  @Test
  void shouldAddCommunityHouseSuccessfully() {
    // given
    AddCommunityHouseRequest addCommunityHouseRequest = new AddCommunityHouseRequest();
    Community community = createTestCommunity();
    Set<CommunityHouse> communityHouses = community.getHouses();
    Set<CommunityHouseName> communityHouseNames = new HashSet<>();
    communityHouseNames.add(new CommunityHouseName().name(COMMUNITY_HOUSE_NAME));

    Set<String> houseIds = new HashSet<>();
    for (CommunityHouse house : communityHouses) {
      houseIds.add(house.getHouseId());
    }

    addCommunityHouseRequest.getHouses().addAll(communityHouseNames);

    AddCommunityHouseResponse response = new AddCommunityHouseResponse().houses(houseIds);

    given(communityApiMapper.communityHouseNamesSetToCommunityHouseSet(communityHouseNames))
        .willReturn(communityHouses);
    given(communityService.addHousesToCommunity(COMMUNITY_ID, communityHouses))
        .willReturn(houseIds);

    // when
    ResponseEntity<AddCommunityHouseResponse> responseEntity =
        communityController.addCommunityHouses(COMMUNITY_ID, addCommunityHouseRequest);

    // then
    assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
    assertEquals(response, responseEntity.getBody());
    verify(communityApiMapper).communityHouseNamesSetToCommunityHouseSet(communityHouseNames);
    verify(communityService).addHousesToCommunity(COMMUNITY_ID, communityHouses);
  }

  /**
   * Tests that a bad request is returned when an empty `AddCommunityHouseRequest` is
   * sent to the `addCommunityHouses` method.
   */
  @Test
  void shouldThrowBadRequestWithEmptyAddHouseRequest() {
    // given
    AddCommunityHouseRequest emptyRequest = new AddCommunityHouseRequest();

    given(communityApiMapper.communityHouseNamesSetToCommunityHouseSet(emptyRequest.getHouses()))
        .willReturn(new HashSet<>());
    given(communityService.addHousesToCommunity(COMMUNITY_ID, new HashSet<>()))
        .willReturn(new HashSet<>());

    // when
    ResponseEntity<AddCommunityHouseResponse> responseEntity =
        communityController.addCommunityHouses(COMMUNITY_ID, emptyRequest);

    // then
    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    assertNull(responseEntity.getBody());
    verify(communityApiMapper).communityHouseNamesSetToCommunityHouseSet(new HashSet<>());
    verify(communityService).addHousesToCommunity(COMMUNITY_ID, new HashSet<>());
  }

  /**
   * Tests the removal of a community house by simulating a successful removal and
   * verifying the response status and service interactions.
   */
  @Test
  void shouldRemoveCommunityHouseSuccessfully() {
    // given
    Community community = createTestCommunity();

    given(communityService.getCommunityDetailsById(COMMUNITY_ID))
        .willReturn(Optional.of(community));
    given(communityService.removeHouseFromCommunityByHouseId(createTestCommunity(),
        COMMUNITY_HOUSE_ID))
        .willReturn(true);

    // when
    ResponseEntity<Void> responseEntity =
        communityController.removeCommunityHouse(COMMUNITY_ID, COMMUNITY_HOUSE_ID);

    // then
    assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
    verify(communityService).removeHouseFromCommunityByHouseId(community, COMMUNITY_HOUSE_ID);
    verify(communityService).getCommunityDetailsById(COMMUNITY_ID);
  }

  /**
   * Tests the removal of a community house when the community is not found. It expects
   * a 404 status code if the community is not found, and verifies that the community
   * service is called with the correct parameters.
   */
  @Test
  void shouldNotRemoveCommunityHouseIfNotFoundSuccessfully() {
    // given
    Community community = createTestCommunity();

    given(communityService.getCommunityDetailsById(COMMUNITY_ID))
        .willReturn(Optional.of(community));
    given(communityService.removeHouseFromCommunityByHouseId(community, COMMUNITY_HOUSE_ID))
        .willReturn(false);

    // when
    ResponseEntity<Void> responseEntity =
        communityController.removeCommunityHouse(COMMUNITY_ID, COMMUNITY_HOUSE_ID);

    // then
    assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    verify(communityService).removeHouseFromCommunityByHouseId(community, COMMUNITY_HOUSE_ID);
  }

  /**
   * Tests the removal of a community house when the community is not found.
   * It verifies that a 404 status code is returned and the community service is called
   * to retrieve the community details but not to remove the house.
   */
  @Test
  void shouldNotRemoveCommunityHouseIfCommunityNotFound() {
    //given
    Community community = createTestCommunity();

    given(communityService.getCommunityDetailsById(COMMUNITY_ID))
        .willReturn(Optional.empty());

    // when
    ResponseEntity<Void> responseEntity =
        communityController.removeCommunityHouse(COMMUNITY_ID, COMMUNITY_HOUSE_ID);

    // then
    assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    verify(communityService).getCommunityDetailsById(COMMUNITY_ID);
    verify(communityService, never()).removeHouseFromCommunityByHouseId(community,
        COMMUNITY_HOUSE_ID);
  }

  /**
   * Tests the removal of an admin from a community by verifying that the service returns
   * a successful response and the community service method is called correctly.
   */
  @Test
  void shouldRemoveAdminFromCommunitySuccessfully() {
    // given
    given(communityService.removeAdminFromCommunity(COMMUNITY_ID, COMMUNITY_ADMIN_ID))
        .willReturn(true);

    // when
    ResponseEntity<Void> responseEntity =
        communityController.removeAdminFromCommunity(COMMUNITY_ID, COMMUNITY_ADMIN_ID);

    // then
    assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
    verify(communityService).removeAdminFromCommunity(COMMUNITY_ID, COMMUNITY_ADMIN_ID);
  }

  /**
   * Tests the removal of an administrator from a community. It verifies that if the
   * administrator is not found, the community service is still called and a 404 status
   * code is returned. The test is successful if the expected behavior is observed.
   */
  @Test
  void shouldNotRemoveAdminIfNotFoundSuccessfully() {
    // given
    given(communityService.removeAdminFromCommunity(COMMUNITY_ID, COMMUNITY_ADMIN_ID))
        .willReturn(false);

    // when
    ResponseEntity<Void> responseEntity =
        communityController.removeAdminFromCommunity(COMMUNITY_ID, COMMUNITY_ADMIN_ID);

    // then
    assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    verify(communityService).removeAdminFromCommunity(COMMUNITY_ID, COMMUNITY_ADMIN_ID);
  }

  /**
   * Tests the deletion of a community by verifying that a successful response is
   * returned when calling the `deleteCommunity` method of the `communityController`.
   * The community service is also mocked to return true, indicating a successful deletion.
   */
  @Test
  void shouldDeleteCommunitySuccessfully() {
    // given
    given(communityService.deleteCommunity(COMMUNITY_ID))
        .willReturn(true);

    // when
    ResponseEntity<Void> responseEntity =
        communityController.deleteCommunity(COMMUNITY_ID);

    // then
    assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
    verify(communityService).deleteCommunity(COMMUNITY_ID);
  }

  /**
   * Tests the deletion of a non-existent community. It verifies that a `NOT_FOUND`
   * status is returned when attempting to delete a community that does not exist. The
   * `communityService` is checked to have been called with the correct community ID.
   */
  @Test
  void shouldNotDeleteCommunityNotFoundSuccessfully() {
    // given
    given(communityService.deleteCommunity(COMMUNITY_ID))
        .willReturn(false);

    // when
    ResponseEntity<Void> responseEntity =
        communityController.deleteCommunity(COMMUNITY_ID);

    // then
    assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    verify(communityService).deleteCommunity(COMMUNITY_ID);
  }

  /**
   * Creates a mock instance of `CommunityHouse` with specified name, ID, and an empty
   * set of house members.
   *
   * @returns an instance of CommunityHouse with specified name, ID, and an empty set
   * of members.
   */
  private CommunityHouse getMockCommunityHouse() {
    CommunityHouse communityHouse = new CommunityHouse();
    communityHouse.setName(COMMUNITY_HOUSE_NAME);
    communityHouse.setHouseId(COMMUNITY_HOUSE_ID);
    communityHouse.setHouseMembers(new HashSet<>());

    return communityHouse;
  }

  /**
   * Creates a mock community object with a specified set of admins, and adds an admin
   * user to the community and the community to the admin's list of communities.
   *
   * @param admins set of administrators for the community being created.
   *
   * Are destructured into a Set of User objects.
   *
   * @returns a Community object with specified properties and associations.
   *
   * The output is a `Community` object with the following attributes:
   * - A set of administrators, containing at least one admin user with specific details.
   * - An empty set of users.
   * - A specified name, ID, district, and a set of community houses.
   */
  private Community getMockCommunity(Set<User> admins) {
    Community community =
        new Community(admins, new HashSet<>(), COMMUNITY_NAME, COMMUNITY_ID,
            COMMUNITY_DISTRICT, new HashSet<>());
    User admin = new User(COMMUNITY_ADMIN_NAME, COMMUNITY_ADMIN_ID, COMMUNITY_ADMIN_EMAIL, true,
        COMMUNITY_ADMIN_PASSWORD, new HashSet<>(), new HashSet<>());
    community.getAdmins().add(admin);
    admin.getCommunities().add(community);

    CommunityHouse communityHouse = getMockCommunityHouse();
    communityHouse.setCommunity(community);
    community.getHouses().add(communityHouse);

    return community;
  }
}
