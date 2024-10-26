package com.myhome.services.springdatajpa;

import com.myhome.domain.SecurityToken;
import com.myhome.domain.User;
import com.myhome.services.MailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.MailSendException;
import org.springframework.stereotype.Service;

/**
 * Provides email notifications for various user events, including password recovery,
 * account confirmation, password change, and account creation. It is conditionally
 * enabled based on the spring.mail.dev-mode property.
 */
@Slf4j
@Service
@ConditionalOnProperty(value = "spring.mail.dev-mode", havingValue = "true", matchIfMissing = true)
public class DevMailSDJpaService implements MailService {

  /**
   * Sends a password recovery code to a specified user via email and logs the event,
   * returning true to indicate successful execution.
   *
   * @param user recipient of the password recovery code and its associated user ID.
   *
   * @param randomCode password recovery code sent to the user.
   *
   * @returns a boolean indicating success, set to true.
   */
  @Override
  public boolean sendPasswordRecoverCode(User user, String randomCode) throws MailSendException {
    log.info(String.format("Password recover code sent to user with id= %s, code=%s", user.getUserId()), randomCode);
    return true;
  }

  /**
   * Logs an information message with the user's ID and returns a boolean indicating
   * that the account confirmation message was sent successfully.
   *
   * @param user user object for whom the account confirmation message is being sent.
   *
   * @returns a boolean value indicating success, set to true.
   */
  @Override
  public boolean sendAccountConfirmed(User user) {
    log.info(String.format("Account confirmed message sent to user with id=%s", user.getUserId()));
    return true;
  }

  /**
   * Logs a message indicating a password change notification has been sent to a user
   * and returns true to indicate successful completion of the operation.
   *
   * @param user user for whom a password change notification is being sent, with the
   * `userId` being logged for informational purposes.
   *
   * @returns a boolean value indicating successful operation, always returning true.
   */
  @Override
  public boolean sendPasswordSuccessfullyChanged(User user) {
    log.info(String.format("Password successfully changed message sent to user with id=%s", user.getUserId()));
    return true;
  }


  /**
   * Logs a message indicating an account creation notification has been sent to the
   * specified user and returns a boolean value indicating success.
   *
   * @param user user account for which an account creation message is being sent.
   *
   * @param emailConfirmToken security token sent to the user's email to confirm their
   * account creation.
   *
   * @returns a boolean value indicating success, set to true.
   */
  @Override
  public boolean sendAccountCreated(User user, SecurityToken emailConfirmToken) {
    log.info(String.format("Account created message sent to user with id=%s", user.getUserId()));
    return true;
  }


}
