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

package com.myhome.services.unit;

import helpers.TestUtils;
import com.myhome.controllers.dto.CommunityDto;
import com.myhome.controllers.dto.mapper.CommunityMapper;
import com.myhome.domain.Community;
import com.myhome.domain.CommunityHouse;
import com.myhome.domain.HouseMember;
import com.myhome.domain.User;
import com.myhome.repositories.CommunityHouseRepository;
import com.myhome.repositories.CommunityRepository;
import com.myhome.repositories.UserRepository;
import com.myhome.services.HouseService;
import com.myhome.services.springdatajpa.CommunitySDJpaService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

/**
 * This isUse the following the Community testCommunity is notFound that this is
 *         are used in
 * Create a
 * Create a
 */
public class CommunitySDJpaServiceTest {

  private final String TEST_COMMUNITY_ID = "test-community-id";
  private final String TEST_COMMUNITY_NAME = "test-community-name";
  private final String TEST_COMMUNITY_DISTRICT = "test-community-name";

  private final int TEST_ADMINS_COUNT = 2;
  private final int TEST_HOUSES_COUNT = 2;
  private final int TEST_HOUSE_MEMBERS_COUNT = 2;
  private final int TEST_COMMUNITIES_COUNT = 2;

  private final String TEST_ADMIN_ID = "test-admin-id";
  private final String TEST_ADMIN_NAME = "test-user-name";
  private final String TEST_ADMIN_EMAIL = "test-user-email";
  private final String TEST_ADMIN_PASSWORD = "test-user-password";
  private final String TEST_HOUSE_ID = "test-house-id";

  @Mock
  private CommunityRepository communityRepository;
  @Mock
  private UserRepository communityAdminRepository;
  @Mock
  private CommunityMapper communityMapper;
  @Mock
  private CommunityHouseRepository communityHouseRepository;
  @Mock
  private HouseService houseService;

  @InjectMocks
  private CommunitySDJpaService communitySDJpaService;

  /**
   * Is a JUnit setup method, annotated with `@BeforeEach`, which initializes Mockito
   * annotations for the test class.
   * MockitoAnnotations.initMocks(this) sets up Mockito mock objects.
   */
  @BeforeEach
  private void init() {
    MockitoAnnotations.initMocks(this);
  }

  /**
   * Returns a new instance of the `User` class, initialized with predefined test
   * administrator data, including name, ID, email, password, and empty sets for roles
   * and permissions.
   *
   * @returns an instance of the User class with specified attributes.
   */
  private User getTestAdmin() {
    return new User(
        TEST_ADMIN_NAME,
        TEST_ADMIN_ID,
        TEST_ADMIN_EMAIL,
        false,
        TEST_ADMIN_PASSWORD,
        new HashSet<>(),
        new HashSet<>());
  }

  /**
   * Tests the retrieval of all communities from a data repository. It verifies that
   * the `communitySDJpaService` successfully retrieves a set of communities and that
   * the `communityRepository` is called with the `findAll` method.
   */
  @Test
  void listAllCommunities() {
    // given
    Set<Community> communities = TestUtils.CommunityHelpers.getTestCommunities(TEST_COMMUNITIES_COUNT);
    given(communityRepository.findAll())
        .willReturn(communities);

    // when
    Set<Community> resultCommunities = communitySDJpaService.listAll();

    // then
    assertEquals(communities, resultCommunities);
    verify(communityRepository).findAll();
  }

  /**
   * Tests the creation of a community by a community administrator. It verifies that
   * a community is successfully created with the provided details, and that the necessary
   * repository and mapper interactions occur correctly.
   */
  @Test
  void createCommunity() {
    // given
    CommunityDto testCommunityDto = getTestCommunityDto();
    Community testCommunity = TestUtils.CommunityHelpers.getTestCommunity(TEST_COMMUNITY_ID, TEST_COMMUNITY_NAME, TEST_COMMUNITY_DISTRICT, 0, 0);
    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(TEST_ADMIN_ID,
            null, Collections.emptyList());
    SecurityContextHolder.getContext().setAuthentication(authentication);

    given(communityMapper.communityDtoToCommunity(testCommunityDto))
        .willReturn(testCommunity);
    given(communityAdminRepository.findByUserIdWithCommunities(TEST_ADMIN_ID))
            .willReturn(Optional.of(getTestAdmin()));
    given(communityRepository.save(testCommunity))
        .willReturn(testCommunity);

    // when
    Community createdCommunity = communitySDJpaService.createCommunity(testCommunityDto);

    // then
    assertNotNull(createdCommunity);
    assertEquals(testCommunityDto.getName(), createdCommunity.getName());
    assertEquals(testCommunityDto.getDistrict(), createdCommunity.getDistrict());
    verify(communityMapper).communityDtoToCommunity(testCommunityDto);
    verify(communityAdminRepository).findByUserIdWithCommunities(TEST_ADMIN_ID);
    verify(communityRepository).save(testCommunity);
  }

