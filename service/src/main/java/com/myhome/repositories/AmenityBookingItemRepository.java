package com.myhome.repositories;

import com.myhome.domain.AmenityBookingItem;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Extends Spring Data JPA's JpaRepository to provide data access operations for
 * AmenityBookingItem entities.
 */
public interface AmenityBookingItemRepository extends JpaRepository<AmenityBookingItem, String> {
  Optional<AmenityBookingItem> findByAmenityBookingItemId(String amenityBookingItemId);
}
