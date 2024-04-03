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
   * authenticates a user by checking their email and password. If the credentials are
   * correct, it creates an JWT token and returns an AuthenticationData object with the
   * encoded token and user ID.
   * 
   * @param loginRequest authentication request containing the email address and password
   * to be verified for authentication.
   * 
   * @returns an `AuthenticationData` object containing the encoded JWT token and the
   * user ID.
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
   * compares a provided password with a reference password and returns a boolean
   * indicating if they match.
   * 
   * @param requestPassword password entered by the user.
   * 
   * @param databasePassword password stored in the database that the function is
   * checking for matching with the provided `requestPassword`.
   * 
   * @returns a boolean value indicating whether the provided password matches the
   * stored password.
   */
  private boolean isPasswordMatching(String requestPassword, String databasePassword) {
    return passwordEncoder.matches(requestPassword, databasePassword);
  }

  /**
   * creates a JWT token with the specified user ID and expiration time based on the
   * given token expiration time.
   * 
   * @param userDto user details required to generate an JWT token.
   * 
   * @returns a new AppJwt instance containing user-specific information and an expiration
   * time.
   */
  private AppJwt createJwt(UserDto userDto) {
    final LocalDateTime expirationTime = LocalDateTime.now().plus(tokenExpirationTime);
    return AppJwt.builder()
        .userId(userDto.getUserId())
        .expiration(expirationTime)
        .build();
  }
}
























