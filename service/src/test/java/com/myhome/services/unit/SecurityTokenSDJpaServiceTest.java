package com.myhome.services.unit;

import com.myhome.domain.SecurityToken;
import com.myhome.domain.SecurityTokenType;
import com.myhome.domain.User;
import com.myhome.repositories.SecurityTokenRepository;
import com.myhome.services.springdatajpa.SecurityTokenSDJpaService;
import helpers.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Provides unit tests for the SecurityTokenSDJpaService class, ensuring its functionality
 * in creating and saving security tokens.
 */
public class SecurityTokenSDJpaServiceTest {

  private final Duration TEST_TOKEN_LIFETIME_SECONDS = Duration.ofDays(1);

  @Mock
  private SecurityTokenRepository securityTokenRepository;

  @InjectMocks
  private SecurityTokenSDJpaService securityTokenSDJpaService;

  /**
   * Initializes the testing environment, sets Mockito annotations, and configures the
   * lifetime of security tokens for password reset and email confirmation.
   */
  @BeforeEach
  private void init() {
    MockitoAnnotations.initMocks(this);
    ReflectionTestUtils.setField(securityTokenSDJpaService, "passResetTokenTime",
        TEST_TOKEN_LIFETIME_SECONDS);
    ReflectionTestUtils.setField(securityTokenSDJpaService, "emailConfirmTokenTime",
        TEST_TOKEN_LIFETIME_SECONDS);
  }

  /**
   * Generates a password reset token for a user, saves it to the repository, and
   * verifies its creation and expiry dates, token type, and owner.
   */
  @Test
  void createSecurityToken() {
    // given
    User user = new User();
    user.setUserId(TestUtils.General.generateUniqueId());
    SecurityTokenType testTokenType = SecurityTokenType.RESET;
    when(securityTokenRepository.save(any()))
        .then(returnsFirstArg());

    // when
    SecurityToken actualSecurityToken = securityTokenSDJpaService.createPasswordResetToken(user);
    LocalDate creationDate = actualSecurityToken.getCreationDate();
    LocalDate expiryDate = actualSecurityToken.getExpiryDate();
    Duration lifetime = Duration.between(creationDate.atStartOfDay(), expiryDate.atStartOfDay());

    // then
    assertEquals(actualSecurityToken.getTokenType(), testTokenType);
    assertTrue(creationDate.isBefore(expiryDate));
    assertEquals(lifetime, TEST_TOKEN_LIFETIME_SECONDS);
    assertEquals(user, actualSecurityToken.getTokenOwner());
    assertNotNull(actualSecurityToken.getToken());
    verify(securityTokenRepository).save(any());
  }

  /**
   * Generates a password reset token for a given user, saves it to the database, and
   * returns the created security token with a predefined creation date, expiry date,
   * and token type.
   */
  @Test
  void createPasswordResetToken() {
    // given
    User user = new User();
    user.setUserId(TestUtils.General.generateUniqueId());
    when(securityTokenRepository.save(any()))
        .then(returnsFirstArg());

    // when
    SecurityToken actualSecurityToken = securityTokenSDJpaService.createPasswordResetToken(user);
    LocalDate creationDate = actualSecurityToken.getCreationDate();
    LocalDate expiryDate = actualSecurityToken.getExpiryDate();
    Duration lifetime = Duration.between(creationDate.atStartOfDay(), expiryDate.atStartOfDay());

    // then
    assertEquals(actualSecurityToken.getTokenType(), SecurityTokenType.RESET);
    assertTrue(creationDate.isBefore(expiryDate));
    assertEquals(lifetime, TEST_TOKEN_LIFETIME_SECONDS);
    assertEquals(user, actualSecurityToken.getTokenOwner());
    assertNotNull(actualSecurityToken.getToken());
    verify(securityTokenRepository).save(any());
  }

  /**
   * Generates a security token for email confirmation, saves it to the repository, and
   * returns the token with a specified lifetime and owner.
   */
  @Test
  void createEmailConfirmToken() {
    // given
    User user = new User();
    user.setUserId(TestUtils.General.generateUniqueId());
    when(securityTokenRepository.save(any()))
        .then(returnsFirstArg());

    // when
    SecurityToken actualSecurityToken = securityTokenSDJpaService.createEmailConfirmToken(user);
    LocalDate creationDate = actualSecurityToken.getCreationDate();
    LocalDate expiryDate = actualSecurityToken.getExpiryDate();
    Duration lifetime = Duration.between(creationDate.atStartOfDay(), expiryDate.atStartOfDay());

    // then
    assertEquals(actualSecurityToken.getTokenType(), SecurityTokenType.EMAIL_CONFIRM);
    assertTrue(creationDate.isBefore(expiryDate));
    assertEquals(lifetime, TEST_TOKEN_LIFETIME_SECONDS);
    assertEquals(user, actualSecurityToken.getTokenOwner());
    assertNotNull(actualSecurityToken.getToken());
    verify(securityTokenRepository).save(any());
  }

}
