{"name":"TestUtils.java","path":"service/src/test/java/helpers/TestUtils.java","content":{"structured":{"description":"","items":[{"id":"fa3d4868-c97f-4fce-97cd-c07a46c05e74","ancestors":[],"type":"function","name":"getImageAsByteArray","location":{"offset":" ","indent":4,"insert":37,"start":37},"returns":"byte[]","params":[{"name":"height","type":"int"},{"name":"width","type":"int"}],"code":"public static byte[] getImageAsByteArray(int height, int width) throws IOException {\n      BufferedImage documentImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);\n      try (ByteArrayOutputStream imageBytesStream = new ByteArrayOutputStream()) {\n        ImageIO.write(documentImage, \"jpg\", imageBytesStream);\n        return imageBytesStream.toByteArray();\n      }\n    }","skip":false,"length":7,"comment":{"description":"creates a new `BufferedImage` object with specified dimensions, then writes it to a byte array using `ImageIO`. The resulting byte array contains the image data in JPEG format.","params":[{"name":"height","type":"int","description":"height of the image in pixels that will be generated when calling the `getImageAsByteArray()` method."},{"name":"width","type":"int","description":"width of the image that is to be converted into a byte array."}],"returns":{"type":"byte[]","description":"a byte array containing the image data in JPEG format."}}},{"id":"75c01990-77a6-402d-b6d6-54a819038b32","ancestors":[],"type":"function","name":"generateUniqueId","location":{"offset":" ","indent":4,"insert":45,"start":45},"returns":"String","params":[],"code":"public static String generateUniqueId() {\n      return UUID.randomUUID().toString();\n    }","skip":false,"length":3,"comment":{"description":"generates a unique, randomly-generated string of characters using the `UUID.randomUUID()` method.","params":[],"returns":{"type":"String","description":"a unique string of characters generated using the `UUID.randomUUID()` method."}}},{"id":"21ee133f-c3fa-43a1-99fe-34347c4e6542","ancestors":[],"type":"function","name":"getTestHouses","location":{"offset":" ","indent":4,"insert":52,"start":52},"returns":"Set<CommunityHouse>","params":[{"name":"count","type":"int"}],"code":"public static Set<CommunityHouse> getTestHouses(int count) {\n      return Stream\n          .generate(() -> new CommunityHouse()\n              .withHouseId(generateUniqueId())\n              .withName(\"default-house-name\")\n          )\n          .limit(count)\n          .collect(Collectors.toSet());\n    }","skip":false,"length":9,"comment":{"description":"generates `count` sets of a `CommunityHouse` object, each containing a unique ID and default name.","params":[{"name":"count","type":"int","description":"maximum number of community houses to be generated."}],"returns":{"type":"Set<CommunityHouse>","description":"a `Set` of `CommunityHouse` objects, generated randomly and limited to a specified count."}}},{"id":"f95a670b-0b81-4a24-9f16-ba07fdffea62","ancestors":[],"type":"function","name":"getTestCommunityHouse","location":{"offset":" ","indent":4,"insert":62,"start":62},"returns":"CommunityHouse","params":[],"code":"public static CommunityHouse getTestCommunityHouse() {\n      return new CommunityHouse()\n          .withHouseId(generateUniqueId())\n          .withName(\"default-community-name\");\n    }","skip":false,"length":5,"comment":{"description":"generates a new instance of the `CommunityHouse` class with a unique ID and default name.","params":[],"returns":{"type":"CommunityHouse","description":"a new `CommunityHouse` instance with a unique ID and a default community name."}}},{"id":"7d3349b0-b901-406a-a5d5-d7200e48f041","ancestors":[],"type":"function","name":"getTestCommunityHouse","location":{"offset":" ","indent":4,"insert":68,"start":68},"returns":"CommunityHouse","params":[{"name":"houseId","type":"String"}],"code":"public static CommunityHouse getTestCommunityHouse(String houseId) {\n      return new CommunityHouse()\n          .withHouseId(houseId)\n          .withName(\"default-community-name\");\n    }","skip":false,"length":5,"comment":{"description":"generates a new instance of the `CommunityHouse` class with an provided house ID and a default name.","params":[{"name":"houseId","type":"String","description":"identifier for the community house to be created, which is used to uniquely identify the community house within the system."}],"returns":{"type":"CommunityHouse","description":"a new instance of the `CommunityHouse` class with the specified house ID and default community name."}}},{"id":"50aeebef-968c-4c7f-99e8-cc7e9d517a90","ancestors":[],"type":"function","name":"getTestHouseMembers","location":{"offset":" ","indent":4,"insert":77,"start":77},"returns":"Set<HouseMember>","params":[{"name":"count","type":"int"}],"code":"public static Set<HouseMember> getTestHouseMembers(int count) {\n      return Stream\n          .generate(() -> new HouseMember()\n              .withMemberId(generateUniqueId())\n              .withName(\"default-house-member-name\")\n          )\n          .limit(count)\n          .collect(Collectors.toSet());\n    }","skip":false,"length":9,"comment":{"description":"generates a set of `HouseMember` objects using a `Stream` API and returns it with a specified count.","params":[{"name":"count","type":"int","description":"number of `HouseMember` objects to generate and return from the function."}],"returns":{"type":"Set<HouseMember>","description":"a set of `HouseMember` objects generated dynamically with unique IDs and default names."}}},{"id":"e4c811d6-c154-4b30-9335-6ae4bfa5115b","ancestors":[],"type":"function","name":"getTestHouseMember","location":{"offset":" ","indent":4,"insert":86,"start":86},"returns":"HouseMember","params":[],"code":"public static HouseMember getTestHouseMember() {\n      return new HouseMember()\n              .withMemberId(generateUniqueId())\n              .withName(\"default-house-member-name\");\n    }","skip":false,"length":5,"comment":{"description":"generates a new instance of the `HouseMember` class with a unique ID and a predetermined name for testing purposes.","params":[],"returns":{"type":"HouseMember","description":"a new `HouseMember` object with a randomly generated ID and a default name."}}},{"id":"63ba7e06-a7aa-4820-a351-73207e0652af","ancestors":[],"type":"function","name":"getTestCommunities","location":{"offset":" ","indent":4,"insert":95,"start":95},"returns":"Set<Community>","params":[{"name":"count","type":"int"}],"code":"public static Set<Community> getTestCommunities(int count) {\n      return Stream.iterate(0, n -> n + 1)\n          .map(index -> getTestCommunity(\n              generateUniqueId(),\n              \"default-community-name\" + index,\n              \"default-community-district\" + index,\n              0, 0)\n          )\n          .limit(count)\n          .collect(Collectors.toSet());\n    }","skip":false,"length":11,"comment":{"description":"iteratively generates `count` sets of a `Community` object, each containing unique ID, name, and district values.","params":[{"name":"count","type":"int","description":"maximum number of community objects to return in the generated set."}],"returns":{"type":"Set<Community>","description":"a set of `Community` objects generated randomly based on a specified count."}}},{"id":"ac02cb79-b108-45c5-99ce-7625472fcc08","ancestors":[],"type":"function","name":"getTestCommunity","location":{"offset":" ","indent":4,"insert":107,"start":107},"returns":"Community","params":[],"code":"public static Community getTestCommunity() {\n      return getTestCommunity(\n          generateUniqueId(),\n          \"default-community-name\",\n          \"default-community-district\",\n          0, 0);\n    }","skip":false,"length":7,"comment":{"description":"generates a new community instance with a unique ID, name, and district.","params":[],"returns":{"type":"Community","description":"a `Community` object representing a fictional community with a unique ID, name, and district."}}},{"id":"7d81c998-28e8-4beb-82f4-a27e2d06a286","ancestors":[],"type":"function","name":"getTestCommunity","location":{"offset":" ","indent":4,"insert":115,"start":115},"returns":"Community","params":[{"name":"admin","type":"User"}],"code":"public static Community getTestCommunity(User admin) {\n      Community testCommunity = getTestCommunity();\n      admin.getCommunities().add(testCommunity);\n      testCommunity.setAdmins(Collections.singleton(admin));\n      return testCommunity;\n    }","skip":false,"length":6,"comment":{"description":"retrieves and returns a pre-defined community instance for testing purposes, adding it to the user's community list and setting the user as its admin.","params":[{"name":"admin","type":"User","description":"User who will have ownership and administration rights over the returned `Community`."}],"returns":{"type":"Community","description":"a new `Community` object representing a test community with the specified user as an administrator."}}},{"id":"67d4b025-5834-4a95-a4cb-d8fae9db85e5","ancestors":[],"type":"function","name":"getTestCommunity","location":{"offset":" ","indent":4,"insert":122,"start":122},"returns":"Community","params":[{"name":"communityId","type":"String"},{"name":"communityName","type":"String"},{"name":"communityDistrict","type":"String"},{"name":"adminsCount","type":"int"},{"name":"housesCount","type":"int"}],"code":"public static Community getTestCommunity(String communityId, String communityName, String communityDistrict, int adminsCount, int housesCount) {\n      Community testCommunity = new Community(\n          new HashSet<>(),\n          new HashSet<>(),\n          communityName,\n          communityId,\n          communityDistrict,\n          new HashSet<>()\n      );\n      Set<CommunityHouse> communityHouses = getTestHouses(housesCount);\n      communityHouses.forEach(house -> house.setCommunity(testCommunity));\n      Set<User> communityAdmins = getTestUsers(adminsCount);\n      communityAdmins.forEach(user -> user.getCommunities().add(testCommunity));\n\n      testCommunity.setHouses(communityHouses);\n      testCommunity.setAdmins(communityAdmins);\n      return testCommunity;\n    }","skip":false,"length":18,"comment":{"description":"creates a new community object and sets its name, ID, district, admin count, and house count. It then populates the community with houses and admins generated randomly, and returns the complete community object.","params":[{"name":"communityId","type":"String","description":"unique identifier of the community being created, which is used to assign the community its own distinct identity and distinguish it from other communities in the system."},{"name":"communityName","type":"String","description":"name of the community being created or retrieved, and is used to set the name of the new Community object created by the function."},{"name":"communityDistrict","type":"String","description":"district of the community being generated, which is used to create a unique identifier for the community."},{"name":"adminsCount","type":"int","description":"number of users to be added as administrators to the generated community."},{"name":"housesCount","type":"int","description":"number of houses to generate for the test community."}],"returns":{"type":"Community","description":"a fully formed `Community` object with houses and admins added."}}},{"id":"d4000c4b-4942-44b5-a6fd-b01aa4fcd25a","ancestors":[],"type":"function","name":"getTestAmenity","location":{"offset":" ","indent":4,"insert":144,"start":144},"returns":"Amenity","params":[{"name":"amenityId","type":"String"},{"name":"amenityDescription","type":"String"}],"code":"public static Amenity getTestAmenity(String amenityId, String amenityDescription) {\n      return new Amenity()\n          .withAmenityId(amenityId)\n          .withDescription(amenityDescription)\n          .withCommunity(CommunityHelpers.getTestCommunity());\n    }","skip":false,"length":6,"comment":{"description":"creates a new `Amenity` object with provided `amenityId` and `amenityDescription`, and also sets the `Community` of the amenity to a test community object.","params":[{"name":"amenityId","type":"String","description":"identifier of the amenity to be created or retrieved, which is used to uniquely identify the amenity within the system."},{"name":"amenityDescription","type":"String","description":"description of the amenity that is being created, and it is used to set the value of the `description` field of the resulting `Amenity` object."}],"returns":{"type":"Amenity","description":"a new `Amenity` object with specified `amenityId`, `amenityDescription`, and `community`."}}},{"id":"1815831f-5b6f-4ad7-a761-90f9b27b582c","ancestors":[],"type":"function","name":"getTestAmenities","location":{"offset":" ","indent":4,"insert":151,"start":151},"returns":"Set<Amenity>","params":[{"name":"count","type":"int"}],"code":"public static Set<Amenity> getTestAmenities(int count) {\n      return Stream\n          .generate(() -> new Amenity()\n              .withAmenityId(generateUniqueId())\n              .withName(\"default-amenity-name\")\n              .withDescription(\"default-amenity-description\")\n          )\n          .limit(count)\n          .collect(Collectors.toSet());\n    }","skip":false,"length":10,"comment":{"description":"generates a set of `Amenity` objects with unique IDs, names, and descriptions using a stream of randomly generated amenities. It limits the number of created amenities to the input count.","params":[{"name":"count","type":"int","description":"number of amenities to be generated and returned by the `getTestAmenities` function."}],"returns":{"type":"Set<Amenity>","description":"a set of `Amenity` objects generated randomly with predefined properties."}}},{"id":"bc770796-7172-49b0-a1b5-7d887939790c","ancestors":[],"type":"function","name":"getTestUsers","location":{"offset":" ","indent":4,"insert":166,"start":166},"returns":"Set<User>","params":[{"name":"count","type":"int"}],"code":"public static Set<User> getTestUsers(int count) {\n      return Stream.iterate(0, n -> n + 1)\n          .map(index -> new User(\n              \"default-user-name\" + index,\n              generateUniqueId(),\n              \"default-user-email\" + index,\n              false,\n              \"default-user-password\" + index,\n              new HashSet<>(),\n              new HashSet<>())\n          )\n          .limit(count)\n          .collect(Collectors.toSet());\n    }","skip":false,"length":14,"comment":{"description":"iterates over a sequence of integers, creating and returning a set of `User` objects with randomly generated names, emails, and passwords, up to a specified count.","params":[{"name":"count","type":"int","description":"number of users to be generated by the function."}],"returns":{"type":"Set<User>","description":"a set of `User` objects, each with unique ID and email address, generated within a limited count."}}},{"id":"7233b7de-ae3a-413d-982a-476a5e081e23","ancestors":[],"type":"function","name":"getTestMailProperties","location":{"offset":" ","indent":4,"insert":184,"start":184},"returns":"MailProperties","params":[],"code":"public static MailProperties getTestMailProperties() {\n      MailProperties testMailProperties = new MailProperties();\n      testMailProperties.setHost(\"test host\");\n      testMailProperties.setUsername(\"test username\");\n      testMailProperties.setPassword(\"test password\");\n      testMailProperties.setPort(0);\n      testMailProperties.setProtocol(\"test protocol\");\n      testMailProperties.setDebug(false);\n      testMailProperties.setDevMode(false);\n      return testMailProperties;\n    }","skip":false,"length":11,"comment":{"description":"generates a mock MailProperties object with predefined values for various configuration options, allowing for unit testing of mail-related code without relying on external dependencies.","params":[],"returns":{"type":"MailProperties","description":"a MailProperties object with pre-defined properties for testing purposes."}}},{"id":"35a2ca38-b2ff-463e-abd2-2e83e5bfd1e9","ancestors":[],"type":"function","name":"getTestMailTemplateProperties","location":{"offset":" ","indent":4,"insert":196,"start":196},"returns":"EmailTemplateProperties","params":[],"code":"public static EmailTemplateProperties getTestMailTemplateProperties() {\n      EmailTemplateProperties testMailTemplate = new EmailTemplateProperties();\n      testMailTemplate.setPath(\"test path\");\n      testMailTemplate.setEncoding(\"test encoding\");\n      testMailTemplate.setMode(\"test mode\");\n      testMailTemplate.setCache(false);\n      return testMailTemplate;\n    }","skip":false,"length":8,"comment":{"description":"creates a new `EmailTemplateProperties` instance with custom properties for testing purposes, setting the path, encoding, mode, and cache to specific values.","params":[],"returns":{"type":"EmailTemplateProperties","description":"an `EmailTemplateProperties` object with pre-defined properties for testing purposes."}}},{"id":"3dd43108-7d00-4efa-b79e-7568202e5ae3","ancestors":[],"type":"function","name":"getTestLocalizationMailProperties","location":{"offset":" ","indent":4,"insert":205,"start":205},"returns":"EmailTemplateLocalizationProperties","params":[],"code":"public static EmailTemplateLocalizationProperties getTestLocalizationMailProperties() {\n      EmailTemplateLocalizationProperties testTemplatesLocalization = new EmailTemplateLocalizationProperties();\n      testTemplatesLocalization.setPath(\"test path\");\n      testTemplatesLocalization.setEncoding(\"test encodig\");\n      testTemplatesLocalization.setCacheSeconds(0);\n      return testTemplatesLocalization;\n    }","skip":false,"length":7,"comment":{"description":"generates high-quality documentation for code by creating and returning an instance of `EmailTemplateLocalizationProperties` with predefined properties related to email template localization, including path, encoding, and cache seconds.","params":[],"returns":{"type":"EmailTemplateLocalizationProperties","description":"a `EmailTemplateLocalizationProperties` object containing properties for testing email localization."}}},{"id":"8366b2d6-81f9-4ec7-92a0-c6965dd04d96","ancestors":[],"type":"function","name":"getTestPaymentDto","location":{"offset":" ","indent":4,"insert":216,"start":216},"returns":"PaymentDto","params":[{"name":"charge","type":"BigDecimal"},{"name":"type","type":"String"},{"name":"description","type":"String"},{"name":"recurring","type":"boolean"},{"name":"dueDate","type":"LocalDate"},{"name":"admin","type":"UserDto"},{"name":"member","type":"HouseMemberDto"}],"code":"public static PaymentDto getTestPaymentDto(BigDecimal charge, String type, String description, boolean recurring, LocalDate dueDate, UserDto admin, HouseMemberDto member) {\n\n      return PaymentDto.builder()\n          .charge(charge)\n          .type(type)\n          .description(description)\n          .recurring(recurring)\n          .dueDate(dueDate.toString())\n          .admin(admin)\n          .member(member)\n          .build();\n    }","skip":false,"length":12,"comment":{"description":"builds a `PaymentDto` object with provided parameters, including charge amount, payment type, description, recurrence status, due date, and user and member details.","params":[{"name":"charge","type":"BigDecimal","description":"amount to be charged for the payment."},{"name":"type","type":"String","description":"payment type, which determines the specific payment being processed."},{"name":"description","type":"String","description":"description of the payment being created in the builder object, which is used to construct the final PaymentDto object."},{"name":"recurring","type":"boolean","description":"whether the payment is recurring or not."},{"name":"dueDate","type":"LocalDate","description":"LocalDate when the payment is due, which is used to build the PaymentDto object."},{"name":"admin","type":"UserDto","description":"user who made the payment, and it is passed to the `PaymentDto.builder()` method as an object of type `UserDto`."},{"name":"member","type":"HouseMemberDto","description":"HouseMemberDto object containing information about the member whose payment is being processed."}],"returns":{"type":"PaymentDto","description":"a PaymentDto object built with the given parameters."}}},{"id":"20da1881-07ca-4253-9245-c20fc3d99672","ancestors":[],"type":"function","name":"getTestPaymentNullFields","location":{"offset":" ","indent":4,"insert":228,"start":228},"returns":"Payment","params":[],"code":"public static Payment getTestPaymentNullFields() {\n      //Only 'recurring' field will be not null, but false\n      return new Payment(\n          null,\n          null,\n          null,\n          null,\n          false,\n          null,\n          null,\n          null);\n    }","skip":false,"length":12,"comment":{"description":"generates a Payment object with all fields set to null except for the 'recurring' field which is false.","params":[],"returns":{"type":"Payment","description":"a `Payment` object with all fields null or false except for the `recurring` field."}}}]}}}