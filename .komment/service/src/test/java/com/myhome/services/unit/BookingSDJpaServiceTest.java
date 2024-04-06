{"name":"BookingSDJpaServiceTest.java","path":"service/src/test/java/com/myhome/services/unit/BookingSDJpaServiceTest.java","content":{"structured":{"description":"A class called BookingSDJpaServiceTest, which is used to test the BookingSDJpaService class. The tests include deleting a booking item from the database, checking if the deletion was successful, and verifying that the amenity booking item ID has been updated. The code also includes Mockito annotations for mocking dependencies and BDDMockito statements for providing test data.","image":"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\"\n \"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">\n<!-- Generated by graphviz version 2.43.0 (0)\n -->\n<!-- Title: com.myhome.domain.AmenityBookingItem Pages: 1 -->\n<svg width=\"187pt\" height=\"148pt\"\n viewBox=\"0.00 0.00 187.00 148.00\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\">\n<g id=\"graph0\" class=\"graph\" transform=\"scale(1 1) rotate(0) translate(4 144)\">\n<title>com.myhome.domain.AmenityBookingItem</title>\n<!-- Node1 -->\n<g id=\"Node000001\" class=\"node\">\n<title>Node1</title>\n<g id=\"a_Node000001\"><a xlink:title=\" \">\n<polygon fill=\"#999999\" stroke=\"#666666\" points=\"173.5,-30 5.5,-30 5.5,0 173.5,0 173.5,-30\"/>\n<text text-anchor=\"start\" x=\"13.5\" y=\"-18\" font-family=\"Helvetica,sans-Serif\" font-size=\"10.00\">com.myhome.domain.Amenity</text>\n<text text-anchor=\"middle\" x=\"89.5\" y=\"-7\" font-family=\"Helvetica,sans-Serif\" font-size=\"10.00\">BookingItem</text>\n</a>\n</g>\n</g>\n<!-- Node2 -->\n<g id=\"Node000002\" class=\"node\">\n<title>Node2</title>\n<g id=\"a_Node000002\"><a xlink:href=\"classcom_1_1myhome_1_1domain_1_1BaseEntity.html\" target=\"_top\" xlink:title=\" \">\n<polygon fill=\"white\" stroke=\"#666666\" points=\"179,-85 0,-85 0,-66 179,-66 179,-85\"/>\n<text text-anchor=\"middle\" x=\"89.5\" y=\"-73\" font-family=\"Helvetica,sans-Serif\" font-size=\"10.00\">com.myhome.domain.BaseEntity</text>\n</a>\n</g>\n</g>\n<!-- Node2&#45;&gt;Node1 -->\n<g id=\"edge1_Node000001_Node000002\" class=\"edge\">\n<title>Node2&#45;&gt;Node1</title>\n<g id=\"a_edge1_Node000001_Node000002\"><a xlink:title=\" \">\n<path fill=\"none\" stroke=\"#63b8ff\" d=\"M89.5,-55.65C89.5,-47.36 89.5,-37.78 89.5,-30.11\"/>\n<polygon fill=\"#63b8ff\" stroke=\"#63b8ff\" points=\"86,-55.87 89.5,-65.87 93,-55.87 86,-55.87\"/>\n</a>\n</g>\n</g>\n<!-- Node3 -->\n<g id=\"Node000003\" class=\"node\">\n<title>Node3</title>\n<g id=\"a_Node000003\"><a xlink:title=\" \">\n<polygon fill=\"white\" stroke=\"#666666\" points=\"126.5,-140 52.5,-140 52.5,-121 126.5,-121 126.5,-140\"/>\n<text text-anchor=\"middle\" x=\"89.5\" y=\"-128\" font-family=\"Helvetica,sans-Serif\" font-size=\"10.00\">Serializable</text>\n</a>\n</g>\n</g>\n<!-- Node3&#45;&gt;Node2 -->\n<g id=\"edge2_Node000002_Node000003\" class=\"edge\">\n<title>Node3&#45;&gt;Node2</title>\n<g id=\"a_edge2_Node000002_Node000003\"><a xlink:title=\" \">\n<path fill=\"none\" stroke=\"#63b8ff\" d=\"M89.5,-110.66C89.5,-101.93 89.5,-91.99 89.5,-85.09\"/>\n<polygon fill=\"#63b8ff\" stroke=\"#63b8ff\" points=\"86,-110.75 89.5,-120.75 93,-110.75 86,-110.75\"/>\n</a>\n</g>\n</g>\n</g>\n</svg>\n","items":[{"id":"fca5dce0-270e-34ab-ff45-09b3be923b31","ancestors":[],"type":"function","description":"TODO","name":"BookingSDJpaServiceTest","code":"public class BookingSDJpaServiceTest {\n\n  private static final String TEST_BOOKING_ID = \"test-booking-id\";\n  private static final String TEST_AMENITY_ID = \"test-amenity-id\";\n  private static final String TEST_AMENITY_ID_2 = \"test-amenity-id-2\";\n  private final String TEST_AMENITY_DESCRIPTION = \"test-amenity-description\";\n\n  @Mock\n  private AmenityBookingItemRepository bookingItemRepository;\n\n  @InjectMocks\n  private BookingSDJpaService bookingSDJpaService;\n\n  @BeforeEach\n  private void init() {\n    MockitoAnnotations.initMocks(this);\n  }\n\n  @Test\n  void deleteBookingItem() {\n    // given\n    AmenityBookingItem testBookingItem = getTestBookingItem();\n\n    given(bookingItemRepository.findByAmenityBookingItemId(TEST_BOOKING_ID))\n        .willReturn(Optional.of(testBookingItem));\n    testBookingItem.setAmenity(TestUtils.AmenityHelpers\n        .getTestAmenity(TEST_AMENITY_ID, TEST_AMENITY_DESCRIPTION));\n\n    // when\n    boolean bookingDeleted = bookingSDJpaService.deleteBooking(TEST_AMENITY_ID, TEST_BOOKING_ID);\n\n    // then\n    assertTrue(bookingDeleted);\n    verify(bookingItemRepository).findByAmenityBookingItemId(TEST_BOOKING_ID);\n    verify(bookingItemRepository).delete(testBookingItem);\n  }\n\n  @Test\n  void deleteBookingNotExists() {\n    // given\n    given(bookingItemRepository.findByAmenityBookingItemId(TEST_BOOKING_ID))\n        .willReturn(Optional.empty());\n\n    // when\n    boolean bookingDeleted = bookingSDJpaService.deleteBooking(TEST_AMENITY_ID, TEST_BOOKING_ID);\n\n    // then\n    assertFalse(bookingDeleted);\n    verify(bookingItemRepository).findByAmenityBookingItemId(TEST_BOOKING_ID);\n    verify(bookingItemRepository, never()).delete(any());\n  }\n\n  @Test\n  void deleteBookingAmenityNotExists() {\n    // given\n    AmenityBookingItem testBookingItem = getTestBookingItem();\n\n    given(bookingItemRepository.findByAmenityBookingItemId(TEST_BOOKING_ID))\n        .willReturn(Optional.of(testBookingItem));\n    testBookingItem.setAmenity(TestUtils.AmenityHelpers\n        .getTestAmenity(TEST_AMENITY_ID_2, TEST_AMENITY_DESCRIPTION));\n    // when\n    boolean bookingDeleted = bookingSDJpaService.deleteBooking(TEST_AMENITY_ID, TEST_BOOKING_ID);\n\n    // then\n    assertFalse(bookingDeleted);\n    assertNotEquals(TEST_AMENITY_ID, testBookingItem.getAmenity().getAmenityId());\n    verify(bookingItemRepository).findByAmenityBookingItemId(TEST_BOOKING_ID);\n    verify(bookingItemRepository, never()).delete(any());\n  }\n\n  private AmenityBookingItem getTestBookingItem() {\n    return new AmenityBookingItem()\n        .withAmenityBookingItemId(TEST_BOOKING_ID);\n  }\n}","location":{"start":22,"insert":22,"offset":" ","indent":0},"item_type":"class","length":76},{"id":"6e1a90e2-1ddf-16af-3845-a6ed9b55e9fd","ancestors":["fca5dce0-270e-34ab-ff45-09b3be923b31"],"type":"function","description":"initialize Mockito Annotations for the current class, enabling mocking of classes and methods.","params":[],"usage":{"language":"java","code":"@BeforeEach\n  private void init() {\n    MockitoAnnotations.initMocks(this);\n  }\n","description":"\nIn this method, the @BeforeEach annotation indicates that it is a setup method for unit tests. The MockitoAnnotations class is imported to enable the use of mock annotations in the test.\nThe init() method is then called at the beginning of each test case using JUnit's BeforeEach annotation. This method initializes the mock objects by calling the initMocks method of the MockitoAnnotations class."},"name":"init","code":"@BeforeEach\n  private void init() {\n    MockitoAnnotations.initMocks(this);\n  }","location":{"start":35,"insert":35,"offset":" ","indent":2},"item_type":"method","length":4},{"id":"b71afd40-54ef-3299-eb4a-e74aaa762964","ancestors":["fca5dce0-270e-34ab-ff45-09b3be923b31"],"type":"function","description":"deletes a booking item from the repository, given the amenity ID and booking ID. It utilizes mocking to verify the correct calls to the `bookingItemRepository`.","params":[],"usage":{"language":"java","code":"@Test\n  void deleteBookingItem() {\n    // given\n    AmenityBookingItem testBookingItem = getTestBookingItem();\n\n    given(bookingItemRepository.findByAmenityBookingItemId(TEST_BOOKING_ID))\n        .willReturn(Optional.of(testBookingItem));\n    testBookingItem.setAmenity(TestUtils.AmenityHelpers\n        .getTestAmenity(TEST_AMENITY_ID, TEST_AMENITY_DESCRIPTION));\n\n    // when\n    boolean bookingDeleted = bookingSDJpaService.deleteBooking(TEST_AMENITY_ID, TEST_BOOKING_ID);\n\n    // then\n    assertTrue(bookingDeleted);\n    verify(bookingItemRepository).findByAmenityBookingItemId(TEST_BOOKING_ID);\n    verify(bookingItemRepository).delete(testBookingItem);\n  }\n","description":""},"name":"deleteBookingItem","code":"@Test\n  void deleteBookingItem() {\n    // given\n    AmenityBookingItem testBookingItem = getTestBookingItem();\n\n    given(bookingItemRepository.findByAmenityBookingItemId(TEST_BOOKING_ID))\n        .willReturn(Optional.of(testBookingItem));\n    testBookingItem.setAmenity(TestUtils.AmenityHelpers\n        .getTestAmenity(TEST_AMENITY_ID, TEST_AMENITY_DESCRIPTION));\n\n    // when\n    boolean bookingDeleted = bookingSDJpaService.deleteBooking(TEST_AMENITY_ID, TEST_BOOKING_ID);\n\n    // then\n    assertTrue(bookingDeleted);\n    verify(bookingItemRepository).findByAmenityBookingItemId(TEST_BOOKING_ID);\n    verify(bookingItemRepository).delete(testBookingItem);\n  }","location":{"start":40,"insert":40,"offset":" ","indent":2},"item_type":"method","length":18},{"id":"8a77c81e-0763-2892-a149-d4ef6afdf854","ancestors":["fca5dce0-270e-34ab-ff45-09b3be923b31"],"type":"function","description":"verifies that a booking with the given amenity ID and booking ID does not exist before deleting it, using the `bookingSDJpaService` to delete the booking and verify the `bookingItemRepository`.","params":[],"usage":{"language":"java","code":"@Test\nvoid deleteBookingNotExists() {\n  // given\n  given(bookingItemRepository.findByAmenityBookingItemId(TEST_BOOKING_ID))\n      .willReturn(Optional.empty());\n  \n  // when\n  boolean bookingDeleted = bookingSDJpaService.deleteBooking(TEST_AMENITY_ID, TEST_BOOKING_ID);\n  \n  // then\n  assertFalse(bookingDeleted);\n  verify(bookingItemRepository).findByAmenityBookingItemId(TEST_BOOKING_ID);\n  verify(bookingItemRepository, never()).delete(any());\n}\n","description":"\nThis test is checking the case where the booking with the specified id does not exist in the repository. The given method will return an empty Optional, which means that no booking was found with the provided id. The test then asserts that the deleteBooking method returned false, indicating that the deletion was unsuccessful. Additionally, it verifies that the findByAmenityBookingItemId method of the repository was called with the correct id, and that the delete method of the repository was not called."},"name":"deleteBookingNotExists","code":"@Test\n  void deleteBookingNotExists() {\n    // given\n    given(bookingItemRepository.findByAmenityBookingItemId(TEST_BOOKING_ID))\n        .willReturn(Optional.empty());\n\n    // when\n    boolean bookingDeleted = bookingSDJpaService.deleteBooking(TEST_AMENITY_ID, TEST_BOOKING_ID);\n\n    // then\n    assertFalse(bookingDeleted);\n    verify(bookingItemRepository).findByAmenityBookingItemId(TEST_BOOKING_ID);\n    verify(bookingItemRepository, never()).delete(any());\n  }","location":{"start":59,"insert":59,"offset":" ","indent":2},"item_type":"method","length":14},{"id":"d893c342-a33d-818e-e24c-d710839fe283","ancestors":["fca5dce0-270e-34ab-ff45-09b3be923b31"],"type":"function","description":"tests the deletion of a booking amenity that does not exist in the database. It verifies that the method returns `false`, updates the amenity ID of the booking item, and calls the `verify` methods to confirm the expected actions on the repository.","params":[],"usage":{"language":"java","code":"@Test\n  void deleteBookingItem() {\n    // given\n    AmenityBookingItem testBookingItem = getTestBookingItem();\n\n    given(bookingItemRepository.findByAmenityBookingItemId(TEST_BOOKING_ID))\n        .willReturn(Optional.of(testBookingItem));\n    testBookingItem.setAmenity(TestUtils.AmenityHelpers\n        .getTestAmenity(TEST_AMENITY_ID, TEST_AMENITY_DESCRIPTION));\n\n    // when\n    boolean bookingDeleted = bookingSDJpaService.deleteBooking(TEST_AMENITY_ID, TEST_BOOKING_ID);\n\n    // then\n    assertTrue(bookingDeleted);\n    verify(bookingItemRepository).findByAmenityBookingItemId(TEST_BOOKING_ID);\n    verify(bookingItemRepository).delete(testBookingItem);\n  }\n","description":""},"name":"deleteBookingAmenityNotExists","code":"@Test\n  void deleteBookingAmenityNotExists() {\n    // given\n    AmenityBookingItem testBookingItem = getTestBookingItem();\n\n    given(bookingItemRepository.findByAmenityBookingItemId(TEST_BOOKING_ID))\n        .willReturn(Optional.of(testBookingItem));\n    testBookingItem.setAmenity(TestUtils.AmenityHelpers\n        .getTestAmenity(TEST_AMENITY_ID_2, TEST_AMENITY_DESCRIPTION));\n    // when\n    boolean bookingDeleted = bookingSDJpaService.deleteBooking(TEST_AMENITY_ID, TEST_BOOKING_ID);\n\n    // then\n    assertFalse(bookingDeleted);\n    assertNotEquals(TEST_AMENITY_ID, testBookingItem.getAmenity().getAmenityId());\n    verify(bookingItemRepository).findByAmenityBookingItemId(TEST_BOOKING_ID);\n    verify(bookingItemRepository, never()).delete(any());\n  }","location":{"start":74,"insert":74,"offset":" ","indent":2},"item_type":"method","length":18},{"id":"e6c65e48-a140-2496-dc43-bf939ecff4b9","ancestors":["fca5dce0-270e-34ab-ff45-09b3be923b31"],"type":"function","description":"creates a new instance of `AmenityBookingItem` with a predefined ID for testing purposes.","params":[],"returns":{"type_name":"AmenityBookingItem","description":"a new instance of `AmenityBookingItem` with a pre-defined ID.\n\n* `AmenityBookingItemId`: This is an identifier for the booking item, set to a specific value (`TEST_BOOKING_ID`).\n* The `AmenityBookingItem` class itself, which represents a booking item with its own properties and methods.","complex_type":true},"usage":{"language":"java","code":"AmenityBookingItem testBookingItem = BookingSDJpaServiceTest.getTestBookingItem();\n","description":""},"name":"getTestBookingItem","code":"private AmenityBookingItem getTestBookingItem() {\n    return new AmenityBookingItem()\n        .withAmenityBookingItemId(TEST_BOOKING_ID);\n  }","location":{"start":93,"insert":93,"offset":" ","indent":2},"item_type":"method","length":4}]}}}