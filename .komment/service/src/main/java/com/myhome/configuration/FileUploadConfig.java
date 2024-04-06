{"name":"FileUploadConfig.java","path":"service/src/main/java/com/myhome/configuration/FileUploadConfig.java","content":{"structured":{"description":"A configuration class called `FileUploadConfig` that sets up multipart file uploads for a Spring Boot application. The class sets the maximum file size in kilobytes using the `@Value` annotation and creates a `MultipartConfigElement` bean to configure the multipart settings. The `multipartConfigElement()` method returns a `MultipartConfigElement` instance with the maximum file size and request size set.","items":[{"id":"8911784d-1095-ab8b-9841-e9ddacb79b76","ancestors":[],"type":"function","description":"TODO","name":"FileUploadConfig","code":"@Configuration\npublic class FileUploadConfig {\n\n  @Value(\"${files.maxSizeKBytes}\")\n  private int maxSizeKBytes;\n\n  @Bean\n  public MultipartConfigElement multipartConfigElement() {\n    MultipartConfigFactory factory = new MultipartConfigFactory();\n    factory.setMaxFileSize(DataSize.ofKilobytes(maxSizeKBytes));\n    factory.setMaxRequestSize(DataSize.ofKilobytes(maxSizeKBytes));\n    return factory.createMultipartConfig();\n  }\n}","location":{"start":26,"insert":26,"offset":" ","indent":0},"item_type":"class","length":14},{"id":"fe1b4364-9f30-539d-c144-a19d569a2c94","ancestors":["8911784d-1095-ab8b-9841-e9ddacb79b76"],"type":"function","description":"creates a `MultipartConfig` object, setting limits on maximum file size and request size.","params":[],"returns":{"type_name":"MultipartConfig","description":"a `MultipartConfig` object configured with maximum file and request sizes in kilobytes.\n\n* The MultipartConfigFactory object is created with the `setMaxFileSize()` and `setMaxRequestSize()` methods, which set the maximum file size in kilobytes (KB) and request size in KB, respectively.\n* The createMultipartConfig() method returns a newly created MultipartConfig instance.\n* The MultipartConfig instance has several attributes, including the maximum file size, maximum request size, and the number of files that can be uploaded simultaneously.","complex_type":true},"usage":{"language":"java","code":"@Bean\npublic MultipartConfigElement multipartConfigElement() {\n    MultipartConfigFactory factory = new MultipartConfigFactory();\n    factory.setMaxFileSize(DataSize.ofKilobytes(maxSizeKBytes));\n    factory.setMaxRequestSize(DataSize.ofKilobytes(maxSizeKBytes));\n    return factory.createMultipartConfig();\n}\n","description":""},"name":"multipartConfigElement","code":"@Bean\n  public MultipartConfigElement multipartConfigElement() {\n    MultipartConfigFactory factory = new MultipartConfigFactory();\n    factory.setMaxFileSize(DataSize.ofKilobytes(maxSizeKBytes));\n    factory.setMaxRequestSize(DataSize.ofKilobytes(maxSizeKBytes));\n    return factory.createMultipartConfig();\n  }","location":{"start":32,"insert":32,"offset":" ","indent":2},"item_type":"method","length":7}]}}}