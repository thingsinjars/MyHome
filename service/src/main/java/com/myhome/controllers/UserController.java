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
   * receives a `CreateUserRequest`, creates a `UserDto` using the provided data, and
   * then creates a new `User` object using the `UserDto`. If successful, it returns a
   * `ResponseEntity` with the created user's details in the response body.
   * 
   * @param request `CreateUserRequest` object passed from the client to the server for
   * creating a new user account.
   * 
   * @returns a `ResponseEntity` with a status code of either `CREATED` or `CONFLICT`,
   * depending on whether the user was created successfully or not.
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
   * receives a pageable request from the client, retrieves all users from the database
   * through userService, maps them to GetUserDetailsResponseUserSet using userApiMapper,
   * and returns a ResponseEntity with OK status code and the list of users in the
   * response body.
   * 
   * @param pageable pagination information for the list of users, allowing the method
   * to fetch a subset of the users based on page size and current page number.
   * 
   * @returns a list of user details in REST API format.
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
   * retrieves user details based on their ID and maps them to a `GetUserDetailsResponse`
   * object before returning it as an HTTP response entity.
   * 
   * @param userId ID of the user for whom details are to be retrieved.
   * 
   * @returns a `ResponseEntity` object containing the details of the user with the
   * provided `userId`.
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
   * takes a request body containing a `ForgotPasswordRequest` object and a `String`
   * action parameter, and performs actions related to resetting or requesting a password
   * reset based on the parsed action type. If successful, it returns an `ResponseEntity`
   * with an `OK` status code; otherwise, it returns an error response.
   * 
   * @param action type of password-related action to be performed, and it is used to
   * determine the appropriate response based on the value of the `parsedAction` variable.
   * 
   * @param forgotPasswordRequest Forgot Password Request object containing the user's
   * email address and other details required to initiate the password reset process.
   * 
   * @returns a `ResponseEntity` object containing an `OK` status and no error message.
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
   * receives a request to list all members of all houses of a user with a given `userId`.
   * It uses the `houseService` to retrieve a list of house members for each house owned
   * by the user, maps them to a `HashSet`, and then maps each set to a `ListHouseMembersResponse`
   * object. The function returns a `ResponseEntity` with the list of house members.
   * 
   * @param userId ID of the user for whom all houses' members are to be listed.
   * 
   * @param pageable request to list all members of all houses of a user, with the
   * ability to page through the results using standard pagination mechanisms.
   * 
   * @returns a `ResponseEntity` object containing a list of `HouseMemberSet` objects
   * in a REST API format.
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
   * confirms an email address for a user by checking with the user service. If the
   * email is confirmed, it returns an ok response entity; otherwise, it returns a bad
   * request response entity.
   * 
   * @param userId identifier of the user whose email is being confirmed.
   * 
   * @param emailConfirmToken token sent to the user's email address for email confirmation.
   * 
   * @returns a `ResponseEntity` object with a status of either `ok` or `badRequest`,
   * depending on whether the email confirmation was successful or not.
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
   * resends an email confirmation to a user's registered email address if the email
   * confirmation was previously sent and failed, returning a success or failure response
   * accordingly.
   * 
   * @param userId 12-digit unique identifier of the user for whom the email confirmation
   * status needs to be checked and resent if necessary.
   * 
   * @returns a `ResponseEntity` object with an `OK` status code and a successful message.
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
