  /**
   * Retrieves a list of community houses for a given community ID from the database
   * and returns it as an optional list, verifying that the community and community
   * houses exist in the database.
   */
  @Test
  void findCommunityHousesById() {
    // given
    Community testCommunity = TestUtils.CommunityHelpers.getTestCommunity();
    List<CommunityHouse> testCommunityHouses = new ArrayList<>(testCommunity.getHouses());
    given(communityRepository.existsByCommunityId(TEST_COMMUNITY_ID))
        .willReturn(true);
    given(communityHouseRepository.findAllByCommunity_CommunityId(TEST_COMMUNITY_ID, null))
        .willReturn(testCommunityHouses);

    // when
    Optional<List<CommunityHouse>> resultCommunityHousesOptional =
        communitySDJpaService.findCommunityHousesById(TEST_COMMUNITY_ID, null);

    // then
    assertTrue(resultCommunityHousesOptional.isPresent());
    List<CommunityHouse> resultCommunityHouses = resultCommunityHousesOptional.get();
    assertEquals(testCommunityHouses, resultCommunityHouses);
    verify(communityRepository).existsByCommunityId(TEST_COMMUNITY_ID);
    verify(communityHouseRepository).findAllByCommunity_CommunityId(TEST_COMMUNITY_ID, null);
  }

  /**
   * Tests the retrieval of community houses by a non-existent community ID. It asserts
   * that an empty result is returned and verifies that the community repository is
   * queried for existence, but the community house repository is not queried for data.
   */
  @Test
  void findCommunityHousesByIdNotExist() {
    // given
    given(communityRepository.existsByCommunityId(TEST_COMMUNITY_ID))
        .willReturn(false);

    // when
    Optional<List<CommunityHouse>> resultCommunityHousesOptional =
        communitySDJpaService.findCommunityHousesById(TEST_COMMUNITY_ID, null);

    // then
    assertFalse(resultCommunityHousesOptional.isPresent());
    verify(communityRepository).existsByCommunityId(TEST_COMMUNITY_ID);
    verify(communityHouseRepository, never()).findAllByCommunity_CommunityId(TEST_COMMUNITY_ID,
        null);
  }

  /**
   * Returns a list of community administrators corresponding to a given community ID,
   * utilizing a service layer to interact with the community and community admin repositories.
   */
  @Test
  void findCommunityAdminsById() {
    // given
    Community testCommunity = TestUtils.CommunityHelpers.getTestCommunity();
    List<User> testCommunityAdmins = new ArrayList<>(testCommunity.getAdmins());
    given(communityRepository.existsByCommunityId(TEST_COMMUNITY_ID))
        .willReturn(true);
    given(communityAdminRepository.findAllByCommunities_CommunityId(TEST_COMMUNITY_ID, null))
        .willReturn(testCommunityAdmins);

    // when
    Optional<List<User>> resultAdminsOptional =
        communitySDJpaService.findCommunityAdminsById(TEST_COMMUNITY_ID, null);

    // then
    assertTrue((resultAdminsOptional.isPresent()));
    List<User> resultAdmins = resultAdminsOptional.get();
    assertEquals(testCommunityAdmins, resultAdmins);
    verify(communityRepository).existsByCommunityId(TEST_COMMUNITY_ID);
    verify(communityAdminRepository).findAllByCommunities_CommunityId(TEST_COMMUNITY_ID, null);
  }

  /**
   * Tests the retrieval of community admins for a non-existent community ID, verifying
   * that an empty result is returned and the community repository's `existsByCommunityId`
   * method is called.
   */
  @Test
  void findCommunityAdminsByIdNotExists() {
    // given
    given(communityRepository.existsByCommunityId(TEST_COMMUNITY_ID))
        .willReturn(false);

    // when
    Optional<List<User>> resultAdminsOptional =
        communitySDJpaService.findCommunityAdminsById(TEST_COMMUNITY_ID, null);

    // then
    assertFalse((resultAdminsOptional.isPresent()));
    verify(communityRepository).existsByCommunityId(TEST_COMMUNITY_ID);
  }

