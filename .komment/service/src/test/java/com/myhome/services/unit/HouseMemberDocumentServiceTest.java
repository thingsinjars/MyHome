{"name":"HouseMemberDocumentServiceTest.java","path":"service/src/test/java/com/myhome/services/unit/HouseMemberDocumentServiceTest.java","content":{"structured":{"description":"","image":"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\"\n \"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">\n<!-- Generated by graphviz version 2.43.0 (0)\n -->\n<!-- Title: com.myhome.domain.HouseMemberDocument Pages: 1 -->\n<svg width=\"206pt\" height=\"148pt\"\n viewBox=\"0.00 0.00 206.00 148.00\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\">\n<g id=\"graph0\" class=\"graph\" transform=\"scale(1 1) rotate(0) translate(4 144)\">\n<title>com.myhome.domain.HouseMemberDocument</title>\n<!-- Node1 -->\n<g id=\"Node000001\" class=\"node\">\n<title>Node1</title>\n<g id=\"a_Node000001\"><a xlink:title=\" \">\n<polygon fill=\"#999999\" stroke=\"#666666\" points=\"198,-30 0,-30 0,0 198,0 198,-30\"/>\n<text text-anchor=\"start\" x=\"8\" y=\"-18\" font-family=\"Helvetica,sans-Serif\" font-size=\"10.00\">com.myhome.domain.HouseMember</text>\n<text text-anchor=\"middle\" x=\"99\" y=\"-7\" font-family=\"Helvetica,sans-Serif\" font-size=\"10.00\">Document</text>\n</a>\n</g>\n</g>\n<!-- Node2 -->\n<g id=\"Node000002\" class=\"node\">\n<title>Node2</title>\n<g id=\"a_Node000002\"><a xlink:href=\"classcom_1_1myhome_1_1domain_1_1BaseEntity.html\" target=\"_top\" xlink:title=\" \">\n<polygon fill=\"white\" stroke=\"#666666\" points=\"188.5,-85 9.5,-85 9.5,-66 188.5,-66 188.5,-85\"/>\n<text text-anchor=\"middle\" x=\"99\" y=\"-73\" font-family=\"Helvetica,sans-Serif\" font-size=\"10.00\">com.myhome.domain.BaseEntity</text>\n</a>\n</g>\n</g>\n<!-- Node2&#45;&gt;Node1 -->\n<g id=\"edge1_Node000001_Node000002\" class=\"edge\">\n<title>Node2&#45;&gt;Node1</title>\n<g id=\"a_edge1_Node000001_Node000002\"><a xlink:title=\" \">\n<path fill=\"none\" stroke=\"#63b8ff\" d=\"M99,-55.65C99,-47.36 99,-37.78 99,-30.11\"/>\n<polygon fill=\"#63b8ff\" stroke=\"#63b8ff\" points=\"95.5,-55.87 99,-65.87 102.5,-55.87 95.5,-55.87\"/>\n</a>\n</g>\n</g>\n<!-- Node3 -->\n<g id=\"Node000003\" class=\"node\">\n<title>Node3</title>\n<g id=\"a_Node000003\"><a xlink:title=\" \">\n<polygon fill=\"white\" stroke=\"#666666\" points=\"136,-140 62,-140 62,-121 136,-121 136,-140\"/>\n<text text-anchor=\"middle\" x=\"99\" y=\"-128\" font-family=\"Helvetica,sans-Serif\" font-size=\"10.00\">Serializable</text>\n</a>\n</g>\n</g>\n<!-- Node3&#45;&gt;Node2 -->\n<g id=\"edge2_Node000002_Node000003\" class=\"edge\">\n<title>Node3&#45;&gt;Node2</title>\n<g id=\"a_edge2_Node000002_Node000003\"><a xlink:title=\" \">\n<path fill=\"none\" stroke=\"#63b8ff\" d=\"M99,-110.66C99,-101.93 99,-91.99 99,-85.09\"/>\n<polygon fill=\"#63b8ff\" stroke=\"#63b8ff\" points=\"95.5,-110.75 99,-120.75 102.5,-110.75 95.5,-110.75\"/>\n</a>\n</g>\n</g>\n</g>\n</svg>\n","items":[{"id":"7e352e76-1b2c-4f8c-a18a-5218319b82ed","ancestors":[],"type":"function","name":"init","location":{"offset":" ","indent":2,"insert":66,"start":66},"returns":false,"params":[],"code":"@BeforeEach\n  private void init() {\n    MockitoAnnotations.initMocks(this);\n    ReflectionTestUtils.setField(houseMemberDocumentService, \"compressionBorderSizeKBytes\",\n        COMPRESSION_BORDER_SIZE_KB);\n    ReflectionTestUtils.setField(houseMemberDocumentService, \"maxFileSizeKBytes\", MAX_FILE_SIZE_KB);\n    ReflectionTestUtils.setField(houseMemberDocumentService, \"compressedImageQuality\",\n        COMPRESSED_IMAGE_QUALITY);\n  }","skip":false,"length":9,"comment":{"description":"sets up mock objects and sets field values for a class called `HouseMemberDocumentService`.","params":[],"returns":null}},{"id":"ba2bdff3-74f1-4dc1-869a-4cd3ab9ebcb8","ancestors":[],"type":"function","name":"findMemberDocumentSuccess","location":{"offset":" ","indent":2,"insert":76,"start":76},"returns":false,"params":[],"code":"@Test\n  void findMemberDocumentSuccess() {\n    // given\n    HouseMember testMember = new HouseMember(MEMBER_ID, MEMBER_DOCUMENT, MEMBER_NAME, null);\n    given(houseMemberRepository.findByMemberId(MEMBER_ID))\n        .willReturn(Optional.of(testMember));\n    // when\n    Optional<HouseMemberDocument> houseMemberDocument =\n        houseMemberDocumentService.findHouseMemberDocument(MEMBER_ID);\n\n    // then\n    assertTrue(houseMemberDocument.isPresent());\n    assertEquals(MEMBER_DOCUMENT, houseMemberDocument.get());\n    verify(houseMemberRepository).findByMemberId(MEMBER_ID);\n  }","skip":false,"length":15,"comment":{"description":"verifies that a HouseMemberDocument can be found for a given member ID using the HouseMemberRepository and HouseMemberDocumentService.","params":[],"returns":null}},{"id":"c79ddec3-e07e-4a08-bda3-929820082ed6","ancestors":[],"type":"function","name":"findMemberDocumentNoDocumentPresent","location":{"offset":" ","indent":2,"insert":92,"start":92},"returns":false,"params":[],"code":"@Test\n  void findMemberDocumentNoDocumentPresent() {\n    // given\n    HouseMember testMember = new HouseMember(MEMBER_ID, null, MEMBER_NAME, null);\n    given(houseMemberRepository.findByMemberId(MEMBER_ID))\n        .willReturn(Optional.of(testMember));\n    // when\n    Optional<HouseMemberDocument> houseMemberDocument =\n        houseMemberDocumentService.findHouseMemberDocument(MEMBER_ID);\n\n    // then\n    assertFalse(houseMemberDocument.isPresent());\n    verify(houseMemberRepository).findByMemberId(MEMBER_ID);\n  }","skip":false,"length":14,"comment":{"description":"verifies that no House Member Document is present for a given member ID through various interactions with the repository and document service.","params":[],"returns":null}},{"id":"1573814c-0487-4826-bf26-ee63e92c83bb","ancestors":[],"type":"function","name":"findMemberDocumentMemberNotExists","location":{"offset":" ","indent":2,"insert":107,"start":107},"returns":false,"params":[],"code":"@Test\n  void findMemberDocumentMemberNotExists() {\n    // given\n    given(houseMemberRepository.findByMemberId(MEMBER_ID))\n        .willReturn(Optional.empty());\n    // when\n    Optional<HouseMemberDocument> houseMemberDocument =\n        houseMemberDocumentService.findHouseMemberDocument(MEMBER_ID);\n\n    // then\n    assertFalse(houseMemberDocument.isPresent());\n    verify(houseMemberRepository).findByMemberId(MEMBER_ID);\n  }","skip":false,"length":13,"comment":{"description":"verifies that a House Member Document does not exist for a given member ID by querying the repository and checking the presence of the document in the service.","params":[],"returns":null}},{"id":"cd9292e0-0257-457b-b9c0-718ac01ca18a","ancestors":[],"type":"function","name":"deleteMemberDocumentSuccess","location":{"offset":" ","indent":2,"insert":121,"start":121},"returns":false,"params":[],"code":"@Test\n  void deleteMemberDocumentSuccess() {\n    // given\n    HouseMember testMember = new HouseMember(MEMBER_ID, MEMBER_DOCUMENT, MEMBER_NAME, null);\n    given(houseMemberRepository.findByMemberId(MEMBER_ID))\n        .willReturn(Optional.of(testMember));\n    // when\n    boolean isDocumentDeleted = houseMemberDocumentService.deleteHouseMemberDocument(MEMBER_ID);\n\n    // then\n    assertTrue(isDocumentDeleted);\n    assertNull(testMember.getHouseMemberDocument());\n    verify(houseMemberRepository).findByMemberId(MEMBER_ID);\n    verify(houseMemberRepository).save(testMember);\n  }","skip":false,"length":15,"comment":{"description":"deletes a house member's document by calling the `houseMemberDocumentService.deleteHouseMemberDocument()` method and verifying that the document is deleted and the member's document is null.","params":[],"returns":null}},{"id":"da0de810-2ef6-4193-b366-71c7aa0cb62b","ancestors":[],"type":"function","name":"deleteMemberDocumentNoDocumentPresent","location":{"offset":" ","indent":2,"insert":137,"start":137},"returns":false,"params":[],"code":"@Test\n  void deleteMemberDocumentNoDocumentPresent() {\n    // given\n    HouseMember testMember = new HouseMember(MEMBER_ID, null, MEMBER_NAME, null);\n    given(houseMemberRepository.findByMemberId(MEMBER_ID))\n        .willReturn(Optional.of(testMember));\n    // when\n    boolean isDocumentDeleted = houseMemberDocumentService.deleteHouseMemberDocument(MEMBER_ID);\n\n    // then\n    assertFalse(isDocumentDeleted);\n    assertNull(testMember.getHouseMemberDocument());\n    verify(houseMemberRepository).findByMemberId(MEMBER_ID);\n    verify(houseMemberRepository, never()).save(testMember);\n  }","skip":false,"length":15,"comment":{"description":"verifies that a house member's document is not present when deleting it.","params":[],"returns":null}},{"id":"0c5b9404-42dd-4c4c-992c-f669b8e2a9bc","ancestors":[],"type":"function","name":"deleteMemberDocumentMemberNotExists","location":{"offset":" ","indent":2,"insert":153,"start":153},"returns":false,"params":[],"code":"@Test\n  void deleteMemberDocumentMemberNotExists() {\n    // given\n    given(houseMemberRepository.findByMemberId(MEMBER_ID))\n        .willReturn(Optional.empty());\n    // when\n    boolean isDocumentDeleted = houseMemberDocumentService.deleteHouseMemberDocument(MEMBER_ID);\n\n    // then\n    assertFalse(isDocumentDeleted);\n    verify(houseMemberRepository).findByMemberId(MEMBER_ID);\n    verify(houseMemberRepository, never()).save(any());\n  }","skip":false,"length":13,"comment":{"description":"verifies that a house member document is deleted when the member does not exist in the repository.","params":[],"returns":null}},{"id":"640daf10-7132-40f1-a987-b48b2fe37102","ancestors":[],"type":"function","name":"updateHouseMemberDocumentSuccess","location":{"offset":" ","indent":2,"insert":167,"start":167},"returns":false,"params":[],"code":"@Test\n  void updateHouseMemberDocumentSuccess() throws IOException {\n    // given\n    byte[] imageBytes = TestUtils.General.getImageAsByteArray(10, 10);\n    MockMultipartFile newDocumentFile = new MockMultipartFile(\"new-test-file-name\", imageBytes);\n    HouseMemberDocument savedDocument =\n        new HouseMemberDocument(String.format(\"member_%s_document.jpg\", MEMBER_ID), imageBytes);\n    HouseMember testMember = new HouseMember(MEMBER_ID, MEMBER_DOCUMENT, MEMBER_NAME, null);\n\n    given(houseMemberRepository.findByMemberId(MEMBER_ID))\n        .willReturn(Optional.of(testMember));\n    given(houseMemberDocumentRepository.save(savedDocument))\n        .willReturn(savedDocument);\n    // when\n    Optional<HouseMemberDocument> houseMemberDocument =\n        houseMemberDocumentService.updateHouseMemberDocument(newDocumentFile, MEMBER_ID);\n\n    // then\n    assertTrue(houseMemberDocument.isPresent());\n    assertEquals(testMember.getHouseMemberDocument(), houseMemberDocument.get());\n    verify(houseMemberRepository).findByMemberId(MEMBER_ID);\n    verify(houseMemberDocumentRepository).save(savedDocument);\n    verify(houseMemberRepository).save(testMember);\n  }","skip":false,"length":24,"comment":{"description":"updates a member's document in the database. It retrieves the existing member document, saves it with updated content, and returns the updated document object.","params":[],"returns":null}},{"id":"ab19498a-0448-4590-8982-8245336a009f","ancestors":[],"type":"function","name":"updateHouseMemberDocumentMemberNotExists","location":{"offset":" ","indent":2,"insert":192,"start":192},"returns":false,"params":[],"code":"@Test\n  void updateHouseMemberDocumentMemberNotExists() throws IOException {\n    // given\n    byte[] imageBytes = TestUtils.General.getImageAsByteArray(10, 10);\n    MockMultipartFile newDocumentFile = new MockMultipartFile(\"new-test-file-name\", imageBytes);\n\n    given(houseMemberRepository.findByMemberId(MEMBER_ID))\n        .willReturn(Optional.empty());\n\n    // when\n    Optional<HouseMemberDocument> houseMemberDocument =\n        houseMemberDocumentService.updateHouseMemberDocument(newDocumentFile, MEMBER_ID);\n\n    // then\n    assertFalse(houseMemberDocument.isPresent());\n    verify(houseMemberRepository).findByMemberId(MEMBER_ID);\n    verify(houseMemberDocumentRepository, never()).save(any());\n    verify(houseMemberRepository, never()).save(any());\n  }","skip":false,"length":19,"comment":{"description":"updates a house member document with an image file for a member who does not exist in the repository.","params":[],"returns":null}},{"id":"01cbf13a-a60d-43dc-a656-41908e71a6cf","ancestors":[],"type":"function","name":"updateHouseMemberDocumentTooLargeFile","location":{"offset":" ","indent":2,"insert":212,"start":212},"returns":false,"params":[],"code":"@Test\n  void updateHouseMemberDocumentTooLargeFile() throws IOException {\n    // given\n    byte[] imageBytes = TestUtils.General.getImageAsByteArray(1000, 1000);\n    MockMultipartFile tooLargeDocumentFile =\n        new MockMultipartFile(\"new-test-file-name\", imageBytes);\n    HouseMemberDocument savedDocument =\n        new HouseMemberDocument(String.format(\"member_%s_document.jpg\", MEMBER_ID), imageBytes);\n    HouseMember testMember = new HouseMember(MEMBER_ID, MEMBER_DOCUMENT, MEMBER_NAME, null);\n\n    given(houseMemberRepository.findByMemberId(MEMBER_ID))\n        .willReturn(Optional.of(testMember));\n    given(houseMemberDocumentRepository.save(savedDocument))\n        .willReturn(savedDocument);\n    // when\n    Optional<HouseMemberDocument> houseMemberDocument =\n        houseMemberDocumentService.updateHouseMemberDocument(tooLargeDocumentFile, MEMBER_ID);\n\n    // then\n    assertFalse(houseMemberDocument.isPresent());\n    assertEquals(testMember.getHouseMemberDocument(), MEMBER_DOCUMENT);\n    verify(houseMemberRepository).findByMemberId(MEMBER_ID);\n    verify(houseMemberDocumentRepository, never()).save(any());\n    verify(houseMemberRepository, never()).save(any());\n  }","skip":false,"length":25,"comment":{"description":"updates an existing house member document with a file that is too large, retrieving the member document from the repository and saving it with the updated file.","params":[],"returns":null}},{"id":"9f20d757-9a62-4ece-92a4-42f3d7e50a2c","ancestors":[],"type":"function","name":"createHouseMemberDocumentSuccess","location":{"offset":" ","indent":2,"insert":238,"start":238},"returns":false,"params":[],"code":"@Test\n  void createHouseMemberDocumentSuccess() throws IOException {\n    // given\n    byte[] imageBytes = TestUtils.General.getImageAsByteArray(10, 10);\n    HouseMemberDocument savedDocument =\n        new HouseMemberDocument(String.format(\"member_%s_document.jpg\", MEMBER_ID), imageBytes);\n    MockMultipartFile newDocumentFile = new MockMultipartFile(\"new-test-file-name\", imageBytes);\n    HouseMember testMember = new HouseMember(MEMBER_ID, MEMBER_DOCUMENT, MEMBER_NAME, null);\n\n    given(houseMemberRepository.findByMemberId(MEMBER_ID))\n        .willReturn(Optional.of(testMember));\n    given(houseMemberDocumentRepository.save(savedDocument))\n        .willReturn(savedDocument);\n    // when\n    Optional<HouseMemberDocument> houseMemberDocument =\n        houseMemberDocumentService.createHouseMemberDocument(newDocumentFile, MEMBER_ID);\n\n    // then\n    assertTrue(houseMemberDocument.isPresent());\n    assertNotEquals(testMember.getHouseMemberDocument().getDocumentFilename(),\n        MEMBER_DOCUMENT.getDocumentFilename());\n    verify(houseMemberRepository).findByMemberId(MEMBER_ID);\n    verify(houseMemberDocumentRepository).save(savedDocument);\n    verify(houseMemberRepository).save(testMember);\n  }","skip":false,"length":25,"comment":{"description":"tests the createHouseMemberDocument service, given a new document file and member ID, it creates a new house member document in the database, updates the member's document filename and saves the document to the repository.","params":[],"returns":null}},{"id":"3517529c-73c7-4906-8874-0832e497a292","ancestors":[],"type":"function","name":"createHouseMemberDocumentMemberNotExists","location":{"offset":" ","indent":2,"insert":264,"start":264},"returns":false,"params":[],"code":"@Test\n  void createHouseMemberDocumentMemberNotExists() throws IOException {\n    // given\n    byte[] imageBytes = TestUtils.General.getImageAsByteArray(10, 10);\n    MockMultipartFile newDocumentFile = new MockMultipartFile(\"new-test-file-name\", imageBytes);\n\n    given(houseMemberRepository.findByMemberId(MEMBER_ID))\n        .willReturn(Optional.empty());\n    // when\n    Optional<HouseMemberDocument> houseMemberDocument =\n        houseMemberDocumentService.createHouseMemberDocument(newDocumentFile, MEMBER_ID);\n\n    // then\n    assertFalse(houseMemberDocument.isPresent());\n    verify(houseMemberRepository).findByMemberId(MEMBER_ID);\n    verify(houseMemberDocumentRepository, never()).save(any());\n    verify(houseMemberRepository, never()).save(any());\n  }","skip":false,"length":18,"comment":{"description":"creates a new House Member Document for a member who does not exist in the database. It then verifies that the document is not present in the database and that no save calls have been made to the repository.","params":[],"returns":null}},{"id":"7d5672ef-8c86-401b-9a52-cfc9b0e81e48","ancestors":[],"type":"function","name":"createHouseMemberDocumentTooLargeFile","location":{"offset":" ","indent":2,"insert":283,"start":283},"returns":false,"params":[],"code":"@Test\n  void createHouseMemberDocumentTooLargeFile() throws IOException {\n    // given\n    byte[] imageBytes = TestUtils.General.getImageAsByteArray(1000, 1000);\n    MockMultipartFile tooLargeDocumentFile =\n        new MockMultipartFile(\"new-test-file-name\", imageBytes);\n    HouseMember testMember = new HouseMember(MEMBER_ID, MEMBER_DOCUMENT, MEMBER_NAME, null);\n\n    given(houseMemberRepository.findByMemberId(MEMBER_ID))\n        .willReturn(Optional.of(testMember));\n    // when\n    Optional<HouseMemberDocument> houseMemberDocument =\n        houseMemberDocumentService.createHouseMemberDocument(tooLargeDocumentFile, MEMBER_ID);\n\n    // then\n    assertFalse(houseMemberDocument.isPresent());\n    assertEquals(testMember.getHouseMemberDocument(), MEMBER_DOCUMENT);\n    verify(houseMemberRepository).findByMemberId(MEMBER_ID);\n    verify(houseMemberDocumentRepository, never()).save(any());\n    verify(houseMemberRepository, never()).save(any());\n  }","skip":false,"length":21,"comment":{"description":"tests the creation of a House Member Document with an image that is too large to be saved. It verifies that the method returns `Optional.empty()` when the image is too large and the existing document is not updated.","params":[],"returns":null}}]}}}