package com.ccd.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ccd.model.Rent_Booking;

@Repository
public interface RentBookingRepository extends JpaRepository<Rent_Booking, Long> {
	List<Rent_Booking> findByEndDateAfter(LocalDate today);
}