  /**
   * Adds administrators to a community. It takes a community ID and a set of administrator
   * IDs, updates the community with the new administrators, and verifies that the
   * administrators are associated with the community.
   */
  @Test
  void addAdminsToCommunity() {
    // given
    Community testCommunity = TestUtils.CommunityHelpers.getTestCommunity();
    Set<User> adminToAdd = TestUtils.UserHelpers.getTestUsers(TEST_ADMINS_COUNT);
    Set<String> adminToAddIds = adminToAdd.stream()
        .map(admin -> admin.getUserId())
        .collect(Collectors.toSet());

    given(communityRepository.findByCommunityIdWithAdmins(TEST_COMMUNITY_ID))
        .willReturn(Optional.of(testCommunity));
    given(communityRepository.save(testCommunity))
        .willReturn(testCommunity);
    adminToAdd.forEach(admin -> {
      given(communityAdminRepository.findByUserIdWithCommunities(admin.getUserId()))
          .willReturn(Optional.of(admin));
    });
    adminToAdd.forEach(admin -> {
      given(communityAdminRepository.save(admin))
          .willReturn(admin);
    });
    // when
    Optional<Community> updatedCommunityOptional =
        communitySDJpaService.addAdminsToCommunity(TEST_COMMUNITY_ID, adminToAddIds);

    // then
    assertTrue(updatedCommunityOptional.isPresent());
    adminToAdd.forEach(admin -> assertTrue(admin.getCommunities().contains(testCommunity)));
    verify(communityRepository).findByCommunityIdWithAdmins(TEST_COMMUNITY_ID);
    adminToAdd.forEach(
        admin -> verify(communityAdminRepository).findByUserIdWithCommunities(admin.getUserId()));
  }

  /**
   * Tests the addition of admins to a non-existent community by verifying that the
   * community repository is called and that an empty result is returned when the
   * community does not exist.
   */
  @Test
  void addAdminsToCommunityNotExist() {
    // given
    given(communityRepository.findByCommunityIdWithAdmins(TEST_COMMUNITY_ID))
        .willReturn(Optional.empty());

    // when
    Optional<Community> updatedCommunityOptional =
        communitySDJpaService.addAdminsToCommunity(TEST_COMMUNITY_ID, any());

    // then
    assertFalse(updatedCommunityOptional.isPresent());
    verify(communityRepository).findByCommunityIdWithAdmins(TEST_COMMUNITY_ID);
  }

  /**
   * Tests the retrieval of a community by its ID from a data repository. It verifies
   * that the repository returns the expected community object when provided with a
   * valid ID.
   */
  @Test
  void communityDetailsById() {
    // given
    Community testCommunity = TestUtils.CommunityHelpers.getTestCommunity();
    given(communityRepository.findByCommunityId(TEST_COMMUNITY_ID))
        .willReturn(Optional.of(testCommunity));

    // when
    Optional<Community> communityOptional =
        communitySDJpaService.getCommunityDetailsById(TEST_COMMUNITY_ID);

    // then
    assertTrue(communityOptional.isPresent());
    assertEquals(testCommunity, communityOptional.get());
    verify(communityRepository).findByCommunityId(TEST_COMMUNITY_ID);
  }

  /**
   * Retrieves community details by ID along with associated administrators from a
   * database repository. It uses a service layer to encapsulate the database interaction
   * and verify the repository's behavior. The result is an optional community object.
   */
  @Test
  void communityDetailsByIdWithAdmins() {
    // given
    Community testCommunity = TestUtils.CommunityHelpers.getTestCommunity();
    given(communityRepository.findByCommunityIdWithAdmins(TEST_COMMUNITY_ID))
        .willReturn(Optional.of(testCommunity));

    // when
    Optional<Community> communityOptional =
        communitySDJpaService.getCommunityDetailsByIdWithAdmins(TEST_COMMUNITY_ID);

    // then
    assertTrue(communityOptional.isPresent());
    assertEquals(testCommunity, communityOptional.get());
    verify(communityRepository).findByCommunityIdWithAdmins(TEST_COMMUNITY_ID);
  }

