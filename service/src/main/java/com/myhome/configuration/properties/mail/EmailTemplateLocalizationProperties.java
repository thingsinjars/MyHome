package com.myhome.configuration.properties.mail;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Defines a set of configuration properties for email template localization with the
 * prefix "email.location".
 *
 * - path (String): is an instance variable of type String in the EmailTemplateLocalizationProperties
 * class.
 *
 * - encoding (String): represents a string value specifying the encoding format used
 * for email templates.
 *
 * - cacheSeconds (int): stores an integer value.
 */
@Data
@Component
@ConfigurationProperties(prefix = "email.location")
public class EmailTemplateLocalizationProperties {
  private String path;
  private String encoding;
  private int cacheSeconds;
}
