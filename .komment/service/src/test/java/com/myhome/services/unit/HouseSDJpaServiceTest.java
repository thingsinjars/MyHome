{"name":"HouseSDJpaServiceTest.java","path":"service/src/test/java/com/myhome/services/unit/HouseSDJpaServiceTest.java","content":{"structured":{"description":"","items":[{"id":"294ec489-e42d-40ea-91ac-6d7a2502d7b5","ancestors":[],"type":"function","name":"setUp","location":{"offset":" ","indent":2,"insert":66,"start":66},"returns":false,"params":[],"code":"@BeforeEach\n  void setUp() {\n    MockitoAnnotations.initMocks(this);\n  }","skip":false,"length":4,"comment":{"description":"initializes and mocks various components for unit testing using the `MockitoAnnotations`.","params":[],"returns":null}},{"id":"ef3c456b-614e-41e1-9919-aed01e1d5d95","ancestors":[],"type":"function","name":"listAllHousesDefault","location":{"offset":" ","indent":2,"insert":71,"start":71},"returns":false,"params":[],"code":"@Test\n  void listAllHousesDefault() {\n    // given\n    Set<CommunityHouse> housesInDatabase = TestUtils.CommunityHouseHelpers.getTestHouses(TEST_HOUSES_COUNT);\n    \n    given(communityHouseRepository.findAll())\n        .willReturn(housesInDatabase);\n\n    // when\n    Set<CommunityHouse> resultHouses = houseSDJpaService.listAllHouses();\n\n    // then\n    assertEquals(housesInDatabase, resultHouses);\n    verify(communityHouseRepository).findAll();\n  }","skip":false,"length":15,"comment":{"description":"retrieves a set of community houses from the database using the `communityHouseRepository.findAll()` method and passes it to the `houseSDJpaService` for listing, resulting in the same set of houses being returned.","params":[],"returns":null}},{"id":"bc5ad28e-47b3-41d8-90f4-5290ca5ca01f","ancestors":[],"type":"function","name":"listAllHousesCustomPageable","location":{"offset":" ","indent":2,"insert":87,"start":87},"returns":false,"params":[],"code":"@Test\n  void listAllHousesCustomPageable() {\n    // given\n    Set<CommunityHouse> housesInDatabase = TestUtils.CommunityHouseHelpers.getTestHouses(TEST_HOUSES_COUNT);\n    Pageable pageRequest = PageRequest.of(0, TEST_HOUSES_COUNT);\n    Page<CommunityHouse> housesPage = new PageImpl<>(\n        new ArrayList<>(housesInDatabase),\n        pageRequest,\n        TEST_HOUSES_COUNT\n    );\n    given(communityHouseRepository.findAll(pageRequest))\n        .willReturn(housesPage);\n\n    // when\n    Set<CommunityHouse> resultHouses = houseSDJpaService.listAllHouses(pageRequest);\n\n    // then\n    assertEquals(housesInDatabase, resultHouses);\n    verify(communityHouseRepository).findAll(pageRequest);\n  }","skip":false,"length":20,"comment":{"description":"retrieves a page of houses from the database using a custom page request, and then asserts that the result set is equal to the expected set of houses in the database. It also verifies that the community house repository was called with the correct page request.","params":[],"returns":null}},{"id":"41fe3655-078a-42db-878d-6604e99b8d60","ancestors":[],"type":"function","name":"addHouseMembers","location":{"offset":" ","indent":2,"insert":108,"start":108},"returns":false,"params":[],"code":"@Test\n  void addHouseMembers() {\n    // given\n    Set<HouseMember> membersToAdd = TestUtils.HouseMemberHelpers.getTestHouseMembers(TEST_HOUSE_MEMBERS_COUNT);\n    int membersToAddSize = membersToAdd.size();\n    CommunityHouse communityHouse = TestUtils.CommunityHouseHelpers.getTestCommunityHouse();\n\n    given(communityHouseRepository.findByHouseIdWithHouseMembers(HOUSE_ID))\n        .willReturn(Optional.of(communityHouse));\n    given(houseMemberRepository.saveAll(membersToAdd))\n        .willReturn(membersToAdd);\n\n    // when\n    Set<HouseMember> resultMembers = houseSDJpaService.addHouseMembers(HOUSE_ID, membersToAdd);\n\n    // then\n    assertEquals(membersToAddSize, resultMembers.size());\n    assertEquals(membersToAddSize, communityHouse.getHouseMembers().size());\n    verify(communityHouseRepository).save(communityHouse);\n    verify(houseMemberRepository).saveAll(membersToAdd);\n    verify(communityHouseRepository).findByHouseIdWithHouseMembers(HOUSE_ID);\n  }","skip":false,"length":22,"comment":{"description":"adds a set of house members to a community house. It utilizes the repository interfaces to save the members and retrieve the community house, ensuring the correctness of the member associations.","params":[],"returns":null}},{"id":"3e907f58-33a5-422d-b883-02a623785d22","ancestors":[],"type":"function","name":"addHouseMembersHouseNotExists","location":{"offset":" ","indent":2,"insert":131,"start":131},"returns":false,"params":[],"code":"@Test\n  void addHouseMembersHouseNotExists() {\n    // given\n    Set<HouseMember> membersToAdd = TestUtils.HouseMemberHelpers.getTestHouseMembers(TEST_HOUSE_MEMBERS_COUNT);\n\n    given(communityHouseRepository.findByHouseIdWithHouseMembers(HOUSE_ID))\n        .willReturn(Optional.empty());\n\n    // when\n    Set<HouseMember> resultMembers = houseSDJpaService.addHouseMembers(HOUSE_ID, membersToAdd);\n\n    // then\n    assertTrue(resultMembers.isEmpty());\n    verify(communityHouseRepository).findByHouseIdWithHouseMembers(HOUSE_ID);\n    verify(communityHouseRepository, never()).save(any());\n    verifyNoInteractions(houseMemberRepository);\n  }","skip":false,"length":17,"comment":{"description":"adds a set of house members to a non-existent house. It uses the `communityHouseRepository` to retrieve the list of house members associated with the given house ID, and then adds the provided members to the house using the `houseSDJpaService`. The resulting member set is then checked to ensure it is empty, and the function verifies the expected interactions with the `communityHouseRepository` and `houseMemberRepository`.","params":[],"returns":null}},{"id":"e8d31235-f1dc-48fd-8e1a-74234bf38444","ancestors":[],"type":"function","name":"deleteMemberFromHouse","location":{"offset":" ","indent":2,"insert":149,"start":149},"returns":false,"params":[],"code":"@Test\n  void deleteMemberFromHouse() {\n    // given\n    Set<HouseMember> houseMembers = TestUtils.HouseMemberHelpers.getTestHouseMembers(TEST_HOUSE_MEMBERS_COUNT);\n    CommunityHouse communityHouse = TestUtils.CommunityHouseHelpers.getTestCommunityHouse();\n\n    HouseMember memberToDelete = new HouseMember().withMemberId(MEMBER_ID);\n    memberToDelete.setCommunityHouse(communityHouse);\n\n    houseMembers.add(memberToDelete);\n    communityHouse.setHouseMembers(houseMembers);\n\n    given(communityHouseRepository.findByHouseIdWithHouseMembers(HOUSE_ID))\n        .willReturn(Optional.of(communityHouse));\n\n    // when\n    boolean isMemberDeleted = houseSDJpaService.deleteMemberFromHouse(HOUSE_ID, MEMBER_ID);\n\n    // then\n    assertTrue(isMemberDeleted);\n    assertNull(memberToDelete.getCommunityHouse());\n    assertFalse(communityHouse.getHouseMembers().contains(memberToDelete));\n    verify(communityHouseRepository).findByHouseIdWithHouseMembers(HOUSE_ID);\n    verify(communityHouseRepository).save(communityHouse);\n    verify(houseMemberRepository).save(memberToDelete);\n  }","skip":false,"length":26,"comment":{"description":"deletes a member from a community house. It takes the house ID and member ID as inputs, retrieves the relevant data from the database, deletes the member from the house, and saves the changes to the database.","params":[],"returns":null}},{"id":"6752f688-3365-4fd5-a424-77c4e5be34a7","ancestors":[],"type":"function","name":"deleteMemberFromHouseNotExists","location":{"offset":" ","indent":2,"insert":176,"start":176},"returns":false,"params":[],"code":"@Test\n  void deleteMemberFromHouseNotExists() {\n    // given\n    given(communityHouseRepository.findByHouseIdWithHouseMembers(HOUSE_ID))\n        .willReturn(Optional.empty());\n\n    // when\n    boolean isMemberDeleted = houseSDJpaService.deleteMemberFromHouse(HOUSE_ID, MEMBER_ID);\n\n    // then\n    assertFalse(isMemberDeleted);\n    verify(communityHouseRepository).findByHouseIdWithHouseMembers(HOUSE_ID);\n    verify(communityHouseRepository, never()).save(any());\n    verifyNoInteractions(houseMemberRepository);\n  }","skip":false,"length":15,"comment":{"description":"tests whether a member can be deleted from a house that does not exist.","params":[],"returns":null}},{"id":"65dd964c-ad93-4ffc-841a-87b112337395","ancestors":[],"type":"function","name":"deleteMemberFromHouseMemberNotPresent","location":{"offset":" ","indent":2,"insert":192,"start":192},"returns":false,"params":[],"code":"@Test\n  void deleteMemberFromHouseMemberNotPresent() {\n    // given\n    Set<HouseMember> houseMembers = TestUtils.HouseMemberHelpers.getTestHouseMembers(TEST_HOUSE_MEMBERS_COUNT);\n    CommunityHouse communityHouse = TestUtils.CommunityHouseHelpers.getTestCommunityHouse();\n\n    communityHouse.setHouseMembers(houseMembers);\n\n    given(communityHouseRepository.findByHouseIdWithHouseMembers(HOUSE_ID))\n        .willReturn(Optional.of(communityHouse));\n\n    // when\n    boolean isMemberDeleted = houseSDJpaService.deleteMemberFromHouse(HOUSE_ID, MEMBER_ID);\n\n    // then\n    assertFalse(isMemberDeleted);\n    verify(communityHouseRepository).findByHouseIdWithHouseMembers(HOUSE_ID);\n    verify(communityHouseRepository, never()).save(communityHouse);\n    verifyNoInteractions(houseMemberRepository);\n  }","skip":false,"length":20,"comment":{"description":"tests whether a member can be deleted from a community house if the member is not present in the database.","params":[],"returns":null}}]}}}