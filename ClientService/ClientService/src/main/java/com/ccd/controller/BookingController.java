package com.ccd.controller;

import com.ccd.model.Car;
import com.ccd.model.Customer;
import com.ccd.model.Employee;
import com.ccd.model.Rent_Booking;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpSession;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.time.temporal.ChronoUnit;
import java.util.*;

@Controller
public class BookingController {

	private static final String BASE_URL = "http://localhost:9090/rent-booking";

	// All about booking - Team 4
	@GetMapping("/rent")
	public String getRent(Model model) {
		RestTemplate restTemplate = new RestTemplate();
		try {
			ResponseEntity<List<Car>> response = restTemplate.exchange(BASE_URL + "/viewAllCars", HttpMethod.GET, null,
					new ParameterizedTypeReference<List<Car>>() {
					});
			List<Car> cars = response.getBody();
			model.addAttribute("allCars", cars);

		} catch (HttpClientErrorException e) {
			Map<String, String> errors = null;
			try {
				errors = new ObjectMapper().readValue(e.getResponseBodyAsString(),
						new TypeReference<Map<String, String>>() {
						});
				model.addAttribute("errorMessage", errors.get("message"));
			} catch (JsonProcessingException jsonProcessingException) {
				jsonProcessingException.printStackTrace();
			}
			for (Map.Entry<String, String> entry : errors.entrySet()) {
				model.addAttribute(entry.getKey(), entry.getValue());
			}
		}
		Rent_Booking booking = new Rent_Booking();
		model.addAttribute("booking", booking);
		return "rent";
	}

	@GetMapping("/bookingForm/{carId}")
	public String bookingForm(@PathVariable int carId, Model model) {
		model.addAttribute("carId", carId);
		Rent_Booking booking = new Rent_Booking();
		model.addAttribute("booking", booking);

		RestTemplate restTemplate = new RestTemplate();
		try {
			ResponseEntity<Car> response = restTemplate.getForEntity(BASE_URL + "/cars/" + carId, Car.class);
			Car car = response.getBody();
			System.out.println("car details:" + car);
			System.out.println();
			model.addAttribute("car", car);
		} catch (HttpClientErrorException e) {
			Map<String, String> errors = null;
			try {
				errors = new ObjectMapper().readValue(e.getResponseBodyAsString(),
						new TypeReference<Map<String, String>>() {
						});
				model.addAttribute("errorMessage", errors.get("message"));
			} catch (JsonProcessingException jsonProcessingException) {
				jsonProcessingException.printStackTrace();
			}
			for (Map.Entry<String, String> entry : errors.entrySet()) {
				model.addAttribute(entry.getKey(), entry.getValue());
			}
		}
		return "bookForm";
	}

	@PostMapping("/bookCar/{carId}")
	public String bookCar(@PathVariable int carId, @ModelAttribute("booking") Rent_Booking booking,
			BindingResult result, Model model, HttpSession session) {
		RestTemplate restTemplate = new RestTemplate();
		try {
			if (booking != null) {
				if ((Customer) session.getAttribute("customer") != null) {
					Customer c = (Customer) session.getAttribute("customer");
					Customer customer = restTemplate.getForObject(BASE_URL + "/customers/" + c.getId(), Customer.class);
					Car car = restTemplate.getForObject(BASE_URL + "/cars/" + carId, Car.class);
					System.out.println(
							"For Sake Of Understanding: Customer Details Will Come From Session At This Moment Taking Static Data");

					ResponseEntity<List<Employee>> employeeResponse = restTemplate.exchange(
							"http://localhost:9090/getAllEmployees",
							HttpMethod.GET,
							null,
							new ParameterizedTypeReference<List<Employee>>() {
							});
					List<Employee> employees = employeeResponse.getBody();
					Employee employee = null;
					if (employees != null && !employees.isEmpty()) {
						for (Employee emp : employees) {
							if ("available".equalsIgnoreCase(emp.getAvailabilityStatus())) {
								employee = emp;
								break;
							}
						}
						if (employee == null) {
							ResponseEntity<Car> response = restTemplate.getForEntity(BASE_URL + "/cars/" + carId,
									Car.class);
							Car car1 = response.getBody();
							System.out.println("car details:" + car1);
							System.out.println();
							model.addAttribute("car", car1);
							model.addAttribute("error", "Employees are not available!");
							return "bookForm";
						}
					}

					Rent_Booking book = new Rent_Booking();
					book.setCar(car);
					book.setCustomer(customer);

					if (employee != null) {
						String availability = "not-available";
						ResponseEntity<?> availabilityResponse = restTemplate.exchange(
								"http://localhost:9090/updateEmployeeAvailability/" + employee.getEmployeeId() + "/"
										+ availability,
								HttpMethod.POST,
								null,
								Void.class);
						System.out.println(availabilityResponse);
						book.setEmployee(employee);
					}
					book.setStartDate(booking.getStartDate());
					book.setEndDate(booking.getEndDate());
					book.setLocation(booking.getLocation());
					book.setStatus(booking.getStatus());
					book.setDiscount(booking.getDiscount());
					if (booking.getStartDate() != null && booking.getEndDate() != null) {
						int days = (int) ChronoUnit.DAYS.between(booking.getStartDate(), booking.getEndDate());

						double perDayCharge = book.getCar().getRentRate();
						int disc = (booking.getDiscount());
						int totalFare = (int) (days * perDayCharge);

						double discountAmount = totalFare * (disc / 100.0);
						double finalFare = totalFare - discountAmount;
						book.setDays(days);
						book.setTotalFare(finalFare);
					}

					ResponseEntity<Rent_Booking> response = restTemplate.postForEntity(BASE_URL + "/bookCar", book,
							Rent_Booking.class);

					if (response.getBody() != null) {
						model.addAttribute("successMessage", "Booking Process Completed!");
						return "redirect:/viewAllBookings";
					}
					
				} else {
					ResponseEntity<Car> response = restTemplate.getForEntity(BASE_URL + "/cars/" + carId,
							Car.class);
					Car car1 = response.getBody();
					System.out.println("car details:" + car1);
					System.out.println();
					model.addAttribute("car", car1);
					model.addAttribute("error", "Please login!");
					return "bookForm";
				}

			}
		} catch (HttpClientErrorException e) {
			Map<String, String> errors = null;
			try {
				errors = new ObjectMapper().readValue(e.getResponseBodyAsString(),
						new TypeReference<Map<String, String>>() {
						});
			} catch (JsonMappingException e1) {
				e1.printStackTrace();
			} catch (JsonProcessingException e1) {
				e1.printStackTrace();
			}

			for (Map.Entry<String, String> entryset : errors.entrySet()) {
				String field = entryset.getKey();
				String errorMsg = entryset.getValue();
				result.rejectValue(field, "", errorMsg);
			}

			try {
				Car car = restTemplate.getForObject(BASE_URL + "/cars/" + carId, Car.class);
				model.addAttribute("car", car);
				model.addAttribute("carId", carId);
			} catch (Exception ex) {
				model.addAttribute("errorMessage", "Failed to fetch car details");
			}
		}
		return "bookForm";
	}

