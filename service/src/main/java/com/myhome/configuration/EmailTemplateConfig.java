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
 * Is responsible for configuring email templates in an application. It defines two
 * beans: ResourceBundleMessageSource and SpringTemplateEngine. The class also contains
 * a private method that creates an ITemplateResolver for Thymeleaf template engine.
 */
@Configuration
@RequiredArgsConstructor
public class EmailTemplateConfig {

  private final EmailTemplateProperties templateProperties;
  private final EmailTemplateLocalizationProperties localizationProperties;

  /**
   * Sets up a ResourceBundleMessageSource bean, configuring it to retrieve messages
   * from a resource bundle with a specified basename, default locale, and encoding.
   * It also sets a cache timeout for the message source. The function returns the
   * configured message source bean.
   *
   * @returns a ResourceBundleMessageSource bean.
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
   * Sets up a SpringTemplateEngine instance for rendering Thymeleaf templates, specifying
   * a ResourceBundleMessageSource as the engine's message source and a custom Thymeleaf
   * template resolver. The configured engine is then returned to be used in the application.
   *
   * @param emailMessageSource ResourceBundleMessageSource instance, which is set as
   * the message source for the SpringTemplateEngine to resolve messages in Thymeleaf
   * templates.
   *
   * @returns a `SpringTemplateEngine` instance configured for Thymeleaf template rendering.
   */
  @Bean
  public SpringTemplateEngine thymeleafTemplateEngine(ResourceBundleMessageSource emailMessageSource) {
    SpringTemplateEngine templateEngine = new SpringTemplateEngine();
    templateEngine.setTemplateResolver(thymeleafTemplateResolver());
    templateEngine.setTemplateEngineMessageSource(emailMessageSource);
    return templateEngine;
  }

  /**
   * Initializes a `ClassLoaderTemplateResolver` instance to resolve Thymeleaf templates.
   * It sets properties such as prefix, suffix, mode, character encoding, and cacheability
   * based on configuration parameters. The configured resolver is then returned for
   * use in template rendering.
   *
   * @returns an instance of `ClassLoaderTemplateResolver`.
   *
   * Set prefix to the template path with or without file separator depending on its
   * existence. Set suffix to the format specified by templateProperties. Set template
   * mode and character encoding as per the provided properties.
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