  /**
   * Adds a specified number of houses to a community, saves the updated community, and
   * returns the IDs of the added houses.
   */
  @Test
  void addHousesToCommunity() {
    // given
    Community testCommunity = TestUtils.CommunityHelpers.getTestCommunity();
    Set<CommunityHouse> housesToAdd = TestUtils.CommunityHouseHelpers.getTestHouses(TEST_HOUSES_COUNT);

    given(communityRepository.findByCommunityIdWithHouses(TEST_COMMUNITY_ID))
        .willReturn(Optional.of(testCommunity));
    given(communityRepository.save(testCommunity))
        .willReturn(testCommunity);
    housesToAdd.forEach(house -> {
      given(communityHouseRepository.save(house))
          .willReturn(house);
    });

    // when
    Set<String> addedHousesIds =
        communitySDJpaService.addHousesToCommunity(TEST_COMMUNITY_ID, housesToAdd);

    // then
    assertEquals(housesToAdd.size(), addedHousesIds.size());
    housesToAdd.forEach(house -> {
      assertEquals(house.getCommunity(), testCommunity);
    });
    verify(communityRepository).findByCommunityIdWithHouses(TEST_COMMUNITY_ID);
    housesToAdd.forEach(house -> {
      verify(communityHouseRepository).save(house);
    });
  }

  /**
   * Checks if adding houses to a non-existent community results in no added houses and
   * verifies that database operations are not performed.
   */
  @Test
  void addHousesToCommunityNotExist() {
    // given
    Set<CommunityHouse> housesToAdd = TestUtils.CommunityHouseHelpers.getTestHouses(TEST_HOUSES_COUNT);

    given(communityRepository.findByCommunityIdWithHouses(TEST_COMMUNITY_ID))
        .willReturn(Optional.empty());

    // when
    Set<String> addedHousesIds =
        communitySDJpaService.addHousesToCommunity(TEST_COMMUNITY_ID, housesToAdd);

    // then
    assertTrue(addedHousesIds.isEmpty());
    verify(communityRepository).findByCommunityIdWithHouses(TEST_COMMUNITY_ID);
    verify(communityRepository, never()).save(any());
    verify(communityHouseRepository, never()).save(any());
  }

  /**
   * Tests the functionality of adding houses to a community that already exists. It
   * verifies that no new houses are saved to the database when a community with existing
   * houses is updated.
   */
  @Test
  void addHousesToCommunityHouseExists() {
    // given
    Community testCommunity = TestUtils.CommunityHelpers.getTestCommunity();
    Set<CommunityHouse> houses = TestUtils.CommunityHouseHelpers.getTestHouses(TEST_HOUSES_COUNT);
    testCommunity.setHouses(houses);

    given(communityRepository.findByCommunityIdWithHouses(TEST_COMMUNITY_ID))
        .willReturn(Optional.of(testCommunity));
    given(communityRepository.save(testCommunity))
        .willReturn(testCommunity);
    houses.forEach(house -> given(communityHouseRepository.save(house)).willReturn(house));

    // when
    Set<String> addedHousesIds =
        communitySDJpaService.addHousesToCommunity(TEST_COMMUNITY_ID, houses);

    // then
    assertTrue(addedHousesIds.isEmpty());
    verify(communityRepository).findByCommunityIdWithHouses(TEST_COMMUNITY_ID);
    verify(communityRepository).save(testCommunity);
    verify(communityHouseRepository, never()).save(any());
  }

  /**
   * Removes an admin from a community by community ID and admin ID, updating the
   * community repository accordingly, and returns a boolean indicating successful removal.
   */
  @Test
  void removeAdminFromCommunity() {
    // given
    Community testCommunity = TestUtils.CommunityHelpers.getTestCommunity();
    User testAdmin = getTestAdmin();
    testCommunity.getAdmins().add(testAdmin);

    given(communityRepository.findByCommunityIdWithAdmins(TEST_COMMUNITY_ID))
        .willReturn(Optional.of(testCommunity));
    given(communityRepository.save(testCommunity))
        .willReturn(testCommunity);

    // when
    boolean adminRemoved =
        communitySDJpaService.removeAdminFromCommunity(TEST_COMMUNITY_ID, TEST_ADMIN_ID);

    // then
    assertTrue(adminRemoved);
    verify(communityRepository).findByCommunityIdWithAdmins(TEST_COMMUNITY_ID);
    verify(communityRepository).save(testCommunity);
  }