	@GetMapping("/viewBookingById")
	public String viewBookingById(@RequestParam("bookingId") String bookingId, Model model) {
		RestTemplate restTemplate = new RestTemplate();
		try {
			ResponseEntity<Rent_Booking> response = restTemplate
					.getForEntity(BASE_URL + "/viewBookingById/" + bookingId, Rent_Booking.class);
			Rent_Booking booking = response.getBody();

			model.addAttribute("booking", booking);

		} catch (HttpClientErrorException e) {
			Map<String, String> errors = null;
			try {
				errors = new ObjectMapper().readValue(e.getResponseBodyAsString(),
						new TypeReference<Map<String, String>>() {
						});
				model.addAttribute("errorMessage", errors.get("message"));
			} catch (JsonProcessingException jsonProcessingException) {
				jsonProcessingException.printStackTrace();
			}
			for (Map.Entry<String, String> entry : errors.entrySet()) {
				model.addAttribute(entry.getKey(), entry.getValue());
			}
		}
		return "rent";
	}

	@GetMapping("/viewAllBookings")
	public String showHistory(Model model) {

		RestTemplate restTemplate = new RestTemplate();
		try {
			ResponseEntity<List<Rent_Booking>> response = restTemplate.exchange(BASE_URL + "/viewAllBookings",
					HttpMethod.GET, null, new ParameterizedTypeReference<List<Rent_Booking>>() {
					});
			List<Rent_Booking> bookings = response.getBody();
			model.addAttribute("allBookings", bookings);
		} catch (HttpClientErrorException e) {
			Map<String, String> errors = null;
			try {
				errors = new ObjectMapper().readValue(e.getResponseBodyAsString(),
						new TypeReference<Map<String, String>>() {
						});
				model.addAttribute("errorMessage", errors.get("message"));
			} catch (JsonProcessingException jsonProcessingException) {
				jsonProcessingException.printStackTrace();
			}
			for (Map.Entry<String, String> entry : errors.entrySet()) {
				model.addAttribute(entry.getKey(), entry.getValue());
			}
		}
		return "allBooking";
	}

