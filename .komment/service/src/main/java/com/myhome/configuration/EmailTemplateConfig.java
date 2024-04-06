{"name":"EmailTemplateConfig.java","path":"service/src/main/java/com/myhome/configuration/EmailTemplateConfig.java","content":{"structured":{"description":"An Email Template Configuration class that sets up email template configuration for a Spring Boot application. It provides a way to locate and render email templates using Thymeleaf. The class creates a ResourceBundleMessageSource for email messages, which is used to provide message resources to the Thymeleaf template engine. The template engine is then configured with the email message source and a custom template resolver that uses the Thymeleaf ClassLoaderTemplateResolver to locate and render email templates.","items":[{"id":"ff91c632-1c3f-8fad-a844-952afc467aa9","ancestors":[],"type":"function","description":"TODO","name":"EmailTemplateConfig","code":"@Configuration\n@RequiredArgsConstructor\npublic class EmailTemplateConfig {\n\n  private final EmailTemplateProperties templateProperties;\n  private final EmailTemplateLocalizationProperties localizationProperties;\n\n  @Bean\n  public ResourceBundleMessageSource emailMessageSource() {\n    ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();\n    messageSource.setBasename(localizationProperties.getPath());\n    messageSource.setDefaultLocale(Locale.ENGLISH);\n    messageSource.setDefaultEncoding(localizationProperties.getEncoding());\n    messageSource.setCacheSeconds(localizationProperties.getCacheSeconds());\n    return messageSource;\n  }\n\n  @Bean\n  public SpringTemplateEngine thymeleafTemplateEngine(ResourceBundleMessageSource emailMessageSource) {\n    SpringTemplateEngine templateEngine = new SpringTemplateEngine();\n    templateEngine.setTemplateResolver(thymeleafTemplateResolver());\n    templateEngine.setTemplateEngineMessageSource(emailMessageSource);\n    return templateEngine;\n  }\n\n  private ITemplateResolver thymeleafTemplateResolver() {\n    ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();\n\n    String templatePath = templateProperties.getPath();\n    String fileSeparator = System.getProperty(\"file.separator\");\n    templateResolver.setPrefix(templatePath.endsWith(fileSeparator) ? templatePath : templatePath + fileSeparator);\n\n    templateResolver.setSuffix(templateProperties.getFormat());\n    templateResolver.setTemplateMode(templateProperties.getMode());\n    templateResolver.setCharacterEncoding(templateProperties.getEncoding());\n    templateResolver.setCacheable(templateProperties.isCache());\n    return templateResolver;\n  }\n\n}","location":{"start":15,"insert":15,"offset":" ","indent":0},"item_type":"class","length":40},{"id":"75742de9-7f80-cbb0-9841-12bafaf2b720","ancestors":["ff91c632-1c3f-8fad-a844-952afc467aa9"],"type":"function","description":"creates a `ResourceBundleMessageSource` instance that retrieves email messages from a resource bundle file. The file path, default locale, and encoding are set using properties from an external `localizationProperties` object. The cache seconds can also be set using the same object.","params":[],"returns":{"type_name":"instance","description":"a ResourceBundleMessageSource instance set with configuration properties for email localization.\n\n* `ResourceBundleMessageSource messageSource`: The output is a `ResourceBundleMessageSource`, which means it provides access to message keys in a resource bundle.\n* `setBasename(localizationProperties.getPath())`: The basename of the resource bundle is set to the value of `localizationProperties.getPath()`.\n* `setDefaultLocale(Locale.ENGLISH)`: The default locale for the resource bundle is set to English.\n* `setDefaultEncoding(localizationProperties.getEncoding())`: The default encoding for the resource bundle is set to the value of `localizationProperties.getEncoding()`.\n* `setCacheSeconds(localizationProperties.getCacheSeconds())`: The number of seconds that the resource bundle will be cached is set to the value of `localizationProperties.getCacheSeconds()`.","complex_type":true},"usage":{"language":"java","code":"@Bean\n  public ResourceBundleMessageSource emailMessageSource() {\n    ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();\n    messageSource.setBasename(localizationProperties.getPath());\n    messageSource.setDefaultLocale(Locale.ENGLISH);\n    messageSource.setDefaultEncoding(localizationProperties.getEncoding());\n    messageSource.setCacheSeconds(localizationProperties.getCacheSeconds());\n    return messageSource;\n  }\n","description":""},"name":"emailMessageSource","code":"@Bean\n  public ResourceBundleMessageSource emailMessageSource() {\n    ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();\n    messageSource.setBasename(localizationProperties.getPath());\n    messageSource.setDefaultLocale(Locale.ENGLISH);\n    messageSource.setDefaultEncoding(localizationProperties.getEncoding());\n    messageSource.setCacheSeconds(localizationProperties.getCacheSeconds());\n    return messageSource;\n  }","location":{"start":22,"insert":22,"offset":" ","indent":2},"item_type":"method","length":9},{"id":"8518a61c-def5-9e9a-4243-76d64504d213","ancestors":["ff91c632-1c3f-8fad-a844-952afc467aa9"],"type":"function","description":"creates a new instance of the `SpringTemplateEngine` class and sets its template resolver and message source to the specified values, returning the instance.","params":[{"name":"emailMessageSource","type_name":"ResourceBundleMessageSource","description":"message source for email-related messages in the Spring Template Engine.\n\n1. ResourceBundleMessageSource: This is an interface that provides a way to retrieve message keys in a resource bundle format. It has methods for getting message keys and their corresponding values.","complex_type":true}],"returns":{"type_name":"SpringTemplateEngine","description":"a Spring Template Engine instance configured to use Thymeleaf templates and email message source.\n\n* The `SpringTemplateEngine` instance is created with various configuration options set, including the `templateResolver` and `templateEngineMessageSource`.\n* The `templateResolver` sets the template engine's resolver to `thymeleafTemplateResolver`, which allows for the use of Thymeleaf templates.\n* The `templateEngineMessageSource` sets the message source for the template engine, in this case an instance of `ResourceBundleMessageSource` for handling email-related messages.\n\nThe other attributes of the returned output, such as its name or any other configuration options set, are not explicitly mentioned in the provided code snippet.","complex_type":true},"usage":{"language":"java","code":"@Bean\n  public SpringTemplateEngine thymeleafTemplateEngine(ResourceBundleMessageSource emailMessageSource) {\n    SpringTemplateEngine templateEngine = new SpringTemplateEngine();\n    templateEngine.setTemplateResolver(thymeleafTemplateResolver());\n    templateEngine.setTemplateEngineMessageSource(emailMessageSource);\n    return templateEngine;\n  }\n","description":""},"name":"thymeleafTemplateEngine","code":"@Bean\n  public SpringTemplateEngine thymeleafTemplateEngine(ResourceBundleMessageSource emailMessageSource) {\n    SpringTemplateEngine templateEngine = new SpringTemplateEngine();\n    templateEngine.setTemplateResolver(thymeleafTemplateResolver());\n    templateEngine.setTemplateEngineMessageSource(emailMessageSource);\n    return templateEngine;\n  }","location":{"start":32,"insert":32,"offset":" ","indent":2},"item_type":"method","length":7},{"id":"dc4bc1e2-b8c1-dfad-a643-4368e382062d","ancestors":["ff91c632-1c3f-8fad-a844-952afc467aa9"],"type":"function","description":"creates a Thymeleaf template resolver that sets the prefix, suffix, template mode, character encoding, and caching based on properties.","params":[],"returns":{"type_name":"ITemplateResolver","description":"a `ITemplateResolver` instance set up to resolve Thymeleaf templates based on properties defined in the `templateProperties` class.\n\n* `templateProperties`: This is an instance of `ITemplateProperties`, which contains information about the Thymeleaf template resolver.\n\t+ `path`: The path to the template file.\n\t+ `format`: The template file format (e.g., \"html\", \"xml\").\n\t+ `mode`: The template rendering mode (e.g., \"HTML\", \"XML\").\n\t+ `encoding`: The character encoding of the template file.\n\t+ `cacheable`: A boolean indicating whether the template is cacheable.\n\nThe properties are set using the `setPrefix`, `setSuffix`, `setTemplateMode`, and `setCharacterEncoding` methods, respectively. Additionally, the `setCacheable` method is used to indicate whether the template is cacheable or not.","complex_type":true},"usage":{"language":"java","code":"private ITemplateResolver thymeleafTemplateResolver() {\n    ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();\n\n    String templatePath = \"templates\";\n    String fileSeparator = System.getProperty(\"file.separator\");\n    templateResolver.setPrefix(templatePath.endsWith(fileSeparator) ? templatePath : templatePath + fileSeparator);\n\n    templateResolver.setSuffix(\".html\");\n    templateResolver.setTemplateMode(TemplateMode.HTML);\n    templateResolver.setCharacterEncoding(\"UTF-8\");\n    templateResolver.setCacheable(true);\n    return templateResolver;\n  }\n","description":""},"name":"thymeleafTemplateResolver","code":"private ITemplateResolver thymeleafTemplateResolver() {\n    ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();\n\n    String templatePath = templateProperties.getPath();\n    String fileSeparator = System.getProperty(\"file.separator\");\n    templateResolver.setPrefix(templatePath.endsWith(fileSeparator) ? templatePath : templatePath + fileSeparator);\n\n    templateResolver.setSuffix(templateProperties.getFormat());\n    templateResolver.setTemplateMode(templateProperties.getMode());\n    templateResolver.setCharacterEncoding(templateProperties.getEncoding());\n    templateResolver.setCacheable(templateProperties.isCache());\n    return templateResolver;\n  }","location":{"start":40,"insert":40,"offset":" ","indent":2},"item_type":"method","length":13}]}}}