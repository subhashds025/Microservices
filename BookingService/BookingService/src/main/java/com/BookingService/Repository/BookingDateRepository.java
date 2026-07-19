package com.BookingService.Repository;

import com.BookingService.Entity.BookingDate;
import org.springframework.data.jpa.repository.JpaRepository;



public interface BookingDateRepository extends JpaRepository<BookingDate, Long> {

}
