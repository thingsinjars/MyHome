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

@Service
@RequiredArgsConstructor
public class SecurityTokenSDJpaService implements SecurityTokenService {

  private final SecurityTokenRepository securityTokenRepository;

  @Value("${tokens.reset.expiration}")
  private Duration passResetTokenTime;
  @Value("${tokens.email.expiration}")
  private Duration emailConfirmTokenTime;

  /**
   * generates a unique token, creates it in the database, and sets its owner based on
   * the provided parameters.
   * 
   * @param tokenType type of security token being created, which determines the
   * characteristics and purpose of the token.
   * 
   * @param liveTimeSeconds duration of time that the security token is valid for,
   * measured in seconds.
   * 
   * @param tokenOwner user who owns the security token being created.
   * 
   * @returns a newly created SecurityToken instance containing the specified token,
   * creation and expiry dates, and token owner.
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
   * generates a security token for an user to confirm their email address.
   * 
   * @param tokenOwner user for whom the email confirmation token is being generated.
   * 
   * @returns a security token for email confirmation.
   */
  @Override
  public SecurityToken createEmailConfirmToken(User tokenOwner) {
    return createSecurityToken(SecurityTokenType.EMAIL_CONFIRM, emailConfirmTokenTime, tokenOwner);
  }

  /**
   * creates a security token for password reset, generating a unique token based on
   * the current time and the user's account information.
   * 
   * @param tokenOwner User object for which the password reset token is being generated.
   * 
   * @returns a security token with the `SecurityTokenType.RESET` value and a customized
   * expiration time based on the token owner.
   */
  @Override
  public SecurityToken createPasswordResetToken(User tokenOwner) {
    return createSecurityToken(SecurityTokenType.RESET, passResetTokenTime, tokenOwner);
  }

  /**
   * saves a SecurityToken to the repository and marks it as used, returning the saved
   * token.
   * 
   * @param token SecurityToken object that is being processed and updated by the
   * `useToken()` method.
   * 
   * @returns a valid SecurityToken instance with updated `used` flag and persisted in
   * the repository.
   */
  @Override
  public SecurityToken useToken(SecurityToken token) {
    token.setUsed(true);
    token = securityTokenRepository.save(token);
    return token;
  }

  /**
   * takes a `LocalDate` and a `Duration` as input, and returns the resulting `LocalDate`
   * after adding the specified number of days to the original date.
   * 
   * @param date local date that is to be modified by adding a specified number of days.
   * 
   * @param liveTime number of days that the output date should be after the given `date`.
   * 
   * @returns a new `LocalDate` instance representing the date that is `liveTime` days
   * after the initial input date.
   */
  private LocalDate getDateAfterDays(LocalDate date, Duration liveTime) {
    return date.plusDays(liveTime.toDays());
  }
}
























