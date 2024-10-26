package com.myhome.services;

/**
 * Defines a contract for managing bookings.
 */
public interface BookingService {

  boolean deleteBooking(String amenityId, String bookingId);

}
