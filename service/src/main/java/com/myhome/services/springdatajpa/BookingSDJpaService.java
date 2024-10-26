package com.myhome.services.springdatajpa;

import com.myhome.domain.AmenityBookingItem;
import com.myhome.repositories.AmenityBookingItemRepository;
import com.myhome.services.BookingService;
import java.util.Optional;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Provides a method to delete a booking from the database based on the booking ID
 * and associated amenity ID.
 * The deletion operation is transactional and uses a Spring Data JPA repository for
 * database interactions.
 * It implements the BookingService interface.
 */
@Service
@RequiredArgsConstructor
public class BookingSDJpaService implements BookingService {

  private final AmenityBookingItemRepository bookingRepository;

  /**
   * Deletes a booking by ID if it exists and is associated with a specified amenity
   * ID, otherwise returns false. It uses JPA transactions for database operations. The
   * function returns a boolean indicating success or failure of the deletion operation.
   *
   * @param amenityId identifier of the amenity associated with the booking being deleted.
   *
   * @param bookingId identifier used to locate the booking item to be deleted.
   *
   * @returns a boolean indicating whether the booking was successfully deleted or not.
   */
  @Transactional
  @Override
  public boolean deleteBooking(String amenityId, String bookingId) {
    Optional<AmenityBookingItem> booking =
        bookingRepository.findByAmenityBookingItemId(bookingId);
    return booking.map(bookingItem -> {
      boolean amenityFound =
          bookingItem.getAmenity().getAmenityId().equals(amenityId);
      if (amenityFound) {
        bookingRepository.delete(bookingItem);
        return true;
      } else {
        return false;
      }
    }).orElse(false);
  }
}
