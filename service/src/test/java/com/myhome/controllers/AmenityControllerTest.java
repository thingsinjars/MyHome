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

import com.myhome.controllers.mapper.AmenityApiMapper;
import com.myhome.domain.Amenity;
import com.myhome.model.AddAmenityRequest;
import com.myhome.model.AddAmenityResponse;
import com.myhome.model.AmenityDto;
import com.myhome.model.GetAmenityDetailsResponse;
import com.myhome.model.UpdateAmenityRequest;
import com.myhome.services.AmenityService;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Is a JUnit test suite for testing the functionality of an Amenity Controller in a
 * web application. The tests cover scenarios such as adding, retrieving, updating,
 * and deleting amenities, including handling cases where community IDs do not exist.
 */
class AmenityControllerTest {

  private static final String TEST_AMENITY_NAME = "test-amenity-name";
  private static final BigDecimal TEST_AMENITY_PRICE = BigDecimal.valueOf(1);
  private final String TEST_AMENITY_ID = "test-amenity-id";
  private final String TEST_AMENITY_DESCRIPTION = "test-amenity-description";
  private final String TEST_COMMUNITY_ID = "1";

  @Mock
  private AmenityService amenitySDJpaService;
  @Mock
  private AmenityApiMapper amenityApiMapper;

  @InjectMocks
  private AmenityController amenityController;

