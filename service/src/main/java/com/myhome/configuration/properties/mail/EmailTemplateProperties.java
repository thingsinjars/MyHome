package com.myhome.configuration.properties.mail;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Serves as a configuration container for email template settings such as file path,
 * format, encoding, and caching preferences.
 * Fields:
 * 	- path (String): in the EmailTemplateProperties class represents a file path where
 * an email template is stored.
 * 	- format (String): in the EmailTemplateProperties class represents a string value
 * defining the email template file format.
 * 	- encoding (String): represents a string value used to specify the character
 * encoding for sending emails.
 * 	- mode (String): represents how to handle the email template file: it can be
 * opened in read-only mode (`readOnly`) or overwritten (`writeOnly`).
 * 	- cache (boolean): in the EmailTemplateProperties class represents a boolean value
 * indicating whether to cache the email template contents.
 */
@Data
@Component
@ConfigurationProperties(prefix = "email.template")
public class EmailTemplateProperties {
  private String path;
  private String format;
  private String encoding;
  private String mode;
  private boolean cache;
}
