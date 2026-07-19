package com.BookingService.Repository;

import com.BookingService.Entity.Bookings;
import org.springframework.data.jpa.repository.JpaRepository;


public interface BookingRepository extends JpaRepository<Bookings, Long> {

}
