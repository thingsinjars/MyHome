package com.myhome.configuration.properties.mail;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Contains properties for specifying location details such as path, encoding, and
 * cache seconds for email template localization.
 * Fields:
 * 	- path (String): in the EmailTemplateLocalizationProperties class represents the
 * file path where email templates are stored.
 * 	- encoding (String): represents a string value specifying the character encoding
 * used for email templates.
 * 	- cacheSeconds (int): represents the number of seconds that an email template's
 * location information should be cached before being refreshed.
 */
@Data
@Component
@ConfigurationProperties(prefix = "email.location")
public class EmailTemplateLocalizationProperties {
  private String path;
  private String encoding;
  private int cacheSeconds;
}