  /**
   * Tests the removal of an admin from a non-existent community. It verifies that the
   * function returns false when the community does not exist, and that the repository
   * is queried but not saved.
   */
  @Test
  void removeAdminFromCommunityNotExists() {
    // given
    given(communityRepository.findByCommunityIdWithAdmins(TEST_COMMUNITY_ID))
        .willReturn(Optional.empty());

    // when
    boolean adminRemoved =
        communitySDJpaService.removeAdminFromCommunity(TEST_COMMUNITY_ID, TEST_ADMIN_ID);

    // then
    assertFalse(adminRemoved);
    verify(communityRepository).findByCommunityIdWithAdmins(TEST_COMMUNITY_ID);
    verify(communityRepository, never()).save(any());
  }

  /**
   * Tests the removal of an admin from a community when the admin does not exist. It
   * checks if the community service returns false and verifies that the repository is
   * queried but not updated.
   */
  @Test
  void removeAdminFromCommunityAdminNotExists() {
    // given
    Community testCommunity = TestUtils.CommunityHelpers.getTestCommunity();

    given(communityRepository.findByCommunityIdWithAdmins(TEST_COMMUNITY_ID))
        .willReturn(Optional.of(testCommunity));
    given(communityRepository.save(testCommunity))
        .willReturn(testCommunity);

    // when
    boolean adminRemoved =
        communitySDJpaService.removeAdminFromCommunity(TEST_COMMUNITY_ID, TEST_ADMIN_ID);

    // then
    assertFalse(adminRemoved);
    verify(communityRepository).findByCommunityIdWithAdmins(TEST_COMMUNITY_ID);
    verify(communityRepository, never()).save(testCommunity);
  }

  /**
   * Tests the deletion of a community with its associated houses from the database.
   * It verifies that the community is successfully deleted and that the correct database
   * operations are performed.
   */
  @Test
  void deleteCommunity() {
    // given
    Community testCommunity = TestUtils.CommunityHelpers.getTestCommunity();
    Set<CommunityHouse> testCommunityHouses = TestUtils.CommunityHouseHelpers.getTestHouses(TEST_HOUSES_COUNT);
    testCommunity.setHouses(testCommunityHouses);

    given(communityRepository.findByCommunityIdWithHouses(TEST_COMMUNITY_ID))
        .willReturn(Optional.of(testCommunity));
    testCommunityHouses.forEach(house -> {
      given(communityHouseRepository.findByHouseId(house.getHouseId()))
          .willReturn(Optional.of(house));
    });

    testCommunityHouses.forEach(house -> {
      given(communityHouseRepository.findByHouseId(house.getHouseId()))
          .willReturn(Optional.of(house));
    });

    // when
    boolean communityDeleted = communitySDJpaService.deleteCommunity(TEST_COMMUNITY_ID);

    // then
    assertTrue(communityDeleted);
    verify(communityRepository).findByCommunityIdWithHouses(TEST_COMMUNITY_ID);
    verify(communityRepository).delete(testCommunity);
  }

  /**
   * Tests the deletion of a non-existent community. It simulates a community repository
   * that returns an empty optional when searching for a community by ID, and verifies
   * that the community is not deleted and the expected database interactions do not occur.
   */
  @Test
  void deleteCommunityNotExists() {
    // given
    Community testCommunity = TestUtils.CommunityHelpers.getTestCommunity();

    given(communityRepository.findByCommunityIdWithHouses(TEST_COMMUNITY_ID))
        .willReturn(Optional.empty());

    // when
    boolean communityDeleted = communitySDJpaService.deleteCommunity(TEST_COMMUNITY_ID);

    // then
    assertFalse(communityDeleted);
    verify(communityRepository).findByCommunityIdWithHouses(TEST_COMMUNITY_ID);
    verify(communityHouseRepository, never()).deleteByHouseId(any());
    verify(communityRepository, never()).delete(testCommunity);
  }

