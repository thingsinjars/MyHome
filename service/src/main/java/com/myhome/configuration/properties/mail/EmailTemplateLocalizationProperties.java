package com.myhome.configuration.properties.mail;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Provides configuration properties for email templates localization.
 *
 * - path (String): represents a string value.
 *
 * - encoding (String): represents a string value specifying the file encoding for
 * email templates.
 *
 * - cacheSeconds (int): represents an integer value for setting the cache duration
 * in seconds.
 */
@Data
@Component
@ConfigurationProperties(prefix = "email.location")
public class EmailTemplateLocalizationProperties {
  private String path;
  private String encoding;
  private int cacheSeconds;
}
