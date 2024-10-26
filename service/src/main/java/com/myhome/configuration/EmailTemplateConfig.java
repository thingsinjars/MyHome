package com.myhome.configuration;

import com.myhome.configuration.properties.mail.EmailTemplateLocalizationProperties;
import com.myhome.configuration.properties.mail.EmailTemplateProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

import java.util.Locale;

/**
 * Configures email templates using Thymeleaf and Spring, providing message source
 * and template engine beans.
 */
@Configuration
@RequiredArgsConstructor
public class EmailTemplateConfig {

  private final EmailTemplateProperties templateProperties;
  private final EmailTemplateLocalizationProperties localizationProperties;

  /**
   * Configures a ResourceBundleMessageSource instance to handle email-related messages.
   * It sets the basename, default locale, default encoding, and cache seconds based
   * on properties from the localizationProperties object.
   *
   * @returns a configured `ResourceBundleMessageSource` bean.
   */
  @Bean
  public ResourceBundleMessageSource emailMessageSource() {
    ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
    messageSource.setBasename(localizationProperties.getPath());
    messageSource.setDefaultLocale(Locale.ENGLISH);
    messageSource.setDefaultEncoding(localizationProperties.getEncoding());
    messageSource.setCacheSeconds(localizationProperties.getCacheSeconds());
    return messageSource;
  }

  /**
   * Configures a SpringTemplateEngine with a Thymeleaf template resolver and sets a
   * message source for email templates, enabling the rendering of Thymeleaf templates
   * with localized messages.
   *
   * @param emailMessageSource source of messages used for resolving message keys in
   * the template engine.
   *
   * @returns a configured SpringTemplateEngine instance.
   */
  @Bean
  public SpringTemplateEngine thymeleafTemplateEngine(ResourceBundleMessageSource emailMessageSource) {
    SpringTemplateEngine templateEngine = new SpringTemplateEngine();
    templateEngine.setTemplateResolver(thymeleafTemplateResolver());
    templateEngine.setTemplateEngineMessageSource(emailMessageSource);
    return templateEngine;
  }

  /**
   * Configures a Thymeleaf template resolver with properties from a template properties
   * object. It sets the prefix, suffix, template mode, character encoding, and
   * cacheability based on the provided properties.
   *
   * @returns a configured `ClassLoaderTemplateResolver` instance.
   *
   * Include a prefix for template paths which is a directory path where templates are
   * stored.
   * Specify a suffix for template files, indicating their format (e.g., .html).
   * Configure character encoding and template mode.
   */
  private ITemplateResolver thymeleafTemplateResolver() {
    ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();

    String templatePath = templateProperties.getPath();
    String fileSeparator = System.getProperty("file.separator");
    templateResolver.setPrefix(templatePath.endsWith(fileSeparator) ? templatePath : templatePath + fileSeparator);

    templateResolver.setSuffix(templateProperties.getFormat());
    templateResolver.setTemplateMode(templateProperties.getMode());
    templateResolver.setCharacterEncoding(templateProperties.getEncoding());
    templateResolver.setCacheable(templateProperties.isCache());
    return templateResolver;
  }

}