  /**
   * Removes a house from a community by its ID, deleting its associated house members
   * and updating the community repository.
   */
  @Test
  void removeHouseFromCommunityByHouseId() {
    // given
    Community testCommunity = TestUtils.CommunityHelpers.getTestCommunity();
    CommunityHouse testHouse = TestUtils.CommunityHouseHelpers.getTestCommunityHouse(TEST_HOUSE_ID);
    Set<HouseMember> testHouseMembers = TestUtils.HouseMemberHelpers.getTestHouseMembers(TEST_HOUSE_MEMBERS_COUNT);
    testHouse.setHouseMembers(testHouseMembers);
    testCommunity.getHouses().add(testHouse);

    given(communityRepository.findByCommunityIdWithHouses(TEST_COMMUNITY_ID))
        .willReturn(Optional.of(testCommunity));
    given(communityHouseRepository.findByHouseIdWithHouseMembers(TEST_HOUSE_ID))
        .willReturn(Optional.of(testHouse));

    // when
    boolean houseDeleted =
        communitySDJpaService.removeHouseFromCommunityByHouseId(testCommunity, TEST_HOUSE_ID);

    // then
    assertTrue(houseDeleted);
    assertFalse(testCommunity.getHouses().contains(testHouse));
    verify(communityRepository).save(testCommunity);
    testHouse.getHouseMembers()
        .forEach(houseMember -> verify(houseService).deleteMemberFromHouse(TEST_HOUSE_ID,
            houseMember.getMemberId()));
    verify(communityHouseRepository).findByHouseIdWithHouseMembers(TEST_HOUSE_ID);
    verify(communityHouseRepository).deleteByHouseId(TEST_HOUSE_ID);
  }

  /**
   * Tests the removal of a house from a non-existent community by house ID. It verifies
   * that the function returns false when the community does not exist and that the
   * correct repositories are not interacted with.
   */
  @Test
  void removeHouseFromCommunityByHouseIdCommunityNotExists() {
    // given
    Community testCommunity = TestUtils.CommunityHelpers.getTestCommunity();

    given(communityRepository.findByCommunityIdWithHouses(TEST_COMMUNITY_ID))
        .willReturn(Optional.empty());

    // when
    boolean houseDeleted =
        communitySDJpaService.removeHouseFromCommunityByHouseId(null, TEST_HOUSE_ID);

    // then
    assertFalse(houseDeleted);
    verify(communityHouseRepository, never()).findByHouseId(TEST_HOUSE_ID);
    verifyNoInteractions(houseService);
    verify(communityRepository, never()).save(testCommunity);
  }

  /**
   * Tests the removal of a non-existent house from a community. It verifies that the
   * house deletion is not successful, the community house repository is called, the
   * house service is not interacted with, and the community is not saved.
   */
  @Test
  void removeHouseFromCommunityByHouseIdHouseNotExists() {
    // given
    Community testCommunity = TestUtils.CommunityHelpers.getTestCommunity();

    given(communityHouseRepository.findByHouseIdWithHouseMembers(TEST_HOUSE_ID))
        .willReturn(Optional.empty());

    // when
    boolean houseDeleted =
        communitySDJpaService.removeHouseFromCommunityByHouseId(testCommunity, TEST_HOUSE_ID);

    // then
    assertFalse(houseDeleted);
    verify(communityHouseRepository).findByHouseIdWithHouseMembers(TEST_HOUSE_ID);
    verifyNoInteractions(houseService);
    verify(communityRepository, never()).save(testCommunity);
  }

  /**
   * Tests the removal of a house from a community when the house is not present in the
   * community. It checks that the house is not deleted and that the community repository
   * is queried accordingly. The community service saves the community after deletion.
   */
  @Test
  void removeHouseFromCommunityByHouseIdHouseNotInCommunity() {
    // given
    Community testCommunity = TestUtils.CommunityHelpers.getTestCommunity();

    given(communityHouseRepository.findByHouseIdWithHouseMembers(TEST_HOUSE_ID))
        .willReturn(Optional.empty());

    // when
    boolean houseDeleted =
        communitySDJpaService.removeHouseFromCommunityByHouseId(testCommunity, TEST_HOUSE_ID);

    // then
    assertFalse(houseDeleted);
    verify(communityHouseRepository).findByHouseIdWithHouseMembers(TEST_HOUSE_ID);
    verifyNoInteractions(houseService);
    verify(communityRepository, never()).save(testCommunity);
  }

  /**
   * Creates a CommunityDto object with predefined attributes, specifically a community
   * ID, district, and name, and returns it. The attributes are assumed to be constants
   * or predefined variables. This function is likely used for testing purposes.
   *
   * @returns a CommunityDto object populated with TEST_COMMUNITY_ID, TEST_COMMUNITY_DISTRICT,
   * and TEST_COMMUNITY_NAME.
   */
  private CommunityDto getTestCommunityDto() {
    CommunityDto testCommunityDto = new CommunityDto();
    testCommunityDto.setCommunityId(TEST_COMMUNITY_ID);
    testCommunityDto.setDistrict(TEST_COMMUNITY_DISTRICT);
    testCommunityDto.setName(TEST_COMMUNITY_NAME);
    return testCommunityDto;
  }

}
