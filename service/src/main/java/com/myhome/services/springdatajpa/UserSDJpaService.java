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
   * creates a new user in the system by generating a unique ID, encrypting the password,
   * and sending an email confirmation token to the registered email address. It returns
   * an Optional<UserDto> representing the created user.
   * 
   * @param request user data that is to be created in the system, including their email
   * address and any additional details.
   * 
   * @returns an `Optional` containing a `UserDto` representing the newly created user.
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
   * retrieves a list of `User` objects from the database using a pagination-based query.
   * 
   * @returns a set of `User` objects representing all users in the system.
   */
  @Override
  public Set<User> listAll() {
    return listAll(PageRequest.of(0, 200));
  }

  /**
   * in the code snippet returns a set of all users in the user repository by calling
   * the `findAll` method and converting the resulting list to a set.
   * 
   * @param pageable paging information for the users, allowing the `listAll()` method
   * to retrieve a subset of users based on the current page and page size.
   * 
   * @returns a set of `User` objects.
   */
  @Override
  public Set<User> listAll(Pageable pageable) {
    return userRepository.findAll(pageable).toSet();
  }

  /**
   * retrieves a user's details and community membership from the database using the
   * given user ID, maps them to a `UserDto` object, and returns an optional version
   * of the resulting object.
   * 
   * @param userId unique identifier of the user for whom the user details are being requested.
   * 
   * @returns an optional `UserDto` object containing the user's community IDs and details.
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
   * retrieves a user from the repository based on their email address, maps them to a
   * `UserDto` object, and returns the mapped object with the user's community IDs added.
   * 
   * @param userEmail email address of the user to search for in the database.
   * 
   * @returns a `Optional<UserDto>` object containing the user details and community IDs.
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
   * takes a `ForgotPasswordRequest` object as input and retrieves the user's email
   * from it. It then uses the email to find the user in the repository, create a new
   * security token for password reset, add the token to the user's tokens, and save
   * the updated user in the repository. Finally, it sends an email with a password
   * recovery code to the user.
   * 
   * @param forgotPasswordRequest user's email address for which a password reset link
   * will be generated.
   * 
   * @returns a boolean value indicating whether the password reset process was successful.
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
   * resets a user's password by validating the provided token, finding the corresponding
   * user, and then saving a new token for that user. If successful, it sends a
   * notification to the user's registered email address.
   * 
   * @param passwordResetRequest ForgotPasswordRequest object containing the email
   * address and password reset token of a user who wants to reset their password.
   * 
   * @returns a boolean value indicating whether the password reset process was successful.
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
   * 1) finds a user with an email confirmation token, 2) checks if they're not confirmed,
   * and 3) uses the token to confirm their email.
   * 
   * @param userId identifier of the user for whom the email confirmation status is to
   * be checked.
   * 
   * @param emailConfirmToken confirmation token for the user's email address, which
   * is used to verify the user's email address and mark it as confirmed or unconfirmed.
   * 
   * @returns a boolean value indicating whether the email confirmation process was
   * successful or not.
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
   * checks if a user's email is unconfirmed, and if so, sends an email confirmation
   * token to the user's registered email address. If the token is used, it removes the
   * token from the user's tokens list and saves the updated user record in the database.
   * If the mail send is successful, it returns `true`, otherwise, it returns `false`.
   * 
   * @param userId unique identifier of the user for whom an email confirmation link
   * needs to be resent.
   * 
   * @returns a boolean value indicating whether an email confirmation token was
   * successfully sent to the user.
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
   * saves a user's encrypted password in the database after updating it with the new
   * value provided.
   * 
   * @param user User object that contains the encrypted password, which is then updated
   * and saved in the database by the `saveTokenForUser()` function.
   * 
   * @param newPassword encrypted password for the user that will be saved in the
   * database by the `saveTokenForUser()` function.
   * 
   * @returns a saved User object with an encrypted password.
   */
  private User saveTokenForUser(User user, String newPassword) {
    user.setEncryptedPassword(passwordEncoder.encode(newPassword));
    return userRepository.save(user);
  }

  /**
   * queries a database for an unused security token with the matching token type and
   * value, and expiration date greater than the current date, and returns the
   * Optional<SecurityToken> if found, otherwise returns None.
   * 
   * @param token token being searched for in the `user.getUserTokens()` collection,
   * which is used to determine if it exists and meets certain criteria set by the `securityTokenType`.
   * 
   * @param user User object that is used to filter the user tokens based on the provided
   * token, security token type, and user credentials.
   * 
   * @param securityTokenType type of security token being searched for, and is used
   * to filter the stream of user tokens to only include those with the specified type.
   * 
   * @returns an `Optional` object containing the valid security token for the specified
   * user and type, if found.
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
   * creates a new user object from a UserDto request and saves it to the repository
   * for storage.
   * 
   * @param request `UserDto` object containing the details of the user to be created
   * in the repository, which is passed through to the `userMapper` to convert it into
   * a `User` object before saving it to the repository.
   * 
   * @returns a saved User object in the repository.
   */
  private User createUserInRepository(UserDto request) {
    User user = userMapper.userDtoToUser(request);
    log.trace("saving user with id[{}] to repository", request.getId());
    return userRepository.save(user);
  }

  /**
   * updates a user's email status to confirmed and sends an account confirmation
   * notification to the user, then saves the updated user record in the repository.
   * 
   * @param user User object that contains the email to be confirmed, and its methods
   * are used to set the email as confirmed and send a notification to the user's
   * registered email address.
   */
  private void confirmEmail(User user) {
    user.setEmailConfirmed(true);
    mailService.sendAccountConfirmed(user);
    userRepository.save(user);
  }

  /**
   * encrypts a user's password by passing it through the `passwordEncoder`. The encrypted
   * password is then stored in the `request` object.
   * 
   * @param request UserDto object that contains the original password to be encrypted,
   * and it is used to store the encrypted password in the encoded form.
   */
  private void encryptUserPassword(UserDto request) {
    request.setEncryptedPassword(passwordEncoder.encode(request.getPassword()));
  }

  /**
   * generates a unique user ID for a given UserDto object using the UUID library and
   * sets it as the `UserId` property of the request object.
   * 
   * @param request userDto object containing information about the user whose unique
   * ID is being generated.
   */
  private void generateUniqueUserId(UserDto request) {
    request.setUserId(UUID.randomUUID().toString());
  }
}
























