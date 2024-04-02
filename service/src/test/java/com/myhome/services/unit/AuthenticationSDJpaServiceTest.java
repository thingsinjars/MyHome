package com.myhome.services.unit;

import com.myhome.controllers.dto.UserDto;
import com.myhome.controllers.exceptions.CredentialsIncorrectException;
import com.myhome.controllers.exceptions.UserNotFoundException;
import com.myhome.domain.AuthenticationData;
import com.myhome.model.LoginRequest;
import com.myhome.security.jwt.AppJwt;
import com.myhome.security.jwt.AppJwtEncoderDecoder;
import com.myhome.services.springdatajpa.AuthenticationSDJpaService;
import com.myhome.services.springdatajpa.UserSDJpaService;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class AuthenticationSDJpaServiceTest {

  private final String USER_ID = "test-user-id";
  private final String USERNAME = "test-user-name";
  private final String USER_EMAIL = "test-user-email";
  private final String USER_PASSWORD = "test-user-password";
  private final String REQUEST_PASSWORD = "test-request-password";
  private final Duration TOKEN_LIFETIME = Duration.ofDays(1);
  private final String SECRET = "secret";

  @Mock
  private final UserSDJpaService userSDJpaService = mock(UserSDJpaService.class);
  @Mock
  private final AppJwtEncoderDecoder appJwtEncoderDecoder = mock(AppJwtEncoderDecoder.class);
  @Mock
  private final PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
  private final AuthenticationSDJpaService authenticationSDJpaService =
      new AuthenticationSDJpaService(TOKEN_LIFETIME, SECRET, userSDJpaService, appJwtEncoderDecoder,
          passwordEncoder);

  /**
   * tests the login feature by verifying that a valid user can be authenticated and
   * obtain an JWT token.
   */
  @Test
  void loginSuccess() {
    // given
    LoginRequest request = getDefaultLoginRequest();
    UserDto userDto = getDefaultUserDtoRequest();
    AppJwt appJwt = getDefaultJwtToken(userDto);
    String encodedJwt = appJwtEncoderDecoder.encode(appJwt, SECRET);
    given(userSDJpaService.findUserByEmail(request.getEmail()))
        .willReturn(Optional.of(userDto));
    given(passwordEncoder.matches(request.getPassword(), userDto.getEncryptedPassword()))
        .willReturn(true);
    given(appJwtEncoderDecoder.encode(appJwt, SECRET))
        .willReturn(encodedJwt);

    // when
    AuthenticationData authenticationData = authenticationSDJpaService.login(request);

    // then
    assertNotNull(authenticationData);
    assertEquals(authenticationData.getUserId(), userDto.getUserId());
    assertEquals(authenticationData.getJwtToken(), encodedJwt);
    verify(userSDJpaService).findUserByEmail(request.getEmail());
    verify(passwordEncoder).matches(request.getPassword(), userDto.getEncryptedPassword());
    verify(appJwtEncoderDecoder).encode(appJwt, SECRET);
  }

  /**
   * tests the login functionality when no user is found with the provided email address.
   * It throws a `UserNotFoundException` when no user is returned from the `findUserByEmail`
   * method.
   */
  @Test
  void loginUserNotFound() {
    // given
    LoginRequest request = getDefaultLoginRequest();
    given(userSDJpaService.findUserByEmail(request.getEmail()))
        .willReturn(Optional.empty());

    // when and then
    assertThrows(UserNotFoundException.class,
        () -> authenticationSDJpaService.login(request));
  }

  /**
   * tests the scenario where the user's login credentials do not match, causing an
   * `CredentialsIncorrectException` to be thrown when attempting to log in.
   */
  @Test
  void loginCredentialsAreIncorrect() {
    // given
    LoginRequest request = getDefaultLoginRequest();
    UserDto userDto = getDefaultUserDtoRequest();
    given(userSDJpaService.findUserByEmail(request.getEmail()))
        .willReturn(Optional.of(userDto));
    given(passwordEncoder.matches(request.getPassword(), userDto.getEncryptedPassword()))
        .willReturn(false);

    // when and then
    assertThrows(CredentialsIncorrectException.class,
        () -> authenticationSDJpaService.login(request));
  }

  /**
   * creates a default login request with provided email and password.
   * 
   * @returns a `LoginRequest` object containing the email address and password for the
   * default login.
   */
  private LoginRequest getDefaultLoginRequest() {
    return new LoginRequest().email(USER_EMAIL).password(REQUEST_PASSWORD);
  }

  /**
   * creates a default `UserDto` instance with specified user ID, name, email, encrypted
   * password, and community IDs.
   * 
   * @returns a `UserDto` object filled with default values for a user.
   */
  private UserDto getDefaultUserDtoRequest() {
    return UserDto.builder()
        .userId(USER_ID)
        .name(USERNAME)
        .email(USER_EMAIL)
        .encryptedPassword(USER_PASSWORD)
        .communityIds(new HashSet<>())
        .build();
  }

  /**
   * generates a JWT token for an user based on the current date and time, and stores
   * the user ID and expiration time in the token.
   * 
   * @param userDto user details for generating the JWT token.
   * 
   * @returns a newly generated AppJwt token with a user ID and an expiration time.
   */
  private AppJwt getDefaultJwtToken(UserDto userDto) {
    final LocalDateTime expirationTime = LocalDateTime.now().plus(TOKEN_LIFETIME);
    return AppJwt.builder()
        .userId(userDto.getUserId())
        .expiration(expirationTime)
        .build();
  }
}






















