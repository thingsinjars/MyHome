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

import com.myhome.controllers.mapper.AmenityApiMapper;
import com.myhome.domain.Amenity;
import com.myhome.domain.Community;
import com.myhome.model.AmenityDto;
import com.myhome.repositories.AmenityRepository;
import com.myhome.repositories.CommunityRepository;
import com.myhome.services.CommunityService;
import com.myhome.services.springdatajpa.AmenitySDJpaService;
import helpers.TestUtils;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

/**
 * This class contains unit tests for the AmenitySDJpaService class, which appears
 * to be a data access object for managing amenities in a community. The tests cover
 * various scenarios, including deleting amenities, listing amenities, adding amenities
 * to communities, and updating amenities.
 */
class AmenitySDJpaServiceTest {

  private static final String TEST_AMENITY_NAME = "test-amenity-name";
  private static final BigDecimal TEST_AMENITY_PRICE = BigDecimal.valueOf(1);
  private final String TEST_AMENITY_ID = "test-amenity-id";
  private final String TEST_AMENITY_DESCRIPTION = "test-amenity-description";
  private final String TEST_COMMUNITY_ID = "test-community-id";
  private final int TEST_AMENITIES_COUNT = 2;
  @Mock
  private AmenityRepository amenityRepository;
  @Mock
  private CommunityRepository communityRepository;
  @Mock
  private CommunityService communityService;
  @Mock
  private AmenityApiMapper amenityApiMapper;

  @InjectMocks
  private AmenitySDJpaService amenitySDJpaService;

  /**
   * Initializes Mockito annotations, allowing for the creation of mock objects in test
   * classes.
   */
  @BeforeEach
  private void init() {
    MockitoAnnotations.initMocks(this);
  }

  /**
   * Tests the deletion of an amenity from the database. It simulates a test amenity's
   * existence, calls the `deleteAmenity` service method, and verifies that the amenity
   * is successfully deleted and the repository methods are called as expected.
   */
  @Test
  void deleteAmenity() {
    // given
    Amenity testAmenity =
        TestUtils.AmenityHelpers.getTestAmenity(TEST_AMENITY_ID, TEST_AMENITY_DESCRIPTION);

    given(amenityRepository.findByAmenityIdWithCommunity(TEST_AMENITY_ID))
        .willReturn(Optional.of(testAmenity));

    // when
    boolean amenityDeleted = amenitySDJpaService.deleteAmenity(TEST_AMENITY_ID);

    // then
    assertTrue(amenityDeleted);
    verify(amenityRepository).findByAmenityIdWithCommunity(TEST_AMENITY_ID);
    verify(amenityRepository).delete(testAmenity);
  }

  /**
   * Tests the deletion of an amenity that does not exist in the database. It verifies
   * that the repository is queried to check if the amenity exists, but the deletion
   * is not performed. The test expects a return value of false indicating the amenity
   * was not deleted.
   */
  @Test
  void deleteAmenityNotExists() {
    // given
    given(amenityRepository.findByAmenityIdWithCommunity(TEST_AMENITY_ID))
        .willReturn(Optional.empty());

    // when
    boolean amenityDeleted = amenitySDJpaService.deleteAmenity(TEST_AMENITY_ID);

    // then
    assertFalse(amenityDeleted);
    verify(amenityRepository).findByAmenityIdWithCommunity(TEST_AMENITY_ID);
    verify(amenityRepository, never()).delete(any());
  }

  /**
   * Retrieves a set of amenities for a specified community ID from the database and
   * returns them. It uses a mock repository to simulate a database query. The retrieved
   * amenities are compared to the expected set for verification.
   */
  @Test
  void listAllAmenities() {
    // given
    Set<Amenity> testAmenities = TestUtils.AmenityHelpers.getTestAmenities(TEST_AMENITIES_COUNT);
    Community testCommunity = TestUtils.CommunityHelpers.getTestCommunity();
    testCommunity.setAmenities(testAmenities);

    given(communityRepository.findByCommunityIdWithAmenities(TEST_COMMUNITY_ID))
        .willReturn(Optional.of(testCommunity));

    // when
    Set<Amenity> resultAmenities = amenitySDJpaService.listAllAmenities(TEST_COMMUNITY_ID);

    // then
    assertEquals(testAmenities, resultAmenities);
    verify(communityRepository).findByCommunityIdWithAmenities(TEST_COMMUNITY_ID);
  }

  /**
   * Tests the `listAllAmenities` method when no amenities exist for a given community
   * ID. It verifies that the method returns an empty set and that the `communityRepository`
   * is called with the correct method and parameters.
   */
  @Test
  void listAllAmenitiesNotExists() {
    // given
    given(communityRepository.findByCommunityIdWithAmenities(TEST_COMMUNITY_ID))
        .willReturn(Optional.empty());

    // when
    Set<Amenity> resultAmenities = amenitySDJpaService.listAllAmenities(TEST_COMMUNITY_ID);

    // then
    assertEquals(new HashSet<>(), resultAmenities);
    verify(communityRepository).findByCommunityIdWithAmenities(TEST_COMMUNITY_ID);
  }

