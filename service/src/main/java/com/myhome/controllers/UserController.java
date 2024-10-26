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

import com.myhome.api.UsersApi;
import com.myhome.controllers.dto.UserDto;
import com.myhome.controllers.dto.mapper.HouseMemberMapper;
import com.myhome.controllers.mapper.UserApiMapper;
import com.myhome.domain.PasswordActionType;
import com.myhome.domain.User;
import com.myhome.model.CreateUserRequest;
import com.myhome.model.CreateUserResponse;
import com.myhome.model.ForgotPasswordRequest;
import com.myhome.model.GetUserDetailsResponse;
import com.myhome.model.GetUserDetailsResponseUser;
import com.myhome.model.ListHouseMembersResponse;
import com.myhome.services.HouseService;
import com.myhome.services.UserService;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;

/**
 * Controller for facilitating user actions.
 */
@RestController
@Slf4j
@RequiredArgsConstructor
public class UserController implements UsersApi {

  private final UserService userService;
  private final UserApiMapper userApiMapper;
  private final HouseService houseService;
  private final HouseMemberMapper houseMemberMapper;

  /**
   * Handles user sign-up requests by converting the request to a UserDto, creating a
   * new user, and returning a CreateUserResponse with a created status if successful,
   * or a conflict status if the user already exists.
   *
   * @param request data sent in the request body to create a new user, validated by
   * the `@Valid` annotation.
   *
   * Contain fields for user data such as email, password, and other relevant details.
   *
   * @returns either a `ResponseEntity` with a created user response and a 201 status
   * code, or a 409 conflict response.
   *
   * The returned `ResponseEntity` contains a `CreateUserResponse` object in its body,
   * and a status code of either `HttpStatus.CREATED` (201) or `HttpStatus.CONFLICT` (409).
   */
  @Override
  public ResponseEntity<CreateUserResponse> signUp(@Valid CreateUserRequest request) {
    log.trace("Received SignUp request");
    UserDto requestUserDto = userApiMapper.createUserRequestToUserDto(request);
    Optional<UserDto> createdUserDto = userService.createUser(requestUserDto);
    return createdUserDto
        .map(userDto -> {
          CreateUserResponse response = userApiMapper.userDtoToCreateUserResponse(userDto);
          return ResponseEntity.status(HttpStatus.CREATED).body(response);
        })
        .orElseGet(() -> ResponseEntity.status(HttpStatus.CONFLICT).build());
  }

