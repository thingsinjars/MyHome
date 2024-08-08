package com.myhome.configuration.properties.mail;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;


/**
 * Encapsulates Spring Boot configuration properties for mail services with annotations
 * from Lombok and Spring Boot.
 *
 * - host (String): represents the hostname of a mail server.
 *
 * - username (String): represents the user name used for mail operations.
 *
 * - password (String): represents a mail server's authentication password.
 *
 * - port (int): represents an integer value specifying the port number for mail communication.
 *
 * - protocol (String): stores a string representing a mail transfer protocol.
 *
 * - debug (boolean): represents a boolean value indicating whether debugging mode
 * is enabled in the mail configuration.
 *
 * - devMode (boolean): indicates whether the development mode is enabled for the
 * mail configuration.
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

