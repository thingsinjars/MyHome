package com.myhome.configuration.properties.mail;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;


/**
 * Represents a configuration object for mail properties used by the Spring Boot application.
 *
 * - host (String): stores the host address of a mail server.
 *
 * - username (String): stores a string representing the username for mail authentication.
 *
 * - password (String): stores a mail account password.
 *
 * - port (int): represents the port number of the mail server.
 *
 * - protocol (String): represents the communication protocol used for mail.
 *
 * - debug (boolean): represents a boolean flag indicating a debug mode.
 *
 * - devMode (boolean): is a boolean flag indicating a development mode.
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

