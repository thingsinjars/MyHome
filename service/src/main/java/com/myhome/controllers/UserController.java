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

  @Override
  public ResponseEntity<GetUserDetailsResponseUser> getUserDetails(String userId) {
    log.trace("Received request to get details of user with Id[{}]", userId);

    return userService.getUserDetails(userId)
        .map(userApiMapper::userDtoToGetUserDetailsResponse)
        .map(response -> ResponseEntity.status(HttpStatus.OK).body(response))
        .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
  }

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

  @Override
  public ResponseEntity<Void> confirmEmail(String userId, String emailConfirmToken) {
    boolean emailConfirmed = userService.confirmEmail(userId, emailConfirmToken);
    if(emailConfirmed) {
      return ResponseEntity.ok().build();
    } else {
      return ResponseEntity.badRequest().build();
    }
  }

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


















