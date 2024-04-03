package com.myhome.services.springdatajpa;

import com.myhome.domain.AmenityBookingItem;
import com.myhome.repositories.AmenityBookingItemRepository;
import com.myhome.services.BookingService;
import java.util.Optional;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookingSDJpaService implements BookingService {

  private final AmenityBookingItemRepository bookingRepository;

  /**
   * searches for a booking item in the repository using the `bookingId`. If it finds
   * one, it deletes the booking item and returns `true`. Otherwise, it returns `false`.
   * 
   * @param amenityId ID of an amenity for which the booking is to be deleted.
   * 
   * @param bookingId ID of the booking to be deleted.
   * 
   * @returns a boolean value indicating whether the booking item was successfully deleted.
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
























