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
 * TODO
 */
@RestController
@Slf4j
@RequiredArgsConstructor
public class BookingController implements BookingsApi {

  private final BookingService bookingSDJpaService;

  /**
   * deletes a booking based on its amenity ID and ID, returning a response entity
   * indicating whether the operation was successful or not.
   * 
   * @param amenityId ID of an amenity associated with the booking to be deleted.
   * 
   * 	- `String amenityId`: The unique identifier for an amenity.
   * 	- `@PathVariable String bookingId`: The unique identifier for a booking that is
   * being deleted.
   * 
   * @param bookingId ID of the booking that needs to be deleted.
   * 
   * 	- `amenityId`: The ID of the amenity for which the booking is being deleted.
   * 	- `bookingId`: The ID of the booking that needs to be deleted.
   * 
   * @returns a `ResponseEntity` object with a status code indicating whether the booking
   * was successfully deleted.
   * 
   * 	- `ResponseEntity`: This is the class that represents the HTTP response entity,
   * which contains information about the status code and body of the response.
   * 	- `status`: This is a field of type `HttpStatus` that indicates the HTTP status
   * code of the response. In this case, it can be either `NO_CONTENT` or `NOT_FOUND`.
   * 	- `build`: This is a method that constructs the response entity based on the
   * properties of the function's output.
   * 
   * Therefore, the output of the `deleteBooking` function can be destructured as follows:
   * 
   * `ResponseEntity<Void> deletedBooking = deleteBooking(amenityId, bookingId)`
   * 
   * Where `deletedBooking` is an instance of `ResponseEntity` with a status code of
   * either `NO_CONTENT` or `NOT_FOUND`, depending on whether the booking was successfully
   * deleted or not.
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
