package com.myhome.configuration.properties.mail;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Represents a set of configurable email template properties.
 *
 * - path (String): stores a string value representing the path.
 *
 * - format (String): Stores the format.
 *
 * - encoding (String): represents a character set used for email templates.
 *
 * - mode (String): represents a string indicating the mode of operation.
 *
 * - cache (boolean): represents a boolean flag indicating whether caching is enabled.
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
