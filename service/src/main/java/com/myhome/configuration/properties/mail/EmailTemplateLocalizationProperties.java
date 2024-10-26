package com.myhome.configuration.properties.mail;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Describes the configuration properties for email template localization at a specific
 * location.
 *
 * - path (String): represents the path to email templates.
 *
 * - encoding (String): represents the character encoding used for email templates.
 *
 * - cacheSeconds (int): represents a cache expiration time in seconds.
 */
@Data
@Component
@ConfigurationProperties(prefix = "email.location")
public class EmailTemplateLocalizationProperties {
  private String path;
  private String encoding;
  private int cacheSeconds;
}
