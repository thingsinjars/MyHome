package com.myhome.configuration.properties.mail;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Serves as a configuration bean for specifying email template localization properties
 * using Spring Boot's configuration properties feature.
 *
 * - path (String): represents a string value for specifying a path.
 *
 * - encoding (String): represents a string property used to store the encoding format
 * of files.
 *
 * - cacheSeconds (int): represents an integer value representing seconds for caching
 * purposes.
 */
@Data
@Component
@ConfigurationProperties(prefix = "email.location")
public class EmailTemplateLocalizationProperties {
  private String path;
  private String encoding;
  private int cacheSeconds;
}
