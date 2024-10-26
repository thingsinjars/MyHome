package com.myhome.services.springdatajpa;

import com.myhome.configuration.properties.mail.MailProperties;
import com.myhome.configuration.properties.mail.MailTemplatesNames;
import com.myhome.domain.SecurityToken;
import com.myhome.domain.User;
import com.myhome.services.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.HashMap;
import java.util.Map;

/**
 * Provides email sending functionality for various events such as password recovery,
 * account creation, and confirmation.
 */
@Service
@ConditionalOnProperty(value = "spring.mail.devMode", havingValue = "false", matchIfMissing = false)
@RequiredArgsConstructor
@Slf4j
public class MailSDJpaService implements MailService {

  private final ITemplateEngine emailTemplateEngine;
  private final JavaMailSender mailSender;
  private final ResourceBundleMessageSource messageSource;
  private final MailProperties mailProperties;

  /**
   * Generates a password recovery email with a random code to the user's email address,
   * then sends the email. It uses a template model to populate the email's content
   * with the user's name and the random code.
   *
   * @param user user for whom the password recovery code is being sent.
   *
   * @param randomCode random code generated for password recovery, which is included
   * in the email sent to the user.
   *
   * @returns a boolean indicating whether a password recovery email was successfully
   * sent to the user.
   */
  @Override
  public boolean sendPasswordRecoverCode(User user, String randomCode) {
    Map<String, Object> templateModel = new HashMap<>();
    templateModel.put("username", user.getName());
    templateModel.put("recoverCode", randomCode);
    String passwordRecoverSubject = getLocalizedMessage("locale.EmailSubject.passwordRecover");
    boolean mailSent = send(user.getEmail(), passwordRecoverSubject,
        MailTemplatesNames.PASSWORD_RESET.filename, templateModel);
    return mailSent;
  }

  /**
   * Sends an email to the specified user with a password change confirmation template
   * after verifying the email was successfully sent.
   *
   * @param user user whose password has been successfully changed, providing the
   * necessary information to construct and send a notification email.
   *
   * @returns a boolean indicating whether a password change email was successfully sent.
   */
  @Override
  public boolean sendPasswordSuccessfullyChanged(User user) {
    Map<String, Object> templateModel = new HashMap<>();
    templateModel.put("username", user.getName());
    String passwordChangedSubject = getLocalizedMessage("locale.EmailSubject.passwordChanged");
    boolean mailSent = send(user.getEmail(), passwordChangedSubject,
        MailTemplatesNames.PASSWORD_CHANGED.filename, templateModel);
    return mailSent;
  }

  /**
   * Sends an email to a user confirming their account creation.
   * It generates a link for email confirmation and populates a template model with the
   * user's name and email confirmation link.
   * The email is sent using a mail service.
   *
   * @param user account being created and is used to retrieve the username and email
   * address.
   *
   * Extract its name.
   *
   * @param emailConfirmToken token used for email confirmation.
   *
   * Extract its properties.
   *
   * The `emailConfirmToken` object likely contains a `token` property.
   *
   * @returns a boolean indicating whether an email was sent to the user's email address.
   */
  @Override
  public boolean sendAccountCreated(User user, SecurityToken emailConfirmToken) {
    Map<String, Object> templateModel = new HashMap<>();
    String emailConfirmLink = getAccountConfirmLink(user, emailConfirmToken);
    templateModel.put("username", user.getName());
    templateModel.put("emailConfirmLink", emailConfirmLink);
    String accountCreatedSubject = getLocalizedMessage("locale.EmailSubject.accountCreated");
    boolean mailSent = send(user.getEmail(), accountCreatedSubject,
        MailTemplatesNames.ACCOUNT_CREATED.filename, templateModel);
    return mailSent;
  }

  /**
   * Sends an email to a user confirming their account, using a template to populate
   * the email with the user's name. The email is sent using a mail service, and the
   * function returns whether the email was successfully sent.
   *
   * @param user user whose account confirmation email is being sent.
   *
   * @returns a boolean value indicating whether the email was successfully sent.
   */
  @Override
  public boolean sendAccountConfirmed(User user) {
    Map<String, Object> templateModel = new HashMap<>();
    templateModel.put("username", user.getName());
    String accountConfirmedSubject = getLocalizedMessage("locale.EmailSubject.accountConfirmed");
    boolean mailSent = send(user.getEmail(), accountConfirmedSubject,
        MailTemplatesNames.ACCOUNT_CONFIRMED.filename, templateModel);
    return mailSent;
  }

  /**
   * Constructs and sends an HTML email message using a Java mail sender. It takes
   * recipient email, subject, and HTML body as parameters, sets the sender's email,
   * and sends the message using the specified mail sender.
   *
   * @param to recipient's email address to whom the message will be sent.
   *
   * @param subject title of the email message being sent.
   *
   * @param htmlBody the HTML content of the email message, which is set as the message
   * body.
   */
  private void sendHtmlMessage(String to, String subject, String htmlBody) throws MessagingException {
    MimeMessage message = mailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
    helper.setFrom(mailProperties.getUsername());
    helper.setTo(to);
    helper.setSubject(subject);
    helper.setText(htmlBody, true);
    mailSender.send(message);
  }

  /**
   * Processes an email template using Thymeleaf, sets variables from a map, and sends
   * an HTML email to a specified recipient. It returns true on success and false on
   * failure, logging errors if an exception occurs.
   *
   * @param emailTo recipient's email address to which the email is being sent.
   *
   * @param subject subject of the email being sent.
   *
   * @param templateName name of the email template to be processed using the Thymeleaf
   * engine.
   *
   * @param templateModel data that is used to populate the placeholders in the email
   * template.
   *
   * Deconstruct the Map of templateModel, it contains a set of key-value pairs where
   * keys are Strings and values are Objects.
   *
   * @returns a boolean value indicating whether the email was sent successfully.
   */
  private boolean send(String emailTo, String subject, String templateName, Map<String, Object> templateModel) {
    try {
      Context thymeleafContext = new Context(LocaleContextHolder.getLocale());
      thymeleafContext.setVariables(templateModel);
      String htmlBody = emailTemplateEngine.process(templateName, thymeleafContext);
      sendHtmlMessage(emailTo, subject, htmlBody);
    } catch (MailException | MessagingException mailException) {
      log.error("Mail send error!", mailException);
      return false;
    }
    return true;
  }

  /**
   * Constructs a URL for email confirmation by combining a base URL with the user's
   * ID and a security token. It uses the `ServletUriComponentsBuilder` to create the
   * base URL from the current context path. The resulting URL can be used to confirm
   * a user's email address.
   *
   * @param user user for whom the email confirmation link is being generated.
   *
   * @param token SecurityToken used to confirm a user's account, which is included in
   * the generated link.
   *
   * @returns a URL string for confirming a user's email address.
   */
  private String getAccountConfirmLink(User user, SecurityToken token) {
    String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
        .replacePath(null)
        .build()
        .toUriString();
    return String.format("%s/users/%s/email-confirm/%s", baseUrl, user.getUserId(), token.getToken());
  }

  /**
   * Retrieves a localized message from a message source based on a given property,
   * using the current locale. If an exception occurs, it returns a default error
   * message. The function returns the localized message or the default error message.
   *
   * @param prop property key used to retrieve a localized message from the message source.
   *
   * @returns a localized message based on the given property or a default error message
   * if localization fails.
   */
  private String getLocalizedMessage(String prop) {
    String message = "";
    try {
      message = messageSource.getMessage(prop, null, LocaleContextHolder.getLocale());
    } catch (Exception e) {
      message = prop + ": localization error";
    }
    return message;
  }

}
