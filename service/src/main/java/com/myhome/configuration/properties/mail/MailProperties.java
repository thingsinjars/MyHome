package com.myhome.configuration.properties.mail;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;


/**
 * Provides a configuration container for Spring Boot application's mail settings
 * using the Lombok library and Spring Boot's ConfigurationProperties annotation.
 *
 * - host (String): specifies the hostname of an email server.
 *
 * - username (String): represents a string value for the username.
 *
 * - password (String): represents the password for mail services.
 *
 * - port (int): represents an integer value for mail server's port number.
 *
 * - protocol (String): represents the mail protocol to be used for sending emails.
 *
 * - debug (boolean): is a boolean property indicating whether to enable debugging
 * mode for mail operations.
 *
 * - devMode (boolean): represents a boolean value indicating whether debug mode is
 * enabled for mail operations.
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

