package com.myhome.configuration.properties.mail;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;


/**
 * Encapsulates configuration properties for mail services using Spring Boot's
 * @ConfigurationProperties annotation with a prefix "spring.mail".
 *
 * - host (String): represents the hostname of the mail server.
 *
 * - username (String): represents a property in the mail configuration for authentication
 * purposes.
 *
 * - password (String): represents a private string property in the MailProperties class.
 *
 * - port (int): represents an integer value for mail server port number.
 *
 * - protocol (String): represents the mail protocol used for sending and receiving
 * emails.
 *
 * - debug (boolean): represents a boolean value indicating whether debugging mode
 * is enabled for mail configurations.
 *
 * - devMode (boolean): represents a boolean property indicating whether development
 * mode is enabled for mail configuration.
 */
@Data
@ConfigurationProperties(prefix = "spring.mail")
public class MailProperties {
  private String host;
  private String username;
  private String password;
  private int port;
  private String protocol;
  private boolean debug;
  private boolean devMode;
}

