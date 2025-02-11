package com.ccd.service;

import java.util.List;

import com.ccd.exception.InvalidEntityException;
import com.ccd.exception.NoDataFoundException;
import com.ccd.model.Rent_Booking;

public interface RentBookingService {

	public Rent_Booking addBooking(Rent_Booking booking) throws InvalidEntityException;

	public List<Rent_Booking> getAllBookings() throws NoDataFoundException;

	public Rent_Booking getBookingById(long id) throws InvalidEntityException;

	public Rent_Booking updateBooking(long id, Rent_Booking updatedBooking) throws InvalidEntityException;

	public void deleteBooking(long id) throws InvalidEntityException;

}
