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

import com.myhome.controllers.dto.UserDto;
import com.myhome.controllers.dto.mapper.HouseMemberMapper;
import com.myhome.controllers.mapper.UserApiMapper;
import com.myhome.domain.PasswordActionType;
import com.myhome.domain.HouseMember;
import com.myhome.domain.User;
import com.myhome.model.CreateUserRequest;
import com.myhome.model.CreateUserResponse;
import com.myhome.model.ForgotPasswordRequest;
import com.myhome.model.GetUserDetailsResponse;
import com.myhome.model.GetUserDetailsResponseUser;
import com.myhome.model.ListHouseMembersResponse;
import com.myhome.services.HouseService;
import com.myhome.services.UserService;
import java.util.Collections;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

class UserControllerTest {

  private static final String TEST_ID = "1";
  private static final String TEST_NAME = "name";
  private static final String TEST_EMAIL = "email@mail.com";
  private static final String TEST_PASSWORD = "password";
  private static final String TEST_NEW_PASSWORD = "new-password";
  private static final String TEST_TOKEN = "test-token";


  @Mock
  private UserService userService;

  @Mock
  private UserApiMapper userApiMapper;

  @Mock
  private HouseService houseService;

  @Mock
  private HouseMemberMapper houseMemberMapper;

  @InjectMocks
  private UserController userController;

  /**
   * initializes mock objects using MockitoAnnotations, ensuring that mock dependencies
   * are available for further testing.
   */
  @BeforeEach
  private void init() {
    MockitoAnnotations.initMocks(this);
  }

  /**
   * tests the sign-up functionality of the `UserController`. Given a valid user creation
   * request, it verifies that the user is created successfully and returns a `CreateUserResponse`.
   */
  @Test
  void shouldSignUpSuccessful() {
    // given
    CreateUserRequest request = new CreateUserRequest()
        .name(TEST_NAME)
        .email(TEST_EMAIL)
        .password(TEST_PASSWORD);
    UserDto userDto = UserDto.builder()
        .name(TEST_NAME)
        .email(TEST_EMAIL)
        .password(TEST_PASSWORD)
        .build();
    CreateUserResponse createUserResponse = new CreateUserResponse()
        .userId(TEST_ID)
        .name(TEST_NAME)
        .email(TEST_EMAIL);

    given(userApiMapper.createUserRequestToUserDto(request))
        .willReturn(userDto);
    given(userService.createUser(userDto))
        .willReturn(Optional.of(userDto));
    given(userApiMapper.userDtoToCreateUserResponse(userDto))
        .willReturn(createUserResponse);

    // when
    ResponseEntity<CreateUserResponse> responseEntity = userController.signUp(request);

    // then
    assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
    assertEquals(createUserResponse, responseEntity.getBody());
    verify(userApiMapper).createUserRequestToUserDto(request);
    verify(userService).createUser(userDto);
    verify(userApiMapper).userDtoToCreateUserResponse(userDto);
  }

  /**
   * tests the `listAllUsers()` method of a user controller by providing a page request
   * and verifying that the resulting response contains the expected users and status
   * code.
   */
  @Test
  void shouldListUsersSuccess() {
    // given
    int limit = 150;
    int start = 50;
    PageRequest pageRequest = PageRequest.of(start, limit);

    Set<User> users = new HashSet<>();
    users.add(new User(TEST_NAME, TEST_ID, TEST_EMAIL, false, TEST_PASSWORD, new HashSet<>(), new HashSet<>()));

    Set<GetUserDetailsResponseUser> responseUsers = new HashSet<>();
    responseUsers.add(
        new GetUserDetailsResponseUser()
            .userId(TEST_ID)
            .name(TEST_NAME)
            .email(TEST_EMAIL)
            .communityIds(Collections.emptySet())
    );
    GetUserDetailsResponse expectedResponse = new GetUserDetailsResponse();
    expectedResponse.setUsers(responseUsers);

    given(userService.listAll(pageRequest))
        .willReturn(users);
    given(userApiMapper.userSetToRestApiResponseUserSet(users))
        .willReturn(responseUsers);

    // when
    ResponseEntity<GetUserDetailsResponse> responseEntity =
        userController.listAllUsers(pageRequest);

    // then
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(expectedResponse, responseEntity.getBody());
    verify(userService).listAll(pageRequest);
    verify(userApiMapper).userSetToRestApiResponseUserSet(users);
  }

