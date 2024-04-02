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
   * generates a random password recovery code for a user and sends an email with the
   * code to the user's registered email address using a templated message.
   * 
   * @param user user for whom the password recovery code is being generated and sent.
   * 
   * @param randomCode 6-digit code sent to the user's email address for password recovery.
   * 
   * @returns a boolean value indicating whether an email was sent to the user's
   * registered email address with a password recover code.
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
   * sends an email to a user upon successful password change.
   * 
   * @param user user for whom the password change was successfuly completed.
   * 
   * @returns a boolean value indicating whether an email was successfully sent to the
   * user's registered email address.
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
   * sends a confirmation email to a user's registered email address after creating an
   * account, with a link to confirm the account creation.
   * 
   * @param user created account user.
   * 
   * @param emailConfirmToken token for the user to confirm their email address.
   * 
   * @returns a boolean value indicating whether an email was sent successfully to
   * confirm the account creation.
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
   * sends an email to a user's registered email address with a confirmation message.
   * 
   * @param user user for whom the account confirmation email should be sent.
   * 
   * @returns a boolean value indicating whether an email was sent to the user's
   * registered email address.
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
   * creates a MimeMessage object and sets various properties such as from, to, subject,
   * and text. It then uses the mailSender's send method to send the message.
   * 
   * @param to email address of the recipient to whom the HTML message is sent.
   * 
   * @param subject subject line of the email to be sent.
   * 
   * @param htmlBody text message that is sent as an HTML email.
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
   * takes an email to, subject, template name, and model as parameters. It uses Thymeleaf
   * to generate HTML content from a template based on the provided model, then sends
   * the message via `sendHtmlMessage`. If any errors occur during sending, it logs
   * them and returns false.
   * 
   * @param emailTo email address to which the generated HTML message will be sent.
   * 
   * @param subject subject line of the email to be sent.
   * 
   * @param templateName name of the Thymeleaf template to be processed and rendered
   * as an HTML message.
   * 
   * @param templateModel mapping of Thymeleaf templates to application data, which is
   * used to generate the email body through the `emailTemplateEngine.process()` method.
   * 
   * @returns a boolean value indicating whether the email was sent successfully or not.
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
   * generates a unique link for an email confirmation process to verify a user's
   * account. The link is constructed by combining the base URL with the user's ID and
   * security token.
   * 
   * @param user User object containing information about the user for whom the email
   * confirmation link should be generated.
   * 
   * @param token security token that is used to authenticate the user and retrieve
   * their email confirmation link.
   * 
   * @returns a URL string in the format `/users/{userId}/email-confirm/{token}`.
   */
  private String getAccountConfirmLink(User user, SecurityToken token) {
    String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
        .replacePath(null)
        .build()
        .toUriString();
    return String.format("%s/users/%s/email-confirm/%s", baseUrl, user.getUserId(), token.getToken());
  }

  /**
   * retrieves a message from a message source based on a given property name, handling
   * potential localization errors.
   * 
   * @param prop string that will be localized and returned by the `getLocalizedMessage()`
   * function.
   * 
   * @returns a localized message based on a given property name and locale.
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






















