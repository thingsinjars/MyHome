package com.myhome.controllers;

import com.myhome.services.BookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

public class BookingControllerTest {

  private final String TEST_AMENITY_ID = "test-amenity-id";
  private static final String TEST_BOOKING_ID = "test-booking-id";

  @Mock
  private BookingService bookingSDJpaService;

  @InjectMocks
  private BookingController bookingController;

  /**
   * initializes Mockito annotations for the class, enabling mocking of dependencies
   * and methods.
   */
  @BeforeEach
  private void init() {
    MockitoAnnotations.initMocks(this);
  }

  /**
   * tests the deleteBooking method of the BookingController class by providing a given
   * booking ID and expecting the method to delete it successfully from the database
   * and return a ResponseEntity with a status code of NO_CONTENT.
   */
  @Test
  void deleteBooking() {
    // given
    given(bookingSDJpaService.deleteBooking(TEST_AMENITY_ID, TEST_BOOKING_ID))
        .willReturn(true);

    // when
    ResponseEntity<Void> response =
        bookingController.deleteBooking(TEST_AMENITY_ID, TEST_BOOKING_ID);

    // then
    assertNull(response.getBody());
    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    verify(bookingSDJpaService).deleteBooking(TEST_AMENITY_ID, TEST_BOOKING_ID);
  }

  /**
   * tests whether the `bookingController.deleteBooking()` method throws a `NotFoundException`
   * when the booking does not exist in the database.
   */
  @Test
  void deleteBookingNotExists() {
    // given
    given(bookingSDJpaService.deleteBooking(TEST_AMENITY_ID, TEST_BOOKING_ID))
        .willReturn(false);

    // when
    ResponseEntity<Void> response =
        bookingController.deleteBooking(TEST_AMENITY_ID, TEST_BOOKING_ID);

    // then
    assertNull(response.getBody());
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    verify(bookingSDJpaService).deleteBooking(TEST_AMENITY_ID, TEST_BOOKING_ID);
  }
}
