  /**
   * tests the `getUserDetails` endpoint, given an empty user ID, and verifies the
   * response status code and body.
   */
  @Test
  void shouldGetUserDetailsSuccessWithNoResults() {
    // given
    String userId = TEST_ID;
    given(userService.getUserDetails(userId))
        .willReturn(Optional.empty());

    // when
    ResponseEntity<GetUserDetailsResponseUser> response = userController.getUserDetails(userId);

    // then
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertNull(response.getBody());
    verify(userService).getUserDetails(userId);
    verifyNoInteractions(userApiMapper);
  }

  /**
   * tests the `getUserDetails` endpoint of the `UserController`. It provides a user
   * ID and expected response data, and verifies that the response status code is
   * `HttpStatus.OK` and the response body matches the expected data.
   */
  @Test
  void shouldGetUserDetailsSuccessWithResults() {
    // given
    String userId = TEST_ID;
    UserDto userDto = UserDto.builder()
        .userId(userId)
        .build();
    GetUserDetailsResponseUser expectedResponse = new GetUserDetailsResponseUser()
        .userId(TEST_ID)
        .name(TEST_NAME)
        .email(TEST_EMAIL)
        .communityIds(Collections.emptySet());

    given(userService.getUserDetails(userId))
        .willReturn(Optional.of(userDto));
    given(userApiMapper.userDtoToGetUserDetailsResponse(userDto))
        .willReturn(expectedResponse);

    // when
    ResponseEntity<GetUserDetailsResponseUser> response = userController.getUserDetails(userId);

    // then
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(expectedResponse, response.getBody());
    verify(userService).getUserDetails(userId);
    verify(userApiMapper).userDtoToGetUserDetailsResponse(userDto);
  }

  /**
   * verifies that a successful request to reset a user's password results in a
   * `HttpStatus.OK` response and the password being requested for reset by the user service.
   */
  @Test
  void userForgotPasswordRequestResetSuccess() {
    // given
    ForgotPasswordRequest forgotPasswordRequest = getForgotPasswordRequest();

    // when
    ResponseEntity<Void> response = userController.usersPasswordPost(PasswordActionType.FORGOT.toString(), forgotPasswordRequest);

    // then
    assertEquals(HttpStatus.OK, response.getStatusCode());
    verify(userService).requestResetPassword(forgotPasswordRequest);
    verify(userService, never()).resetPassword(forgotPasswordRequest);
  }

  /**
   * tests the user controller's password reset functionality by providing a valid
   * forgotten password request and verifying that it fails with an OK status code and
   * the expected actions are performed on the user service.
   */
  @Test
  void userForgotPasswordRequestResetFailure() {
    // given
    ForgotPasswordRequest forgotPasswordRequest = getForgotPasswordRequest();

    // when
    ResponseEntity<Void> response = userController.usersPasswordPost(PasswordActionType.FORGOT.toString(), forgotPasswordRequest);

    // then
    assertEquals(HttpStatus.OK, response.getStatusCode());
    verify(userService).requestResetPassword(forgotPasswordRequest);
    verify(userService, never()).resetPassword(forgotPasswordRequest);
  }

  /**
   * tests the password reset process for a user who has forgotten their password. It
   * verifies that the password is successfully reset and that no unnecessary requests
   * to reset the password are made to the user service.
   */
  @Test
  void userForgotPasswordResetSuccess() {
    // given
    ForgotPasswordRequest forgotPasswordRequest = getForgotPasswordRequest();
    given(userService.resetPassword(forgotPasswordRequest))
        .willReturn(true);
    // when
    ResponseEntity<Void> response = userController.usersPasswordPost(PasswordActionType.RESET.toString(), forgotPasswordRequest);

    // then
    assertEquals(HttpStatus.OK, response.getStatusCode());
    verify(userService, never()).requestResetPassword(forgotPasswordRequest);
    verify(userService).resetPassword(forgotPasswordRequest);
  }