	@GetMapping("/cancelBooking/{bookingId}")
	public String cancelBooking(@PathVariable("bookingId") int id, Model model) {
		RestTemplate restTemplate = new RestTemplate();
		try {
			ResponseEntity<Rent_Booking> response1 = restTemplate
					.getForEntity(BASE_URL + "/viewBookingById/" + id, Rent_Booking.class);
			Rent_Booking book = response1.getBody();

			if (book == null) {
				model.addAttribute("errorMessage", "Booking not found");
				return "redirect:/viewAllBookings";
			}

			ResponseEntity<String> response = restTemplate.exchange(BASE_URL + "/cancelBooking/" + id,
					HttpMethod.DELETE, null, String.class);

			if (book.getEmployee() != null) {
				String availability = "available";
				ResponseEntity<?> availabilityResponse = restTemplate.exchange(
						"http://localhost:9090/updateEmployeeAvailability/" + book.getEmployee().getEmployeeId() + "/"
								+ availability,
						HttpMethod.POST,
						null,
						Void.class);
				System.out.println(availabilityResponse);
			}

			model.addAttribute("message", response.getBody());
		} catch (HttpClientErrorException e) {
			Map<String, String> errors = null;
			try {
				errors = new ObjectMapper().readValue(e.getResponseBodyAsString(),
						new TypeReference<Map<String, String>>() {
						});
				model.addAttribute("errorMessage", errors.get("message"));
			} catch (JsonProcessingException jsonProcessingException) {
				jsonProcessingException.printStackTrace();
			}
			for (Map.Entry<String, String> entry : errors.entrySet()) {
				model.addAttribute(entry.getKey(), entry.getValue());
			}
		} catch (Exception e) {
			model.addAttribute("errorMessage", "An unexpected error occurred: " + e.getMessage());
			e.printStackTrace();
		}
		return "redirect:/viewAllBookings";
	}

	@GetMapping("/updateBookingForm/{bookingId}")
	public String updateBookingForm(@PathVariable int bookingId, Model model) {
		RestTemplate restTemplate = new RestTemplate();
		try {
			ResponseEntity<Rent_Booking> response = restTemplate
					.getForEntity(BASE_URL + "/viewBookingById/" + bookingId, Rent_Booking.class);

			Rent_Booking booking = response.getBody();
			if (booking == null) {
				model.addAttribute("errorMessage", "Booking not found");
				return "redirect:/viewAllBookings";
			}

			model.addAttribute("booking", booking);
			model.addAttribute("bookingId", bookingId);

			if (booking.getCar() != null) {
				model.addAttribute("car", booking.getCar());
			} else {
				model.addAttribute("errorMessage", "Car details not found");
			}
			if (booking.getEmployee() != null) {
				model.addAttribute("emp", booking.getEmployee());
			} else {
				model.addAttribute("errorMessage", "Employee Datails Not Found");
			}

			return "updateBookingForm";

		} catch (HttpClientErrorException e) {
			Map<String, String> errors = null;
			try {
				errors = new ObjectMapper().readValue(e.getResponseBodyAsString(),
						new TypeReference<Map<String, String>>() {
						});
				model.addAttribute("errorMessage", errors.get("message"));
			} catch (JsonProcessingException jsonProcessingException) {
				model.addAttribute("errorMessage", "An unexpected error occurred");
			}
			if (errors != null) {
				for (Map.Entry<String, String> entry : errors.entrySet()) {
					model.addAttribute(entry.getKey(), entry.getValue());
				}
			}
		}
		return "updateBookingForm";
	}

	@PostMapping("/updateBooking/{bookingId}")
	public String handleUpdateBooking(@PathVariable int bookingId, @ModelAttribute("booking") Rent_Booking booking,
			BindingResult result, Model model) {

		RestTemplate restTemplate = new RestTemplate();
		try {
			ResponseEntity<Rent_Booking> response1 = restTemplate
					.getForEntity(BASE_URL + "/viewBookingById/" + bookingId, Rent_Booking.class);
			Rent_Booking book = response1.getBody();

			if (book != null) {
				book.setStartDate(booking.getStartDate());
				book.setEndDate(booking.getEndDate());
				book.setLocation(booking.getLocation());
				book.setStatus(booking.getStatus());

				int days = (int) ChronoUnit.DAYS.between(booking.getStartDate(), booking.getEndDate());
				double perDayCharge = book.getCar().getRentRate();
				int disc = (booking.getDiscount());
				int totalFare = (int) (days * perDayCharge);

				double discountAmount = totalFare * (disc / 100.0);
				double finalFare = totalFare - discountAmount;
				book.setDiscount(booking.getDiscount());
				book.setDays(days);
				book.setTotalFare(finalFare);
			}

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			HttpEntity<Rent_Booking> requestEntity = new HttpEntity<>(book, headers);

			ResponseEntity<String> response = restTemplate.exchange(BASE_URL + "/updateBooking/" + bookingId,
					HttpMethod.PUT, requestEntity, String.class);

			if (response.getStatusCode() == HttpStatus.OK) {
				model.addAttribute("successMessage", "Booking updated successfully");
				return "redirect:/viewAllBookings";
			}

		} catch (HttpClientErrorException e) {

			Map<String, String> errors = null;
			try {
				errors = new ObjectMapper().readValue(e.getResponseBodyAsString(),
						new TypeReference<Map<String, String>>() {
						});
			} catch (JsonMappingException e1) {
				e1.printStackTrace();
			} catch (JsonProcessingException e1) {
				e1.printStackTrace();
			}

			for (Map.Entry<String, String> entryset : errors.entrySet()) {
				String field = entryset.getKey();
				String errorMsg = entryset.getValue();
				result.rejectValue(field, "", errorMsg);
			}
		}
		return "updateBookingForm";
	}

}