  /**
   * Tests the creation of amenities for an existing community. It verifies that a
   * community ID is associated with an amenity, and the amenity is successfully saved
   * to the repository.
   */
  @Test
  void shouldAddAmenityToExistingCommunity() {
    // given
    final String communityId = "communityId";
    final Community community = new Community().withCommunityId(communityId);
    final AmenityDto baseAmenityDto = new AmenityDto()
        .id(1L)
        .amenityId("amenityId")
        .name("name")
        .description("description")
        .price(BigDecimal.valueOf(12));
    final AmenityDto amenityDtoWithCommunity = baseAmenityDto.communityId(communityId);
    final Amenity baseAmenity = new Amenity();
    final Amenity amenityWithCommunity = new Amenity().withCommunity(community);
    final List<Amenity> amenitiesWithCommunity = singletonList(amenityWithCommunity);
    final HashSet<AmenityDto> requestAmenitiesDto = new HashSet<>(singletonList(baseAmenityDto));
    given(communityService.getCommunityDetailsById(communityId))
        .willReturn(Optional.of(community));
    given(amenityApiMapper.amenityDtoToAmenity(baseAmenityDto))
        .willReturn(baseAmenity);
    given(amenityRepository.saveAll(amenitiesWithCommunity))
        .willReturn(amenitiesWithCommunity);
    given(amenityApiMapper.amenityToAmenityDto(amenityWithCommunity))
        .willReturn(amenityDtoWithCommunity);

    // when
    final Optional<List<AmenityDto>> actualResult =
        amenitySDJpaService.createAmenities(requestAmenitiesDto, communityId);

    // then
    assertTrue(actualResult.isPresent());
    final List<AmenityDto> actualResultAmenitiesDtos = actualResult.get();
    assertEquals(singletonList(amenityDtoWithCommunity), actualResultAmenitiesDtos);
    verify(communityService).getCommunityDetailsById(communityId);
    verify(amenityApiMapper).amenityDtoToAmenity(baseAmenityDto);
    verify(amenityRepository).saveAll(amenitiesWithCommunity);
    verify(amenityApiMapper).amenityToAmenityDto(amenityWithCommunity);
  }

  /**
   * Tests the creation of amenities for a non-existent community by verifying that no
   * result is returned when attempting to add amenities to a community that does not
   * exist.
   */
  @Test
  void shouldFailOnAddAmenityToNotExistingCommunity() {
    // given
    final String communityId = "communityId";
    final AmenityDto baseAmenityDto = new AmenityDto()
        .id(1L)
        .amenityId("amenityId")
        .name("name")
        .description("description")
        .price(BigDecimal.valueOf(12));
    final HashSet<AmenityDto> requestAmenitiesDto = new HashSet<>(singletonList(baseAmenityDto));
    given(communityService.getCommunityDetailsById(communityId))
        .willReturn(Optional.empty());

    // when
    final Optional<List<AmenityDto>> actualResult =
        amenitySDJpaService.createAmenities(requestAmenitiesDto, communityId);

    // then
    assertFalse(actualResult.isPresent());
    verify(communityService).getCommunityDetailsById(communityId);
    verifyNoInteractions(amenityApiMapper);
    verifyNoInteractions(amenityRepository);
  }

  /**
   * Tests the update functionality of the `amenitySDJpaService` by updating a community
   * amenity with a given ID, verifying the repository calls and the result of the
   * update operation.
   */
  @Test
  void shouldUpdateCommunityAmenitySuccessfully() {
    // given
    Amenity communityAmenity =
        TestUtils.AmenityHelpers.getTestAmenity(TEST_AMENITY_ID, TEST_AMENITY_DESCRIPTION);
    Community testCommunity = TestUtils.CommunityHelpers.getTestCommunity();
    AmenityDto updated = getTestAmenityDto();
    Amenity updatedAmenity = getUpdatedCommunityAmenity();

    given(amenityRepository.findByAmenityId(TEST_AMENITY_ID))
        .willReturn(Optional.of(communityAmenity));
    given(communityRepository.findByCommunityId(TEST_COMMUNITY_ID))
        .willReturn(Optional.of(testCommunity));
    given(amenityRepository.save(updatedAmenity))
        .willReturn(updatedAmenity);

    // when
    boolean result = amenitySDJpaService.updateAmenity(updated);

    // then
    assertTrue(result);
    verify(amenityRepository).findByAmenityId(TEST_AMENITY_ID);
    verify(communityRepository).findByCommunityId(TEST_COMMUNITY_ID);
    verify(amenityRepository).save(updatedAmenity);
  }

