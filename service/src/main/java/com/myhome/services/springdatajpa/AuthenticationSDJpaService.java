package com.myhome.services.springdatajpa;

import com.myhome.controllers.dto.UserDto;
import com.myhome.controllers.exceptions.CredentialsIncorrectException;
import com.myhome.controllers.exceptions.UserNotFoundException;
import com.myhome.domain.AuthenticationData;
import com.myhome.model.LoginRequest;
import com.myhome.security.jwt.AppJwt;
import com.myhome.security.jwt.AppJwtEncoderDecoder;
import com.myhome.services.AuthenticationService;
import java.time.Duration;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Handles user authentication by verifying login credentials and issuing JWT tokens
 * with a specified expiration time.
 */
@Slf4j
@Service
public class AuthenticationSDJpaService implements AuthenticationService {

  private final Duration tokenExpirationTime;
  private final String tokenSecret;

  private final UserSDJpaService userSDJpaService;
  private final AppJwtEncoderDecoder appJwtEncoderDecoder;
  private final PasswordEncoder passwordEncoder;

  public AuthenticationSDJpaService(@Value("${token.expiration_time}") Duration tokenExpirationTime,
      @Value("${token.secret}") String tokenSecret,
      UserSDJpaService userSDJpaService,
      AppJwtEncoderDecoder appJwtEncoderDecoder,
      PasswordEncoder passwordEncoder) {
    this.tokenExpirationTime = tokenExpirationTime;
    this.tokenSecret = tokenSecret;
    this.userSDJpaService = userSDJpaService;
    this.appJwtEncoderDecoder = appJwtEncoderDecoder;
    this.passwordEncoder = passwordEncoder;
  }

  /**
   * Handles user authentication by verifying email and password, generating a JWT
   * token, and returning an `AuthenticationData` object containing the token and user
   * ID.
   *
   * @param loginRequest login credentials submitted by the user, containing their email
   * address and password.
   *
   * Extract the Email from the loginRequest and pass it to the userSDJpaService to
   * find the user.
   *
   * @returns an `AuthenticationData` object containing a JWT token and a user ID.
   *
   * The output returned by the `login` function is an instance of `AuthenticationData`,
   * which has two main properties:
   * - `encodedToken`: a string representing the encoded JWT token.
   * - `userId`: a unique identifier of the logged-in user.
   */
  @Override
  public AuthenticationData login(LoginRequest loginRequest) {
    log.trace("Received login request");
    final UserDto userDto = userSDJpaService.findUserByEmail(loginRequest.getEmail())
        .orElseThrow(() -> new UserNotFoundException(loginRequest.getEmail()));
    if (!isPasswordMatching(loginRequest.getPassword(), userDto.getEncryptedPassword())) {
      throw new CredentialsIncorrectException(userDto.getUserId());
    }
    final AppJwt jwtToken = createJwt(userDto);
    final String encodedToken = appJwtEncoderDecoder.encode(jwtToken, tokenSecret);
    return new AuthenticationData(encodedToken, userDto.getUserId());
  }

  /**
   * Compares a provided password with a stored password in a database. It uses a
   * password encoder to hash the input password and match it with the stored hash. The
   * function returns true if the passwords match and false otherwise.
   *
   * @param requestPassword password entered by the user for verification purposes.
   *
   * @param databasePassword hashed password stored in the database for comparison.
   *
   * @returns a boolean value indicating whether the input passwords match after encoding.
   */
  private boolean isPasswordMatching(String requestPassword, String databasePassword) {
    return passwordEncoder.matches(requestPassword, databasePassword);
  }

  /**
   * Generates a JSON Web Token (JWT) with a user ID and an expiration time based on
   * the provided `tokenExpirationTime`. The expiration time is set to the current time
   * plus the specified time period. The resulting JWT is returned in the form of an
   * `AppJwt` object.
   *
   * @param userDto user data, specifically the user's ID, which is used to construct
   * the JWT.
   *
   * @returns an AppJwt object with a user ID and expiration time.
   */
  private AppJwt createJwt(UserDto userDto) {
    final LocalDateTime expirationTime = LocalDateTime.now().plus(tokenExpirationTime);
    return AppJwt.builder()
        .userId(userDto.getUserId())
        .expiration(expirationTime)
        .build();
  }
}
