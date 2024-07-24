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
 * Implements the UsersApi interface and provides CRUD operations for users. It handles
 * user registration, listing all users, retrieving user details by ID, resetting
 * passwords, confirming email addresses, and resending confirmation emails.
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
   * Processes a user registration request, maps it to a UserDto, creates a new user
   * using the userService, and returns a response indicating the result of the operation.
   * A successful creation returns a CreateUserResponse with a 201 status code; an error
   * results in a 409 conflict status code.
   * 
   * @param request validated user creation request object, which is expected to be of
   * type `CreateUserRequest`.
   * 
   * Contains a valid instance of type `CreateUserRequest`.
   * 
   * @returns a ResponseEntity with a CreateUserResponse and HTTP status code.
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
   * Retrieves a list of users based on the provided page parameters and maps the results
   * to a response object. It handles the request, retrieves user details, converts
   * them into a REST API response, and returns the response with a status code indicating
   * success.
   * 
   * @param pageable pagination details, such as the page number and size, which are
   * used to retrieve a subset of users from the database.
   * 
   * Sort - specifies the sort criteria for pagination; Offset - denotes the starting
   * index for pagination; and Limit - defines the maximum number of records to be retrieved.
   * 
   * @returns a ResponseEntity containing a GetUserDetailsResponse object.
   * 
   * The response is a ResponseEntity object with a status code of 200 (OK) and a body
   * containing a GetUserDetailsResponse object. This GetUserDetailsResponse object has
   * a set of GetUserDetailsResponseUser objects representing the list of users in JSON
   * format.
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
   * Retrieves user details based on a provided ID, logs a trace message and returns a
   * response entity containing the details. If no matching user is found, it returns
   * a 404 status code.
   * 
   * @param userId identifier of the user for which details are requested, serving as
   * an argument to retrieve and return relevant information.
   * 
   * @returns a response entity containing user details.
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
   * Handles password-related actions for users, accepting a request body containing a
   * forgotten or reset password action. It uses a `UserService` to validate and process
   * the request, returning a successful response if the action is completed, and an
   * error response otherwise.
   * 
   * @param action type of password action, either FORGOT or RESET, and is used to
   * determine the subsequent operation on the user's password.
   * 
   * @param forgotPasswordRequest request for password reset or forgotten password,
   * which is validated and processed based on the specified action type.
   * 
   * - The `forgotPasswordRequest` contains an email address and a password reset token.
   * 
   * @returns a ResponseEntity with a status code of OK or BAD_REQUEST.
   * 
   * Returns a ResponseEntity object with Void type content, which represents an empty
   * response body. The response status is either "OK" (200) or "Bad Request" (400),
   * depending on whether the password action is successful or not.
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
   * Retrieves a list of all members from all houses associated with a specified user
   * ID, paginates the result using `Pageable`, and returns it as a response entity.
   * 
   * @param userId identifier of the user for whom the list of all members from their
   * houses is requested.
   * 
   * @param pageable criteria for pagination, allowing the function to retrieve and
   * return a subset of data according to specified page number and size.
   * 
   * Sort - The direction and property to sort the results; Size - The number of items
   * to return in a single response; Number - The page number for which data is required.
   * 
   * @returns a ResponseEntity containing a List of House Members.
   * 
   * Returns a ResponseEntity containing a ListHouseMembersResponse with members property.
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
   * Verifies whether an email confirmation token is valid for a given user ID. If the
   * token is valid, it returns a successful response; otherwise, it returns a bad
   * request response.
   * 
   * @param userId unique identifier of the user, used to verify their email confirmation
   * with the provided token.
   * 
   * @param emailConfirmToken verification token for the email confirmation process,
   * used by the `userService.confirmEmail()` method to verify whether the provided
   * token is valid.
   * 
   * @returns either an OK response or a bad request response.
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
   * Sends a confirmation email to a user with a specified ID. It calls the `userService`
   * to resend the email and returns a response based on the result: OK if the resend
   * is successful, or BAD REQUEST if it fails.
   * 
   * @param userId identifier of the user for whom an email confirmation needs to be resent.
   * 
   * @returns a ResponseEntity with either OK or BAD_REQUEST status.
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
