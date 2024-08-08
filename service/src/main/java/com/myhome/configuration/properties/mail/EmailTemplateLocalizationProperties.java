package com.myhome.configuration.properties.mail;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Represents configuration properties for email template localization with
 * annotation-driven configuration and data binding capabilities.
 *
 * - path (String): represents a string value.
 *
 * - encoding (String): represents the encoding type used for files in the path.
 *
 * - cacheSeconds (int): represents an integer value for cache seconds in email
 * template localization settings.
 */
@Data
@Component
@ConfigurationProperties(prefix = "email.location")
public class EmailTemplateLocalizationProperties {
  private String path;
  private String encoding;
  private int cacheSeconds;
}
