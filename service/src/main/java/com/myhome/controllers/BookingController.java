package com.myhome.controllers;

import com.myhome.api.BookingsApi;
import com.myhome.services.BookingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * Handles booking deletion requests, implementing the BookingsApi interface.
 * It uses the BookingService to delete bookings and returns a response based on the
 * deletion status.
 * The controller is annotated with Spring Boot and logging configurations.
 */
@RestController
@Slf4j
@RequiredArgsConstructor
public class BookingController implements BookingsApi {

  private final BookingService bookingSDJpaService;

  /**
   * Deletes a booking based on the provided `amenityId` and `bookingId`,
   * returns a `NO_CONTENT` response if the deletion is successful,
   * returns a `NOT_FOUND` response if the booking does not exist.
   *
   * @param amenityId identifier of the amenity associated with the booking being deleted.
   *
   * @param bookingId identifier of a booking to be deleted from the system.
   *
   * @returns a ResponseEntity with a status of NO_CONTENT if the booking is deleted,
   * or NOT_FOUND otherwise.
   */
  @Override
  public ResponseEntity<Void> deleteBooking(@PathVariable String amenityId,
      @PathVariable String bookingId) {
    boolean isBookingDeleted = bookingSDJpaService.deleteBooking(amenityId, bookingId);
    if (isBookingDeleted) {
      return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    } else {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
  }
}