  /**
   * Adds an amenity to a community. It creates an `AddAmenityRequest` object with
   * amenities and sends it to the `amenityController` to add the amenity to the
   * community. The response is expected to be HTTP status OK (200).
   */
  @Test
  void shouldAddAmenityToCommunity() {
    // given
    final String communityId = "communityId";
    final AmenityDto amenityDto =
        new AmenityDto().id(1L)
            .amenityId("amenityId")
            .name("name")
            .description("description")
            .price(BigDecimal.ONE)
            .communityId("");
    final HashSet<AmenityDto> amenities = new HashSet<>(singletonList(amenityDto));
    final AddAmenityRequest request = new AddAmenityRequest().amenities(amenities);
    given(amenitySDJpaService.createAmenities(amenities, communityId))
        .willReturn(Optional.of(singletonList(amenityDto)));

    // when
    final ResponseEntity<AddAmenityResponse> response =
        amenityController.addAmenityToCommunity(communityId, request);

    // then
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  /**
   * Tests adding an amenity to a community that does not exist. It creates an amenities
   * set and a request object, then mocks the service layer's response as empty. The
   * controller's addAmenityToCommunity method is called with the request and a
   * non-existent community ID.
   */
  @Test
  void shouldNotAddAmenityWhenCommunityNotExists() {
    // given
    final String communityId = "communityId";
    final AmenityDto amenityDto = new AmenityDto();
    final HashSet<AmenityDto> amenities = new HashSet<>(singletonList(amenityDto));
    final AddAmenityRequest request = new AddAmenityRequest().amenities(amenities);
    given(amenitySDJpaService.createAmenities(amenities, communityId))
        .willReturn(Optional.empty());

    // when
    final ResponseEntity<AddAmenityResponse> response =
        amenityController.addAmenityToCommunity(communityId, request);

    // then
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  /**
   * Initializes Mockito annotations for the current object, ensuring that any annotated
   * mocks are set up properly before each test is run. This allows for efficient and
   * reliable testing with Mockito. Mocks are initialized to have specific behaviors
   * or returns for each test.
   */
  @BeforeEach
  private void init() {
    MockitoAnnotations.initMocks(this);
  }

  /**
   * Retrieves amenity details by ID, maps them to a response object, and returns it
   * as an HTTP response with a status code of OK. It uses a service layer for data
   * access and an API mapper for response conversion.
   */
  @Test
  void getAmenityDetails() {
    // given
    Amenity testAmenity = getTestAmenity();
    GetAmenityDetailsResponse expectedResponseBody = new GetAmenityDetailsResponse()
        .amenityId(testAmenity.getAmenityId())
        .description(testAmenity.getDescription());

    given(amenitySDJpaService.getAmenityDetails(TEST_AMENITY_ID))
        .willReturn(Optional.of(testAmenity));
    given(amenityApiMapper.amenityToAmenityDetailsResponse(testAmenity))
        .willReturn(expectedResponseBody);

    // when
    ResponseEntity<GetAmenityDetailsResponse> response =
        amenityController.getAmenityDetails(TEST_AMENITY_ID);

    // then
    assertEquals(expectedResponseBody, response.getBody());
    assertEquals(HttpStatus.OK, response.getStatusCode());
    verify(amenitySDJpaService).getAmenityDetails(TEST_AMENITY_ID);
    verify(amenityApiMapper).amenityToAmenityDetailsResponse(testAmenity);
  }

  /**
   * Mocks a non-existent amenity ID to test the controller's response when the requested
   * amenity does not exist. It verifies that the response body is null and the status
   * code is NOT_FOUND, while also checking that the service and mapper were not used.
   */
  @Test
  void getAmenityDetailsNotExists() {
    // given
    given(amenitySDJpaService.getAmenityDetails(TEST_AMENITY_ID))
        .willReturn(Optional.empty());

    // when
    ResponseEntity<GetAmenityDetailsResponse> response =
        amenityController.getAmenityDetails(TEST_AMENITY_ID);

    // then
    assertNull(response.getBody());
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    verify(amenitySDJpaService).getAmenityDetails(TEST_AMENITY_ID);
    verify(amenityApiMapper, never()).amenityToAmenityDetailsResponse(any());
  }

  /**
   * Deletes an amenity identified by the specified ID and verifies that it is successfully
   * deleted. It tests whether the response body is null and the status code is NO
   * CONTENT, indicating a successful deletion.
   */
  @Test
  void deleteAmenity() {
    // given
    given(amenitySDJpaService.deleteAmenity(TEST_AMENITY_ID))
        .willReturn(true);

    // when
    ResponseEntity response = amenityController.deleteAmenity(TEST_AMENITY_ID);

    // then
    assertNull(response.getBody());
    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    verify(amenitySDJpaService).deleteAmenity(TEST_AMENITY_ID);
  }

  /**
   * Tests the deletion of an amenity that does not exist. It asserts that a null body
   * and a NOT_FOUND status are returned, indicating the absence of the amenity. The
   * service is also verified to have made the attempt to delete the non-existent amenity.
   */
  @Test
  void deleteAmenityNotExists() {
    // given
    given(amenitySDJpaService.deleteAmenity(TEST_AMENITY_ID))
        .willReturn(false);

    // when
    ResponseEntity response = amenityController.deleteAmenity(TEST_AMENITY_ID);

    // then
    assertNull(response.getBody());
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    verify(amenitySDJpaService).deleteAmenity(TEST_AMENITY_ID);
  }

  /**
   * Updates an amenity successfully and verifies the response status code as NO_CONTENT.
   * It mocks the updateAmenityRequestToAmenityDto and updateAmenity services to test
   * their functionality.
   */
  @Test
  void shouldUpdateAmenitySuccessfully() {
    // given
    AmenityDto amenityDto = getTestAmenityDto();
    UpdateAmenityRequest request = getUpdateAmenityRequest();

    given(amenityApiMapper.updateAmenityRequestToAmenityDto(request))
        .willReturn(amenityDto);
    given(amenitySDJpaService.updateAmenity(amenityDto))
        .willReturn(true);

    // when
    ResponseEntity<Void> responseEntity =
        amenityController.updateAmenity(TEST_AMENITY_ID, request);

    // then
    assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
    verify(amenityApiMapper).updateAmenityRequestToAmenityDto(request);
    verify(amenitySDJpaService).updateAmenity(amenityDto);
  }

  /**
   * Tests whether an amenity is updated successfully when provided with a valid update
   * request and ID, but the amenity does not exist. It asserts that the response status
   * code should be NOT_FOUND if the amenity is not found.
   */
  @Test
  void shouldNotUpdateCommunityAmenityIfAmenityNotExists() {
    // given
    AmenityDto amenityDto = getTestAmenityDto();
    UpdateAmenityRequest request = getUpdateAmenityRequest();

    given(amenityApiMapper.updateAmenityRequestToAmenityDto(request))
        .willReturn(amenityDto);
    given(amenitySDJpaService.updateAmenity(amenityDto))
        .willReturn(false);

    // when
    ResponseEntity<Void> responseEntity =
        amenityController.updateAmenity(TEST_AMENITY_ID, request);

    // then
    assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    verify(amenityApiMapper).updateAmenityRequestToAmenityDto(request);
    verify(amenitySDJpaService).updateAmenity(amenityDto);
  }

  /**
   * Creates and returns a new instance of an `Amenity` object with specific properties:
   * `amenityId` set to `TEST_AMENITY_ID` and `description` set to `TEST_AMENITY_DESCRIPTION`.
   * This function is used to provide a test amenity.
   *
   * @returns an instance of class `Amenity`.
   */
  private Amenity getTestAmenity() {
    return new Amenity()
        .withAmenityId(TEST_AMENITY_ID)
        .withDescription(TEST_AMENITY_DESCRIPTION);
  }

  /**
   * Creates an instance of `AmenityDto`. It sets various properties on the object,
   * including its ID, amenity ID, name, description, price, and community ID, all with
   * test data values. The constructed object is then returned as a result.
   *
   * @returns an instance of `AmenityDto`.
   */
  private AmenityDto getTestAmenityDto() {
    return new AmenityDto()
        .id(1L)
        .amenityId(TEST_AMENITY_ID)
        .name(TEST_AMENITY_NAME)
        .description(TEST_AMENITY_DESCRIPTION)
        .price(TEST_AMENITY_PRICE)
        .communityId(TEST_COMMUNITY_ID);
  }

  /**
   * Constructs an `UpdateAmenityRequest` object with predefined values for name,
   * description, price, and community ID. The object is initialized with test data,
   * represented by constants TEST_AMENITY_NAME, TEST_AMENITY_DESCRIPTION, and TEST_COMMUNITY_ID.
   *
   * @returns an instance of `UpdateAmenityRequest`.
   */
  private UpdateAmenityRequest getUpdateAmenityRequest() {
    return new UpdateAmenityRequest()
        .name(TEST_AMENITY_NAME)
        .description(TEST_AMENITY_DESCRIPTION)
        .price(1L)
        .communityId(TEST_COMMUNITY_ID);
  }
}