  /**
   * Handles HTTP requests to retrieve a list of users, mapping user details to a REST
   * API response. It takes a `Pageable` object as input, retrieves the list of users,
   * converts it to a response format, and returns the result with a 200 OK status.
   *
   * @param pageable pagination criteria for retrieving a subset of users from the
   * database, allowing for efficient data retrieval and handling of large user datasets.
   *
   * Deconstruct `pageable` into its properties.
   *
   * `pageable` has properties:
   * - `pageNumber`: the current page number.
   * - `pageSize`: the number of items per page.
   * - `sort`: an instance of `Sort` specifying how to sort the data.
   * - `offset`: the offset of the current page.
   * - `paged`: a boolean indicating whether the result is paged.
   * - `unpaged`: a boolean indicating whether the result is unpaged.
   * - `pageNumberOrOneBasedPage`: the current page number or 1-based page number.
   * - `pageSizeOrOneBasedPageSize`: the number of items per page or 1-based page size.
   *
   * @returns a HTTP response with a list of users in JSON format.
   *
   * Contain a set of User objects and a set of GetUserDetailsResponseUser objects.
   */
  @Override
  public ResponseEntity<GetUserDetailsResponse> listAllUsers(Pageable pageable) {
    log.trace("Received request to list all users");

    Set<User> userDetails = userService.listAll(pageable);
    Set<GetUserDetailsResponseUser> userDetailsResponse =
        userApiMapper.userSetToRestApiResponseUserSet(userDetails);

    GetUserDetailsResponse response = new GetUserDetailsResponse();
    response.setUsers(userDetailsResponse);

    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  /**
   * Retrieves user details for a given user ID, maps the response to a
   * `GetUserDetailsResponseUser` object, and returns it in a `ResponseEntity` with a
   * 200 status if found, or a 404 status if not.
   *
   * @param userId identifier of the user for whom details are to be retrieved.
   *
   * @returns either a `ResponseEntity` containing a `GetUserDetailsResponseUser` object
   * with HTTP status OK (200), or a `ResponseEntity` with HTTP status NOT FOUND (404).
   */
  @Override
  public ResponseEntity<GetUserDetailsResponseUser> getUserDetails(String userId) {
    log.trace("Received request to get details of user with Id[{}]", userId);

    return userService.getUserDetails(userId)
        .map(userApiMapper::userDtoToGetUserDetailsResponse)
        .map(response -> ResponseEntity.status(HttpStatus.OK).body(response))
        .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
  }

  /**
   * Handles password-related requests, processing either a password reset or a forgotten
   * password request based on the provided action. It calls the `userService` to perform
   * the requested action and returns a successful response if successful, otherwise a
   * bad request response.
   *
   * @param action type of password action requested, either to reset a password or to
   * request a reset.
   *
   * @param forgotPasswordRequest request data for either password reset or forgot
   * password action, depending on the `action` parameter.
   *
   * Parse the `forgotPasswordRequest` object into its constituent properties, it has
   * email and password properties.
   *
   * @returns a ResponseEntity with either a 200 OK status or a 400 Bad Request status.
   *
   * The returned output is a `ResponseEntity` object, which contains a body of type
   * `Void`. The status of the response is either OK (200) or BAD_REQUEST (400).
   */
  @Override
  public ResponseEntity<Void> usersPasswordPost(@NotNull @Valid String action, @Valid @RequestBody ForgotPasswordRequest forgotPasswordRequest) {
    boolean result = false;
    PasswordActionType parsedAction = PasswordActionType.valueOf(action);
    if (parsedAction == PasswordActionType.FORGOT) {
      result = true;
      userService.requestResetPassword(forgotPasswordRequest);
    } else if (parsedAction == PasswordActionType.RESET) {
      result = userService.resetPassword(forgotPasswordRequest);
    }
    if (result) {
      return ResponseEntity.ok().build();
    } else {
      return ResponseEntity.badRequest().build();
    }
  }

  /**
   * Retrieves a list of house members for a user with a specified ID, converts the
   * result to a REST API response, and returns it as a ResponseEntity.
   *
   * @param userId identifier of the user for whom all house members are to be listed.
   *
   * @param pageable pagination criteria for the list of house members, allowing for
   * efficient retrieval and processing of large datasets.
   *
   * Destructure:
   * - `pageable` is of type `Pageable` which can be destructured into `sort`, `pageNumber`,
   * `pageSize`, `offset`, and `pageNumber`.
   * - `sort` is a `Sort` object that represents the sorting criteria.
   * - `pageNumber` and `pageSize` represent the pagination parameters.
   * - `offset` is the number of elements to skip before starting to collect the result.
   *
   * @returns a ResponseEntity containing a ListHouseMembersResponse with a set of house
   * members.
   *
   * The output is a `ResponseEntity` containing a `ListHouseMembersResponse` object.
   * The `ListHouseMembersResponse` object has a single attribute: `members`.
   */
  @Override
  public ResponseEntity<ListHouseMembersResponse> listAllHousemates(String userId, Pageable pageable) {
    log.trace("Received request to list all members of all houses of user with Id[{}]", userId);

    return houseService.listHouseMembersForHousesOfUserId(userId, pageable)
            .map(HashSet::new)
            .map(houseMemberMapper::houseMemberSetToRestApiResponseHouseMemberSet)
            .map(houseMembers -> new ListHouseMembersResponse().members(houseMembers))
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
  }

  /**
   * Verifies an email confirmation by calling the `confirmEmail` method of the
   * `userService`, returning a successful response (`ResponseEntity.ok()`) if confirmed,
   * or a bad request response (`ResponseEntity.badRequest()`) if not.
   *
   * @param userId identifier for the user whose email is being confirmed.
   *
   * @param emailConfirmToken token used to confirm the user's email address.
   *
   * @returns either a successful HTTP response (200 OK) or a bad request response (400).
   */
  @Override
  public ResponseEntity<Void> confirmEmail(String userId, String emailConfirmToken) {
    boolean emailConfirmed = userService.confirmEmail(userId, emailConfirmToken);
    if(emailConfirmed) {
      return ResponseEntity.ok().build();
    } else {
      return ResponseEntity.badRequest().build();
    }
  }

  /**
   * Resends a confirmation email to a user with the specified ID and returns a successful
   * response if the operation is successful, otherwise returns a bad request response.
   *
   * @param userId unique identifier of the user whose email confirmation is being resent.
   *
   * @returns either a 200 OK response or a 400 Bad Request response.
   */
  @Override
  public ResponseEntity<Void> resendConfirmEmailMail(String userId) {
    boolean emailConfirmResend = userService.resendEmailConfirm(userId);
    if(emailConfirmResend) {
      return ResponseEntity.ok().build();
    } else {
      return ResponseEntity.badRequest().build();
    }
  }
}
