package com.myhome.services.springdatajpa;

import com.myhome.domain.SecurityToken;
import com.myhome.domain.User;
import com.myhome.services.MailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.MailSendException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@ConditionalOnProperty(value = "spring.mail.dev-mode", havingValue = "true", matchIfMissing = true)
public class DevMailSDJpaService implements MailService {

  /**
   * sends a password recovery code to a specified user via email.
   * 
   * @param user User object whose password recovery code is being sent.
   * 
   * @param randomCode 6-digit password recover code that is sent to the user's registered
   * email address for verification purposes.
   * 
   * @returns a success message indicating that the password recover code has been sent
   * to the user.
   */
  @Override
  public boolean sendPasswordRecoverCode(User user, String randomCode) throws MailSendException {
    log.info(String.format("Password recover code sent to user with id= %s, code=%s", user.getUserId()), randomCode);
    return true;
  }

  /**
   * sends a message to a user indicating that their account has been confirmed.
   * 
   * @param user User object containing the user's account confirmation information
   * that is being sent an account confirmed message.
   * 
   * @returns a boolean value indicating successful message sending.
   */
  @Override
  public boolean sendAccountConfirmed(User user) {
    log.info(String.format("Account confirmed message sent to user with id=%s", user.getUserId()));
    return true;
  }

  /**
   * sends a message to a user with a specified ID indicating that their password has
   * been successfully changed.
   * 
   * @param user User object whose password has been successfully changed.
   * 
   * @returns a message indicating that the user's password has been successfully changed.
   */
  @Override
  public boolean sendPasswordSuccessfullyChanged(User user) {
    log.info(String.format("Password successfully changed message sent to user with id=%s", user.getUserId()));
    return true;
  }


  /**
   * sends a message to a user upon creation of their account, logging the event and
   * returning `true`.
   * 
   * @param user user who has created an account and is passed to the `log.info()`
   * method to log a message indicating that the account has been created.
   * 
   * @param emailConfirmToken Security Token sent to the user for email confirmation,
   * which is used to verify the user's identity and complete their account creation.
   * 
   * @returns a boolean value indicating that the account creation message was successfully
   * sent to the user.
   */
  @Override
  public boolean sendAccountCreated(User user, SecurityToken emailConfirmToken) {
    log.info(String.format("Account created message sent to user with id=%s", user.getUserId()));
    return true;
  }


}






















