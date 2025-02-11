package com.ccd.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.ccd.exception.InvalidEntityException;
import com.ccd.exception.NoDataFoundException;
import com.ccd.model.Car;
import com.ccd.model.Customer;
import com.ccd.model.Employee;
import com.ccd.model.Rent_Booking;
import com.ccd.service.CarService;
import com.ccd.service.CustomerService;
import com.ccd.service.EmployeeService;
import com.ccd.service.RentBookingService;

@RestController
@RequestMapping("/rent-booking")
public class RentBookingController {

	@Autowired
	private RentBookingService rentBookingService;

	@Autowired
	private CarService carService;

	@Autowired
	private EmployeeService employeeService;

	@Autowired
	private CustomerService customerService;

	@PostMapping("/bookCar")
	public ResponseEntity<Rent_Booking> createBooking(@Validated @RequestBody Rent_Booking booking)
			throws InvalidEntityException {
		return new ResponseEntity<>(rentBookingService.addBooking(booking), HttpStatus.OK);
	}

	@GetMapping("/viewBookingById/{id}")
	public ResponseEntity<Rent_Booking> getBookingById(@PathVariable int id) throws InvalidEntityException {
		return new ResponseEntity<>(rentBookingService.getBookingById(id), HttpStatus.OK);
	}

	@GetMapping("/viewAllBookings")
	public ResponseEntity<?> getAllBookings() throws NoDataFoundException {
		return new ResponseEntity<>(rentBookingService.getAllBookings(), HttpStatus.OK);
	}

	@PutMapping("/updateBooking/{id}")
	public ResponseEntity<Rent_Booking> updateBooking(@PathVariable int id,
			@Validated @RequestBody Rent_Booking updatedBooking) throws InvalidEntityException {
		return new ResponseEntity<>(rentBookingService.updateBooking(id, updatedBooking), HttpStatus.OK);
	}

	@DeleteMapping("/cancelBooking/{id}")
	public ResponseEntity<String> deleteBooking(@PathVariable int id) throws InvalidEntityException {
		rentBookingService.deleteBooking(id);
		return ResponseEntity.ok("Booking deleted successfully!");
	}

	@GetMapping("/cars/{id}")
	public ResponseEntity<Car> getCarById(@PathVariable int id) throws InvalidEntityException {
		return new ResponseEntity<>(carService.getCarById(id), HttpStatus.OK);
	}

	@GetMapping("/viewAllCars")
	public ResponseEntity<?> getAllCars() throws NoDataFoundException {
		return new ResponseEntity<>(carService.getAllBookings(), HttpStatus.OK);
	}

	@GetMapping("/employees/{id}")
	public ResponseEntity<Employee> getEmployeeById(@PathVariable int id) throws InvalidEntityException {
		return new ResponseEntity<>(employeeService.getEmployeeById(id), HttpStatus.OK);
	}

	@GetMapping("/customers/{id}")
	public ResponseEntity<Customer> getCustomerById(@PathVariable int id) throws InvalidEntityException {
		return new ResponseEntity<>(customerService.findByCustomerId(id), HttpStatus.OK);
	}
}
