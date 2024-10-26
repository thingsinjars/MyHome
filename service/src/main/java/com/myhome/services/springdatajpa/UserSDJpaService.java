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

package com.myhome.services.springdatajpa;

import com.myhome.controllers.dto.UserDto;
import com.myhome.controllers.dto.mapper.UserMapper;
import com.myhome.domain.Community;
import com.myhome.domain.SecurityToken;
import com.myhome.domain.SecurityTokenType;
import com.myhome.domain.User;
import com.myhome.model.ForgotPasswordRequest;
import com.myhome.repositories.UserRepository;
import com.myhome.services.MailService;
import com.myhome.services.SecurityTokenService;
import com.myhome.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implements {@link UserService} and uses Spring Data JPA repository to does its work.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserSDJpaService implements UserService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final PasswordEncoder passwordEncoder;
  private final SecurityTokenService securityTokenService;
  private final MailService mailService;

  /**
   * Handles user registration by verifying email uniqueness, generating a unique ID,
   * encrypting the password, creating a new user in the repository, sending a confirmation
   * email, and returning a user DTO if the email is unique.
   *
   * @param request User data to be created, encapsulated in a `UserDto` object.
   *
   * Contain email.
   *
   * @returns an `Optional` containing a `UserDto` object or an empty `Optional` if the
   * email already exists.
   *
   * The returned `Optional` contains a `UserDto` object.
   * The `UserDto` object has properties mapped from the `User` object, such as user data.
   */
  @Override
  public Optional<UserDto> createUser(UserDto request) {
    if (userRepository.findByEmail(request.getEmail()) == null) {
      generateUniqueUserId(request);
      encryptUserPassword(request);
      User newUser = createUserInRepository(request);
      SecurityToken emailConfirmToken = securityTokenService.createEmailConfirmToken(newUser);
      mailService.sendAccountCreated(newUser, emailConfirmToken);
      UserDto newUserDto = userMapper.userToUserDto(newUser);
      return Optional.of(newUserDto);
    } else {
      return Optional.empty();
    }
  }

  /**
   * Returns a set of all users, utilizing the `listAll` function with a custom page
   * request to fetch 200 users starting from the first page.
   *
   * @returns a set of users, where the set size is limited to 200.
   */
  @Override
  public Set<User> listAll() {
    return listAll(PageRequest.of(0, 200));
  }

  /**
   * Returns a set of all users in the database, paginated according to the provided
   * `Pageable` object. The `userRepository` is used to retrieve the users, and the
   * result is converted to a set.
   *
   * @param pageable pagination criteria, allowing for efficient retrieval of a subset
   * of data from the database.
   *
   * @returns a set of User objects paginated according to the provided Pageable.
   */
  @Override
  public Set<User> listAll(Pageable pageable) {
    return userRepository.findAll(pageable).toSet();
  }

  /**
   * retrieves user details for a given user ID from the database, converts the user
   * data to a `UserDto` object, and includes a set of community IDs associated with
   * the user.
   *
   * @param userId identifier used to retrieve user details from the database.
   *
   * @returns an Optional containing a UserDto object with communityIds.
   *
   * The returned output is an Optional object containing a UserDto object.
   */
  @Override
  public Optional<UserDto> getUserDetails(String userId) {
    Optional<User> userOptional = userRepository.findByUserIdWithCommunities(userId);
    return userOptional.map(admin -> {
      Set<String> communityIds = admin.getCommunities().stream()
          .map(Community::getCommunityId)
          .collect(Collectors.toSet());

      UserDto userDto = userMapper.userToUserDto(admin);
      userDto.setCommunityIds(communityIds);
      return Optional.of(userDto);
    }).orElse(Optional.empty());
  }

  /**
   * Finds a user by their email, retrieves their associated community IDs, and maps
   * the user data to a `UserDto` object. It returns an `Optional` containing the
   * `UserDto` if the user exists, or an empty `Optional` otherwise.
   *
   * @param userEmail email address used to search for a user in the database.
   *
   * @returns an Optional containing a UserDto object with community IDs.
   *
   * The output is an Optional containing a UserDto object.
   */
  public Optional<UserDto> findUserByEmail(String userEmail) {
    return Optional.ofNullable(userRepository.findByEmail(userEmail))
        .map(user -> {
          Set<String> communityIds = user.getCommunities().stream()
              .map(Community::getCommunityId)
              .collect(Collectors.toSet());

          UserDto userDto = userMapper.userToUserDto(user);
          userDto.setCommunityIds(communityIds);
          return userDto;
        });
  }

  /**
   * Handles a password reset request by validating the input, retrieving a user by
   * email, creating a new security token, and sending a password recovery code to the
   * user's email address.
   *
   * @param forgotPasswordRequest request to reset a password, containing the email
   * address associated with the account.
   *
   * Contain an email.
   *
   * @returns a boolean value indicating success or failure of the password reset request.
   */
  @Override
  public boolean requestResetPassword(ForgotPasswordRequest forgotPasswordRequest) {
    return Optional.ofNullable(forgotPasswordRequest)
        .map(ForgotPasswordRequest::getEmail)
        .flatMap(email -> userRepository.findByEmailWithTokens(email)
            .map(user -> {
              SecurityToken newSecurityToken = securityTokenService.createPasswordResetToken(user);
              user.getUserTokens().add(newSecurityToken);
              userRepository.save(user);
              return mailService.sendPasswordRecoverCode(user, newSecurityToken.getToken());
            }))
        .orElse(false);
  }

  /**
   * Verifies a password reset request by finding a user with a valid reset token,
   * updates the user's password, and sends a confirmation email to the user. It returns
   * true if the operation is successful, false otherwise.
   *
   * @param passwordResetRequest request to reset a user's password, containing the
   * user's email, current token, and new password.
   *
   * Extract the email from the request and map it to a user.
   *
   * @returns a boolean indicating whether the password reset operation was successful.
   */
  @Override
  public boolean resetPassword(ForgotPasswordRequest passwordResetRequest) {
    final Optional<User> userWithToken = Optional.ofNullable(passwordResetRequest)
        .map(ForgotPasswordRequest::getEmail)
        .flatMap(userRepository::findByEmailWithTokens);
    return userWithToken
        .flatMap(user -> findValidUserToken(passwordResetRequest.getToken(), user, SecurityTokenType.RESET))
        .map(securityTokenService::useToken)
        .map(token -> saveTokenForUser(userWithToken.get(), passwordResetRequest.getNewPassword()))
        .map(mailService::sendPasswordSuccessfullyChanged)
        .orElse(false);
  }

  /**
   * Verifies an email confirmation for a given user ID and token by checking if the
   * user exists, their email is unconfirmed, and the provided token is valid for email
   * confirmation. If valid, it confirms the user's email and returns true. Otherwise,
   * it returns false.
   *
   * @param userId identifier of the user whose email confirmation is being verified.
   *
   * @param emailConfirmToken token used to verify the user's email address for confirmation.
   *
   * @returns a boolean indicating whether the email confirmation was successful.
   *
   * The output is a Boolean value indicating whether the email confirmation was successful.
   */
  @Override
  public Boolean confirmEmail(String userId, String emailConfirmToken) {
    final Optional<User> userWithToken = userRepository.findByUserIdWithTokens(userId);
    Optional<SecurityToken> emailToken = userWithToken
        .filter(user -> !user.isEmailConfirmed())
        .map(user -> findValidUserToken(emailConfirmToken, user, SecurityTokenType.EMAIL_CONFIRM)
        .map(token -> {
          confirmEmail(user);
          return token;
        })
        .map(securityTokenService::useToken)
        .orElse(null));
    return emailToken.map(token -> true).orElse(false);
  }

  /**
   * Resends an email confirmation to a user if their email has not been confirmed yet.
   * It generates a new confirmation token, removes any unused tokens, and sends the
   * email using the `mailService`.
   *
   * @param userId unique identifier for the user whose email confirmation is to be resent.
   *
   * @returns a boolean indicating whether the email confirmation mail was sent
   * successfully or not.
   */
  @Override
  public boolean resendEmailConfirm(String userId) {
    return userRepository.findByUserId(userId).map(user -> {
      if(!user.isEmailConfirmed()) {
        SecurityToken emailConfirmToken = securityTokenService.createEmailConfirmToken(user);
        user.getUserTokens().removeIf(token -> token.getTokenType() == SecurityTokenType.EMAIL_CONFIRM && !token.isUsed());
        userRepository.save(user);
        boolean mailSend = mailService.sendAccountCreated(user, emailConfirmToken);
        return mailSend;
      } else {
        return false;
      }
    }).orElse(false);
  }

  /**
   * Encrypts a new password for a given user and updates the user's encrypted password
   * in the database.
   * The updated user is then saved to the repository.
   * The function returns the saved user.
   *
   * @param user entity to be updated with a new encrypted password and then saved to
   * the database.
   *
   * @param newPassword password to be encrypted and stored for the given user.
   *
   * @returns the saved User object.
   */
  private User saveTokenForUser(User user, String newPassword) {
    user.setEncryptedPassword(passwordEncoder.encode(newPassword));
    return userRepository.save(user);
  }

  /**
   * Checks for a valid security token among a user's tokens, filtering by unused,
   * matching type, token, and expiry date, and returns the first matching token as an
   * Optional.
   *
   * @param token security token to be searched for in the user's tokens.
   *
   * @param user entity from which a list of user tokens is retrieved for further filtering.
   *
   * @param securityTokenType type of security token being searched for, used to filter
   * valid user tokens.
   *
   * @returns an Optional containing a valid SecurityToken if found, or an empty Optional
   * otherwise.
   */
  private Optional<SecurityToken> findValidUserToken(String token, User user, SecurityTokenType securityTokenType) {
    Optional<SecurityToken> userPasswordResetToken = user.getUserTokens()
        .stream()
        .filter(tok -> !tok.isUsed()
            && tok.getTokenType() == securityTokenType
            && tok.getToken().equals(token)
            && tok.getExpiryDate().isAfter(LocalDate.now()))
        .findFirst();
    return userPasswordResetToken;
  }

  /**
   * Maps a `UserDto` to a `User` object, logs a save operation, and saves the `User`
   * to the repository using the `userRepository`.
   *
   * @param request data to be mapped into a `User` object.
   *
   * @returns a User object saved in the repository, wrapped in a return value.
   */
  private User createUserInRepository(UserDto request) {
    User user = userMapper.userDtoToUser(request);
    log.trace("saving user with id[{}] to repository", request.getId());
    return userRepository.save(user);
  }

  /**
   * Marks a user's email as confirmed, sends a confirmation email using the `mailService`,
   * and updates the user in the database using the `userRepository`.
   *
   * @param user authenticated user whose email confirmation is being processed.
   */
  private void confirmEmail(User user) {
    user.setEmailConfirmed(true);
    mailService.sendAccountConfirmed(user);
    userRepository.save(user);
  }

  /**
   * Encrypts the user's password using a password encoder, storing the encrypted result
   * in the `encryptedPassword` field of the provided `UserDto` object.
   *
   * @param request object that contains user data to be encrypted, specifically the password.
   */
  private void encryptUserPassword(UserDto request) {
    request.setEncryptedPassword(passwordEncoder.encode(request.getPassword()));
  }

  /**
   * Generates a unique identifier for a user by converting a randomly generated UUID
   * into a string and assigns it to the user's ID in the provided UserDto object.
   *
   * @param request object that receives the generated unique user ID.
   */
  private void generateUniqueUserId(UserDto request) {
    request.setUserId(UUID.randomUUID().toString());
  }
}