  /**
   * tests the failure case of resetting a user's password through the `/users/password/reset`
   * endpoint. It verifies that the response status code is `HttpStatus.BAD_REQUEST`
   * and that the `userService.requestResetPassword` and `userService.resetPassword`
   * methods are never called.
   */
  @Test
  void userForgotPasswordResetFailure() {
    // given
    ForgotPasswordRequest forgotPasswordRequest = getForgotPasswordRequest();
    given(userService.resetPassword(forgotPasswordRequest))
        .willReturn(false);
    // when
    ResponseEntity<Void> response = userController.usersPasswordPost(PasswordActionType.RESET.toString(), forgotPasswordRequest);

    // then
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    verify(userService, never()).requestResetPassword(forgotPasswordRequest);
    verify(userService).resetPassword(forgotPasswordRequest);
  }

  /**
   * creates a new `ForgotPasswordRequest` object with predefined email, new password
   * and token values for testing purposes.
   * 
   * @returns a `ForgotPasswordRequest` object containing the email, new password, and
   * token for password reset.
   */
  private ForgotPasswordRequest getForgotPasswordRequest() {
    ForgotPasswordRequest request = new ForgotPasswordRequest();
    request.setEmail(TEST_EMAIL);
    request.setNewPassword(TEST_NEW_PASSWORD);
    request.setToken(TEST_TOKEN);
    return request;
  }

  /**
   * tests the `listAllHousemates` method of the `UserController` class. It provides a
   * page request to list all house members for a given user ID and verifies that the
   * response is NOT_FOUND status code and an empty list of house members in the body
   * of the response.
   */
  void shouldListAllHousematesSuccessWithNoResults() {
    // given
    String userId = TEST_ID;
    int start = 50;
    int limit = 150;
    PageRequest pageRequest = PageRequest.of(start, limit);

    given(houseService.listHouseMembersForHousesOfUserId(userId, pageRequest))
        .willReturn(Optional.empty());

    // when
    ResponseEntity<ListHouseMembersResponse> response =
        userController.listAllHousemates(userId, pageRequest);

    // then
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertNull(response.getBody());
    then(houseService).should().listHouseMembersForHousesOfUserId(userId, pageRequest);
    then(houseMemberMapper).shouldHaveNoInteractions();
    then(userService).shouldHaveNoInteractions();
    then(userApiMapper).shouldHaveNoInteractions();
  }

  /**
   * tests the `listAllHousemates` method of the `UserController` class, which calls
   * the `listHouseMembersForHousesOfUserId` method of the `HouseService`. The function
   * verifies that the response from the `listHouseMembersForHousesOfUserId` method is
   * converted to a `ListHouseMembersResponse` object using the `houseMemberMapper`,
   * and that the response status code is `OK`.
   */
  @Test
  void shouldListAllHousematesSuccessWithResults() {
    // given
    String userId = TEST_ID;
    int start = 50;
    int limit = 150;
    PageRequest pageRequest = PageRequest.of(start, limit);

    List<HouseMember> houseMemberList = Collections.singletonList(
        new HouseMember(TEST_ID, null, TEST_NAME, null)
    );

    Set<com.myhome.model.HouseMember> responseSet = Collections.singleton(
        new com.myhome.model.HouseMember()
            .memberId(TEST_ID)
            .name(TEST_NAME)
    );

    ListHouseMembersResponse expectedResponse = new ListHouseMembersResponse();
    expectedResponse.setMembers(responseSet);

    given(houseService.listHouseMembersForHousesOfUserId(userId, pageRequest))
        .willReturn(Optional.of(houseMemberList));
    given(houseMemberMapper.houseMemberSetToRestApiResponseHouseMemberSet(
        new HashSet<>(houseMemberList)))
        .willReturn(responseSet);

    // when
    ResponseEntity<ListHouseMembersResponse> response =
        userController.listAllHousemates(userId, pageRequest);

    // then
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(expectedResponse, response.getBody());
    then(houseService).should().listHouseMembersForHousesOfUserId(userId, pageRequest);
    then(houseMemberMapper).should()
        .houseMemberSetToRestApiResponseHouseMemberSet(new HashSet<>(houseMemberList));
    then(userService).shouldHaveNoInteractions();
    then(userApiMapper).shouldHaveNoInteractions();
  }
}























