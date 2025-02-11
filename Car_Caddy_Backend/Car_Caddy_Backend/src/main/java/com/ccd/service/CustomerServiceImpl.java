package com.ccd.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ccd.model.*;
import com.ccd.service.EmailService;
import com.ccd.exception.InvalidEntityException;
import com.ccd.model.Customer;
import com.ccd.repository.CustomerRepository;

@Service
public class CustomerServiceImpl implements CustomerService {

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private EmailService emailService;

	public Customer findByCustomerId(long customerId) throws InvalidEntityException {
		return customerRepository.findById(customerId)
				.orElseThrow(() -> new InvalidEntityException("Customer with ID " + customerId + " not found."));
	}

	public Customer registerCustomer(Customer customer) {
		Customer customer_register = customerRepository.save(customer);
		String emailBody = "<html>" + "<body>" + "<p>Hello <b>" + customer_register.getFirstName() + " "
				+ customer_register.getLastName() + "</b>,</p>"
				+ "<p>You have successfully registered with CarCaddy - Car Rental Automation System.</p>"
				+ "<p>Thank you!</p>" + "</body>" + "</html>";
		// Send email
		try {
			emailService.sendEmail(customer_register.getEmail(), "Welcome to CarCaddy", emailBody);
		} catch (Exception e) {
			System.out.println("Error Ocurred!");
		}
		// emailService.sendEmail(customer.getEmail(), "Welcome to CarCaddy", "You have
		// successfully registered with CarCaddy.\nThank You.");
		return customer_register;
	}

	public List<Customer> getAllCustomers() throws InvalidEntityException {
		List<Customer> customers = customerRepository.findAll();
		if (customers.size() > 0)
			return customers;
		else
			throw new InvalidEntityException("No customers available");
	}

	public Optional<Customer> getCustomerById(long id) throws InvalidEntityException {
		Optional<Customer> customer = customerRepository.findById(id);
		if (customer.isPresent())
			return customer;
		else
			throw new InvalidEntityException("Customer not found with id :" + id);
	}

	public Customer updateCustomer(long id, Map<String, Object> updatedCustomerData) throws InvalidEntityException {
		Customer customer = customerRepository.findById(id)
				.orElseThrow(() -> new InvalidEntityException("Customer with ID " + id + " not found."));
		if (updatedCustomerData.containsKey("firstName")) {
			customer.setFirstName((String) updatedCustomerData.get("firstName"));
		}
		if (updatedCustomerData.containsKey("lastName")) {
			customer.setLastName((String) updatedCustomerData.get("lastName"));
		}
		if (updatedCustomerData.containsKey("email")) {
			customer.setEmail((String) updatedCustomerData.get("email"));
		}
		if (updatedCustomerData.containsKey("mobile")) {
			customer.setMobile((String) updatedCustomerData.get("mobile"));
		}
		if (updatedCustomerData.containsKey("gender")) {
			customer.setGender((String) updatedCustomerData.get("gender"));
		}
		if (updatedCustomerData.containsKey("loyaltyPoints")) {
			customer.setLoyaltyPoints((Integer) updatedCustomerData.get("loyaltyPoints"));
		}
		if (updatedCustomerData.containsKey("blocklistStatus")) {
			customer.setBlocklistStatus((Boolean) updatedCustomerData.get("blocklistStatus"));
		}
		if (updatedCustomerData.containsKey("password")) {
			customer.setPassword((String) updatedCustomerData.get("password"));
		}

		String emailBody = "<html>" + "<body>" + "<p>Hello <b>" + customer.getFirstName() + " " + customer.getLastName()
				+ "</b>,</p>" + "<p>Your details have been successfully updated!!!</p>" + "</body>" + "</html>";
		// Send email
		try {
			emailService.sendEmail(customer.getEmail(), "Update on your Profile", emailBody);
		} catch (Exception e) {
			System.out.println("Error Ocurred!");
		}

		return customerRepository.save(customer);
	}

	public String blockCustomer(long id) throws InvalidEntityException {
		Optional<Customer> customer = customerRepository.findById(id);
		if (customer.isPresent()) {
			// Check if the customer is already blocked
			if (customer.get().getBlocklistStatus()) {
				return "Customer is already blocked";
			}
			customer.get().setBlocklistStatus(Boolean.TRUE);
			customerRepository.save(customer.get());
			return "Customer is blocked";
		} else {
			throw new InvalidEntityException("Customer with ID: " + id + " is not found.");
		}
	}

	public Customer login(LoginRequest loginRequest) throws InvalidEntityException {
		String email = loginRequest.getEmail().trim().toLowerCase();
		String password = loginRequest.getPassword();

		// Debugging: Log the extracted email
		System.out.println("Attempting login for email: " + email);

		// Fetch user by email
		Optional<Customer> optionalCustomer = null;
		try {
			optionalCustomer = customerRepository.findByEmail(email);
			// System.out.println(optionalCustomer.get());
		} catch (Exception e) {
			throw new InvalidEntityException("mail not found");
		}
		Customer customer = null;
		if (optionalCustomer.equals(null))
			throw new InvalidEntityException("Invalid login credentials");
		if (optionalCustomer.isPresent())
			customer = optionalCustomer.get();

		// Debugging: Log the customer details
		System.out.println("Customer found: " + customer);

		// Validate password
		if (customer.getPassword().equals(password)) {
			return customer;
		}
		throw new InvalidEntityException("Invalid Login Credentials");
	}

}