  /**
   * Tests the functionality of updating a community amenity when the amenity does not
   * exist. It checks that the update is unsuccessful and that the amenity repository
   * is not called to save the updated amenity.
   */
  @Test
  void shouldNotUpdateCommunityAmenitySuccessfullyIfAmenityNotExists() {
    // given
    given(amenityRepository.findByAmenityId(TEST_AMENITY_ID))
        .willReturn(Optional.empty());

    // when
    boolean result = amenitySDJpaService.updateAmenity(getTestAmenityDto());

    // then
    assertFalse(result);
    verify(amenityRepository, times(0)).save(getUpdatedCommunityAmenity());
    verifyNoInteractions(communityRepository);
  }

  /**
   * Tests the functionality of updating a community amenity when saving fails. It
   * checks if the update operation returns false when the amenity repository fails to
   * save the updated amenity.
   */
  @Test
  void shouldNotUpdateCommunityAmenitySuccessfullyIfSavingFails() {
    // given
    Amenity testAmenity =
        TestUtils.AmenityHelpers.getTestAmenity(TEST_AMENITY_ID, TEST_AMENITY_DESCRIPTION);
    Amenity updatedAmenity = getUpdatedCommunityAmenity();
    AmenityDto updatedDto = getTestAmenityDto();
    Community community = TestUtils.CommunityHelpers.getTestCommunity();

    given(amenityRepository.findByAmenityId(TEST_AMENITY_ID))
        .willReturn(Optional.of(testAmenity));
    given(communityRepository.findByCommunityId(TEST_COMMUNITY_ID))
        .willReturn(Optional.of(community));
    given(amenityRepository.save(updatedAmenity))
        .willReturn(null);

    // when
    boolean result = amenitySDJpaService.updateAmenity(updatedDto);

    // then
    assertFalse(result);
    verify(amenityRepository).findByAmenityId(TEST_AMENITY_ID);
    verify(communityRepository).findByCommunityId(TEST_COMMUNITY_ID);
    verify(amenityRepository).save(updatedAmenity);
  }

  /**
   * Tests the `updateAmenity` method's behavior when a community does not exist. It
   * verifies that the method returns `false` and only interacts with the `amenityRepository`.
   */
  @Test
  void shouldNotUpdateAmenityIfCommunityDoesNotExist() {
    // given
    Amenity communityAmenity =
        TestUtils.AmenityHelpers.getTestAmenity(TEST_AMENITY_ID, TEST_AMENITY_DESCRIPTION);
    AmenityDto updatedDto = getTestAmenityDto();

    given(amenityRepository.findByAmenityId(TEST_AMENITY_ID))
        .willReturn(Optional.of(communityAmenity));
    given(communityRepository.findByCommunityId(TEST_COMMUNITY_ID))
        .willReturn(Optional.empty());

    // when
    boolean result = amenitySDJpaService.updateAmenity(updatedDto);

    // then
    assertFalse(result);
    verify(amenityRepository).findByAmenityId(TEST_AMENITY_ID);
    verify(communityRepository).findByCommunityId(TEST_COMMUNITY_ID);
    verifyNoMoreInteractions(amenityRepository);
  }

  /**
   * Creates and returns an instance of `AmenityDto` populated with predefined values.
   * The instance is initialized with a specified ID and various attributes, including
   * name, description, price, and community ID.
   *
   * @returns an `AmenityDto` object with specified id, amenityId, name, description,
   * price, and communityId.
   *
   * The output is an instance of `AmenityDto` with properties id, amenityId, name,
   * description, price, and communityId.
   */
  private AmenityDto getTestAmenityDto() {
    Long TEST_AMENITY_ENTITY_ID = 1L;

    return new AmenityDto()
        .id(TEST_AMENITY_ENTITY_ID)
        .amenityId(TEST_AMENITY_ID)
        .name(TEST_AMENITY_NAME)
        .description(TEST_AMENITY_DESCRIPTION)
        .price(TEST_AMENITY_PRICE)
        .communityId(TEST_COMMUNITY_ID);
  }

  /**
   * Constructs an `Amenity` object from a `communityAmenityDto` object, copying its
   * details and associating it with a test community. It returns this newly constructed
   * `Amenity` object. The function relies on other helper functions to obtain a community
   * and an amenity DTO.
   *
   * @returns an `Amenity` object with updated properties from `communityAmenityDto`.
   */
  private Amenity getUpdatedCommunityAmenity() {
    AmenityDto communityAmenityDto = getTestAmenityDto();
    return new Amenity()
        .withAmenityId(communityAmenityDto.getAmenityId())
        .withName(communityAmenityDto.getName())
        .withPrice(communityAmenityDto.getPrice())
        .withDescription(communityAmenityDto.getDescription())
        .withCommunity(TestUtils.CommunityHelpers.getTestCommunity());
  }
}