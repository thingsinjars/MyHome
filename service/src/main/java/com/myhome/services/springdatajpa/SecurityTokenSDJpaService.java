package com.myhome.services.springdatajpa;

import com.myhome.domain.SecurityTokenType;
import com.myhome.domain.SecurityToken;
import com.myhome.domain.User;
import com.myhome.repositories.SecurityTokenRepository;
import com.myhome.services.SecurityTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Provides services for managing security tokens, including creation and usage,
 * utilizing Spring Data JPA for database operations. It offers methods for creating
 * email confirmation and password reset tokens, as well as marking tokens as used.
 */
@Service
@RequiredArgsConstructor
public class SecurityTokenSDJpaService implements SecurityTokenService {

  private final SecurityTokenRepository securityTokenRepository;

  @Value("${tokens.reset.expiration}")
  private Duration passResetTokenTime;
  @Value("${tokens.email.expiration}")
  private Duration emailConfirmTokenTime;

  /**
   * Generates a unique security token, sets its creation and expiry dates, and stores
   * it in the database. The token is associated with a specified user and has a specified
   * lifetime. The function returns the newly created security token.
   *
   * @param tokenType type of security token being created, influencing the newSecurityToken
   * object.
   *
   * @param liveTimeSeconds duration in seconds after which the security token expires.
   *
   * @param tokenOwner owner of the newly created security token, which is set as the
   * token owner in the `SecurityToken` object.
   *
   * @returns a saved `SecurityToken` object.
   */
  private SecurityToken createSecurityToken(SecurityTokenType tokenType, Duration liveTimeSeconds, User tokenOwner) {
    String token = UUID.randomUUID().toString();
    LocalDate creationDate = LocalDate.now();
    LocalDate expiryDate = getDateAfterDays(LocalDate.now(), liveTimeSeconds);
    SecurityToken newSecurityToken = new SecurityToken(tokenType, token, creationDate, expiryDate, false, null);
    newSecurityToken.setTokenOwner(tokenOwner);
    newSecurityToken = securityTokenRepository.save(newSecurityToken);
    return newSecurityToken;
  }

  /**
   * Generates a security token for email confirmation. It creates a security token
   * with a specific type and expiration time, and associates it with a given user. The
   * token is then returned for further use.
   *
   * @param tokenOwner user whose email confirmation is being confirmed.
   *
   * @returns a `SecurityToken` instance.
   */
  @Override
  public SecurityToken createEmailConfirmToken(User tokenOwner) {
    return createSecurityToken(SecurityTokenType.EMAIL_CONFIRM, emailConfirmTokenTime, tokenOwner);
  }

  /**
   * Generates a security token for password reset purposes. It calls another function,
   * `createSecurityToken`, to create the token with the specified type, time to live,
   * and token owner. The token type is set to `RESET` for password reset functionality.
   *
   * @param tokenOwner user for whom the password reset token is being created.
   *
   * @returns a SecurityToken object representing a password reset token.
   */
  @Override
  public SecurityToken createPasswordResetToken(User tokenOwner) {
    return createSecurityToken(SecurityTokenType.RESET, passResetTokenTime, tokenOwner);
  }

  /**
   * Marks a SecurityToken as used by setting its 'used' status to true, saves the
   * updated token to the repository, and returns the saved token.
   *
   * @param token SecurityToken object to be updated and saved in the repository.
   *
   * @returns the updated SecurityToken object with its used status set to true.
   */
  @Override
  public SecurityToken useToken(SecurityToken token) {
    token.setUsed(true);
    token = securityTokenRepository.save(token);
    return token;
  }

  /**
   * Calculates a date by adding a specified duration of days to a given date. The
   * duration is defined by a `Duration` object, which is converted to days before
   * addition. The result is a new date.
   *
   * @param date initial date from which a specified duration is added.
   *
   * @param liveTime duration of time that is added to the `date` parameter.
   *
   * @returns a `LocalDate` representing the input date plus the number of days specified
   * by `liveTime`.
   */
  private LocalDate getDateAfterDays(LocalDate date, Duration liveTime) {
    return date.plusDays(liveTime.toDays());
  }
}
