package com.myhome.configuration.properties.mail;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;


/**
 * Is a configuration class used to define properties for mail services in Spring
 * Boot applications.
 *
 * - host (String): represents the hostname of a mail server.
 *
 * - username (String): represents a string value for a username.
 *
 * - password (String): is a string property.
 *
 * - port (int): represents an integer value specifying the port number for mail operations.
 *
 * - protocol (String): represents the mail protocol used in the MailProperties.
 *
 * - debug (boolean): is a boolean property representing whether debugging mode is
 * enabled for mail operations.
 *
 * - devMode (boolean): represents a boolean value indicating whether development
 * mode is enabled for email configurations in the application.
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

