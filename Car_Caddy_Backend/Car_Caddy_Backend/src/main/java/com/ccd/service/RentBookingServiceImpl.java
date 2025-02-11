package com.ccd.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.ccd.exception.InvalidEntityException;
import com.ccd.exception.NoDataFoundException;
import com.ccd.model.Customer;
import com.ccd.model.Employee;
import com.ccd.model.Rent_Booking;
import com.ccd.repository.CustomerRepository;
import com.ccd.repository.EmployeeRepository;
import com.ccd.repository.RentBookingRepository;

@Service
public class RentBookingServiceImpl implements RentBookingService {
	@Autowired
	private RentBookingRepository rentBookingRepository;

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private EmployeeRepository employeeRepository;

	@Autowired
	private EmailService emailService;

	public Rent_Booking addBooking(Rent_Booking booking) throws InvalidEntityException {
		if (booking == null)
			throw new InvalidEntityException("Empty Data!");
		Customer customer = customerRepository.findById(booking.getCustomer().getId()).orElseThrow(
				() -> new InvalidEntityException("Customer not found with ID: " + booking.getCustomer().getId()));
		Employee employee = employeeRepository.findById(booking.getEmployee().getEmployeeId())
				.orElseThrow(() -> new InvalidEntityException(
						"Employee not found with ID: " + booking.getEmployee().getEmployeeId()));
		System.out.println(customer.getEmail() + " " + employee.getEmployeeEmail());
		emailService.sendEmail(customer.getEmail(), "🚗 Booking Confirmation: Your Ride Details",
				"Booking Summary: \n" + "Start Date: " + booking.getStartDate() + "\nEnd Date: " + booking.getEndDate()
						+ "\nTotal Number Of Days: " + booking.getDays() + "\nDestination: " + booking.getLocation());
		emailService.sendEmail(employee.getEmployeeEmail(), "New Ride Assignment",
				"Details: \n" + "\nDestination: " + booking.getLocation() + "\nStart Date: " + booking.getStartDate()
						+ "\nEnd Date: " + booking.getEndDate());
		return rentBookingRepository.save(booking);
	}

	public List<Rent_Booking> getAllBookings() throws NoDataFoundException {
		List<Rent_Booking> bookings = rentBookingRepository.findAll();
		if (bookings.isEmpty()) {
			throw new NoDataFoundException("No bookings found.");
		}
		return bookings;
	}

	public Rent_Booking getBookingById(long id) throws InvalidEntityException {
		return rentBookingRepository.findById(id)
				.orElseThrow(() -> new InvalidEntityException("Booking with ID " + id + " not found."));
	}

	public Rent_Booking updateBooking(long id, Rent_Booking updatedBooking) throws InvalidEntityException {
		Rent_Booking existingBooking = rentBookingRepository.findById(id)
				.orElseThrow(() -> new InvalidEntityException("Booking not found with ID: " + id));

		Customer customer = customerRepository.findById(updatedBooking.getCustomer().getId())
				.orElseThrow(() -> new InvalidEntityException(
						"Customer not found with ID: " + updatedBooking.getCustomer().getId()));
		Employee employee = employeeRepository.findById(updatedBooking.getEmployee().getEmployeeId())
				.orElseThrow(() -> new InvalidEntityException(
						"Employee not found with ID: " + updatedBooking.getEmployee().getEmployeeId()));
		System.out.println(customer.getEmail() + " " + employee.getEmployeeEmail());

		existingBooking.setCar(updatedBooking.getCar());
		existingBooking.setCustomer(updatedBooking.getCustomer());
		existingBooking.setStartDate(updatedBooking.getStartDate());
		existingBooking.setEndDate(updatedBooking.getEndDate());
		existingBooking.setTotalFare(updatedBooking.getTotalFare());
		existingBooking.setStatus(updatedBooking.getStatus());
		existingBooking.setLocation(updatedBooking.getLocation());
		existingBooking.setEmployee(updatedBooking.getEmployee());
		existingBooking.setDiscount(updatedBooking.getDiscount());
		existingBooking.setDays(updatedBooking.getDays());

		emailService.sendEmail(customer.getEmail(), "🚗 Booking Updated: Your Ride Details",
				"Booking Summary: \n" + "Start Date: " + updatedBooking.getStartDate() + "\nEnd Date: "
						+ updatedBooking.getEndDate() + "\nTotal Number Of Days: " + updatedBooking.getDays()
						+ "\nDestination: " + updatedBooking.getLocation());
		emailService.sendEmail(employee.getEmployeeEmail(), "Rent " + updatedBooking.getBookingId() + " Updated",
				"Hi " + employee.getEmployeeName() + ", \nRent Details: \n" + "\nDestination: "
						+ updatedBooking.getLocation() + "\nStart Date: " + updatedBooking.getStartDate()
						+ "\nEnd Date: " + updatedBooking.getEndDate());
		return rentBookingRepository.save(existingBooking);
	}

	public void deleteBooking(long id) throws InvalidEntityException {
		if (!rentBookingRepository.existsById(id)) {
			throw new InvalidEntityException("Booking not found with ID: " + id);
		}
		rentBookingRepository.deleteById(id);
	}

	private boolean isProcessing = false;

	@Scheduled(cron = "0 0 10 * * ?")
	public String checkAndNotifyUpcomingEndDates() throws InvalidEntityException {
		if (isProcessing) {
			return "Processing already in progress";
		}

		try {
			isProcessing = true;
			LocalDate today = LocalDate.now();
			List<Rent_Booking> activeBookings = rentBookingRepository.findByEndDateAfter(today);
			boolean emailsSent = false;

			for (Rent_Booking booking : activeBookings) {
				long daysUntilEnd = ChronoUnit.DAYS.between(today, booking.getEndDate());

				if (daysUntilEnd <= 1 && daysUntilEnd > 0) {
					sendEndDateNotifications(booking, daysUntilEnd);
					emailsSent = true;
				}
			}
			return emailsSent ? "Mail Sent" : "No notifications needed";
		} finally {
			isProcessing = false;
		}
	}

	private void sendEndDateNotifications(Rent_Booking booking, long daysRemaining) throws InvalidEntityException {
		Customer customer = customerRepository.findById(booking.getCustomer().getId()).orElseThrow(
				() -> new InvalidEntityException("Customer not found with ID: " + booking.getCustomer().getId()));
		Employee employee = employeeRepository.findById(booking.getEmployee().getEmployeeId())
				.orElseThrow(() -> new InvalidEntityException(
						"Employee not found with ID: " + booking.getEmployee().getEmployeeId()));

		String customerSubject = "🚗 Reminder: Your Car Rental is Ending Soon";
		String customerBody = String.format("""
				Dear Customer,

				Your car rental booking is ending in %d day(s).

				Booking Details:
				End Date: %s
				Location: %s

				Please ensure timely return of the vehicle.

				Thank you for choosing our service!
				""", daysRemaining, booking.getEndDate(), booking.getLocation());

		emailService.sendEmail(customer.getEmail(), customerSubject, customerBody);

		String employeeSubject = "Upcoming Rental End - Action Required";
		String employeeBody = String.format("""
				Dear Employee,
				The following rental booking is ending in %d day(s):

				End Date: %s
				Location: %s

				Please prepare for the vehicle return process.
				""", daysRemaining, booking.getEndDate(), booking.getLocation());

		emailService.sendEmail(employee.getEmployeeEmail(), employeeSubject, employeeBody);
	}

}
